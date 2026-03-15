package net.eric_nicolas.jmdispatch.sample.serialization.multidispatch;

import net.eric_nicolas.jmdispatch.Dispatch;
import net.eric_nicolas.jmdispatch.DispatchTable2;
import net.eric_nicolas.jmdispatch.sample.serialization.*;

import java.util.ArrayList;
import java.util.List;

public class MultiDispatchSerializationDemo {

    private static final DispatchTable2 TABLE = new DispatchTable2()
            .autoregister(MultiDispatchSerializationDemo.class);

    // --- User ---

    @Dispatch
    public static String serialize(User u, JsonFormat f) {
        return "{\"name\":\"" + u.getName() + "\",\"email\":\"" + u.getEmail() + "\"}";
    }

    @Dispatch
    public static String serialize(User u, XmlFormat f) {
        return "<User><name>" + u.getName() + "</name><email>" + u.getEmail() + "</email></User>";
    }

    @Dispatch
    public static String serialize(User u, BinaryFormat f) {
        return "[binary:User|" + u.getName() + "|" + u.getEmail() + "]";
    }

    @Dispatch
    public static String serialize(User u, CsvFormat f) {
        return u.getName() + "," + u.getEmail();
    }

    // --- Product ---

    @Dispatch
    public static String serialize(Product p, JsonFormat f) {
        return "{\"sku\":\"" + p.getSku() + "\",\"price\":" + p.getPrice() + "}";
    }

    @Dispatch
    public static String serialize(Product p, XmlFormat f) {
        return "<Product><sku>" + p.getSku() + "</sku><price>" + p.getPrice() + "</price></Product>";
    }

    @Dispatch
    public static String serialize(Product p, BinaryFormat f) {
        return "[binary:Product|" + p.getSku() + "|" + p.getPrice() + "]";
    }

    @Dispatch
    public static String serialize(Product p, CsvFormat f) {
        return p.getSku() + "," + p.getPrice();
    }

    // --- Order ---

    @Dispatch
    public static String serialize(Order o, JsonFormat f) {
        return "{\"orderId\":\"" + o.getOrderId() + "\",\"quantity\":" + o.getQuantity() + "}";
    }

    @Dispatch
    public static String serialize(Order o, XmlFormat f) {
        return "<Order><orderId>" + o.getOrderId() + "</orderId><quantity>" + o.getQuantity() + "</quantity></Order>";
    }

    @Dispatch
    public static String serialize(Order o, BinaryFormat f) {
        return "[binary:Order|" + o.getOrderId() + "|" + o.getQuantity() + "]";
    }

    @Dispatch
    public static String serialize(Order o, CsvFormat f) {
        return o.getOrderId() + "," + o.getQuantity();
    }

    // --- Fallback ---

    @Dispatch
    public static String serialize(DomainObject o, Format f) {
        return "<unsupported:" + o.getType() + ":" + f.getName() + ">";
    }

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
                results.add((String) TABLE.dispatch(obj, fmt));
            }
        }

        return results;
    }
}
