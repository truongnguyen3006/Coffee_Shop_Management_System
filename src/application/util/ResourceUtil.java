package application.util;

import java.net.URL;

import javafx.scene.image.Image;

public final class ResourceUtil {
    private ResourceUtil() {
    }

    public static String normalizeResourcePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Đường dẫn resource không hợp lệ");
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/application/" + normalized;
        }
        return normalized;
    }

    public static URL getResource(String absoluteOrRelativePath) {
        String normalized = normalizeResourcePath(absoluteOrRelativePath);
        URL url = ResourceUtil.class.getResource(normalized);
        if (url == null) {
            throw new IllegalArgumentException("Không tìm thấy resource: " + normalized);
        }
        return url;
    }

    public static URL getFxml(String fxmlPath) {
        return getResource(fxmlPath);
    }

    public static Image loadImage(String absoluteOrRelativePath) {
        return new Image(getResource(absoluteOrRelativePath).toExternalForm());
    }
}
