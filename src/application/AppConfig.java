package application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class AppConfig {

    private static final Properties PROPS = new Properties();
    private static boolean loaded = false;

    private AppConfig() {
    }

    private static synchronized void loadIfNeeded() {
        if (loaded) {
            return;
        }

        Path path = Paths.get("app.properties");

        if (!Files.exists(path)) {
            throw new RuntimeException(
                    "Không tìm thấy file app.properties ở thư mục gốc project. " +
                            "Hãy copy từ app.properties.example sang app.properties và điền giá trị thật."
            );
        }

        try (InputStream inputStream = Files.newInputStream(path)) {
            PROPS.load(inputStream);
            loaded = true;
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file app.properties", e);
        }
    }

    public static String get(String key) {
        loadIfNeeded();

        if (!PROPS.containsKey(key)) {
            throw new RuntimeException("Thiếu cấu hình bắt buộc: " + key);
        }

        String value = PROPS.getProperty(key);
        return value == null ? "" : value.trim();
    }

    public static int getInt(String key) {
        String value = get(key);

        if (value.isEmpty()) {
            throw new RuntimeException("Giá trị số không được để trống: " + key);
        }

        return Integer.parseInt(value);
    }
}
