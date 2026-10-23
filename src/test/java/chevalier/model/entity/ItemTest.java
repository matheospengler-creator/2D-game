package chevalier.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void coinAddsOneCoin() {
        Player player = new Player(0, 0);

        new Coin(0, 0).onPickup(player);
        new Coin(0, 0).onPickup(player);

        assertEquals(2, player.getCoins());
    }

    @Test
    void heartHealsOneHeart() {
        Player player = new Player(0, 0);
        player.hurt(2);

        new Heart(0, 0).onPickup(player);

        assertEquals(Player.MAX_HEARTS - 1, player.getHp());
    }

    @Test
    void heartCannotExceedTheMaximum() {
        Player player = new Player(0, 0);

        new Heart(0, 0).onPickup(player);

        assertEquals(Player.MAX_HEARTS, player.getHp());
    }

    @Test
    void itemsAreUsedThroughTheirCommonType() {
        Player player = new Player(0, 0);
        player.hurt(1);
        List<Item> items = List.of(new Coin(0, 0), new Heart(0, 0));

        items.forEach(item -> item.onPickup(player));   // polymorphisme

        assertEquals(1, player.getCoins());
        assertEquals(Player.MAX_HEARTS, player.getHp());
    }

    @Test
    void negativeCoinsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Player(0, 0).addCoins(-1));
    }
}
