package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DelaiMerTest {

    @Test
    void testCalculerDelai() {
        DelaiMer delai = new DelaiMer();
        int jours = delai.calculerDelai(720, false);
        assertEquals(3, jours);
    }

    @Test
    void testCalculerDelai_Urgent() {
        DelaiMer delai = new DelaiMer();
        int jours = delai.calculerDelai(720, true);
        assertEquals(3, jours);
    }
}
