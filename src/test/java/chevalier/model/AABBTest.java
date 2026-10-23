package chevalier.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AABBTest {

    private final AABB box = new AABB(0, 0, 10, 10);

    @Test
    void overlappingBoxesIntersect() {
        assertTrue(box.intersects(new AABB(5, 5, 10, 10)));
    }

    @Test
    void distantBoxesDoNotIntersect() {
        assertFalse(box.intersects(new AABB(20, 20, 5, 5)));
    }

    @Test
    void boxesTouchingByAnEdgeDoNotIntersect() {
        assertFalse(box.intersects(new AABB(10, 0, 5, 5)));
    }

    @Test
    void boxInsideAnotherIntersects() {
        assertTrue(box.intersects(new AABB(2, 2, 3, 3)));
    }

    @Test
    void nonPositiveSizeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AABB(0, 0, 0, 5));
        assertThrows(IllegalArgumentException.class, () -> new AABB(0, 0, 5, -1));
    }
}
