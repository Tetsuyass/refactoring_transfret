package fr.efrei.transport;

public class DecorateurAssurance extends DecorateurPrix {

    private static final double TAUX_ASSURANCE = 0.02;
    private static final double ASSURANCE_MINIMUM = 15.0;
    private final double valeur;

    public DecorateurAssurance(Prix composant, double valeur) {
        super(composant);
        this.valeur = valeur;
    }

    @Override
    public double calculer() {
        double montantAssurance = valeur * TAUX_ASSURANCE;
        if (montantAssurance < ASSURANCE_MINIMUM) montantAssurance = ASSURANCE_MINIMUM;
        return composant.calculer() + montantAssurance;
    }
}
