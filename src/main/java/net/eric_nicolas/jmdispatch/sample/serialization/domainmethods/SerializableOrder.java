package net.eric_nicolas.jmdispatch.sample.serialization.domainmethods;

import net.eric_nicolas.jmdispatch.sample.serialization.Order;

public class SerializableOrder extends Order {

    public SerializableOrder(String orderId, int quantity) {
        super(orderId, quantity);
    }

    public String toJson() {
        return "{\"orderId\":\"" + getOrderId() + "\",\"quantity\":" + getQuantity() + "}";
    }

    public String toXml() {
        return "<Order><orderId>" + getOrderId() + "</orderId><quantity>" + getQuantity() + "</quantity></Order>";
    }

    public String toBinary() {
        return "[binary:Order|" + getOrderId() + "|" + getQuantity() + "]";
    }

    public String toCsv() {
        return getOrderId() + "," + getQuantity();
    }
}
