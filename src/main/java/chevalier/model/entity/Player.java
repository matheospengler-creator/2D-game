package chevalier.model.entity;

import chevalier.model.AABB;
import chevalier.model.Damageable;
import chevalier.model.level.Level;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Le chevalier contrôlé par le joueur.
 *
 * <p>Le chevalier ne lit pas le clavier lui-même : le contrôleur lui transmet
 * des intentions ({@link #setMoveDirection}, {@link #setJumpHeld}). Il reste
 * ainsi testable sans fenêtre ni clavier.</p>
 *
 * <p>Il possède des <b>cœurs</b> (ses points de vie) et des <b>vies</b> :
 * perdre tous ses cœurs coûte une vie. Après un coup, il est brièvement
 * invincible.</p>
 */
public class Player extends Entity {

    /** Largeur du chevalier, en pixels (un peu moins qu'une tuile). */
    public static final double WIDTH = 22;

    /** Hauteur du chevalier, en pixels. */
    public static final double HEIGHT = 30;

    /** Vitesse de course, en pixels par seconde. */
    public static final double RUN_SPEED = 240;

    /** Vitesse verticale donnée par un saut, en pixels par seconde (≈ 4,2 tuiles de haut). */
    public static final double JUMP_SPEED = 700;

    /**
     * Vitesse de montée maximale quand la touche de saut est relâchée :
     * un appui bref donne un petit saut, un appui long un grand saut.
     */
    public static final double JUMP_CUT_SPEED = 250;

    /**
     * « Coyote time » : délai, en secondes, pendant lequel on peut encore sauter
     * après avoir quitté le bord d'une plateforme.
     */
    public static final double COYOTE_TIME = 0.1;

    /**
     * « Jump buffer » : un appui sur saut reste mémorisé pendant ce délai, en
     * secondes ; s'il est fait juste avant d'atterrir, le saut part à l'atterrissage.
     */
    public static final double JUMP_BUFFER = 0.1;

    /** Durée pendant laquelle un coup d'épée touche, en secondes. */
    public static final double ATTACK_DURATION = 0.15;

    /** Délai minimal entre deux coups d'épée, en secondes. */
    public static final double ATTACK_COOLDOWN = 0.35;

    /** Portée de l'épée devant le chevalier, en pixels. */
    public static final double SWORD_REACH = 28;

    /** Hauteur de la zone touchée par l'épée, en pixels. */
    public static final double SWORD_HEIGHT = 20;

    /** Points de vie retirés par un coup d'épée. */
    public static final int SWORD_DAMAGE = 1;

    /** Nombre de cœurs du chevalier. */
    public static final int MAX_HEARTS = 3;

    /** Nombre de vies au début de la partie. */
    public static final int START_LIVES = 3;

    /** Durée d'invincibilité après un coup, en secondes. */
    public static final double INVINCIBILITY_TIME = 1.0;

    private final double spawnX;
    private final double spawnY;

    private int lives = START_LIVES;
    private int coins;
    private double invincibilityTimer;

    private int moveDirection;
    private boolean jumpHeld;
    private double coyoteTimer;
    private double jumpBufferTimer;

    private int facing = 1;
    private boolean attackHeld;
    private double attackTimer;
    private double attackCooldown;
    private final Set<Damageable> hitThisSwing = new HashSet<>();

    /**
     * Crée le chevalier à sa position de départ.
     *
     * @param x abscisse du coin supérieur gauche
     * @param y ordonnée du coin supérieur gauche
     */
    public Player(double x, double y) {
        super(x, y, WIDTH, HEIGHT, MAX_HEARTS);
        this.spawnX = x;
        this.spawnY = y;
    }

    /**
     * Indique dans quelle direction le joueur veut courir.
     *
     * @param direction négatif = gauche, 0 = immobile, positif = droite
     */
    public void setMoveDirection(int direction) {
        this.moveDirection = Integer.signum(direction);
        if (moveDirection != 0) {
            facing = moveDirection;
        }
    }

    /**
     * Indique si la touche d'attaque est enfoncée. C'est l'appui qui déclenche
     * un coup d'épée, si le délai de récupération est écoulé : maintenir la
     * touche ne frappe qu'une fois.
     *
     * @param held {@code true} si la touche d'attaque est enfoncée
     */
    public void setAttackHeld(boolean held) {
        if (held && !attackHeld && attackCooldown == 0) {
            attackTimer = ATTACK_DURATION;
            attackCooldown = ATTACK_COOLDOWN;
            hitThisSwing.clear();
        }
        this.attackHeld = held;
    }

    /**
     * Indique si un coup d'épée est en cours.
     *
     * @return {@code true} pendant la durée du coup
     */
    public boolean isAttacking() {
        return attackTimer > 0;
    }

    /**
     * Retourne la zone touchée par l'épée, devant le chevalier, si un coup est en cours.
     *
     * @return la hitbox de l'épée, ou {@link Optional#empty()} si le chevalier n'attaque pas
     */
    public Optional<AABB> getAttackHitbox() {
        if (!isAttacking()) {
            return Optional.empty();
        }
        double swordX = facing > 0 ? x + getWidth() : x - SWORD_REACH;
        double swordY = y + (getHeight() - SWORD_HEIGHT) / 2;
        return Optional.of(new AABB(swordX, swordY, SWORD_REACH, SWORD_HEIGHT));
    }

    /**
     * Frappe une cible avec le coup d'épée en cours. Une même cible n'est
     * touchée qu'une fois par coup, même si la hitbox la touche pendant
     * plusieurs pas.
     *
     * @param target la cible touchée par la hitbox
     * @return {@code true} si la cible a reçu des dégâts
     */
    public boolean strike(Damageable target) {
        if (!isAttacking() || !hitThisSwing.add(target)) {
            return false;
        }
        target.takeDamage(SWORD_DAMAGE);
        return true;
    }

    /**
     * Retourne la direction dans laquelle regarde le chevalier.
     *
     * @return -1 (gauche) ou 1 (droite)
     */
    public int getFacing() {
        return facing;
    }

    /**
     * Indique si la touche de saut est enfoncée.
     *
     * <p>C'est l'<b>appui</b> (passage de relâchée à enfoncée) qui demande un saut :
     * maintenir la touche ne fait pas rebondir indéfiniment. Garder la touche
     * enfoncée pendant le saut le rend plus haut.</p>
     *
     * @param held {@code true} si la touche de saut est enfoncée
     */
    public void setJumpHeld(boolean held) {
        if (held && !jumpHeld) {
            jumpBufferTimer = JUMP_BUFFER;
        }
        this.jumpHeld = held;
    }

    @Override
    public void update(Level level, double dt) {
        invincibilityTimer = Math.max(0, invincibilityTimer - dt);
        attackTimer = Math.max(0, attackTimer - dt);
        attackCooldown = Math.max(0, attackCooldown - dt);
        coyoteTimer = onGround ? COYOTE_TIME : Math.max(0, coyoteTimer - dt);

        vx = moveDirection * RUN_SPEED;

        if (jumpBufferTimer > 0 && coyoteTimer > 0) {
            vy = -JUMP_SPEED;
            jumpBufferTimer = 0;
            coyoteTimer = 0;
        } else {
            jumpBufferTimer = Math.max(0, jumpBufferTimer - dt);
        }
        if (!jumpHeld && vy < -JUMP_CUT_SPEED) {
            vy = -JUMP_CUT_SPEED;
        }

        super.update(level, dt);
    }

    /**
     * Blesse le chevalier, sauf s'il est invincible. Un coup reçu le rend
     * invincible pendant {@link #INVINCIBILITY_TIME} secondes.
     *
     * @param amount le nombre de cœurs perdus
     * @return {@code true} si le coup a porté, {@code false} si le chevalier était invincible
     */
    public boolean hurt(int amount) {
        if (isInvincible()) {
            return false;
        }
        takeDamage(amount);
        invincibilityTimer = INVINCIBILITY_TIME;
        return true;
    }

    /**
     * Indique si le chevalier est temporairement invincible.
     *
     * @return {@code true} pendant la seconde qui suit un coup
     */
    public boolean isInvincible() {
        return invincibilityTimer > 0;
    }

    /**
     * Indique si le chevalier est tombé sous le bas du niveau.
     *
     * @param level le niveau
     * @return {@code true} s'il est tombé dans un trou
     */
    public boolean hasFallenOut(Level level) {
        return y > level.getPixelHeight();
    }

    /**
     * Ajoute des pièces au compteur du joueur.
     *
     * @param amount le nombre de pièces, positif ou nul
     * @throws IllegalArgumentException si {@code amount} est négatif
     */
    public void addCoins(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("coins must be >= 0");
        }
        coins += amount;
    }

    /**
     * Retourne le nombre de pièces ramassées.
     *
     * @return les pièces ramassées
     */
    public int getCoins() {
        return coins;
    }

    /**
     * Reprend la progression d'un chevalier du niveau précédent : vies et
     * pièces sont conservées d'un niveau à l'autre (les cœurs repartent au maximum).
     *
     * @param previous le chevalier du niveau précédent
     */
    public void inheritProgress(Player previous) {
        this.lives = previous.lives;
        this.coins = previous.coins;
    }

    /** Retire une vie (sans descendre sous zéro). */
    public void loseLife() {
        lives = Math.max(0, lives - 1);
    }

    /**
     * Retourne le nombre de vies restantes.
     *
     * @return les vies restantes
     */
    public int getLives() {
        return lives;
    }

    /**
     * Replace le chevalier à sa position de départ, immobile, avec tous ses cœurs.
     */
    public void respawn() {
        x = spawnX;
        y = spawnY;
        vx = 0;
        vy = 0;
        invincibilityTimer = 0;
        coyoteTimer = 0;
        jumpBufferTimer = 0;
        attackTimer = 0;
        attackCooldown = 0;
        heal(getMaxHp());
    }

    @Override
    public String getSpriteId() {
        return "player";
    }
}
