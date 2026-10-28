package fr.efrei.transport;

public class SupplementRefrigere extends DecorateurPrix {

    private static final double MULTIPLICATEUR = 1.20;

    public SupplementRefrigere(Prix composant) {
        super(composant);
    }

    @Override
    public double calculer() {
        return composant.calculer() * MULTIPLICATEUR;
    }
}
