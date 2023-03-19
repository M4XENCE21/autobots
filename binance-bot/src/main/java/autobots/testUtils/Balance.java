package autobots.testUtils;

import java.text.DecimalFormat;

public class Balance {
	private Double freeBalance;
	private Double usedBalance;
	DecimalFormat f = new DecimalFormat("##");

	public Double getFreeBalance() {
		return freeBalance;
	}

	public Double getUsedBalance() {
		return usedBalance;
	}

	public Double getBalance() {
		return usedBalance + freeBalance;
	}

	public String getFreeBalanceString() {
		String str = f.format(freeBalance);
		return str;
	}

	public String getUsedBalanceString() {
		String str = f.format(usedBalance);
		return str;
	}

	public String getBalanceString() {
		String str = f.format(usedBalance + freeBalance);
		return str;
	}

	public void setFreeBalance(Double freeBalance) {
		this.freeBalance = freeBalance;
	}

	public void setUsedBalance(Double usedBalance) {
		this.usedBalance = usedBalance;
	}

	public Balance(Double freeBalance, Double usedBalance) {
		this.freeBalance = freeBalance;
		this.usedBalance = usedBalance;
	}

}
