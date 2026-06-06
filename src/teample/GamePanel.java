package teample;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel implements Runnable {
    public final int tileSize = 48;
    public final int maxScreenCol = 21; 
    public final int maxScreenRow = 16;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public BookUI diaryBook = new BookUI();
    public double dollOffset = 0; // 흔들림 정도를 조절할 변수
    private double angle = 0;
    
    public java.util.Set<String> inventory = new java.util.HashSet<>();
    public void acquireKey(String keyName) {
        inventory.add(keyName);
        showSingleDialog(keyName + "을(를) 획득했습니다!");
    }

    // 열쇠 소지 여부 확인
    public boolean hasKey(String keyName) {
        return inventory.contains(keyName);
    }
    
    public TypingBoss TypingBoss = new TypingBoss(this);
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public PlayerMove player = new PlayerMove(this, keyH);
    public TileManager tileM = new TileManager(this);
    public TypingScene typingScene = new TypingScene(this);
    public MapManager mapM;
    public DialogUI dialogUI = new DialogUI();
    public boolean isBossBattle = false;
    public StageData currentRoom; 
    public int gameState;
    public boolean playerHasLivingroomKey = false;
    
    // === 타이틀 화면 관련 ===
    public final int titleState = 12;
    public BufferedImage titleImage;
    private long titleStartTime;
    private float titleAlpha = 1.0f;
    
    private final int TITLE_HOLD_MS  = 6000;   // 6초간 선명하게 표시
    private final int TITLE_FADE_MS  = 4000;   // 4초에 걸쳐 페이드아웃
    private final int TITLE_TOTAL_MS = TITLE_HOLD_MS + TITLE_FADE_MS;  // 총 10초
    
    // === 사운드 매니저 객체 생성 ===
    public SoundManager soundM = new SoundManager();
    
    // --- 게임 상태 상수 목록 ---
    public final int introState = 0;
    public final int playState = 1;
    public final int typingState = 2;
    public final int getUpState = 3;
    public final int diaryEventState = 4; 
    public final int windowEventState = 5; 
    public final int gameOverState = 6;
    public final int endingState = 7;
    public final int dialogState = 8;
    public final int endingWalkState = 9;
    public final int dialogueState = 10;
    public int bossEventState = 11;
    public int dialogIndex = 0;
    public String[] currentDialogs;
    private boolean isIntroDialog = false;
    private boolean isWindowEvent = false;
    private boolean isBossEvent = false;
    public boolean monsterAlive = false;
    public boolean playerHasBedroomKey = false; // [수정] 침실 열쇠 보유 여부 플래그
    Thread gameThread;
    public int bossX = 0;
    public int bossY = 0;
    
    // --- 스토리 플래그 변수 ---
    private boolean isEndingDialog = false;
    private int fadeAlpha = 255; 
    private boolean dialogTriggered = false;
    private final int targetWakeUpX = 4 * tileSize; 
    private boolean diaryReadCompleted = false; 
    private boolean windowEventTriggered = false; 
    private int endingAlpha = 0;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        mapM = new MapManager(maxScreenCol, maxScreenRow);
        
        // 배경음악 재생
        soundM.playBgm("titlemusic.wav");
        
        // === 타이틀 시작 ===
        loadImages();
        gameState = titleState;
        titleStartTime = System.currentTimeMillis();
        
        changeRoom("침실");
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 1. 일기장 처리
                if (diaryBook.isOpen) {
                    if (diaryBook.currentPage >= diaryBook.pages.size() - 1) {
                        diaryBook.closeBook();
                    } else {
                        diaryBook.currentPage++;
                    }
                    repaint();
                    return;
                }

                // 2. 대사/보스 이벤트 처리 (dialogState와 bossEventState 통합)
                else if (gameState == dialogState || gameState == bossEventState) {
                    dialogIndex++;
                    
                    if (isBossEvent && dialogIndex == 1) {
                        currentRoom.removeTile(10, 8);
                        currentRoom.monsterPos.x = bossX;
                        currentRoom.monsterPos.y = bossY;
                        monsterAlive = true;
                    }
                    // [윈도우 이벤트 소환 연출]
                    else if (isWindowEvent && dialogIndex == 1) {
                        player.y += tileSize;
                        currentRoom.setStructure(9, 0, 7);
                        currentRoom.setStructure(10, 0, 7);
                        currentRoom.setStructure(11, 0, 7);
                        currentRoom.setStructure(8, 1, 8);  
                        currentRoom.setStructure(9, 1, 0);  
                        currentRoom.setStructure(10, 1, 0); 
                        currentRoom.setStructure(11, 1, 0); 
                        currentRoom.setStructure(12, 1, 8); 
                        currentRoom.setMonster(10 * tileSize, 0 * tileSize);
                        monsterAlive = true;
                    }

                    // 대사 출력 및 종료 로직
                    if (dialogIndex < currentDialogs.length) {
                        dialogUI.currentText = currentDialogs[dialogIndex];
                    } else {
                        dialogUI.isVisible = false;

                        if (isEndingDialog) {
                            isEndingDialog = false;
                            startWalkingAnimation();
                        } else if (gameState == bossEventState || isBossEvent) {
                            dialogUI.isVisible = false;
                            isBossEvent = false;         // 이벤트 플래그 해제
                            gameState = typingState;     
                            isBossBattle = true;         // 보스전 시작!
                            monsterAlive = true;         // 보스는 살아있음
                            TypingBoss.startBattle();    // 보스전 로직 시작
                        }
                        else if (isWindowEvent) {
                            isWindowEvent = false;
                            gameState = typingState;
                            isBossBattle = false;     // 일반 전투
                            typingScene.startBattle();
                        } else if (isIntroDialog) {
                            isIntroDialog = false;
                            gameState = getUpState;
                        } else {
                            gameState = playState;
                        }
                    }
                    repaint();
                }
            }
        });
    }
    
    public void loadImages() {
        try {
            titleImage = ImageIO.read(getClass().getResourceAsStream("title.png"));
        } catch (Exception e) {
            System.out.println("타이틀 이미지 로드 실패: " + e.getMessage());
            titleImage = null;
        }
    }
    
    private void updateTitle() {
        long elapsed = System.currentTimeMillis() - titleStartTime;
        
        if (elapsed < TITLE_HOLD_MS) {
            titleAlpha = 1.0f;
        } else if (elapsed < TITLE_TOTAL_MS) {
            float fadeProgress = (elapsed - TITLE_HOLD_MS) / (float) TITLE_FADE_MS;
            titleAlpha = 1.0f - fadeProgress;
        } else {
            titleAlpha = 0f;
            gameState = introState;
            titleStartTime = 0;
        }
    }
    
    private void drawTitle(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        if (titleImage != null && titleAlpha > 0) {
            Composite original = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));
            g2.drawImage(titleImage, 0, 0, screenWidth, screenHeight, null);
            g2.setComposite(original);
        }
    }
    
    public void startWalkingAnimation() {
        gameState = endingWalkState; // [추가] 걷는 상태로 전환
        new Thread(() -> {
            int totalSteps = screenHeight / 2;
            for (int i = 0; i < totalSteps; i++) {
                player.y += 5; 
                if (i > totalSteps * 0.3) {
                    if (endingAlpha < 255) endingAlpha += 3;
                }
                repaint();
                try { Thread.sleep(15); } catch (InterruptedException ex) { ex.printStackTrace(); }
            }
            endingAlpha = 255;
            gameState = endingState; // [핵심] 이동이 다 끝났을 때 클리어 상태로 변경!
            repaint();
        }).start();
    }
    
    public void showSingleDialog(String message) {
        dialogUI.currentText = message;
        dialogUI.isVisible = true;
        gameState = dialogState;
    }

    public void startDialogSequence(String[] messages) {
        this.currentDialogs = messages;
        this.dialogIndex = 0;
        this.dialogUI.currentText = messages[dialogIndex];
        this.dialogUI.isVisible = true;
        this.gameState = dialogState;
    }
    
    public void changeRoom(String roomName) {
        StageData prevRoom = currentRoom;
        currentRoom = mapM.roomMap.get(roomName);
        tileM.mapData = currentRoom.mapLayout;
        
        // 이미 침실 열쇠를 획득하여 가지고 나간 후 재입장했을 시 괴물 복구 전면 차단
        if (currentRoom.isMonsterDefeated) {
            monsterAlive = false;
        } else {
            monsterAlive = currentRoom.hasMonster;
        }
        
        keyH.resetKeys();

        if (prevRoom != null) {
            if (player.x > 800) player.x = 100;
            else if (player.x < 100) player.x = 850;
            else if (player.y < 100) player.y = 600;
            else if (player.y > 600) player.y = 100;
        } else {
            player.x = 2 * tileSize; 
            player.y = 2 * tileSize;
        }
    }
    
    public void onMonsterDefeated(String roomName) {
        monsterAlive = false;
        StageData room = mapM.roomMap.get(roomName);
        if (room != null) {
            room.isMonsterDefeated = true; // [핵심] 해당 방 데이터를 직접 수정
        }
        showSingleDialog("괴물을 물리쳤다!");
    }
    
    public void update() {
        // === 타이틀 화면 처리 (가장 먼저!) ===
        if (gameState == titleState) {
            updateTitle();
            return;
        }
        
    	if (diaryBook.justClosedAfterLastPage) {
            diaryBook.justClosedAfterLastPage = false;
            if (!windowEventTriggered && !playerHasBedroomKey && !diaryReadCompleted) {
                String[] messages = {
                    "* 똑... 똑똑... 창문 쪽에서 기분 나쁜 소리가 들려왔다. *",
                    "무슨 소리지?",
                    "창문에서 난 소리 같은데...",
                    "한번 조사해 보자."
                };
                startDialogSequence(messages);
                diaryReadCompleted = true;
            } else {
            	gameState = playState;
            }
            return;
        }
    	
        if (gameState == introState) {
            if (fadeAlpha > 0) {
                fadeAlpha -= 3; 
                if (fadeAlpha < 0) fadeAlpha = 0;
            } else if (!dialogTriggered) {
                dialogTriggered = true;
                isIntroDialog = true;
                String[] introMessages = {
                        "...으윽, 여기가 어디지...?",
                        "분명 방 안에서 컴퓨터를 켜고 코딩 과제를 하고 있었는데...",
                        "주변 분위기가 심상치 않다. 어서 방을 나가서 탈출하자."
                    };
                startDialogSequence(introMessages);
            }
            return;
        }
        
        if (gameState == playState && currentRoom.roomName.equals("탈출")) {
            gameState = endingState;
            startEndingSequence();
        }

        if (gameState == getUpState) {
            if (player.x < targetWakeUpX) {
                player.x += 2; 
            } else {
                gameState = playState;
            }
            return;
        }

        if (gameState == diaryEventState || gameState == windowEventState) {
            return;
        }

        if (gameState == playState) {
            player.update();

            // 1. 일기장 상호작용 체크
            if (currentRoom.hasInteractObject) {
                double distance = Point.distance(
                    player.x + tileSize/2, player.y + tileSize/2,
                    currentRoom.interactObjectPos.x + tileSize/2, currentRoom.interactObjectPos.y + tileSize/2
                );
                
                if (distance < 72 && keyH.fPressed) {
                    keyH.fPressed = false; // 키 입력 초기화 (중요)
                    diaryBook.isOpen = true;
                    diaryBook.currentPage = 0;
                    gameState = diaryEventState;
                    return;
                }
            }

            // 2. 사물 및 유리 파편 F키 상호작용 체크
            if (keyH.fPressed) {
                int pCol = (player.x + tileSize/2) / tileSize;
                int pRow = (player.y + tileSize/2) / tileSize;
                boolean checked = false;

                for (int r = pRow - 1; r <= pRow + 1; r++) {
                    for (int c = pCol - 1; c <= pCol + 1; c++) {
                        if (r >= 0 && r < maxScreenRow && c >= 0 && c < maxScreenCol) {
                            int tileType = currentRoom.mapLayout[r][c];
                            
                            if (tileType == 8) {
                                keyH.resetKeys();
                                showSingleDialog("깨진 창문 조각이다. 밟지 않는게 좋을 것 같다.");
                                checked = true;
                                break;
                            }
                            else if (tileType == 4 && diaryReadCompleted && !windowEventTriggered) {
                                keyH.resetKeys();
                                gameState = windowEventState;
                                windowEventTriggered = true; 
                                isWindowEvent = true;
                              
                                String[] windowDialogs = {
                                        "밖엔 어두워서 아무것도 안 보이는데...",
                                        "* 쨍그랑!!! 창문 유리가 산산조각나며 깨졌다! *",
                                        "으악! 괴물이다!!!"
                                    };
                                
                                startDialogSequence(windowDialogs);
                                checked = true;
                            }
                            else if (tileType > 1 && tileType != 7 && tileType != 8) { 
                                keyH.resetKeys();
                                
                                if (tileType == 13) {
                                    if (!currentRoom.isMonsterDefeated) {
                                        isBossEvent = true;
                                        gameState = dialogState;
                                        
                                        // 2. 보스 소환 (좌표는 실제 게임 타일 크기에 맞게 설정)
                                        bossX = c * tileSize; 
                                        bossY = r * tileSize;
                                        
                                        // 일단 보스를 생성만 함
                                        currentRoom.setMonster(bossX, bossY);
                                        // 3. 대사 시퀀스만 시작
                                        String[] BossDialogs = {
                                            "보스 : 넌 여기서 나갈 수 없다.",
                                            "이제 너의 죽음을 맞이할 시간이다!"
                                        };
                                        startDialogSequence(BossDialogs);
                                    } else {
                                        showSingleDialog("인형은 이제 평범한 인형처럼 보인다.");
                                    }
                                }
                                else {
                                    String msg = getObjectDescription(tileType);
                                    
                                    // 2. 만약 msg가 null이거나 비어있으면 종료
                                    if (msg != null && !msg.isEmpty()) {
                                        showSingleDialog(msg);
                                        checked = true;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (checked) break;
                }
            }
            
            angle += 0.2; 
            dollOffset = Math.sin(angle) * 3;

            // 3. 일반 배틀 체크
            if (monsterAlive && currentRoom.monsterPos != null) {
                if (player.getBounds().intersects(currentRoom.monsterPos)) {
                    keyH.resetKeys();
                    gameState = typingState;
                    
                    // 보스전이 아닐 때만 일반 전투를 시작함
                    if (!isBossBattle) {
                        typingScene.startBattle();
                    } 
                    // 보스전이라면 TypingBoss가 이미 처리 중이므로 건드리지 않음
                    return; 
                }
            }

            // 4. 열쇠 획득 체크
            if (currentRoom.hasKey && player.getBounds().intersects(currentRoom.keyPos)) {
                keyH.resetKeys();
                currentRoom.hasKey = false;
                
                String acquiredKeyName = currentRoom.roomName.equals("침실") ? "침실 열쇠" : "정문 열쇠";
                
                acquireKey(acquiredKeyName);
                
                if (acquiredKeyName.equals("침실 열쇠")) {
                	playerHasBedroomKey = true;
              
                    String[] bedroomkeylog = {
                    		"방금... 그 괴물은 뭐였지..?\n일단 해치운건가...?",
                    		"이건...여기서 나갈 수 있는 열쇠인가?",
                    		"* 침실 열쇠를 획득했습니다! *",
                    		"어서 여기서 나가자"
                    };
                    startDialogSequence(bedroomkeylog);  
                }
                else if(acquiredKeyName.equals("정문 열쇠")) {
                	playerHasLivingroomKey = true;
                	String[] livingroomkeylog = {
                    		"정말 강력했어...",
                    		"이건...여기서 나갈 수 있는 열쇠인가?",
                    		"* 정문 열쇠를 획득했습니다! *",
                    		"어서 여기서 나가자"
                    };
                    startDialogSequence(livingroomkeylog);  
                }
            }

            // 5. 문 이동 체크
            for (Rectangle doorRect : currentRoom.doors.keySet()) {
                if (player.getBounds().intersects(doorRect)) {
                    String nextRoomName = currentRoom.doors.get(doorRect);
                    
                    StageData nextRoom = mapM.roomMap.get(nextRoomName);
                    
                    if (nextRoom != null) {
                        // 해당 방이 요구하는 열쇠가 있는지 확인
                        if (nextRoom.requiredKey != null && !inventory.contains(nextRoom.requiredKey)) {
                            keyH.resetKeys();
                            showSingleDialog(nextRoom.requiredKey + "이(가) 필요합니다.");
                            bouncePlayer(doorRect); // 튕겨내기
                            return;
                        }
                        
                        // 모든 잠금 체크 통과 시에만 이동
                        changeRoom(nextRoomName);
                        break;
                    }
                }
            }
        }
    }
    
    public void bouncePlayer(Rectangle doorRect) {
        if (player.x < doorRect.x) player.x -= 30;
        else player.x += 30;
        
        if (player.y < doorRect.y) player.y -= 30;
        else player.y += 30;
    }
    
    public void startEndingSequence() {
    	isEndingDialog = true;
        String[] endingMessages = {
            "드디어 벗어났어...",
            "다시는 생각도 하기 싫은 곳이야..",
            "빨리 집으로 돌아가자..."
        };
        startDialogSequence(endingMessages);
    }
    
    private String getObjectDescription(int type) {
        switch (type) {
            case 2: return "푹신하고 하얀 침대이다.\n방금 여기서 정신을 차리고 일어났다.";
            case 3: return "오래된 원목 책상이다.\n한쪽에 일기장이 놓여 있다.";
            case 4: return "커다란 유리창이다.\n바깥은 어두컴컴해서 아무것도 보이지 않는다.";
            case 5: return "오래된 책들이 빽빽하게 꽂혀 있는 책장이다.\n읽을 만한 책은 보이지 않는다.";
            case 6: return "평범한 나무 의자이다.\n먼지가 조금 쌓여 있다.";
            case 9: return "거미줄이다.\n방치된 공간에는 항상 있는 법이다.";
            case 10: return "오래된 포스터다.\n뭔가 무서운 느낌이 든다.";
            case 11: return "잘 가꿔진 풀숲이다.\n누군가 여기에 살고있는걸까?";
            case 12: return "문이 고장난 옷장이다.\n문은 열리지 않는다.";
            case 14: return "오래된 시계다.\n더 이상 작동은 하지 않는 것 같다.";
            case 15: return "흙만 남은 화분이다.\n식물은 죽은 것 같다.";
            case 16: return "오래된 벽난로다.\n불을 피울 필요는 없어보인다.";
            case 17: return "오래되서 먼지만 날리는 소파다.\n비싸게 팔렸을 것 같다.";
            case 20: return "오래된 인형이다.\n잠깐...방금 움직이지 않았나?";
            default: return "평범한 물건이다.";
        }
    }

    public double getDollOffset() {
        return dollOffset;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // === 타이틀 화면이면 타이틀만 그리고 끝 ===
        if (gameState == titleState) {
            drawTitle(g2);
            g2.dispose();
            return;
        }
        
        tileM.draw(g2); 
        
        g2.setColor(Color.BLUE);
        for (Rectangle r : currentRoom.doors.keySet()) g2.fill(r);
        
        if (currentRoom.hasInteractObject) {
            int ox = currentRoom.interactObjectPos.x;
            int oy = currentRoom.interactObjectPos.y;
            g2.setColor(Color.WHITE);
            g2.fillRect(ox + 12, oy + 12, 24, 24);
            g2.setColor(new Color(30, 144, 255)); 
            g2.fillRect(ox + 12, oy + 12, 6, 24);  
            g2.drawRect(ox + 12, oy + 12, 24, 24); 
        }

        if (monsterAlive && currentRoom.monsterPos != null) { 
            g2.setColor(Color.RED); 
            g2.fill(currentRoom.monsterPos); 
        }
        if (currentRoom.hasKey) { 
        	g2.setColor(Color.YELLOW); 
        	g2.fill(currentRoom.keyPos);
        }
        
        player.draw(g2); 
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        // 상단 UI에 소지하고 있는 열쇠 명칭 출력 변경
        g2.drawString("Room: " + currentRoom.roomName + 
                (playerHasBedroomKey ? " [침실 열쇠]" : "") + 
                (inventory.contains("정문 열쇠") ? " [정문 열쇠]" : ""), 20, 30);
        
        if (gameState == playState) {
            if (currentRoom.hasInteractObject) {
                double distance = Point.distance(
                    player.x + tileSize/2, player.y + tileSize/2,
                    currentRoom.interactObjectPos.x + tileSize/2, currentRoom.interactObjectPos.y + tileSize/2
                );
                if (distance < 72) {
                    g2.setColor(Color.YELLOW);
                    g2.drawString("[F] 일기장 읽기", currentRoom.interactObjectPos.x - 20, currentRoom.interactObjectPos.y + tileSize + 20);
                }
            }

            if (diaryReadCompleted && !windowEventTriggered && currentRoom.roomName.equals("침실")) {
                int pCol = (player.x + tileSize/2) / tileSize;
                int pRow = (player.y + tileSize/2) / tileSize;
                
                boolean windowNear = false;
                for (int r = pRow - 1; r <= pRow + 1; r++) {
                    for (int c = pCol - 1; c <= pCol + 1; c++) {
                        if (r >= 0 && r < maxScreenRow && c >= 0 && c < maxScreenCol) {
                            if (currentRoom.mapLayout[r][c] == 4) {
                                windowNear = true;
                                break;
                            }
                        }
                    }
                    if (windowNear) break;
                }
                
                if (windowNear) {
                    g2.setColor(Color.YELLOW);
                    g2.setFont(new Font("맑은 고딕", Font.BOLD, 16)); 
                    g2.drawString("[F] 조사하기", 10 * tileSize - 24, 1 * tileSize - 10);
                }
            }
        }
        
        if (diaryBook.isOpen) {
            diaryBook.draw(g2, screenWidth, screenHeight);
        }

        if (gameState == typingState) {
            if (isBossBattle) {
                TypingBoss.draw(g2); // 보스전 그리기
            } else {
                typingScene.draw(g2); // 일반전 그리기
            }
        } else if (gameState == dialogState || gameState == bossEventState) {
            dialogUI.draw(g2, screenWidth, screenHeight);
        }

        if (gameState == introState || fadeAlpha > 0) {
            g2.setColor(new Color(0, 0, 0, fadeAlpha));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }
        
        if (diaryBook.isOpen) diaryBook.draw(g2, screenWidth, screenHeight);
        if (gameState == dialogState) dialogUI.draw(g2, screenWidth, screenHeight);
        
        if (gameState == endingState|| gameState == endingWalkState) {
            g2.setColor(new Color(0, 0, 0, endingAlpha));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            if (gameState == endingState && endingAlpha >= 255) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 80));
                
                // 텍스트 중앙 정렬 로직
                String text = "GAME CLEAR";
                FontMetrics fm = g2.getFontMetrics(); // 폰트의 가로 폭을 계산하기 위해 사용
                int x = (screenWidth - fm.stringWidth(text)) / 2; // (화면너비 - 텍스트너비) / 2
                int y = screenHeight / 2; // 세로 중앙
                
                g2.drawString(text, x, y);
            }
        }
        
        g2.dispose();
    }

    public void startGameThread() { gameThread = new Thread(this); gameThread.start(); }
    @Override public void run() {
        while (gameThread != null) { update(); repaint();
            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }
}