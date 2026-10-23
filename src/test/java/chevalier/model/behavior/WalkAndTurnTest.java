package chevalier.model.behavior;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.GameObject;
import chevalier.model.entity.Enemy;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

class WalkAndTurnTest {

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

    private static void simulate(Level level, int steps) {
        for (int i = 0; i < steps; i++) {
            for (GameObject object : List.copyOf(level.getObjects())) {
                object.update(level, DT);
            }
        }
    }

    @Test
    void walksInItsDirection() throws InvalidLevelException {
        Level level = level(
            "P........",
            "....S....",
            "#########");
        Enemy slime = firstEnemy(level);
        simulate(level, 10);
        double startX = slime.getX();

        simulate(level, 10);

        assertEquals(-1, slime.getDirection());
        assertTrue(slime.getX() < startX);
    }

    @Test
    void turnsAroundAtAWall() throws InvalidLevelException {
        Level level = level(
            "P....",
            "#.S..",
            "#####");
        Enemy slime = firstEnemy(level);

        simulate(level, 120);

        assertEquals(1, slime.getDirection());   // a rebondi sur le mur de gauche
    }

    @Test
    void neverWalksOffALedge() throws InvalidLevelException {
        Level level = level(
            "P.......",
            "...S....",
            "..####..");                          // plateforme de 4 cases au milieu du vide
        Enemy slime = firstEnemy(level);

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        for (int i = 0; i < 600; i++) {
            simulate(level, 1);
            minX = Math.min(minX, slime.getX());
            maxX = Math.max(maxX, slime.getX() + slime.getWidth());
        }

        assertFalse(slime.isRemoved());
        assertTrue(minX >= 2 * Level.TILE_SIZE - 1);
        assertTrue(maxX <= 6 * Level.TILE_SIZE + 1);
    }

    @Test
    void speedMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new WalkAndTurn(0));
    }
}
