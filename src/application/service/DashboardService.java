package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.SQLException;
import java.util.Map;

import application.model.DashboardSummary;
import application.repository.DashboardRepository;
import application.repository.DashboardRepository.ProductChartData;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

public class DashboardService {
    private final DashboardRepository dashboardRepository;

    public DashboardService() {
        this(new DashboardRepository());
    }

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardSummary getDashboardSummary() throws SQLException {
        return dashboardRepository.getDashboardSummary();
    }

    public ObservableList<PieChart.Data> getPieChartData() throws SQLException {
        return dashboardRepository.getPieChartData();
    }

    public Map<String, Number> getYearlyRevenue() throws SQLException {
        return dashboardRepository.getYearlyRevenue();
    }

    public Map<String, Number> getMonthlyRevenue(int year) throws SQLException {
        return dashboardRepository.getMonthlyRevenue(year);
    }

    public Map<String, Number> getDailyRevenue(int month, int year) throws SQLException {
        return dashboardRepository.getDailyRevenue(month, year);
    }

    public Map<String, Number> getRevenueLineChart() throws SQLException {
        return dashboardRepository.getRevenueLineChart();
    }

    public ProductChartData getYearlyProductSales() throws SQLException {
        return dashboardRepository.getYearlyProductSales();
    }

    public ProductChartData getMonthlyProductSales(int year) throws SQLException {
        return dashboardRepository.getMonthlyProductSales(year);
    }

    public ProductChartData getDailyProductSales(int month, int year) throws SQLException {
        return dashboardRepository.getDailyProductSales(month, year);
    }
}
