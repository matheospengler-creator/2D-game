package chevalier.view;

import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.GameObject;
import chevalier.model.level.EntityFactory;
import chevalier.view.sprite.SpritePainter;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RendererTest {

    /**
     * Chaque objet que la fabrique sait créer doit avoir une apparence : sinon il
     * s'afficherait comme un rectangle magenta « sprite manquant ».
     */
    @Test
    void everyObjectOfTheGameHasAPainter() {
        Map<String, SpritePainter> painters = Renderer.defaultPainters();
        EntityFactory factory = new EntityFactory();

        for (char symbol : "PSBGCHX".toCharArray()) {
            GameObject object = factory.create(symbol, 0, 0, 1).orElseThrow();
            assertTrue(painters.containsKey(object.getSpriteId()),
                "no painter for '" + object.getSpriteId() + "'");
        }
    }
}
