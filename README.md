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
- 🖥️ Cross-platform support (Windows, Linux, macOS)

## Requirements

- JDK 17 or higher
- Gradle 8.14+

## Building

```bash
# Build for current platform
./gradlew build
```

## Running

```bash
./gradlew run
```

## Testing

```bash
./gradlew test
```

## Creating Distributions

```bash
# Create distribution for current platform
./gradlew dist

# Cross-platform distributions
./gradlew windows   # Create Windows MSI installer
./gradlew linux     # Create Linux DEB package
./gradlew macos     # Create macOS DMG image
./gradlew all       # Create distributions for all platforms
```

## How to Play

1. **Left-click** on a cell to reveal it
2. **Right-click** on a cell to place/remove a flag
3. **Double-click** on a revealed number to chord (reveal surrounding cells if enough flags are placed)
4. Numbers indicate how many adjacent mines there are
5. Flag all mines and reveal all safe cells to win!

## Project Structure

```
src/main/kotlin/minesweeper/
├── Main.kt              # Application entry point
├── model/
│   ├── Cell.kt          # Cell data class and enums
│   └── Board.kt         # Game board logic
├── game/
│   └── GameManager.kt   # Game state management
└── ui/
    ├── Theme.kt         # Color definitions
    ├── CellView.kt      # Individual cell component
    ├── BoardView.kt     # Game board and status panel
    └── GameScreen.kt    # Main game screen
```

## License

MIT License - see [LICENSE](LICENSE) file
