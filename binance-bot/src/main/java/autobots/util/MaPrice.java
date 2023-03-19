package autobots.util;

public class MaPrice {

	protected int index;
	protected double price;

	public MaPrice(int index, double price) {
		this.index = index;
		this.price = price;
	}

	public int getIndex() {
		return index;
	}

	public double getPrice() {
		return price;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
