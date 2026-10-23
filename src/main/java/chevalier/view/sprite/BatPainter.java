package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/** La chauve-souris : un corps rond, deux ailes qui battent et des yeux dorés. */
public class BatPainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        double x = object.getX();
        double y = object.getY();
        double w = object.getWidth();
        double h = object.getHeight();

        // les ailes montent et descendent avec le temps
        double flap = Math.sin(time * 14) * 3;
        gc.setFill(Palette.BAT_WING);
        gc.fillOval(x - 6, y + 2 + flap, w / 2 + 4, h / 2);
        gc.fillOval(x + w / 2 + 2, y + 2 + flap, w / 2 + 4, h / 2);

        // corps
        gc.setFill(Palette.BAT_BODY);
        gc.fillOval(x + w / 2 - 7, y, 14, h);

        // yeux
        gc.setFill(Palette.GOLD);
        gc.fillOval(x + w / 2 - 4, y + 5, 3, 3);
        gc.fillOval(x + w / 2 + 1, y + 5, 3, 3);
    }
}
