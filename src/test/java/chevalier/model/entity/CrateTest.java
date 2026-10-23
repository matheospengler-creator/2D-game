package chevalier.model.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.Damageable;
import org.junit.jupiter.api.Test;

class CrateTest {

    @Test
    void oneHitBreaksIt() {
        Crate crate = new Crate(0, 0, null);
        assertFalse(crate.isDead());

        crate.takeDamage(1);

        assertTrue(crate.isDead());
    }

    @Test
    void keepsItsLoot() {
        Coin coin = new Coin(0, 0);
        assertSame(coin, new Crate(0, 0, coin).getLoot().orElseThrow());
    }

    @Test
    void canBeEmpty() {
        assertTrue(new Crate(0, 0, null).getLoot().isEmpty());
    }

    @Test
    void isDamageableWithoutBeingAnEntity() {
        Object crate = new Crate(0, 0, null);
        assertTrue(crate instanceof Damageable);
        assertFalse(crate instanceof Entity);
    }

    @Test
    void negativeDamageIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Crate(0, 0, null).takeDamage(-1));
    }
}
