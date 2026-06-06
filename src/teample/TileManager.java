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
                else if(tileType == 9) {//거미줄
                	g2.setColor(new Color(200, 200, 200, 150)); // 희미하고 밝은 회색
                    g2.setStroke(new BasicStroke(1)); // 얇은 선

                    // 1. 대각선 거미줄 (X자 형태)
                    g2.drawLine(x + 5, y + 5, x + gp.tileSize - 5, y + gp.tileSize - 5);
                    g2.drawLine(x + gp.tileSize - 5, y + 5, x + 5, y + gp.tileSize - 5);

                    // 2. 중앙을 가로지르는 십자형 거미줄
                    g2.drawLine(x + gp.tileSize / 2, y + 5, x + gp.tileSize / 2, y + gp.tileSize - 5);
                    g2.drawLine(x + 5, y + gp.tileSize / 2, x + gp.tileSize - 5, y + gp.tileSize / 2);

                    // 3. 디테일: 거미줄에 맺힌 먼지 표현 (작은 점)
                    g2.setColor(new Color(255, 255, 255, 200));
                    g2.fillRect(x + gp.tileSize / 2 - 1, y + 10, 2, 2);
                    g2.fillRect(x + 10, y + gp.tileSize / 2 - 1, 2, 2);
                }
                else if(tileType == 10) {//포스터
                	int p = 6; // 포스터와 타일 경계 간격
                    
                    // 1. 포스터 배경 (누렇게 바랜 종이색)
                    g2.setColor(new Color(220, 210, 180)); 
                    g2.fillRect(x + p, y + p, gp.tileSize - (p * 2), gp.tileSize - (p * 2));
                    
                    // 2. 포스터 테두리 (조금 더 진한 종이색)
                    g2.setColor(new Color(180, 170, 140));
                    g2.drawRect(x + p, y + p, gp.tileSize - (p * 2), gp.tileSize - (p * 2));
                    
                    // 3. 포스터 찢어진 느낌 (기괴한 낙서나 손상)
                    g2.setColor(new Color(150, 140, 110));
                    // 대각선으로 그어진 찢어진 자국 표현
                    g2.drawLine(x + p + 2, y + p + 2, x + gp.tileSize - p - 2, y + gp.tileSize - p - 2);
                    
                    // 4. 낙서/흔적 (칙칙한 느낌을 주는 작은 점)
                    g2.setColor(new Color(100, 90, 80));
                    g2.fillRect(x + 12, y + 12, 2, 2);
                    g2.fillRect(x + 24, y + 18, 2, 2);
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
                else if(tileType == 12) {//옷장
                	int xPos = x + 4;
                    int yPos = y + 4;
                    int w = gp.tileSize - 8;
                    int h = gp.tileSize - 8;

                    // 1. 옷장 몸통 (어두운 나무색)
                    g2.setColor(new Color(60, 40, 30));
                    g2.fillRect(xPos, yPos, w, h);

                    // 2. 옷장 문 (약간 더 밝은 나무색, 비대칭으로 배치하여 낡은 느낌)
                    g2.setColor(new Color(90, 60, 40));
                    g2.fillRect(xPos + 4, yPos + 4, w / 2 - 2, h - 8); // 왼쪽 문
                    g2.fillRect(xPos + w / 2 + 2, yPos + 4, w / 2 - 4, h - 8); // 오른쪽 문

                    // 3. 문 사이 틈 (귀신이 나올 것 같은 어두운 그림자)
                    g2.setColor(new Color(20, 10, 5));
                    g2.fillRect(xPos + w / 2 - 1, yPos + 4, 2, h - 8);

                    // 4. 옷장 손잡이 (작고 녹슨 느낌)
                    g2.setColor(new Color(150, 150, 150));
                    g2.fillOval(xPos + 8, yPos + h / 2, 4, 4);
                    g2.fillOval(xPos + w - 12, yPos + h / 2, 4, 4);

                    // 5. 세월의 흔적 (스크래치)
                    g2.setColor(new Color(0, 0, 0, 100));
                    g2.drawLine(xPos + 4, yPos + 10, xPos + w - 4, yPos + 20);
                }
                else if(tileType == 13) {//인형
                	int p = 12; // 인형 크기 조절
                	int offset = (int)gp.getDollOffset(); // GamePanel의 값을 가져옴
                    int centerX = x + gp.tileSize / 2 + offset; // X좌표를 흔들리게!
                    int centerY = y + gp.tileSize / 2;

                    // 1. 인형 몸통 (칙칙한 살구색 혹은 낡은 천 색상)
                    g2.setColor(new Color(200, 180, 160));
                    g2.fillOval(centerX - 8, centerY - 8, 16, 16); // 머리
                    g2.fillOval(centerX - 6, centerY + 4, 12, 14); // 몸통

                    // 2. 인형 팔/다리 (헝겊 느낌)
                    g2.setColor(new Color(180, 160, 140));
                    g2.fillRect(centerX - 10, centerY, 4, 10); // 왼쪽 팔
                    g2.fillRect(centerX + 6, centerY, 4, 10);  // 오른쪽 팔

                    // 3. 삐져나온 솜/실 (낡은 느낌 강조)
                    g2.setColor(new Color(230, 230, 230));
                    g2.fillRect(centerX + 4, centerY - 4, 2, 6); 

                    // 4. 단추 눈 (하나가 떨어지기 직전인 것처럼 배치)
                    g2.setColor(Color.BLACK);
                    g2.fillOval(centerX - 4, centerY - 4, 3, 3); // 왼쪽 눈
                    // 오른쪽 눈은 아주 작게 그려서 '떨어진 것'처럼 표현
                    g2.fillOval(centerX + 2, centerY - 3, 1, 1); 
                    
                    // 5. 기괴한 입 (실로 꿰맨 듯한 느낌)
                    g2.setColor(new Color(100, 80, 80));
                    g2.drawLine(centerX - 3, centerY + 2, centerX + 3, centerY + 2);
                }
                else if(tileType == 14) {//시계
                	int centerX = x + gp.tileSize / 2;
                    int centerY = y + gp.tileSize / 2;
                    int clr = 16; // 시계 반지름

                    // 1. 시계 본체 (낡은 금속/나무 프레임)
                    g2.setColor(new Color(80, 70, 60));
                    g2.fillOval(centerX - clr, centerY - clr, clr * 2, clr * 2);
                    
                    // 2. 시계판 (빛바랜 흰색)
                    g2.setColor(new Color(220, 220, 200));
                    g2.fillOval(centerX - clr + 3, centerY - clr + 3, (clr - 3) * 2, (clr - 3) * 2);

                    // 3. 시계 바늘 (멈춰 있는 시간)
                    g2.setColor(Color.BLACK);
                    // 시침
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(centerX, centerY, centerX, centerY - 6);
                    // 분침
                    g2.drawLine(centerX, centerY, centerX + 8, centerY);

                    // 4. 시계 유리 느낌 (약간의 하이라이트)
                    g2.setColor(new Color(255, 255, 255, 50));
                    g2.fillOval(centerX - clr + 5, centerY - clr + 5, 6, 6);
                }
                else if(tileType == 15){//화분
                	int padding = 4;
                    int internalPadding = 8;
                    int actualSize = gp.tileSize - (padding * 2);

                    // 1. 화분 몸통 (어두운 테라코타 색상)
                    g2.setColor(new Color(110, 70, 40)); 
                    g2.fillRect(x + internalPadding, y + internalPadding, actualSize - (internalPadding * 2), actualSize / 2);

                    // 2. 화분 윗부분 림 (조금 더 밝은 색상)
                    g2.setColor(new Color(160, 100, 70)); 
                    g2.fillRect(x + (padding * 2), y + internalPadding - (padding / 2), actualSize - (padding * 4), padding / 2);

                    // 3. 마른 식물 표현 (앙상하고 칙칙한 갈색/초록)
                    g2.setColor(new Color(100, 110, 80)); 

                    // 식물 몸통 (중앙에서 위로 뻗음)
                    g2.fillRect(x + (gp.tileSize / 2) - 2, y + padding, 4, gp.tileSize / 2);

                    // 가지 1 (왼쪽)
                    g2.fillRect(x + (gp.tileSize / 2) - 8, y + padding + 6, 6, 2);
                    // 가지 2 (오른쪽)
                    g2.fillRect(x + (gp.tileSize / 2) + 2, y + padding + 10, 8, 2);

                    // 식물 느낌을 위해 작은 점 추가
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x + 10, y + 10, 4, 4); // [요청 반영] 풀숲과 동일한 점 추가
                }
                else if(tileType == 16) {//벽난로
                	int p = 4; // 테두리 간격
                    int w = gp.tileSize - (p * 2);
                    int h = gp.tileSize - (p * 2);

                    // 1. 벽난로 외벽 (어두운 회색 벽돌 느낌)
                    g2.setColor(new Color(80, 80, 80));
                    g2.fillRect(x + p, y + p, w, h);
                    
                    // 2. 벽돌 질감 라인 (가로선)
                    g2.setColor(new Color(60, 60, 60));
                    g2.drawLine(x + p, y + p + h/2, x + p + w, y + p + h/2);
                    
                    // 3. 화구 (안쪽 어두운 공간)
                    g2.setColor(new Color(20, 20, 20));
                    g2.fillRect(x + p + 6, y + p + 6, w - 12, h - 12);
                    
                    // 4. 꺼진 불씨 (흐릿한 붉은 점들)
                    g2.setColor(new Color(150, 40, 20, 100)); // 투명도 있는 붉은색
                    g2.fillOval(x + gp.tileSize/2 - 4, y + gp.tileSize - 12, 8, 4);
                    
                    // 5. 그을음 (벽난로 위쪽 벽면이 검게 탄 흔적)
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRect(x + p + 2, y + p - 8, w - 4, 8);
                }
                else if(tileType == 17) {//소파
                	int p = 6; 
                    int w = gp.tileSize - (p * 2);
                    int h = gp.tileSize - (p * 2);

                    // 1. 소파 전체 형태 (칙칙한 갈색/어두운 회색)
                    g2.setColor(new Color(100, 80, 60));
                    g2.fillRect(x + p, y + p, w, h);

                    // 2. 등받이 부분 (약간 더 어두운 색)
                    g2.setColor(new Color(80, 60, 40));
                    g2.fillRect(x + p + 2, y + p + 2, w - 4, h / 2 - 2);

                    // 3. 앉는 부분 (쿠션이 꺼진 표현)
                    g2.setColor(new Color(120, 100, 80));
                    g2.fillRect(x + p + 2, y + p + h / 2 + 2, w - 4, h / 2 - 4);

                    // 4. 해진 느낌 (긁힌 자국 - 칙칙한 공포 분위기)
                    g2.setColor(new Color(50, 40, 30));
                    g2.drawLine(x + p + 4, y + p + h / 2, x + w + p - 4, y + p + h / 2);
                    
                    // 5. 삐져나온 솜/충전재 (낡음을 강조)
                    g2.setColor(new Color(220, 220, 200));
                    g2.fillRect(x + p + (w/2) - 2, y + p + (h/2) + 4, 4, 4);
                }
                else if(tileType == 20) {//그냥인형
                	int p = 12; // 인형 크기 조절
                	int offset = (int)gp.getDollOffset(); // GamePanel의 값을 가져옴
                    int centerX = x + gp.tileSize / 2 + offset; // X좌표를 흔들리게!
                    int centerY = y + gp.tileSize / 2;

                    // 1. 인형 몸통 (칙칙한 살구색 혹은 낡은 천 색상)
                    g2.setColor(new Color(200, 180, 160));
                    g2.fillOval(centerX - 8, centerY - 8, 16, 16); // 머리
                    g2.fillOval(centerX - 6, centerY + 4, 12, 14); // 몸통

                    // 2. 인형 팔/다리 (헝겊 느낌)
                    g2.setColor(new Color(180, 160, 140));
                    g2.fillRect(centerX - 10, centerY, 4, 10); // 왼쪽 팔
                    g2.fillRect(centerX + 6, centerY, 4, 10);  // 오른쪽 팔

                    // 3. 삐져나온 솜/실 (낡은 느낌 강조)
                    g2.setColor(new Color(230, 230, 230));
                    g2.fillRect(centerX + 4, centerY - 4, 2, 6); 

                    // 4. 단추 눈 (하나가 떨어지기 직전인 것처럼 배치)
                    g2.setColor(Color.BLACK);
                    g2.fillOval(centerX - 4, centerY - 4, 3, 3); // 왼쪽 눈
                    // 오른쪽 눈은 아주 작게 그려서 '떨어진 것'처럼 표현
                    g2.fillOval(centerX + 2, centerY - 3, 1, 1); 
                    
                    // 5. 기괴한 입 (실로 꿰맨 듯한 느낌)
                    g2.setColor(new Color(100, 80, 80));
                    g2.drawLine(centerX - 3, centerY + 2, centerX + 3, centerY + 2);
                }
            }
        }
    }
}