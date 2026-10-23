package chevalier.controller;

/**
 * Observateur des événements de la partie (patron Observer).
 *
 * <p>Le {@link Game} notifie ses abonnés sans savoir qui ils sont : le HUD,
 * un futur gestionnaire de son, ou un faux abonné dans les tests.</p>
 */
public interface GameListener {

    /** Le jeu affiche l'écran titre et attend que le joueur lance la partie. */
    void onTitleScreen();

    /**
     * La partie vient d'être mise en pause ou de reprendre.
     *
     * @param paused {@code true} si la partie est en pause
     */
    void onPauseChanged(boolean paused);

    /**
     * Un niveau commence (début de partie ou nouvelle partie).
     *
     * @param levelNumber le numéro du niveau
     */
    void onLevelStarted(int levelNumber);

    /**
     * Les cœurs du joueur ont changé (coup reçu, soin, réapparition).
     *
     * @param hearts    cœurs restants
     * @param maxHearts cœurs maximum
     */
    void onHeartsChanged(int hearts, int maxHearts);

    /**
     * Le nombre de vies du joueur a changé.
     *
     * @param lives vies restantes
     */
    void onLivesChanged(int lives);

    /**
     * Le nombre de pièces ramassées a changé.
     *
     * @param coins pièces ramassées depuis le début de la partie
     */
    void onCoinsChanged(int coins);

    /**
     * Le joueur a atteint la sortie du niveau.
     *
     * @param levelNumber le numéro du niveau terminé
     */
    void onLevelCompleted(int levelNumber);

    /**
     * Le joueur a terminé le dernier niveau : la partie est gagnée.
     *
     * @param coins pièces ramassées sur toute la partie
     */
    void onVictory(int coins);

    /** Le joueur n'a plus de vies : la partie est perdue. */
    void onGameOver();
}
