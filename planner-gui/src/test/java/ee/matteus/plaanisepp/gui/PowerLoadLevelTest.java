package ee.matteus.plaanisepp.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PowerLoadLevelTest {
    @Test
    void usesDocumentedUtilizationThresholds() {
        assertEquals(PowerLoadLevel.NORMAL, PowerLoadLevel.from(0, 3500));
        assertEquals(PowerLoadLevel.NORMAL, PowerLoadLevel.from(2449, 3500));
        assertEquals(PowerLoadLevel.WARNING, PowerLoadLevel.from(2450, 3500));
        assertEquals(PowerLoadLevel.HIGH, PowerLoadLevel.from(3150, 3500));
        assertEquals(PowerLoadLevel.HIGH, PowerLoadLevel.from(3500, 3500));
        assertEquals(PowerLoadLevel.OVERLOADED, PowerLoadLevel.from(3501, 3500));
    }

    @Test
    void handlesZeroCapacityWithoutDivisionByZero() {
        assertEquals(PowerLoadLevel.NORMAL, PowerLoadLevel.from(0, 0));
        assertEquals(PowerLoadLevel.OVERLOADED, PowerLoadLevel.from(1, 0));
    }
}
