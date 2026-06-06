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

        // 2번 코드의 안전장치: 인트로, 기상, 이벤트 연출 중에는 조작 불가 (꼬임 방지)
        if (gp.gameState == gp.introState || gp.gameState == gp.getUpState ||
            gp.gameState == gp.diaryEventState || gp.gameState == gp.windowEventState) {
            return;
        }

        // 1. 일반 이동 상태 처리
        if (gp.gameState == gp.playState) {
            if (code == KeyEvent.VK_W) upPressed = true;
            if (code == KeyEvent.VK_S) downPressed = true;
            if (code == KeyEvent.VK_A) leftPressed = true;
            if (code == KeyEvent.VK_D) rightPressed = true;
            if (code == KeyEvent.VK_ENTER) enterPressed = true;
            if (code == KeyEvent.VK_F) fPressed = true;
        }
        // 2. 타이핑 상태 처리 (보스전/일반전)
        else if (gp.gameState == gp.typingState) {
            // 백스페이스 처리 (글자 수 체크 방식으로 안전하게 결합)
            if (code == KeyEvent.VK_BACK_SPACE) {
                if (gp.isBossBattle && getBossInputLength() > 0) {
                    gp.TypingBoss.userInput = gp.TypingBoss.userInput.substring(0, getBossInputLength() - 1);
                } else if (!gp.isBossBattle && getNormalInputLength() > 0) {
                    gp.typingScene.userInput = gp.typingScene.userInput.substring(0, getNormalInputLength() - 1);
                }
            }
            // 엔터키 처리
            else if (code == KeyEvent.VK_ENTER) {
                if (gp.isBossBattle) gp.TypingBoss.checkAnswer();
                else gp.typingScene.checkAnswer();
            }
        }
    }

    // 2번 코드의 글자 수 반환 로직을 보스전/일반전으로 각각 안전하게 분리
    private int getNormalInputLength() { return gp.typingScene.userInput.length(); }
    private int getBossInputLength() { return gp.TypingBoss.userInput.length(); }

    @Override
    public void keyTyped(KeyEvent e) {
        if (gp.gameState == gp.typingState) {
            char c = e.getKeyChar();

            // 백스페이스, 엔터, 이스케이프 등 제어 문자는 여기서 처리하지 않음 (1번 코드 검증 조건)
            if (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_ENTER && c != KeyEvent.VK_ESCAPE && c != '\b') {
                // 문자, 숫자, 공백일 때만 처리하는 조건 결합 (2번 코드 방식)
                if (Character.isLetterOrDigit(c) || c == ' ') {
                    if (gp.isBossBattle) {
                        gp.TypingBoss.userInput += c;
                    } else {
                        gp.typingScene.userInput += c;
                    }
                }
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
        if (code == KeyEvent.VK_ENTER) enterPressed = false; // 2번 코드의 엔터 해제 누락 방지
        if (code == KeyEvent.VK_F) fPressed = false;
    }
}