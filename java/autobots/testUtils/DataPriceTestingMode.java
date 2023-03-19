package autobots.testUtils;

public class DataPriceTestingMode {

	private double open;
	private double high;
	private double low;
	private double close;

	public DataPriceTestingMode(double open, double high, double low, double close) {
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
	}

	public double getOpen() {
		return open;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public double getClose() {
		return close;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public void setClose(double close) {
		this.close = close;
	}

}
