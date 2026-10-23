package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/** Une pièce d'or : un disque doré avec un contour. */
public class CoinPainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        double x = object.getX();
        double y = object.getY();
        double w = object.getWidth();
        double h = object.getHeight();

        gc.setFill(Palette.GOLD);
        gc.fillOval(x, y, w, h);
        gc.setStroke(Palette.GOLD_EDGE);
        gc.setLineWidth(1.5);
        gc.strokeOval(x, y, w, h);
    }
}
