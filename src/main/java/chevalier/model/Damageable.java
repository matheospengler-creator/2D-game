package chevalier.model;

/**
 * Capacité de subir des dégâts.
 *
 * <p>Implémentée par des objets de branches différentes de la hiérarchie
 * (les entités et les caisses) : l'attaque à l'épée vise tout ce qui est
 * {@code Damageable}, sans connaître son type concret.</p>
 */
public interface Damageable {

    /**
     * Inflige des dégâts à cet objet.
     *
     * @param amount le nombre de points de vie retirés, positif ou nul
     * @throws IllegalArgumentException si {@code amount} est négatif
     */
    void takeDamage(int amount);

    /**
     * Indique si l'objet n'a plus de points de vie.
     *
     * @return {@code true} si l'objet est détruit / mort
     */
    boolean isDead();
}
