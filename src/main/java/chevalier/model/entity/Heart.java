package chevalier.model.entity;

/** Un cœur : rend un cœur au joueur (sans dépasser son maximum). */
public class Heart extends Item {

    /** Largeur d'un cœur, en pixels. */
    public static final double WIDTH = 18;

    /** Hauteur d'un cœur, en pixels. */
    public static final double HEIGHT = 16;

    /**
     * Crée un cœur.
     *
     * @param x abscisse du coin supérieur gauche
     * @param y ordonnée du coin supérieur gauche
     */
    public Heart(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    @Override
    public void onPickup(Player player) {
        player.heal(1);
    }

    @Override
    public String getSpriteId() {
        return "heart";
    }
}
