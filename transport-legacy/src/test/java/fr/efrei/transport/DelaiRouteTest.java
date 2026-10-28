package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DelaiRouteTest {

    @Test
    void testCalculerDelai_Normal() {
        DelaiRoute delai = new DelaiRoute();
        int jours = delai.calculerDelai(140, false);
        assertEquals((int) Math.ceil(140 / 70.0 / 9), jours);
    }

    @Test
    void testCalculerDelai_Urgent() {
        DelaiRoute delai = new DelaiRoute();
        int jours = delai.calculerDelai(140, true);
        assertEquals((int) Math.ceil(140 / 70.0 / 18), jours);
    }
}
