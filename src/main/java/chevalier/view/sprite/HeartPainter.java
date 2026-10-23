package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/** Un cœur rouge : deux ronds en haut et un triangle pointe en bas. */
public class HeartPainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        double x = object.getX();
        double y = object.getY();
        double w = object.getWidth();
        double h = object.getHeight();

        gc.setFill(Palette.HEART);
        gc.fillOval(x, y, w / 2 + 1, h / 2 + 1);
        gc.fillOval(x + w / 2 - 1, y, w / 2 + 1, h / 2 + 1);
        gc.fillPolygon(
            new double[] {x, x + w, x + w / 2},
            new double[] {y + h / 3, y + h / 3, y + h}, 3);
    }
}
