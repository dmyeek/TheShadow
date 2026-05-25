package teample;
import java.awt.*;

public class DialogUI {
    public boolean isVisible = false;
    public String currentText = "";
    
    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        if (!isVisible) return;

        // 대화창 배경
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(50, screenHeight - 150, screenWidth - 100, 120);
        g2.setColor(Color.WHITE);
        g2.drawRect(55, screenHeight - 145, screenWidth - 110, 110);
        
        // 텍스트 출력 (줄바꿈 처리)
        g2.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        String[] lines = currentText.split("\n"); // \n 기준으로 나누기
        int y = screenHeight - 115;
        for (String line : lines) {
            g2.drawString(line, 80, y);
            y += 30; // 다음 줄로 간격 띄우기
        }
    }
}