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
import application.model.Product;

public class ProductRepository {
    public List<Product> findAll() throws SQLException {
        return queryProducts("SELECT * FROM products ORDER BY productID ASC", null);
    }

    public List<Product> findByName(String productName) throws SQLException {
        return queryProducts("SELECT * FROM products WHERE productName LIKE ? ORDER BY productID ASC",
                ps -> ps.setString(1, "%" + productName.trim() + "%"));
    }

    public List<Product> findByStatus(String status) throws SQLException {
        return queryProducts("SELECT * FROM products WHERE status = ? ORDER BY productID ASC", ps -> ps.setString(1, status));
    }

    private List<Product> queryProducts(String sql, SqlConsumer<PreparedStatement> binder) throws SQLException {
        List<Product> products = new ArrayList<>();
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            if (binder != null) {
                binder.accept(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setProductID(rs.getInt("productID"));
                    product.setThumbnail(rs.getBytes("thumbnail"));
                    product.setProductName(rs.getString("productName"));
                    product.setPrice(rs.getDouble("price"));
                    product.setDescription(rs.getString("description"));
                    product.setTypeProduct(rs.getString("typeProduct"));
                    product.setStatus(rs.getString("status"));
                    product.setProductCode(rs.getString("productCode"));
                    product.setDate(rs.getDate("date"));
                    products.add(product);
                }
            }
        }
        return products;
    }

    @FunctionalInterface
    private interface SqlConsumer<T> {
        void accept(T value) throws SQLException;
    }
}
