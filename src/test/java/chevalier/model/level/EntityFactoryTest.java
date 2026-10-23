package chevalier.model.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.GameObject;
import chevalier.model.behavior.Fly;
import chevalier.model.behavior.WalkAndTurn;
import chevalier.model.entity.Coin;
import chevalier.model.entity.Crate;
import chevalier.model.entity.Enemy;
import chevalier.model.entity.Heart;
import chevalier.model.entity.Player;
import org.junit.jupiter.api.Test;

class EntityFactoryTest {

    private final EntityFactory factory = new EntityFactory();

    @Test
    void playerIsCreatedStandingAtTheBottomCenterOfItsTile() {
        GameObject object = factory.create('P', 64, 32, 1).orElseThrow();

        assertInstanceOf(Player.class, object);
        assertEquals(64 + (Level.TILE_SIZE - Player.WIDTH) / 2, object.getX());
        assertEquals(32 + Level.TILE_SIZE, object.getY() + object.getHeight());
    }

    @Test
    void slimeIsAWalkingEnemyWithOneHitPoint() {
        Enemy slime = (Enemy) factory.create('S', 0, 0, 1).orElseThrow();

        assertEquals("slime", slime.getSpriteId());
        assertEquals(1, slime.getMaxHp());
        assertInstanceOf(WalkAndTurn.class, slime.getBehavior());
        assertEquals(Level.TILE_SIZE, slime.getY() + slime.getHeight());
    }

    @Test
    void slimesGetFasterInLaterLevels() {
        Enemy early = (Enemy) factory.create('S', 0, 0, 1).orElseThrow();
        Enemy late = (Enemy) factory.create('S', 0, 0, 5).orElseThrow();

        double earlySpeed = ((WalkAndTurn) early.getBehavior()).getSpeed();
        double lateSpeed = ((WalkAndTurn) late.getBehavior()).getSpeed();
        assertTrue(lateSpeed > earlySpeed);
    }

    @Test
    void batFliesAndStartsByPatrolling() {
        Enemy bat = (Enemy) factory.create('B', 0, 0, 1).orElseThrow();

        assertEquals("bat", bat.getSpriteId());
        assertFalse(bat.isAffectedByGravity());
        assertInstanceOf(Fly.class, bat.getBehavior());
    }

    @Test
    void guardWalksAndNeedsTwoHits() {
        Enemy guard = (Enemy) factory.create('G', 0, 0, 1).orElseThrow();

        assertEquals("guard", guard.getSpriteId());
        assertEquals(2, guard.getMaxHp());
        assertTrue(guard.isAffectedByGravity());
        assertInstanceOf(WalkAndTurn.class, guard.getBehavior());
    }

    @Test
    void coinFloatsInTheMiddleOfItsTile() {
        GameObject coin = factory.create('C', 0, 0, 1).orElseThrow();

        assertInstanceOf(Coin.class, coin);
        assertEquals(Level.TILE_SIZE / 2.0, coin.getY() + coin.getHeight() / 2);
    }

    @Test
    void heartSymbolCreatesAHeart() {
        assertInstanceOf(Heart.class, factory.create('H', 0, 0, 1).orElseThrow());
    }

    @Test
    void crateContainsACoin() {
        Crate crate = (Crate) factory.create('X', 0, 0, 1).orElseThrow();

        assertInstanceOf(Coin.class, crate.getLoot().orElseThrow());
    }

    @Test
    void unknownSymbolCreatesNothing() {
        assertTrue(factory.create('?', 0, 0, 1).isEmpty());
    }
}
