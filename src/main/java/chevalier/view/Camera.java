package chevalier.view;

import chevalier.model.entity.Player;
import chevalier.model.level.Level;

/**
 * La partie du niveau visible à l'écran.
 *
 * <p>La caméra se centre sur le joueur sans jamais sortir du niveau : près
 * des bords, c'est le joueur qui s'écarte du centre de l'écran. Pour dessiner,
 * on décale tout de {@code (-x, -y)}.</p>
 */
public class Camera {

    private final double viewWidth;
    private final double viewHeight;
    private double x;
    private double y;

    /**
     * Crée une caméra de la taille de l'écran.
     *
     * @param viewWidth  largeur visible, en pixels
     * @param viewHeight hauteur visible, en pixels
     */
    public Camera(double viewWidth, double viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    /**
     * Centre la caméra sur le joueur, en restant dans les limites du niveau.
     *
     * @param player le joueur à suivre
     * @param level  le niveau, qui fixe les limites
     */
    public void follow(Player player, Level level) {
        double targetX = player.getX() + player.getWidth() / 2 - viewWidth / 2;
        double targetY = player.getY() + player.getHeight() / 2 - viewHeight / 2;
        x = clamp(targetX, level.getPixelWidth() - viewWidth);
        y = clamp(targetY, level.getPixelHeight() - viewHeight);
    }

    /**
     * Limite une position entre 0 et {@code max}. Si le niveau est plus petit
     * que l'écran ({@code max} négatif), la caméra reste en 0.
     */
    private static double clamp(double value, double max) {
        return Math.max(0, Math.min(value, max));
    }

    /**
     * Retourne l'abscisse du bord gauche de la zone visible.
     *
     * @return l'abscisse en pixels du niveau
     */
    public double getX() {
        return x;
    }

    /**
     * Retourne l'ordonnée du bord haut de la zone visible.
     *
     * @return l'ordonnée en pixels du niveau
     */
    public double getY() {
        return y;
    }

    /**
     * Retourne la largeur visible.
     *
     * @return la largeur en pixels
     */
    public double getViewWidth() {
        return viewWidth;
    }

    /**
     * Retourne la hauteur visible.
     *
     * @return la hauteur en pixels
     */
    public double getViewHeight() {
        return viewHeight;
    }
}
