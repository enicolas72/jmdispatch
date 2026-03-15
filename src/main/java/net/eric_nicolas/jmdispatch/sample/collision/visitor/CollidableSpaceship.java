package net.eric_nicolas.jmdispatch.sample.collision.visitor;

import net.eric_nicolas.jmdispatch.sample.collision.Asteroid;
import net.eric_nicolas.jmdispatch.sample.collision.Laser;
import net.eric_nicolas.jmdispatch.sample.collision.Spaceship;

public class CollidableSpaceship extends Spaceship implements Collidable {

    public CollidableSpaceship(String name, int shieldStrength) {
        super(name, shieldStrength);
    }

    @Override
    public String collideWith(Collidable other) {
        return other.collideWithSpaceship(this);
    }

    @Override
    public String collideWithSpaceship(Spaceship s) {
        return s.getName() + " and " + getName() + " collide! Both take hull damage.";
    }

    @Override
    public String collideWithAsteroid(Asteroid a) {
        return getName() + " hit by asteroid " + a.getName() + " (size " + a.getSize() + "). Ship damaged!";
    }

    @Override
    public String collideWithLaser(Laser l) {
        return getName() + " hit by laser " + l.getName() + " (power " + l.getPower() + "). Shields(" + getShieldStrength() + ") absorb impact.";
    }
}
