package net.eric_nicolas.jmdispatch.sample.collision;

public class Spaceship extends GameObject {
    private final int shieldStrength;

    public Spaceship(String name, int shieldStrength) {
        super(name);
        this.shieldStrength = shieldStrength;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }
}
