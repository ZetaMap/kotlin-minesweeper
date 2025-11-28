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

### Local Platform (default)
```bash
./gradlew build
```

### Specific Platform
```bash
# Build for Windows
./gradlew :windows:build

# Build for Linux
./gradlew :linux:build

# Build for macOS
./gradlew :macos:build
```

### All Platforms
```bash
./gradlew :all:build
```

## Running

```bash
# Run on current platform
./gradlew run

# Run specific platform (requires matching OS)
./gradlew :linux:run
./gradlew :windows:run
./gradlew :macos:run
```

## Testing

```bash
# Test on current platform
./gradlew test

# Test specific platform
./gradlew :linux:test
./gradlew :windows:test
./gradlew :macos:test

# Test all platforms
./gradlew :all:testAll
```

## Creating Distributions

```bash
# Create native distributions for current platform
./gradlew packageDistributionForCurrentOS

# Create specific distributions
./gradlew :windows:packageMsi    # Windows MSI installer
./gradlew :linux:packageDeb      # Linux DEB package
./gradlew :macos:packageDmg      # macOS DMG image
```

## How to Play

1. **Left-click** on a cell to reveal it
2. **Right-click** on a cell to place/remove a flag
3. **Double-click** on a revealed number to chord (reveal surrounding cells if enough flags are placed)
4. Numbers indicate how many adjacent mines there are
5. Flag all mines and reveal all safe cells to win!

## Project Structure

```
├── src/main/kotlin/minesweeper/    # Main source code
│   ├── Main.kt                     # Application entry point
│   ├── model/                      # Game data models
│   ├── game/                       # Game state management
│   └── ui/                         # Compose UI components
├── windows/                        # Windows-specific build config
├── linux/                          # Linux-specific build config
├── macos/                          # macOS-specific build config
└── all/                            # Aggregator for all platforms
```

## License

MIT License - see [LICENSE](LICENSE) file
