package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TarifRailTest {

    @Test
    void testCalculerTarifBase_AuDessusMinimum() {
        TarifRail tarif = new TarifRail();
        double prix = tarif.calculerTarifBase(100, 50);
        assertEquals(100 * 0.8 + 50 * 0.07, prix, 0.01);
    }

    @Test
    void testCalculerTarifBase_EnDessousMinimum() {
        TarifRail tarif = new TarifRail();
        double prix = tarif.calculerTarifBase(10, 10);
        assertEquals(150, prix, 0.01);
    }

    @Test
    void testGetMinimumFacturation() {
        TarifRail tarif = new TarifRail();
        assertEquals(150, tarif.getMinimumFacturation());
    }

    @Test
    void testGetFacteurVolume() {
        TarifRail tarif = new TarifRail();
        assertEquals(0, tarif.getFacteurVolume());
    }
}
