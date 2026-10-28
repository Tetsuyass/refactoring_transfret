package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecorateurAssuranceTest {

    @Test
    void testCalculer_AuDessusMinimum() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        DecorateurAssurance decorateur = new DecorateurAssurance(base, 1000);
        assertEquals(100 + 1000 * 0.02, decorateur.calculer(), 0.01);
    }

    @Test
    void testCalculer_EnDessousMinimum() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        DecorateurAssurance decorateur = new DecorateurAssurance(base, 100);
        assertEquals(100 + 15, decorateur.calculer(), 0.01);
    }
}
