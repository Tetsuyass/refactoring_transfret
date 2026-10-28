package fr.efrei.transport;

public class SupplementUrgence extends DecorateurPrix {

    private static final double MULTIPLICATEUR = 1.5;

    public SupplementUrgence(Prix composant) {
        super(composant);
    }

    @Override
    public double calculer() {
        return composant.calculer() * MULTIPLICATEUR;
    }
}
