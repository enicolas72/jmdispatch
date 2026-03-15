package net.eric_nicolas.jmdispatch.sample.serialization;

public class Product extends DomainObject {
    private final String sku;
    private final double price;

    public Product(String sku, double price) {
        super("Product");
        this.sku = sku;
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public double getPrice() {
        return price;
    }
}
