package chevalier.model.entity;

import chevalier.model.Damageable;
import chevalier.model.GameObject;
import chevalier.model.level.Level;
import java.util.Optional;

/**
 * Une caisse immobile qu'un coup d'épée brise, libérant éventuellement un objet.
 *
 * <p>La caisse n'est pas une {@link Entity} (elle ne bouge pas, ne tombe pas),
 * mais elle est {@link Damageable} : l'épée la frappe exactement comme un
 * ennemi, sans code spécifique. C'est ce qui justifie que {@code Damageable}
 * soit une interface plutôt qu'une capacité d'{@code Entity}.</p>
 */
public class Crate extends GameObject implements Damageable {

    /** Taille d'une caisse, en pixels (une tuile). */
    public static final double SIZE = 32;

    private final Item loot;
    private int hp = 1;

    /**
     * Crée une caisse.
     *
     * @param x    abscisse du coin supérieur gauche
     * @param y    ordonnée du coin supérieur gauche
     * @param loot l'objet libéré quand la caisse est brisée, ou {@code null} si elle est vide
     */
    public Crate(double x, double y, Item loot) {
        super(x, y, SIZE, SIZE);
        this.loot = loot;
    }

    @Override
    public void takeDamage(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("damage must be >= 0");
        }
        hp = Math.max(0, hp - amount);
    }

    @Override
    public boolean isDead() {
        return hp == 0;
    }

    /**
     * Retourne l'objet que contient la caisse.
     *
     * @return l'objet libéré à la destruction, s'il y en a un
     */
    public Optional<Item> getLoot() {
        return Optional.ofNullable(loot);
    }

    /**
     * Une caisse est immobile : rien à mettre à jour.
     *
     * @param level le niveau
     * @param dt    la durée du pas, en secondes
     */
    @Override
    public void update(Level level, double dt) {
        // immobile
    }

    @Override
    public String getSpriteId() {
        return "crate";
    }
}
