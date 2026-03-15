package net.eric_nicolas.jmdispatch.sample.serialization;

public class User extends DomainObject {
    private final String name;
    private final String email;

    public User(String name, String email) {
        super("User");
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
