package fr.efrei.transport;

public enum ModeTransport {
    ROUTE("Route"),
    RAIL("Rail"),
    MER("Mer"),
    AIR("Air");

    private final String libelle;

    ModeTransport(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
