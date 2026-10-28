package fr.efrei.transport;

import java.util.List;

public interface ExpeditionRepository {
    void sauvegarder(Expedition expedition);
    void mettreAJourStatut(Expedition expedition);
    Expedition trouverParId(String id);
    List<Expedition> toutesLesExpeditions();
}
