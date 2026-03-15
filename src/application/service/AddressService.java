package application.service;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;

public class AddressService {
    private static final String LEGACY_V1_URL = "https://provinces.open-api.vn/api/?depth=3";
    private static final String CURRENT_V2_PROVINCES_URL = "https://provinces.open-api.vn/api/v2/p/";
    private static final String CURRENT_V2_WARDS_URL = "https://provinces.open-api.vn/api/v2/w/";
    private static final String DISTRICT_PLACEHOLDER_NAME = "Không áp dụng (mô hình 2 cấp)";

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private final Gson gson = new Gson();

    public void loadProvinces(Callback<List<Province>> callback) {
        loadCurrentStructure(callback, () -> loadLegacyStructure(callback));
    }

    private void loadCurrentStructure(Callback<List<Province>> callback, Runnable fallback) {
        HttpRequest provinceRequest = HttpRequest.newBuilder().uri(URI.create(CURRENT_V2_PROVINCES_URL)).GET().build();
        HttpRequest wardRequest = HttpRequest.newBuilder().uri(URI.create(CURRENT_V2_WARDS_URL)).GET().build();

        httpClient.sendAsync(provinceRequest, HttpResponse.BodyHandlers.ofString())
                .thenCombine(httpClient.sendAsync(wardRequest, HttpResponse.BodyHandlers.ofString()),
                        (provinceResponse, wardResponse) -> buildCurrentStructure(provinceResponse.body(), wardResponse.body()))
                .thenAccept(provinces -> {
                    if (provinces == null || provinces.isEmpty()) {
                        fallback.run();
                        return;
                    }
                    Platform.runLater(() -> callback.call(provinces));
                })
                .exceptionally(e -> {
                    fallback.run();
                    return null;
                });
    }

    private List<Province> buildCurrentStructure(String provinceBody, String wardBody) {
        Type provinceType = new TypeToken<List<Province>>() {}.getType();
        Type wardType = new TypeToken<List<Ward>>() {}.getType();
        List<Province> provinces = gson.fromJson(provinceBody, provinceType);
        List<Ward> wards = gson.fromJson(wardBody, wardType);
        if (provinces == null || provinces.isEmpty() || wards == null || wards.isEmpty()) {
            return null;
        }
        Map<Integer, List<Ward>> wardsByProvince = wards.stream()
                .filter(w -> w != null && w.getProvinceCode() != 0)
                .sorted(Comparator.comparing(Ward::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.groupingBy(Ward::getProvinceCode, TreeMap::new, Collectors.toList()));

        for (Province province : provinces) {
            List<Ward> provinceWards = new ArrayList<>(wardsByProvince.getOrDefault(province.getCode(), List.of()));
            District placeholderDistrict = new District();
            placeholderDistrict.setCode(-province.getCode());
            placeholderDistrict.setName(DISTRICT_PLACEHOLDER_NAME);
            placeholderDistrict.setProvinceCode(province.getCode());
            placeholderDistrict.setWards(provinceWards);
            province.setDistricts(new ArrayList<>(List.of(placeholderDistrict)));
        }
        provinces.sort(Comparator.comparing(Province::getName, String.CASE_INSENSITIVE_ORDER));
        return provinces;
    }

    private void loadLegacyStructure(Callback<List<Province>> callback) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(LEGACY_V1_URL)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    Type provinceListType = new TypeToken<List<Province>>() {}.getType();
                    List<Province> provinces = gson.fromJson(responseBody, provinceListType);
                    Platform.runLater(() -> callback.call(provinces));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> callback.call(List.of()));
                    return null;
                });
    }

    public static boolean isTwoLevelDistrict(District district) {
        return district != null && DISTRICT_PLACEHOLDER_NAME.equals(district.getName());
    }

    public interface Callback<T> {
        void call(T data);
    }

    public static class Province {
        private int code;
        private String name;
        private List<District> districts;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public List<District> getDistricts() {
            return districts;
        }

        public void setDistricts(List<District> districts) {
            this.districts = districts;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class District {
        private int code;
        private String name;
        private int province_code;
        private List<Ward> wards;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getProvinceCode() {
            return province_code;
        }

        public void setProvinceCode(int provinceCode) {
            this.province_code = provinceCode;
        }

        public List<Ward> getWards() {
            return wards;
        }

        public void setWards(List<Ward> wards) {
            this.wards = wards;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Ward {
        private int code;
        private String name;
        private int province_code;

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public int getProvinceCode() {
            return province_code;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
