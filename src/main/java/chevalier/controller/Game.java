package chevalier.controller;

import chevalier.model.AABB;
import chevalier.model.Damageable;
import chevalier.model.GameObject;
import chevalier.model.entity.Crate;
import chevalier.model.entity.Enemy;
import chevalier.model.entity.Item;
import chevalier.model.entity.Player;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.TileType;
import chevalier.view.Camera;
import chevalier.view.Renderer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.animation.AnimationTimer;

/**
 * Le chef d'orchestre du jeu : fait tourner la boucle
 * « lire le clavier → mettre à jour → dessiner » et applique les règles
 * (dégâts, vies, victoire, game over).
 *
 * <p>C'est aussi le <b>sujet</b> du patron Observer : il prévient ses
 * {@link GameListener} abonnés de chaque événement, sans savoir qui ils sont.</p>
 *
 * <p>La logique avance par pas fixes ({@link FixedTimestep}) ; l'affichage,
 * lui, a lieu une fois par image de l'écran.</p>
 */
public class Game {

    /** Cœurs perdus au contact des piques. */
    public static final int SPIKE_DAMAGE = 1;

    private final LevelProvider levels;
    private final int levelCount;
    private final InputHandler input;
    private Level level;
    private final List<GameListener> listeners = new ArrayList<>();
    private final FixedTimestep timestep = new FixedTimestep();
    private GameState state = GameState.MENU;

    /**
     * Crée une partie et charge le premier niveau. Le jeu démarre sur l'écran titre.
     *
     * @param levels     fournit les niveaux à jouer
     * @param levelCount nombre de niveaux de la partie (numérotés de 1 à {@code levelCount})
     * @param input      le gestionnaire du clavier
     * @throws InvalidLevelException    si le premier niveau ne peut pas être chargé
     * @throws IllegalArgumentException si {@code levelCount} n'est pas strictement positif
     */
    public Game(LevelProvider levels, int levelCount, InputHandler input)
            throws InvalidLevelException {
        if (levelCount <= 0) {
            throw new IllegalArgumentException("levelCount must be > 0");
        }
        this.levels = levels;
        this.levelCount = levelCount;
        this.input = input;
        this.level = levels.load(1);
    }

    /**
     * Abonne un observateur aux événements de la partie. Il reçoit
     * immédiatement l'état actuel (niveau, cœurs, vies) pour pouvoir s'afficher.
     *
     * @param listener l'observateur à abonner
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
        sendCurrentState(listener);
        if (state == GameState.MENU) {
            listener.onTitleScreen();
        }
    }

    private void sendCurrentState(GameListener listener) {
        Player player = level.getPlayer();
        listener.onLevelStarted(level.getNumber());
        listener.onHeartsChanged(player.getHp(), player.getMaxHp());
        listener.onLivesChanged(player.getLives());
        listener.onCoinsChanged(player.getCoins());
    }

    /**
     * Désabonne un observateur.
     *
     * @param listener l'observateur à désabonner
     */
    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    /**
     * Démarre la boucle de jeu, appelée par JavaFX à chaque image de l'écran.
     *
     * @param renderer l'objet chargé de dessiner
     * @param camera   la caméra qui suit le joueur
     */
    public void start(Renderer renderer, Camera camera) {
        new AnimationTimer() {
            private long lastTime = -1;

            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    return;
                }
                double elapsed = (now - lastTime) / 1_000_000_000.0;   // nanosecondes → secondes
                lastTime = now;

                int steps = timestep.advance(elapsed);
                for (int i = 0; i < steps; i++) {
                    update(FixedTimestep.STEP);
                }
                camera.follow(level.getPlayer(), level);
                renderer.render(level, camera);
            }
        }.start();
    }

    /**
     * Fait avancer la partie d'un pas de temps : commandes, déplacements, règles.
     * Hors du jeu proprement dit (menu, pause, écrans de fin), seules les
     * touches de navigation sont lues.
     *
     * @param dt la durée du pas, en secondes
     */
    void update(double dt) {
        try {
            switch (state) {
                case MENU, VICTORY, GAME_OVER -> {
                    if (input.wasPressed(Action.CONFIRM)) {
                        restart();
                    }
                }
                case LEVEL_COMPLETE -> {
                    if (input.wasPressed(Action.CONFIRM)) {
                        nextLevel();
                    }
                }
                case PAUSED -> {
                    if (input.wasPressed(Action.PAUSE)) {
                        setPaused(false);
                    }
                }
                case PLAYING -> {
                    if (input.wasPressed(Action.PAUSE)) {
                        setPaused(true);
                    } else {
                        play(dt);
                    }
                }
            }
        } finally {
            input.endStep();
        }
    }

    private void setPaused(boolean paused) {
        state = paused ? GameState.PAUSED : GameState.PLAYING;
        for (GameListener listener : listeners) {
            listener.onPauseChanged(paused);
        }
    }

    /** Un pas de jeu proprement dit : commandes du joueur, déplacements, règles. */
    private void play(double dt) {
        Player player = level.getPlayer();
        int direction = 0;
        if (input.isActive(Action.LEFT)) {
            direction -= 1;
        }
        if (input.isActive(Action.RIGHT)) {
            direction += 1;
        }
        player.setMoveDirection(direction);
        player.setJumpHeld(input.isActive(Action.JUMP));
        player.setAttackHeld(input.isActive(Action.ATTACK));

        for (GameObject object : List.copyOf(level.getObjects())) {
            object.update(level, dt);
        }

        applyRules(player);
        level.removeMarkedObjects();
    }

    private void applyRules(Player player) {
        resolveSword(player);
        collectItems(player);
        if (player.hasFallenOut(level)) {
            loseLife(player);
            return;
        }
        if (level.touchesTile(player.getBounds(), TileType.SPIKES)) {
            hurtPlayer(player, SPIKE_DAMAGE);
        }
        for (GameObject object : level.getObjects()) {
            if (object instanceof Enemy enemy && !enemy.isRemoved()
                    && enemy.getBounds().intersects(player.getBounds())) {
                hurtPlayer(player, enemy.getContactDamage());
            }
        }
        if (player.isDead()) {
            loseLife(player);
            return;
        }
        if (level.touchesTile(player.getBounds(), TileType.EXIT)) {
            completeLevel(player);
        }
    }

    private void completeLevel(Player player) {
        if (level.getNumber() >= levelCount) {
            state = GameState.VICTORY;
            for (GameListener listener : listeners) {
                listener.onVictory(player.getCoins());
            }
        } else {
            state = GameState.LEVEL_COMPLETE;
            for (GameListener listener : listeners) {
                listener.onLevelCompleted(level.getNumber());
            }
        }
    }

    /**
     * Passe au niveau suivant en conservant les vies et les pièces du joueur.
     *
     * @throws IllegalStateException si le niveau suivant ne peut pas être chargé
     */
    void nextLevel() {
        Player previous = level.getPlayer();
        level = loadLevel(level.getNumber() + 1);
        level.getPlayer().inheritProgress(previous);
        state = GameState.PLAYING;
        listeners.forEach(this::sendCurrentState);
    }

    /**
     * Lance une partie neuve : premier niveau rechargé, vies et cœurs au maximum.
     * Utilisé depuis l'écran titre, après une victoire et après un game over.
     *
     * @throws IllegalStateException si le premier niveau ne peut plus être chargé
     */
    void restart() {
        level = loadLevel(1);
        state = GameState.PLAYING;
        listeners.forEach(this::sendCurrentState);
    }

    /**
     * Charge un niveau en cours de partie. Une erreur ici est un bug (fichier
     * de niveau manquant ou invalide) : elle est remontée en exception non vérifiée.
     */
    private Level loadLevel(int number) {
        try {
            return levels.load(number);
        } catch (InvalidLevelException e) {
            throw new IllegalStateException("Cannot load level " + number, e);
        }
    }

    /**
     * L'épée frappe tout objet {@link Damageable} touché par sa hitbox, quel que
     * soit son type concret (le joueur lui-même excepté).
     */
    private void resolveSword(Player player) {
        Optional<AABB> sword = player.getAttackHitbox();
        if (sword.isEmpty()) {
            return;
        }
        List<Item> released = new ArrayList<>();
        for (GameObject object : level.getObjects()) {
            if (object != player && object instanceof Damageable target
                    && !object.isRemoved() && object.getBounds().intersects(sword.get())) {
                player.strike(target);
                if (target.isDead()) {
                    object.markRemoved();
                    if (object instanceof Crate crate) {
                        crate.getLoot().ifPresent(released::add);
                    }
                }
            }
        }
        released.forEach(level::addObject);
    }

    /**
     * Ramasse les objets touchés par le joueur. Chaque objet applique son propre
     * effet ({@link Item#onPickup}) : le jeu n'a pas besoin de savoir lequel c'est.
     */
    private void collectItems(Player player) {
        boolean picked = false;
        for (GameObject object : level.getObjects()) {
            if (object instanceof Item item && !item.isRemoved()
                    && item.getBounds().intersects(player.getBounds())) {
                item.onPickup(player);
                item.markRemoved();
                picked = true;
            }
        }
        if (picked) {
            notifyHeartsChanged(player);
            for (GameListener listener : listeners) {
                listener.onCoinsChanged(player.getCoins());
            }
        }
    }

    private void hurtPlayer(Player player, int damage) {
        if (player.hurt(damage)) {
            notifyHeartsChanged(player);
        }
    }

    private void loseLife(Player player) {
        player.loseLife();
        notifyLivesChanged(player);
        if (player.getLives() == 0) {
            state = GameState.GAME_OVER;
            listeners.forEach(GameListener::onGameOver);
        } else {
            player.respawn();
            notifyHeartsChanged(player);
        }
    }

    private void notifyHeartsChanged(Player player) {
        for (GameListener listener : listeners) {
            listener.onHeartsChanged(player.getHp(), player.getMaxHp());
        }
    }

    private void notifyLivesChanged(Player player) {
        for (GameListener listener : listeners) {
            listener.onLivesChanged(player.getLives());
        }
    }

    /**
     * Retourne le niveau en cours.
     *
     * @return le niveau
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Retourne l'état de la partie.
     *
     * @return l'état actuel
     */
    public GameState getState() {
        return state;
    }
}
