package chevalier.model.level;

/**
 * Les différents types de cases (tuiles) qui composent le décor d'un niveau.
 *
 * <p>Chaque type est associé au caractère qui le représente dans les fichiers
 * de niveau, et sait s'il bloque le déplacement (solide) ou non.</p>
 */
public enum TileType {

    /** Air : on le traverse librement. */
    EMPTY('.', false),

    /** Sol ou mur : bloque le déplacement dans toutes les directions. */
    GROUND('#', true),

    /** Plateforme en suspension : bloque comme le sol. */
    PLATFORM('=', true),

    /** Piques : traversables, mais blessent le joueur au contact. */
    SPIKES('^', false),

    /** Porte de sortie : l'atteindre termine le niveau. */
    EXIT('E', false);

    private final char symbol;
    private final boolean solid;

    TileType(char symbol, boolean solid) {
        this.symbol = symbol;
        this.solid = solid;
    }

    /**
     * Retourne le caractère qui représente ce type dans les fichiers de niveau.
     *
     * @return le symbole de la tuile
     */
    public char getSymbol() {
        return symbol;
    }

    /**
     * Indique si cette tuile bloque le déplacement des entités.
     *
     * @return {@code true} si la tuile est solide
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * Indique si un caractère de fichier de niveau représente une tuile.
     *
     * @param c le caractère lu dans le fichier
     * @return {@code true} si un type de tuile correspond à ce caractère
     */
    public static boolean isSymbol(char c) {
        for (TileType type : values()) {
            if (type.symbol == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrouve le type de tuile correspondant à un caractère de fichier de niveau.
     *
     * @param c le caractère lu dans le fichier
     * @return le type de tuile correspondant
     * @throws IllegalArgumentException si aucun type ne correspond à ce caractère
     */
    public static TileType fromChar(char c) {
        for (TileType type : values()) {
            if (type.symbol == c) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown tile symbol: '" + c + "'");
    }
}
