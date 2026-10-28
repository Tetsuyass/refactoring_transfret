package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupplementUrgenceTest {

    @Test
    void testCalculer() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        SupplementUrgence supplement = new SupplementUrgence(base);
        assertEquals(150, supplement.calculer(), 0.01);
    }
}
