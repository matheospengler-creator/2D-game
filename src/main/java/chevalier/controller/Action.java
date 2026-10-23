package chevalier.controller;

/**
 * Les actions que le joueur peut déclencher, indépendamment des touches utilisées.
 *
 * <p>La logique du jeu raisonne en actions (« sauter ») et non en touches
 * (« espace ») : on peut ainsi associer plusieurs touches à une même action.</p>
 */
public enum Action {
    /** Courir vers la gauche. */
    LEFT,
    /** Courir vers la droite. */
    RIGHT,
    /** Sauter. */
    JUMP,
    /** Donner un coup d'épée. */
    ATTACK,
    /** Mettre le jeu en pause. */
    PAUSE,
    /** Valider (lancer la partie, niveau suivant, recommencer). */
    CONFIRM
}
