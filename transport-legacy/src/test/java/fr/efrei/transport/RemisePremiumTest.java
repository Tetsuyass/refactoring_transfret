package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemisePremiumTest {

    @Test
    void testAppliquerRemise() {
        RemisePremium remise = new RemisePremium();
        assertEquals(100 * 0.80, remise.appliquerRemise(100), 0.01);
        assertEquals(5000 * 0.80, remise.appliquerRemise(5000), 0.01);
        assertEquals(10000 * 0.80, remise.appliquerRemise(10000), 0.01);
    }
}
