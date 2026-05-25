package teample;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public boolean checkTile(PlayerMove player) {
        if (gp.gameState == gp.introState || gp.gameState == gp.getUpState || 
            gp.gameState == gp.diaryEventState || gp.gameState == gp.windowEventState) {
            return false;
        }

        int leftX = player.x;
        int rightX = player.x + gp.tileSize;
        int topY = player.y;
        int bottomY = player.y + gp.tileSize;

        int leftCol = leftX / gp.tileSize;
        int rightCol = (rightX - 1) / gp.tileSize;
        int topRow = topY / gp.tileSize;
        int bottomRow = (bottomY - 1) / gp.tileSize;

        try {
            if (leftCol < 0 || rightCol >= gp.maxScreenCol || topRow < 0 || bottomRow >= gp.maxScreenRow) return true;
            
            if (gp.currentRoom.hasInteractObject && 
                player.getBounds().intersects(gp.currentRoom.interactObjectPos)) {
                return false;
            }

            if (gp.currentRoom.mapLayout[topRow][leftCol] != 0 ||
                gp.currentRoom.mapLayout[topRow][rightCol] != 0 ||
                gp.currentRoom.mapLayout[bottomRow][leftCol] != 0 ||
                gp.currentRoom.mapLayout[bottomRow][rightCol] != 0) {
                return true;
            }
        } catch (Exception e) { return true; }

        return false;
    }
}