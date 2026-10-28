package fr.efrei.transport;

public class DelaiAir implements DelaiTransport {

    private static final int DISTANCE_LONG = 3000;

    @Override
    public int calculerDelai(int distance, boolean urgent) {
        int jours = 1;
        if (distance > DISTANCE_LONG) jours = 2;
        if (urgent) jours = 1;
        return jours;
    }
}
