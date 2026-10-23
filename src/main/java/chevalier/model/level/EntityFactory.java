package chevalier.model.level;

import chevalier.model.GameObject;
import chevalier.model.behavior.Fly;
import chevalier.model.behavior.WalkAndTurn;
import chevalier.model.entity.Coin;
import chevalier.model.entity.Crate;
import chevalier.model.entity.Enemy;
import chevalier.model.entity.Heart;
import chevalier.model.entity.Player;
import java.util.Optional;

/**
 * Crée les objets du jeu à partir des caractères d'un fichier de niveau
 * (patron Simple Factory).
 *
 * <p>C'est le seul endroit du code qui associe un symbole à une classe
 * concrète : ajouter un nouveau type d'objet se fait ici, sans toucher au
 * chargeur de niveau.</p>
 */
public class EntityFactory {

    /** Largeur d'un slime, en pixels. */
    static final double SLIME_WIDTH = 28;

    /** Hauteur d'un slime, en pixels. */
    static final double SLIME_HEIGHT = 20;

    /** Largeur d'une chauve-souris, en pixels. */
    static final double BAT_WIDTH = 24;

    /** Hauteur d'une chauve-souris, en pixels. */
    static final double BAT_HEIGHT = 16;

    /** Largeur d'un garde, en pixels. */
    static final double GUARD_WIDTH = 24;

    /** Hauteur d'un garde, en pixels. */
    static final double GUARD_HEIGHT = 30;

    /**
     * Crée l'objet correspondant à un symbole, posé en bas et au centre de sa case.
     *
     * @param symbol      le caractère lu dans le fichier de niveau
     * @param x           abscisse du coin supérieur gauche de la case, en pixels
     * @param y           ordonnée du coin supérieur gauche de la case, en pixels
     * @param levelNumber le numéro du niveau (pour calibrer la difficulté)
     * @return l'objet créé, ou {@link Optional#empty()} si le symbole n'est pas un objet
     */
    public Optional<GameObject> create(char symbol, double x, double y, int levelNumber) {
        return switch (symbol) {
            case 'P' -> Optional.of(new Player(
                centerX(x, Player.WIDTH), bottomY(y, Player.HEIGHT)));
            case 'S' -> Optional.of(makeSlime(x, y, levelNumber));
            case 'B' -> Optional.of(makeBat(x, y, levelNumber));
            case 'G' -> Optional.of(makeGuard(x, y, levelNumber));
            case 'C' -> Optional.of(makeCoin(x, y));
            case 'H' -> Optional.of(new Heart(centerX(x, Heart.WIDTH), centerY(y, Heart.HEIGHT)));
            case 'X' -> Optional.of(new Crate(x, y, makeCoin(x, y)));
            default -> Optional.empty();
        };
    }

    /**
     * Le slime : marche et fait demi-tour, 1 PV. Il accélère un peu à chaque
     * niveau (difficulté croissante).
     */
    private static Enemy makeSlime(double x, double y, int levelNumber) {
        double speed = 60 + 10 * (levelNumber - 1);
        return new Enemy("slime",
            centerX(x, SLIME_WIDTH), bottomY(y, SLIME_HEIGHT), SLIME_WIDTH, SLIME_HEIGHT,
            1, 1, new WalkAndTurn(speed));
    }

    /**
     * La chauve-souris : vole sans gravité autour de sa case, puis fonce sur le
     * joueur ; 1 PV. Plus rapide dans les niveaux suivants.
     */
    private static Enemy makeBat(double x, double y, int levelNumber) {
        double batX = centerX(x, BAT_WIDTH);
        double batY = centerY(y, BAT_HEIGHT);
        double speed = 70 + 10 * (levelNumber - 1);
        double chaseSpeed = 110 + 15 * (levelNumber - 1);
        Enemy bat = new Enemy("bat", batX, batY, BAT_WIDTH, BAT_HEIGHT,
            1, 1, new Fly(batX, batY, speed, chaseSpeed));
        bat.setAffectedByGravity(false);
        return bat;
    }

    /** Le garde : marche lentement et fait demi-tour comme le slime, mais a 2 PV. */
    private static Enemy makeGuard(double x, double y, int levelNumber) {
        double speed = 45 + 5 * (levelNumber - 1);
        return new Enemy("guard",
            centerX(x, GUARD_WIDTH), bottomY(y, GUARD_HEIGHT), GUARD_WIDTH, GUARD_HEIGHT,
            2, 1, new WalkAndTurn(speed));
    }

    /** Une pièce, au centre de sa case (elle flotte). */
    private static Coin makeCoin(double x, double y) {
        return new Coin(centerX(x, Coin.SIZE), centerY(y, Coin.SIZE));
    }

    private static double centerY(double tileY, double height) {
        return tileY + (Level.TILE_SIZE - height) / 2;
    }

    private static double centerX(double tileX, double width) {
        return tileX + (Level.TILE_SIZE - width) / 2;
    }

    private static double bottomY(double tileY, double height) {
        return tileY + Level.TILE_SIZE - height;
    }
}
