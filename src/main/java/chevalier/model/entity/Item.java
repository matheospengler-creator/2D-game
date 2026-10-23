package chevalier.model.entity;

import chevalier.model.GameObject;
import chevalier.model.level.Level;

/**
 * Un objet que le joueur ramasse en le touchant (pièce, cœur…).
 *
 * <p>Chaque sous-classe définit son effet dans {@link #onPickup(Player)} : le
 * jeu ramasse un {@code Item} sans connaître son type concret. Un objet ne
 * subit ni gravité ni collisions : il reste là où il a été placé.</p>
 */
public abstract class Item extends GameObject {

    /**
     * Crée un objet à ramasser.
     *
     * @param x      abscisse du coin supérieur gauche
     * @param y      ordonnée du coin supérieur gauche
     * @param width  largeur
     * @param height hauteur
     */
    protected Item(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    /**
     * Applique l'effet de l'objet au joueur qui le ramasse.
     *
     * @param player le joueur qui ramasse l'objet
     */
    public abstract void onPickup(Player player);

    /**
     * Un objet à ramasser est immobile : rien à mettre à jour.
     *
     * @param level le niveau
     * @param dt    la durée du pas, en secondes
     */
    @Override
    public void update(Level level, double dt) {
        // immobile
    }
}
