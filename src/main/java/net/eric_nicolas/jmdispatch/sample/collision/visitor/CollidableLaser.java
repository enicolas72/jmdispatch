package net.eric_nicolas.jmdispatch.sample.collision.visitor;

import net.eric_nicolas.jmdispatch.sample.collision.Asteroid;
import net.eric_nicolas.jmdispatch.sample.collision.Laser;
import net.eric_nicolas.jmdispatch.sample.collision.Spaceship;

public class CollidableLaser extends Laser implements Collidable {

    public CollidableLaser(String name, int power) {
        super(name, power);
    }

    @Override
    public String collideWith(Collidable other) {
        return other.collideWithLaser(this);
    }

    @Override
    public String collideWithSpaceship(Spaceship s) {
        return s.getName() + " hit by laser " + getName() + " (power " + getPower() + "). Shields(" + s.getShieldStrength() + ") absorb impact.";
    }

    @Override
    public String collideWithAsteroid(Asteroid a) {
        return "Laser " + getName() + " vaporizes asteroid " + a.getName() + "!";
    }

    @Override
    public String collideWithLaser(Laser l) {
        return "Lasers " + l.getName() + " and " + getName() + " cross harmlessly.";
    }
}
