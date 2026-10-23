package chevalier.model;

/**
 * Boîte de collision rectangulaire alignée sur les axes
 * (Axis-Aligned Bounding Box).
 *
 * <p>Sert à détecter les contacts entre objets du jeu : joueur / ennemi,
 * hitbox de l'épée / cible, joueur / objet à ramasser.</p>
 *
 * @param x      abscisse du coin supérieur gauche
 * @param y      ordonnée du coin supérieur gauche (l'axe Y descend)
 * @param width  largeur, strictement positive
 * @param height hauteur, strictement positive
 */
public record AABB(double x, double y, double width, double height) {

    /**
     * Vérifie que les dimensions sont valides.
     *
     * @throws IllegalArgumentException si la largeur ou la hauteur n'est pas strictement positive
     */
    public AABB {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must be > 0");
        }
    }

    /**
     * Indique si cette boîte chevauche une autre boîte.
     *
     * <p>Deux boîtes qui se touchent seulement par un bord ne se chevauchent pas.</p>
     *
     * @param other l'autre boîte
     * @return {@code true} si les deux boîtes se chevauchent
     */
    public boolean intersects(AABB other) {
        return x < other.x + other.width
            && x + width > other.x
            && y < other.y + other.height
            && y + height > other.y;
    }
}
