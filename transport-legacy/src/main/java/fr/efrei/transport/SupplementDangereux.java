package fr.efrei.transport;

public class SupplementDangereux extends DecorateurPrix {

    private static final double MULTIPLICATEUR = 1.30;

    public SupplementDangereux(Prix composant) {
        super(composant);
    }

    @Override
    public double calculer() {
        return composant.calculer() * MULTIPLICATEUR;
    }
}
