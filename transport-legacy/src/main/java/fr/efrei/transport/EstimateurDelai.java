package fr.efrei.transport;

public class EstimateurDelai {

    private static final int VITESSE_ROUTE_URGENT = 18;
    private static final int VITESSE_ROUTE_NORMAL = 9;
    private static final double VITESSE_ROUTE_KMH = 70.0;
    private static final double VITESSE_RAIL_KMJ = 1440.0;
    private static final double VITESSE_MER_KMJ = 720.0;
    private static final int DISTANCE_AIR_LONG = 3000;

    public int calculerDelai(int distance, ModeTransport mode, boolean urgent) {
        int jours = 0;
        switch (mode) {
            case ROUTE:
                double heures = distance / VITESSE_ROUTE_KMH;
                if (urgent) {
                    jours = (int) Math.ceil(heures / VITESSE_ROUTE_URGENT);
                } else {
                    jours = (int) Math.ceil(heures / VITESSE_ROUTE_NORMAL);
                }
                break;
            case RAIL:
                jours = (int) Math.ceil(distance / VITESSE_RAIL_KMJ) + 1;
                if (urgent) jours = jours - 1;
                break;
            case MER:
                jours = (int) Math.ceil(distance / VITESSE_MER_KMJ) + 2;
                break;
            case AIR:
                jours = 1;
                if (distance > DISTANCE_AIR_LONG) jours = 2;
                if (urgent) jours = 1;
                break;
        }
        if (jours < 1) jours = 1;
        return jours;
    }
}
