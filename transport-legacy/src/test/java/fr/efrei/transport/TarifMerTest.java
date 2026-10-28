package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TarifMerTest {

    @Test
    void testCalculerTarifBase_AuDessusMinimum() {
        TarifMer tarif = new TarifMer();
        double prix = tarif.calculerTarifBase(100, 50);
        assertEquals(100 * 0.4 + 50 * 0.05, prix, 0.01);
    }

    @Test
    void testCalculerTarifBase_EnDessousMinimum() {
        TarifMer tarif = new TarifMer();
        double prix = tarif.calculerTarifBase(10, 10);
        assertEquals(167, prix, 0.01);
    }

    @Test
    void testGetMinimumFacturation() {
        TarifMer tarif = new TarifMer();
        assertEquals(167, tarif.getMinimumFacturation());
    }

    @Test
    void testGetFacteurVolume() {
        TarifMer tarif = new TarifMer();
        assertEquals(0, tarif.getFacteurVolume());
    }
}
