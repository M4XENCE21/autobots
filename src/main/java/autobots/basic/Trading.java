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
	public long buyOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price) {
		long id = -1;
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		if ((quantity * price) > 15.0) {
			NewOrderResponse newOrderResponse = client
					.newOrder(NewOrder.limitBuy(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
			listOfOrders.add(id);
			id = newOrderResponse.getOrderId();
			Parser.write(log, "Nouvel ordre d'achat de " + quantityString + " " + pair1 + " au prix de " + priceString
					+ "| id : " + id);
		}
		return id;
	}

	@Override
	public long sellOrder(BinanceApiRestClient client, String pair1, String pair2, double quantity, double price) {
		long id = -1;
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		NewOrderResponse newOrderResponse = client
				.newOrder(NewOrder.limitSell(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
		id = newOrderResponse.getOrderId();
		listOfOrders.add(id);
		Parser.write(log, "Nouvel ordre de vente de " + quantityString + " " + pair1 + " au prix de " + priceString
				+ "| id : " + id);
		return id;
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

	@Override
	public void updateListOfOrders(BinanceApiRestClient client, String pair1, String pair2) {
		for (int i = 0; i < listOfOrders.size(); i++) {
			OrderStatus orderStatus = client.getOrderStatus(new OrderStatusRequest(pair1 + pair2, listOfOrders.get(i)))
					.getStatus();
			boolean orderIsActive = (orderStatus == OrderStatus.NEW) || (orderStatus == OrderStatus.PARTIALLY_FILLED);
			if (!orderIsActive) {
				listOfOrders.remove(i);
				try {
					Parser.write(log, "Ordre executé - pair : " + pair1 + pair2 + " | statut : " + orderStatus
							+ " | id : " + listOfOrders.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}
}
