package net.eric_nicolas.jmdispatch.sample.serialization;

public class Order extends DomainObject {
    private final String orderId;
    private final int quantity;

    public Order(String orderId, int quantity) {
        super("Order");
        this.orderId = orderId;
        this.quantity = quantity;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getQuantity() {
        return quantity;
    }
}
