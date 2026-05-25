package teample;

import java.awt.event.*;

public class KeyHandler implements KeyListener {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed;
    public boolean fPressed; 
    GamePanel gp;

    public KeyHandler(GamePanel gp) { this.gp = gp; }

    public void resetKeys() {
        upPressed = false; downPressed = false;
        leftPressed = false; rightPressed = false;
        enterPressed = false;
        fPressed = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        
        if (gp.gameState == gp.introState || gp.gameState == gp.getUpState || 
            gp.gameState == gp.diaryEventState || gp.gameState == gp.windowEventState) {
            return;
        }

        if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            if (code == KeyEvent.VK_ENTER) enterPressed = true;
            if (code == KeyEvent.VK_F) fPressed = true; 
        } else if (gp.gameState == gp.typingState) {
            if (code == KeyEvent.VK_BACK_SPACE && userInputLength() > 0) {
                gp.typingScene.userInput = gp.typingScene.userInput.substring(0, userInputLength() - 1);
            } else if (code == KeyEvent.VK_ENTER) {
                gp.typingScene.checkAnswer();
            }
        }
    }

    private int userInputLength() { return gp.typingScene.userInput.length(); }

    @Override
    public void keyTyped(KeyEvent e) {
        if (gp.gameState == gp.typingState) {
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) || c == ' ') {
                gp.typingScene.userInput += Character.toUpperCase(c);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) upPressed = false;
        if (code == KeyEvent.VK_S) downPressed = false;
        if (code == KeyEvent.VK_A) leftPressed = false;
        if (code == KeyEvent.VK_D) rightPressed = false;
        if (code == KeyEvent.VK_ENTER) enterPressed = false;
        if (code == KeyEvent.VK_F) fPressed = false;
    }
}