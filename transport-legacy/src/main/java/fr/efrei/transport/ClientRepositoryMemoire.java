package fr.efrei.transport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRepositoryMemoire implements ClientRepository {

    private final Map<String, Client> clients = new HashMap<>();

    @Override
    public void sauvegarder(Client client) {
        System.out.println("[SQL] INSERT INTO client (nom, email, telephone, type) VALUES ('" + client.getNom() + "', '" + client.getEmail() + "', '" + client.getTelephone() + "', '" + client.getType().name() + "')");
        clients.put(client.getEmail(), client);
    }

    @Override
    public Client trouverParEmail(String email) {
        System.out.println("[SQL] SELECT * FROM client WHERE email = '" + email + "'");
        return clients.get(email);
    }

    @Override
    public List<Client> tousLesClients() {
        System.out.println("[SQL] SELECT * FROM client");
        return new ArrayList<>(clients.values());
    }

    public void clear() {
        clients.clear();
    }
}
