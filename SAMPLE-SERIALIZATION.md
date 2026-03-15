# Sample: Multi-Format Serialization — Domain Methods vs instanceof vs Multi-Dispatch

This sample demonstrates how jmdispatch solves the **2D dispatch matrix** problem: serializing domain objects to multiple formats where behavior depends on both the **object type** and the **target format**.

## The Problem

You have 3 domain object types (`User`, `Product`, `Order`) and 4 output formats (JSON, XML, Binary, CSV). That's 12 type-format combinations, each producing different output. Java's single dispatch can only vary on one dimension at a time, forcing you to either scatter format logic across domain classes or scatter type logic across serializers.

## Domain Objects

```
DomainObject (type)
├── User    (name, email)
├── Product (sku, price)
└── Order   (orderId, quantity)
```

## Format Markers

```
Format (name)
├── JsonFormat
├── XmlFormat
├── BinaryFormat
└── CsvFormat
```

These are concrete classes used as dispatch keys. jmdispatch requires concrete parameter types for distance computation.

## The 12 Outputs

| | JSON | XML | Binary | CSV |
|---|---|---|---|---|
| **User** | `{"name":"Alice","email":"alice@example.com"}` | `<User><name>Alice</name>...</User>` | `[binary:User\|Alice\|alice@example.com]` | `Alice,alice@example.com` |
| **Product** | `{"sku":"SKU-42","price":29.99}` | `<Product><sku>SKU-42</sku>...</Product>` | `[binary:Product\|SKU-42\|29.99]` | `SKU-42,29.99` |
| **Order** | `{"orderId":"ORD-100","quantity":3}` | `<Order><orderId>ORD-100</orderId>...</Order>` | `[binary:Order\|ORD-100\|3]` | `ORD-100,3` |

## Solution A: Format Logic in Domain Objects (bad)

**Files:** `domainmethods/` subpackage (4 files)

Each domain class gets a wrapper subclass with one method per format:

```java
public class SerializableUser extends User {
    public String toJson()   { return "{\"name\":\"" + getName() + "\",...}"; }
    public String toXml()    { return "<User><name>" + getName() + "</name>...</User>"; }
    public String toBinary() { return "[binary:User|" + getName() + "|...]"; }
    public String toCsv()    { return getName() + "," + getEmail(); }
}
```

### Problems

- **3 wrapper classes** with 4 methods each = 12 serialization methods spread across 3 classes
- **Adding a format** (e.g., YAML): modify all 3 domain classes
- **Adding a type** (e.g., Invoice): create 1 class with all 4 format methods
- Violates Single Responsibility Principle — domain objects shouldn't know about serialization formats

## Solution B: instanceof Chains in Serializers (bad)

**Files:** `instanceof_/` subpackage (1 file)

Four serializer methods, each with instanceof checks for every type:

```java
private String serializeJson(DomainObject obj) {
    if (obj instanceof User) { User u = (User) obj; return ...; }
    if (obj instanceof Product) { Product p = (Product) obj; return ...; }
    if (obj instanceof Order) { Order o = (Order) obj; return ...; }
    throw new IllegalArgumentException("Unknown type: " + obj.getType());
}
```

### Problems

- **4 methods** with 3 instanceof branches each = 12 branches in one class
- **Adding a type** (e.g., Invoice): modify all 4 serializer methods
- **Adding a format** (e.g., YAML): add 1 method with all type branches
- No compile-time safety — miss a branch and you get a runtime exception
- Casts everywhere — the compiler can't help you

## Solution C: Multi-Dispatch with jmdispatch (correct)

**Files:** `multidispatch/` subpackage (1 file)

Flat `@Dispatch` methods, one per (type, format) pair:

```java
@Dispatch
public static String serialize(User u, JsonFormat f) {
    return "{\"name\":\"" + u.getName() + "\",\"email\":\"" + u.getEmail() + "\"}";
}

@Dispatch
public static String serialize(Product p, XmlFormat f) {
    return "<Product><sku>" + p.getSku() + "</sku>...</Product>";
}

// ... 10 more specific handlers + 1 fallback

@Dispatch
public static String serialize(DomainObject o, Format f) {
    return "<unsupported:" + o.getType() + ":" + f.getName() + ">";
}
```

A single `DispatchTable2` routes to the right handler at runtime:

```java
private static final DispatchTable2 TABLE = new DispatchTable2()
        .autoregister(MultiDispatchSerializationDemo.class);

TABLE.dispatch(user, jsonFormat);  // → serialize(User, JsonFormat)
TABLE.dispatch(order, csvFormat);  // → serialize(Order, CsvFormat)
```

### Advantages

- **1 class**, 12 flat methods + 1 fallback
- **Adding a type** (e.g., Invoice): add 4 new `@Dispatch` methods — no existing code touched
- **Adding a format** (e.g., YAML): add 3 new `@Dispatch` methods — no existing code touched
- Uses plain domain objects directly — no wrappers, no casts, no instanceof
- Fallback handler catches unhandled combinations gracefully

## Running the Sample

```bash
mvn compile
java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout) \
    net.eric_nicolas.jmdispatch.sample.serialization.Main
```

The `Main` class runs all three approaches and verifies they produce identical output for all 12 type-format combinations.

## Side-by-Side Comparison

| Dimension              | Domain Methods               | instanceof Chains            | jmdispatch                     |
|------------------------|------------------------------|------------------------------|--------------------------------|
| Files                  | 4 (3 wrappers + demo)        | 1                            | 1                              |
| Logic distribution     | 12 methods across 3 classes  | 12 branches in 4 methods     | 12 flat methods in 1 class     |
| Adding a new type      | 1 class with N format methods | Modify all N format methods  | Add N `@Dispatch` methods      |
| Adding a new format    | Modify all M type classes    | 1 method with M branches     | Add M `@Dispatch` methods      |
| Type safety            | Compile-time (wrapper types) | Runtime only (instanceof)    | Runtime dispatch, typed params |
| SRP violation          | Yes (domain knows formats)   | No                           | No                             |
| Model class dependency | Requires wrappers            | None                         | None                           |

## File Layout

```
src/main/java/net/eric_nicolas/jmdispatch/sample/serialization/
├── DomainObject.java                            # base class
├── User.java                                    # name, email
├── Product.java                                 # sku, price
├── Order.java                                   # orderId, quantity
├── Format.java                                  # base format marker
├── JsonFormat.java                              # JSON format
├── XmlFormat.java                               # XML format
├── BinaryFormat.java                            # Binary format
├── CsvFormat.java                               # CSV format
├── Main.java                                    # runs all three, compares results
├── domainmethods/
│   ├── SerializableUser.java                    # User + 4 toXxx() methods
│   ├── SerializableProduct.java                 # Product + 4 toXxx() methods
│   ├── SerializableOrder.java                   # Order + 4 toXxx() methods
│   └── DomainMethodsSerializationDemo.java      # demo runner
├── instanceof_/
│   └── InstanceofSerializationDemo.java         # 4 methods x 3 instanceof branches
└── multidispatch/
    └── MultiDispatchSerializationDemo.java      # 12 @Dispatch methods + fallback
```
