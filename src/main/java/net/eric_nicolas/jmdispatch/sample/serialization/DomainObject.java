package net.eric_nicolas.jmdispatch.sample.serialization;

public class DomainObject {
    private final String type;

    public DomainObject(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
