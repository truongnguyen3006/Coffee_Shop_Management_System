package application.util;

import java.io.IOException;
import java.util.logging.Logger;

import application.AlertMessage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public final class SceneNavigator {
    private static final Logger LOGGER = AppLogger.getLogger(SceneNavigator.class);

    private SceneNavigator() {
    }

    public static FXMLLoader openWindow(Object owner, String fxml, String title, boolean modal) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceUtil.getFxml(fxml));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title == null ? "" : title);
            stage.setScene(new Scene(parent));
            if (modal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            stage.show();
            return loader;
        } catch (IOException | IllegalArgumentException e) {
            AppLogger.error(LOGGER, "Không thể mở màn hình: " + fxml, e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở giao diện",
                    "Đã xảy ra lỗi khi tải giao diện. Vui lòng thử lại.");
            return null;
        }
    }

    public static FXMLLoader openAndWait(Object owner, String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceUtil.getFxml(fxml));
            Parent parent = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title == null ? "" : title);
            stage.setScene(new Scene(parent));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            return loader;
        } catch (IOException | IllegalArgumentException e) {
            AppLogger.error(LOGGER, "Không thể mở màn hình: " + fxml, e);
            AlertMessage.showAlert(AlertType.ERROR, "Lỗi hệ thống", "Không thể mở giao diện",
                    "Đã xảy ra lỗi khi tải giao diện. Vui lòng thử lại.");
            return null;
        }
    }
}
