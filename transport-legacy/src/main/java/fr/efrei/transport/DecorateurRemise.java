package fr.efrei.transport;

public class DecorateurRemise extends DecorateurPrix {

    private final PolitiqueRemise politiqueRemise;

    public DecorateurRemise(Prix composant, PolitiqueRemise politiqueRemise) {
        super(composant);
        this.politiqueRemise = politiqueRemise;
    }

    @Override
    public double calculer() {
        return politiqueRemise.appliquerRemise(composant.calculer());
    }
}
