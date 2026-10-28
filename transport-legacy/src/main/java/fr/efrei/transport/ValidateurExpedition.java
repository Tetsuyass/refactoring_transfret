package fr.efrei.transport;

import java.util.List;

public class ValidateurExpedition {

    public String valider(Client client, List<Colis> colis, String villeDepart, String villeArrivee, ModeTransport mode, boolean urgent) {
        if (client == null) {
            return "ERREUR : client obligatoire";
        }
        if (colis == null || colis.isEmpty()) {
            return "ERREUR : au moins un colis est nécessaire";
        }
        
        boolean dangereux = false;
        boolean refrigere = false;
        for (Colis c : colis) {
            if (c.getPoids() <= 0) {
                return "ERREUR : poids invalide pour " + c.getDescription();
            }
            if (c.getType() == TypeMarchandise.DANGEREUX) dangereux = true;
            if (c.getType() == TypeMarchandise.REFRIGERE) refrigere = true;
        }
        
        if (dangereux && mode == ModeTransport.AIR) {
            return "ERREUR : les matières dangereuses sont interdites en transport aérien";
        }
        if (refrigere && !(mode == ModeTransport.ROUTE || mode == ModeTransport.MER)) {
            return "ERREUR : le transport réfrigéré n'est possible que par route ou par mer";
        }
        if (urgent && mode == ModeTransport.MER) {
            return "ERREUR : pas d'option urgente pour le transport maritime";
        }
        
        int distance = Utils.getDistance(villeDepart, villeArrivee);
        if (distance == -1) {
            return "ERREUR : trajet inconnu " + villeDepart + " -> " + villeArrivee;
        }
        
        return null; // Validation OK
    }
}
