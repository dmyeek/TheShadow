package teample;

import java.awt.*;

public class PlayerMove {
    GamePanel gp;
    KeyHandler keyH;
    public int x, y;
    public int speed = 5;

    public PlayerMove(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
    }

    public void update() {
        if (keyH.upPressed) {
            y -= speed;
            if (gp.cChecker.checkTile(this)) y += speed;
        }
        if (keyH.downPressed) {
            y += speed;
            if (gp.cChecker.checkTile(this)) y -= speed;
        }
        if (keyH.leftPressed) {
            x -= speed;
            if (gp.cChecker.checkTile(this)) x += speed;
        }
        if (keyH.rightPressed) {
            x += speed;
            if (gp.cChecker.checkTile(this)) x -= speed;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, gp.tileSize, gp.tileSize);
    }

    public Rectangle getBounds() {
        // 히트박스를 살짝 작게 설정 (좁은 공간 통과 용이)
        return new Rectangle(x + 5, y + 5, gp.tileSize - 10, gp.tileSize - 10);
    }
}