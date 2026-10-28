package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemiseEntrepriseTest {

    @Test
    void testAppliquerRemise_AuDessusSeuil() {
        RemiseEntreprise remise = new RemiseEntreprise();
        double prix = remise.appliquerRemise(6000);
        assertEquals(6000 * 0.85, prix, 0.01);
    }

    @Test
    void testAppliquerRemise_EnDessousSeuil() {
        RemiseEntreprise remise = new RemiseEntreprise();
        double prix = remise.appliquerRemise(4000);
        assertEquals(4000 * 0.90, prix, 0.01);
    }

    @Test
    void testAppliquerRemise_AuSeuil() {
        RemiseEntreprise remise = new RemiseEntreprise();
        double prix = remise.appliquerRemise(5000);
        assertEquals(5000 * 0.90, prix, 0.01);
    }
}
