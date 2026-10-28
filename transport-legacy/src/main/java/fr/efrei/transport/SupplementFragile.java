package fr.efrei.transport;

public class SupplementFragile extends DecorateurPrix {

    private static final double MULTIPLICATEUR = 1.15;

    public SupplementFragile(Prix composant) {
        super(composant);
    }

    @Override
    public double calculer() {
        return composant.calculer() * MULTIPLICATEUR;
    }
}
