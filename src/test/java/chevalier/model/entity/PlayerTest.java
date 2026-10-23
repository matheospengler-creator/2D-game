package chevalier.model.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import chevalier.model.AABB;
import chevalier.model.level.InvalidLevelException;
import chevalier.model.level.Level;
import chevalier.model.level.LevelLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Tests de la physique du chevalier : gravité, collisions, saut.
 * Chaque test simule un certain nombre de pas de 1/60 s, sans fenêtre.
 */
class PlayerTest {

    private static final double DT = 1.0 / 60.0;
    private static final int TILE = Level.TILE_SIZE;

    private static Level level(String... lines) throws InvalidLevelException {
        return new LevelLoader().parse(List.of(lines), 1);
    }

    private static void simulate(Level level, int steps) {
        for (int i = 0; i < steps; i++) {
            level.getPlayer().update(level, DT);
        }
    }

    // Une pièce fermée : sol en ligne 3, murs en colonnes 0 et 5.
    private static Level room() throws InvalidLevelException {
        return level(
            "#....#",
            "#.P..#",
            "#....#",
            "######");
    }

    @Test
    void fallsAndLandsOnTheGround() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();

        simulate(level, 60);

        assertTrue(player.isOnGround());
        assertEquals(3 * TILE, player.getY() + player.getHeight(), 1e-9);
        assertEquals(0, player.getVy());
    }

    @Test
    void rightWallStopsTheRun() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        player.setMoveDirection(1);

        simulate(level, 120);

        assertEquals(5 * TILE, player.getX() + player.getWidth(), 1e-9);
    }

    @Test
    void leftWallStopsTheRun() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        player.setMoveDirection(-1);

        simulate(level, 120);

        assertEquals(TILE, player.getX(), 1e-9);
    }

    @Test
    void jumpLeavesTheGround() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        simulate(level, 60);

        player.setJumpHeld(true);
        simulate(level, 1);

        assertTrue(player.getVy() < 0);
        assertFalse(player.isOnGround());
    }

    @Test
    void cannotJumpInMidAir() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        simulate(level, 1);                 // encore en l'air
        assertFalse(player.isOnGround());

        player.setJumpHeld(true);
        player.setJumpHeld(false);          // appui bref en l'air
        simulate(level, 1);

        assertTrue(player.getVy() > 0);     // continue de tomber
    }

    @Test
    void coyoteTimeAllowsAJumpRightAfterLeavingALedge() throws InvalidLevelException {
        Level level = ledge();
        Player player = level.getPlayer();
        simulate(level, 30);
        player.setMoveDirection(1);
        while (player.isOnGround()) {
            simulate(level, 1);              // court jusqu'à quitter le bord
        }

        simulate(level, 3);                  // 3 pas (0,05 s) dans le vide
        player.setJumpHeld(true);
        simulate(level, 1);

        assertTrue(player.getVy() < 0);      // le saut est quand même parti
    }

    @Test
    void noJumpLongAfterLeavingALedge() throws InvalidLevelException {
        Level level = ledge();
        Player player = level.getPlayer();
        simulate(level, 30);
        player.setMoveDirection(1);
        while (player.isOnGround()) {
            simulate(level, 1);
        }

        simulate(level, 12);                 // 0,2 s : trop tard
        player.setJumpHeld(true);
        simulate(level, 1);

        assertTrue(player.getVy() > 0);
    }

    @Test
    void jumpPressedJustBeforeLandingIsBuffered() throws InvalidLevelException {
        int fallSteps = stepsToLand(tallRoom());

        Level level = tallRoom();
        Player player = level.getPlayer();
        simulate(level, fallSteps - 3);      // 3 pas avant l'atterrissage
        player.setJumpHeld(true);
        simulate(level, 4);

        assertTrue(player.getVy() < 0);      // a sauté dès l'atterrissage
    }

    @Test
    void jumpPressedLongBeforeLandingIsForgotten() throws InvalidLevelException {
        int fallSteps = stepsToLand(tallRoom());
        assertTrue(fallSteps > 12);

        Level level = tallRoom();
        Player player = level.getPlayer();
        simulate(level, fallSteps - 12);     // 0,2 s avant l'atterrissage : trop tôt
        player.setJumpHeld(true);
        simulate(level, 20);

        assertTrue(player.isOnGround());     // aucun saut n'est parti
    }

    // Une pièce haute : la chute depuis le départ dure environ 0,4 s.
    private static Level tallRoom() throws InvalidLevelException {
        return level(
            "#.P..#",
            "#....#",
            "#....#",
            "#....#",
            "#....#",
            "######");
    }

    /** Nombre de pas pour que le joueur, lâché à son point de départ, touche le sol. */
    private static int stepsToLand(Level level) {
        int steps = 0;
        while (!level.getPlayer().isOnGround()) {
            simulate(level, 1);
            steps++;
        }
        return steps;
    }

    // Une plateforme qui s'arrête en colonne 3, puis du vide.
    private static Level ledge() throws InvalidLevelException {
        return level(
            "..........",
            "..........",
            "P.........",
            "###.......",
            "..........",
            "..........",
            "..........",
            "..........");
    }

    @Test
    void holdingJumpDoesNotBounceForever() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        simulate(level, 60);

        player.setJumpHeld(true);
        simulate(level, 180);               // largement de quoi retomber

        assertTrue(player.isOnGround());
    }

    @Test
    void jumpIsLimitedByTheCeiling() throws InvalidLevelException {
        Level level = level(
            "####",
            "#..#",
            "#P.#",
            "####");
        Player player = level.getPlayer();
        simulate(level, 30);

        player.setJumpHeld(true);
        double highest = player.getY();
        for (int i = 0; i < 60; i++) {
            simulate(level, 1);
            highest = Math.min(highest, player.getY());
        }

        assertEquals(TILE, highest, 1e-9);  // la tête bute sous la ligne 0
    }

    @Test
    void shortPressGivesALowerJumpThanLongPress() throws InvalidLevelException {
        assertTrue(jumpHeight(3) < jumpHeight(60));
    }

    private static double jumpHeight(int heldSteps) throws InvalidLevelException {
        Level level = level(
            "#....#",
            "#....#",
            "#....#",
            "#....#",
            "#.P..#",
            "######");
        Player player = level.getPlayer();
        simulate(level, 30);
        double ground = player.getY();

        player.setJumpHeld(true);
        double highest = ground;
        for (int i = 0; i < 90; i++) {
            if (i == heldSteps) {
                player.setJumpHeld(false);
            }
            simulate(level, 1);
            highest = Math.min(highest, player.getY());
        }
        return ground - highest;
    }

    @Test
    void fallingBelowTheLevelIsDetected() throws InvalidLevelException {
        Level level = level(
            "..P..",
            ".....");                        // pas de sol : chute dans le vide
        Player player = level.getPlayer();
        assertFalse(player.hasFallenOut(level));

        simulate(level, 60);

        assertTrue(player.hasFallenOut(level));
    }

    @Test
    void respawnRestoresPositionSpeedAndHearts() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        double startX = player.getX();
        double startY = player.getY();
        player.setMoveDirection(1);
        simulate(level, 30);
        player.hurt(2);

        player.respawn();

        assertEquals(startX, player.getX());
        assertEquals(startY, player.getY());
        assertEquals(0, player.getVx());
        assertEquals(0, player.getVy());
        assertEquals(Player.MAX_HEARTS, player.getHp());
        assertFalse(player.isInvincible());
    }

    @Test
    void hitMakesThePlayerInvincibleForASecond() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();

        assertTrue(player.hurt(1));
        assertTrue(player.isInvincible());
        assertFalse(player.hurt(1));          // second coup ignoré
        assertEquals(Player.MAX_HEARTS - 1, player.getHp());

        simulate(level, 61);                   // un peu plus d'une seconde

        assertFalse(player.isInvincible());
        assertTrue(player.hurt(1));
        assertEquals(Player.MAX_HEARTS - 2, player.getHp());
    }

    @Test
    void livesNeverGoBelowZero() {
        Player player = new Player(0, 0);
        for (int i = 0; i < Player.START_LIVES + 2; i++) {
            player.loseLife();
        }
        assertEquals(0, player.getLives());
    }

    @Test
    void fallSpeedIsCapped() throws InvalidLevelException {
        Level level = level(
            "P", ".", ".", ".", ".", ".", ".", ".", ".", ".",
            ".", ".", ".", ".", ".", ".", ".", ".", ".", ".");
        Player player = level.getPlayer();

        simulate(level, 40);

        assertEquals(Entity.MAX_FALL_SPEED, player.getVy());
    }

    @Test
    void facesTheLastDirectionItRan() {
        Player player = new Player(0, 0);
        assertEquals(1, player.getFacing());

        player.setMoveDirection(-1);
        player.setMoveDirection(0);

        assertEquals(-1, player.getFacing());
    }

    @Test
    void swordHitboxIsInFrontOfThePlayer() {
        Player player = new Player(100, 0);

        player.setAttackHeld(true);
        AABB right = player.getAttackHitbox().orElseThrow();
        assertEquals(100 + Player.WIDTH, right.x());

        Player other = new Player(100, 0);
        other.setMoveDirection(-1);
        other.setAttackHeld(true);
        AABB left = other.getAttackHitbox().orElseThrow();
        assertEquals(100 - Player.SWORD_REACH, left.x());
    }

    @Test
    void noHitboxWhenNotAttacking() {
        assertTrue(new Player(0, 0).getAttackHitbox().isEmpty());
    }

    @Test
    void swingLastsOnlyAShortTime() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        player.setAttackHeld(true);
        assertTrue(player.isAttacking());

        simulate(level, 10);                  // 0,17 s > durée du coup

        assertFalse(player.isAttacking());
    }

    @Test
    void holdingTheAttackKeyStrikesOnlyOnce() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        player.setAttackHeld(true);
        simulate(level, 30);

        player.setAttackHeld(true);           // toujours enfoncée

        assertFalse(player.isAttacking());
    }

    @Test
    void cannotAttackAgainDuringTheCooldown() throws InvalidLevelException {
        Level level = room();
        Player player = level.getPlayer();
        player.setAttackHeld(true);
        player.setAttackHeld(false);
        simulate(level, 12);                  // coup fini, mais délai pas écoulé

        player.setAttackHeld(true);
        assertFalse(player.isAttacking());

        player.setAttackHeld(false);
        simulate(level, 12);                  // délai écoulé
        player.setAttackHeld(true);
        assertTrue(player.isAttacking());
    }

    @Test
    void sameTargetIsHitOnlyOncePerSwing() {
        Player player = new Player(0, 0);
        Player target = new Player(50, 0);    // n'importe quel Damageable

        player.setAttackHeld(true);

        assertTrue(player.strike(target));
        assertFalse(player.strike(target));
        assertEquals(Player.MAX_HEARTS - 1, target.getHp());
    }

    @Test
    void cannotStrikeWithoutAttacking() {
        Player player = new Player(0, 0);
        Player target = new Player(50, 0);

        assertFalse(player.strike(target));
        assertEquals(Player.MAX_HEARTS, target.getHp());
    }

    @Test
    void damageAndHealingStayWithinBounds() {
        Player player = new Player(0, 0);

        player.takeDamage(1);
        assertEquals(player.getMaxHp() - 1, player.getHp());

        player.heal(10);
        assertEquals(player.getMaxHp(), player.getHp());

        player.takeDamage(100);
        assertEquals(0, player.getHp());
        assertTrue(player.isDead());
    }

    @Test
    void negativeAmountsAreRejected() {
        Player player = new Player(0, 0);
        assertThrows(IllegalArgumentException.class, () -> player.takeDamage(-1));
        assertThrows(IllegalArgumentException.class, () -> player.heal(-1));
    }
}
