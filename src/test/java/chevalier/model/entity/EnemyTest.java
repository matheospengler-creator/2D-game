package chevalier.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.behavior.MovementBehavior;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

class EnemyTest {

    private static final double DT = 1.0 / 60.0;

    /** Stratégie de test : ne bouge pas, compte ses appels. */
    private static class StandStill implements MovementBehavior {
        int calls;

        @Override
        public void move(Enemy enemy, Level level, Player player, double dt) {
            calls++;
            enemy.setVelocity(0, enemy.getVy());
        }
    }

    private static Level level(String... lines) throws InvalidLevelException {
        return new LevelLoader().parse(List.of(lines), 1);
    }

    private static Enemy enemyAt(double x, double y, MovementBehavior behavior) {
        return new Enemy("test", x, y, 20, 20, 2, 1, behavior);
    }

    @Test
    void delegatesItsMovementToTheStrategy() throws InvalidLevelException {
        Level level = level("P...", "....", "####");
        StandStill strategy = new StandStill();
        Enemy enemy = enemyAt(64, 0, strategy);
        level.addObject(enemy);

        enemy.update(level, DT);
        enemy.update(level, DT);

        assertEquals(2, strategy.calls);
    }

    @Test
    void strategyCanBeSwappedDuringTheGame() throws InvalidLevelException {
        Level level = level("P...", "....", "####");
        StandStill first = new StandStill();
        StandStill second = new StandStill();
        Enemy enemy = enemyAt(64, 0, first);

        enemy.update(level, DT);
        enemy.setBehavior(second);
        enemy.update(level, DT);

        assertSame(second, enemy.getBehavior());
        assertEquals(1, first.calls);
        assertEquals(1, second.calls);
    }

    @Test
    void isRemovedWhenDead() throws InvalidLevelException {
        Level level = level("P...", "....", "####");
        Enemy enemy = enemyAt(64, 0, new StandStill());

        enemy.takeDamage(2);
        enemy.update(level, DT);

        assertTrue(enemy.isRemoved());
    }

    @Test
    void isRemovedWhenFallingOutOfTheLevel() throws InvalidLevelException {
        Level level = level("P...", "....");
        Enemy enemy = enemyAt(64, 0, new StandStill());

        for (int i = 0; i < 60 && !enemy.isRemoved(); i++) {
            enemy.update(level, DT);
        }

        assertTrue(enemy.isRemoved());
    }

    @Test
    void flashesBrieflyWhenHit() throws InvalidLevelException {
        Level level = level("P...", "....", "####");
        Enemy enemy = enemyAt(64, 0, new StandStill());

        enemy.takeDamage(1);
        assertTrue(enemy.isHurt());

        for (int i = 0; i < 20; i++) {         // 0,33 s > durée du clignotement
            enemy.update(level, DT);
        }
        assertFalse(enemy.isHurt());
    }

    @Test
    void turnAroundFlipsTheDirection() {
        Enemy enemy = enemyAt(0, 0, new StandStill());
        assertEquals(-1, enemy.getDirection());
        enemy.turnAround();
        assertEquals(1, enemy.getDirection());
    }

    @Test
    void aliveEnemyStaysInTheLevel() throws InvalidLevelException {
        Level level = level("P...", "....", "####");
        Enemy enemy = enemyAt(64, 0, new StandStill());

        for (int i = 0; i < 60; i++) {
            enemy.update(level, DT);
        }

        assertFalse(enemy.isRemoved());
    }
}
