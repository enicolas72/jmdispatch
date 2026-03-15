package net.eric_nicolas.jmdispatch.sample.collision.visitor;

import net.eric_nicolas.jmdispatch.sample.collision.Asteroid;
import net.eric_nicolas.jmdispatch.sample.collision.Laser;
import net.eric_nicolas.jmdispatch.sample.collision.Spaceship;

public interface Collidable {
    String collideWith(Collidable other);
    String collideWithSpaceship(Spaceship s);
    String collideWithAsteroid(Asteroid a);
    String collideWithLaser(Laser l);
}
