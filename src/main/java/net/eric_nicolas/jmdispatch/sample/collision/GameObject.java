package net.eric_nicolas.jmdispatch.sample.collision;

public class GameObject {
    private final String name;

    public GameObject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
