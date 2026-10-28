package fr.efrei.transport;

import java.util.List;

public interface TarifTransport {
    double calculerTarifBase(int distance, double poids);
    double getMinimumFacturation();
    double getFacteurVolume();
}
