package net.eric_nicolas.jmdispatch.sample.serialization.domainmethods;

import net.eric_nicolas.jmdispatch.sample.serialization.*;

import java.util.ArrayList;
import java.util.List;

public class DomainMethodsSerializationDemo {

    public List<String> run() {
        SerializableUser user = new SerializableUser("Alice", "alice@example.com");
        SerializableProduct product = new SerializableProduct("SKU-42", 29.99);
        SerializableOrder order = new SerializableOrder("ORD-100", 3);

        List<String> results = new ArrayList<>();

        // User x 4 formats
        results.add(user.toJson());
        results.add(user.toXml());
        results.add(user.toBinary());
        results.add(user.toCsv());

        // Product x 4 formats
        results.add(product.toJson());
        results.add(product.toXml());
        results.add(product.toBinary());
        results.add(product.toCsv());

        // Order x 4 formats
        results.add(order.toJson());
        results.add(order.toXml());
        results.add(order.toBinary());
        results.add(order.toCsv());

        return results;
    }
}
