package teample;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerMove {
    GamePanel gp;
    KeyHandler keyH;
    public int x, y;
    public int speed = 5;

    // 방향 및 스프라이트 이미지 변수
    public String direction = "down"; 
    private BufferedImage upImg, downImg, leftImg, rightImg;

    public PlayerMove(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;
        
        // 코드가 직접 비트맵 이미지를 생성함
        generatePixelCharacters(); 
    }

    /**
     * 32x48 해상도의 픽셀 도트 캐릭터를 코드로 직접 생성하는 메서드
     */
    private void generatePixelCharacters() {
        upImg = createCharacterImage("up");
        downImg = createCharacterImage("down");
        leftImg = createCharacterImage("left");
        rightImg = createCharacterImage("right");
    }

    private BufferedImage createCharacterImage(String dir) {
        // 언더테일 비율에 맞춘 32 x 48 크기의 투명 이미지 생성
        BufferedImage img = new BufferedImage(32, 48, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // 컬러 팔레트 정의
        Color hair  = new Color(101, 67, 33);    // 갈색 머리
        Color skin  = new Color(255, 219, 172);  // 살구색 피부
        Color shirt = new Color(50, 100, 200);   // 파란 셔츠
        Color stripe = new Color(220, 80, 150);  // 핑크색 줄무늬
        Color pants = new Color(70, 60, 60);     // 바지
        Color eyes  = Color.BLACK;

        // 1. 머리카락 (기본 틀)
        g.setColor(hair);
        g.fillRect(6, 4, 20, 12);
        g.fillRect(4, 6, 24, 10);

        // 2. 얼굴 및 방향별 눈 처리
        g.setColor(skin);
        g.fillRect(6, 12, 20, 10);
        
        g.setColor(eyes);
        if (dir.equals("down")) {
            g.fillRect(9, 15, 3, 2);  // 왼쪽 눈
            g.fillRect(20, 15, 3, 2); // 오른쪽 눈
            g.setColor(hair);
            g.fillRect(6, 4, 20, 4);  // 앞머리 음영
        } else if (dir.equals("left")) {
            g.fillRect(6, 15, 3, 2);  // 왼쪽을 보는 눈
            g.fillRect(14, 15, 3, 2);
            g.setColor(hair);
            g.fillRect(22, 6, 4, 16); // 뒷머리가 덮는 부분
        } else if (dir.equals("right")) {
            g.fillRect(15, 15, 3, 2); // 오른쪽을 보는 눈
            g.fillRect(23, 15, 3, 2);
            g.setColor(hair);
            g.fillRect(6, 6, 4, 16);  // 뒷머리가 덮는 부분
        } else if (dir.equals("up")) {
            // 뒷모습은 눈이 없고 전부 머리카락으로 덮음
            g.setColor(hair);
            g.fillRect(6, 10, 20, 12);
        }

        // 3. 몸통 (셔츠와 줄무늬)
        g.setColor(shirt);
        g.fillRect(6, 22, 20, 14); // 몸통 기본
        g.fillRect(3, 22, 3, 8);   // 왼팔
        g.fillRect(26, 22, 3, 8);  // 오른팔

        // 줄무늬
        g.setColor(stripe);
        g.fillRect(6, 25, 20, 3);
        g.fillRect(6, 31, 20, 3);

        // 4. 바지와 신발
        g.setColor(pants);
        g.fillRect(8, 36, 7, 8);   // 왼다리 바지
        g.fillRect(17, 36, 7, 8);  // 오른다리 바지 (★ y좌표 8 -> 36으로 수정 완료!)

        g.setColor(hair);          // 갈색 신발
        g.fillRect(7, 44, 8, 4);
        g.fillRect(17, 44, 8, 4);

        g.dispose();
        return img;
    }

    public void update() {
        if (keyH.upPressed) {
            direction = "up";
            y -= speed;
            if (gp.cChecker.checkTile(this)) y += speed;
        }
        if (keyH.downPressed) {
            direction = "down";
            y += speed;
            if (gp.cChecker.checkTile(this)) y -= speed;
        }
        if (keyH.leftPressed) {
            direction = "left";
            x -= speed;
            if (gp.cChecker.checkTile(this)) x += speed;
        }
        if (keyH.rightPressed) {
            direction = "right";
            x += speed;
            if (gp.cChecker.checkTile(this)) x -= speed;
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;

        switch (direction) {
            case "up":    image = upImg;    break;
            case "down":  image = downImg;  break;
            case "left":  image = leftImg;  break;
            case "right": image = rightImg; break;
        }

        if (image != null) {
            // 48x48 타일 그리드 정중앙에 32x48 캐릭터 정렬 배치
            int offsetX = (gp.tileSize - 32) / 2; 
            g2.drawImage(image, x + offsetX, y, 32, gp.tileSize, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }
    }

    public Rectangle getBounds() {
        // 가로 여백을 10에서 2로 줄여서 문(Door)이나 열쇠에 판정이 닿도록 수정
        return new Rectangle(x + 2, y + 2, gp.tileSize - 4, gp.tileSize - 4);
    }
}