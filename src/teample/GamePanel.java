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
    
    public KeyHandler keyH = new KeyHandler(this);
    public CollisionChecker cChecker = new CollisionChecker(this);
    public PlayerMove player = new PlayerMove(this, keyH);
    public TileManager tileM = new TileManager(this);
    public TypingScene typingScene = new TypingScene(this);
    public MapManager mapM;
    public DialogUI dialogUI = new DialogUI();
    
    public StageData currentRoom; 
    public int gameState;
    
    // === 타이틀 화면 관련 ===
    public final int titleState = 10;
    public BufferedImage titleImage;
    private long titleStartTime;
    private float titleAlpha = 1.0f;
    
    private final int TITLE_HOLD_MS  = 6000;   // 6초간 선명하게 표시
    private final int TITLE_FADE_MS  = 4000;   // 4초에 걸쳐 페이드아웃
    private final int TITLE_TOTAL_MS = TITLE_HOLD_MS + TITLE_FADE_MS;  // 총 10초
    
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
    
    private boolean isIntroDialog = false;
    private boolean isWindowEvent = false;
    public boolean monsterAlive = false;
    public boolean playerHasBedroomKey = false;
    Thread gameThread;

    // --- 스토리 플래그 변수 ---
    private String[] currentDialogs;
    private int dialogIndex = 0;
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
        
        // === 타이틀 시작 ===
        loadImages();
        gameState = titleState;                       // 게임 시작은 타이틀부터
        titleStartTime = System.currentTimeMillis(); // 시작 시각 기록
        
        changeRoom("침실");
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	if (diaryBook.isOpen) {
                    if (diaryBook.currentPage >= diaryBook.pages.size() - 1) {
                        diaryBook.closeBook();
                    } else {
                        diaryBook.currentPage++;
                    }
                    repaint();
                    return; 
                
                } else if (gameState == dialogState) {
                    dialogIndex++;
                    if (isWindowEvent && dialogIndex == 1) {
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
                        repaint(); 
                    }
                    
                    if (dialogIndex < currentDialogs.length) {
                        dialogUI.currentText = currentDialogs[dialogIndex];
                    } else {
                        dialogUI.isVisible = false;
                        
                        if (isEndingDialog) {
                            isEndingDialog = false;
                            startWalkingAnimation();
                        }
                        else if (isWindowEvent) {
                            isWindowEvent = false;
                            gameState = typingState;
                            typingScene.startBattle();
                        } else if (isIntroDialog) {
                            isIntroDialog = false;
                            gameState = getUpState;
                        } else {
                            gameState = playState;
                        }
                    }
                }
                repaint();
            }
        });              
    }
    
    // === 이미지 로드 메서드 ===
    public void loadImages() {
        try {
            titleImage = ImageIO.read(getClass().getResourceAsStream("title.png"));
        } catch (Exception e) {
            System.out.println("타이틀 이미지 로드 실패: " + e.getMessage());
            titleImage = null;
        }
    }
    
    // === 타이틀 페이드 업데이트 ===
    private void updateTitle() {
        long elapsed = System.currentTimeMillis() - titleStartTime;
        
        if (elapsed < TITLE_HOLD_MS) {
            titleAlpha = 1.0f;
        } else if (elapsed < TITLE_TOTAL_MS) {
            float fadeProgress = (elapsed - TITLE_HOLD_MS) / (float) TITLE_FADE_MS;
            titleAlpha = 1.0f - fadeProgress;
        } else {
            titleAlpha = 0f;
            gameState = introState;  // 10초 끝나면 인트로로
            titleStartTime = 0;
        }
    }
    
    // === 타이틀 그리기 ===
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
        gameState = endingWalkState;
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
            gameState = endingState;
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
        
        if (currentRoom.roomName.equals("침실") && playerHasBedroomKey) {
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
                    keyH.fPressed = false;
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
                            }
                            else if (tileType > 1 && tileType != 7 && tileType != 8) { 
                                keyH.resetKeys();
                                
                                String msg = getObjectDescription(tileType);
                                
                                if (msg != null && !msg.isEmpty()) {
                                    showSingleDialog(msg);
                                    checked = true;
                                }
                                
                                break;
                            }
                        }
                    }
                    if (checked) break;
                }
            }

            // 3. 일반 배틀 체크
            if (monsterAlive && currentRoom.monsterPos != null) {
                if (player.getBounds().intersects(currentRoom.monsterPos)) {
                    keyH.resetKeys();
                    gameState = typingState;
                    typingScene.startBattle();
                    return; 
                }
            }

            // 4. 열쇠 획득 체크
            if (currentRoom.hasKey && player.getBounds().intersects(currentRoom.keyPos)) {
                keyH.resetKeys();
                currentRoom.hasKey = false;
                playerHasBedroomKey = true;
                showSingleDialog("침실 열쇠를 획득했습니다!");
            }

            // 5. 문 이동 체크
            for (Rectangle doorRect : currentRoom.doors.keySet()) {
                if (player.getBounds().intersects(doorRect)) {
                    String next = currentRoom.doors.get(doorRect);
                    
                    if (currentRoom.roomName.equals("침실") && !playerHasBedroomKey) {
                        keyH.resetKeys();
                        showSingleDialog("문이 굳게 잠겨 있다.\n방을 탐색해보자.");
                        player.x = (player.x < doorRect.x) ? player.x - 30 : player.x + 30;
                        player.y = (player.y < doorRect.y) ? player.y - 30 : player.y + 30;
                    } else {
                        changeRoom(next);
                    }
                    break;
                }
            }
        }
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
            case 11: return "잘 가꿔진 풀숲이다.\n누군가 여기에 살고있는걸까?";
            default: return "평범한 물건이다.";
        }
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
        g2.drawString("Room: " + currentRoom.roomName + (playerHasBedroomKey ? " [침실 열쇠 보유]" : ""), 20, 30);
        
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

        if (gameState == typingState) typingScene.draw(g2);

        if (gameState == introState || fadeAlpha > 0) {
            g2.setColor(new Color(0, 0, 0, fadeAlpha));
            g2.fillRect(0, 0, screenWidth, screenHeight);
        }
        
        if (diaryBook.isOpen) diaryBook.draw(g2, screenWidth, screenHeight);
        if (gameState == dialogState) dialogUI.draw(g2, screenWidth, screenHeight);
        if (gameState == typingState) typingScene.draw(g2);
        
        if (gameState == endingState || gameState == endingWalkState) {
            g2.setColor(new Color(0, 0, 0, endingAlpha));
            g2.fillRect(0, 0, screenWidth, screenHeight);
            
            if (gameState == endingState && endingAlpha >= 255) {
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 80));
                
                String text = "GAME CLEAR";
                FontMetrics fm = g2.getFontMetrics();
                int x = (screenWidth - fm.stringWidth(text)) / 2;
                int y = screenHeight / 2;
                
                g2.drawString(text, x, y);
            }
        }
        
        g2.dispose();
    }

    public void startGameThread() { 
        gameThread = new Thread(this); 
        gameThread.start(); 
    }
    
    @Override 
    public void run() {
        while (gameThread != null) { 
            update(); 
            repaint();
            try { Thread.sleep(16); } catch (Exception e) {}
        }
    }
}
