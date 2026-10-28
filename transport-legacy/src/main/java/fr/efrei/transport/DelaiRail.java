package fr.efrei.transport;

public class DelaiRail implements DelaiTransport {

    private static final double VITESSE_KMJ = 1440.0;

    @Override
    public int calculerDelai(int distance, boolean urgent) {
        int jours = (int) Math.ceil(distance / VITESSE_KMJ) + 1;
        if (urgent) jours = jours - 1;
        if (jours < 1) jours = 1;
        return jours;
    }
}
