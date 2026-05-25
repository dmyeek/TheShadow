package teample;

import java.awt.*;

public class TileManager {
    GamePanel gp;
    public int[][] mapData;

    public TileManager(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        if (mapData == null) return;
        for (int r = 0; r < gp.maxScreenRow; r++) {
            for (int c = 0; c < gp.maxScreenCol; c++) {
                int tileType = mapData[r][c];
                int x = c * gp.tileSize;
                int y = r * gp.tileSize;

                // 1. 일반 벽
                if (tileType == 1) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
                }
                // 2. 침대
                else if (tileType == 2) {
                    g2.setColor(new Color(135, 206, 250));
                    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
                    g2.setColor(Color.WHITE);
                    g2.drawRect(x + 2, y + 2, gp.tileSize - 4, gp.tileSize - 4);
                }
                // 3. 책상
                else if (tileType == 3) {
                    g2.setColor(new Color(160, 82, 45)); 
                    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
                    g2.setColor(new Color(139, 69, 19));
                    g2.drawRect(x, y, gp.tileSize - 1, gp.tileSize - 1);
                }
                // 4. 멀쩡한 창문
                else if (tileType == 4) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
                    g2.setColor(new Color(173, 216, 230)); 
                    g2.fillRect(x + 2, y + 6, gp.tileSize - 4, gp.tileSize - 12);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawLine(x + gp.tileSize/2, y + 6, x + gp.tileSize/2, y + gp.tileSize - 6);
                    g2.drawLine(x + 2, y + gp.tileSize/2, x + gp.tileSize - 2, y + gp.tileSize/2);
                }
                // 5. 책장
                else if (tileType == 5) {
                    g2.setColor(new Color(101, 67, 33));
                    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
                    g2.setColor(Color.YELLOW);
                    g2.fillRect(x + 6, y + 10, 8, gp.tileSize - 20);
                    g2.setColor(Color.CYAN);
                    g2.fillRect(x + 20, y + 12, 6, gp.tileSize - 24);
                }
                // 6. 의자
                else if (tileType == 6) {
                    g2.setColor(new Color(222, 184, 135)); 
                    g2.fillOval(x + 6, y + 6, gp.tileSize - 12, gp.tileSize - 12);
                    g2.setColor(new Color(139, 69, 19));
                    g2.drawOval(x + 6, y + 6, gp.tileSize - 12, gp.tileSize - 12);
                }
                // 7. [신규] 깨진 창문틀 (유리가 날아가고 텅 빈 실루엣)
                else if (tileType == 7) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(x, y, gp.tileSize, gp.tileSize);
                    g2.setColor(new Color(70, 80, 90)); // 어둡고 깨진 흔적
                    g2.fillRect(x + 4, y + 6, gp.tileSize - 8, gp.tileSize - 12);
                    g2.setColor(Color.LIGHT_GRAY);
                    // 깨진 유리 뾰족한 단면 표현
                    g2.drawLine(x + 4, y + 6, x + 12, y + 15);
                    g2.drawLine(x + gp.tileSize - 4, y + 6, x + gp.tileSize - 12, y + 18);
                }
                // 8. [신규] 바닥에 떨어진 유리 파편 
                else if (tileType == 8) {
                    g2.setColor(new Color(200, 230, 245, 180)); // 투명한 유리 조각 표현
                    int[] xPoints = {x + 10, x + 24, x + 16};
                    int[] yPoints = {y + 30, y + 14, y + 38};
                    g2.fillPolygon(xPoints, yPoints, 3);

                    int[] xPoints2 = {x + 32, x + 42, x + 24};
                    int[] yPoints2 = {y + 10, y + 26, y + 28};
                    g2.fillPolygon(xPoints2, yPoints2, 3);
                }
                else if (tileType == 11) { // 풀숲 (타일 11)
                    g2.setColor(new Color(34, 139, 34)); // 포레스트 그린 색상
                    g2.fillRect(x + 4, y + 4, gp.tileSize - 8, gp.tileSize - 8);
                    g2.setColor(new Color(0, 100, 0)); // 짙은 녹색 테두리
                    g2.drawRect(x + 4, y + 4, gp.tileSize - 8, gp.tileSize - 8);
                    // 풀 느낌을 위해 작은 점 추가
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x + 10, y + 10, 4, 4);
                }
            }
        }
    }
}