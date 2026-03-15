package net.eric_nicolas.jmdispatch.sample.collision;

public class Laser extends GameObject {
    private final int power;

    public Laser(String name, int power) {
        super(name);
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
