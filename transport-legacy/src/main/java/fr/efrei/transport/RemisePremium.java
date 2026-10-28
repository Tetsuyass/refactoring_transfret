package fr.efrei.transport;

public class RemisePremium implements PolitiqueRemise {

    private static final double REMISE = 0.80;

    @Override
    public double appliquerRemise(double prix) {
        return prix * REMISE;
    }
}
