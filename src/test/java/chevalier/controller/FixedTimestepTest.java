package chevalier.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FixedTimestepTest {

    private final FixedTimestep timestep = new FixedTimestep();

    @Test
    void oneStepPerSixtiethOfASecond() {
        assertEquals(1, timestep.advance(1.0 / 60.0));
    }

    @Test
    void slowFrameRunsSeveralSteps() {
        assertEquals(2, timestep.advance(2.0 / 60.0 + 0.001));
    }

    @Test
    void fastFramesAccumulateUntilAStepIsReached() {
        assertEquals(0, timestep.advance(1.0 / 120.0));
        assertEquals(1, timestep.advance(1.0 / 120.0 + 0.001));
    }

    @Test
    void oneSecondGivesSixtySteps() {
        int total = 0;
        for (int frame = 0; frame < 144; frame++) {   // un écran 144 Hz
            total += timestep.advance(1.0 / 144.0);
        }
        assertEquals(60, total, 1);
    }

    @Test
    void longFreezeIsCapped() {
        int maxSteps = (int) (FixedTimestep.MAX_FRAME_TIME / FixedTimestep.STEP);
        assertEquals(maxSteps, timestep.advance(10.0), 1);
    }

    @Test
    void negativeTimeIsIgnored() {
        assertEquals(0, timestep.advance(-1.0));
    }
}
