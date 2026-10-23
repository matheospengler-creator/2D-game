package chevalier.model.behavior;

import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.Level;

/**
 * Marche droit devant et fait demi-tour face à un mur ou au bord d'un vide.
 * Un ennemi qui suit cette stratégie ne tombe jamais dans un trou.
 */
public class WalkAndTurn implements MovementBehavior {

    private final double speed;

    /**
     * Crée la stratégie avec une vitesse de marche.
     *
     * @param speed la vitesse, en pixels par seconde (positive)
     * @throws IllegalArgumentException si la vitesse n'est pas strictement positive
     */
    public WalkAndTurn(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be > 0");
        }
        this.speed = speed;
    }

    @Override
    public void move(Enemy enemy, Level level, Player player, double dt) {
        if (enemy.isOnGround() && (wallAhead(enemy, level) || gapAhead(enemy, level))) {
            enemy.turnAround();
        }
        enemy.setVelocity(enemy.getDirection() * speed, enemy.getVy());
    }

    /** Le point juste devant l'ennemi, à mi-hauteur, est-il dans un mur ? */
    private static boolean wallAhead(Enemy enemy, Level level) {
        return level.isSolidAt(frontX(enemy), enemy.getY() + enemy.getHeight() / 2);
    }

    /** Le point juste devant et sous les pieds de l'ennemi est-il du vide ? */
    private static boolean gapAhead(Enemy enemy, Level level) {
        return !level.isSolidAt(frontX(enemy), enemy.getY() + enemy.getHeight() + 1);
    }

    private static double frontX(Enemy enemy) {
        return enemy.getDirection() > 0
            ? enemy.getX() + enemy.getWidth() + 1
            : enemy.getX() - 1;
    }

    /**
     * Retourne la vitesse de marche.
     *
     * @return la vitesse, en pixels par seconde
     */
    public double getSpeed() {
        return speed;
    }
}
