package application;

import java.time.LocalDateTime;

public class Order {
	private int orderID;
	private String customerName;
	private String Email;
	private String address;
	private String phoneNumber;
	private double orderTotalPrice;
	private LocalDateTime dateOrder;
	private String note;
	
	public Order(int orderID, String customerName, String email, String address, String phoneNumber,
			double orderTotalPrice, LocalDateTime dateOrder, String note) {
		super();
		this.orderID = orderID;
		this.customerName = customerName;
		Email = email;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.orderTotalPrice = orderTotalPrice;
		this.dateOrder = dateOrder;
		this.note = note;
	}
	
	
	
	public Order(int orderID, double orderTotalPrice, LocalDateTime dateOrder) {
		super();
		this.orderID = orderID;
		this.orderTotalPrice = orderTotalPrice;
		this.dateOrder = dateOrder;
	}



	public int getOrderID() {
		return orderID;
	}
	public void setOrderID(int orderID) {
		this.orderID = orderID;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getEmail() {
		return Email;
	}
	public void setEmail(String email) {
		Email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public double getOrderTotalPrice() {
		return orderTotalPrice;
	}
	public void setOrderTotalPrice(double orderTotalPrice) {
		this.orderTotalPrice = orderTotalPrice;
	}
	public LocalDateTime getDateOrder() {
		return dateOrder;
	}
	public void setDateOrder(LocalDateTime dateOrder) {
		this.dateOrder = dateOrder;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	
}
