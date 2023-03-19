package autobots.util;

public class BuySellOrder {
	private double hour;

	public void setHour(double newHour) {
		this.hour = newHour;
	}

	private final Long orderId;

	public BuySellOrder(double hour, Long orderId) {
		this.hour = hour;
		this.orderId = orderId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public double getHour() {
		return hour;
	}

}
