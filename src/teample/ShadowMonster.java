package teample;

import java.awt.*;

public class ShadowMonster {
    GamePanel gp;
    public int x, y;
    public int speed = 2; 
    public String currentRoom = "거실"; // 괴물의 초기 내부 방 위치

    private boolean isAwakened = false; // [핵심] 괴물 각성 상태 플래그 (처음 거실 진입시 true로 변경)
    private String targetRoom = "거실";   
    private long roomTransitionTime = 0; 

    private int incomingX = 500;
    private int incomingY = 350;

    public ShadowMonster(GamePanel gp) {
        this.gp = gp;
        this.x = 500;
        this.y = 350;
    }

    public void setSpawnPointAtPlayer(int spawnX, int spawnY) {
        this.incomingX = spawnX;
        this.incomingY = spawnY;
    }

    public void update() {
        if (gp.currentRoom == null) return;

        String playerRoom = gp.currentRoom.roomName;

        // 1. 플레이어가 처음으로 "거실"에 들어왔을 때 괴물을 영구 각성시킴
        if (playerRoom.equals("거실") && !isAwakened) {
            isAwakened = true;
            this.currentRoom = "거실";
            this.targetRoom = "거실";
            this.x = 500; // 거실 대기 위치에서 시작
            this.y = 350;
        }

        // 2. 아직 거실에 한 번도 안 들어갔거나(미각성), 플레이어가 안전지대인 '침실'에 있을 때는 추격 차단
        if (!isAwakened || playerRoom.equals("침실")) {
            // 미각성 상태이거나 침실에 있을 때는 화면에 보이지 않고 거실에서 대기
            return; 
        }

        // 3. 각성 상태일 때: 플레이어가 방을 이동하면 1.5초 뒤 따라 들어가는 추격 프로세스 진행
        if (!this.targetRoom.equals(playerRoom)) {
            this.targetRoom = playerRoom;
            this.roomTransitionTime = System.currentTimeMillis();
        }

        // 방 이동 지연 처리 (1.5초 뒤 플레이어가 나갔던 문 좌표에서 괴물이 스폰됨)
        if (!this.currentRoom.equals(this.targetRoom)) {
            long elapsedTime = System.currentTimeMillis() - this.roomTransitionTime;
            if (elapsedTime >= 1500) {
                this.currentRoom = this.targetRoom;
                this.x = this.incomingX;
                this.y = this.incomingY;
            } else {
                return; // 1.5초 대기 중에는 연산 중단 (이전 방에 있거나 대기)
            }
        }

        // 4. 실시간 추격 연산 (괴물과 플레이어가 같은 방에 있을 때만)
        if (this.currentRoom.equals(playerRoom)) {
            if (x < gp.player.x) x += speed;
            else if (x > gp.player.x) x -= speed;

            if (y < gp.player.y) y += speed;
            else if (y > gp.player.y) y -= speed;
        }
    }

    public void draw(Graphics2D g2) {
        // 괴물이 각성된 상태이고, 플레이어와 같은 방에 있을 때만 붉은 사각형을 그려줌
        if (isAwakened && gp.currentRoom != null && gp.currentRoom.roomName.equals(this.currentRoom)) {
            g2.setColor(new Color(150, 0, 0, 200)); 
            g2.fillRect(x, y, gp.tileSize, gp.tileSize);
        }
    }

    public Rectangle getBounds() {
        // 미각성 상태이거나 침실일 때는 충돌하지 않도록 가짜 히트박스 반환
        if (!isAwakened || (gp.currentRoom != null && gp.currentRoom.roomName.equals("침실"))) {
            return new Rectangle(-9999, -9999, 0, 0);
        }
        return new Rectangle(x, y, gp.tileSize, gp.tileSize);
    }
}