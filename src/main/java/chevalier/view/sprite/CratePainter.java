package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/** Une caisse en bois, renforcée en croix. */
public class CratePainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        double x = object.getX();
        double y = object.getY();
        double w = object.getWidth();
        double h = object.getHeight();

        gc.setFill(Palette.CRATE);
        gc.fillRect(x, y, w, h);

        gc.setStroke(Palette.CRATE_DARK);
        gc.setLineWidth(3);
        gc.strokeRect(x, y, w, h);
        gc.strokeLine(x, y, x + w, y + h);
        gc.strokeLine(x + w, y, x, y + h);
    }
}
