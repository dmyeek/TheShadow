package teample;

import java.util.HashMap;

public class MapManager {
    public HashMap<String, StageData> roomMap = new HashMap<>();

    public MapManager(int col, int row) {
        // --- 1. 침실 (시작 방 - 구상도 인테리어 적용) ---
        StageData bedroom = new StageData("침실", "침실 열쇠",createBaseMap(col, row));
        bedroom.addDoor(950, 330, 10, 100, "복도_왼쪽"); 
        bedroom.keyName = "침실 열쇠";
        // 지우지 말것
        String tutorialText = "[낡은 일기장]\n\n";
        bedroom.setInteractObject(4 * 48, 10 * 48, 48, 48, tutorialText);

        // --- 구상도 기반 침실 구조물 배치 ---
        // 1. 창문 (상단 벽면 정중앙 3칸 연속 배치) - 이벤트 시작 지점
        bedroom.setStructure(9, 0, 4);
        bedroom.setStructure(10, 0, 4); 
        bedroom.setStructure(11, 0, 4);

        // 2. 침대 (좌측 상단 구석, 가로 2칸 x 세로 3칸)
        for (int r = 1; r <= 3; r++) {
            bedroom.setStructure(1, r, 2);
            bedroom.setStructure(2, r, 2);
        }

        // 3. 대형 책상 (중앙 하단, 가로 6칸 x 세로 3칸)
        for (int r = 10; r <= 12; r++) {
            for (int c = 3; c <= 8; c++) {
                bedroom.setStructure(c, r, 3);
            }
        }

        // 4. 의자들 (책상 주변 배치)
        bedroom.setStructure(2, 10, 6); 
        bedroom.setStructure(2, 12, 6); 
        bedroom.setStructure(9, 10, 6); 
        bedroom.setStructure(9, 11, 6); 
        bedroom.setStructure(4, 13, 6); 
        bedroom.setStructure(7, 13, 6); 

        // 5. 책장 (우측 벽면 및 하단 우측 구석)
        bedroom.setStructure(19, 11, 5);
        bedroom.setStructure(19, 12, 5);
        bedroom.setStructure(19, 13, 5);
        
        for (int c = 10; c <= 19; c++) { 
            bedroom.setStructure(c, 14, 5);
        }
        
        //화분
        bedroom.setStructure(19, 1, 15);
        
        //벽난로
        bedroom.setStructure(16, 0, 16);
        bedroom.setStructure(17, 0, 16);
        
        //소파
        bedroom.setStructure(15, 3, 17);
        bedroom.setStructure(16, 3, 17);
        bedroom.setStructure(17, 3, 17);
        bedroom.setStructure(18, 3, 17);
        
        //옷장
        bedroom.setStructure(3, 1, 12);
        bedroom.setStructure(4, 1, 12);

        roomMap.put("침실", bedroom);

        // --- 2. 복도 시리즈 ---
        StageData hallwayL = new StageData("복도_왼쪽", "침실 열쇠",createAdvancedMap(col, row, false, true, false, false));
        hallwayL.addDoor(48, 330, 10, 100, "침실");
        hallwayL.addDoor(1002, 0, 5, 800, "복도_중앙"); 
        hallwayL.addDoor(200, 708, 100, 10, "방1");
        roomMap.put("복도_왼쪽", hallwayL);

        StageData hallwayC = new StageData("복도_중앙",null, createAdvancedMap(col, row, true, true, false, false));
        hallwayC.addDoor(0, 0, 5, 800, "복도_왼쪽");
        hallwayC.addDoor(1002, 0, 5, 800, "복도_오른쪽");
        hallwayC.addDoor(460, 710, 100, 10, "중앙계단1"); 
        roomMap.put("복도_중앙", hallwayC);

        // --- 3. 중앙계단 시리즈 ---
        StageData stairs1 = new StageData("중앙계단1", null,createAdvancedMap(col, row, false, false, false, true));
        stairs1.addDoor(460, 48, 100, 10, "복도_중앙"); 
        stairs1.addDoor(0, 760, 1008, 10, "중앙계단2");
        roomMap.put("중앙계단1", stairs1);
        
        StageData stairs2 = new StageData("중앙계단2", null,createAdvancedMap(col, row, false, false, true, false));
        stairs2.addDoor(0, 0, 1008, 10, "중앙계단1"); 
        stairs2.addDoor(460, 710, 100, 10, "거실"); 
        roomMap.put("중앙계단2", stairs2);

        // --- 4. 거실 및 나머지 구역 ---
        StageData livingRoom = new StageData("거실",null, createAdvancedMap(col, row, false, false, false, false));
        livingRoom.addDoor(460, 48, 100, 10, "중앙계단2");
        livingRoom.addDoor(460, 710, 100, 10, "탈출");
        livingRoom.keyName = "정문 열쇠";
        
        
        //책장
        livingRoom.setStructure(1, 1, 5);
        livingRoom.setStructure(2, 1, 5);
        livingRoom.setStructure(3, 1, 5);
        livingRoom.setStructure(4, 1, 5);
        livingRoom.setStructure(5, 1, 5);
        livingRoom.setStructure(6, 1, 5);
        livingRoom.setStructure(7, 1, 5);
        livingRoom.setStructure(8, 1, 5);
        
        livingRoom.setStructure(12, 1, 5);
        livingRoom.setStructure(13, 1, 5);
        livingRoom.setStructure(14, 1, 5);
        livingRoom.setStructure(15, 1, 5);
        livingRoom.setStructure(16, 1, 5);
        livingRoom.setStructure(17, 1, 5);
        livingRoom.setStructure(18, 1, 5);
        livingRoom.setStructure(19, 1, 5);
        
        //창문
        livingRoom.setStructure(6, 15, 7);
        livingRoom.setStructure(5, 15, 4);
        livingRoom.setStructure(15, 15, 4);
        livingRoom.setStructure(14, 15, 4);
        
        //유리파편
        livingRoom.setStructure(6, 14, 8);
        
        //화분
        livingRoom.setStructure(19, 14, 15);
        
        //열쇠인형
        livingRoom.setStructure(10, 8, 13);
        
        //그냥인형
        livingRoom.setStructure(13, 5, 20);
        livingRoom.setStructure(7, 5, 20);
        livingRoom.setStructure(7, 11, 20);
        livingRoom.setStructure(13, 11, 20);
        
        //겨미줄
        livingRoom.setStructure(1, 14, 9);
        
        roomMap.put("거실", livingRoom);

        StageData hallwayR = new StageData("복도_오른쪽", null,createAdvancedMap(col, row, true, false, false, false));
        hallwayR.addDoor(0, 0, 5, 800, "복도_중앙");
        hallwayR.addDoor(950, 330, 10, 100, "방3");
        hallwayR.addDoor(700, 710, 100, 10, "방2");
        roomMap.put("복도_오른쪽", hallwayR);

        // 일반 방들
        StageData room1 = new StageData("방1",null, createBaseMap(col, row));
        room1.addDoor(200, 48, 100, 10, "복도_왼쪽");
        roomMap.put("방1", room1);
        
        StageData room2 = new StageData("방2",null, createBaseMap(col, row));
        room2.addDoor(700, 48, 100, 10, "복도_오른쪽");
        roomMap.put("방2", room2);
        
        StageData room3 = new StageData("방3",null, createBaseMap(col, row));
        room3.addDoor(48, 330, 10, 100, "복도_오른쪽");
        room3.addDoor(460, 48, 100, 10, "방4");
        roomMap.put("방3", room3);

        StageData room4 = new StageData("방4",null,createBaseMap(col, row));
        room4.addDoor(460, 710, 100, 10, "방3");
        roomMap.put("방4", room4);
        
        StageData EsCapeR = new StageData("탈출", "정문 열쇠",createAdvancedMap(col, row, true, true, false,true));
        EsCapeR.addDoor(460, 48, 100, 10,"거실");
        //창문
        EsCapeR.setStructure(5, 0, 4);
        EsCapeR.setStructure(6, 0, 7);
        EsCapeR.setStructure(14, 0, 4);
        EsCapeR.setStructure(15, 0, 4);
        
        //풀숲
        EsCapeR.setStructure(0, 1, 11);
        EsCapeR.setStructure(0, 2, 11);
        EsCapeR.setStructure(0, 3, 11);
        EsCapeR.setStructure(0, 4, 11);
        EsCapeR.setStructure(0, 5, 11);
        EsCapeR.setStructure(0, 6, 11);
        EsCapeR.setStructure(0, 7, 11);
        EsCapeR.setStructure(0, 8, 11);
        EsCapeR.setStructure(0, 9, 11);
        EsCapeR.setStructure(0, 10, 11);
        EsCapeR.setStructure(0, 11, 11);
        EsCapeR.setStructure(0, 12, 11);
        EsCapeR.setStructure(0, 13, 11);
        EsCapeR.setStructure(0, 14, 11);
        EsCapeR.setStructure(0, 15, 11);
        
        EsCapeR.setStructure(20, 1, 11);
        EsCapeR.setStructure(20, 2, 11);
        EsCapeR.setStructure(20, 3, 11);
        EsCapeR.setStructure(20, 4, 11);
        EsCapeR.setStructure(20, 5, 11);
        EsCapeR.setStructure(20, 6, 11);
        EsCapeR.setStructure(20, 7, 11);
        EsCapeR.setStructure(20, 8, 11);
        EsCapeR.setStructure(20, 9, 11);
        EsCapeR.setStructure(20, 10, 11);
        EsCapeR.setStructure(20, 11, 11);
        EsCapeR.setStructure(20, 12, 11);
        EsCapeR.setStructure(20, 13, 11);
        EsCapeR.setStructure(20, 14, 11);
        EsCapeR.setStructure(20, 15, 11);
        
        EsCapeR.setStructure(3, 1, 11);
        EsCapeR.setStructure(4, 1, 11);
        
        EsCapeR.setStructure(7, 1, 11);
        EsCapeR.setStructure(8, 1, 11);
        
        EsCapeR.setStructure(12, 1, 11);
        EsCapeR.setStructure(13, 1, 11);
        
        EsCapeR.setStructure(16, 1, 11);
        EsCapeR.setStructure(17, 1, 11);
        roomMap.put("탈출", EsCapeR);
    }

    private int[][] createAdvancedMap(int col, int row, boolean openL, boolean openR, boolean openUp, boolean openDown) {
        int[][] map = new int[row][col];
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                if (r == 0 && !openUp) map[r][c] = 1;           
                if (r == row - 1 && !openDown) map[r][c] = 1;   
                if (c == 0 && !openL) map[r][c] = 1;            
                if (c == col - 1 && !openR) map[r][c] = 1;      
            }
        }
        return map;
    }

    private int[][] createBaseMap(int col, int row) {
        return createAdvancedMap(col, row, false, false, false, false);
    }
}