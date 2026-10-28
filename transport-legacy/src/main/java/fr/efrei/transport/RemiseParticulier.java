package fr.efrei.transport;

public class RemiseParticulier implements PolitiqueRemise {

    @Override
    public double appliquerRemise(double prix) {
        return prix; // Pas de remise pour les particuliers
    }
}
