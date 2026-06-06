package teample;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class TypingScene {
    GamePanel gp;
    public String userInput = "";
    private int playerHP, monsterHP;
    private String targetWord = "";
    private ArrayList<String> words;
    private int wordIndex = 0;
    
    public TypingScene(GamePanel gp) { this.gp = gp; }

    public void startBattle() {
        words = new ArrayList<>(Arrays.asList("Run", "Room", "Away", "This", "From"));
        //단어를 조합하면 run away from this room == 이 방에서 도망쳐라
        Collections.shuffle(words);
        wordIndex = 0;
        targetWord = words.get(wordIndex);
        playerHP = 100; monsterHP = 100; userInput = "";
    }

    public void checkAnswer() {
        if (userInput.equals(targetWord)) {
            monsterHP -= 20;
            if (monsterHP > 0) {
            	wordIndex++;
            	if (wordIndex >= words.size()) {
                    Collections.shuffle(words);
                    wordIndex = 0;
                }
                targetWord = words.get(wordIndex);
            }
        } else { playerHP -= 20; }
        userInput = "";

        // 괴물 퇴치 성공 시
        if (monsterHP <= 0) {
            gp.keyH.resetKeys(); 
            gp.onMonsterDefeated(gp.currentRoom.roomName);
            // [수정] 괴물 이름 WindowMonster 적용
            gp.showSingleDialog("괴물을 쓰러뜨렸다.");
            
            
            gp.monsterAlive = false; 
            if (gp.currentRoom != null && gp.currentRoom.monsterPos != null) {
                int dropX = gp.currentRoom.monsterPos.x + 14; 
                int dropY = gp.currentRoom.monsterPos.y + gp.tileSize + 14; 
                
                gp.currentRoom.setKey(dropX, dropY);
            }
            
            gp.gameState = gp.playState; 
        } 
        // 플레이어 패배 시 (게임 오버 및 방 리셋)
        else if (playerHP <= 0) {
            playerHP = 100;
            monsterHP = 100;
            gp.playerHasBedroomKey = false; // 패배 시 침실 열쇠 리셋
            gp.changeRoom("침실"); 
            gp.player.x = 500; 
            gp.player.y = 350;
            gp.monsterAlive = gp.currentRoom.hasMonster;
            gp.gameState = gp.playState;
        }
    }

    public void battleFinished() {
        // ... 전투 결과 처리 ...
        gp.onMonsterDefeated(gp.currentRoom.roomName);
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        g2.drawString("WindowMonster HP: " + monsterHP, 50, 50); // 이름 반영
        g2.drawString("Player HP: " + playerHP, 50, 100);
        
        g2.setColor(Color.YELLOW);
        g2.drawString("Target: " + targetWord, 300, 250);
        
        g2.setColor(Color.WHITE);
        g2.drawRect(300, 300, 400, 50);
        g2.drawString(userInput, 310, 335);
    }
}