# Cas 3 — Self‑invocation : @Transactional ignoré

## Objectif du cas
Ce cas démontre un piège très subtil mais extrêmement fréquent :

**Quand une méthode annotée ``@Transactional`` est appelée depuis une autre méthode de la même classe, Spring ignore complètement l’annotation.**

Ce phénomène s’appelle self‑invocation.

Il provoque des comportements inattendus :

- pas de transaction créée

- pas de rollback

- pas de propagation

- pas de logs transactionnels

Et surtout : **aucune erreur visible**, ce qui rend le bug difficile à diagnostiquer.

## Pourquoi Spring ignore l’annotation ?

Spring applique ``@Transactional`` via un proxy autour du bean.

Ce proxy intercepte les appels externes au bean :

```java
Controller → proxy → service.method()
```

Mais si une méthode interne appelle une autre méthode interne :

```java
service.methodA() → this.methodB()
```

➡️ Le proxy n’est jamais traversé
➡️ L’annotation ``@Transactional`` n’est jamais appliquée
➡️ La transaction n’existe pas

## Version bugguée (self‑invocation)

```java
public void processOrder() {
    saveOrder(); // ❌ Appel interne → @Transactional ignoré
}

@Transactional
public void saveOrder() {
    orderRepository.save(order);
    throw new RuntimeException("Erreur simulée");
}
```

## Version corrigée : Extraire la méthode dans un autre service

```java
@Service
public class Case3OrderWriter {
    @Transactional
    public void saveOrder() { ... }
}
```

Puis dans une autre classe :

```java
public void processOrder() {
    orderWriter.saveOrder(); // ✔️ Appel externe → proxy → transaction OK
}
```

Autre solution : S’auto‑invoquer via le proxy Spring

```java
@Autowired
private ApplicationContext context;

public void processOrder() {
    context.getBean(Case3ServiceFix.class).saveOrder(); // ✔️ proxy
}
```

## Les endpoints

GET /case3/run-bug
GET /case3/run-fix

## Ce qu’il faut retenir

``@Transactional`` ne fonctionne que si l’appel passe par le proxy Spring.

Les appels internes (this.method()) contournent le proxy.

Les méthodes privées, statiques ou appelées dans un constructeur sont également ignorées.

La solution la plus propre : extraire la logique transactionnelle dans un autre service.