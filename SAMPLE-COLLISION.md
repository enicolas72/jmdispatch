# Sample: Game Object Collision — Visitor Pattern vs Multi-Dispatch

This sample demonstrates how jmdispatch replaces the classic **visitor pattern** for double-dispatch problems. The use case is game object collision resolution, where behavior depends on the runtime types of **both** colliding objects.

## The Problem

In a space game, collisions between `Spaceship`, `Asteroid`, and `Laser` objects each produce different outcomes. Java's single dispatch (`instanceof` chains or virtual methods) can only switch on one object's type at a time. Handling all *pairs* of types requires double dispatch.

## Game Objects

Four model classes form a simple hierarchy:

```
GameObject (name)
├── Spaceship (shieldStrength)
├── Asteroid  (size: 1=small, 2=medium, 3=large)
└── Laser     (power)
```

These are plain concrete classes with no framework dependencies — shared by both solutions.

## Collision Outcomes

| Pair                  | Outcome                                   |
|-----------------------|-------------------------------------------|
| Spaceship + Spaceship | Both take hull damage                     |
| Spaceship + Asteroid  | Ship damaged (size-dependent)             |
| Spaceship + Laser     | Ship hit (shield-dependent)               |
| Asteroid + Asteroid   | Both shatter into fragments               |
| Asteroid + Laser      | Laser vaporizes asteroid                  |
| Laser + Laser         | Beams cross harmlessly                    |
| *(fallback)*          | Unknown collision                         |

## Solution A: Visitor Pattern

**Files:** `visitor/` subpackage (5 files)

The visitor pattern simulates double dispatch by bouncing through two virtual calls:

1. **`Collidable` interface** — declares `collideWith(Collidable)` plus one reverse-dispatch method per type (`collideWithSpaceship`, `collideWithAsteroid`, `collideWithLaser`).

2. **3 wrapper classes** (`CollidableSpaceship`, `CollidableAsteroid`, `CollidableLaser`) — each extends the model class and implements `Collidable`:
   - `collideWith(Collidable other)` → calls `other.collideWithXxx(this)` (second dispatch)
   - 3 `collideWithXxx` methods each = **9 reverse-dispatch methods** total

3. **`VisitorCollisionDemo`** — creates wrapper objects and runs all 6 collision pairs.

### Structural cost

- 1 interface + 3 wrapper classes
- 9 reverse-dispatch methods spread across 3 classes
- Logic for each pair is split across two classes (the handler lives in whichever class receives the reverse-dispatch call)
- **Adding a 4th type** (e.g., `Shield`) requires: adding a method to the interface, adding a wrapper class, and modifying **all 3 existing** wrapper classes

## Solution B: Multi-Dispatch with jmdispatch

**Files:** `multidispatch/` subpackage (1 file)

A single class with flat `@Dispatch` handler methods:

```java
@Dispatch
public static String collide(Spaceship s, Asteroid a) {
    return s.getName() + " hit by asteroid " + a.getName() + "...";
}

@Dispatch
public static String collide(Asteroid a, Spaceship s) {
    return s.getName() + " hit by asteroid " + a.getName() + "...";
}

// ... 8 more handlers (6 unique pairs + symmetric orderings + fallback)
```

A `DispatchTable` auto-registers all handlers and dispatches at runtime:

```java
private static final DispatchTable table = DispatchTable.factory(2)
        .autoregister(MultiDispatchCollisionDemo.class);

// dispatch using plain model objects — no wrappers needed
TABLE.dispatch(ship, asteroid);
```

### Structural cost

- 1 class, 10 flat methods
- Uses plain model objects directly (no wrappers, no interface ceremony)
- All collision logic in one place
- **Adding a 4th type** = add new `@Dispatch` methods. No existing code touched.

## Running the Sample

```bash
mvn compile
java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout) \
    net.eric_nicolas.jmdispatch.sample.collision.Main
```

The `Main` class runs both solutions and compares results side-by-side, verifying they produce identical output.

## Side-by-Side Comparison

| Dimension                | Visitor Pattern                          | jmdispatch                        |
|--------------------------|------------------------------------------|-----------------------------------|
| Files                    | 5 (interface + 3 wrappers + demo)        | 1                                 |
| Dispatch methods         | 9 spread across 3 classes                | 10 in one class                   |
| Model class modification | Requires wrappers                        | None — uses plain objects         |
| Logic locality           | Split across receiver classes             | All handlers in one place         |
| Adding a new type        | Modify interface + all existing wrappers | Add new `@Dispatch` methods only  |
| Symmetry handling        | Implicit (via reverse-dispatch routing)  | Explicit (separate handler)       |

## File Layout

```
src/main/java/net/eric_nicolas/jmdispatch/sample/collision/
├── GameObject.java                          # base class
├── Spaceship.java                           # shieldStrength
├── Asteroid.java                            # size
├── Laser.java                               # power
├── Main.java                                # runs both, compares results
├── visitor/
│   ├── Collidable.java                      # double-dispatch interface
│   ├── CollidableSpaceship.java             # wrapper + 3 reverse-dispatch methods
│   ├── CollidableAsteroid.java              # wrapper + 3 reverse-dispatch methods
│   ├── CollidableLaser.java                 # wrapper + 3 reverse-dispatch methods
│   └── VisitorCollisionDemo.java            # demo runner
└── multidispatch/
    └── MultiDispatchCollisionDemo.java      # 10 @Dispatch methods + demo runner
```
