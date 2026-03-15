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

import application.repository.DashboardRepository;

public class RevenueService {
    private final DashboardRepository dashboardRepository;

    public RevenueService() {
        this(new DashboardRepository());
    }

    public RevenueService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
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
}
