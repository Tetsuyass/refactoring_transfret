package fr.efrei.transport;

public class DelaiMer implements DelaiTransport {

    private static final double VITESSE_KMJ = 720.0;

    @Override
    public int calculerDelai(int distance, boolean urgent) {
        return (int) Math.ceil(distance / VITESSE_KMJ) + 2;
    }
}
