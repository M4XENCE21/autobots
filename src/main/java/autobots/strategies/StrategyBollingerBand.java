package autobots.strategies;

import java.io.FileWriter;

import org.ta4j.core.BarSeries;

import autobots.basic.Trading;
import autobots.indicators.BollingerBand;
import autobots.indicators.IndicatorsToChart;

public class StrategyBollingerBand extends Strategy {

	public static final double AMOUNT = 150.0;

	private FileWriter log;
	private String[] csv;
	private Trading trading;

	private BollingerBand bb1h;
	private boolean boughtBb1h;
	private boolean soldBb1h;
	private long timeStamp1h;

	private BollingerBand bb4h;
	private boolean boughtBb4h;
	private boolean soldBb4h;
	private long timeStamp4h;

	private BollingerBand bb1d;
	private boolean boughtBb1d;
	private boolean soldBb1d;
	private long timeStamp1d;

	private BollingerBand bb1w;
	private boolean boughtBb1w;
	private boolean soldBb1w;
	private long timeStamp1w;

	StrategyBollingerBand(FileWriter log, String[] csv) {
		super(log, csv);
		// Getting csv data
		BarSeries series1h = IndicatorsToChart.loadCsvSeriesCustom(csv[0]);
		BarSeries series4h = IndicatorsToChart.loadCsvSeriesCustom(csv[0]);
		BarSeries series1d = IndicatorsToChart.loadCsvSeriesCustom(csv[0]);
		BarSeries series1w = IndicatorsToChart.loadCsvSeriesCustom(csv[0]);

		// Creating BollingerBand Object
		bb1h = new BollingerBand(series1h, 14);
		bb4h = new BollingerBand(series4h, 14);
		bb1d = new BollingerBand(series1d, 14);
		bb1w = new BollingerBand(series1w, 14);

		boughtBb1h = true;
		boughtBb4h = true;
		boughtBb1d = true;
		boughtBb1w = true;
		soldBb1h = true;
		soldBb4h = true;
		soldBb1d = true;
		soldBb1w = true;

		trading = new Trading(log);

		// counter to know when to update bb
		final long time = System.currentTimeMillis();
		timeStamp1h = time + 3600000;
		timeStamp4h = time + (4 * 3600000);
		timeStamp1d = time + (24 * 3600000);
		timeStamp1w = time + (7 * 24 * 3600000);
	}

	@Override
	void run() {
		// TODO Auto-generated method stub
		// pour le backtesting, il faut vérifier en plus si les ordres ont trigger, a
		// faire dans cette classe ou alors dans la class main avant l'appelle de la
		// methode run
		final long time = System.currentTimeMillis();
		if (time >= timeStamp1h) {
			timeStamp1h = time + 3600000;
			if (boughtBb1h && soldBb1h) {
			}
		}

	}
}
