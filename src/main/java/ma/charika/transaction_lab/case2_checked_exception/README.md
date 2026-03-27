# Cas 2 — Exceptions checked → Rollback non déclenché

## Objectif du cas
Ce cas montre un piège très courant :

**Spring ne rollback pas sur les exceptions checked (``Exception``, ``IOException``, ``SQLException``, etc.) à moins de le lui demander explicitement.**

Beaucoup de développeurs pensent que toute exception déclenche un rollback.
C’est faux : seules les ``RuntimeException`` le font par défaut.


``RuntimeException``	→ ✔️ Oui
``Error``	→ ✔️ Oui
``Exception`` (checked)	→ ❌ Non
``IOException``, ``SQLException``	→ ❌ Non

Cela signifie que si une méthode annotée ``@Transactional`` lance une exception checked, Spring commit la transaction, sauf si on configure explicitement le rollback.

## Version bugguée (Case2ServiceBug)

```java
@Transactional
public void placeOrderWithCheckedException() throws Exception {
    Order order = orderRepository.save(new Order(...));

    // ❌ Lance une Exception (checked)
    paymentService.processPayment(order);

    // ❌ Spring ne rollback pas → COMMIT
}
```

## Version corrigée (Case2ServiceFix)

```java
@Transactional(rollbackFor = Exception.class)
public void placeOrderWithCheckedException() throws Exception {
    Order order = orderRepository.save(new Order(...));
    paymentService.processPayment(order); // Exception checked
}
```
➡️ Spring rollback sur toutes les exceptions checked.

Autre solution : Transformer l’exception en RuntimeException

```java
@Transactional
public void placeOrderWithCheckedException() {
    try {
        paymentService.processPayment(order);
    } catch (Exception e) {
        throw new RuntimeException(e); // ✔️ Déclenche rollback
    }
}
```

### Tester les endpoints 

```bash
GET /case2/run-bug

GET /case2/run-fix
```

## Ce qu’il faut retenir

Spring rollback uniquement sur RuntimeException par défaut.

Les exceptions checked ne déclenchent pas de rollback.

Pour corriger :

utiliser ``rollbackFor = Exception.class``

ou relancer une ``RuntimeException``