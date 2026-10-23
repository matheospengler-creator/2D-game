package chevalier.controller;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * Mémorise les touches actuellement enfoncées et les traduit en {@link Action}.
 *
 * <p>Les touches suivent le GDD : flèches ou Q/D pour courir, espace, flèche
 * haut ou Z pour sauter, X ou E pour attaquer, Échap pour la pause, Entrée
 * pour valider.</p>
 */
public class InputHandler {

    private final Set<KeyCode> pressed = new HashSet<>();
    private final Set<KeyCode> justPressed = new HashSet<>();
    private final Map<Action, Set<KeyCode>> bindings = new EnumMap<>(Action.class);

    /** Crée un gestionnaire avec les touches par défaut. */
    public InputHandler() {
        bindings.put(Action.LEFT, EnumSet.of(KeyCode.LEFT, KeyCode.Q));
        bindings.put(Action.RIGHT, EnumSet.of(KeyCode.RIGHT, KeyCode.D));
        bindings.put(Action.JUMP, EnumSet.of(KeyCode.SPACE, KeyCode.UP, KeyCode.Z));
        bindings.put(Action.ATTACK, EnumSet.of(KeyCode.X, KeyCode.E));
        bindings.put(Action.PAUSE, EnumSet.of(KeyCode.ESCAPE));
        bindings.put(Action.CONFIRM, EnumSet.of(KeyCode.ENTER));
    }

    /**
     * Branche ce gestionnaire sur les événements clavier d'une scène JavaFX.
     *
     * @param scene la scène à écouter
     */
    public void attach(Scene scene) {
        scene.setOnKeyPressed(event -> press(event.getCode()));
        scene.setOnKeyReleased(event -> release(event.getCode()));
        scene.windowProperty().addListener((obs, oldWindow, window) -> {
            if (window != null) {
                window.focusedProperty().addListener((o, wasFocused, focused) -> {
                    if (!focused) {
                        releaseAll();
                    }
                });
            }
        });
    }

    /**
     * Enregistre qu'une touche vient d'être enfoncée.
     *
     * @param key la touche
     */
    public void press(KeyCode key) {
        if (pressed.add(key)) {
            justPressed.add(key);   // la répétition automatique du clavier ne compte pas
        }
    }

    /**
     * Enregistre qu'une touche vient d'être relâchée.
     *
     * @param key la touche
     */
    public void release(KeyCode key) {
        pressed.remove(key);
    }

    /**
     * Relâche toutes les touches. Appelé quand la fenêtre perd le focus, sinon
     * une touche relâchée hors de la fenêtre resterait « enfoncée ».
     */
    public void releaseAll() {
        pressed.clear();
        justPressed.clear();
    }

    /**
     * Indique si une touche précise est enfoncée.
     *
     * @param key la touche
     * @return {@code true} si la touche est enfoncée
     */
    public boolean isPressed(KeyCode key) {
        return pressed.contains(key);
    }

    /**
     * Indique si une action vient d'être <b>appuyée</b> depuis le dernier pas de
     * jeu (et non simplement maintenue). Sert aux actions ponctuelles : pause,
     * validation. Vrai une seule fois par appui.
     *
     * @param action l'action
     * @return {@code true} si une touche de l'action a été appuyée pendant ce pas
     */
    public boolean wasPressed(Action action) {
        for (KeyCode key : bindings.get(action)) {
            if (justPressed.contains(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Termine un pas de jeu : les appuis de ce pas sont oubliés. Appelé par le
     * jeu à la fin de chaque mise à jour.
     */
    public void endStep() {
        justPressed.clear();
    }

    /**
     * Indique si une action est demandée, c'est-à-dire si au moins une des
     * touches associées est enfoncée.
     *
     * @param action l'action
     * @return {@code true} si l'action est demandée
     */
    public boolean isActive(Action action) {
        for (KeyCode key : bindings.get(action)) {
            if (pressed.contains(key)) {
                return true;
            }
        }
        return false;
    }
}
