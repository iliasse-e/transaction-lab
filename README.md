# Spring Transaction Lab

#### *Comprendre, reproduire et résoudre les problématiques de rollback et de transactions dans Spring Boot*

## Introduction
Dans une application backend, la gestion des transactions est essentielle pour garantir la cohérence et l’intégrité des données.
Le mécanisme de rollback joue un rôle central : il permet de revenir à un état stable lorsque une opération échoue partiellement.

Ce projet a pour objectif d’illustrer, à travers des cas concrets, les erreurs transactionnelles les plus fréquentes et leurs solutions.

## À quelle problématique répond le rollback ?
Le rollback répond à une question simple mais cruciale :

Comment garantir que toutes les étapes d’une opération réussissent ensemble, ou qu’aucune ne soit appliquée ?

En backend, une opération métier implique souvent plusieurs actions dépendantes.
Si l’une échoue, on ne veut surtout pas laisser la base dans un état incohérent.

## Le rollback ne dépend pas du nombre de tables
Contrairement à une idée répandue, le rollback n’est pas réservé aux opérations qui modifient plusieurs tables.

Il intervient dès qu’une opération implique plusieurs actions qui doivent réussir ensemble, qu’elles touchent :

- une seule table

- plusieurs tables

- ou même aucune table (ex. : appel API externe + écriture DB)

Ce qui compte, ce n’est pas la base de données, mais l’atomicité de l’opération.

### Exemples concrets

### ✔️ Exemple 1 — Une seule table, mais rollback nécessaire
Imaginons une table ``users`` :

1. On insère un utilisateur

2. On envoie un email de confirmation

3. L’envoi d’email échoue

➡️ On peut décider que l’inscription doit être annulée → rollback  
Même si une seule table est concernée.

### ✔️ Exemple 2 — Aucune table, mais rollback applicatif
Une transaction peut englober :

- un appel à un service de paiement

- un appel à un service de livraison

- un envoi d’email

Si l’un échoue, on doit annuler les autres actions.
Dans ce cas, on utilise un rollback applicatif ou un pattern Saga.


## ⚠️ Cas complexes et erreurs fréquentes
Il existe plusieurs situations où les développeurs rencontrent des comportements inattendus, des rollbacks silencieux… ou l’absence de rollback alors qu’ils pensaient être protégés.

Voici un exemple parmi les plus courants :

### 1. Les exceptions “avalées” → rollback manquant
```java
try {
    service.process();
} catch (Exception e) {
    log.error("Erreur", e);
}
```

Ici, l’exception est attrapée mais jamais relancée.
Spring ne voit aucune erreur → pas de rollback.

C’est l’un des pièges les plus fréquents.

### 2. Exceptions checked → rollback non déclenché
Spring rollback uniquement sur :

``RuntimeException``

``Error``

Mais pas sur :

``Exception``

``IOException``

``SQLException``

Exemple :

```java
@Transactional
public void createUser() throws Exception {
    userRepository.save(user);
    throw new Exception("Erreur");
}
```

**Pas de rollback**, sauf si on ajoute :

```java
@Transactional(rollbackFor = Exception.class)
```

### 3. Self-invocation → @Transactional ignoré
Spring utilise un proxy pour gérer les transactions.
Si une méthode annotée @Transactional est appelée depuis la même classe, le proxy n’intercepte pas l’appel.

Exemple :

```java
public void register() {
    saveUser(); // Appel interne → @Transactional ignoré
}

@Transactional
public void saveUser() { ... }
```

### 4. Transactions trop larges → rollback involontaire
Certains développeurs mettent @Transactional sur des méthodes énormes :

- 5 écritures DB

- 3 appels API

- 1 envoi d’email

- 1 traitement lourd

Si une seule étape échoue → rollback total.

➡️ Problème : les appels externes ne sont pas rollbackables  
(ex : email déjà envoyé, paiement déjà effectué).

### 5. Tests transactionnels qui masquent des bugs
Spring exécute souvent les tests avec :

```java
@Transactional
```

➡️ Tout est rollbacké après le test.

Cela masque :

des problèmes de commit réel

des contraintes DB non testées

des comportements de concurrence invisibles

### 6. Transactions dans les microservices → fausse sécurité
Beaucoup pensent que @Transactional fonctionne entre microservices.

❌ Faux.

Chaque service a sa propre base → pas de transaction globale.

Exemple :

- Service A crée une commande

- Service B réserve le stock

- Service C échoue

➡️ A et B ne rollbackent pas → incohérence métier.

Solution : **Saga, Outbox, Eventual Consistency**

### 7. Transactions et appels asynchrones
Une méthode annotée ``@Transactional`` qui appelle un service ``@Async`` :

```java
@Transactional
public void process() {
    userRepository.save(user);
    asyncService.sendEmail();
}
```
➡️ L’appel async s’exécute hors transaction.
Si l’email échoue → impossible de rollback la DB.

### 8. Méthodes privées → @Transactional ignoré
Spring ne peut pas appliquer de proxy sur une méthode ``private``.

➡️ Le ``@Transactional`` est ignoré.

### 9. Transactions dans les constructeurs
Si une méthode annotée est appelée dans un constructeur :

```java
public UserService() {
    initData(); // @Transactional ignoré
}
```
➡️ Le proxy n’existe pas encore → pas de transaction.

### 10. Problèmes de concurrence silencieux
Exemple classique :

- Deux transactions lisent un stock à 10

- Les deux décrémentent à 9

- Les deux écrivent 9

➡️ Pas de rollback, mais **perte de données**.

Solutions :

- isolation level

- verrouillage pessimiste

- verrouillage optimiste

## Résumé des erreurs fréquentes

| Problème | Pourquoi ça bloque |
|---------|---------------------|
| **Exceptions avalées** | L’exception est attrapée mais non relancée → Spring ne voit aucune erreur → pas de rollback |
| **Exceptions checked** | Spring ne rollback pas sur `Exception` ou `IOException` par défaut |
| **Self-invocation** | Appel interne `this.method()` contourne le proxy → `@Transactional` ignoré |
| **Transactions trop larges** | Un échec tardif rollback tout, y compris des actions externes non rollbackables |
| **Tests transactionnels** | Le rollback automatique masque des bugs liés aux commits réels |
| **Microservices** | Pas de transaction globale entre services → incohérences si un service échoue |
| **Async / @Async** | Le code async s’exécute hors transaction → rollback impossible |
| **Méthodes privées** | Spring ne peut pas proxifier une méthode `private` → annotation ignorée |
| **Appels dans les constructeurs** | Le proxy n’est pas encore créé → `@Transactional` ignoré |
| **Problèmes de concurrence** | Deux transactions peuvent écraser les données de l’autre sans rollback |


## Le projet d'application

Chaque projet est représenté par un package et contenant un fichier README decrivant la problématique à résoudre.

La structure :

```java
spring-transaction-lab/
│
├── common/                 # Entités, repositories, DTO communs
├── case1_swallowed_exception/
├── case2_checked_exception/
├── case3_self_invocation/
├── case4_external_calls/
├── case5_test_transactions/
└── config/
```