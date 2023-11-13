package autobots.basic;

import java.util.ArrayList;

import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrderResponse;

abstract class ATrading {

	abstract double getBalance(String pair, Account account);

	abstract double getPrice();

//	private void updateCsv() {
//		// update csv, 1 file by day
//	}

	abstract NewOrderResponse buyOrder(double quantity, double price);

	abstract NewOrderResponse sellOrder(double quantity, double price);

	abstract void cancelOrders();

	abstract ArrayList<NewOrderResponse> getFilledOrders();
}
