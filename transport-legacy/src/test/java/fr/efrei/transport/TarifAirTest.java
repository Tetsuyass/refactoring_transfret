package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TarifAirTest {

    @Test
    void testCalculerTarifBase() {
        TarifAir tarif = new TarifAir();
        double prix = tarif.calculerTarifBase(100, 50);
        assertEquals(100 * 2.5 + 50 * 0.15, prix, 0.01);
    }

    @Test
    void testGetMinimumFacturation() {
        TarifAir tarif = new TarifAir();
        assertEquals(0, tarif.getMinimumFacturation());
    }

    @Test
    void testGetFacteurVolume() {
        TarifAir tarif = new TarifAir();
        assertEquals(0.001, tarif.getFacteurVolume());
    }
}
