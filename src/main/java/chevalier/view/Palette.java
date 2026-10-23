package chevalier.view;

import javafx.scene.paint.Color;

/**
 * Les couleurs du jeu, rassemblées ici pour garder un rendu cohérent
 * et facile à retoucher.
 */
public final class Palette {

    private Palette() {
        // constantes uniquement
    }

    // Décor lointain
    /** Haut du ciel. */
    public static final Color SKY_TOP = Color.web("#2a1f4a");
    /** Horizon. */
    public static final Color SKY_HORIZON = Color.web("#d9794f");
    /** Lune. */
    public static final Color MOON = Color.web("#fff2d0");
    /** Collines. */
    public static final Color HILLS = Color.web("#3a2446");

    // Tuiles
    /** Terre. */
    public static final Color DIRT = Color.web("#6b4a2f");
    /** Herbe. */
    public static final Color GRASS = Color.web("#5aa94a");
    /** Piques. */
    public static final Color SPIKE = Color.web("#c9cdd6");
    /** Porte de sortie. */
    public static final Color DOOR = Color.web("#73502b");

    // Objets
    /** Or (pièces, porte, HUD). */
    public static final Color GOLD = Color.web("#f4c44a");
    /** Contour des pièces. */
    public static final Color GOLD_EDGE = Color.web("#8a5e12");
    /** Cœur. */
    public static final Color HEART = Color.web("#e5484d");
    /** Bois de caisse. */
    public static final Color CRATE = Color.web("#9a6a3a");
    /** Renforts de caisse. */
    public static final Color CRATE_DARK = Color.web("#6b4422");

    // Chevalier
    /** Tunique du chevalier. */
    public static final Color KNIGHT_TUNIC = Color.web("#3f6bd0");
    /** Heaume. */
    public static final Color ARMOR = Color.web("#c7d2e6");
    /** Fente du heaume, jambes du garde. */
    public static final Color VISOR = Color.web("#2a2f3a");
    /** Jambes du chevalier. */
    public static final Color LEGS = Color.web("#3a4a6b");
    /** Lame de l'épée. */
    public static final Color BLADE = Color.web("#dfe6f2");

    // Ennemis
    /** Slime. */
    public static final Color SLIME = Color.web("#5ac46a");
    /** Yeux du slime. */
    public static final Color SLIME_EYE = Color.web("#15301a");
    /** Corps de la chauve-souris. */
    public static final Color BAT_BODY = Color.web("#2a2140");
    /** Ailes de la chauve-souris. */
    public static final Color BAT_WING = Color.web("#5b4a86");
    /** Casque du garde. */
    public static final Color ARMOR_EDGE = Color.web("#8a93a8");
    /** Tunique du garde. */
    public static final Color GUARD_TUNIC = Color.web("#8c2f35");
    /** Bouclier du garde. */
    public static final Color GUARD_SHIELD = Color.web("#5a3a1f");
}
