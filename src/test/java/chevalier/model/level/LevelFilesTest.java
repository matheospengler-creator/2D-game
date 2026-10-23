package chevalier.model.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Vérifie que chaque fichier de niveau du jeu se charge et est jouable :
 * un joueur (garanti par le chargeur) et au moins une porte de sortie.
 */
class LevelFilesTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void levelFileIsValidAndHasAnExit(int number) throws InvalidLevelException {
        Level level = new LevelLoader().load("/levels/level" + number + ".txt", number);

        assertEquals(number, level.getNumber());
        assertTrue(hasExit(level), "level " + number + " has no exit");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void levelsHaveTheScreenHeight(int number) throws InvalidLevelException {
        Level level = new LevelLoader().load("/levels/level" + number + ".txt", number);

        assertEquals(17, level.getRows());
    }

    private static boolean hasExit(Level level) {
        for (int row = 0; row < level.getRows(); row++) {
            for (int col = 0; col < level.getColumns(); col++) {
                if (level.getTile(col, row) == TileType.EXIT) {
                    return true;
                }
            }
        }
        return false;
    }
}
