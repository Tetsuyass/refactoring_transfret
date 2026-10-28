package fr.efrei.transport;

public abstract class DecorateurPrix implements Prix {

    protected final Prix composant;

    public DecorateurPrix(Prix composant) {
        this.composant = composant;
    }

    @Override
    public double calculer() {
        return composant.calculer();
    }
}
