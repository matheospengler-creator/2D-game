package chevalier.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

class InputHandlerTest {

    private final InputHandler input = new InputHandler();

    @Test
    void nothingIsActiveAtStart() {
        for (Action action : Action.values()) {
            assertFalse(input.isActive(action));
        }
    }

    @Test
    void pressedKeyActivatesItsAction() {
        input.press(KeyCode.SPACE);
        assertTrue(input.isPressed(KeyCode.SPACE));
        assertTrue(input.isActive(Action.JUMP));
        assertFalse(input.isActive(Action.LEFT));
    }

    @Test
    void severalKeysTriggerTheSameAction() {
        input.press(KeyCode.Q);
        assertTrue(input.isActive(Action.LEFT));
        input.release(KeyCode.Q);
        input.press(KeyCode.LEFT);
        assertTrue(input.isActive(Action.LEFT));
    }

    @Test
    void releasedKeyDeactivatesItsAction() {
        input.press(KeyCode.D);
        input.release(KeyCode.D);
        assertFalse(input.isActive(Action.RIGHT));
    }

    @Test
    void aPressIsSeenOnlyUntilTheEndOfTheStep() {
        input.press(KeyCode.ESCAPE);
        assertTrue(input.wasPressed(Action.PAUSE));

        input.endStep();

        assertFalse(input.wasPressed(Action.PAUSE));
        assertTrue(input.isActive(Action.PAUSE));         // toujours enfoncée
    }

    @Test
    void keyRepeatWhileHeldIsNotANewPress() {
        input.press(KeyCode.ENTER);
        input.endStep();

        input.press(KeyCode.ENTER);                        // répétition automatique du clavier

        assertFalse(input.wasPressed(Action.CONFIRM));
    }

    @Test
    void pressingAgainAfterReleasingIsANewPress() {
        input.press(KeyCode.ENTER);
        input.endStep();
        input.release(KeyCode.ENTER);

        input.press(KeyCode.ENTER);

        assertTrue(input.wasPressed(Action.CONFIRM));
    }

    @Test
    void releaseAllClearsEveryKey() {
        input.press(KeyCode.D);
        input.press(KeyCode.X);
        input.releaseAll();
        assertFalse(input.isActive(Action.RIGHT));
        assertFalse(input.isActive(Action.ATTACK));
    }
}
