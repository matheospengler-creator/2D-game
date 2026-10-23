package chevalier.model.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TileTypeTest {

    @Test
    void everySymbolMapsBackToItsType() {
        for (TileType type : TileType.values()) {
            assertEquals(type, TileType.fromChar(type.getSymbol()));
        }
    }

    @Test
    void groundAndPlatformAreSolid() {
        assertTrue(TileType.GROUND.isSolid());
        assertTrue(TileType.PLATFORM.isSolid());
    }

    @Test
    void emptySpikesAndExitAreNotSolid() {
        assertFalse(TileType.EMPTY.isSolid());
        assertFalse(TileType.SPIKES.isSolid());
        assertFalse(TileType.EXIT.isSolid());
    }

    @Test
    void unknownSymbolIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> TileType.fromChar('?'));
    }
}
