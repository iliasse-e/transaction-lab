# Cas 1 — Exception avalée → Rollback manquant

## Objectif du cas
Ce cas illustre un piège très courant :

**Une exception est attrapée (catch) mais jamais relancée → Spring ne voit aucune erreur → la transaction ne rollback pas.**

C’est l’un des bugs transactionnels les plus fréquents dans les applications Spring Boot.

## Problématique
Spring déclenche un rollback uniquement lorsqu’une exception remonte jusqu’au proxy transactionnel.

Lorsqu'on fait :

```java
try {
    service.process();
} catch (Exception e) {
    log.error("Erreur", e);
}
```

➡️ L’exception est avalée.
➡️ Spring pense que tout s’est bien passé.
➡️ La transaction est commit, même si une étape a échoué.

Résultat : incohérence en base.

## Cas concret dans l'application

#### Le scénario est le suivant :

1. Le service crée une commande (Order)
2. Il tente de créer un paiement (Payment)
3. Le paiement échoue volontairement (exception)
4. L’exception est attrapée mais non relancée
➡️ La commande est enregistrée en base
➡️ Le paiement n’existe pas
➡️ La transaction n’a pas rollbacké

On créé deux services (et chacun son endpoint) :

``/case1/run-bug`` → commande créée, paiement échoué, pas de rollback

``/case1/run-fix`` → exception remontée, rollback total

### Version bugguée (Case1ServiceBug)

```java
@Transactional
public void placeOrder() {
    Order order = orderRepository.save(new Order(...));

    try {
        paymentService.processPayment(order);
    } catch (Exception e) {
        log.error("Erreur lors du paiement", e);
        // ❌ On n’envoie pas l’exception vers le haut
    }

    // ❌ Spring pense que tout s’est bien passé → COMMIT
}
```

### Version corrigée (Case1ServiceFix)

```java
@Transactional
public void placeOrder() {
    Order order = orderRepository.save(new Order(...));

    try {
        paymentService.processPayment(order);
    } catch (Exception e) {
        log.error("Erreur lors du paiement", e);
        throw e; // ✔️ Spring voit l’erreur → ROLLBACK
    }
}
```

Ou bien une autre solution consiste à marquer explicitement le rollback :

```java
@Transactional
public void placeOrder() {
    Order order = orderRepository.save(new Order(...));

    try {
        paymentService.processPayment(order);
    } catch (Exception e) {
        log.error("Erreur lors du paiement", e);
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        // ✔️ On force le rollback même sans relancer l’exception
    }
}
```

## Ce qu’il faut retenir

Spring rollback uniquement si une exception remonte jusqu’au proxy.

Attraper une exception sans la relancer = désactiver le rollback.

C’est un bug silencieux, difficile à détecter sans logs.

Toujours relancer l’exception ou marquer explicitement le rollback.