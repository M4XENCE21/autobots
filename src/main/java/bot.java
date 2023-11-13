import java.io.IOException;
import java.util.List;

import org.ta4j.core.BarSeries;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import autobots.basic.Trading;
import autobots.connectors.Connector;
import autobots.indicators.BollingerBand;
import autobots.indicators.IndicatorsToChart;
import autobots.strategies.StrategyBollingerBand;

public class bot {

	public static void main(String[] args) throws IOException {
		String API_KEY = "xxx";
		String SECRET = "xxx";

		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		BinanceApiRestClient client = factory.newRestClient();
		Connector connector = new Connector(API_KEY, SECRET);

//    		Test connectivity
		client.ping();

//    		Check server time
		long serverTime = client.getServerTime();
		System.out.println("server time : " + serverTime);
		System.out.println("system time - server time : " + (System.currentTimeMillis() - serverTime));

		// Getting csv data
		Trading trading = new Trading(connector.getClient(), "ETH", "USDT", connector.getLog());
		List<Candlestick> candlesticks = client.getCandlestickBars("ETH" + "USDT", CandlestickInterval.FOUR_HOURLY);
		// TODO pouvoir convertir une List<Candlestick> en BarSeries
		BarSeries series4h = IndicatorsToChart.loadCsvSeriesCustom("FLUXUSDT-1h-2022-03.csv");
		BollingerBand bb = new BollingerBand(series4h, 14);
		StrategyBollingerBand strategy = new StrategyBollingerBand(bb, connector, "ETH", "USDT");

//    		Order book of a symbol

//		OrderBook orderBook = client.getOrderBook("NEOETH", 10);
//		List<OrderBookEntry> asks = orderBook.getAsks();
//		OrderBookEntry firstAskEntry = asks.get(0);
//		System.out.println(firstAskEntry.getPrice() + " / " + firstAskEntry.getQty());

//		Get account balances

//		Account account = client.getAccount();
//		System.out.println(account.getBalances());
//		System.out.println(account.getAssetBalance("USDT").getFree());
//		
//		List<Trade> myTrades = client.getMyTrades("FLUXUSDT");
//		System.out.println(myTrades);
//		
//		NewOrderResponse newOrderResponse = client
//				.newOrder(NewOrder.limitSell("ACAUSDT", TimeInForce.GTC, "30", "2.0"));
//		OrderStatus orderStatus = client
//				.getOrderStatus(new OrderStatusRequest("ACAUSDT", newOrderResponse.getOrderId())).getStatus();
//		boolean orderIsActive = (orderStatus == OrderStatus.NEW) || (orderStatus == OrderStatus.PARTIALLY_FILLED);
//		if (orderIsActive) {
//			System.out.println("actif ! : " + client
//					.getOrderStatus(new OrderStatusRequest("ACAUSDT", newOrderResponse.getOrderId())).getStatus());
//		}
		System.out.println(client.getAccount().getBalances());

//		System.out.println(newOrderResponse.getOrderId());
//		
//		client.cancelOrder(new CancelOrderRequest("LINKUSDT", newOrderResponse.getOrderId()));

		// InstanceOfMA30 ma = new InstanceOfMA30("FLUX", client);
		// System.out.println(Integer.parseInt(client.getPrice("FLUXUSDT").getPrice()));
		System.out.println(Double.parseDouble(client.getPrice("ETHUSDT").getPrice()));

//		List<Candlestick> candlesticks = client.getCandlestickBars("ETHUSDT", CandlestickInterval.FOUR_HOURLY);
//		System.out.println(candlesticks.get(candlesticks.size() - 1));
	}
}
