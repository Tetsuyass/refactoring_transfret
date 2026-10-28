package fr.efrei.transport;

public class RemiseEntreprise implements PolitiqueRemise {

    private static final double SEUIL_REMISE = 5000;
    private static final double REMISE_HAUT = 0.85;
    private static final double REMISE_BAS = 0.90;

    @Override
    public double appliquerRemise(double prix) {
        if (prix > SEUIL_REMISE) {
            return prix * REMISE_HAUT;
        } else {
            return prix * REMISE_BAS;
        }
    }
}
