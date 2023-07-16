package autobots.basic;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;

abstract class ATrading {

	abstract double getBalance(String pair, Account account);

	abstract double getPrice(BinanceApiRestClient client, String pair1, String pair2);

//	private void updateCsv() {
//		// update csv, 1 file by day
//	}

	abstract long buyOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price);

	abstract long sellOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price);

	abstract void cancelOrders(BinanceApiRestClient client, String pair1, String pair2);

	abstract void updateListOfOrders(BinanceApiRestClient client, String pair1, String pair2);
}
