package fr.efrei.transport;

import java.util.List;

public interface ClientRepository {
    void sauvegarder(Client client);
    Client trouverParEmail(String email);
    List<Client> tousLesClients();
}
