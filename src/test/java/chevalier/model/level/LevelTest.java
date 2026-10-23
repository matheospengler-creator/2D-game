package chevalier.model.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.entity.Player;
import org.junit.jupiter.api.Test;

class LevelTest {

    private static final TileType E = TileType.EMPTY;
    private static final TileType G = TileType.GROUND;

    // . .
    // # #
    private final Level level = new Level(1, new TileType[][] {
        {E, E},
        {G, G},
    });

    @Test
    void dimensionsAreCountedInTiles() {
        assertEquals(2, level.getColumns());
        assertEquals(2, level.getRows());
    }

    @Test
    void pixelsAreConvertedToTiles() {
        assertEquals(0, Level.toTile(0));
        assertEquals(0, Level.toTile(31.9));
        assertEquals(1, Level.toTile(32));
        assertEquals(-1, Level.toTile(-0.5));
    }

    @Test
    void solidityIsReadAtPixelPositions() {
        assertFalse(level.isSolidAt(10, 10));   // case (0, 0) : vide
        assertTrue(level.isSolidAt(40, 40));    // case (1, 1) : sol
    }

    @Test
    void outsideTheGridIsEmpty() {
        assertEquals(TileType.EMPTY, level.getTile(-1, 0));
        assertEquals(TileType.EMPTY, level.getTile(0, 5));
        assertFalse(level.isSolidAt(10, 1000));
    }

    @Test
    void levelWithoutPlayerRefusesToGiveOne() {
        assertFalse(level.hasPlayer());
        assertThrows(IllegalStateException.class, level::getPlayer);
    }

    @Test
    void addedPlayerIsKnownAndListed() {
        Player player = new Player(0, 0);
        level.addObject(player);

        assertSame(player, level.getPlayer());
        assertEquals(1, level.getObjects().size());
    }

    @Test
    void secondPlayerIsRejected() {
        level.addObject(new Player(0, 0));
        assertThrows(IllegalArgumentException.class, () -> level.addObject(new Player(10, 0)));
    }

    @Test
    void objectListCannotBeModifiedFromOutside() {
        assertThrows(UnsupportedOperationException.class,
            () -> level.getObjects().add(new Player(0, 0)));
    }

    @Test
    void irregularGridIsRejected() {
        assertThrows(IllegalArgumentException.class,
            () -> new Level(1, new TileType[][] {{E, E}, {G}}));
    }

    @Test
    void emptyGridIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Level(1, new TileType[0][0]));
    }
}
