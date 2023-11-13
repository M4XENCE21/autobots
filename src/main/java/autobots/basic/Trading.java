package autobots.basic;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import autobots.parsing.Parser;

public class Trading extends ATrading {
	public Trading(BinanceApiRestClient client, String pair1, String pair2, FileWriter log) {
		this.client = client;
		this.pair1 = pair1;
		this.pair2 = pair2;
		this.log = log;
		listOfOrders = new ArrayList<NewOrderResponse>();
	}

	private FileWriter log;

	private BinanceApiRestClient client;

	private String pair1;

	private String pair2;

	/** Contient la liste des id des ordres en cours. */
	private ArrayList<NewOrderResponse> listOfOrders;

	@Override
	public double getBalance(String pair, Account account) {
		return Double.parseDouble(account.getAssetBalance(pair).getFree());
	}

	@Override
	public double getPrice() {
		return Double.parseDouble(client.getPrice(pair1 + pair2).getPrice());
	}

	public ArrayList<NewOrderResponse> getOrders() {
		return listOfOrders;
	}

	private void updateCsv() {
		// update csv, 1 file by day
	}

	@Override
	public NewOrderResponse buyOrder(double quantity, double price) {
		long id = -1;
		NewOrderResponse newOrderResponse = null;
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		if ((quantity * price) > 15.0) {
			newOrderResponse = client
					.newOrder(NewOrder.limitBuy(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
			listOfOrders.add(newOrderResponse);
			id = newOrderResponse.getOrderId();
			Parser.write(log, "Nouvel ordre d'achat de " + quantityString + " " + pair1 + " au prix de " + priceString
					+ "| id : " + id);
		}
		return newOrderResponse;
	}

	@Override
	public NewOrderResponse sellOrder(double quantity, double price) {
		long id = -1;
		NewOrderResponse newOrderResponse = null;
		DecimalFormat f = new DecimalFormat("##.00");
		String quantityString = f.format(quantity);
		String priceString = f.format(price);
		newOrderResponse = client
				.newOrder(NewOrder.limitSell(pair1 + pair2, TimeInForce.GTC, quantityString, priceString));
		id = newOrderResponse.getOrderId();
		listOfOrders.add(newOrderResponse);
		Parser.write(log, "Nouvel ordre de vente de " + quantityString + " " + pair1 + " au prix de " + priceString
				+ "| id : " + id);
		return newOrderResponse;
	}

	private void cancelOrder(BinanceApiRestClient client, long orderId, String pair) {
		client.cancelOrder(new CancelOrderRequest(pair, orderId));
	}

	@Override
	public void cancelOrders() {
		for (NewOrderResponse order : listOfOrders) {
			OrderStatus orderStatus = client.getOrderStatus(new OrderStatusRequest(pair1 + pair2, order.getOrderId()))
					.getStatus();
			boolean orderIsActive = (orderStatus == OrderStatus.NEW) || (orderStatus == OrderStatus.PARTIALLY_FILLED);
			if (orderIsActive) {
				try {
					Parser.write(log, "Ordre annulé - pair : " + pair1 + pair2 + " | statut : " + orderStatus
							+ " | id : " + order.getOrderId());
					cancelOrder(client, order.getOrderId(), pair1 + pair2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		listOfOrders = new ArrayList<NewOrderResponse>();

	}

	@Override
	public ArrayList<NewOrderResponse> getFilledOrders() {
		ArrayList<NewOrderResponse> listOfFilledOrders = new ArrayList<NewOrderResponse>();
		for (int i = 0; i < listOfOrders.size(); i++) {
			OrderStatus orderStatus = client
					.getOrderStatus(new OrderStatusRequest(pair1 + pair2, listOfOrders.get(i).getOrderId()))
					.getStatus();
			if ((orderStatus == OrderStatus.FILLED) || (orderStatus == OrderStatus.PARTIALLY_FILLED)) {
				listOfFilledOrders.add(listOfOrders.get(i));
				try {
					Parser.write(log, "Ordre executé - pair : " + pair1 + pair2 + " | statut : " + orderStatus
							+ " | id : " + listOfOrders.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return listOfFilledOrders;

	}

	public Candlestick getLastCandleStick(BinanceApiRestClient client, String pair1, String pair2,
			CandlestickInterval interval) {
		List<Candlestick> candlesticks = client.getCandlestickBars(pair1 + pair2, interval);
		return candlesticks.get(candlesticks.size() - 1);
	}
}
