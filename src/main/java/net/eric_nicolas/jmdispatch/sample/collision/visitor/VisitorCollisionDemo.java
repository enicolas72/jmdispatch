package net.eric_nicolas.jmdispatch.sample.collision.visitor;

import java.util.ArrayList;
import java.util.List;

public class VisitorCollisionDemo {

    public List<String> run() {
        CollidableSpaceship ship1 = new CollidableSpaceship("Ship1", 80);
        CollidableSpaceship ship2 = new CollidableSpaceship("Ship2", 60);
        CollidableAsteroid asteroid1 = new CollidableAsteroid("Rock1", 3);
        CollidableAsteroid asteroid2 = new CollidableAsteroid("Rock2", 1);
        CollidableLaser laser1 = new CollidableLaser("Beam1", 50);
        CollidableLaser laser2 = new CollidableLaser("Beam2", 30);

        List<String> results = new ArrayList<>();
        results.add(ship1.collideWith(ship2));
        results.add(ship1.collideWith(asteroid1));
        results.add(ship1.collideWith(laser1));
        results.add(asteroid1.collideWith(asteroid2));
        results.add(asteroid1.collideWith(laser1));
        results.add(laser1.collideWith(laser2));
        return results;
    }
}
