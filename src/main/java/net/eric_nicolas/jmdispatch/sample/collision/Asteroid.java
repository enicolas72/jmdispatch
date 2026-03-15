package net.eric_nicolas.jmdispatch.sample.collision;

public class Asteroid extends GameObject {
    private final int size;

    public Asteroid(String name, int size) {
        super(name);
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
