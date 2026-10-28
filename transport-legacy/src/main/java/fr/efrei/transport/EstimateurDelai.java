package fr.efrei.transport;

import java.util.Map;

public class EstimateurDelai {

    private final Map<ModeTransport, DelaiTransport> delaisParMode;

    public EstimateurDelai(Map<ModeTransport, DelaiTransport> delaisParMode) {
        this.delaisParMode = delaisParMode;
    }

    public int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        DelaiTransport delaiTransport = delaisParMode.get(mode);
        if (delaiTransport == null) {
            throw new IllegalArgumentException("Mode inconnu : " + mode);
        }
        int jours = delaiTransport.calculerDelai(distance, urgent);
        if (jours < 1) jours = 1;
        return jours;
    }
}
