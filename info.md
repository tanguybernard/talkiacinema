C'est un sujet **très visuel et trompeur**. À première vue, ça a l'air simple (CRUD de tickets), mais si tu ajoutes la **livraison à la place**, ça devient un cas d'école parfait pour le DDD.

C'est "complexe" juste comme il faut pour ton talk, car tu vas pouvoir jouer sur **la synchronisation temporelle** (Le film démarre) et **spatiale** (Où est le client ?).

Voici comment transformer ce sujet "fun" en démonstration technique implacable.

---

### L'Angle d'attaque : "L'Expérience Cinéma Fluide"

Le piège ici n'est pas la réservation, c'est **l'interaction** entre le Film, le Siège et la Cuisine.

#### 1. Le Piège des "Places Orphelines" (Optimisation)

* **Projet A (Générique) :**
* L'IA voit une liste de sièges [1, 2, 3, 4].
* Le client réserve le 2 et le 4.
* **Résultat :** Il reste le 3 tout seul au milieu. Personne ne l'achètera. Perte de revenu.


* **Projet B (DDD) :**
* La Memory Bank impose la règle du **"No Single Gap"**.
* Claude refuse la réservation ou propose de décaler : *"Pour des raisons d'optimisation salle, vous ne pouvez pas laisser un siège isolé."*



#### 2. Le Piège du "Pop-corn Froid" (Temporel)

* **Projet A (Générique) :**
* Commande créée à 18h00. Film à 20h00.
* L'IA lance la commande en cuisine tout de suite.
* **Résultat :** Le client arrive à 20h, son pop-corn est prêt depuis 2h. Il est froid et mou.


* **Projet B (DDD) :**
* Concept de `ScreeningTime` et `PreparationLeadTime`.
* La commande est stockée mais son **état** ne passe à "ToPrepare" que 15 min avant la séance.



#### 3. Le Piège de la "Livraison Disruptive" (Expérience)

* **Projet A (Générique) :**
* Le client commande un supplément boisson à 20h45 (milieu du film).
* Le système envoie un runner.
* **Résultat :** Le serveur passe devant l'écran en plein suspense. Scandale dans la salle.


* **Projet B (DDD) :**
* La Memory Bank définit des `ServiceWindows` (Avant séance, Entracte).
* Claude répond : *"La séance a commencé (Feature Presentation). Livraison impossible en salle. Retrait au comptoir uniquement."*



---

### La Structure pour ton Talk

C'est un super sujet car tu peux montrer une "Architecture Hexagonale" très claire.

1. **Domaine Cinéma :** Gère les films, les salles (Capacité, PMR), les séances.
2. **Domaine Vente :** Gère les paniers, les tarifs.
3. **Domaine Opérations :** Gère la cuisine et le staff.

**Le point de bascule (Le "Aha!" moment) :**
Demande à Claude : *"Change la salle du film 'Avatar' de la salle 1 (200 places) à la salle 2 (100 places) car la clim est en panne."*

* **Projet Générique :** `UPDATE screening SET room_id = 2`.
* **Catastrophe :** Si tu avais déjà vendu 150 billets, tu as 50 clients sur les genoux des autres. L'IA ne vérifie pas la cohérence des tickets *déjà vendus*.


* **Projet DDD :**
* Il lève une exception métier `CapacityOverflowException`.
* Il suggère : *"Impossible de changer de salle sans annuler 50 tickets. Veux-tu lancer une procédure de 'Rebooking' automatique ?"*



---

### Ta Memory Bank pour le Projet B

Voici le fichier `.claude/productContext.md` adapté au Cinéma :

```markdown
# Contexte Produit : CineLux Experience Manager

## But du système
Offrir une expérience cinéma premium avec service à la place.
L'objectif n'est pas de vendre des tickets, mais de gérer des "Séances".

## Langage Omniprésent (Ubiquitous Language)
- **Séance (Screening)** : L'association d'un Film, d'une Salle et d'un Horaire.
- **Placement (Seating)** : La carte physique de la salle.
- **Siège PMR** : Emplacement réservé aux fauteuils roulants (Règle légale stricte).
- **Commande Confiserie (Concession Order)** : Doit être synchronisée avec la Séance.
- **Fenêtre de Service** : Période où le staff peut entrer en salle (Pre-show, Pubs, Trailers). Interdit pendant le "Feature Movie".

## Règles Métier Inviolables
1. **Intégrité de la Jauge** : On ne peut pas vendre plus de tickets que de sièges physiquement fonctionnels.
2. **Règle de l'Orphelin** : Interdiction de laisser un siège vide isolé entre deux réservations sur une même rangée.
3. **Fraîcheur** : Une commande de nourriture ne doit être envoyée en préparation (Cuisine) que 15 minutes avant le début de la Séance associée.
4. **Protection Mineur** : Impossible de vendre un tarif "Enfant" pour un film classé "Interdit -12 ans".

```