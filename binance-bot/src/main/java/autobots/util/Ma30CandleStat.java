package autobots.util;

public class Ma30CandleStat {

	private boolean isOpenUpperMa30; // true : open > ma30 - false : open < ma30
	private boolean isCloseUpperMa30; // true : close > ma30 - false : close < ma30
	private double openPrice;
	private double closePrice;

	public Ma30CandleStat(boolean isOpenUpperMa30, boolean isCloseUpperMa30, double openPrice, double closePrice) {
		super();
		this.isOpenUpperMa30 = isOpenUpperMa30;
		this.isCloseUpperMa30 = isCloseUpperMa30;
		this.openPrice = openPrice;
		this.closePrice = closePrice;
	}

	public boolean isOpenUpperMa30() {
		return isOpenUpperMa30;
	}

	public boolean isGreenCandle() {
		return (openPrice < closePrice);
	}

	public boolean isRedCandle() {
		return (openPrice >= closePrice);
	}

	public boolean isCloseUpperMa30() {
		return isCloseUpperMa30;
	}

	public double getOpenPrice() {
		return openPrice;
	}

	public double getClosePrice() {
		return closePrice;
	}

	public void setOpenUpperMa30(boolean isOpenUpperMa30) {
		this.isOpenUpperMa30 = isOpenUpperMa30;
	}

	public void setCloseUpperMa30(boolean isCloseUpperMa30) {
		this.isCloseUpperMa30 = isCloseUpperMa30;
	}

	public void setOpenPrice(double openPrice) {
		this.openPrice = openPrice;
	}

	public void setClosePrice(double closePrice) {
		this.closePrice = closePrice;
	}

}
