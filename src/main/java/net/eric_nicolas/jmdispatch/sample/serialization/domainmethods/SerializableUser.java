package net.eric_nicolas.jmdispatch.sample.serialization.domainmethods;

import net.eric_nicolas.jmdispatch.sample.serialization.User;

public class SerializableUser extends User {

    public SerializableUser(String name, String email) {
        super(name, email);
    }

    public String toJson() {
        return "{\"name\":\"" + getName() + "\",\"email\":\"" + getEmail() + "\"}";
    }

    public String toXml() {
        return "<User><name>" + getName() + "</name><email>" + getEmail() + "</email></User>";
    }

    public String toBinary() {
        return "[binary:User|" + getName() + "|" + getEmail() + "]";
    }

    public String toCsv() {
        return getName() + "," + getEmail();
    }
}
