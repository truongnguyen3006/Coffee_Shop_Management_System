package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProductDAO {
	public ObservableList<Product> getAllProductsSatus() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM products WHERE status = 'Đang hoạt động' ";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
        	
        	while (rs.next()) {
        	    int productID = rs.getInt("productID");
        	    String productCode = rs.getString("productCode");
        	    byte[] thumbnail = rs.getBytes("thumbnail");
        	    String productName = rs.getString("productName");
        	    double price = rs.getDouble("price");
        	    String typeProduct = rs.getString("typeProduct");
        	    String description = rs.getString("description");
        	    String status = rs.getString("status");
        	    Timestamp timestamp = rs.getTimestamp("date");

        	    Product product = new Product(productID, productCode, thumbnail, productName, price, typeProduct, description, status, timestamp);

        	    products.add(product);
        	}

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
	
	public ObservableList<Product> getAllProductsBestSeller() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM products WHERE bestSeller = 1 ";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
        	
        	while (rs.next()) {
        	    int productID = rs.getInt("productID");
        	    String productCode = rs.getString("productCode");
        	    byte[] thumbnail = rs.getBytes("thumbnail");
        	    String productName = rs.getString("productName");
        	    double price = rs.getDouble("price");
        	    String typeProduct = rs.getString("typeProduct");
        	    String description = rs.getString("description");
        	    String status = rs.getString("status");
        	    Timestamp timestamp = rs.getTimestamp("date");

        	    Product product = new Product(productID, productCode, thumbnail, productName, price, typeProduct, description, status, timestamp);

        	    products.add(product);
        	}

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
	
	public ObservableList<Product> getAllProducts() {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM products";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
        	
        	while (rs.next()) {
        	    int productID = rs.getInt("productID");
        	    String productCode = rs.getString("productCode");
        	    byte[] thumbnail = rs.getBytes("thumbnail");
        	    String productName = rs.getString("productName");
        	    double price = rs.getDouble("price");
        	    String typeProduct = rs.getString("typeProduct");
        	    String description = rs.getString("description");
        	    String status = rs.getString("status");
        	    Timestamp timestamp = rs.getTimestamp("date");

        	    Product product = new Product(productID, productCode, thumbnail, productName, price, typeProduct, description, status, timestamp);

        	    products.add(product);
        	}

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
	
	public ObservableList<Product> getAllProductByType(String type) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        String sql = "SELECT * FROM products WHERE typeProduct = ?";
        	try {
				Connection conn = Database.connect();
				 PreparedStatement prepare = conn.prepareStatement(sql);
				 prepare.setString(1, type);
				 ResultSet rs = prepare.executeQuery();	
				while (rs.next()) {
				    // Lấy dữ liệu từ ResultSet
				    int productID = rs.getInt("productID");
				    String productCode = rs.getString("productCode");
				    byte[] thumbnail = rs.getBytes("thumbnail");
				    String productName = rs.getString("productName");
				    String typeProduct = rs.getString("typeProduct");
				    double price = rs.getDouble("price");
				    String description = rs.getString("description");
				    String status = rs.getString("status");
				    Timestamp timestamp = rs.getTimestamp("date");

				    Product product = new Product(productID, productCode, thumbnail, productName, price, typeProduct, description, status, timestamp);

				    products.add(product);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        return products;
    }
	
	public void addProduct(Product product) {
	    String sql = "INSERT INTO products (productCode, thumbnail, productName, typeProduct, price, description, status, date)"
	               + "VALUES (?,?,?,?,?,?,?,?)";
	    try (Connection connect = Database.connect();
	         PreparedStatement prepare = connect.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	         prepare.setString(1, product.getProductCode());
	         prepare.setBytes(2, product.getThumbnail());
	         prepare.setString(3, product.getProductName());
	         prepare.setString(4, product.getTypeProduct());
	         prepare.setDouble(5, product.getPrice());
	         prepare.setString(6, product.getDescription());
	         prepare.setString(7, product.getStatus());
	         prepare.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
	         
	         int result = prepare.executeUpdate();
	         if (result > 0) {
	             try (ResultSet keys = prepare.getGeneratedKeys()) {
	                 if (keys.next()) {
	                     int generatedId = keys.getInt(1);
	                     product.setProductID(generatedId);
	                     System.out.println("Generated ID: " + generatedId);
	                 }
	             }
	             System.out.println("Add successfully product.");
	         } else {
	             System.out.println("Add failure.");
	         }
	    } catch (SQLException e) {
	         e.printStackTrace();
	    }
	}
	
	public boolean deleteProduct(int productID) {		
		try {
			Connection connect = Database.connect();
			String sql = "UPDATE products SET status = ? where productID = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			prepare.setString(1, "Dừng hoạt động");
			prepare.setInt(2, productID);
			int result = prepare.executeUpdate();
			return result > 0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return false;
	}
	
	public void updateProduct(Product product) {
		try {
			Connection connect = Database.connect();
			String sql = "UPDATE products SET productCode = ?, thumbnail = ?, productName = ?, typeProduct = ?, price = ?, "
					+ "description = ?, status = ?, date = ? WHERE productID = ?";
			PreparedStatement prepare = connect.prepareStatement(sql);
			
			 prepare.setString(1, product.getProductCode());
			 prepare.setBytes(2, product.getThumbnail());
	         prepare.setString(3, product.getProductName());
	         prepare.setString(4, product.getTypeProduct());
	         prepare.setDouble(5, product.getPrice());
	         prepare.setString(6, product.getDescription());
	         prepare.setString(7, product.getStatus());
	         prepare.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
	         prepare.setInt(9, product.getProductID());
	         prepare.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Product> searchProduct(String keyWord){
		List<Product> productList = new ArrayList<>();
		try {
			Connection connect = Database.connect();
			String sql = "SELECT productID,productCode, thumbnail, productName, typeProduct, price, description  FROM products WHERE productName LIKE ?"
					+ "AND status = 'Đang hoạt động'";
			PreparedStatement prepare = connect.prepareStatement(sql);
			String searchPattern = "%" + keyWord + "%";
			prepare.setString(1, searchPattern);
			ResultSet rs = prepare.executeQuery();
			while(rs.next()) {
				int productID = rs.getInt("productID");
				String productCode = rs.getString("productCode");
				byte[] thumbnail = rs.getBytes("thumbnail");
				String productName = rs.getString("productName");
				String typeProduct = rs.getString("typeProduct");
				double price = rs.getDouble("price");
				String description = rs.getString("description");
				
				Product product = new Product(productID, productCode, thumbnail, productName, price, description, typeProduct);
				productList.add(product);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return productList;
		
	}
	
}
