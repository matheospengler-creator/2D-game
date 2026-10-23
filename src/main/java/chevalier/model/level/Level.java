package chevalier.model.level;

import chevalier.model.AABB;
import chevalier.model.GameObject;
import chevalier.model.entity.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Un niveau du jeu : une grille de tuiles et les objets qui s'y trouvent.
 *
 * <p>Les coordonnées en <b>pixels</b> (position des entités) se convertissent en
 * coordonnées de <b>case</b> (colonne, ligne) en divisant par {@link #TILE_SIZE}.
 * Tout ce qui est en dehors de la grille est considéré comme du vide : on peut
 * donc tomber hors du niveau par un trou.</p>
 */
public class Level {

    /** Taille d'une tuile, en pixels (les tuiles sont carrées). */
    public static final int TILE_SIZE = 32;

    private final int number;
    private final TileType[][] tiles;
    private final List<GameObject> objects = new ArrayList<>();
    private Player player;

    /**
     * Crée un niveau à partir de sa grille de tuiles.
     *
     * @param number le numéro du niveau (1 pour le premier)
     * @param tiles  la grille, indexée {@code tiles[ligne][colonne]} ; toutes les
     *               lignes doivent avoir la même longueur
     * @throws IllegalArgumentException si la grille est vide ou irrégulière
     */
    public Level(int number, TileType[][] tiles) {
        if (tiles.length == 0 || tiles[0].length == 0) {
            throw new IllegalArgumentException("A level needs at least one tile");
        }
        for (TileType[] row : tiles) {
            if (row.length != tiles[0].length) {
                throw new IllegalArgumentException("All rows must have the same length");
            }
        }
        this.number = number;
        this.tiles = tiles;
    }

    /**
     * Retourne le numéro de ce niveau.
     *
     * @return le numéro du niveau
     */
    public int getNumber() {
        return number;
    }

    /**
     * Retourne la largeur du niveau, en nombre de tuiles.
     *
     * @return le nombre de colonnes
     */
    public int getColumns() {
        return tiles[0].length;
    }

    /**
     * Retourne la hauteur du niveau, en nombre de tuiles.
     *
     * @return le nombre de lignes
     */
    public int getRows() {
        return tiles.length;
    }

    /**
     * Retourne la largeur du niveau, en pixels.
     *
     * @return la largeur en pixels
     */
    public double getPixelWidth() {
        return getColumns() * TILE_SIZE;
    }

    /**
     * Retourne la hauteur du niveau, en pixels.
     *
     * @return la hauteur en pixels
     */
    public double getPixelHeight() {
        return getRows() * TILE_SIZE;
    }

    /**
     * Ajoute un objet au niveau.
     *
     * @param object l'objet à ajouter
     * @throws IllegalArgumentException si l'objet est un second joueur
     */
    public void addObject(GameObject object) {
        if (object instanceof Player p) {
            if (player != null) {
                throw new IllegalArgumentException("A level can only have one player");
            }
            player = p;
        }
        objects.add(object);
    }

    /** Retire du niveau les objets marqués comme à retirer (ennemis morts…). */
    public void removeMarkedObjects() {
        objects.removeIf(object -> object.isRemoved() && object != player);
    }

    /**
     * Retourne les objets du niveau (lecture seule), joueur compris.
     *
     * @return la liste des objets
     */
    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    /**
     * Indique si le niveau contient un joueur.
     *
     * @return {@code true} si un joueur a été ajouté
     */
    public boolean hasPlayer() {
        return player != null;
    }

    /**
     * Retourne le joueur du niveau.
     *
     * @return le joueur
     * @throws IllegalStateException si le niveau n'a pas de joueur
     */
    public Player getPlayer() {
        if (player == null) {
            throw new IllegalStateException("This level has no player");
        }
        return player;
    }

    /**
     * Retourne la tuile située à une case donnée.
     *
     * @param col la colonne (0 = tout à gauche)
     * @param row la ligne (0 = tout en haut)
     * @return la tuile, ou {@link TileType#EMPTY} si la case est hors du niveau
     */
    public TileType getTile(int col, int row) {
        if (row < 0 || row >= getRows() || col < 0 || col >= getColumns()) {
            return TileType.EMPTY;
        }
        return tiles[row][col];
    }

    /**
     * Retourne la tuile qui contient un point donné en pixels.
     *
     * @param x abscisse en pixels
     * @param y ordonnée en pixels
     * @return la tuile qui contient ce point
     */
    public TileType getTileAt(double x, double y) {
        return getTile(toTile(x), toTile(y));
    }

    /**
     * Indique si le point donné en pixels se trouve dans une tuile solide.
     *
     * @param x abscisse en pixels
     * @param y ordonnée en pixels
     * @return {@code true} si le point est dans une tuile solide
     */
    public boolean isSolidAt(double x, double y) {
        return getTileAt(x, y).isSolid();
    }

    /**
     * Indique si une boîte chevauche au moins une tuile d'un type donné.
     *
     * @param box  la boîte à tester (en pixels)
     * @param type le type de tuile recherché
     * @return {@code true} si la boîte touche une tuile de ce type
     */
    public boolean touchesTile(AABB box, TileType type) {
        double epsilon = 0.001;   // une boîte posée contre une tuile ne la « touche » pas
        int left = toTile(box.x());
        int right = toTile(box.x() + box.width() - epsilon);
        int top = toTile(box.y());
        int bottom = toTile(box.y() + box.height() - epsilon);
        for (int row = top; row <= bottom; row++) {
            for (int col = left; col <= right; col++) {
                if (getTile(col, row) == type) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Convertit une coordonnée en pixels en indice de case.
     *
     * <p>{@code Math.floor} (et non un simple cast) garantit qu'une coordonnée
     * négative tombe bien dans la case -1, et non dans la case 0.</p>
     *
     * @param pixels la coordonnée en pixels
     * @return l'indice de la case qui contient cette coordonnée
     */
    public static int toTile(double pixels) {
        return (int) Math.floor(pixels / TILE_SIZE);
    }
}
