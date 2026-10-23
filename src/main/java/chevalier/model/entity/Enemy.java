package chevalier.model.entity;

import chevalier.model.behavior.MovementBehavior;
import chevalier.model.level.Level;

/**
 * Un ennemi piloté par une stratégie de déplacement (patron Strategy).
 *
 * <p>Il n'y a pas de sous-classe par type d'ennemi : un slime, une
 * chauve-souris ou un garde sont des {@code Enemy} qui diffèrent par leur
 * {@link MovementBehavior}, leurs statistiques et leur apparence. Ces
 * combinaisons sont fabriquées par l'{@code EntityFactory}.</p>
 */
public class Enemy extends Entity {

    /** Durée du clignotement après un coup, en secondes. */
    public static final double HURT_FLASH_TIME = 0.25;

    private final String spriteId;
    private final int contactDamage;
    private MovementBehavior behavior;
    private int direction = -1;
    private double hurtTimer;

    /**
     * Crée un ennemi.
     *
     * @param spriteId      l'apparence (ex. {@code "slime"})
     * @param x             abscisse du coin supérieur gauche
     * @param y             ordonnée du coin supérieur gauche
     * @param width         largeur
     * @param height        hauteur
     * @param maxHp         points de vie
     * @param contactDamage cœurs retirés au joueur qui le touche
     * @param behavior      la stratégie de déplacement
     */
    public Enemy(String spriteId, double x, double y, double width, double height,
                 int maxHp, int contactDamage, MovementBehavior behavior) {
        super(x, y, width, height, maxHp);
        this.spriteId = spriteId;
        this.contactDamage = contactDamage;
        this.behavior = behavior;
    }

    /**
     * Délègue le choix du mouvement à la stratégie, puis applique gravité et
     * collisions. Un ennemi mort ou tombé hors du niveau est retiré.
     *
     * @param level le niveau
     * @param dt    la durée du pas, en secondes
     */
    @Override
    public void update(Level level, double dt) {
        hurtTimer = Math.max(0, hurtTimer - dt);
        behavior.move(this, level, level.getPlayer(), dt);
        super.update(level, dt);
        if (isDead() || y > level.getPixelHeight()) {
            markRemoved();
        }
    }

    /**
     * Inflige des dégâts et fait clignoter brièvement l'ennemi.
     *
     * @param amount le nombre de points de vie retirés
     */
    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);
        if (amount > 0) {
            hurtTimer = HURT_FLASH_TIME;
        }
    }

    /**
     * Indique si l'ennemi vient d'être touché (il clignote).
     *
     * @return {@code true} juste après un coup
     */
    public boolean isHurt() {
        return hurtTimer > 0;
    }

    /**
     * Change la stratégie de déplacement, même en cours de partie.
     *
     * @param behavior la nouvelle stratégie
     */
    public void setBehavior(MovementBehavior behavior) {
        this.behavior = behavior;
    }

    /**
     * Retourne la stratégie de déplacement actuelle.
     *
     * @return la stratégie
     */
    public MovementBehavior getBehavior() {
        return behavior;
    }

    /**
     * Fixe la vitesse de l'ennemi. Utilisé par les stratégies.
     *
     * @param vx vitesse horizontale, en pixels par seconde
     * @param vy vitesse verticale, en pixels par seconde
     */
    public void setVelocity(double vx, double vy) {
        this.vx = vx;
        this.vy = vy;
    }

    /**
     * Retourne la direction dans laquelle l'ennemi regarde.
     *
     * @return -1 (gauche) ou 1 (droite)
     */
    public int getDirection() {
        return direction;
    }

    /** Fait faire demi-tour à l'ennemi. */
    public void turnAround() {
        direction = -direction;
    }

    /**
     * Retourne les cœurs retirés au joueur qui touche cet ennemi.
     *
     * @return les dégâts de contact
     */
    public int getContactDamage() {
        return contactDamage;
    }

    @Override
    public String getSpriteId() {
        return spriteId;
    }
}
