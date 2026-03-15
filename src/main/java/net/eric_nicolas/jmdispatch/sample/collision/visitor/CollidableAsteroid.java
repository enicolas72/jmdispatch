package net.eric_nicolas.jmdispatch.sample.collision.visitor;

import net.eric_nicolas.jmdispatch.sample.collision.Asteroid;
import net.eric_nicolas.jmdispatch.sample.collision.Laser;
import net.eric_nicolas.jmdispatch.sample.collision.Spaceship;

public class CollidableAsteroid extends Asteroid implements Collidable {

    public CollidableAsteroid(String name, int size) {
        super(name, size);
    }

    @Override
    public String collideWith(Collidable other) {
        return other.collideWithAsteroid(this);
    }

    @Override
    public String collideWithSpaceship(Spaceship s) {
        return s.getName() + " hit by asteroid " + getName() + " (size " + getSize() + "). Ship damaged!";
    }

    @Override
    public String collideWithAsteroid(Asteroid a) {
        return a.getName() + " and " + getName() + " collide! Both shatter into fragments.";
    }

    @Override
    public String collideWithLaser(Laser l) {
        return "Laser " + l.getName() + " vaporizes asteroid " + getName() + "!";
    }
}
