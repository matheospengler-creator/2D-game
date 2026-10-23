package chevalier;

import chevalier.controller.Game;
import chevalier.controller.InputHandler;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import chevalier.view.Camera;
import chevalier.view.HUD;
import chevalier.view.Renderer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Point d'entrée du jeu « Chevalier ».
 *
 * <p>Charge le premier niveau, crée la fenêtre, branche le clavier, abonne
 * le HUD à la partie et démarre la boucle de jeu.</p>
 */
public class Main extends Application {

    /** Largeur de la fenêtre, en pixels (30 tuiles). */
    public static final int WINDOW_WIDTH = 30 * Level.TILE_SIZE;

    /** Hauteur de la fenêtre, en pixels (17 tuiles). */
    public static final int WINDOW_HEIGHT = 17 * Level.TILE_SIZE;

    /** Nombre de niveaux : fichiers {@code levels/level1.txt} à {@code levelN.txt}. */
    public static final int LEVEL_COUNT = 3;

    /**
     * Construit et affiche la fenêtre principale.
     *
     * @param stage la fenêtre fournie par JavaFX
     * @throws InvalidLevelException si le premier niveau ne peut pas être chargé
     */
    @Override
    public void start(Stage stage) throws InvalidLevelException {
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        HUD hud = new HUD();
        Scene scene = new Scene(new StackPane(canvas, hud.getNode()));
        InputHandler input = new InputHandler();
        input.attach(scene);

        stage.setTitle("Chevalier");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        LevelLoader loader = new LevelLoader();
        Game game = new Game(n -> loader.load("/levels/level" + n + ".txt", n), LEVEL_COUNT, input);
        game.addListener(hud);
        game.start(new Renderer(canvas.getGraphicsContext2D()),
                   new Camera(WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    /**
     * Lance l'application JavaFX.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
