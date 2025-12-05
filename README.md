# 💣 Minesweeper - Kotlin Multiplatform

Un jeu de Démineur moderne construit avec Kotlin et Jetpack Compose Multiplatform, avec des animations cool !

## 🎮 Fonctionnalités

- **Multiplateforme** : Fonctionne sur Desktop (Windows, macOS, Linux), Android, et Web (JS/WASM)
- **4 niveaux de difficulté** : Facile, Moyen, Difficile, Expert
- **Animations fluides** :
  - Animation de révélation des cellules
  - Tremblement de la grille en cas de défaite
  - Animation de drapeau
  - Explosion animée des mines
  - Animations de victoire/défaite

## 📁 Structure du projet

```
minesweeper/
├── core/                          # Module partagé (logique + UI)
│   └── src/commonMain/kotlin/
│       └── fr/zetamap/minesweeper/
│           ├── game/              # Logique du jeu
│           ├── ui/                # Composants UI
│           │   ├── components/    # Composants réutilisables
│           │   ├── screens/       # Écrans
│           │   └── theme/         # Thème et couleurs
│           └── viewmodel/         # ViewModel
│
├── backends/                      # Backends spécifiques aux plateformes
│   ├── desktop/                   # Desktop (JVM)
│   ├── android/                   # Android
│   └── web/                       # Web (JS + WASM)
│
└── gradle/                        # Configuration Gradle
```

## 🚀 Lancement

### Desktop (Windows/macOS/Linux)
```bash
./gradlew :backends:desktop:run
```

### Android
```bash
./gradlew :backends:android:installDebug
```

### Web (WASM)
```bash
./gradlew :backends:web:run
```

### Web (JavaScript)
```bash
./gradlew :backends:web:runJs
```

## 🎯 Contrôles

- **Clic gauche** : Révéler une cellule
- **Double-clic / Appui long** : Placer/retirer un drapeau
- **Clic sur cellule révélée** : Chord (révéler les cellules adjacentes si tous les drapeaux sont placés)

## 🛠️ Technologies

- **Kotlin** 2.2.20
- **Jetpack Compose Multiplatform** 1.9.1
- **Gradle** 8.14

## 📦 Build

### Créer un exécutable Desktop
```bash
./gradlew :backends:desktop:packageDistributionForCurrentOS
```

### Créer un APK Android
```bash
./gradlew :backends:android:assembleRelease
```

### Créer une application Web
```bash
# WASM (recommandé)
./gradlew :backends:web:wasmJsBrowserDistribution

# JavaScript (fallback pour anciens navigateurs)
./gradlew :backends:web:jsBrowserDistribution
```

## 📄 Licence

MIT License

