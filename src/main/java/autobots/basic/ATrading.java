package autobots.basic;

import java.util.ArrayList;

import com.binance.api.client.domain.account.Account;

abstract class ATrading {

	abstract double getBalance(String pair, Account account);

	abstract double getPrice();

//	private void updateCsv() {
//		// update csv, 1 file by day
//	}

	abstract long buyOrder(double quantity, double price);

	abstract long sellOrder(double quantity, double price);

	abstract void cancelOrders();

	abstract ArrayList<Long> getFilledOrders();
}
