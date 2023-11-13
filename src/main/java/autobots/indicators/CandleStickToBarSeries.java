package autobots.indicators;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import com.binance.api.client.domain.market.Candlestick;

/**
 * This class builds a graphical chart showing values from indicators.
 */
public class CandleStickToBarSeries {

	public static BarSeries toBarSeries(List<Candlestick> candlesticks) {

		BarSeries series = new BaseBarSeries();

		for (int i = 0; i < candlesticks.size(); i++) {
			Candlestick candlestick = candlesticks.get(i);
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date2 = new Date(candlestick.getOpenTime());
			String strDate = sf.format(date2);
			// LocalDateTime:
			LocalDateTime myLocalDateTime = LocalDateTime.of(Integer.parseInt(strDate.substring(0, 4)),
					Integer.parseInt(strDate.substring(5, 7)), Integer.parseInt(strDate.substring(8, 10)),
					Integer.parseInt(strDate.substring(11, 13)), Integer.parseInt(strDate.substring(14, 16)),
					Integer.parseInt(strDate.substring(17)));
			ZonedDateTime date = ZonedDateTime.of(myLocalDateTime, ZoneId.systemDefault());
			double open = Double.parseDouble(candlestick.getOpen());
			double high = Double.parseDouble(candlestick.getHigh());
			double low = Double.parseDouble(candlestick.getLow());
			double close = Double.parseDouble(candlestick.getClose());
			double volume = Double.parseDouble(candlestick.getVolume());

			series.addBar(date, open, high, low, close, volume);
		}
		return series;
	}

	public static String toString(BarSeries series4h) {
		int size = series4h.getBarCount();
		BarSeries subSerie = (size > 15) ? series4h.getSubSeries(size - 15, size) : series4h;
		StringBuilder sb = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		for (int i = 0; i < subSerie.getBarCount(); i++) {
			Bar bar = subSerie.getBar(i);
			sb.append(String.format("Date : %s, Open : %s, High : %s, Low : %s, Close : %s, Volume : %s%s",
					bar.getBeginTime(), bar.getOpenPrice(), bar.getHighPrice(), bar.getLowPrice(), bar.getClosePrice(),
					bar.getVolume(), newLine));
		}
		return sb.toString();
	}

}
