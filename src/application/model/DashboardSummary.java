package application.model;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

public class DashboardSummary {
    private final int customerCount;
    private final double revenueToday;
    private final String revenueDate;
    private final double totalRevenue;
    private final int totalProductsSold;

    public DashboardSummary(int customerCount, double revenueToday, String revenueDate, double totalRevenue,
            int totalProductsSold) {
        this.customerCount = customerCount;
        this.revenueToday = revenueToday;
        this.revenueDate = revenueDate;
        this.totalRevenue = totalRevenue;
        this.totalProductsSold = totalProductsSold;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public double getRevenueToday() {
        return revenueToday;
    }

    public String getRevenueDate() {
        return revenueDate;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getTotalProductsSold() {
        return totalProductsSold;
    }
}
