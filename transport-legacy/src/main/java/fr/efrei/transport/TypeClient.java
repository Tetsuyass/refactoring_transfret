package fr.efrei.transport;

public enum TypeClient {
    PARTICULIER("Particulier"),
    ENTREPRISE("Entreprise"),
    PREMIUM("Premium");

    private final String libelle;

    TypeClient(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
