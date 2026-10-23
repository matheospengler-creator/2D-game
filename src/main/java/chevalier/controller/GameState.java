package chevalier.controller;

/** Les états possibles d'une partie. */
public enum GameState {
    /** L'écran titre : on attend que le joueur lance la partie. */
    MENU,
    /** La partie est en cours. */
    PLAYING,
    /** La partie est en pause. */
    PAUSED,
    /** Le joueur a atteint la sortie d'un niveau (il en reste d'autres). */
    LEVEL_COMPLETE,
    /** Le joueur a terminé le dernier niveau : la partie est gagnée. */
    VICTORY,
    /** Le joueur n'a plus de vies. */
    GAME_OVER
}
