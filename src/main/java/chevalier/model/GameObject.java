package chevalier.model;

import chevalier.model.level.Level;

/**
 * Tout objet placé dans un niveau : joueur, ennemi, caisse, objet à ramasser…
 *
 * <p>Un objet a une position (coin supérieur gauche, en pixels) et une taille.
 * Il ne sait pas se dessiner : il expose seulement un identifiant de sprite,
 * que la vue utilise pour choisir son apparence.</p>
 */
public abstract class GameObject {

    /** Abscisse du coin supérieur gauche, en pixels. */
    protected double x;

    /** Ordonnée du coin supérieur gauche, en pixels (l'axe Y descend). */
    protected double y;

    private final double width;
    private final double height;
    private boolean removed;

    /**
     * Crée un objet à une position donnée.
     *
     * @param x      abscisse du coin supérieur gauche
     * @param y      ordonnée du coin supérieur gauche
     * @param width  largeur, strictement positive
     * @param height hauteur, strictement positive
     * @throws IllegalArgumentException si la largeur ou la hauteur n'est pas strictement positive
     */
    protected GameObject(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Fait évoluer l'objet d'un pas de temps.
     *
     * @param level le niveau dans lequel se trouve l'objet
     * @param dt    la durée du pas, en secondes
     */
    public abstract void update(Level level, double dt);

    /**
     * Retourne l'identifiant de l'apparence de cet objet (ex. {@code "player"}).
     *
     * @return l'identifiant de sprite
     */
    public abstract String getSpriteId();

    /**
     * Retourne la boîte de collision de l'objet à sa position actuelle.
     *
     * @return la boîte de collision
     */
    public AABB getBounds() {
        return new AABB(x, y, width, height);
    }

    /**
     * Retourne l'abscisse du coin supérieur gauche.
     *
     * @return l'abscisse en pixels
     */
    public double getX() {
        return x;
    }

    /**
     * Retourne l'ordonnée du coin supérieur gauche.
     *
     * @return l'ordonnée en pixels
     */
    public double getY() {
        return y;
    }

    /**
     * Retourne la largeur de l'objet.
     *
     * @return la largeur en pixels
     */
    public double getWidth() {
        return width;
    }

    /**
     * Retourne la hauteur de l'objet.
     *
     * @return la hauteur en pixels
     */
    public double getHeight() {
        return height;
    }

    /**
     * Indique si l'objet doit être retiré du niveau (ennemi mort, pièce ramassée…).
     *
     * @return {@code true} si l'objet est à retirer
     */
    public boolean isRemoved() {
        return removed;
    }

    /** Marque l'objet comme à retirer du niveau. */
    public void markRemoved() {
        removed = true;
    }
}
