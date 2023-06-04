package autobots.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import autobots.util.DecimalNumber;

public class BollingerBand {
	/** Low Bollinger Band */
	private final double lbb;

	/** Middle Bollinger Band */
	private final double mbb;

	/** Up Bollinger Band */
	private final double ubb;

	public BollingerBand(BarSeries series, int barCount) {
		// @XXX on prend barCount=14 par defaut
		// Close price
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		EMAIndicator avg = new EMAIndicator(closePrice, barCount);
		StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, barCount);

		// Bollinger bands
		BollingerBandsMiddleIndicator middleBBand = new BollingerBandsMiddleIndicator(avg);
		BollingerBandsLowerIndicator lowBBand = new BollingerBandsLowerIndicator(middleBBand, sd);
		BollingerBandsUpperIndicator upBBand = new BollingerBandsUpperIndicator(middleBBand, sd);

		this.lbb = lowBBand.getValue(series.getEndIndex()).doubleValue();
		this.mbb = middleBBand.getValue(series.getEndIndex()).doubleValue();
		this.ubb = upBBand.getValue(series.getEndIndex()).doubleValue();
	}

	public double getLbb() {
		return lbb;
	}

	public double getUbb() {
		return ubb;
	}

	public double getMbb() {
		return mbb;
	}

	public String getLbb(DecimalNumber dn) {
		return dn.getDecimalFormat().format(lbb);
	}

	public String getUbb(DecimalNumber dn) {
		return dn.getDecimalFormat().format(ubb);
	}

	public String getMbb(DecimalNumber dn) {
		return dn.getDecimalFormat().format(mbb);
	}

}
