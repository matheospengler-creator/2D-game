package chevalier.model.entity;

/** Une pièce : ajoute une pièce au compteur du joueur. */
public class Coin extends Item {

    /** Taille d'une pièce, en pixels (elle est carrée). */
    public static final double SIZE = 16;

    /**
     * Crée une pièce.
     *
     * @param x abscisse du coin supérieur gauche
     * @param y ordonnée du coin supérieur gauche
     */
    public Coin(double x, double y) {
        super(x, y, SIZE, SIZE);
    }

    @Override
    public void onPickup(Player player) {
        player.addCoins(1);
    }

    @Override
    public String getSpriteId() {
        return "coin";
    }
}
