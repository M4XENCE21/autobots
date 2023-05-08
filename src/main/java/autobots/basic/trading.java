package autobots.basic;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;

import autobots.parsing.Parser;

public class Trading extends ATrading {
	public Trading(FileWriter log) {
		this.log = log;
	}

	private FileWriter log;

	/** Contient la liste des id des ordres en cours. */
	private ArrayList<Long> listOfOrders;

	@Override
	public double getBalance(String pair, Account account) {
		return Double.parseDouble(account.getAssetBalance(pair).getFree());
	}

	@Override
	public double getPrice(BinanceApiRestClient client, String pair1, String pair2) {
		return Double.parseDouble(client.getPrice(pair1 + pair2).getPrice());
	}

	private void updateCsv() {
		// update csv, 1 file by day
	}

	@Override
	public void buyOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price) {
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		if ((quantity * price) > 15.0) {
			NewOrderResponse newOrderResponse = client
					.newOrder(NewOrder.limitBuy(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
			listOfOrders.add(newOrderResponse.getOrderId());
			Parser.write(log, "Nouvel ordre d'achat de " + quantityString + " " + pair1 + " au prix de " + priceString
					+ "| id : " + newOrderResponse.getOrderId());
		}
	}

	@Override
	public void sellOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price) {
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		NewOrderResponse newOrderResponse = client
				.newOrder(NewOrder.limitSell(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
		listOfOrders.add(newOrderResponse.getOrderId());
		Parser.write(log, "Nouvel ordre de vente de " + quantityString + " " + pair1 + " au prix de " + priceString
				+ "| id : " + newOrderResponse.getOrderId());

	}

	private void cancelOrder(BinanceApiRestClient client, long orderId, String pair) {
		client.cancelOrder(new CancelOrderRequest(pair, orderId));
	}

	@Override
	public void cancelOrders(BinanceApiRestClient client, String pair1, String pair2) {
		for (long orderId : listOfOrders) {
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
