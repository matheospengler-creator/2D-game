package chevalier.view;

import chevalier.model.level.Level;
import chevalier.model.level.TileType;
import javafx.scene.canvas.GraphicsContext;

/**
 * Dessine les tuiles du niveau : terre (avec de l'herbe sur le dessus),
 * plateformes, piques et porte de sortie.
 */
public class TilePainter {

    private static final int SIZE = Level.TILE_SIZE;

    /**
     * Dessine les tuiles comprises dans un rectangle de cases (bornes incluses).
     *
     * @param gc       le contexte graphique (déjà décalé par la caméra)
     * @param level    le niveau
     * @param firstCol première colonne visible
     * @param lastCol  dernière colonne visible
     * @param firstRow première ligne visible
     * @param lastRow  dernière ligne visible
     */
    public void paint(GraphicsContext gc, Level level,
                      int firstCol, int lastCol, int firstRow, int lastRow) {
        for (int row = firstRow; row <= lastRow; row++) {
            for (int col = firstCol; col <= lastCol; col++) {
                double x = col * SIZE;
                double y = row * SIZE;
                switch (level.getTile(col, row)) {
                    case GROUND -> {
                        // l'herbe n'apparaît que si rien de solide n'est posé dessus
                        boolean surface = !level.getTile(col, row - 1).isSolid();
                        paintGround(gc, x, y, surface);
                    }
                    case PLATFORM -> paintPlatform(gc, x, y);
                    case SPIKES -> paintSpikes(gc, x, y);
                    case EXIT -> paintDoor(gc, x, y);
                    case EMPTY -> { }
                }
            }
        }
    }

    private void paintGround(GraphicsContext gc, double x, double y, boolean surface) {
        gc.setFill(Palette.DIRT);
        gc.fillRect(x, y, SIZE, SIZE);
        if (surface) {
            gc.setFill(Palette.GRASS);
            gc.fillRect(x, y, SIZE, 6);
        }
    }

    private void paintPlatform(GraphicsContext gc, double x, double y) {
        gc.setFill(Palette.DIRT);
        gc.fillRect(x, y, SIZE, SIZE / 2.0);
        gc.setFill(Palette.GRASS);
        gc.fillRect(x, y, SIZE, 6);
    }

    /** Trois triangles gris posés au fond de la case. */
    private void paintSpikes(GraphicsContext gc, double x, double y) {
        double base = y + SIZE;
        double width = SIZE / 3.0;
        gc.setFill(Palette.SPIKE);
        for (int i = 0; i < 3; i++) {
            double left = x + i * width;
            gc.fillPolygon(
                new double[] {left, left + width / 2, left + width},
                new double[] {base, base - 24, base}, 3);
        }
    }

    private void paintDoor(GraphicsContext gc, double x, double y) {
        gc.setFill(Palette.DOOR);
        gc.fillRect(x + 2, y, SIZE - 4, SIZE);
        gc.setStroke(Palette.GOLD);
        gc.setLineWidth(2);
        gc.strokeRect(x + 2, y, SIZE - 4, SIZE);
    }
}
