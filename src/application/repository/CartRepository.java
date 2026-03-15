package application.repository;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import application.Database;
import application.model.CartDisplayItem;

public class CartRepository {
    public List<CartDisplayItem> findCartItemsByUserId(int userId) throws SQLException {
        String sql = "SELECT c.cartID, c.size, p.productName, p.thumbnail, p.price, p.productID, p.productCode, c.quantity, c.toppingHash, "
                + "(SELECT GROUP_CONCAT(t.toppingName SEPARATOR ', ') FROM cart_toppings ct "
                + " JOIN toppings t ON ct.toppingID = t.toppingID WHERE ct.cartID = c.cartID) AS toppingNames, "
                + "(SELECT IFNULL(SUM(t.toppingPrice), 0) FROM cart_toppings ct "
                + " JOIN toppings t ON ct.toppingID = t.toppingID WHERE ct.cartID = c.cartID) AS toppingTotal "
                + "FROM products p JOIN carts c ON c.productID = p.productID JOIN users u ON c.userID = u.userID "
                + "WHERE u.userID = ?";
        List<CartDisplayItem> items = new ArrayList<>();
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String size = rs.getString("size");
                    double basePrice = rs.getDouble("price");
                    double toppingPrice = rs.getDouble("toppingTotal");
                    double sizeAdditional = 0;
                    if ("M".equalsIgnoreCase(size)) {
                        sizeAdditional = 6000;
                    } else if ("L".equalsIgnoreCase(size)) {
                        sizeAdditional = 10000;
                    }
                    String toppingNames = rs.getString("toppingNames");
                    if (toppingNames == null || toppingNames.trim().isEmpty()) {
                        toppingNames = "Không topping";
                    }
                    items.add(new CartDisplayItem(rs.getInt("cartID"), rs.getInt("productID"), rs.getString("productCode"),
                            rs.getString("productName"), rs.getBytes("thumbnail"), size, rs.getInt("quantity"),
                            rs.getString("toppingHash"), toppingNames, basePrice + sizeAdditional + toppingPrice,
                            basePrice + sizeAdditional + toppingPrice));
                }
            }
        }
        return items;
    }

    public void clearCartByUserId(Connection connection, int userId) throws SQLException {
        String sql = "DELETE FROM carts WHERE userID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}
