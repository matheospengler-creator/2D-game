package chevalier.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * Le décor lointain : un ciel au crépuscule, une lune et des collines.
 *
 * <p>Les collines défilent moins vite que le niveau (parallaxe), ce qui donne
 * une impression de profondeur.</p>
 */
public class Background {

    /** Les collines avancent à 30 % de la vitesse de la caméra. */
    private static final double HILLS_PARALLAX = 0.3;

    /** Distance entre deux collines, en pixels. */
    private static final double HILL_SPACING = 300;

    private final LinearGradient sky = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
        new Stop(0, Palette.SKY_TOP), new Stop(1, Palette.SKY_HORIZON));

    /**
     * Dessine le décor lointain sur tout l'écran.
     *
     * @param gc      le contexte graphique
     * @param cameraX position horizontale de la caméra dans le niveau
     * @param width   largeur de l'écran
     * @param height  hauteur de l'écran
     */
    public void paint(GraphicsContext gc, double cameraX, double width, double height) {
        // ciel
        gc.setFill(sky);
        gc.fillRect(0, 0, width, height);

        // lune
        gc.setFill(Palette.MOON);
        gc.fillOval(width * 0.8, height * 0.1, 70, 70);

        // collines : de grands ovales, décalés selon la caméra
        double offset = (cameraX * HILLS_PARALLAX) % HILL_SPACING;
        gc.setFill(Palette.HILLS);
        for (double x = -offset - HILL_SPACING; x < width; x += HILL_SPACING) {
            gc.fillOval(x, height * 0.6, HILL_SPACING * 1.5, height * 0.8);
        }
    }
}
