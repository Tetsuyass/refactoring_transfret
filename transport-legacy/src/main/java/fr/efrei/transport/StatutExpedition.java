package fr.efrei.transport;

public enum StatutExpedition {
    CREEE("Créée"),
    EN_PREPARATION("En préparation"),
    EN_COURS("En cours"),
    LIVREE("Livrée"),
    ANNULEE("Annulée");

    private final String libelle;

    StatutExpedition(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
