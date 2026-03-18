# JMDispatch

A pure Java **multiple dispatch** (multimethod) framework that selects method implementations at runtime based on the actual types of all arguments, not just the receiver.

Java's built-in virtual dispatch is single dispatch — the method called depends only on the runtime type of `this`. JMDispatch extends this to **one or more arguments**, choosing the best-matching registered handler based on all argument types simultaneously.

## Quick Start

### Maven dependency

```xml
<dependency>
    <groupId>net.eric-nicolas</groupId>
    <artifactId>jmdispatch</artifactId>
    <version>1.2</version>
</dependency>
```

### Single-argument dispatch

```java
import net.eric_nicolas.jmdispatch.*;

public class Shapes {

    @Dispatch
    public static String describe(Shape s) { return "generic shape"; }

    @Dispatch
    public static String describe(Circle c) { return "circle r=" + c.radius; }

    private static final DispatchTable table =
        DispatchTable.factory(1).autoregister(Shapes.class);

    public static String describe(Object obj) {
        return (String) table.dispatch(obj);
    }
}
```

Calling `describe(myCircle)` routes to the `Circle` handler; passing any other `Shape` subclass falls back to the generic handler.

### Two-argument dispatch

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

    private static final DispatchTable table =
        DispatchTable.factory(2).autoregister(Collisions.class);

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
DispatchTable table = DispatchTable.factory(2)
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
DispatchTable table = DispatchTable.factory(3)
    .autoregister(MyHandlers.class);    // static handlers
    // or .autoregister(new MyHandlers());  // instance handlers

Object result = table.dispatch(arg1, arg2, arg3);
```

## How It Works

1. Annotate methods with `@Dispatch` (static or instance)
2. Create a dispatch table and auto-register handlers from a class or instance
3. Call `dispatch(...)` — the framework finds the closest matching handler by computing inheritance distance across all arguments

Under the hood, JMDispatch uses **ASM bytecode generation** to create functor implementations that invoke your methods directly, avoiding reflection overhead at dispatch time.

## Dispatch Algorithm

- Computes **inheritance distance** (number of `extends` steps) from each actual argument type to each registered parameter type
- Uses the **Euclidean norm** (sum of squared distances) to rank matches
- Selects the handler with the lowest total distance
- Throws `DispatchAmbiguousException` if multiple handlers tie
- Throws `DispatchNoMatchException` if no compatible handler exists
- **Caches** resolved dispatches for fast repeated lookups — first call pays the lookup cost, subsequent calls hit the fast exact-match path
- Handler parameter types must be **concrete classes** (not interfaces or abstract classes)
- `@Dispatch` methods must be **concrete** (not abstract)
- All errors are typed: `InvalidDispatchException` at registration, `DispatchNoMatchException` / `DispatchAmbiguousException` at dispatch

## Design Decisions

**Concrete class hierarchies only.** Dispatch operates on concrete classes, not interfaces or abstract classes. Inheritance distance is computed by walking the superclass chain (`getSuperclass()`), which is well-defined and unambiguous — each class has exactly one superclass path to `Object`. Interfaces would introduce multiple inheritance paths (diamond problem), making distance computation ambiguous and order-dependent. Since you always dispatch on *actual objects* (which are always instances of concrete classes), restricting handler parameter types to concrete classes is both simpler and correct.

**Linear scan for exact match.** The `findExact()` lookup uses a linear scan with pointer comparison (`keys[i][0] == type1`), not a `HashMap`. Benchmarking showed that `HashMap` variants (including zero-allocation `ConcurrentHashMap` + `ThreadLocal` probe keys + identity hash codes) made exact-hit dispatch 4-7x slower (2.2 → 8.8–14.2 ns/op for 2 handlers). The pointer-comparison loop is so tight that even at 20 handlers (6.8 ns) it beats the `HashMap`'s constant overhead. The crossover point would be ~50+ handlers, which is far beyond typical use.

**ASM bytecode generation.** Each registered handler gets a generated functor class that calls the target method directly via `invokeStatic` or `invokeVirtual`. This avoids all reflection overhead at dispatch time — the generated functors are essentially zero-overhead wrappers.

**Typed exceptions.** All exceptions are typed: `DispatchNoMatchException` and `DispatchAmbiguousException` for dispatch-time errors, `InvalidDispatchException` for registration-time validation errors (abstract methods, interface/abstract parameter types, duplicate signatures, wrong argument count). No raw `RuntimeException` in user-facing paths.

## Sample: Game Object Collision

The [collision sample](SAMPLE-COLLISION.md) compares jmdispatch against the classic **visitor pattern** for the canonical double-dispatch problem: game object collision resolution where behavior depends on both object types. It implements the same 6 collision pairs (Spaceship, Asteroid, Laser) using both approaches and shows how multi-dispatch eliminates the interface ceremony, wrapper classes, and scattered reverse-dispatch methods that the visitor pattern requires.

## Sample: Multi-Format Serialization

The [serialization sample](SAMPLE-SERIALIZATION.md) tackles the **2D dispatch matrix** problem: serializing domain objects (User, Product, Order) to multiple formats (JSON, XML, Binary, CSV) where behavior depends on both the object type and the target format. It compares three approaches — format logic in domain objects, instanceof chains in serializers, and multi-dispatch — showing how jmdispatch is the only solution that scales cleanly in both dimensions without modifying existing code.

## Known Issues

<!-- FIXME: @Dispatch methods must currently be `public`, not package-private.
     The ASM-generated functor classes are loaded by a separate classloader
     (impl.FunctorImplementationBuilderAbstract$MyClassLoader), which cannot access
     package-private methods in the registering class. This is a framework limitation
     that should be fixed — ideally the generated classes should be defined in the
     same package/classloader as the handler, or the framework should use
     MethodHandles.Lookup to bypass access restrictions. -->

- **`@Dispatch` methods must be `public`.** The ASM-generated functor classes are loaded by an internal classloader that cannot access package-private methods. Declaring handlers as `public` is the current workaround. A future fix should define generated classes in the same classloader/package as the handler, or use `MethodHandles.Lookup` to bypass access restrictions.

## Performance

The hot path (exact-match, warm cache) benchmarks at **~2-7 ns/op** depending on table size. The library is designed for dispatch-heavy patterns like event handling and message routing where this overhead is negligible.

## API Reference

| Class | Description |
|---|---|
| `@Dispatch` | Annotation to mark methods (static or instance) as dispatch handlers |
| `DispatchTable` | Dispatch table interface — create via `DispatchTable.factory(n)` where n is the number of dispatch arguments |
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
