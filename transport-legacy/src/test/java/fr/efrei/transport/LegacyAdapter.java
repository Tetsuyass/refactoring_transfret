package fr.efrei.transport;

import fr.efrei.transport.contrat.Canal;
import fr.efrei.transport.contrat.ColisDto;
import fr.efrei.transport.contrat.DemandeClient;
import fr.efrei.transport.contrat.DemandeDevis;
import fr.efrei.transport.contrat.DemandeExpedition;
import fr.efrei.transport.contrat.EtatExpedition;
import fr.efrei.transport.contrat.Notification;
import fr.efrei.transport.contrat.Statut;
import fr.efrei.transport.contrat.SystemeTransport;
import fr.efrei.transport.contrat.TypeMarchandise;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Branche le code historique sur les tests de contrat.
 * <p>
 * Tout le « bricolage » nécessaire pour tester l'ancien code (état statique à vider,
 * conversion des codes {@code String}/{@code int}…) est confiné ici.
 */
public class LegacyAdapter implements SystemeTransport {

    private ServiceClient serviceClient;
    private ServiceExpedition serviceExpedition;
    private ClientRepositoryMemoire clientRepository;
    private ExpeditionRepositoryMemoire expeditionRepository;
    private CanalNotificationTest canalNotification;

    @Override
    public void reinitialiser() {
        // Construction d'un nouveau graphe d'objets
        FlotteRepository flotteRepository = new FlotteRepositoryMemoire();
        flotteRepository.initialiser();
        
        clientRepository = new ClientRepositoryMemoire();
        expeditionRepository = new ExpeditionRepositoryMemoire();
        canalNotification = new CanalNotificationTest();
        
        ValidateurClient validateurClient = new ValidateurClient();
        ValidateurExpedition validateurExpedition = new ValidateurExpedition();
        
        // Configuration des stratégies de tarif
        Map<ModeTransport, TarifTransport> tarifsParMode = new HashMap<>();
        tarifsParMode.put(ModeTransport.ROUTE, new TarifRoute());
        tarifsParMode.put(ModeTransport.RAIL, new TarifRail());
        tarifsParMode.put(ModeTransport.MER, new TarifMer());
        tarifsParMode.put(ModeTransport.AIR, new TarifAir());
        
        // Configuration des stratégies de remise
        Map<TypeClient, PolitiqueRemise> remisesParTypeClient = new HashMap<>();
        remisesParTypeClient.put(TypeClient.PARTICULIER, new RemiseParticulier());
        remisesParTypeClient.put(TypeClient.ENTREPRISE, new RemiseEntreprise());
        remisesParTypeClient.put(TypeClient.PREMIUM, new RemisePremium());
        
        // Configuration des stratégies de délai
        Map<ModeTransport, DelaiTransport> delaisParMode = new HashMap<>();
        delaisParMode.put(ModeTransport.ROUTE, new DelaiRoute());
        delaisParMode.put(ModeTransport.RAIL, new DelaiRail());
        delaisParMode.put(ModeTransport.MER, new DelaiMer());
        delaisParMode.put(ModeTransport.AIR, new DelaiAir());
        
        CalculateurPrix calculateurPrix = new CalculateurPrix(tarifsParMode, remisesParTypeClient);
        EstimateurDelai estimateurDelai = new EstimateurDelai(delaisParMode);
        AffectationVehicule affectationVehicule = new AffectationVehicule(flotteRepository);
        Notificateur notificateur = new Notificateur(canalNotification);
        GenerateurFacture generateurFacture = new GenerateurFacture();
        
        serviceClient = new ServiceClient(clientRepository, validateurClient, canalNotification);
        serviceExpedition = new ServiceExpedition(
            expeditionRepository, calculateurPrix, affectationVehicule,
            validateurExpedition, estimateurDelai, notificateur, generateurFacture, 0);
    }

    @Override
    public Optional<String> creerClient(DemandeClient d) {
        TypeClient type = TypeClient.valueOf(d.type().name());
        Client client = serviceClient.creerClient(d.nom(), d.email(), d.telephone(), d.adresse(), type, d.siret());
        return Optional.ofNullable(client).map(Client::getEmail);
    }

    @Override
    public Optional<String> creerExpedition(DemandeExpedition d) {
        Client client = clientRepository.trouverParEmail(d.refClient());
        ModeTransport mode = ModeTransport.valueOf(d.mode().name());
        Expedition expedition = serviceExpedition.creerExpedition(client, versColis(d.colis()), d.villeDepart(),
                d.villeArrivee(), mode, d.urgent(), d.assurance());
        return Optional.ofNullable(expedition).map(Expedition::getId);
    }

    @Override
    public Optional<EtatExpedition> consulter(String id) {
        Expedition e = expeditionRepository.trouverParId(id);
        if (e == null) {
            return Optional.empty();
        }
        int delai = (int) ChronoUnit.DAYS.between(jour(e.getDateCreation()), jour(e.getDateLivraisonPrevue()));
        Statut statut = Statut.valueOf(e.getStatut().name());
        return Optional.of(new EtatExpedition(e.getId(), statut, e.getPrixHT(), e.getPrixTTC(),
                delai, e.getVehicule().getImmatriculation(), e.getFraisAnnulation()));
    }

    @Override
    public OptionalDouble devisTTC(DemandeDevis d) {
        ModeTransport mode = ModeTransport.valueOf(d.mode().name());
        TypeClient typeClient = TypeClient.valueOf(d.typeClient().name());
        double devis = serviceExpedition.calculerDevis(versColis(d.colis()), d.villeDepart(), d.villeArrivee(),
                mode, d.urgent(), d.assurance(), typeClient);
        return devis == -1 ? OptionalDouble.empty() : OptionalDouble.of(devis);
    }

    @Override
    public boolean demarrer(String id) {
        return serviceExpedition.demarrerExpedition(id);
    }

    @Override
    public boolean livrer(String id) {
        return serviceExpedition.livrerExpedition(id);
    }

    @Override
    public boolean annuler(String id) {
        return serviceExpedition.annulerExpedition(id);
    }

    @Override
    public Optional<String> facture(String id) {
        return Optional.ofNullable(expeditionRepository.trouverParId(id)).map(serviceExpedition::genererFacture);
    }

    @Override
    public List<Notification> notifications() {
        List<Notification> notifications = new ArrayList<>();
        for (String[] email : canalNotification.emails) {
            notifications.add(new Notification(Canal.EMAIL, email[0], email[1]));
        }
        for (String[] sms : canalNotification.sms) {
            notifications.add(new Notification(Canal.SMS, sms[0], sms[1]));
        }
        return notifications;
    }

    private static java.time.LocalDate jour(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static List<Colis> versColis(List<ColisDto> colis) {
        return colis.stream().map(LegacyAdapter::versColis).collect(Collectors.toCollection(ArrayList::new));
    }

    private static Colis versColis(ColisDto c) {
        TypeMarchandise type = TypeMarchandise.valueOf(c.type().name());
        return new Colis(c.description(), c.poidsKg(), c.longueurCm(), c.largeurCm(), c.hauteurCm(),
                type, c.valeurDeclaree());
    }
}
