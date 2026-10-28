package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DelaiRailTest {

    @Test
    void testCalculerDelai_Normal() {
        DelaiRail delai = new DelaiRail();
        int jours = delai.calculerDelai(1440, false);
        assertEquals(2, jours);
    }

    @Test
    void testCalculerDelai_Urgent() {
        DelaiRail delai = new DelaiRail();
        int jours = delai.calculerDelai(1440, true);
        assertEquals(1, jours);
    }

    @Test
    void testCalculerDelai_MinimumUnJour() {
        DelaiRail delai = new DelaiRail();
        int jours = delai.calculerDelai(100, true);
        assertEquals(1, jours);
    }
}
