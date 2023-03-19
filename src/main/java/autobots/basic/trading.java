package autobots.basic;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;

import autobots.parsing.Parser;

public class trading {
	public trading(FileWriter log) {
		this.log = log;
	}

	private FileWriter log;

	private double getBalance(String pair, Account account) {
		return Double.parseDouble(account.getAssetBalance(pair).getFree());
	}

	private double getPrice(BinanceApiRestClient client, String pair1, String pair2) {
		return Double.parseDouble(client.getPrice(pair1 + pair2).getPrice());
	}

	@SuppressWarnings("deprecation")
	private void initialize(String API_KEY, String SECRET, BinanceApiClientFactory factory, BinanceApiRestClient client,
			Account account, FileWriter csvResult) throws IOException {
		Parser.write(log, "========== Initialisation ==========");
		String csvFileName = Parser.createFile("csvFluxUsdt", ".csv");
		csvResult = new FileWriter(csvFileName);
		// load csv

		// calculate indicators
		// connection API
		factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		client = factory.newRestClient();
		account = client.getAccount();
		Parser.write(log, "account : " + account);
		Parser.write(log, "========== Initialisation FIN ==========");
	}

	private void updateCsv() {
		// update csv, 1 file by day
	}

	private void testConnectivity(BinanceApiRestClient client) {
		// Test connectivity
		client.ping();

		// Check server time
		long serverTime = client.getServerTime();
		System.out.println("server time : " + serverTime);
		System.out.println("system time - server time : " + (System.currentTimeMillis() - serverTime));
	}

	private void buyOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price,
			ArrayList<Long> listOfOrdersStrategy) {
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		if ((quantity * price) > 15.0) {
			NewOrderResponse newOrderResponse = client
					.newOrder(NewOrder.limitBuy(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
			listOfOrdersStrategy.add(newOrderResponse.getOrderId());
			Parser.write(log, "Nouvel ordre d'achat de " + quantityString + " " + pair1 + " au prix de " + priceString
					+ "| id : " + newOrderResponse.getOrderId());
		}
	}

	private void sellOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price,
			ArrayList<Long> listOfOrdersStrategy) {
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		NewOrderResponse newOrderResponse = client
				.newOrder(NewOrder.limitSell(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
		listOfOrdersStrategy.add(newOrderResponse.getOrderId());
		Parser.write(log, "Nouvel ordre de vente de " + quantityString + " " + pair1 + " au prix de " + priceString
				+ "| id : " + newOrderResponse.getOrderId());

	}

	private void cancelOrder(BinanceApiRestClient client, long orderId, String pair) {
		client.cancelOrder(new CancelOrderRequest(pair, orderId));
	}

	private void cancelOrders(BinanceApiRestClient client, String pair1, String pair2,
			ArrayList<Long> listOfOrdersStrategy) {
		for (long orderId : listOfOrdersStrategy) {
			OrderStatus orderStatus = client.getOrderStatus(new OrderStatusRequest(pair1 + pair2, orderId)).getStatus();
			boolean orderIsActive = (orderStatus == OrderStatus.NEW) || (orderStatus == OrderStatus.PARTIALLY_FILLED);
			if (orderIsActive) {
				try {
					Parser.write(log, "Ordre annulé - pair : " + pair1 + pair2 + " | statut : " + orderStatus
							+ " | id : " + orderId);
					cancelOrder(client, orderId, pair1 + pair2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

}
