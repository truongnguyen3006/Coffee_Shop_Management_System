package application.service;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import application.AlertMessage;
import application.Database;
import application.model.Order;
import application.model.CartDisplayItem;
import application.model.OrderConfirmationData;
import application.model.OrderItemSummary;
import application.repository.CartRepository;
import application.repository.OrderRepository;
import application.util.AppLogger;
import application.util.ValidationUtil;
import javafx.scene.control.Alert.AlertType;

public class OrderService {
    private static final Logger LOGGER = AppLogger.getLogger(OrderService.class);
    private static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final EmailService emailService;

    public OrderService() {
        this(new OrderRepository(), new CartRepository(), new EmailService());
    }

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.emailService = emailService;
    }

    public int placeOrder(int userId, String address, String phone, double totalPrice, String note, List<CartDisplayItem> items,
            String deliveryTimeText) throws SQLException {
        if (!ValidationUtil.isValidAddress(address)) {
            throw new IllegalArgumentException("Địa chỉ giao hàng chưa hợp lệ.");
        }
        if (!ValidationUtil.isValidPhoneNumber(phone)) {
            throw new IllegalArgumentException("Số điện thoại chưa hợp lệ.");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng đang trống.");
        }
        try (Connection connection = Database.connect()) {
            connection.setAutoCommit(false);
            try {
                int orderId = orderRepository.createOrder(connection, userId, address, phone, totalPrice, note);
                orderRepository.createOrderItems(connection, orderId, items);
                cartRepository.clearCartByUserId(connection, userId);
                OrderConfirmationData confirmationData = orderRepository.getOrderConfirmationData(connection, userId, orderId);
                connection.commit();
                sendConfirmationEmail(confirmationData, orderId, note, deliveryTimeText);
                return orderId;
            } catch (SQLException ex) {
                connection.rollback();
                AppLogger.error(LOGGER, "Đặt hàng thất bại cho userId=" + userId, ex);
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private void sendConfirmationEmail(OrderConfirmationData data, int orderId, String note, String deliveryTimeText) {
        if (data.getEmail() == null || data.getEmail().trim().isEmpty()) {
            return;
        }
        StringBuilder productDetails = new StringBuilder();
        for (OrderItemSummary item : data.getItems()) {
            double price = item.getUnitPrice() * item.getQuantity();
            productDetails.append("- ").append(item.getProductName()).append(" (").append(item.getSize()).append(") x ")
                    .append(item.getQuantity()).append(": ").append(PRICE_FORMATTER.format(price)).append("đ");
            if (item.getToppingName() != null && !item.getToppingName().trim().isEmpty()
                    && !"Không topping".equalsIgnoreCase(item.getToppingName())) {
                productDetails.append(" (").append(item.getToppingName()).append(")");
            }
            productDetails.append(System.lineSeparator());
        }
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(data.getOrderDate());
        String normalizedNote = (note == null || note.trim().isEmpty()) ? "Ghi chú: Không có ghi chú"
                : "Ghi chú: " + note.trim();
        String content = "Chào " + data.getFullName() + ",\n\n" + "Cảm ơn bạn đã đặt nước tại CoffeeShop!\n\n"
                + "Mã đơn hàng: #" + orderId + "\n" + "Thời gian đặt: Ngày " + formattedDate + "\n\n"
                + "Sản phẩm:\n" + productDetails + "\n" + normalizedNote + "\n" + "Phí vận chuyển: 25,000đ\n"
                + "Tổng tiền: " + PRICE_FORMATTER.format(data.getTotalPrice()) + "đ (Bao gồm VAT 10%)\n"
                + "Thanh toán: Đã thanh toán\n\n" + "Địa chỉ giao: " + data.getAddressOrder() + "\n\n"
                + "Thời gian giao dự kiến: " + deliveryTimeText + " ngày hôm nay.\n"
                + "Mọi thắc mắc vui lòng liên hệ: 1800 123 456\n\n"
                + "CoffeeShop xin cảm ơn và hẹn gặp lại!";
        try {
            emailService.sendOrderConfirmation(data.getEmail(), content);
        } catch (MessagingException e) {
            AppLogger.error(LOGGER, "Gửi email xác nhận đơn hàng thất bại", e);
            AlertMessage.showAlert(AlertType.WARNING, "Cảnh báo", "Đặt hàng thành công",
                    "Đơn hàng đã được tạo nhưng gửi email xác nhận chưa thành công.");
        }
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderRepository.findAllOrders();
    }
}
