package net.eric_nicolas.jmdispatch.sample.serialization.instanceof_;

import net.eric_nicolas.jmdispatch.sample.serialization.*;

import java.util.ArrayList;
import java.util.List;

public class InstanceofSerializationDemo {

    public List<String> run() {
        User user = new User("Alice", "alice@example.com");
        Product product = new Product("SKU-42", 29.99);
        Order order = new Order("ORD-100", 3);

        Format json = new JsonFormat();
        Format xml = new XmlFormat();
        Format binary = new BinaryFormat();
        Format csv = new CsvFormat();

        List<String> results = new ArrayList<>();

        for (DomainObject obj : new DomainObject[]{user, product, order}) {
            for (Format fmt : new Format[]{json, xml, binary, csv}) {
                results.add(serialize(obj, fmt));
            }
        }

        return results;
    }

    private String serialize(DomainObject obj, Format fmt) {
        if (fmt instanceof JsonFormat) return serializeJson(obj);
        if (fmt instanceof XmlFormat) return serializeXml(obj);
        if (fmt instanceof BinaryFormat) return serializeBinary(obj);
        if (fmt instanceof CsvFormat) return serializeCsv(obj);
        throw new IllegalArgumentException("Unknown format: " + fmt.getName());
    }

    private String serializeJson(DomainObject obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return "{\"name\":\"" + u.getName() + "\",\"email\":\"" + u.getEmail() + "\"}";
        }
        if (obj instanceof Product) {
            Product p = (Product) obj;
            return "{\"sku\":\"" + p.getSku() + "\",\"price\":" + p.getPrice() + "}";
        }
        if (obj instanceof Order) {
            Order o = (Order) obj;
            return "{\"orderId\":\"" + o.getOrderId() + "\",\"quantity\":" + o.getQuantity() + "}";
        }
        throw new IllegalArgumentException("Unknown type: " + obj.getType());
    }

    private String serializeXml(DomainObject obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return "<User><name>" + u.getName() + "</name><email>" + u.getEmail() + "</email></User>";
        }
        if (obj instanceof Product) {
            Product p = (Product) obj;
            return "<Product><sku>" + p.getSku() + "</sku><price>" + p.getPrice() + "</price></Product>";
        }
        if (obj instanceof Order) {
            Order o = (Order) obj;
            return "<Order><orderId>" + o.getOrderId() + "</orderId><quantity>" + o.getQuantity() + "</quantity></Order>";
        }
        throw new IllegalArgumentException("Unknown type: " + obj.getType());
    }

    private String serializeBinary(DomainObject obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return "[binary:User|" + u.getName() + "|" + u.getEmail() + "]";
        }
        if (obj instanceof Product) {
            Product p = (Product) obj;
            return "[binary:Product|" + p.getSku() + "|" + p.getPrice() + "]";
        }
        if (obj instanceof Order) {
            Order o = (Order) obj;
            return "[binary:Order|" + o.getOrderId() + "|" + o.getQuantity() + "]";
        }
        throw new IllegalArgumentException("Unknown type: " + obj.getType());
    }

    private String serializeCsv(DomainObject obj) {
        if (obj instanceof User) {
            User u = (User) obj;
            return u.getName() + "," + u.getEmail();
        }
        if (obj instanceof Product) {
            Product p = (Product) obj;
            return p.getSku() + "," + p.getPrice();
        }
        if (obj instanceof Order) {
            Order o = (Order) obj;
            return o.getOrderId() + "," + o.getQuantity();
        }
        throw new IllegalArgumentException("Unknown type: " + obj.getType());
    }
}
