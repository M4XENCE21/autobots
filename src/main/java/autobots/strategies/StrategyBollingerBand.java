package autobots.strategies;

import java.util.ArrayList;

import org.ta4j.core.BarSeries;

import com.binance.api.client.domain.market.CandlestickInterval;

import autobots.basic.Trading;
import autobots.connectors.Connector;
import autobots.indicators.BollingerBand;
import autobots.indicators.IndicatorsToChart;

public class StrategyBollingerBand extends Strategy {

	public static final double AMOUNT = 150.0;
	public static final String PAIR1 = "ETH";
	public static final String PAIR2 = "USDT";

	private String[] csv;
	private Connector connector;
	private Trading trading;

	private BollingerBand bb;
	private boolean boughtBb;
	private boolean soldBb;
	private long timeStamp;

	StrategyBollingerBand(String[] csv, Connector connector) {
		super(csv);
		this.connector = connector;

		// Getting csv data
		BarSeries series4h = IndicatorsToChart.loadCsvSeriesCustom(csv[1]);

		// Creating BollingerBand Object
		this.bb = new BollingerBand(series4h, 14);
		this.boughtBb = true;
		this.soldBb = true;
		this.trading = new Trading(connector.getClient(), PAIR1, PAIR2, connector.getLog());

		// counter to know when to update bb
		final long time = System.currentTimeMillis();
//		this.timeStamp = time + 3600000; //1h
		this.timeStamp = time + (4 * 3600000); // 4h
//		this.timeStamp = time + (24 * 3600000); //1d
//		this.timeStamp = time + (7 * 24 * 3600000); //1w
	}

	@Override
	void run() {
		ArrayList<Long> listOfFilledOrders = new ArrayList<Long>();
		// pour le backtesting, il faut vérifier en plus si les ordres ont trigger, a
		// faire dans cette classe ou alors dans la class main avant l'appelle de la
		// methode run
		// Etape 1 : recuperation du temps
		final long time = System.currentTimeMillis();
		// Etape 2 : verifier si on a depasse les 4h
		if (time >= timeStamp) {
			// Etape 3 : verifer quels ordres ont ete executes
			listOfFilledOrders = trading.getFilledOrders();
			// @TODO voir comment utiliser cela pour mettre en place une strategie d'achat
			// et de vente en escalier (exemple : si on achete en bb4h mais qu'on a pas
			// vendu en bb4h, on ne rachete plus en bb4h mais en bb1d

			// Etape 4 : MAJ de la BB
			// @TODO il faudrait verifier le timestamp de la bougie pour s'assurer qu'on ne
			// l'a pas deja precedemment ajoutee
			bb.updateBollingerBand(
					trading.getLastCandleStick(connector.getClient(), PAIR1, PAIR2, CandlestickInterval.FOUR_HOURLY));
			// Etape 5 : Placer des nouveaux ordres
			placeBuySellOrders();
			// Etape 6 : MAJ le timeout pour trigger a nouveau dans 4h
			timeStamp = time + (4 * 3600000);
		}

	}

	private void placeBuySellOrders() {
		// TODO Auto-generated method stub

	}
}
