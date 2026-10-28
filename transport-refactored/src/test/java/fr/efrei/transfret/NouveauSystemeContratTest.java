package fr.efrei.transfret;

import fr.efrei.transport.contrat.ContratTransportTest;
import fr.efrei.transport.contrat.SystemeTransport;
import org.junit.jupiter.api.Disabled;

class NouveauSystemeContratTest extends ContratTransportTest {

    @Override
    protected SystemeTransport creerSysteme() {
        return new NouveauSystemeAdapter();
    }
}
