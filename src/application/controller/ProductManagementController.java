package application.controller;

import application.*;
import application.model.*;
import application.repository.*;
import application.service.*;
import application.messaging.*;
import application.util.*;
import application.config.*;

import java.io.IOException;
import java.util.logging.Logger;

import application.util.AppLogger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Placeholder controller kept so the legacy ProductManagement.fxml can still load.
 * The main admin workflow is handled by MainLayoutAdmin.
 */
public class ProductManagementController {
    private static final Logger LOGGER = AppLogger.getLogger(ProductManagementController.class);

    @FXML
    public void switchAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/AddAndUpdateProduct.fxml"));
            Parent view = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Product");
            stage.setScene(new Scene(view));
            stage.show();
        } catch (IOException e) {
            AppLogger.error(LOGGER, "Không thể mở màn hình thêm sản phẩm", e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở màn hình thêm sản phẩm",
                    "Vui lòng thử lại sau.");
        }
    }
}
