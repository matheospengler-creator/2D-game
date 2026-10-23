package chevalier.view.sprite;

import chevalier.model.GameObject;
import chevalier.model.entity.Player;
import chevalier.view.Palette;
import javafx.scene.canvas.GraphicsContext;

/**
 * Le chevalier : heaume gris, tunique bleue, épée devant lui.
 * Pendant un coup, l'épée est dessinée sur la zone que le coup touche.
 */
public class KnightPainter implements SpritePainter {

    @Override
    public void paint(GraphicsContext gc, GameObject object, double time) {
        Player knight = (Player) object;
        double x = knight.getX();
        double y = knight.getY();
        double w = knight.getWidth();
        double h = knight.getHeight();
        boolean facingRight = knight.getFacing() == 1;

        // jambes
        gc.setFill(Palette.LEGS);
        gc.fillRect(x + 5, y + h - 8, w - 10, 8);

        // tunique
        gc.setFill(Palette.KNIGHT_TUNIC);
        gc.fillRoundRect(x + 2, y + 11, w - 4, h - 18, 6, 6);

        // heaume, avec la fente du côté où il regarde
        gc.setFill(Palette.ARMOR);
        gc.fillRoundRect(x + 3, y, w - 6, 12, 6, 6);
        gc.setFill(Palette.VISOR);
        gc.fillRect(facingRight ? x + w / 2 : x + 5, y + 5, w / 2 - 5, 2);

        // épée : sur la zone d'attaque pendant un coup, sinon levée devant lui
        gc.setFill(Palette.BLADE);
        if (knight.getAttackHitbox().isPresent()) {
            var sword = knight.getAttackHitbox().get();
            gc.fillRect(sword.x(), sword.y() + sword.height() / 2 - 2, sword.width(), 4);
        } else {
            gc.fillRect(facingRight ? x + w : x - 3, y + 4, 3, 16);
        }
    }
}
