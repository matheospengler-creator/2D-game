package chevalier.view.sprite;

import chevalier.model.GameObject;
import javafx.scene.canvas.GraphicsContext;

/**
 * Sait dessiner un type d'objet du jeu.
 *
 * <p>Le {@code Renderer} associe chaque identifiant de sprite
 * ({@link GameObject#getSpriteId()}) à un {@code SpritePainter}. Ajouter
 * l'apparence d'un nouvel objet revient à écrire un nouveau peintre et à
 * l'enregistrer, sans modifier le reste du rendu.</p>
 */
@FunctionalInterface
public interface SpritePainter {

    /**
     * Dessine un objet à sa position (le contexte est déjà décalé par la caméra).
     *
     * @param gc     le contexte graphique
     * @param object l'objet à dessiner
     * @param time   temps écoulé, en secondes (pour les animations)
     */
    void paint(GraphicsContext gc, GameObject object, double time);
}
