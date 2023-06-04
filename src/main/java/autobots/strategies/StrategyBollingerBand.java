package autobots.strategies;

import org.ta4j.core.BarSeries;

import autobots.indicators.BollingerBand;
import autobots.indicators.IndicatorsToChart;

public class StrategyBollingerBand extends Strategy {

	@Override
	void initialize() {

		// Getting csv data
		BarSeries series = IndicatorsToChart.loadCsvSeriesCustom("FLUXUSDT-1h-2022-03.csv");

		// Creating BollingerBand Object
		BollingerBand bb = new BollingerBand(series, 14);

	}

	@Override
	void run() {
		// TODO Auto-generated method stub

	}

}
