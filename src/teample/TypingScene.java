package teample;

import javax.swing.*;
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

    public TypingScene(GamePanel gp) { this.gp = gp; }

    public void startBattle() {
        words = new ArrayList<>(Arrays.asList("JAVA", "SWING", "KEY", "ROOM", "CODE"));
        Collections.shuffle(words);
        targetWord = words.get(0);
        playerHP = 100; monsterHP = 100; userInput = "";
    }

    public void checkAnswer() {
        if (userInput.equalsIgnoreCase(targetWord)) {
            monsterHP -= 25;
            if (monsterHP > 0) {
                Collections.shuffle(words);
                targetWord = words.get(0);
            }
        } else { playerHP -= 20; }
        userInput = "";

        // 괴물 퇴치 성공 시
        if (monsterHP <= 0) {
            gp.keyH.resetKeys(); 
            // [수정] 괴물 이름 WindowMonster 적용
            gp.showSingleDialog("승리! 'WindowMonster'를 쓰러뜨렸습니다.");
            
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