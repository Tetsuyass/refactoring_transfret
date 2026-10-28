package fr.efrei.transport;

import java.util.List;

public interface FlotteRepository {
    List<Vehicule> tousLesVehicules();
    void initialiser();
}
