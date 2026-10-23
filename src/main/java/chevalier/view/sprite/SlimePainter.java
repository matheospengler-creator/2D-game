package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.model.entity.Enemy;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/** Le slime : un pavé vert arrondi avec deux yeux qui regardent où il va. */
public class SlimePainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        Enemy slime = (Enemy) object;
        double x = slime.getX();
        double y = slime.getY();
        double w = slime.getWidth();
        double h = slime.getHeight();

        // corps
        gc.setFill(Palette.SLIME);
        gc.fillRoundRect(x, y, w, h, 16, 16);

        // yeux, décalés du côté où il marche (direction = -1 ou 1)
        double look = slime.getDirection() * 3;
        gc.setFill(Palette.SLIME_EYE);
        gc.fillOval(x + w / 2 - 7 + look, y + 6, 5, 5);
        gc.fillOval(x + w / 2 + 2 + look, y + 6, 5, 5);
    }
}
