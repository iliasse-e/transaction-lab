# Cas 4 — Transactions trop larges + appels externes non‑rollbackables

## Objectif du cas
Ce cas montre un problème fréquent dans les applications réelles :

**Une méthode transactionnelle effectue à la fois des opérations en base ET des appels externes (paiement, email, API). Si l’appel externe échoue, la transaction rollback… mais l’appel externe, lui, ne peut pas être annulé.**

Résultat :

- la base est rollbackée

- mais l’appel externe est déjà exécuté

- → incohérence métier

C’est exactement ce qui arrive dans les systèmes e‑commerce mal conçus.

## Problématique

Une transaction Spring ne couvre que :

- la base de données locale

- les opérations internes au service

Elle ne couvre pas :

- les appels HTTP

- les appels à un prestataire de paiement

- les envois d’email

- les appels à un microservice

- les actions physiques (expédition, impression, etc.)

Donc si on a :

```java
@Transactional
public void placeOrder() {
    orderRepository.save(order);
    paymentGateway.charge(order); // appel externe
    emailService.sendConfirmation(order); // appel externe
}
```
Et que l'email échoue, alors :

- On rollback la sauvegarde la commande
- L'utilisateur est débité

## Version bugguée (transaction trop large)

```java
@Transactional
public void placeOrder() {
    Order order = orderRepository.save(order);

    paymentGateway.charge(order); // appel externe non rollbackable

    emailService.send(order); // échoue → rollback DB
}
```

➡️ Le paiement est fait, mais la commande n’existe plus.

## Version corrigée : découper en deux transaction

```java
public void placeOrder() {
    Order order = createOrder(); // transaction 1
    processPayment(order);       // hors transaction
    sendEmail(order);            // hors transaction
}
```

Parce que maintenant, la transaction ne couvre que la création de la commande.

✔️ Étape 1 : créer la commande    
→ transaction courte   
→ si ça échoue, rien n’est fait   
→ si ça réussit, la commande est en base

✔️ Étape 2 : paiement  
→ hors transaction  
→ si ça échoue, la commande reste en base  
→ tu peux marquer la commande comme “payment_failed” plus tard

✔️ Étape 3 : email  
→ hors transaction  
→ si ça échoue, la commande reste en base  
→ tu peux retenter l’email plus tard

#### Solution 2 — Pattern Outbox (recommandé)

1. On enregistre un événement “EmailToSend” dans une table outbox

2. Un worker séparé envoie l’email

3. En cas d’échec → retry automatique

#### Solution 3 — Saga / orchestration

Chaque étape est indépendante et compensable.

## Ce qu’il faut retenir

- Une transaction Spring ne protège pas les appels externes.

- Les transactions trop larges créent des incohérences silencieuses.

- Il faut découper ou utiliser un pattern Outbox.

- Ce cas est très courant dans les systèmes e‑commerce, bancaires, logistiques.