# Étape 1 – Diagnostic

Les références `Fichier:ligne` portent sur `transport-legacy`. Le module `transport-refactored` en est
une copie à l'identique (hors package et une ligne vide), les numéros de ligne y sont donc les mêmes.

## 1. Qu'est-ce que chaque classe fait, de quoi dépend-elle ?

| Classe | Ce qu'elle fait | Dépend de |
|---|---|---|
| `ServiceClient` | valide les données d'un client, l'enregistre, envoie l'email de bienvenue | `Client`, `Database` (singleton), `EmailService` (statique) |
| `ServiceExpedition` | valide une demande, choisit le véhicule, calcule prix et délai, crée l'expédition, la sauvegarde, gère démarrage / livraison / annulation, notifie le client | `Database`, `CalculateurPrix`, `AffectationVehicule` (tous en dur), `EmailService`, `SmsService`, `Utils`, `Vehicule`, `Colis`, `Client`, `Expedition` |
| `CalculateurPrix` | calcule le prix HT : tarif par mode, suppléments, urgence, remise client, assurance, arrondi | `Colis` |
| `AffectationVehicule` | trouve le premier véhicule disponible et compatible | `Flotte` (statique), `Vehicule` et ses cinq sous-classes (`instanceof`), `Colis` |
| `Flotte` | registre statique des véhicules, initialisé à la main | toutes les classes de véhicules |
| `IVehicule` | contrat commun de tous les véhicules (13 méthodes) | `Colis` |
| `Vehicule` | état et chargement communs (capacité, charge, disponibilité) | `IVehicule`, `Colis` |
| `Camion`, `CamionFrigorifique`, `Train`, `Bateau`, `Avion` | véhicules concrets, avec leurs particularités | `Vehicule`, `Colis` (le frigo lit `getType()`) |
| `Client` | données d'un client (type en `String`) | – |
| `Colis` | données d'un colis (type en `int`), volume, libellé du type | – |
| `Expedition` | données d'une expédition, totaux, **et génération de la facture** | `Client`, `Colis`, `Vehicule`, `Utils` |
| `Database` | faux accès base : singleton, SQL simulé, deux `Map` statiques publiques | `Client`, `Expedition` |
| `EmailService` | envoi simulé d'emails, journal statique public | – |
| `SmsService` | envoi simulé de SMS, journal statique public, clé d'API en dur | – |
| `Utils` | fourre-tout : format de date, arrondi, validation d'email, table des distances | – |
| `Main` | démo : construit les services à la main | tout |

## 2. Tableau défaut → emplacement → principe violé

### S – Responsabilité unique

| Défaut | Emplacement | Principe |
|---|---|---|
| `ServiceExpedition` a au moins six raisons de changer : validation, affectation, prix/devis, délais, cycle de vie, notifications | `ServiceExpedition` (toute la classe) | **S** |
| 35 lignes de règles d'acceptation mélangées à la création | `ServiceExpedition.java:17-51` | **S** |
| `creerExpedition` fabrique aussi l'identifiant, la date de livraison et déclenche la notification | `ServiceExpedition.java:62-86` | **S** |
| Les délais de livraison vivent dans le service d'expédition | `ServiceExpedition.java:101-127` | **S** |
| Le service envoie lui-même email et SMS | `ServiceExpedition.java:204-209` | **S** |
| Une entité métier fabrique sa propre facture (mise en forme, TVA, dates) | `Expedition.java:52-82` | **S** |
| `ServiceClient` valide, persiste et envoie l'email de bienvenue | `ServiceClient.java:6-25` | **S** |
| `Database` mélange fausse exécution SQL, stockage mémoire et accès aux données | `Database.java:29-55` | **S** |
| `Utils` mélange dates, arrondi, validation d'email et géographie | `Utils.java` (toute la classe) | **S** |
| `CalculateurPrix` fait seul tarif, suppléments, urgence, remise, assurance et arrondi | `CalculateurPrix.java:7-67` | **S** (et **O**) |

### O – Ouvert / fermé

| Défaut | Emplacement | Principe |
|---|---|---|
| `if/else` sur le mode pour le tarif de base | `CalculateurPrix.java:25-41` | **O** |
| `if` en chaîne sur le type de client pour la remise | `CalculateurPrix.java:50-58` | **O** |
| Suppléments codés en `if` successifs | `CalculateurPrix.java:44-47` | **O** |
| `switch` sur le mode pour les délais | `ServiceExpedition.java:103-124` | **O** |
| Règles d'acceptation écrites mode par mode | `ServiceExpedition.java:35-46` | **O** |
| Dispatch du démarrage selon le mode | `ServiceExpedition.java:142-149` | **O** |
| Cascade `if mode` puis `instanceof` pour choisir le véhicule | `AffectationVehicule.java:20-52` | **O** |
| Validation du type de client par liste de `String` | `ServiceClient.java:14-21` | **O** |
| `switch` sur un entier pour le libellé du type de colis | `Colis.java:33-46` | **O** |
| Le type de client est testé pour afficher le SIRET | `Expedition.java:60-62` | **O** |
| Table de distances en `if/else if` : une ville de plus = une ligne de plus | `Utils.java:30-51` | **O** |
| Le SMS est lié au type `PREMIUM` | `ServiceExpedition.java:206` | **O** |

### L – Substitution de Liskov

| Défaut | Emplacement | Principe |
|---|---|---|
| `CamionFrigorifique.charger()` renforce la précondition (refuse tout sauf réfrigéré) : il n'est pas utilisable là où un `Camion` est attendu | `CamionFrigorifique.java:10-16` | **L** |
| Contournement de cette violation : le `continue` « sinon ça plante » | `AffectationVehicule.java:26-28` | **L** (symptôme) |
| `Train.faireLePlein()` lève une exception alors que `Vehicule` promet un plein | `Train.java:29-32` | **L** |
| Exception avalée par un `catch` vide dans l'appelant | `ServiceExpedition.java:138-141` | **L** (symptôme) |
| `rouler`, `naviguer`, `voler`, `activerRefrigeration` lèvent `UnsupportedOperationException` : les sous-types ne tiennent pas le contrat de `IVehicule` | `Camion.java:16-26`, `Train.java:13-23`, `Bateau.java:12-22`, `Avion.java:14-24` | **L** |
| `Bateau.activerRefrigeration` lève une exception selon un état interne | `Bateau.java:24-29` | **L** |
| Cast `(Bateau) v` pour accéder à une méthode absente du contrat | `AffectationVehicule.java:43` | **L** (symptôme) |

### I – Ségrégation des interfaces

| Défaut | Emplacement | Principe |
|---|---|---|
| `IVehicule` oblige tout véhicule à savoir rouler, naviguer, voler, réfrigérer et faire le plein | `IVehicule.java:18-32` | **I** |
| Chaque véhicule implémente en `throw` des méthodes qui n'ont pas de sens pour lui | `Camion.java:16-26`, `Train.java:13-23`, `Bateau.java:12-22`, `Avion.java:14-24` | **I** |
| `peutTransporterMatieresDangereuses()` figure dans l'interface mais n'est consultée que pour la route | `IVehicule.java:30`, `AffectationVehicule.java:30` | **I** |
| `ServiceExpedition` dépend de toute l'interface fat pour n'en utiliser qu'une partie par mode | `ServiceExpedition.java:136-155` | **I** |
| `AffectationVehicule` et `ServiceExpedition` reçoivent des `Vehicule` concrets, pas des rôles | `AffectationVehicule.java:8`, `ServiceExpedition.java:53,136` | **I** / **D** |

### D – Inversion des dépendances

| Défaut | Emplacement | Principe |
|---|---|---|
| Singleton `Database.getInstance()` | `ServiceExpedition.java:12`, `ServiceClient.java:23`, `Database.java:13-27` | **D** |
| `new CalculateurPrix()` et `new AffectationVehicule()` en dur | `ServiceExpedition.java:13-14` | **D** |
| `EmailService.envoyer()` appelé en statique | `ServiceExpedition.java:205`, `ServiceClient.java:24` | **D** |
| `new SmsService()` à chaque notification | `ServiceExpedition.java:207` | **D** |
| `Flotte.vehicules` : liste statique publique lue directement | `Flotte.java:8`, `AffectationVehicule.java:17` | **D** |
| `Database.expeditions` et `Database.clients` : état statique public | `Database.java:15-16` | **D** |
| `EmailService.journal`, `SmsService.journal`, `ServiceExpedition.compteur` : états statiques modifiables | `EmailService.java:12`, `SmsService.java:9`, `ServiceExpedition.java:10` | **D** |
| `Utils.FORMAT_DATE` : `SimpleDateFormat` statique partagé (non thread-safe) | `Utils.java:8` | **D** |
| `Expedition` dépend de `Utils` pour formater ses dates | `Expedition.java:58` | **D** |
| Preuve : l'adaptateur de test doit vider six états statiques pour « réinitialiser » | `LegacyAdapter.java:35-45` | **D** |
| Aucun point d'assemblage : chaque service construit ses propres collaborateurs | `ServiceExpedition.java:12-14`, `Main.java:9-11` | **D** |

### Autres défauts (traités aux étapes 2 et suivantes)

| Défaut | Emplacement |
|---|---|
| Nombres magiques (`1.2`, `0.10`, `167`, `5000`, `70`, `1440`, `1.852`, `35000`…) | `CalculateurPrix.java:26-62`, `ServiceExpedition.java:76,105-121,145-148,186` |
| Mode, statut et type de client en `String` ; type de colis en `int` | `Expedition.java:15,18`, `Client.java:11`, `Colis.java:15` |
| `Math.round(x * 100.0) / 100.0` répété alors que `Utils.arrondir` existe | `CalculateurPrix.java:66`, `ServiceExpedition.java:76,97,186` |
| Validation d'email dupliquée alors que `Utils.emailValide` existe | `ServiceClient.java:10`, `Utils.java:25` |
| Boucle « détecter les types de colis » copiée quatre fois | `CalculateurPrix.java:14-21`, `ServiceExpedition.java:27-34`, `AffectationVehicule.java:12-16`, `ServiceExpedition.java:150-155` |
| Volume et poids totaux calculés en double | `Expedition.java:27-50`, `Colis.java:29`, `CalculateurPrix.java:14-17` |
| Erreurs signalées par `println` + `return null` / `false` | `ServiceExpedition.java` (partout), `ServiceClient.java` |
| `Vehicule.charger()` refuse silencieusement un colis hors capacité | `Vehicule.java:39-43` |
| Clé d'API SMS écrite en clair | `SmsService.java:11` |
| SQL construit par concaténation (injection) et URL de prod affichée | `Database.java:19,34,39,44,48` |

## 3. Où modifier pour ajouter le mode `FLUVIAL`

À conserver pour l'étape 8.

| # | Emplacement | Modification |
|---|---|---|
| 1 | `CalculateurPrix.java:25-41` | ajouter une branche : 0,40 €/km + 0,02 €/kg, minimum 300 € (sinon `IllegalArgumentException("Mode inconnu")`) |
| 2 | `ServiceExpedition.java:103-124` | ajouter le délai : ⌈distance / 360⌉ + 1 jour |
| 3 | `ServiceExpedition.java:39` | le réfrigéré est refusé par la liste blanche `ROUTE \|\| MER`, mais seulement « par accident » : à rendre explicite |
| 4 | `ServiceExpedition.java:43` | ajouter l'interdiction de l'urgence en fluvial (le test actuel ne vise que `MER`) |
| 5 | `ServiceExpedition.java:142-149` | ajouter le départ : sans cela un fluvial démarre sans qu'aucun véhicule ne bouge, **sans erreur** |
| 6 | `AffectationVehicule.java:20-52` | ajouter une branche `FLUVIAL` → `instanceof Peniche` |
| 7 | nouvelle classe `Peniche` | doit implémenter les **13 méthodes** de `IVehicule`, dont trois en `throw` (`rouler`, `voler`, `activerRefrigeration`) |
| 8 | `Flotte.java:10-18` | ajouter `new Peniche("PEN-0001", 2_500_000)` (2 500 t en kg) |
| 9 | `Utils.java:30-51` | ajouter le trajet fluvial (Paris ↔ Le Havre, 365 km) |
| 10 | `Main.java` | démo, facultatif |
| 11 | `transport-contrat` : `ModeTransport.java` | `FLUVIAL` n'existe pas dans le contrat, **qu'on n'a pas le droit de modifier** : les 68 tests ne peuvent donc pas exercer ce mode, il faudra des tests unitaires dans `transport-refactored` |

À vérifier sans modification attendue : `Expedition.genererFacture` (le mode est affiché tel quel, `Expedition.java:66`),
`Database` (le mode est stocké comme `String`).

Bilan avant refactoring : **5 fichiers existants à modifier** (`CalculateurPrix`, `ServiceExpedition` en
4 endroits, `AffectationVehicule`, `Flotte`, `Utils`) et 1 classe à créer.

## 4. Où modifier pour ajouter le supplément « hors gabarit »

Règle : ×1,10 si un colis dépasse 200 cm sur une dimension. À conserver pour l'étape 8.

| # | Emplacement | Modification |
|---|---|---|
| 1 | `CalculateurPrix.java:14-21` | dans la boucle sur les colis, détecter qu'une dimension dépasse 200 |
| 2 | `CalculateurPrix.java:44-47` | ajouter `if (horsGabarit) prix = prix * 1.10;` **avant la remise** (`:50`) : la remise entreprise dépend du seuil de 5 000 € (`:51`), donc l'ordre compte |
| 3 | `Colis.java` | facultatif : ajouter un accesseur « plus grande dimension » pour ne pas dupliquer la logique |

À vérifier sans modification attendue : `ServiceExpedition.calculerDevis` et `creerExpedition` passent par
`CalculateurPrix`, donc devis et prix réel suivent automatiquement ; `Expedition.genererFacture` n'affiche aucun détail de supplément.

Bilan avant refactoring : **1 fichier existant à modifier** (`CalculateurPrix`), plus `Colis` en option.
La modification est petite, mais c'est une modification et non une extension : c'est ce que l'étape 6
(Decorator) doit transformer en « créer une classe + l'ajouter à l'assemblage ».