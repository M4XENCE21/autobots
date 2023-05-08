package autobots.basic;

public class OrderForTest {

	private String symbol1;
	private String symbol2;
	private double price;
	private double quantity;
	private OrderDate date;

	public OrderForTest(String symbol1, String symbol2, double price, double quantity) {
		super();
		this.symbol1 = symbol1;
		this.symbol2 = symbol2;
		this.price = price;
		this.quantity = quantity;
		this.date = new OrderDate();
	}

	/**
	 * @return the date
	 */
	public OrderDate getOrderDate() {
		return date;
	}

	/**
	 * @return the symbol1
	 */
	public String getSymbol1() {
		return symbol1;
	}

	/**
	 * @return the symbol2
	 */
	public String getSymbol2() {
		return symbol2;
	}

	/**
	 * @return the amount
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

}
