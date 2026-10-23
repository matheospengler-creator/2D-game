package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.model.entity.Enemy;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/** Le garde : casque gris, tunique rouge et bouclier tenu devant lui. */
public class GuardPainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        Enemy guard = (Enemy) object;
        double x = guard.getX();
        double y = guard.getY();
        double w = guard.getWidth();
        double h = guard.getHeight();
        boolean facingRight = guard.getDirection() == 1;

        // jambes
        gc.setFill(Palette.VISOR);
        gc.fillRect(x + 5, y + h - 8, w - 10, 8);

        // tunique
        gc.setFill(Palette.GUARD_TUNIC);
        gc.fillRoundRect(x + 1, y + 11, w - 2, h - 18, 6, 6);

        // casque
        gc.setFill(Palette.ARMOR_EDGE);
        gc.fillRoundRect(x + 3, y, w - 6, 12, 8, 8);

        // bouclier, du côté où il marche
        gc.setFill(Palette.GUARD_SHIELD);
        gc.fillRoundRect(facingRight ? x + w - 6 : x - 3, y + 10, 9, 15, 4, 4);
        gc.setStroke(Palette.GOLD);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(facingRight ? x + w - 6 : x - 3, y + 10, 9, 15, 4, 4);
    }
}
