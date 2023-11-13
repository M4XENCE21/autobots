package autobots.indicators;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import com.binance.api.client.domain.market.Candlestick;

import autobots.util.DecimalNumber;

public class BollingerBand {
	/** Low Bollinger Band */
	private double lbb;

	/** Middle Bollinger Band */
	private double mbb;

	/** Up Bollinger Band */
	private double ubb;

	/** Low Bollinger Band Serie */
	private BollingerBandsLowerIndicator lbbSerie;

	/** Middle Bollinger Band Serie */
	private BollingerBandsMiddleIndicator mbbSerie;

	/** Up Bollinger Band Serie */
	private BollingerBandsUpperIndicator ubbSerie;

	/** Bollinger Band Bar Serie */
	private BarSeries series;

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
		this.lbbSerie = lowBBand;
		this.mbbSerie = middleBBand;
		this.ubbSerie = upBBand;
		this.series = series;
	}

	/**
	 * @return the lbbSerie
	 */
	public BarSeries getBarSeries() {
		return series;
	}

	/**
	 * @return the lbbSerie
	 */
	public BollingerBandsLowerIndicator getLbbSerie() {
		return lbbSerie;
	}

	/**
	 * @return the mbbSerie
	 */
	public BollingerBandsMiddleIndicator getMbbSerie() {
		return mbbSerie;
	}

	/**
	 * @return the ubbSerie
	 */
	public BollingerBandsUpperIndicator getUbbSerie() {
		return ubbSerie;
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

	// @FIXME il faut prendre en argument le barCount ou passer la méthode en
	// private, pour ne pas mettre 14 en dur
	public void updateBollingerBand(final Candlestick candle) {

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date2 = new Date(candle.getOpenTime());
		String strDate = sf.format(date2);
		// LocalDateTime:
		LocalDateTime myLocalDateTime = LocalDateTime.of(Integer.parseInt(strDate.substring(0, 4)),
				Integer.parseInt(strDate.substring(5, 7)), Integer.parseInt(strDate.substring(8, 10)),
				Integer.parseInt(strDate.substring(11, 13)), Integer.parseInt(strDate.substring(14, 16)),
				Integer.parseInt(strDate.substring(17)));
		ZonedDateTime date = ZonedDateTime.of(myLocalDateTime, ZoneId.systemDefault());
		double open = Double.parseDouble(candle.getOpen());
		double high = Double.parseDouble(candle.getHigh());
		double low = Double.parseDouble(candle.getLow());
		double close = Double.parseDouble(candle.getClose());
		double volume = Double.parseDouble(candle.getVolume());

		series.addBar(date, open, high, low, close, volume);
		/*
		 * Creating indicators
		 */
		// Close price
		ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
		EMAIndicator avg14 = new EMAIndicator(closePrice, 14);
		StandardDeviationIndicator sd14 = new StandardDeviationIndicator(closePrice, 14);

		// Bollinger bands
		BollingerBandsMiddleIndicator middleBBand = new BollingerBandsMiddleIndicator(avg14);
		BollingerBandsLowerIndicator lowBBand = new BollingerBandsLowerIndicator(middleBBand, sd14);
		BollingerBandsUpperIndicator upBBand = new BollingerBandsUpperIndicator(middleBBand, sd14);

		// Updating MBB, LBB and UBB
		this.lbb = lowBBand.getValue(series.getEndIndex()).doubleValue();
		this.mbb = middleBBand.getValue(series.getEndIndex()).doubleValue();
		this.ubb = upBBand.getValue(series.getEndIndex()).doubleValue();
		this.lbbSerie = lowBBand;
		this.mbbSerie = middleBBand;
		this.ubbSerie = upBBand;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("BollingerBand - ").append(String.format("UBB : %s | ", getUbb()))
				.append(String.format("MBB : %s | ", getMbb())).append(String.format("LBB : %s", getLbb()));
		return sb.toString();
	}
}
