package chevalier.view;

import chevalier.model.GameObject;
import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.Level;
import chevalier.view.sprite.BatPainter;
import chevalier.view.sprite.CoinPainter;
import chevalier.view.sprite.CratePainter;
import chevalier.view.sprite.GuardPainter;
import chevalier.view.sprite.HeartPainter;
import chevalier.view.sprite.KnightPainter;
import chevalier.view.sprite.SlimePainter;
import chevalier.view.sprite.SpritePainter;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Dessine l'état du jeu sur un canevas JavaFX : décor lointain, tuiles, puis objets.
 *
 * <p>Le modèle ne sait pas se dessiner : chaque objet expose seulement un
 * identifiant de sprite, que le {@code Renderer} associe à un
 * {@link SpritePainter}. Ajouter l'apparence d'un nouvel objet revient à
 * enregistrer un nouveau peintre ({@link #register}).</p>
 */
public class Renderer {

    private final GraphicsContext gc;
    private final Background background = new Background();
    private final TilePainter tiles = new TilePainter();
    private final Map<String, SpritePainter> painters = defaultPainters();

    /**
     * Crée un renderer qui dessine sur le canevas donné.
     *
     * @param gc le contexte graphique du canevas
     */
    public Renderer(GraphicsContext gc) {
        this.gc = gc;
    }

    /**
     * Retourne les peintres de tous les objets du jeu, par identifiant de sprite.
     *
     * @return une nouvelle table identifiant → peintre
     */
    public static Map<String, SpritePainter> defaultPainters() {
        Map<String, SpritePainter> map = new HashMap<>();
        map.put("player", new KnightPainter());
        map.put("slime", new SlimePainter());
        map.put("bat", new BatPainter());
        map.put("guard", new GuardPainter());
        map.put("coin", new CoinPainter());
        map.put("heart", new HeartPainter());
        map.put("crate", new CratePainter());
        return map;
    }

    /**
     * Associe (ou remplace) le peintre d'un identifiant de sprite.
     *
     * @param spriteId l'identifiant, tel que renvoyé par {@link GameObject#getSpriteId()}
     * @param painter  le peintre à utiliser
     */
    public void register(String spriteId, SpritePainter painter) {
        painters.put(spriteId, painter);
    }

    /**
     * Dessine la partie du niveau visible par la caméra.
     *
     * @param level  le niveau à dessiner
     * @param camera la caméra qui fixe la zone visible
     */
    public void render(Level level, Camera camera) {
        double time = System.nanoTime() / 1_000_000_000.0;
        background.paint(gc, camera.getX(), gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        gc.save();
        gc.translate(-Math.round(camera.getX()), -Math.round(camera.getY()));

        // Seules les tuiles à l'écran sont dessinées.
        tiles.paint(gc, level,
            Level.toTile(camera.getX()), Level.toTile(camera.getX() + camera.getViewWidth()),
            Level.toTile(camera.getY()), Level.toTile(camera.getY() + camera.getViewHeight()));

        for (GameObject object : level.getObjects()) {
            if (!isBlinkedOut(object, time)) {
                painters.getOrDefault(object.getSpriteId(), Renderer::paintMissing)
                    .paint(gc, object, time);
            }
        }

        gc.restore();
    }

    /**
     * Un joueur invincible ou un ennemi qui vient d'être touché clignote : il est
     * caché un dixième de seconde sur deux (un vingtième pour les ennemis).
     */
    private static boolean isBlinkedOut(GameObject object, double time) {
        if (object instanceof Player player && player.isInvincible()) {
            return (long) (time * 10) % 2 == 0;
        }
        if (object instanceof Enemy enemy && enemy.isHurt()) {
            return (long) (time * 20) % 2 == 0;
        }
        return false;
    }

    /** Objet sans peintre : un rectangle magenta, bien visible pour le repérer. */
    private static void paintMissing(GraphicsContext gc, GameObject object, double time) {
        gc.setFill(Color.MAGENTA);
        gc.fillRect(object.getX(), object.getY(), object.getWidth(), object.getHeight());
    }
}
