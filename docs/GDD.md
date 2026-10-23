# Game Design Document — « Chevalier » (titre provisoire)


## 1. Résumé 

Le joueur incarne un **chevalier** qui traverse des niveaux de **gauche à droite**.
Il **court**, **saute** de plateforme en plateforme, **frappe à l'épée** les ennemis,
évite les **pièges** (piques, trous), ramasse des **pièces** et des **cœurs**, et doit
atteindre la **sortie** de chaque niveau. Le chevalier a un nombre de **vies** limité :
tomber dans un trou, se faire toucher par un ennemi ou un piège coûte de la vie.

Thème : **fantasy / médiéval** (chevalier, slimes, chauves-souris, château).

---

## 2. Objectif du joueur & conditions de victoire/défaite

- **But d'un niveau** : atteindre la **porte de sortie** à droite, vivant.
- **But du jeu** : terminer les **5-6 niveaux** du POC (minimum 3).
- **Perdre une vie** : PV à 0, chute dans un trou, ou contact avec un piège/ennemi.
- **Game over** : plus de vies → écran de fin, possibilité de recommencer.
- **Score** : pièces ramassées + niveaux terminés (+ temps, optionnel).

---

## 3. Boucle de jeu (game loop — TEMPS RÉEL)

Contrairement à un jeu au tour par tour, un plateformer tourne en **continu** : l'écran
se rafraîchit ~60 fois par seconde. À chaque « frame » (image) :

1. **Lire les entrées** (touches enfoncées : gauche, droite, saut, attaque).
2. **Mettre à jour** la logique : appliquer la gravité, déplacer le héros et les ennemis,
   résoudre les collisions, résoudre les attaques, vérifier morts / sortie atteinte.
3. **Dessiner** la scène (décor + entités + HUD).



---

## 4. Déplacement & physique

Méthode **standard des jeux 2D** (simple et fiable), à détailler à l'étape 3 :

- Chaque entité a une **position**, une **vitesse** (vx, vy) et une **boîte de collision**
  rectangulaire (**AABB** : Axis-Aligned Bounding Box).
- **Gravité** : à chaque frame, on ajoute une accélération vers le bas à `vy`.
- **Saut** : appuyer sur saut donne une impulsion vers le haut (vy négatif), seulement si
  le héros est **au sol**.
- **Collisions** : le monde est une **grille de tuiles**. On teste la boîte du héros
  contre les tuiles solides autour de lui ; on le repousse pour qu'il ne traverse pas
  les murs/sols. On traite l'axe X puis l'axe Y séparément (technique classique qui
  évite 90 % des bugs de collision).
- **Au sol ?** : vrai si une tuile solide se trouve juste sous les pieds → autorise le saut.

---

## 5. Le monde / les niveaux

- Un **niveau** = une **grille de tuiles** plus large que l'écran → **défilement
  horizontal** (la caméra suit le héros).
- Types de tuiles : **vide** (air), **sol/mur** (solide), **plateforme**, **piques**
  (piège), **pièce**, **cœur**, **porte de sortie**.
- Les niveaux sont **chargés depuis un fichier** (ex. un fichier texte où chaque
  caractère = une tuile) → facile d'ajouter/éditer des niveaux sans toucher au code.
- Difficulté **croissante** d'un niveau à l'autre : sauts plus serrés, plus d'ennemis,
  plus de pièges.

---

## 6. Entités

### 6.1 Chevalier (joueur)
- Stats : **PV/vies**, vitesse, force de saut.
- Actions : courir (←/→), **sauter**, **attaquer à l'épée**.
- Brève **invincibilité** après avoir été touché (évite de perdre toutes ses vies d'un coup).

### 6.2 Ennemis (IA)
Plusieurs types avec des **comportements distincts** (ça justifie un patron de conception
d'IA à l'étape 3) :

| Ennemi         | Comportement                                                        |
|----------------|---------------------------------------------------------------------|
| Slime          | Marche de gauche à droite, fait demi-tour au bord ou contre un mur.  |
| Chauve-souris  | Vole selon un mouvement ondulant / fonce vers le héros s'il approche.|
| Garde (costaud)| Marche, demande **2 coups** d'épée pour mourir (plus de PV).         |



---

## 7. Combat

- Le chevalier **attaque à l'épée** : l'attaque crée une **zone de dégâts (hitbox)**
  devant lui pendant un court instant.
- Tout ennemi dont la boîte touche cette hitbox perd des PV ; à 0 PV il meurt.
- **Contact ennemi → chevalier** : le chevalier perd de la vie puis devient brièvement
  invincible (clignotement).
- Les **piques** fonctionnent comme un ennemi fixe (contact = dégâts).

---

## 8. Objets & ramassage

- **Pièce** : augmente le score.
- **Cœur** : rend de la vie.
- *(optionnel plus tard)* **clé** pour ouvrir la porte, power-up d'épée, etc.
- Ramassage automatique au contact.

---

## 9. Contrôles (clavier)

- **← / →** (ou Q / D) : courir à gauche / droite.
- **Espace** (ou flèche haut / Z) : sauter.
- **Touche d'attaque** (ex. **X** ou **E**) : coup d'épée.
- **Échap** : pause.

---

## 10. Interface (GUI)

- Fenêtre **JavaFX**, rendu en **tuiles / petites images (sprites)**.
- Caméra qui **suit le héros** (défilement horizontal).
- **HUD** en surimpression : **cœurs/vies** restants, **pièces** ramassées, **niveau** en cours.
- Écrans : menu de départ, écran de **pause**, écran de **game over**, écran de **victoire**.

---

## 11. Périmètre & jalons (dispo : à fond — on peut être ambitieux, par étapes)

Chaque jalon doit **tourner** avant de passer au suivant.

- **Jalon 0 — Le héros bouge (fondations)**
  - Fenêtre + game loop, un niveau affiché, le chevalier court, saute, et les collisions
    avec le décor fonctionnent (ne traverse pas murs/sols, retombe avec la gravité).
- **Jalon 1 — Un vrai niveau jouable**
  - Épée + 1 type d'ennemi, pièges (piques/trous), perte de vie, atteindre la sortie = gagné,
    0 vie = game over.
- **Jalon 2 — Contenu & finition**
  - 5-6 niveaux (min. 3) chargés depuis des fichiers, défilement de caméra, 2-3 types d'ennemis,
    pièces + cœurs, HUD complet, menus (pause / game over / victoire).
- **Jalon 3 (si le temps le permet)**
  - Double saut, plateformes mobiles, un **boss** de fin, son/musique, animations de sprites.



---

## 12. Hors périmètre (pour ce POC)

- Pas de multijoueur, pas de sauvegarde de progression, pas de 3D.
- Son/musique et animations fluides = **bonus** (Jalon 3), pas prioritaires.

