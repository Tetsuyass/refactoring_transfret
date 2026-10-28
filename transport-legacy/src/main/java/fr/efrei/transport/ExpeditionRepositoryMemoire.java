package fr.efrei.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpeditionRepositoryMemoire implements ExpeditionRepository {

    private final Map<String, Expedition> expeditions = new HashMap<>();

    @Override
    public void sauvegarder(Expedition expedition) {
        System.out.println("[SQL] INSERT INTO expedition (id, client, depart, arrivee, mode, statut, prix_ttc) VALUES ('" + expedition.getId() + "', '" + expedition.getClient().getEmail() + "', '" + expedition.getVilleDepart() + "', '" + expedition.getVilleArrivee() + "', '" + expedition.getMode().name() + "', '" + expedition.getStatut().name() + "', " + expedition.getPrixTTC() + ")");
        expeditions.put(expedition.getId(), expedition);
    }

    @Override
    public void mettreAJourStatut(Expedition expedition) {
        System.out.println("[SQL] UPDATE expedition SET statut = '" + expedition.getStatut().name() + "' WHERE id = '" + expedition.getId() + "'");
    }

    @Override
    public Expedition trouverParId(String id) {
        System.out.println("[SQL] SELECT * FROM expedition WHERE id = '" + id + "'");
        return expeditions.get(id);
    }

    @Override
    public List<Expedition> toutesLesExpeditions() {
        System.out.println("[SQL] SELECT * FROM expedition");
        return new ArrayList<>(expeditions.values());
    }

    public void clear() {
        expeditions.clear();
    }
}
