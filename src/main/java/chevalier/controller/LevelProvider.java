package chevalier.controller;

import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;

/**
 * Sait fournir un niveau neuf à partir de son numéro.
 *
 * <p>Le {@link Game} s'en sert pour démarrer, recommencer et (plus tard)
 * passer au niveau suivant, sans savoir d'où viennent les niveaux : fichiers
 * du jeu en production, quelques lignes de texte dans les tests.</p>
 */
@FunctionalInterface
public interface LevelProvider {

    /**
     * Charge un niveau tout neuf (ennemis et joueur à leur position de départ).
     *
     * @param number le numéro du niveau, à partir de 1
     * @return le niveau chargé
     * @throws InvalidLevelException si le niveau est introuvable ou mal formé
     */
    Level load(int number) throws InvalidLevelException;
}
