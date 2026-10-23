package chevalier.controller;

/**
 * Découpe le temps réel écoulé en pas de simulation de durée fixe.
 *
 * <p>Le temps écoulé entre deux images s'accumule ; on en retire des tranches
 * de {@link #STEP} secondes, chacune donnant lieu à une mise à jour de la
 * logique. La physique est ainsi identique quelle que soit la fréquence de
 * l'écran.</p>
 */
public class FixedTimestep {

    /** Durée d'un pas de simulation, en secondes (60 mises à jour par seconde). */
    public static final double STEP = 1.0 / 60.0;

    /**
     * Temps écoulé maximal pris en compte pour une image, en secondes.
     * Évite des centaines de mises à jour d'un coup après un gel (fenêtre
     * déplacée, PC qui rame…).
     */
    public static final double MAX_FRAME_TIME = 0.25;

    private double accumulator;

    /**
     * Ajoute le temps écoulé depuis la dernière image et retourne le nombre de
     * pas de simulation à exécuter.
     *
     * @param elapsedSeconds le temps réel écoulé, en secondes
     * @return le nombre de mises à jour de durée {@link #STEP} à effectuer
     */
    public int advance(double elapsedSeconds) {
        accumulator += Math.min(Math.max(elapsedSeconds, 0), MAX_FRAME_TIME);
        int steps = 0;
        while (accumulator >= STEP) {
            accumulator -= STEP;
            steps++;
        }
        return steps;
    }
}
