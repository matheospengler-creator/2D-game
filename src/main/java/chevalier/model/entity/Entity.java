package chevalier.model.entity;

import chevalier.model.Damageable;
import chevalier.model.GameObject;
import chevalier.model.level.Level;

/**
 * Un objet qui se déplace, subit la gravité, entre en collision avec le décor
 * et possède des points de vie.
 *
 * <p>Les collisions sont résolues <b>un axe à la fois</b> : on déplace en X et
 * on corrige, puis on déplace en Y et on corrige. On sait ainsi toujours dans
 * quel sens repousser l'entité hors d'un mur ou d'un sol.</p>
 */
public abstract class Entity extends GameObject implements Damageable {

    /** Accélération de la gravité, en pixels par seconde². */
    public static final double GRAVITY = 1800;

    /** Vitesse de chute maximale, en pixels par seconde. */
    public static final double MAX_FALL_SPEED = 900;

    /**
     * Petite marge pour qu'une entité posée exactement contre une tuile
     * ne soit pas considérée comme « dedans ».
     */
    private static final double EPSILON = 0.001;

    /** Vitesse horizontale, en pixels par seconde (positive = vers la droite). */
    protected double vx;

    /** Vitesse verticale, en pixels par seconde (positive = vers le bas). */
    protected double vy;

    /** Vrai si l'entité repose sur une tuile solide. */
    protected boolean onGround;

    /** Faux pour les entités qui volent (chauve-souris). */
    protected boolean affectedByGravity = true;

    private final int maxHp;
    private int hp;

    /**
     * Crée une entité avec tous ses points de vie.
     *
     * @param x      abscisse du coin supérieur gauche
     * @param y      ordonnée du coin supérieur gauche
     * @param width  largeur
     * @param height hauteur
     * @param maxHp  points de vie maximum, strictement positifs
     * @throws IllegalArgumentException si {@code maxHp} n'est pas strictement positif
     */
    protected Entity(double x, double y, double width, double height, int maxHp) {
        super(x, y, width, height);
        if (maxHp <= 0) {
            throw new IllegalArgumentException("maxHp must be > 0");
        }
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    /**
     * Applique la gravité puis déplace l'entité en tenant compte du décor.
     *
     * @param level le niveau
     * @param dt    la durée du pas, en secondes
     */
    @Override
    public void update(Level level, double dt) {
        if (affectedByGravity) {
            applyGravity(dt);
        }
        moveAndCollide(level, dt);
    }

    /**
     * Accélère l'entité vers le bas, sans dépasser {@link #MAX_FALL_SPEED}.
     *
     * @param dt la durée du pas, en secondes
     */
    protected void applyGravity(double dt) {
        vy = Math.min(vy + GRAVITY * dt, MAX_FALL_SPEED);
    }

    /**
     * Déplace l'entité selon sa vitesse, d'abord en X puis en Y, en la
     * repoussant hors des tuiles solides.
     *
     * @param level le niveau
     * @param dt    la durée du pas, en secondes
     */
    protected void moveAndCollide(Level level, double dt) {
        moveX(level, vx * dt);
        moveY(level, vy * dt);
    }

    private void moveX(Level level, double dx) {
        x += dx;
        if (dx > 0) {
            int col = Level.toTile(x + getWidth() - EPSILON);
            if (hitsSolidColumn(level, col)) {
                x = col * Level.TILE_SIZE - getWidth();
                vx = 0;
            }
        } else if (dx < 0) {
            int col = Level.toTile(x);
            if (hitsSolidColumn(level, col)) {
                x = (col + 1) * Level.TILE_SIZE;
                vx = 0;
            }
        }
    }

    private void moveY(Level level, double dy) {
        y += dy;
        onGround = false;
        if (dy > 0) {
            int row = Level.toTile(y + getHeight() - EPSILON);
            if (hitsSolidRow(level, row)) {
                y = row * Level.TILE_SIZE - getHeight();
                vy = 0;
                onGround = true;
            }
        } else if (dy < 0) {
            int row = Level.toTile(y);
            if (hitsSolidRow(level, row)) {
                y = (row + 1) * Level.TILE_SIZE;
                vy = 0;
            }
        }
    }

    /** Une des tuiles de la colonne {@code col}, à la hauteur de l'entité, est-elle solide ? */
    private boolean hitsSolidColumn(Level level, int col) {
        int top = Level.toTile(y);
        int bottom = Level.toTile(y + getHeight() - EPSILON);
        for (int row = top; row <= bottom; row++) {
            if (level.getTile(col, row).isSolid()) {
                return true;
            }
        }
        return false;
    }

    /** Une des tuiles de la ligne {@code row}, sous la largeur de l'entité, est-elle solide ? */
    private boolean hitsSolidRow(Level level, int row) {
        int left = Level.toTile(x);
        int right = Level.toTile(x + getWidth() - EPSILON);
        for (int col = left; col <= right; col++) {
            if (level.getTile(col, row).isSolid()) {
                return true;
            }
        }
        return false;
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
     * Rend des points de vie, sans dépasser le maximum.
     *
     * @param amount le nombre de points de vie rendus, positif ou nul
     * @throws IllegalArgumentException si {@code amount} est négatif
     */
    public void heal(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("heal must be >= 0");
        }
        hp = Math.min(maxHp, hp + amount);
    }

    /**
     * Retourne les points de vie actuels.
     *
     * @return les points de vie
     */
    public int getHp() {
        return hp;
    }

    /**
     * Retourne les points de vie maximum.
     *
     * @return les points de vie maximum
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Indique si l'entité subit la gravité.
     *
     * @return {@code false} pour les entités volantes
     */
    public boolean isAffectedByGravity() {
        return affectedByGravity;
    }

    /**
     * Active ou désactive la gravité pour cette entité (désactivée pour les volants).
     *
     * @param affected {@code true} si l'entité doit tomber
     */
    public void setAffectedByGravity(boolean affected) {
        this.affectedByGravity = affected;
    }

    /**
     * Indique si l'entité repose sur une tuile solide.
     *
     * @return {@code true} si l'entité est au sol
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Retourne la vitesse horizontale.
     *
     * @return la vitesse en pixels par seconde
     */
    public double getVx() {
        return vx;
    }

    /**
     * Retourne la vitesse verticale.
     *
     * @return la vitesse en pixels par seconde (positive = vers le bas)
     */
    public double getVy() {
        return vy;
    }
}
