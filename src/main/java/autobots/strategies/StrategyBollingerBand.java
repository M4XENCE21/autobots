package autobots.strategies;

import java.util.ArrayList;

import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

import autobots.basic.Trading;
import autobots.connectors.Connector;
import autobots.indicators.BollingerBand;
import autobots.parsing.Parser;

public class StrategyBollingerBand extends Strategy {

	public static final double AMOUNT = 0.08; // 0.08 ETH
	// quand on a acheté mais pas vendu, on rachete a 0,8x le dernier prix d'achat
	// quand on a vendu mais pas acheté , on revend a 1,2x le dernier prix de vente
	public static final double COEFF = 0.2;

	private Connector connector;
	private Trading trading;
	private String pair1;
	private String pair2;

	private BollingerBand bb;
	private long timeStamp;
	private long lastCandlestickCloseTime = 0L;

	private boolean lastBuyOrderTriggered;
	private boolean lastSellOrderTriggered;
	private double lastBuyOrderTriggeredPrice;
	private double lastSellOrderTriggeredPrice;

	public StrategyBollingerBand(BollingerBand bollingerBand, Connector connector, String pair1, String pair2) {
		super(bollingerBand);
		this.connector = connector;
		this.trading = new Trading(connector.getClient(), pair1, pair2, connector.getLog());
		this.lastBuyOrderTriggered = false;
		this.lastSellOrderTriggered = false;

		// counter to know when to update bb
		final long time = System.currentTimeMillis();
//		this.timeStamp = time + 3600000; //1h
		this.timeStamp = time + (4 * 3600000); // 4h
//		this.timeStamp = time + (24 * 3600000); //1d
//		this.timeStamp = time + (7 * 24 * 3600000); //1w
	}

	@Override
	void run() {
		ArrayList<NewOrderResponse> listOfFilledOrders = new ArrayList<NewOrderResponse>();
		// Etape 1 : recuperation du temps
		final long time = System.currentTimeMillis();
		// Etape 2 : verifier si on a depasse les 4h
		if (time >= timeStamp) {
			// Etape 3 : verifer quels ordres ont ete executes
			listOfFilledOrders = trading.getFilledOrders();
			for (NewOrderResponse filledOrder : listOfFilledOrders) {
				if ((filledOrder.getSide() == OrderSide.BUY) && ((filledOrder.getStatus() == OrderStatus.FILLED)
						|| (filledOrder.getStatus() == OrderStatus.PARTIALLY_FILLED))) {
					lastBuyOrderTriggered = true;
					lastBuyOrderTriggeredPrice = Double.parseDouble(filledOrder.getPrice());
				} else if ((filledOrder.getSide() == OrderSide.SELL) && ((filledOrder.getStatus() == OrderStatus.FILLED)
						|| (filledOrder.getStatus() == OrderStatus.PARTIALLY_FILLED))) {
					lastSellOrderTriggered = true;
					lastSellOrderTriggeredPrice = Double.parseDouble(filledOrder.getPrice());
				}
			}

			// Etape 4 : MAJ de la BB
			// verifier le timestamp de la bougie pour s'assurer qu'on ne
			// l'a pas deja precedemment ajoutee
			Candlestick candlestick = trading.getLastCandleStick(connector.getClient(), pair1, pair2,
					CandlestickInterval.FOUR_HOURLY);
			while (candlestick.getCloseTime() == lastCandlestickCloseTime) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Parser.write(connector.getLog(), "Boucle d'attente pour l'ajout de la nouvelle bougie : "
							+ "Erreur lors de l'attente de 1 seconde");
				}
				candlestick = trading.getLastCandleStick(connector.getClient(), pair1, pair2,
						CandlestickInterval.FOUR_HOURLY);
			}
			lastCandlestickCloseTime = candlestick.getCloseTime();
			bb.updateBollingerBand(candlestick);

			// Etape 5 : Placer des nouveaux ordres
			placeBuySellOrders();
			// Etape 6 : MAJ le timeout pour trigger a nouveau dans 4h
			timeStamp = time + (4 * 3600000);
		}

	}

	private void placeBuySellOrders() {
		// Cas 1 : BUY et SELL ont été exécutés
		if (lastBuyOrderTriggered && lastSellOrderTriggered) {
			trading.buyOrder(AMOUNT, bb.getLbb());
			trading.sellOrder(AMOUNT, bb.getUbb());
			lastBuyOrderTriggered = false;
			lastSellOrderTriggered = false;
		}
		// Cas 2 : BUY et SELL n'ont pas été exécutés
		else if (!lastBuyOrderTriggered && !lastSellOrderTriggered) {
			trading.cancelOrders();
			trading.buyOrder(AMOUNT, bb.getLbb());
			trading.sellOrder(AMOUNT, bb.getUbb());
		}
		// Cas 3 : Seul BUY a été exécutés
		else if (lastBuyOrderTriggered && !lastSellOrderTriggered) {
			trading.cancelOrders();
			trading.buyOrder(AMOUNT, lastBuyOrderTriggeredPrice * (1 - COEFF));
			trading.sellOrder(AMOUNT, bb.getUbb());
			lastBuyOrderTriggered = false;
		}
		// Cas 4 : Seul SELL a été exécutés
		else if (!lastBuyOrderTriggered && lastSellOrderTriggered) {
			trading.cancelOrders();
			trading.buyOrder(AMOUNT, bb.getLbb());
			trading.sellOrder(AMOUNT, lastSellOrderTriggeredPrice * (1 + COEFF));
			lastSellOrderTriggered = false;
		}

	}
}
