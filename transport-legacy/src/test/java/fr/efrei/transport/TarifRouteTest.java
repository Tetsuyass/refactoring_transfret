package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TarifRouteTest {

    @Test
    void testCalculerTarifBase() {
        TarifRoute tarif = new TarifRoute();
        double prix = tarif.calculerTarifBase(100, 50);
        assertEquals(100 * 1.2 + 50 * 0.10, prix, 0.01);
    }

    @Test
    void testGetMinimumFacturation() {
        TarifRoute tarif = new TarifRoute();
        assertEquals(0, tarif.getMinimumFacturation());
    }

    @Test
    void testGetFacteurVolume() {
        TarifRoute tarif = new TarifRoute();
        assertEquals(0, tarif.getFacteurVolume());
    }
}
