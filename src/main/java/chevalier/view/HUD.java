package chevalier.view;

import chevalier.controller.GameListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * L'affichage tête haute : cœurs, vies, pièces, niveau, et les écrans titre,
 * pause et fin, par-dessus le jeu.
 *
 * <p>Observateur de la partie ({@link GameListener}) : il ne lit jamais l'état
 * du jeu lui-même, il se met à jour quand on le prévient.</p>
 */
public class HUD implements GameListener {

    private static final String FULL_HEART = "♥";
    private static final String EMPTY_HEART = "♡";
    private static final Color GOLD = Palette.GOLD;

    /** Pastille sombre semi-transparente, comme sur la maquette. */
    private static final String PILL_STYLE = "-fx-background-color: rgba(20,16,28,0.72);"
        + "-fx-background-radius: 999; -fx-border-color: #332b44; -fx-border-radius: 999;"
        + "-fx-padding: 3 12 3 12;";

    private final Label hearts = label(26, Color.rgb(229, 72, 77));
    private final Label lives = label(18, Color.WHITE);
    private final Label coins = label(18, GOLD);
    private final Label levelName = label(18, GOLD);
    private final Label message = label(48, Color.WHITE);
    private final Label hint = label(20, Color.WHITE);
    private final Label controls = label(15, Color.WHITE);
    private final VBox endScreen = new VBox(8, message, hint, controls);
    private final HBox topBar = new HBox(16);
    private final BorderPane root = new BorderPane();

    /** Construit le HUD (vide tant qu'il n'a reçu aucun événement). */
    public HUD() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        lives.setStyle(PILL_STYLE);
        coins.setStyle(PILL_STYLE);
        message.setFont(Font.font("Serif", FontWeight.BOLD, 52));
        topBar.getChildren().addAll(hearts, lives, spacer, coins, levelName);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 14, 10, 14));

        controls.setText("← → / Q D : courir   ·   Espace / Z : sauter   ·   X / E : épée   ·   Échap : pause");
        endScreen.setAlignment(Pos.CENTER);
        endScreen.setVisible(false);

        root.setTop(topBar);
        root.setCenter(endScreen);
        root.setMouseTransparent(true);
    }

    private static Label label(double size, Color color) {
        Label label = new Label();
        label.setFont(Font.font("System", FontWeight.BOLD, size));
        label.setTextFill(color);
        label.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.7)));
        return label;
    }

    /**
     * Retourne le nœud JavaFX à placer par-dessus le canevas du jeu.
     *
     * @return le nœud du HUD
     */
    public Node getNode() {
        return root;
    }

    @Override
    public void onTitleScreen() {
        topBar.setVisible(false);
        showEndScreen("CHEVALIER", GOLD, "Entrée pour jouer");
        controls.setVisible(true);
    }

    @Override
    public void onPauseChanged(boolean paused) {
        if (paused) {
            showEndScreen("PAUSE", Color.WHITE, "Échap pour reprendre");
            controls.setVisible(true);
        } else {
            endScreen.setVisible(false);
        }
    }

    @Override
    public void onLevelStarted(int levelNumber) {
        levelName.setText("NIVEAU " + levelNumber);
        topBar.setVisible(true);
        endScreen.setVisible(false);
    }

    @Override
    public void onHeartsChanged(int heartsLeft, int maxHearts) {
        hearts.setText(FULL_HEART.repeat(heartsLeft) + EMPTY_HEART.repeat(maxHearts - heartsLeft));
    }

    @Override
    public void onLivesChanged(int livesLeft) {
        lives.setText("Vies : " + livesLeft);
    }

    @Override
    public void onCoinsChanged(int coinCount) {
        coins.setText(String.format("● × %02d", coinCount));
    }

    @Override
    public void onLevelCompleted(int levelNumber) {
        showEndScreen("NIVEAU " + levelNumber + " TERMINÉ !", GOLD, "Entrée : niveau suivant");
    }

    @Override
    public void onVictory(int coinCount) {
        showEndScreen("VICTOIRE !", GOLD,
            coinCount + " pièce" + (coinCount > 1 ? "s" : "") + " — Entrée pour rejouer");
    }

    @Override
    public void onGameOver() {
        showEndScreen("GAME OVER", Color.WHITE, "Entrée pour recommencer");
    }

    private void showEndScreen(String title, Color color, String hintText) {
        message.setText(title);
        message.setTextFill(color);
        hint.setText(hintText);
        controls.setVisible(false);
        endScreen.setVisible(true);
    }
}
