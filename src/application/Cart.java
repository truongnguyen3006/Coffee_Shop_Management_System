package application;

public class Cart {
	private int cartID;
	private String name;
	private double price;
	private String size;
	private int userID;

	public Cart(String name, double price, String size, int userID) {
		super();
		this.name = name;
		this.price = price;
		this.size = size;
		this.userID = userID;
	}

	public int getCartID() {
		return cartID;
	}

	public void setCartID(int cartID) {
		this.cartID = cartID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

}
