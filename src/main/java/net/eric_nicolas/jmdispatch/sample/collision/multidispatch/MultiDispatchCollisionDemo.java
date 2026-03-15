package net.eric_nicolas.jmdispatch.sample.collision.multidispatch;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable2;
import net.eric_nicolas.jmdispatch.sample.collision.*;

import java.util.ArrayList;
import java.util.List;

public class MultiDispatchCollisionDemo {

    private static final DispatchTable2 TABLE = new DispatchTable2()
            .autoregister(MultiDispatchCollisionDemo.class);

    // --- Spaceship + Spaceship ---
    @Dispatch
    public static String collide(Spaceship a, Spaceship b) {
        return a.getName() + " and " + b.getName() + " collide! Both take hull damage.";
    }

    // --- Spaceship + Asteroid (both orderings) ---
    @Dispatch
    public static String collide(Spaceship s, Asteroid a) {
        return s.getName() + " hit by asteroid " + a.getName() + " (size " + a.getSize() + "). Ship damaged!";
    }

    @Dispatch
    public static String collide(Asteroid a, Spaceship s) {
        return s.getName() + " hit by asteroid " + a.getName() + " (size " + a.getSize() + "). Ship damaged!";
    }

    // --- Spaceship + Laser (both orderings) ---
    @Dispatch
    public static String collide(Spaceship s, Laser l) {
        return s.getName() + " hit by laser " + l.getName() + " (power " + l.getPower() + "). Shields(" + s.getShieldStrength() + ") absorb impact.";
    }

    @Dispatch
    public static String collide(Laser l, Spaceship s) {
        return s.getName() + " hit by laser " + l.getName() + " (power " + l.getPower() + "). Shields(" + s.getShieldStrength() + ") absorb impact.";
    }

    // --- Asteroid + Asteroid ---
    @Dispatch
    public static String collide(Asteroid a, Asteroid b) {
        return a.getName() + " and " + b.getName() + " collide! Both shatter into fragments.";
    }

    // --- Asteroid + Laser (both orderings) ---
    @Dispatch
    public static String collide(Asteroid a, Laser l) {
        return "Laser " + l.getName() + " vaporizes asteroid " + a.getName() + "!";
    }

    @Dispatch
    public static String collide(Laser l, Asteroid a) {
        return "Laser " + l.getName() + " vaporizes asteroid " + a.getName() + "!";
    }

    // --- Laser + Laser ---
    @Dispatch
    public static String collide(Laser a, Laser b) {
        return "Lasers " + a.getName() + " and " + b.getName() + " cross harmlessly.";
    }

    // --- Fallback ---
    @Dispatch
    public static String collide(GameObject a, GameObject b) {
        return "Unknown collision between " + a.getName() + " and " + b.getName() + ".";
    }

    public List<String> run() {
        Spaceship ship1 = new Spaceship("Ship1", 80);
        Spaceship ship2 = new Spaceship("Ship2", 60);
        Asteroid asteroid1 = new Asteroid("Rock1", 3);
        Asteroid asteroid2 = new Asteroid("Rock2", 1);
        Laser laser1 = new Laser("Beam1", 50);
        Laser laser2 = new Laser("Beam2", 30);

        List<String> results = new ArrayList<>();
        results.add((String) TABLE.dispatch(ship1, ship2));
        results.add((String) TABLE.dispatch(ship1, asteroid1));
        results.add((String) TABLE.dispatch(ship1, laser1));
        results.add((String) TABLE.dispatch(asteroid1, asteroid2));
        results.add((String) TABLE.dispatch(asteroid1, laser1));
        results.add((String) TABLE.dispatch(laser1, laser2));
        return results;
    }
}
