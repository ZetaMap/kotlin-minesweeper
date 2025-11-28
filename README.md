# kotlin-minesweeper
Minesweeper... in Kotlin... using Compose... with cool animations. =)

## Features

- 🎮 Classic Minesweeper gameplay
- 🎯 Three difficulty levels: Beginner (9x9, 10 mines), Intermediate (16x16, 40 mines), Expert (16x30, 99 mines)
- ✨ Cool animations:
  - Cell reveal with scale and rotation effects
  - Flag toggle with bounce animation
  - Explosion animation when hitting a mine
  - Shake animation when losing the game
  - Pulsing face button when winning
  - Blinking win/lose messages
- 🛡️ First-click safety (first click never reveals a mine)
- ⏱️ Timer and mine counter with LED-style digital display
- 🖱️ Full mouse support (left-click to reveal, right-click to flag, double-click to chord)
- 📱 Touch support on Android (tap to reveal, long-press to flag)
- 🖥️ Cross-platform support (Windows, Linux, macOS, Android)

## Requirements

- JDK 17 or higher
- Gradle 8.14+
- Android SDK (for Android builds only)

## Building

### Desktop
```bash
# Build for current platform
./gradlew build

# Run desktop application
./gradlew run
```

### Android

To build for Android, you need to:

1. Install Android SDK and set `ANDROID_HOME` environment variable
2. Uncomment the Android plugin and configuration in `build.gradle.kts`
3. Run:
```bash
./gradlew assembleDebug    # Debug APK
./gradlew assembleRelease  # Release APK
```

## Testing

```bash
./gradlew desktopTest
```

## Creating Distributions

```bash
# Create distribution for current platform
./gradlew dist

# Cross-platform distributions (desktop)
./gradlew windows   # Create Windows MSI installer
./gradlew linux     # Create Linux DEB package
./gradlew macos     # Create macOS DMG image
./gradlew all       # Create distributions for all desktop platforms

# Android (requires Android SDK setup)
./gradlew android   # Shows instructions for Android build
```

## How to Play

### Desktop
1. **Left-click** on a cell to reveal it
2. **Right-click** on a cell to place/remove a flag
3. **Double-click** on a revealed number to chord (reveal surrounding cells if enough flags are placed)

### Android
1. **Tap** on a cell to reveal it
2. **Long-press** on a cell to place/remove a flag
3. **Double-tap** on a revealed number to chord

Numbers indicate how many adjacent mines there are. Flag all mines and reveal all safe cells to win!

## Project Structure

```
src/
├── commonMain/kotlin/minesweeper/    # Shared code for all platforms
│   ├── model/
│   │   ├── Cell.kt                   # Cell data class and enums
│   │   └── Board.kt                  # Game board logic
│   ├── game/
│   │   └── GameManager.kt            # Game state management
│   └── ui/
│       ├── Theme.kt                  # Color definitions
│       ├── CellView.kt               # Cell component interface (expect)
│       ├── BoardView.kt              # Game board and status panel
│       └── GameScreen.kt             # Main game screen
├── desktopMain/kotlin/minesweeper/   # Desktop-specific code
│   ├── Main.kt                       # Desktop entry point
│   └── ui/
│       └── CellView.kt               # Desktop cell with mouse support
└── androidMain/                      # Android-specific code
    ├── kotlin/minesweeper/
    │   ├── MainActivity.kt           # Android entry point
    │   └── ui/
    │       └── CellView.android.kt   # Android cell with touch support
    ├── AndroidManifest.xml
    └── res/
        └── values/
            ├── strings.xml
            └── themes.xml
```

## License

MIT License - see [LICENSE](LICENSE) file
