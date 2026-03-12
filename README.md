# JMDispatch

A pure Java **multiple dispatch** (multimethod) framework that selects method implementations at runtime based on the actual types of all arguments, not just the receiver.

Java's built-in virtual dispatch is single dispatch — the method called depends only on the runtime type of `this`. JMDispatch extends this to **two or more arguments**, choosing the best-matching registered handler based on all argument types simultaneously.

## How It Works

1. Annotate static methods with `@Dispatch`
2. Create a dispatch table and auto-register handlers from a class
3. Call `dispatch(...)` — the framework finds the closest matching handler by computing inheritance distance across all arguments

Under the hood, JMDispatch uses **ASM bytecode generation** to create functor implementations that invoke your static methods, avoiding reflection overhead at dispatch time.

## Quick Start

### Two-argument dispatch

```java
import net.eric_nicolas.jmdispatch.*;

public class Collisions {

    // Handler for the general case
    @Dispatch
    public static void collide(Shape a, Shape b) {
        System.out.println("Generic shape collision");
    }

    // Handler for a specific combination
    @Dispatch
    public static void collide(Circle a, Rectangle b) {
        System.out.println("Circle-Rectangle collision");
    }

    // Build the dispatch table once
    private static final DispatchTable2 table =
        new DispatchTable2().autoregister(Collisions.class);

    // Public API — dispatches based on runtime types of both arguments
    public static void handleCollision(Shape a, Shape b) {
        table.dispatch(a, b);
    }
}
```

When `handleCollision` is called with a `Circle` and a `Rectangle`, JMDispatch routes to the specific `(Circle, Rectangle)` handler. If no exact match exists, it falls back to the closest ancestor match.

### Three-or-more-argument dispatch

```java
private static final DispatchTableN table =
    new DispatchTableN(3).autoregister(MyHandlers.class);

// Dispatch on three arguments
table.dispatch(arg1, arg2, arg3);
```

## Dispatch Algorithm

- Computes **inheritance distance** (number of `extends` steps) from each actual argument type to each registered parameter type
- Uses the **Euclidean norm** (sum of squared distances) to rank matches
- Selects the handler with the lowest total distance
- Throws `DispatchAmbiguousException` if multiple handlers tie
- Throws `DispatchNoMatchException` if no compatible handler exists
- **Caches** resolved dispatches for fast repeated lookups

## API Reference

| Class | Description |
|---|---|
| `@Dispatch` | Annotation to mark static methods as dispatch handlers |
| `DispatchTable2` | Dispatch table optimized for 2-argument dispatch |
| `DispatchTableN` | Dispatch table for N-argument dispatch (N specified at construction) |
| `DispatchNoMatchException` | Thrown when no handler matches the argument types |
| `DispatchAmbiguousException` | Thrown when multiple handlers match with equal distance |

## Requirements

- **Java 22+**
- **Maven** for building

## Building

```bash
mvn clean package
```

## Dependencies

- [ASM 9.9.1](https://asm.ow2.io/) — bytecode generation (compile-time + runtime)
- [JUnit 4.13](https://junit.org/junit4/) — testing only

## License

See repository for license details.