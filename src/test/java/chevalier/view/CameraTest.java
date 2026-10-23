package chevalier.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import chevalier.model.entity.Player;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

class CameraTest {

    private static final int TILE = Level.TILE_SIZE;

    /** Un niveau de 20 tuiles de large et 4 de haut, joueur en colonne {@code playerCol}. */
    private static Level wideLevel(int playerCol) throws InvalidLevelException {
        String empty = ".".repeat(20);
        String withPlayer = empty.substring(0, playerCol) + "P" + empty.substring(playerCol + 1);
        return new LevelLoader().parse(List.of(empty, empty, withPlayer, "#".repeat(20)), 1);
    }

    @Test
    void centersOnThePlayerInTheMiddleOfTheLevel() throws InvalidLevelException {
        Level level = wideLevel(10);
        Player player = level.getPlayer();
        Camera camera = new Camera(8 * TILE, 4 * TILE);

        camera.follow(player, level);

        double playerCenter = player.getX() + player.getWidth() / 2;
        assertEquals(playerCenter - 4 * TILE, camera.getX(), 1e-9);
    }

    @Test
    void staysAtTheLeftEdge() throws InvalidLevelException {
        Level level = wideLevel(1);
        Camera camera = new Camera(8 * TILE, 4 * TILE);

        camera.follow(level.getPlayer(), level);

        assertEquals(0, camera.getX());
    }

    @Test
    void staysAtTheRightEdge() throws InvalidLevelException {
        Level level = wideLevel(19);
        Camera camera = new Camera(8 * TILE, 4 * TILE);

        camera.follow(level.getPlayer(), level);

        assertEquals(level.getPixelWidth() - 8 * TILE, camera.getX(), 1e-9);
    }

    @Test
    void levelSmallerThanTheScreenIsNotScrolled() throws InvalidLevelException {
        Level level = wideLevel(10);
        Camera camera = new Camera(40 * TILE, 10 * TILE);

        camera.follow(level.getPlayer(), level);

        assertEquals(0, camera.getX());
        assertEquals(0, camera.getY());
    }
}
