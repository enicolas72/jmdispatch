package net.eric_nicolas.jmdispatch.sample.serialization.domainmethods;

import net.eric_nicolas.jmdispatch.sample.serialization.Product;

public class SerializableProduct extends Product {

    public SerializableProduct(String sku, double price) {
        super(sku, price);
    }

    public String toJson() {
        return "{\"sku\":\"" + getSku() + "\",\"price\":" + getPrice() + "}";
    }

    public String toXml() {
        return "<Product><sku>" + getSku() + "</sku><price>" + getPrice() + "</price></Product>";
    }

    public String toBinary() {
        return "[binary:Product|" + getSku() + "|" + getPrice() + "]";
    }

    public String toCsv() {
        return getSku() + "," + getPrice();
    }
}
