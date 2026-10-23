package chevalier.model.behavior;

import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.Level;

/**
 * Vole en ondulant autour d'un point d'ancrage, puis passe en poursuite
 * quand le joueur s'approche.
 *
 * <p>Le passage en poursuite remplace la stratégie de l'ennemi par une
 * {@link Chase} ({@link Enemy#setBehavior}) : c'est l'intérêt du patron
 * Strategy, le comportement change en cours de partie sans changer d'objet.</p>
 *
 * <p>Contrairement à {@link WalkAndTurn} et {@link Chase}, cette stratégie
 * mémorise son point d'ancrage : chaque ennemi a donc sa propre instance.</p>
 */
public class Fly implements MovementBehavior {

    /** Demi-largeur de la zone de patrouille, en pixels (3 tuiles de chaque côté). */
    public static final double PATROL_RANGE = 3 * Level.TILE_SIZE;

    /** Amplitude de l'ondulation verticale, en pixels. */
    public static final double WAVE_AMPLITUDE = 12;

    /** Longueur d'une ondulation, en pixels parcourus horizontalement. */
    public static final double WAVE_LENGTH = 96;

    /** Distance à laquelle la chauve-souris repère le joueur, en pixels (5 tuiles). */
    public static final double AGGRO_RANGE = 5 * Level.TILE_SIZE;

    private final double anchorX;
    private final double anchorY;
    private final double speed;
    private final double chaseSpeed;

    /**
     * Crée la stratégie de vol.
     *
     * @param anchorX    abscisse du centre de la zone de patrouille
     * @param anchorY    ordonnée de vol
     * @param speed      vitesse de patrouille, en pixels par seconde (positive)
     * @param chaseSpeed vitesse de poursuite une fois le joueur repéré (positive)
     * @throws IllegalArgumentException si une vitesse n'est pas strictement positive
     */
    public Fly(double anchorX, double anchorY, double speed, double chaseSpeed) {
        if (speed <= 0 || chaseSpeed <= 0) {
            throw new IllegalArgumentException("speeds must be > 0");
        }
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.speed = speed;
        this.chaseSpeed = chaseSpeed;
    }

    @Override
    public void move(Enemy enemy, Level level, Player player, double dt) {
        if (distance(enemy, player) < AGGRO_RANGE) {
            Chase chase = new Chase(chaseSpeed);
            enemy.setBehavior(chase);
            chase.move(enemy, level, player, dt);
            return;
        }

        double offset = enemy.getX() - anchorX;
        if (offset > PATROL_RANGE && enemy.getDirection() > 0
                || offset < -PATROL_RANGE && enemy.getDirection() < 0
                || enemy.getVx() == 0 && wasMoving(enemy)) {
            enemy.turnAround();
        }

        double targetY = anchorY + WAVE_AMPLITUDE * Math.sin(offset / WAVE_LENGTH * 2 * Math.PI);
        enemy.setVelocity(enemy.getDirection() * speed, (targetY - enemy.getY()) / dt);
    }

    /** Vrai après le premier pas : une vitesse nulle signifie alors qu'un mur l'a arrêtée. */
    private boolean wasMoving(Enemy enemy) {
        return enemy.getX() != anchorX;
    }

    private static double distance(Enemy enemy, Player player) {
        double dx = (player.getX() + player.getWidth() / 2) - (enemy.getX() + enemy.getWidth() / 2);
        double dy = (player.getY() + player.getHeight() / 2) - (enemy.getY() + enemy.getHeight() / 2);
        return Math.hypot(dx, dy);
    }
}
