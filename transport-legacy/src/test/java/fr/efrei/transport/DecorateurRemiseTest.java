package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DecorateurRemiseTest {

    @Test
    void testCalculer() {
        Prix base = new Prix() {
            @Override
            public double calculer() {
                return 100;
            }
        };
        PolitiqueRemise remise = new PolitiqueRemise() {
            @Override
            public double appliquerRemise(double prix) {
                return prix * 0.80;
            }
        };
        DecorateurRemise decorateur = new DecorateurRemise(base, remise);
        assertEquals(80, decorateur.calculer(), 0.01);
    }
}
