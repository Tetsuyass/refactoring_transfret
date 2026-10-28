package fr.efrei.transport;

public class DelaiRoute implements DelaiTransport {

    private static final int VITESSE_URGENT = 18;
    private static final int VITESSE_NORMAL = 9;
    private static final double VITESSE_KMH = 70.0;

    @Override
    public int calculerDelai(int distance, boolean urgent) {
        double heures = distance / VITESSE_KMH;
        if (urgent) {
            return (int) Math.ceil(heures / VITESSE_URGENT);
        } else {
            return (int) Math.ceil(heures / VITESSE_NORMAL);
        }
    }
}
