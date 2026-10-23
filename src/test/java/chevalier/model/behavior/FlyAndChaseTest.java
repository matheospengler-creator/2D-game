package chevalier.model.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.GameObject;
import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

class FlyAndChaseTest {

    private static final double DT = 1.0 / 60.0;

    private static Level level(String... lines) throws InvalidLevelException {
        return new LevelLoader().parse(List.of(lines), 1);
    }

    private static Enemy firstEnemy(Level level) {
        for (GameObject object : level.getObjects()) {
            if (object instanceof Enemy enemy) {
                return enemy;
            }
        }
        throw new AssertionError("no enemy in level");
    }

    private static void simulateEnemy(Level level, Enemy enemy, int steps) {
        for (int i = 0; i < steps; i++) {
            enemy.update(level, DT);
        }
    }

    // Une grande salle, joueur à gauche, chauve-souris loin à droite.
    private static final String[] HALL = {
        "#...........................#",
        "#P.......................B..#",
        "#...........................#",
        "#...........................#",
        "#############################",
    };

    @Test
    void batDoesNotFall() throws InvalidLevelException {
        Level level = level(HALL);
        Enemy bat = firstEnemy(level);
        double startY = bat.getY();

        simulateEnemy(level, bat, 120);

        assertEquals(startY, bat.getY(), Fly.WAVE_AMPLITUDE + 1);
    }

    @Test
    void batPatrolsAroundItsStartingPoint() throws InvalidLevelException {
        Level level = level(HALL);
        Enemy bat = firstEnemy(level);
        double startX = bat.getX();

        double minX = startX;
        double maxX = startX;
        for (int i = 0; i < 600; i++) {
            simulateEnemy(level, bat, 1);
            minX = Math.min(minX, bat.getX());
            maxX = Math.max(maxX, bat.getX());
        }

        assertTrue(maxX - minX > 2 * Level.TILE_SIZE);        // elle se déplace vraiment
        assertTrue(startX - minX <= Fly.PATROL_RANGE + 5);
        assertTrue(maxX - startX <= Fly.PATROL_RANGE + 5);
    }

    @Test
    void batKeepsFlyingWhileThePlayerIsFar() throws InvalidLevelException {
        Level level = level(HALL);
        Enemy bat = firstEnemy(level);

        simulateEnemy(level, bat, 60);

        assertInstanceOf(Fly.class, bat.getBehavior());
    }

    @Test
    void batSwitchesToChaseWhenThePlayerIsClose() throws InvalidLevelException {
        Level level = level(
            "#..........#",
            "#P..B......#",                                     // 3 cases : à portée
            "#..........#",
            "############");
        Enemy bat = firstEnemy(level);
        assertInstanceOf(Fly.class, bat.getBehavior());

        simulateEnemy(level, bat, 1);

        assertInstanceOf(Chase.class, bat.getBehavior());
    }

    @Test
    void flyingChaserMovesStraightTowardsThePlayer() throws InvalidLevelException {
        Level level = level(
            "#..........#",
            "#P.........#",
            "#..........#",
            "#..........#",
            "#..........#",
            "############");
        Enemy bat = new Enemy("bat", 200, 100, 24, 16, 1, 1, new Chase(100));
        bat.setAffectedByGravity(false);
        Player player = level.getPlayer();
        double before = distance(bat, player);

        simulateEnemy(level, bat, 30);

        assertTrue(distance(bat, player) < before - 40);
        assertTrue(bat.getY() < 100);                           // est monté vers le joueur
    }

    @Test
    void groundChaserOnlyRunsTowardsThePlayer() throws InvalidLevelException {
        Level level = level(
            "#..........#",
            "#P.........#",
            "############");
        Enemy guard = new Enemy("guard", 250, 34, 24, 30, 2, 1, new Chase(80));
        simulateEnemy(level, guard, 10);

        assertEquals(-1, guard.getDirection());
        assertTrue(guard.getVx() < 0);
        assertTrue(guard.isOnGround());
    }

    @Test
    void speedsMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new Chase(0));
        assertThrows(IllegalArgumentException.class, () -> new Fly(0, 0, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Fly(0, 0, 10, -1));
    }

    private static double distance(Enemy enemy, Player player) {
        return Math.hypot(enemy.getX() - player.getX(), enemy.getY() - player.getY());
    }
}
