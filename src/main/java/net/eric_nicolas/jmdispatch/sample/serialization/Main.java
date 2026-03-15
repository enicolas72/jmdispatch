package net.eric_nicolas.jmdispatch.sample.serialization;

import net.eric_nicolas.jmdispatch.sample.serialization.domainmethods.DomainMethodsSerializationDemo;
import net.eric_nicolas.jmdispatch.sample.serialization.instanceof_.InstanceofSerializationDemo;
import net.eric_nicolas.jmdispatch.sample.serialization.multidispatch.MultiDispatchSerializationDemo;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> domainResults = new DomainMethodsSerializationDemo().run();
        List<String> instanceofResults = new InstanceofSerializationDemo().run();
        List<String> multiResults = new MultiDispatchSerializationDemo().run();

        String[] labels = {
                "User    + JSON  ",
                "User    + XML   ",
                "User    + Binary",
                "User    + CSV   ",
                "Product + JSON  ",
                "Product + XML   ",
                "Product + Binary",
                "Product + CSV   ",
                "Order   + JSON  ",
                "Order   + XML   ",
                "Order   + Binary",
                "Order   + CSV   "
        };

        System.out.println("=== Serialization Results: Three Approaches ===");
        System.out.println();

        boolean allMatch = true;
        for (int i = 0; i < labels.length; i++) {
            String d = domainResults.get(i);
            String n = instanceofResults.get(i);
            String m = multiResults.get(i);
            boolean match = d.equals(n) && n.equals(m);
            if (!match) allMatch = false;

            System.out.println("[" + labels[i] + "]");
            System.out.println("  Domain methods: " + d);
            System.out.println("  instanceof:     " + n);
            System.out.println("  Multi-dispatch: " + m);
            System.out.println("  Match: " + (match ? "YES" : "NO <---"));
            System.out.println();
        }

        System.out.println("=== All results match: " + (allMatch ? "YES" : "NO") + " ===");
        System.out.println();

        System.out.println("=== Code Complexity Comparison ===");
        System.out.println();
        System.out.println("Approach A (format logic in domain objects):");
        System.out.println("  - 3 wrapper classes, each with 4 serialization methods (12 total)");
        System.out.println("  - Adding a format: modify ALL 3 domain classes");
        System.out.println("  - Adding a type: create 1 class with ALL format methods");
        System.out.println("  - Violates Single Responsibility Principle");
        System.out.println();
        System.out.println("Approach B (instanceof chains in serializers):");
        System.out.println("  - 4 serializer methods, each with 3 instanceof branches (12 total)");
        System.out.println("  - Adding a type: modify ALL 4 serializer methods");
        System.out.println("  - Adding a format: add 1 method with ALL type branches");
        System.out.println("  - No compile-time safety, easy to miss a branch");
        System.out.println();
        System.out.println("Multi-dispatch (jmdispatch):");
        System.out.println("  - 1 class with 12 flat @Dispatch methods + 1 fallback");
        System.out.println("  - Adding a type: add N new methods (one per format)");
        System.out.println("  - Adding a format: add N new methods (one per type)");
        System.out.println("  - No existing code touched");
    }
}
