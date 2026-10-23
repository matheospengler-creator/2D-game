package chevalier.model.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LevelLoaderTest {

    private final LevelLoader loader = new LevelLoader();

    @Test
    void parsesEachCharacterAsATile() throws InvalidLevelException {
        Level level = loader.parse(List.of(
            "P.E",
            "=^.",
            "###"), 1);

        assertEquals(3, level.getColumns());
        assertEquals(3, level.getRows());
        assertEquals(TileType.EXIT, level.getTile(2, 0));
        assertEquals(TileType.PLATFORM, level.getTile(0, 1));
        assertEquals(TileType.SPIKES, level.getTile(1, 1));
        assertEquals(TileType.GROUND, level.getTile(2, 2));
    }

    @Test
    void shortLinesArePaddedWithEmptyTiles() throws InvalidLevelException {
        Level level = loader.parse(List.of("P##", "#"), 1);

        assertEquals(3, level.getColumns());
        assertEquals(TileType.EMPTY, level.getTile(2, 1));
    }

    @Test
    void unknownSymbolReportsItsPosition() {
        InvalidLevelException e = assertThrows(InvalidLevelException.class,
            () -> loader.parse(List.of("P..", ".?."), 1));

        assertTrue(e.getMessage().contains("line 2, column 2"));
    }

    @Test
    void playerSymbolCreatesThePlayerOnAnEmptyTile() throws InvalidLevelException {
        Level level = loader.parse(List.of(".P.", "###"), 1);

        assertEquals(TileType.EMPTY, level.getTile(1, 0));
        assertTrue(level.hasPlayer());
        assertEquals(1, level.getObjects().size());
        assertSame(level.getPlayer(), level.getObjects().get(0));
    }

    @Test
    void levelWithoutPlayerIsRejected() {
        InvalidLevelException e = assertThrows(InvalidLevelException.class,
            () -> loader.parse(List.of("...", "###"), 1));

        assertTrue(e.getMessage().contains("found 0"));
    }

    @Test
    void levelWithTwoPlayersIsRejected() {
        assertThrows(InvalidLevelException.class, () -> loader.parse(List.of("P.P", "###"), 1));
    }

    @Test
    void emptyContentIsRejected() {
        assertThrows(InvalidLevelException.class, () -> loader.parse(List.of(), 1));
    }

    @Test
    void loadsTheFirstLevelFromResources() throws InvalidLevelException {
        Level level = loader.load("/levels/level1.txt", 1);

        assertEquals(1, level.getNumber());
        assertTrue(level.getColumns() > 0);
    }

    @Test
    void missingFileIsReported() {
        assertThrows(InvalidLevelException.class, () -> loader.load("/levels/nope.txt", 1));
    }
}
