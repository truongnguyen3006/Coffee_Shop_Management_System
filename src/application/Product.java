package application;

import java.io.ByteArrayInputStream;
import java.util.Date;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Product {
	private int productID;
	private String productCode;
	private byte[] thumbnail;
	private String productName;
	private double price;
	private String typeProduct;
	private String description;
	private String status;
	private Date date;
	
	public Product() {
		super();
	}

	public Product(int productID, String productCode, byte[] thumbnail, String productName, double price,
			String typeProduct, String description, String status, Date date) {
		super();
		this.productID = productID;
		this.productCode = productCode;
		this.thumbnail = thumbnail;
		this.productName = productName;
		this.price = price;
		this.typeProduct = typeProduct;
		this.description = description;
		this.status = status;
		this.date = date;
	}

	public Product(String productCode, byte[] thumbnail, String productName, String typeProduct, double price, String description,
			String status, Date date) {
		super();
		this.productCode = productCode;
		this.thumbnail = thumbnail;
		this.productName = productName;
		this.typeProduct = typeProduct;
		this.price = price;
		this.description = description;
		this.status = status;
		this.date = date;
	}

	public Product(int productID, String productCode, byte[] thumbnail, String productName, double price,
			String description, String typeProduct) {
		super();
		this.productID = productID;
		this.productCode = productCode;
		this.thumbnail = thumbnail;
		this.productName = productName;
		this.price = price;
		this.description = description;
		this.typeProduct = typeProduct;
	}

	public String getTypeProduct() {
		return typeProduct;
	}

	public void setTypeProduct(String typeProduct) {
		this.typeProduct = typeProduct;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getProductID() {
		return productID;
	}

	public void setProductID(int productID) {
		this.productID = productID;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}

	public Image getThumbnailImage() {
		if (thumbnail != null && thumbnail.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(thumbnail);
			return new Image(bis);
		}
		// Trả về ảnh placeholder khi không có thumbnail
		return new Image(getClass().getResourceAsStream("/images/placeholder.png"));
	}

	public ImageView getThumbnailImageView() {
		if (thumbnail != null && thumbnail.length > 0) {
			ByteArrayInputStream bis = new ByteArrayInputStream(thumbnail);
			Image image = new Image(bis);
			ImageView imageView = new ImageView(image);
			imageView.setFitWidth(100); // Điều chỉnh kích thước nếu cần
			imageView.setFitHeight(100);
			imageView.setPreserveRatio(true);
			return imageView;
		}
		return new ImageView(); // Trả về ImageView rỗng nếu không có ảnh
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
