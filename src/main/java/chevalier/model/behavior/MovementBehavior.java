package chevalier.model.behavior;

import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.Level;

/**
 * Une façon de se déplacer pour un ennemi (patron Strategy).
 *
 * <p>L'ennemi délègue sa décision de mouvement à cet objet, appelé à chaque
 * pas avant la gravité et les collisions. Changer de stratégie change le
 * comportement de l'ennemi sans modifier la classe {@link Enemy}.</p>
 *
 * <p>Les stratégies ne gardent aucun état propre à un ennemi (la direction est
 * stockée dans l'ennemi) : une même instance peut donc être partagée.</p>
 */
public interface MovementBehavior {

    /**
     * Décide de la vitesse de l'ennemi pour ce pas.
     *
     * @param enemy  l'ennemi à déplacer
     * @param level  le niveau (pour repérer murs et vides)
     * @param player le joueur (pour les stratégies qui le poursuivent)
     * @param dt     la durée du pas, en secondes
     */
    void move(Enemy enemy, Level level, Player player, double dt);
}
