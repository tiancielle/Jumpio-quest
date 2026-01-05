**Jumpio Quest**

- **Project:** Minimal JavaFX platformer used for teaching and prototyping.
# Jumpio Quest

## Overview

- Language: Java (target JDK 17)
- Build: Maven
- UI: JavaFX (Canvas + AnimationTimer)

This project is structured as a small game engine: `Main` starts the JavaFX application, `Engine` runs the game loop, `Player` handles player physics, and classes under the `engine` package contain level and obstacle logic.

---

## Running (Windows / PowerShell)

To compile and run from the terminal (the project includes a Maven setup and the JavaFX Maven plugin):

```powershell
cd "C:\Users\nasri\OneDrive\Desktop\4eme\JEE\Jumpio quest"
or
.\run.bat
# remove target if needed
Remove-Item -Recurse -Force target/ -ErrorAction SilentlyContinue
$env:PATH = "C:\Users\nasri\OneDrive\Desktop\4eme\JEE\Jumpio quest\scripts\tools\apache-maven-3.9.11\bin;$env:PATH"
mvn -DskipTests clean compile
# to launch with JavaFX (uses the javafx-maven-plugin)
mvn -DskipTests javafx:run
```

Tip: prefer `mvn javafx:run` over `java -jar` so native JavaFX libraries are configured correctly.

---

## Controls

- Left: `A` or Left Arrow
- Right: `D` or Right Arrow
- Jump: `W`, Up Arrow or `Space`

---

## Architecture (high level)

- `Main`: JavaFX entry point, creates the `Canvas` and instantiates the `Engine`.
- `Engine`: game loop (`AnimationTimer`) which calls `update(dt)` and `render()` each frame; receives keyboard events and drives game entities.
- `Player`: manages position, velocities, input, gravity and simple ground collision.
- (Planned) `Level` / `Platform` / `Obstacle`: generation and representation of platforms, holes and obstacles; AABB collisions with the player.

Rendering is done on a single `Canvas` for simple, predictable performance. The architecture is intentionally small to make extensions easy.

---

## Key files

- `pom.xml` — Maven configuration and JavaFX dependencies.
- `src/main/java/com/jumpiquest/main/Main.java` — JavaFX application.
- `src/main/java/com/jumpiquest/engine/Engine.java` — loop + rendering + input handling.
- `src/main/java/com/jumpiquest/engine/Player.java` — player entity and physics.

---

## Implemented behavior and gameplay

- Game loop using `AnimationTimer`.
- Visual placeholder: player drawn as an orange rectangle, simple ground, and an FPS HUD.
- Keyboard input handled (left/right/jump), movement and jumping with basic gravity.

---

## Recommended next steps

1. Platforms & AABB collisions (obstacles and holes) — implement `Level`/`Platform` and handle collisions so the player can stand on platforms and jump across gaps.
2. Adding more worlds, and different obstacles.
3. Improve physics: friction, acceleration, coyote time, variable jump height for a tighter feel.
4. Ability to compete with friends woldwise

---

## Development & tips

- VS Code: run `Java: Clean the Java language server workspace` after major changes and reimport the Maven project if needed.
- To run from VS Code, see the launch configuration in `.vscode/launch.json` (a `Launch Main` configuration is provided).

---



