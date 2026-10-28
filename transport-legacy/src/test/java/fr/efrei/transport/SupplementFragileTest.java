package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupplementFragileTest {

    @Test
    void testCalculer() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        SupplementFragile supplement = new SupplementFragile(base);
        assertEquals(115, supplement.calculer(), 0.01);
    }
}
