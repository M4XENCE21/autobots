package autobots.indicators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.PPOIndicator;
import org.ta4j.core.indicators.ROCIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.WilliamsRIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PriceVariationIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;

import com.opencsv.CSVReader;

import ta4jexamples.indicators.IndicatorsToCsv;
import ta4jexamples.loaders.CsvBarsLoader;

/**
 * This class builds a graphical chart showing values from indicators.
 */
public class IndicatorsToChart {

	public static BarSeries loadCsvSeriesCustom(String filename) {

		InputStream stream = IndicatorsToChart.class.getClassLoader().getResourceAsStream(filename);

		BarSeries series = new BaseBarSeries("test_random");

		try (CSVReader csvReader = new CSVReader(new InputStreamReader(stream, Charset.forName("UTF-8")), ',', '"',
				1)) {
			String[] line;
			while ((line = csvReader.readNext()) != null) {
				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date2 = new Date(Long.parseLong(line[0]));
				String strDate = sf.format(date2);
				// LocalDateTime:
				LocalDateTime myLocalDateTime = LocalDateTime.of(Integer.parseInt(strDate.substring(0, 4)),
						Integer.parseInt(strDate.substring(5, 7)), Integer.parseInt(strDate.substring(8, 10)),
						Integer.parseInt(strDate.substring(11, 13)), Integer.parseInt(strDate.substring(14, 16)),
						Integer.parseInt(strDate.substring(17)));
				ZonedDateTime date = ZonedDateTime.of(myLocalDateTime, ZoneId.systemDefault());
				double open = Double.parseDouble(line[1]);
				double high = Double.parseDouble(line[2]);
				double low = Double.parseDouble(line[3]);
				double close = Double.parseDouble(line[4]);
				double volume = Double.parseDouble(line[5]);

				series.addBar(date, open, high, low, close, volume);
			}
		} catch (IOException ioe) {
			Logger.getLogger(CsvBarsLoader.class.getName()).log(Level.SEVERE, "Unable to load bars from CSV", ioe);
		} catch (NumberFormatException nfe) {
			Logger.getLogger(CsvBarsLoader.class.getName()).log(Level.SEVERE, "Error while parsing value", nfe);
		}
		return series;
	}

	public static void main(String[] args) {

		/*
		 * Getting bar series
		 */
		BarSeries series = loadCsvSeriesCustom("FLUXUSDT-1h-2022-03.csv");

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

		/*
		 * Creating indicators
		 */
		// Typical price
		TypicalPriceIndicator typicalPrice = new TypicalPriceIndicator(series);
		// Price variation
		PriceVariationIndicator priceVariation = new PriceVariationIndicator(series);
		// Simple moving averages
		SMAIndicator shortSma = new SMAIndicator(closePrice, 8);
		SMAIndicator longSma = new SMAIndicator(closePrice, 20);
		// Exponential moving averages
		EMAIndicator shortEma = new EMAIndicator(closePrice, 8);
		EMAIndicator longEma = new EMAIndicator(closePrice, 20);
		// Percentage price oscillator
		PPOIndicator ppo = new PPOIndicator(closePrice, 12, 26);
		// Rate of change
		ROCIndicator roc = new ROCIndicator(closePrice, 100);
		// Relative strength index
		RSIIndicator rsi = new RSIIndicator(closePrice, 14);
		// Williams %R
		WilliamsRIndicator williamsR = new WilliamsRIndicator(series, 20);
		// Average true range
		ATRIndicator atr = new ATRIndicator(series, 20);
		// Standard deviation
		StandardDeviationIndicator sd = new StandardDeviationIndicator(closePrice, 14);

		/*
		 * Building header
		 */
		StringBuilder sb = new StringBuilder(
				"timestamp,close,typical,variation,sma8,sma20,ema8,ema20,ppo,roc,rsi,williamsr,atr, sd, middle BB\n");

		/*
		 * Adding indicators values
		 */
		final int nbBars = series.getBarCount();
		for (int i = 0; i < nbBars; i++) {
			sb.append(series.getBar(i).getEndTime()).append(',').append(closePrice.getValue(i)).append(',')
					.append(typicalPrice.getValue(i)).append(',').append(priceVariation.getValue(i)).append(',')
					.append(shortSma.getValue(i)).append(',').append(longSma.getValue(i)).append(',')
					.append(shortEma.getValue(i)).append(',').append(longEma.getValue(i)).append(',')
					.append(ppo.getValue(i)).append(',').append(roc.getValue(i)).append(',').append(rsi.getValue(i))
					.append(',').append(williamsR.getValue(i)).append(',').append(atr.getValue(i)).append(',')
					.append(sd.getValue(i)).append(',').append(middleBBand.getValue(i)).append('\n');
		}

		/*
		 * Writing CSV file
		 */
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File("target", "indicators.csv")));
			writer.write(sb.toString());
		} catch (IOException ioe) {
			Logger.getLogger(IndicatorsToCsv.class.getName()).log(Level.SEVERE, "Unable to write CSV file", ioe);
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}

	}

}
