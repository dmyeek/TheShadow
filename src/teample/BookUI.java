package teample;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.util.*;

public class BookUI {
    public boolean isOpen = false;
    public int currentPage = 0;
    public List<String> pages = new ArrayList<>(); // 책 페이지 내용들
    
    public boolean justClosedAfterLastPage = false;
    
    //public int maxPage = 3;//페이지 장

    public BookUI() {
        pages.add("20XX.05.25\n오늘은 일기장과 대사창을 뜯어고쳤다.\n이게 이벤트 연출과 이어지다 보니\n많은 시간이 소모되었다.\n\n 이제 여기에 누군가 스토리를 써주겠지.");
        pages.add("20XX.06.01\n튜토리얼인 침실은 어느정도 되었지만\n다른 스테이지와 합치는 것이 걱정된다.\n아무래도 시간이 부족할 듯.?\n밤을 새자");
        pages.add("조작 안내\nW - 위로 이동합니다.\nA - 왼쪽으로 이동합니다.\nS - 아래로 이동합니다.\nD - 오른쪽으로 이동합니다.\nF - 주변을 조사합니다.\n마우스 클릭 - 대화창을 넘깁니다.\nEnter - 타이핑 후 Enter를 누르세요.");
    }
    
 // BookUI.java
    public void closeBook() {
        if (currentPage >= pages.size() - 1) {
            justClosedAfterLastPage = true;
        }
        isOpen = false;
    }
    
    public void draw(Graphics2D g2, int screenWidth, int screenHeight) {
        if (!isOpen) return;

        // 1. 책 배경
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(100, 100, screenWidth - 200, screenHeight - 200);
        
        // 2. 책 테두리
        g2.setColor(Color.WHITE);
        g2.drawRect(100, 100, screenWidth - 200, screenHeight - 200);

        // 3. 내용 출력 (줄바꿈 처리)
        g2.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        String[] lines = pages.get(currentPage).split("\n");
        int y = 150;
        for (String line : lines) {
            g2.drawString(line, 150, y);
            y += 30;
        }
        
        // 4. 페이지 넘김 버튼
        g2.drawString("다음 >", 700, 600);
    }
}