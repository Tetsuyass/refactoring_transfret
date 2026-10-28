package fr.efrei.transport;

import java.util.ArrayList;
import java.util.List;

public class FlotteRepositoryMemoire implements FlotteRepository {

    private List<Vehicule> vehicules = new ArrayList<>();

    @Override
    public List<Vehicule> tousLesVehicules() {
        return vehicules;
    }

    @Override
    public void initialiser() {
        vehicules = new ArrayList<>();
        vehicules.add(new Camion("CAM-001", 15000, false));
        vehicules.add(new Camion("CAM-002", 15000, true));
        vehicules.add(new CamionFrigorifique("CAM-F-001", 12000));
        vehicules.add(new Train("TRN-001", 50000));
        vehicules.add(new Bateau("BAT-001", 100000, true));
        vehicules.add(new Bateau("BAT-002", 100000, false));
        vehicules.add(new Avion("AVI-001", 30000));
    }
}
