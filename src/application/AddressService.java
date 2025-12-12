package application;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;


public class AddressService {
	private HttpClient httpClient = HttpClient.newHttpClient();
	private Gson gson = new Gson();
	
	public void loadProvinces(Callback<List<Province>> callback) {
		String url = "https://provinces.open-api.vn/api/?depth=3";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(responseBody -> {
                    Type provinceListType = new TypeToken<List<Province>>() {}.getType();
                    List<Province> provinces = gson.fromJson(responseBody, provinceListType);
                    Platform.runLater(() -> callback.call(provinces));
                })
                .exceptionally(e -> { e.printStackTrace(); return null; });
	}
	
	 // Callback interface để trả về dữ liệu
    public interface Callback<T> {
        void call(T data);
    }
    
    
    public static class Province {
        private int code;
        private String name;
        private List<District> districts;
        public int getCode() { return code; }
        public String getName() { return name; }
        public List<District> getDistricts() { return districts; }
        @Override
        public String toString() { return name; }
    }
    
    public static class District {
        private int code;
        private String name;
        private List<Ward> wards;
        public int getCode() { return code; }
        public String getName() { return name; }
        public List<Ward> getWards() { return wards; }
        @Override
        public String toString() { return name; }
    }
    
    public static class Ward {
        private int code;
        private String name;
        public int getCode() { return code; }
        public String getName() { return name; }
        @Override
        public String toString() { return name; }
    }
}	
