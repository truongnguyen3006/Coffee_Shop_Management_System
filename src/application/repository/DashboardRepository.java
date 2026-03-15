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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import application.Database;
import application.model.DashboardSummary;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class DashboardRepository {
    public DashboardSummary getDashboardSummary() throws SQLException {
        String customerSql = "SELECT COUNT(DISTINCT userID) AS total_customer FROM orders";
        String moneyOfDaySql = "SELECT DATE(orderDate) AS day_value, SUM(totalPrice) AS daily_total FROM orders WHERE DATE(orderDate) = CURDATE()";
        String moneyTotalSql = "SELECT SUM(totalPrice) AS total_price FROM orders";
        String productTotalSql = "SELECT SUM(quantity) AS total_product FROM order_items";

        try (Connection connection = Database.connect()) {
            int customerCount = 0;
            double revenueToday = 0;
            String revenueDate = "";
            double totalRevenue = 0;
            int totalProductSold = 0;

            try (PreparedStatement ps = connection.prepareStatement(customerSql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    customerCount = rs.getInt("total_customer");
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(moneyOfDaySql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    revenueToday = rs.getDouble("daily_total");
                    revenueDate = rs.getString("day_value");
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(moneyTotalSql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRevenue = rs.getDouble("total_price");
                }
            }
            try (PreparedStatement ps = connection.prepareStatement(productTotalSql); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalProductSold = rs.getInt("total_product");
                }
            }
            return new DashboardSummary(customerCount, revenueToday, revenueDate, totalRevenue, totalProductSold);
        }
    }

    public ObservableList<PieChart.Data> getPieChartData() throws SQLException {
        String sql = "SELECT p.typeProduct, SUM(o.quantity) AS total_quantity FROM order_items o JOIN products p ON p.productID = o.productID GROUP BY p.typeProduct";
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                data.add(new PieChart.Data(rs.getString("typeProduct"), rs.getInt("total_quantity")));
            }
        }
        return data;
    }

    public Map<String, Number> getYearlyRevenue() throws SQLException {
        return queryOrderedNumberMap(
                "SELECT YEAR(orderDate) AS time_key, SUM(totalPrice) AS total_value FROM orders GROUP BY YEAR(orderDate) ORDER BY time_key");
    }

    public Map<String, Number> getMonthlyRevenue(int year) throws SQLException {
        return queryOrderedNumberMap(
                "SELECT DATE_FORMAT(orderDate, '%Y-%m') AS time_key, SUM(totalPrice) AS total_value FROM orders WHERE YEAR(orderDate) = ? GROUP BY DATE_FORMAT(orderDate, '%Y-%m') ORDER BY time_key",
                year);
    }

    public Map<String, Number> getDailyRevenue(int month, int year) throws SQLException {
        return queryOrderedNumberMap(
                "SELECT DATE(orderDate) AS time_key, SUM(totalPrice) AS total_value FROM orders WHERE DATE_FORMAT(orderDate, '%m') = ? AND YEAR(orderDate) = ? GROUP BY DATE(orderDate) ORDER BY time_key",
                month, year);
    }

    public Map<String, Number> getRevenueLineChart() throws SQLException {
        return queryOrderedNumberMap(
                "SELECT DATE(orderDate) AS time_key, SUM(totalPrice) AS total_value FROM orders GROUP BY DATE(orderDate) ORDER BY time_key");
    }

    public ProductChartData getYearlyProductSales() throws SQLException {
        return queryProductChart(
                "SELECT p.productName, SUM(o.quantity) AS total_quantity, YEAR(orderDate) AS time_key FROM order_items o JOIN products p ON p.productID = o.productID JOIN orders od ON od.orderID = o.orderID GROUP BY p.productName, YEAR(od.orderDate) ORDER BY time_key ASC");
    }

    public ProductChartData getMonthlyProductSales(int year) throws SQLException {
        return queryProductChart(
                "SELECT p.productName, SUM(oi.quantity) AS total_quantity, DATE_FORMAT(ord.orderDate, '%Y-%m') AS time_key FROM order_items oi JOIN products p ON p.productID = oi.productID JOIN orders ord ON ord.orderID = oi.orderID WHERE YEAR(ord.orderDate) = ? GROUP BY p.productName, time_key ORDER BY time_key ASC",
                year);
    }

    public ProductChartData getDailyProductSales(int month, int year) throws SQLException {
        return queryProductChart(
                "SELECT p.productName, SUM(o.quantity) AS total_quantity, DATE(od.orderDate) AS time_key FROM order_items o JOIN products p ON p.productID = o.productID JOIN orders od ON od.orderID = o.orderID WHERE DATE_FORMAT(orderDate, '%m') = ? AND YEAR(orderDate) = ? GROUP BY p.productName, DATE(od.orderDate) ORDER BY time_key ASC",
                month, year);
    }

    private Map<String, Number> queryOrderedNumberMap(String sql, Integer... params) throws SQLException {
        Map<String, Number> data = new LinkedHashMap<>();
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("time_key"), rs.getDouble("total_value"));
                }
            }
        }
        return data;
    }

    private ProductChartData queryProductChart(String sql, Integer... params) throws SQLException {
        Map<String, Map<String, Number>> seriesMap = new LinkedHashMap<>();
        Set<String> categories = new LinkedHashSet<>();
        try (Connection connection = Database.connect(); PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("productName");
                    String timeKey = rs.getString("time_key");
                    int totalQuantity = rs.getInt("total_quantity");
                    categories.add(timeKey);
                    seriesMap.computeIfAbsent(productName, key -> new LinkedHashMap<>()).put(timeKey, totalQuantity);
                }
            }
        }
        return new ProductChartData(seriesMap, categories);
    }

    public static class ProductChartData {
        private final Map<String, Map<String, Number>> seriesMap;
        private final Set<String> categories;

        public ProductChartData(Map<String, Map<String, Number>> seriesMap, Set<String> categories) {
            this.seriesMap = seriesMap;
            this.categories = categories;
        }

        public Map<String, Map<String, Number>> getSeriesMap() {
            return seriesMap;
        }

        public Set<String> getCategories() {
            return categories;
        }
    }
}
