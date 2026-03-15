package application.model;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class CartDisplayItem {
    private final int cartId;
    private final int productId;
    private final String productCode;
    private final String productName;
    private final byte[] thumbnail;
    private final String size;
    private final int quantity;
    private final String toppingHash;
    private final String toppingNames;
    private final double unitPrice;
    private final double finalPrice;

    public CartDisplayItem(int cartId, int productId, String productCode, String productName, byte[] thumbnail,
            String size, int quantity, String toppingHash, String toppingNames, double unitPrice, double finalPrice) {
        this.cartId = cartId;
        this.productId = productId;
        this.productCode = productCode;
        this.productName = productName;
        this.thumbnail = thumbnail;
        this.size = size;
        this.quantity = quantity;
        this.toppingHash = toppingHash;
        this.toppingNames = toppingNames;
        this.unitPrice = unitPrice;
        this.finalPrice = finalPrice;
    }

    public int getCartId() {
        return cartId;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public String getSize() {
        return size;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getToppingHash() {
        return toppingHash;
    }

    public String getToppingNames() {
        return toppingNames;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }
}
