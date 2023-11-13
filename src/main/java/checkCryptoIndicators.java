import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ta4j.core.BarSeries;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.general.SymbolInfo;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import autobots.connectors.Connector;
import autobots.indicators.BollingerBand;
import autobots.indicators.CandleStickToBarSeries;

public class checkCryptoIndicators {

	public static void main(String[] args) throws IOException {
		String API_KEY = "GT1HC4DbzSQncllX8ZAFeJKaUgRcrN8NmcrbaqgidrhdPgUBbzXvL8YuOeZToqTc";
		String SECRET = "KGpwaonA2yD9YHeCUQt3LjEvRTiU6hPLGwkFsUan2aLO8bFVW7b0fuaNvX8JfwYv";

		BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		BinanceApiRestClient client = factory.newRestClient();
		Connector connector = new Connector(API_KEY, SECRET);
		connector.testConnectivity();
		List<SymbolInfo> assets = client.getExchangeInfo().getSymbols();
//		System.out.println(assets);
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < assets.size(); i++) {
			String pair = assets.get(i).getSymbol();
			if (pair.substring(pair.length() - 4, pair.length()).equals("USDT")) {
				list.add(pair);
			}
		}
		System.out.println(list.toString());
		// Getting csv data
//		Trading trading = new Trading(connector.getClient(), "ETH", "USDT", connector.getLog());
		List<Candlestick> candlesticks = client.getCandlestickBars("ETH" + "USDT", CandlestickInterval.FOUR_HOURLY);
		// TODO pouvoir convertir une List<Candlestick> en BarSeries
		BarSeries series4h = CandleStickToBarSeries
				.toBarSeries(candlesticks.subList(candlesticks.size() - 50, candlesticks.size()));
		System.out.println(CandleStickToBarSeries.toString(series4h));
		BollingerBand bb = new BollingerBand(series4h, 21);
		System.out.println(bb.toString());

//		StrategyBollingerBand strategy = new StrategyBollingerBand(bb, connector, "ETH", "USDT");

//    		Order book of a symbol 

//		OrderBook orderBook = client.getOrderBook("NEOETH", 10);
//		List<OrderBookEntry> asks = orderBook.getAsks();
//		OrderBookEntry firstAskEntry = asks.get(0);
//		System.out.println(firstAskEntry.getPrice() + " / " + firstAskEntry.getQty());

//		Get account balances

//		Account account = client.getAccount();
//		System.out.println(account.getBalances()); @FIXME ca ne marche pas, essayer de voir pourquoi et de corriger
//		System.out.println(account.getAssetBalance("USDT").getFree());
//
//		List<Trade> myTrades = client.getMyTrades("FLUXUSDT");
//		System.out.println(myTrades);

//		NewOrderResponse newOrderResponse = client
//				.newOrder(NewOrder.limitBuy("ACAUSDT", TimeInForce.GTC, "500", "0.05"));
//		OrderStatus orderStatus = client
//				.getOrderStatus(new OrderStatusRequest("ACAUSDT", newOrderResponse.getOrderId())).getStatus();
//		System.out.println(orderStatus);
//		boolean orderIsActive = (orderStatus == OrderStatus.NEW) || (orderStatus == OrderStatus.PARTIALLY_FILLED);
//		if (orderIsActive) {
//			System.out.println("actif ! : " + client
//					.getOrderStatus(new OrderStatusRequest("ACAUSDT", newOrderResponse.getOrderId())).getStatus());
//		}
//		System.out.println(client.getAccount().getBalances());

//		System.out.println(newOrderResponse.getOrderId());
//		
//		client.cancelOrder(new CancelOrderRequest("LINKUSDT", newOrderResponse.getOrderId()));

		// InstanceOfMA30 ma = new InstanceOfMA30("FLUX", client);
		// System.out.println(Integer.parseInt(client.getPrice("FLUXUSDT").getPrice()));
//		System.out.println(Double.parseDouble(client.getPrice("ETHUSDT").getPrice()));

//		List<Candlestick> candlesticks = client.getCandlestickBars("ETHUSDT", CandlestickInterval.FOUR_HOURLY);
//		System.out.println(candlesticks.get(candlesticks.size() - 1));
	}
}
