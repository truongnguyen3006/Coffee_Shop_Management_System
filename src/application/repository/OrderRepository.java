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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import application.Database;
import application.model.Order;
import application.model.CartDisplayItem;
import application.model.OrderConfirmationData;
import application.model.OrderItemSummary;

public class OrderRepository {
    public int createOrder(Connection connection, int userId, String address, String phone, double totalPrice, String note)
            throws SQLException {
        String sql = "INSERT INTO orders(userID, addressOrder, phoneOrder, totalPrice, notes) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, address);
            ps.setString(3, phone);
            ps.setDouble(4, totalPrice);
            ps.setString(5, note == null || note.trim().isEmpty() ? "Không có ghi chú" : note.trim());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
            throw new SQLException("Không tạo được đơn hàng mới.");
        }
    }

    public void createOrderItems(Connection connection, int orderId, List<CartDisplayItem> items) throws SQLException {
        String sql = "INSERT INTO order_items(orderID, productID, quantity, size, toppingName, unitPrice) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CartDisplayItem item : items) {
                ps.setInt(1, orderId);
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setString(4, item.getSize());
                ps.setString(5, item.getToppingNames());
                ps.setDouble(6, item.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public OrderConfirmationData getOrderConfirmationData(Connection connection, int userId, int orderId) throws SQLException {
        String sql = "SELECT u.email, u.fullName, o.addressOrder, o.orderDate, o.totalPrice, p.productName, i.quantity, i.size, i.toppingName, i.unitPrice "
                + "FROM users u JOIN orders o ON o.userID = u.userID JOIN order_items i ON i.orderID = o.orderID "
                + "JOIN products p ON p.productID = i.productID WHERE u.userID = ? AND o.orderID = ?";
        OrderConfirmationData data = new OrderConfirmationData();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean firstRow = true;
                while (rs.next()) {
                    if (firstRow) {
                        data.setEmail(rs.getString("email"));
                        data.setFullName(rs.getString("fullName"));
                        data.setAddressOrder(rs.getString("addressOrder"));
                        data.setOrderDate(rs.getTimestamp("orderDate"));
                        data.setTotalPrice(rs.getDouble("totalPrice"));
                        firstRow = false;
                    }
                    data.getItems().add(new OrderItemSummary(rs.getString("productName"), rs.getInt("quantity"),
                            rs.getString("size"), rs.getString("toppingName"), rs.getDouble("unitPrice")));
                }
            }
        }
        return data;
    }

    public List<Order> findAllOrders() throws SQLException {
        String sql = "SELECT o.orderID, o.addressOrder, o.phoneOrder, o.totalPrice, o.orderDate, o.notes, u.fullName, u.email "
                + "FROM orders o JOIN users u ON u.userID = o.userID ORDER BY orderID ASC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Timestamp orderDate = rs.getTimestamp("orderDate");
                orders.add(new Order(rs.getInt("orderID"), rs.getString("fullName"), rs.getString("email"),
                        rs.getString("addressOrder"), rs.getString("phoneOrder"), rs.getDouble("totalPrice"),
                        orderDate.toLocalDateTime(), rs.getString("notes")));
            }
        }
        return orders;
    }
}
