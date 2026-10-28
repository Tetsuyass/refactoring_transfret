package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupplementRefrigereTest {

    @Test
    void testCalculer() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        SupplementRefrigere supplement = new SupplementRefrigere(base);
        assertEquals(120, supplement.calculer(), 0.01);
    }
}
