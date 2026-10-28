package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupplementDangereuxTest {

    @Test
    void testCalculer() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        SupplementDangereux supplement = new SupplementDangereux(base);
        assertEquals(130, supplement.calculer(), 0.01);
    }
}
