package application.controller;


import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import application.model.CartDisplayItem;
import application.model.UserContactInfo;
import application.service.CartService;
import application.service.OrderService;
import application.service.UserService;
import application.util.AppLogger;
import application.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CartController implements Initializable {
    private static final Logger LOGGER = AppLogger.getLogger(CartController.class);
    public static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("#,###");
    private static final double SHIPPING_FEE = 25000;
    private static final double VAT_RATE = 1.1;

    @FXML
    private Button btnPay;

    @FXML
    private Label date;

    @FXML
    private VBox layoutDelivery;

    @FXML
    private Label lbAdress;

    @FXML
    private Label lbNote;

    @FXML
    private Label lbProvince;

    @FXML
    private VBox listCard;

    @FXML
    private Label name;

    @FXML
    private ToggleGroup pay;

    @FXML
    private Label phone;

    @FXML
    private Label quantityLabel;

    @FXML
    private Label shippingFee;

    @FXML
    private Label time;

    @FXML
    private Label lbTimeExpired;

    @FXML
    private Label totalPrice;

    @FXML
    private Label totalPriceVAT;

    @FXML
    private Button btnTimeDelivery;

    @FXML
    private HBox HboxTimeDelivery;

    private final CartService cartService = new CartService();
    private final OrderService orderService = new OrderService();
    private final UserService userService = new UserService();

    private int currentUserID;
    private double totalVAT = 0;
    private boolean isValid = false;

    public void setUserID(int userID) {
        this.currentUserID = userID;
        refreshCartView();
    }

    public void switchVoucher() {
        openModal("EnterVoucher.fxml", null, null);
    }

    public void translateSelectTime(LocalDate date, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.date.setText(date == null ? "" : date.format(formatter));
        this.time.setText(time == null ? "" : time);
    }

    @FXML
    public void switchTime() {
        openModal("TimeDelivery.fxml", loader -> {
            TimeDeliveryController controller = loader.getController();
            controller.setCartController(this::translateSelectTime);
            controller.setTime(time.getText());
        }, null);
    }

    public void translateSelectAdress(String address, String province) {
        if (address != null && !address.trim().isEmpty()) {
            lbAdress.setText(address.trim());
        }
        if (province != null && !province.trim().isEmpty()) {
            lbProvince.setText(province.trim());
        }
    }

    @FXML
    public void selectAdress() {
        openModal("SelectAddress.fxml", loader -> {
            AddressSelectionController controller = loader.getController();
            controller.setCartController(this::translateSelectAdress);
        }, null);
    }

    public void translateSelectInformatinUser(String userName, String phoneNumber) {
        name.setText(userName == null ? "" : userName);
        phone.setText(phoneNumber == null ? "" : phoneNumber);
    }

    @FXML
    public void selectNameAndPhone() {
        try {
            UserContactInfo contactInfo = userService.getContactInfoByUserName(Session.getUserName());
            openModal("InformationUser.fxml", loader -> {
                InformationUserController controller = loader.getController();
                controller.setInformationController(this::translateSelectInformatinUser);
                controller.setInformation(contactInfo.getFullName(), contactInfo.getPhoneNumber());
            }, null);
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể tải thông tin người dùng trong giỏ hàng", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không tải được thông tin",
                    "Không thể lấy thông tin người dùng. Vui lòng thử lại.");
        }
    }

    public void translateSelectNotes(String notes) {
        lbNote.setText(notes == null ? "" : notes);
    }

    @FXML
    public void switchNote() {
        openModal("NoteForStore.fxml", loader -> {
            NoteController controller = loader.getController();
            controller.setCartController(this::translateSelectNotes);
            controller.setNoteText(lbNote.getText());
        }, null);
    }

    public void requestListCardLayout() {
        listCard.requestLayout();
    }

    public void displayCard() {
        try {
            List<CartDisplayItem> items = cartService.getCartItems(currentUserID);
            listCard.getChildren().clear();
            for (CartDisplayItem item : items) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/CartCard.fxml"));
                Parent cartItem = loader.load();
                CartCardController cartCardController = loader.getController();
                cartCardController.setUserID(currentUserID);
                cartCardController.setRootNode(cartItem);
                cartCardController.setCartController(this);

                Image thumbnail = item.getThumbnail() == null ? null : new Image(new ByteArrayInputStream(item.getThumbnail()));
                cartCardController.setData(item.getSize(), item.getProductName(), thumbnail, item.getFinalPrice(),
                        item.getProductId(), item.getQuantity(), item.getToppingHash(), item.getToppingNames(),
                        item.getProductCode(), item.getCartId());
                cartItem.setUserData(cartCardController);
                listCard.getChildren().add(cartItem);
            }
            updateTotalPrice();
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Không thể tải danh sách sản phẩm trong giỏ hàng", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không tải được giỏ hàng",
                    "Đã xảy ra lỗi khi tải giỏ hàng. Vui lòng thử lại.");
        } catch (IOException e) {
            AppLogger.error(LOGGER, "Không thể tải giao diện CartCard.fxml", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không tải được giao diện",
                    "Đã xảy ra lỗi khi hiển thị giỏ hàng. Vui lòng thử lại.");
        }
    }

    private void refreshCartView() {
        displayCard();
    }

    public void updateTotalPrice() {
        double total = 0;
        int quantity = 0;
        for (javafx.scene.Node node : listCard.getChildren()) {
            Object data = node.getUserData();
            if (data instanceof CartCardController) {
                CartCardController card = (CartCardController) data;
                total += card.getTotalPrice();
                quantity += card.getQuantity();
            }
        }

        totalVAT = total * VAT_RATE;
        totalPrice.setText(PRICE_FORMATTER.format(total));

        if (quantity > 0) {
            shippingFee.setText(PRICE_FORMATTER.format(SHIPPING_FEE));
            totalPriceVAT.setText(PRICE_FORMATTER.format(totalVAT + SHIPPING_FEE));
        } else {
            shippingFee.setText("0");
            totalPriceVAT.setText(PRICE_FORMATTER.format(totalVAT));
        }
        quantityLabel.setText(String.valueOf(quantity));
        updateButtonState();
    }

    @FXML
    public void confirmPayment() {
        List<CartDisplayItem> items = collectCartItemsFromUI();
        String validationError = validateCheckoutInput(items);
        if (validationError != null) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi", "Thông tin chưa hợp lệ", validationError);
            return;
        }

        try {
            int orderId = orderService.placeOrder(Session.getUserID(), lbAdress.getText().trim(), phone.getText().trim(),
                    totalVAT + SHIPPING_FEE, lbNote.getText(), items, time.getText().trim());
            AlertMessage.showAlert(AlertType.INFORMATION, "Thông báo", "Thanh toán thành công",
                    "Thanh toán đã được thực hiện thành công. Mã đơn hàng: #" + orderId);
            refreshCartView();
            promptBackToHome();
        } catch (IllegalArgumentException e) {
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi dữ liệu", "Không thể thanh toán", e.getMessage());
        } catch (SQLException e) {
            AppLogger.error(LOGGER, "Thanh toán thất bại cho userId=" + Session.getUserID(), e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Thanh toán thất bại",
                    "Không thể tạo đơn hàng lúc này. Vui lòng thử lại sau.");
        }
    }

    private List<CartDisplayItem> collectCartItemsFromUI() {
        java.util.ArrayList<CartDisplayItem> items = new java.util.ArrayList<>();
        for (javafx.scene.Node node : listCard.getChildren()) {
            Object data = node.getUserData();
            if (data instanceof CartCardController) {
                CartCardController card = (CartCardController) data;
                items.add(new CartDisplayItem(card.getCartID(), card.getProductID(), null,
                        card.getProductName(), null, card.getSize(), card.getQuantity(), null,
                        card.getToppingName(), card.getUnitPrice(), card.getTotalPrice()));
            }
        }
        return items;
    }

    private String validateCheckoutInput(List<CartDisplayItem> items) {
        if (!ValidationUtil.isValidAddress(lbAdress.getText())) {
            return "Vui lòng nhập địa chỉ giao hàng hợp lệ.";
        }
        if (name.getText() == null || name.getText().trim().isEmpty()) {
            return "Vui lòng chọn tên người nhận.";
        }
        if (!ValidationUtil.isValidPhoneNumber(phone.getText())) {
            return "Vui lòng nhập số điện thoại hợp lệ.";
        }
        if (date.getText() == null || date.getText().trim().isEmpty() || time.getText() == null || time.getText().trim().isEmpty()) {
            return "Vui lòng chọn thời gian giao hàng.";
        }
        if (pay.getSelectedToggle() == null) {
            return "Vui lòng chọn phương thức thanh toán.";
        }
        if (items == null || items.isEmpty()) {
            return "Giỏ hàng đang trống.";
        }
        for (CartDisplayItem item : items) {
            if (!ValidationUtil.isValidQuantity(item.getQuantity())) {
                return "Số lượng sản phẩm phải lớn hơn 0.";
            }
        }
        return null;
    }

    private void promptBackToHome() {
        ButtonType buttonYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getButtonTypes().setAll(buttonYes, buttonCancel);
        alert.setContentText("Bạn có muốn quay về trang chủ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonYes) {
            goToHome();
        }
    }

    private void goToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Home.fxml"));
            Parent homeView = loader.load();
            BorderPane mainLayout = (BorderPane) btnPay.getScene().getRoot();
            mainLayout.setCenter(homeView);
        } catch (IOException e) {
            AppLogger.error(LOGGER, "Không thể chuyển về trang chủ sau khi thanh toán", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở trang chủ",
                    "Đơn hàng đã được tạo nhưng không thể chuyển về trang chủ.");
        }
    }

    private void updateButtonState() {
        isValid = ValidationUtil.isValidAddress(lbAdress.getText()) && name.getText() != null && !name.getText().trim().isEmpty()
                && ValidationUtil.isValidPhoneNumber(phone.getText()) && !"0".equals(quantityLabel.getText().trim())
                && date.getText() != null && !date.getText().trim().isEmpty() && time.getText() != null
                && !time.getText().trim().isEmpty() && pay.getSelectedToggle() != null;

        if (isValid) {
            btnPay.setDisable(false);
            btnPay.setStyle("-fx-background-color: green; -fx-text-fill: white;");
        } else {
            btnPay.setDisable(true);
            btnPay.setStyle("-fx-background-color: grey; -fx-text-fill: white;");
        }
    }

    private void openModal(String fxml, LoaderConsumer consumer, String title) {
        Parent currentRoot = btnPay.getScene().getRoot();
        BoxBlur blur = new BoxBlur(5, 5, 3);
        currentRoot.setEffect(blur);
        try {
            FXMLLoader loader = new FXMLLoader(ResourceUtil.getFxml(fxml));
            Parent view = loader.load();
            if (consumer != null) {
                consumer.accept(loader);
            }
            Stage selectStage = new Stage();
            if (title != null) {
                selectStage.setTitle(title);
            }
            selectStage.setScene(new Scene(view));
            selectStage.initModality(Modality.APPLICATION_MODAL);
            selectStage.show();
            selectStage.setOnHiding(event -> currentRoot.setEffect(null));
        } catch (IOException e) {
            AppLogger.error(LOGGER, "Không thể mở màn hình: " + fxml, e);
            currentRoot.setEffect(null);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở giao diện",
                    "Đã xảy ra lỗi khi tải giao diện. Vui lòng thử lại.");
        }
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        updateButtonState();

        lbAdress.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
        name.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
        phone.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
        quantityLabel.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
        date.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
        time.textProperty().addListener((obs, oldVal, newVal) -> updateButtonState());
        pay.selectedToggleProperty().addListener((obs, oldVal, newVal) -> updateButtonState());

        if (!pay.getToggles().isEmpty()) {
            pay.selectToggle(pay.getToggles().get(0));
        }

        LocalTime now = LocalTime.now();
        boolean expired = now.isAfter(LocalTime.of(21, 0));
        btnTimeDelivery.setVisible(!expired);
        btnTimeDelivery.setDisable(expired);
        HboxTimeDelivery.setVisible(!expired);
        lbTimeExpired.setVisible(expired);
    }

    @FunctionalInterface
    private interface LoaderConsumer {
        void accept(FXMLLoader loader) throws IOException;
    }
}
