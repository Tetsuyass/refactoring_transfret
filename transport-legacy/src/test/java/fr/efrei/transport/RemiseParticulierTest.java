package fr.efrei.transport;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RemiseParticulierTest {

    @Test
    void testAppliquerRemise() {
        RemiseParticulier remise = new RemiseParticulier();
        assertEquals(100, remise.appliquerRemise(100), 0.01);
        assertEquals(5000, remise.appliquerRemise(5000), 0.01);
        assertEquals(10000, remise.appliquerRemise(10000), 0.01);
    }
}
