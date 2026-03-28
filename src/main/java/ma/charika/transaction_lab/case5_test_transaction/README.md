# Cas 5 — Les tests transactionnels masquent les bugs

*En gros : Ne pas utiliser @Transactional dans un fichier de Test, khlass*

## Enjeu du cas
Les tests Spring Boot utilisent souvent :

```java
@SpringBootTest
@Transactional
```

Ce qui signifie :

- chaque test s’exécute dans une transaction

- à la fin du test, Spring fait automatiquement un rollback

- la base revient à l’état initial

C’est pratique…
**Mais cela peut cacher des bugs qui n’apparaissent qu’en production.**

Pourquoi les tests transactionnels posent problème ?

Dans un test :

- tu appelles un service

- il fait des opérations en base

- il lance une exception

- Spring rollback automatiquement

- la base est propre

- le test passe

En production :

- la même exception survient

- mais il n’y a pas de rollback automatique

- la base reste dans un état partiellement modifié

- tu te retrouves avec des données incohérentes

➡️ Le test te donne une **fausse impression de sécurité**.


## Ce qu’il faut retenir

Les tests transactionnels ne reflètent pas la réalité.

Ils peuvent masquer :

- des exceptions checked non rollbackées

- des self-invocations

- des transactions trop larges

- des effets externes non compensés

Pour tester correctement :

- utiliser des tests non transactionnels

- vérifier l’état réel de la base après l’appel

- simuler les erreurs métier