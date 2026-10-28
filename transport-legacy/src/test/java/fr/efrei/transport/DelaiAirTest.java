package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DelaiAirTest {

    @Test
    void testCalculerDelai_Court() {
        DelaiAir delai = new DelaiAir();
        int jours = delai.calculerDelai(1000, false);
        assertEquals(1, jours);
    }

    @Test
    void testCalculerDelai_Long() {
        DelaiAir delai = new DelaiAir();
        int jours = delai.calculerDelai(4000, false);
        assertEquals(2, jours);
    }

    @Test
    void testCalculerDelai_Urgent() {
        DelaiAir delai = new DelaiAir();
        int jours = delai.calculerDelai(4000, true);
        assertEquals(1, jours);
    }
}
