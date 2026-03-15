package net.eric_nicolas.jmdispatch.sample.collision;

import net.eric_nicolas.jmdispatch.sample.collision.multidispatch.MultiDispatchCollisionDemo;
import net.eric_nicolas.jmdispatch.sample.collision.visitor.VisitorCollisionDemo;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> visitorResults = new VisitorCollisionDemo().run();
        List<String> multiResults = new MultiDispatchCollisionDemo().run();

        String[] labels = {
                "Spaceship + Spaceship",
                "Spaceship + Asteroid ",
                "Spaceship + Laser    ",
                "Asteroid  + Asteroid ",
                "Asteroid  + Laser    ",
                "Laser     + Laser    "
        };

        System.out.println("=== Collision Results: Visitor vs Multi-Dispatch ===");
        System.out.println();

        boolean allMatch = true;
        for (int i = 0; i < labels.length; i++) {
            String v = visitorResults.get(i);
            String m = multiResults.get(i);
            boolean match = v.equals(m);
            if (!match) allMatch = false;

            System.out.println("[" + labels[i] + "]");
            System.out.println("  Visitor:  " + v);
            System.out.println("  Dispatch: " + m);
            System.out.println("  Match: " + (match ? "YES" : "NO <---"));
            System.out.println();
        }

        System.out.println("=== All results match: " + (allMatch ? "YES" : "NO") + " ===");
        System.out.println();

        System.out.println("=== Code Complexity Comparison ===");
        System.out.println();
        System.out.println("Visitor pattern:");
        System.out.println("  - 1 interface (Collidable) with 4 methods");
        System.out.println("  - 3 wrapper classes (CollidableSpaceship/Asteroid/Laser)");
        System.out.println("  - 9 reverse-dispatch methods spread across 3 classes");
        System.out.println("  - Adding a 4th type: modify interface + ALL 3 existing classes");
        System.out.println();
        System.out.println("Multi-dispatch (jmdispatch):");
        System.out.println("  - 1 class with 10 flat @Dispatch methods");
        System.out.println("  - Uses plain model objects directly (no wrappers)");
        System.out.println("  - Adding a 4th type: add new @Dispatch methods only");
        System.out.println("  - No existing code touched");
    }
}
