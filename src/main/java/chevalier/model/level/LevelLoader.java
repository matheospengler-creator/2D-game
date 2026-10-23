package chevalier.model.level;

import chevalier.model.GameObject;
import chevalier.model.entity.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Lit les fichiers de niveau et les transforme en objets {@link Level}.
 *
 * <p>Format : un fichier texte où chaque caractère est une case. Un caractère
 * est soit une tuile (voir {@link TileType}), soit un objet créé par
 * l'{@link EntityFactory} (ex. {@code P} pour le joueur) ; la case d'un objet
 * est vide. Les lignes plus courtes que la plus longue sont complétées avec
 * du vide. Un niveau doit contenir exactement un joueur.</p>
 */
public class LevelLoader {

    private final EntityFactory factory;

    /** Crée un chargeur qui utilise la fabrique par défaut. */
    public LevelLoader() {
        this(new EntityFactory());
    }

    /**
     * Crée un chargeur qui utilise la fabrique donnée.
     *
     * @param factory la fabrique qui crée les objets du niveau
     */
    public LevelLoader(EntityFactory factory) {
        this.factory = factory;
    }

    /**
     * Charge un niveau depuis les ressources du jeu (dossier {@code src/main/resources}).
     *
     * @param resourcePath le chemin de la ressource, ex. {@code "/levels/level1.txt"}
     * @param number       le numéro du niveau
     * @return le niveau chargé
     * @throws InvalidLevelException si le fichier est introuvable, illisible ou mal formé
     */
    public Level load(String resourcePath, int number) throws InvalidLevelException {
        InputStream stream = LevelLoader.class.getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new InvalidLevelException("Level file not found: " + resourcePath);
        }
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return parse(reader.lines().toList(), number);
        } catch (IOException e) {
            throw new InvalidLevelException("Cannot read level file: " + resourcePath, e);
        }
    }

    /**
     * Construit un niveau à partir des lignes d'un fichier de niveau.
     *
     * @param lines  les lignes du fichier, de haut en bas
     * @param number le numéro du niveau
     * @return le niveau correspondant
     * @throws InvalidLevelException si le contenu est vide, contient un symbole
     *                               inconnu, ou n'a pas exactement un joueur
     */
    public Level parse(List<String> lines, int number) throws InvalidLevelException {
        int columns = lines.stream().mapToInt(String::length).max().orElse(0);
        if (columns == 0) {
            throw new InvalidLevelException("Level " + number + " is empty");
        }

        TileType[][] tiles = new TileType[lines.size()][columns];
        List<GameObject> objects = new ArrayList<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < columns; col++) {
                char symbol = col < line.length() ? line.charAt(col) : TileType.EMPTY.getSymbol();
                tiles[row][col] = readCell(symbol, row, col, number, objects);
            }
        }

        long players = objects.stream().filter(o -> o instanceof Player).count();
        if (players != 1) {
            throw new InvalidLevelException(
                "Level " + number + " must contain exactly one player 'P' (found " + players + ")");
        }

        Level level = new Level(number, tiles);
        objects.forEach(level::addObject);
        return level;
    }

    /**
     * Lit une case : soit une tuile, soit un objet (la case est alors vide).
     */
    private TileType readCell(char symbol, int row, int col, int number, List<GameObject> objects)
            throws InvalidLevelException {
        if (TileType.isSymbol(symbol)) {
            return TileType.fromChar(symbol);
        }
        Optional<GameObject> object =
            factory.create(symbol, col * Level.TILE_SIZE, row * Level.TILE_SIZE, number);
        if (object.isEmpty()) {
            throw new InvalidLevelException(
                "Unknown symbol '" + symbol + "' at line " + (row + 1) + ", column " + (col + 1));
        }
        objects.add(object.get());
        return TileType.EMPTY;
    }
}
