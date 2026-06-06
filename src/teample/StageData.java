package teample;

import java.awt.Rectangle;
import java.util.HashMap;

public class StageData {
    public String roomName;
    public int[][] mapLayout;
    public Rectangle monsterPos;
    public String requiredKey;
    public String keyName;
    public Rectangle keyPos;
    public boolean hasMonster = false;
    public boolean isMonsterDefeated = false;
    public boolean hasKey = false;
    public HashMap<Rectangle, String> doors = new HashMap<>();

    // --- 상호작용 오브젝트용 변수 ---
    public Rectangle interactObjectPos;
    public String interactText = "";
    public boolean hasInteractObject = false;

    public StageData(String roomName, String requiredKey, int[][] mapLayout) {
        this.roomName = roomName;
        this.requiredKey = requiredKey;
        this.mapLayout = mapLayout;
        this.keyName = ""; // 기본값은 빈 문자열로 초기화
    }
    public void removeTile(int col, int row) {
        if (row >= 0 && row < mapLayout.length && col >= 0 && col < mapLayout[0].length) {
            mapLayout[row][col] = 0; // 0은 보통 '빈 공간'을 의미합니다.
        }
    }
    // 특정 타일 좌표에 구조물 배치
    public void setStructure(int col, int row, int type) {
        if (row >= 0 && row < mapLayout.length && col >= 0 && col < mapLayout[0].length) {
            mapLayout[row][col] = type;
        }
    }

    public void setInteractObject(int x, int y, int width, int height, String text) {
        this.interactObjectPos = new Rectangle(x, y, width, height);
        this.interactText = text;
        this.hasInteractObject = true;
    }

    public void setMonster(int x, int y) {
        this.monsterPos = new Rectangle(x, y, 48, 48);
        this.hasMonster = true;
    }
    public void setKey(int x, int y) {
        this.keyPos = new Rectangle(x, y, 20, 20);
        this.hasKey = true;
    }
    public void addDoor(int x, int y, int width, int height, String toRoomName) {
        doors.put(new Rectangle(x, y, width, height), toRoomName);
    }
}