package application.model;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class OrderItemSummary {
    private final String productName;
    private final int quantity;
    private final String size;
    private final String toppingName;
    private final double unitPrice;

    public OrderItemSummary(String productName, int quantity, String size, String toppingName, double unitPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.size = size;
        this.toppingName = toppingName;
        this.unitPrice = unitPrice;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSize() {
        return size;
    }

    public String getToppingName() {
        return toppingName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }
}
