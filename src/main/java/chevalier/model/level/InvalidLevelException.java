package chevalier.model.level;

/**
 * Levée quand un fichier de niveau est introuvable ou mal formé.
 *
 * <p>Exception vérifiée (« checked ») : l'appelant est obligé de prévoir le cas
 * où un niveau ne peut pas être chargé.</p>
 */
public class InvalidLevelException extends Exception {

    /**
     * Crée l'exception avec un message explicatif.
     *
     * @param message la description du problème
     */
    public InvalidLevelException(String message) {
        super(message);
    }

    /**
     * Crée l'exception avec un message et la cause d'origine.
     *
     * @param message la description du problème
     * @param cause   l'exception qui a provoqué celle-ci
     */
    public InvalidLevelException(String message, Throwable cause) {
        super(message, cause);
    }
}
