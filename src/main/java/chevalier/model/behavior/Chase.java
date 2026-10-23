package chevalier.model.behavior;

import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.Level;

/**
 * Fonce vers le joueur.
 *
 * <p>Un ennemi volant (sans gravité) se dirige droit sur le joueur dans les
 * deux dimensions ; un ennemi au sol ne fait que se tourner et courir vers lui.
 * Sans état propre à un ennemi : une même instance peut être partagée.</p>
 */
public class Chase implements MovementBehavior {

    private final double speed;

    /**
     * Crée la stratégie avec une vitesse de poursuite.
     *
     * @param speed la vitesse, en pixels par seconde (positive)
     * @throws IllegalArgumentException si la vitesse n'est pas strictement positive
     */
    public Chase(double speed) {
        if (speed <= 0) {
            throw new IllegalArgumentException("speed must be > 0");
        }
        this.speed = speed;
    }

    @Override
    public void move(Enemy enemy, Level level, Player player, double dt) {
        double dx = centerX(player.getX(), player.getWidth()) - centerX(enemy.getX(), enemy.getWidth());
        double dy = centerY(player.getY(), player.getHeight()) - centerY(enemy.getY(), enemy.getHeight());

        if ((dx > 0) != (enemy.getDirection() > 0) && dx != 0) {
            enemy.turnAround();
        }

        if (enemy.isAffectedByGravity()) {
            enemy.setVelocity(enemy.getDirection() * speed, enemy.getVy());
            return;
        }
        double distance = Math.hypot(dx, dy);
        if (distance < 1) {
            enemy.setVelocity(0, 0);
        } else {
            enemy.setVelocity(dx / distance * speed, dy / distance * speed);
        }
    }

    private static double centerX(double x, double width) {
        return x + width / 2;
    }

    private static double centerY(double y, double height) {
        return y + height / 2;
    }

    /**
     * Retourne la vitesse de poursuite.
     *
     * @return la vitesse, en pixels par seconde
     */
    public double getSpeed() {
        return speed;
    }
}
