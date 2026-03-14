# JMDispatch

A pure Java **multiple dispatch** (multimethod) framework that selects method implementations at runtime based on the actual types of all arguments, not just the receiver.

Java's built-in virtual dispatch is single dispatch — the method called depends only on the runtime type of `this`. JMDispatch extends this to **two or more arguments**, choosing the best-matching registered handler based on all argument types simultaneously.

## How It Works

1. Annotate methods with `@Dispatch` (static or instance)
2. Create a dispatch table and auto-register handlers from a class or instance
3. Call `dispatch(...)` — the framework finds the closest matching handler by computing inheritance distance across all arguments

Under the hood, JMDispatch uses **ASM bytecode generation** to create functor implementations that invoke your methods directly, avoiding reflection overhead at dispatch time.

## Quick Start

### Static handlers

```java
import net.eric_nicolas.jmdispatch.*;

public class Collisions {

    @Dispatch
    public static void collide(Shape a, Shape b) {
        System.out.println("Generic shape collision");
    }

    @Dispatch
    public static void collide(Circle a, Rectangle b) {
        System.out.println("Circle-Rectangle collision");
    }

    private static final DispatchTable2 table =
        new DispatchTable2().autoregister(Collisions.class);

    public static void handleCollision(Shape a, Shape b) {
        table.dispatch(a, b);
    }
}
```

When `handleCollision` is called with a `Circle` and a `Rectangle`, JMDispatch routes to the specific `(Circle, Rectangle)` handler. If no exact match exists, it falls back to the closest ancestor match.

### Instance handlers

Handlers can be instance methods, giving them natural access to instance state:

```java
public class CollisionHandler {
    private final Logger log;

    public CollisionHandler(Logger log) { this.log = log; }

    @Dispatch
    public void collide(Shape a, Shape b) {
        log.info("Generic shape collision");
    }

    @Dispatch
    public void collide(Circle a, Rectangle b) {
        log.info("Circle-Rectangle collision");
    }
}

// Register an instance — its @Dispatch methods are bound to it
DispatchTable2 table = new DispatchTable2()
    .autoregister(new CollisionHandler(myLogger));

table.dispatch(myCircle, myRect); // calls instance method on the registered handler
```

Static and instance `@Dispatch` methods can coexist in the same class.

### Return values

Handlers can return any type. The return value is boxed and returned as `Object` from `dispatch()`. Void handlers return `null`.

```java
@Dispatch
public static int area(Circle c, Scale s) {
    return (int)(Math.PI * c.radius * c.radius * s.factor);
}

// ...
Object result = table.dispatch(myCircle, myScale); // returns a boxed Integer
```

### Three-or-more-argument dispatch

```java
DispatchTableN table = new DispatchTableN(3)
    .autoregister(MyHandlers.class);    // static handlers
    // or .autoregister(new MyHandlers());  // instance handlers

Object result = table.dispatch(arg1, arg2, arg3);
```

## Dispatch Algorithm

- Computes **inheritance distance** (number of `extends` steps) from each actual argument type to each registered parameter type
- Uses the **Euclidean norm** (sum of squared distances) to rank matches
- Selects the handler with the lowest total distance
- Throws `DispatchAmbiguousException` if multiple handlers tie
- Throws `DispatchNoMatchException` if no compatible handler exists
- **Caches** resolved dispatches for fast repeated lookups
- Handler parameter types must be **concrete classes** (not interfaces or abstract classes)
- `@Dispatch` methods must be **concrete** (not abstract)
- All errors are typed: `InvalidDispatchException` at registration, `DispatchNoMatchException` / `DispatchAmbiguousException` at dispatch

## API Reference

| Class | Description |
|---|---|
| `@Dispatch` | Annotation to mark methods (static or instance) as dispatch handlers |
| `DispatchTable2` | Dispatch table optimized for 2-argument dispatch |
| `DispatchTableN` | Dispatch table for N-argument dispatch (N specified at construction) |
| `DispatchNoMatchException` | Thrown when no handler matches the argument types |
| `DispatchAmbiguousException` | Thrown when multiple handlers match with equal distance |
| `InvalidDispatchException` | Thrown when handler registration is invalid (abstract method, interface/abstract parameter type, etc.) |

## Requirements

- **Java 11+**
- **Maven** for building

## Building

```bash
mvn clean package
```

## Dependencies

- [ASM 9.9.1](https://asm.ow2.io/) — bytecode generation (compile-time + runtime)
- [JUnit 4.13.1](https://junit.org/junit4/) — testing only

## License

This project is licensed under the [GNU Lesser General Public License v3.0](LICENSE.txt) — you can use it in proprietary applications, but modifications to the library itself must remain open source.