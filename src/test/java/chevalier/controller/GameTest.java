package chevalier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.entity.Coin;
import chevalier.model.entity.Crate;
import chevalier.model.entity.Enemy;
import chevalier.model.entity.Player;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

/**
 * Tests des règles de la partie (dégâts, vies, game over) et des notifications
 * envoyées aux observateurs. Aucune fenêtre n'est ouverte.
 */
class GameTest {

    private static final double DT = 1.0 / 60.0;

    /** Faux observateur : enregistre les événements reçus. */
    private static class RecordingListener implements GameListener {
        final List<String> events = new ArrayList<>();

        @Override
        public void onTitleScreen() {
            events.add("title");
        }

        @Override
        public void onPauseChanged(boolean paused) {
            events.add(paused ? "paused" : "resumed");
        }

        @Override
        public void onLevelStarted(int levelNumber) {
            events.add("level " + levelNumber + " started");
        }

        @Override
        public void onLevelCompleted(int levelNumber) {
            events.add("level " + levelNumber + " completed");
        }

        @Override
        public void onHeartsChanged(int hearts, int maxHearts) {
            events.add("hearts " + hearts + "/" + maxHearts);
        }

        @Override
        public void onLivesChanged(int lives) {
            events.add("lives " + lives);
        }

        @Override
        public void onCoinsChanged(int coins) {
            events.add("coins " + coins);
        }

        @Override
        public void onVictory(int coins) {
            events.add("victory " + coins);
        }

        @Override
        public void onGameOver() {
            events.add("game over");
        }
    }

    private final InputHandler input = new InputHandler();
    private final RecordingListener listener = new RecordingListener();

    /** Crée une partie d'un niveau, déjà lancée (l'écran titre est passé). */
    private Game game(String... lines) throws InvalidLevelException {
        Game game = new Game(n -> new LevelLoader().parse(List.of(lines), n), 1, input);
        game.restart();
        game.addListener(listener);
        return game;
    }

    private static void simulate(Game game, int steps) {
        for (int i = 0; i < steps; i++) {
            game.update(DT);
        }
    }

    // Le joueur tombe directement sur des piques.
    private Game spikePit() throws InvalidLevelException {
        return game(
            "#P#",
            "#.#",
            "#^#",
            "###");
    }

    // Pas de sol : le joueur tombe hors du niveau.
    private Game bottomlessPit() throws InvalidLevelException {
        return game(
            ".P.",
            "...");
    }

    @Test
    void newListenerReceivesTheCurrentState() throws InvalidLevelException {
        game("P", "#");

        assertEquals(List.of("level 1 started", "hearts 3/3", "lives 3", "coins 0"), listener.events);
    }

    @Test
    void spikesCostOneHeart() throws InvalidLevelException {
        Game game = spikePit();
        listener.events.clear();

        simulate(game, 30);

        assertEquals(List.of("hearts 2/3"), listener.events);
    }

    @Test
    void losingAllHeartsCostsALifeAndRestoresHearts() throws InvalidLevelException {
        Game game = spikePit();
        listener.events.clear();

        simulate(game, 60 * 4);    // assez pour 3 coups espacés d'une seconde

        assertTrue(listener.events.containsAll(List.of("hearts 1/3", "lives 2", "hearts 3/3")));
        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void fallingOutOfTheLevelCostsALifeAndRespawns() throws InvalidLevelException {
        Game game = bottomlessPit();
        listener.events.clear();

        simulate(game, 20);        // une seule chute (~16 pas)

        assertEquals(List.of("lives 2", "hearts 3/3"), listener.events);
    }

    @Test
    void noLivesLeftMeansGameOver() throws InvalidLevelException {
        Game game = bottomlessPit();
        listener.events.clear();

        simulate(game, 60 * 5);

        assertEquals(GameState.GAME_OVER, game.getState());
        assertTrue(listener.events.contains("lives 0"));
        assertEquals("game over", listener.events.get(listener.events.size() - 1));
    }

    @Test
    void nothingMovesAfterGameOver() throws InvalidLevelException {
        Game game = bottomlessPit();
        simulate(game, 60 * 5);
        listener.events.clear();

        simulate(game, 60);

        assertTrue(listener.events.isEmpty());
    }

    @Test
    void touchingASlimeCostsOneHeart() throws InvalidLevelException {
        Game game = game(
            "#....#",
            "#P.S.#",
            "######");
        listener.events.clear();

        simulate(game, 120);       // le slime marche vers le joueur

        assertTrue(listener.events.contains("hearts 2/3"));
    }

    // Le joueur, tourné vers la droite, avec un slime juste devant lui.
    private Game swordRange() throws InvalidLevelException {
        return game(
            "#..........#",
            "#P.S.......#",
            "############");
    }

    private static long enemiesIn(Game game) {
        return game.getLevel().getObjects().stream().filter(o -> o instanceof Enemy).count();
    }

    @Test
    void swordKillsASlime() throws InvalidLevelException {
        Game game = swordRange();
        simulate(game, 10);                  // le slime s'approche

        input.press(KeyCode.X);
        simulate(game, 5);

        assertEquals(0, enemiesIn(game));
    }

    @Test
    void swordHitsATwoHitPointEnemyOnlyOncePerSwing() throws InvalidLevelException {
        Game game = game(
            "#.........#",
            "#P........#",
            "###########");
        simulate(game, 10);
        Player player = game.getLevel().getPlayer();
        Enemy guard = new Enemy("guard", player.getX() + Player.WIDTH + 4, player.getY(),
            20, 30, 2, 1, (e, l, p, dt) -> e.setVelocity(0, e.getVy()));
        game.getLevel().addObject(guard);

        input.press(KeyCode.X);
        simulate(game, 5);                   // la hitbox reste active plusieurs pas

        assertEquals(1, guard.getHp());
        assertFalse(guard.isRemoved());
    }

    @Test
    void guardDiesAfterTwoSeparateSwings() throws InvalidLevelException {
        Game game = game(
            "#..........#",
            "#PG........#",
            "############");
        simulate(game, 5);

        input.press(KeyCode.X);
        simulate(game, 5);
        input.release(KeyCode.X);
        assertEquals(1, enemiesIn(game));      // 1er coup : encore debout

        simulate(game, 30);                    // délai de récupération écoulé
        input.press(KeyCode.X);
        simulate(game, 5);

        assertEquals(0, enemiesIn(game));      // 2e coup : vaincu
    }

    @Test
    void swordMissesBehindThePlayer() throws InvalidLevelException {
        Game game = game(
            "#..........#",
            "#...S.P....#",
            "############");
        simulate(game, 5);

        input.press(KeyCode.X);             // regarde à droite, le slime est à gauche
        simulate(game, 5);

        assertEquals(1, enemiesIn(game));
    }

    // Le joueur tombe directement sur la porte de sortie.
    private Game exitBelow() throws InvalidLevelException {
        return game(
            "#P#",
            "#E#",
            "###");
    }

    @Test
    void reachingTheExitOfTheLastLevelWinsTheGame() throws InvalidLevelException {
        Game game = exitBelow();                       // partie d'un seul niveau
        listener.events.clear();

        simulate(game, 30);

        assertEquals(GameState.VICTORY, game.getState());
        assertEquals(List.of("victory 0"), listener.events);
    }

    // Deux niveaux : le premier avec une pièce au-dessus de la sortie.
    private Game twoLevels() throws InvalidLevelException {
        List<List<String>> levels = List.of(
            List.of("#P#", "#C#", "#E#", "###"),
            List.of("#....#", "#.P..#", "#...E#", "######"));
        Game game = new Game(n -> new LevelLoader().parse(levels.get(n - 1), n), 2, input);
        game.restart();
        game.addListener(listener);
        return game;
    }

    @Test
    void reachingTheExitOfAnEarlierLevelCompletesIt() throws InvalidLevelException {
        Game game = twoLevels();
        listener.events.clear();

        simulate(game, 30);

        assertEquals(GameState.LEVEL_COMPLETE, game.getState());
        assertTrue(listener.events.contains("level 1 completed"));
    }

    @Test
    void enterAfterALevelLoadsTheNextOneAndKeepsProgress() throws InvalidLevelException {
        Game game = twoLevels();
        simulate(game, 30);                            // ramasse la pièce, atteint la sortie
        game.getLevel().getPlayer().loseLife();
        listener.events.clear();

        input.press(KeyCode.ENTER);
        simulate(game, 1);

        assertEquals(GameState.PLAYING, game.getState());
        assertEquals(2, game.getLevel().getNumber());
        Player player = game.getLevel().getPlayer();
        assertEquals(Player.START_LIVES - 1, player.getLives());
        assertEquals(1, player.getCoins());
        assertEquals(Player.MAX_HEARTS, player.getHp());
        assertEquals(List.of("level 2 started", "hearts 3/3", "lives 2", "coins 1"), listener.events);
    }

    @Test
    void gameOverRestartsFromTheFirstLevel() throws InvalidLevelException {
        Game game = twoLevels();
        simulate(game, 30);
        input.press(KeyCode.ENTER);
        simulate(game, 1);
        input.release(KeyCode.ENTER);
        assertEquals(2, game.getLevel().getNumber());

        game.restart();

        assertEquals(1, game.getLevel().getNumber());
        assertEquals(0, game.getLevel().getPlayer().getCoins());
    }

    @Test
    void levelCountMustBePositive() {
        assertThrows(IllegalArgumentException.class,
            () -> new Game(n -> new LevelLoader().parse(List.of("P"), n), 0, input));
    }

    @Test
    void enterRestartsAfterGameOver() throws InvalidLevelException {
        Game game = bottomlessPit();
        simulate(game, 60 * 5);
        Level oldLevel = game.getLevel();
        listener.events.clear();

        input.press(KeyCode.ENTER);
        simulate(game, 1);

        assertEquals(GameState.PLAYING, game.getState());
        assertNotSame(oldLevel, game.getLevel());
        assertEquals(Player.START_LIVES, game.getLevel().getPlayer().getLives());
        assertEquals(List.of("level 1 started", "hearts 3/3", "lives 3", "coins 0"), listener.events);
    }

    @Test
    void enterRestartsAfterVictory() throws InvalidLevelException {
        Game game = exitBelow();
        simulate(game, 30);

        input.press(KeyCode.ENTER);
        simulate(game, 1);

        assertEquals(GameState.PLAYING, game.getState());
    }

    @Test
    void enterDoesNothingWhilePlaying() throws InvalidLevelException {
        Game game = game(
            "#....#",
            "#.P..#",
            "######");
        Level level = game.getLevel();

        input.press(KeyCode.ENTER);
        simulate(game, 10);

        assertSame(level, game.getLevel());
    }

    @Test
    void restartBringsKilledEnemiesBack() throws InvalidLevelException {
        Game game = swordRange();
        simulate(game, 10);
        input.press(KeyCode.X);
        simulate(game, 5);
        assertEquals(0, enemiesIn(game));
        input.release(KeyCode.X);

        game.restart();

        assertEquals(1, enemiesIn(game));
    }

    @Test
    void touchingACoinCollectsIt() throws InvalidLevelException {
        Game game = game(
            "#P#",
            "#C#",
            "###");
        listener.events.clear();

        simulate(game, 30);

        assertEquals(1, game.getLevel().getPlayer().getCoins());
        assertTrue(listener.events.contains("coins 1"));
        assertEquals(1, game.getLevel().getObjects().size());   // il ne reste que le joueur
    }

    @Test
    void touchingAHeartHeals() throws InvalidLevelException {
        Game game = game(
            "#P#",
            "#.#",
            "#H#",
            "###");
        Player player = game.getLevel().getPlayer();
        player.hurt(2);
        listener.events.clear();

        simulate(game, 30);

        assertEquals(Player.MAX_HEARTS - 1, player.getHp());
        assertTrue(listener.events.contains("hearts 2/3"));
    }

    @Test
    void swordBreaksACrateWhichReleasesACoin() throws InvalidLevelException {
        Game game = game(
            "#.......#",
            "#PX.....#",                       // caisse juste devant le joueur
            "#########");
        simulate(game, 10);
        Player player = game.getLevel().getPlayer();

        input.press(KeyCode.X);
        simulate(game, 5);
        input.release(KeyCode.X);

        assertTrue(game.getLevel().getObjects().stream().noneMatch(o -> o instanceof Crate));
        assertTrue(game.getLevel().getObjects().stream().anyMatch(o -> o instanceof Coin));

        input.press(KeyCode.RIGHT);           // va ramasser la pièce libérée
        simulate(game, 30);

        assertEquals(1, player.getCoins());
    }

    @Test
    void coinsAreKeptAfterLosingALife() throws InvalidLevelException {
        Game game = game(
            ".P.",
            ".C.",
            "...");
        simulate(game, 30);                   // ramasse la pièce en tombant, puis tombe hors du niveau

        Player player = game.getLevel().getPlayer();
        assertTrue(player.getLives() < Player.START_LIVES);
        assertEquals(1, player.getCoins());
    }

    // ---------- Écran titre et pause ----------

    private Game onTitleScreen() throws InvalidLevelException {
        Game game = new Game(n -> new LevelLoader().parse(List.of(
            "#....#",
            "#.P..#",
            "######"), n), 1, input);
        game.addListener(listener);
        return game;
    }

    @Test
    void gameStartsOnTheTitleScreen() throws InvalidLevelException {
        Game game = onTitleScreen();

        assertEquals(GameState.MENU, game.getState());
        assertEquals("title", listener.events.get(listener.events.size() - 1));
    }

    @Test
    void nothingMovesOnTheTitleScreen() throws InvalidLevelException {
        Game game = onTitleScreen();
        Player player = game.getLevel().getPlayer();
        double startY = player.getY();

        simulate(game, 30);

        assertEquals(startY, player.getY());
    }

    @Test
    void enterOnTheTitleScreenStartsTheGame() throws InvalidLevelException {
        Game game = onTitleScreen();
        listener.events.clear();

        input.press(KeyCode.ENTER);
        simulate(game, 1);

        assertEquals(GameState.PLAYING, game.getState());
        assertEquals("level 1 started", listener.events.get(0));
    }

    @Test
    void escapePausesAndFreezesTheGame() throws InvalidLevelException {
        Game game = game(
            "#....#",
            "#.P..#",
            "#....#",
            "######");
        Player player = game.getLevel().getPlayer();
        listener.events.clear();

        input.press(KeyCode.ESCAPE);
        simulate(game, 1);
        double pausedY = player.getY();
        simulate(game, 30);

        assertEquals(GameState.PAUSED, game.getState());
        assertEquals(pausedY, player.getY());              // la gravité est figée
        assertEquals(List.of("paused"), listener.events);
    }

    @Test
    void escapeAgainResumes() throws InvalidLevelException {
        Game game = game("#P#", "###");
        input.press(KeyCode.ESCAPE);
        simulate(game, 1);
        input.release(KeyCode.ESCAPE);

        input.press(KeyCode.ESCAPE);
        simulate(game, 1);

        assertEquals(GameState.PLAYING, game.getState());
        assertTrue(listener.events.contains("resumed"));
    }

    @Test
    void holdingEscapeTogglesOnlyOnce() throws InvalidLevelException {
        Game game = game("#P#", "###");

        input.press(KeyCode.ESCAPE);
        simulate(game, 30);                                // touche maintenue

        assertEquals(GameState.PAUSED, game.getState());
    }

    @Test
    void enterPressedDuringPlayDoesNotSkipTheLevelCompleteScreen() throws InvalidLevelException {
        Game game = twoLevels();

        input.press(KeyCode.ENTER);                        // appui pendant le jeu…
        simulate(game, 30);                                // …puis le joueur atteint la sortie

        assertEquals(GameState.LEVEL_COMPLETE, game.getState());
        assertEquals(1, game.getLevel().getNumber());
    }

    @Test
    void removedListenerIsNoLongerNotified() throws InvalidLevelException {
        Game game = bottomlessPit();
        game.removeListener(listener);
        listener.events.clear();

        simulate(game, 30);

        assertTrue(listener.events.isEmpty());
    }

    @Test
    void keyboardActionsDriveThePlayer() throws InvalidLevelException {
        Game game = game(
            "#....#",
            "#.P..#",
            "######");
        simulate(game, 30);
        Player player = game.getLevel().getPlayer();
        double startX = player.getX();

        input.press(KeyCode.RIGHT);
        simulate(game, 5);

        assertTrue(player.getX() > startX);
    }
}
