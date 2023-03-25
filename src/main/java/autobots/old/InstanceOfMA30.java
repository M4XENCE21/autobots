/**
 * 
 */
package autobots.old;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.binance.api.client.BinanceApiRestClient;

import autobots.testUtils.DataPriceTestingMode;
import autobots.testUtils.ReadFile;

/**
 * @author Maxence
 *
 */
public class InstanceOfMA30 {
	public InstanceOfMA30(String coin, String pairCoin, BinanceApiRestClient client, boolean testingMode, int mode) {
		this.mode = mode;
		this.coin = coin;
		this.pairCoin = pairCoin;
		this.client = client;
		this.testingMode = testingMode;
	}

	public InstanceOfMA30(String coin, String pairCoin, BinanceApiRestClient client, boolean testingMode,
			ReadFile reader, int mode) {
		this.mode = mode;
		this.coin = coin;
		this.pairCoin = pairCoin;
		this.client = client;
		this.testingMode = testingMode;
		this.reader = reader;
	}

	public InstanceOfMA30(String coin, String pairCoin, BinanceApiRestClient client, boolean testingMode,
			ReadFile reader) {
		this.mode = 0;
		this.coin = coin;
		this.pairCoin = pairCoin;
		this.client = client;
		this.testingMode = testingMode;
		this.reader = reader;
	}

	protected final int mode;
	protected final String coin;
	protected final String pairCoin;
	protected final BinanceApiRestClient client;
	protected ArrayList<Double> last30hPrices = new ArrayList<Double>();
	protected ArrayList<Ma30CandleStat> last30hMa30CandleStat = new ArrayList<Ma30CandleStat>();
	protected double previousCandleClose = 0.0;
	protected int lastIndex = 0;
	protected ArrayList<Double> ma30Trending = new ArrayList<Double>();
	protected double previousMa30 = 0.0;
	protected int errorMargin = 5;
	protected int counter = 0;
	// testing mode
	protected boolean testingMode = false;
	protected ReadFile reader;
	protected int fakeHour = 10;

	public int getFakeHour() {
		return fakeHour;
	}

	public void setFakeHour(int fakeHour) {
		this.fakeHour = fakeHour;
	}

	public void setLast30hPrices1h(ArrayList<Double> last30hPrices1h) {
		this.last30hPrices = last30hPrices1h;
	}

	public void setMa30Trending(ArrayList<Double> ma30Trending) {
		this.ma30Trending = ma30Trending;
	}

	public ReadFile getReader() {
		return reader;
	}

	public double getUpdateMa30() {
		MaPrice lastPrice = getHourPrice();
		// init de la premiere previousCandleClose
		previousCandleClose = (previousCandleClose == 0.0) ? (lastPrice.getPrice() + 1) : previousCandleClose;
		if (lastPrice.getIndex() != -1) {
			last30hPrices.set(lastPrice.getIndex(), lastPrice.getPrice());
			ma30Trending.set(lastPrice.getIndex(), Math.round((lastPrice.getPrice() - previousMa30) * 1000.0) / 1000.0);
			previousMa30 = lastPrice.getPrice();
			Ma30CandleStat stat = new Ma30CandleStat((previousCandleClose > getMa30()),
					(lastPrice.getPrice() > getMa30()), previousCandleClose, lastPrice.getPrice());
			last30hMa30CandleStat.set(lastPrice.getIndex(), stat);
			lastIndex = lastPrice.getIndex();
			previousCandleClose = lastPrice.getPrice();
		}
		return getMa30();
	}

	public void checkBuySellTrigger(BinanceApiRestClient client) {
//		definition en dessous ma30 : >=50% des ouvertures / fermetures sont en dessous de la ma30 ; 
//		si + de 50% sont au dessus, on est au dessus de ma30, 
//		idem pour en dessous
//		VENTE
//		on vend apres 4 bougies vertes successives, ou 5 bougies vertes sur 6 
//		(3 bougies vertes / 4 bougies vertes sur 5 si on est sous la ma30)
//		il faut verifier que cloture bougie verte 1 < cloture bougie verte 2 < ... < cloture bougie verte X (la derniere) 
//		et que ouverture bougie1 -> fermeture bougie de fin doit correspondre a une progression d'au moins 15% 
//		(essayer avec differentes valeurs) -> on attend 5h minimum avant de refaire une vente "brochette"
//		sinon on vend si 1 ou 2 bougies consecutives vertes sont a +20% (entre ouverture bougie 1 et fermeture bougie 2),
//		on vend au prix de cloture bougie 2 et 2 palliers a  +10 % et +20% au dessus
//
//		ACHAT
//		on rachete au prix d'ouverture de la premiere bougie qui a declenchee la vente.
//		si 100% des ouvertures/fermetures des meches declenchantes sont au dessus de ma30,
//		on rachete un peu des qu'on retouche la ma30
//		si on vend sous la ma30, on rachete 15% et 25% en dessous du pris d'ouverture de la premiere bougie verte
//		qui a declenche le pattern
//		le pourcentage de token que l'on vend doit etre proportionnel
//		au pourcentage de progression des bougies (plus cest fort, plus on en vend)

		int index = lastIndex;
		int numberOfGreenCandles = 0;
		// on verifie si les clotures sont croissantes
		boolean growingClosePrices = true;
		int numberOfGreenCandlesBelowMa30 = 0;
		int numberOfRedCandles = 0;
		int numberOfRedCandlesBelowMa30 = 0;

		for (int i = 0; i < 6; i++) {
			if (last30hMa30CandleStat.get(index).isGreenCandle()) {
				if (!last30hMa30CandleStat.get(index).isCloseUpperMa30()) {
					numberOfGreenCandlesBelowMa30++;
				} else {
					numberOfGreenCandles++;
				}
			} else {
				if (!last30hMa30CandleStat.get(index).isCloseUpperMa30()) {
					numberOfRedCandlesBelowMa30++;
				} else {
					numberOfRedCandles++;
				}
			}
			if (index == 0) {
				index = 29;
			} else {
				index = index - 1;
			}
			// Conditions de vente :

			// 3 bougies vertes sous la ma30 : VENTE
			if ((i == 2) && (numberOfGreenCandlesBelowMa30 == 3)) {

			}
			// 4 bougies vertes sur 5 sous la ma30 : VENTE
			if ((i == 4) && (numberOfGreenCandlesBelowMa30 >= 4)) {

			}

			// 4 bougies vertes au dessus de la ma30 : VENTE
			if ((i == 3) && (numberOfGreenCandles == 4)) {

			}
			// 5 bougies vertes sur 6 au dessus de la ma30 : VENTE
			if ((i == 5) && (numberOfGreenCandles >= 5)) {

			}
		}
	}

	public int getTrending() {
		int up = 0;
		int down = 0;

		for (double values : ma30Trending) {
			if (values > 0) {
				up++;
			} else if (values < 0) {
				down++;
			}
		}
		if (up >= 22) {
			return 1;
		} else if (down >= 22) {
			return -1;
		}
		return 0;
	}

	public ArrayList<Double> getMa30Trending() {
		return ma30Trending;
	}

	public ArrayList<Double> getLast30hPrices1h() {
		return last30hPrices;
	}

	public double getMa30() {
		double result = 0;
		for (double prices : last30hPrices) {
			result += prices;
		}
		result = result / 30;
		return result;
	}

	private MaPrice getHourPrice() {

		String stringHour = getLocalHour();
		String[] splitHour = stringHour.split(":");

		int hour = -1;
		int minute = -1;
		int count = 0;
		for (String word : splitHour) {
			if (count == 0) {
				hour = Integer.parseInt(word);
			}
			if (count == 1) {
				minute = Integer.parseInt(word);
			}
			count++;
		}
		if ((hour < 0) || (minute < 0)) {
			return new MaPrice(-1, 0);
		}
		int hourInMinutes = (hour * 60) + minute;

		if (testingMode) {
			if (mode == 1) {
				hourInMinutes = 57;
			} else if (mode == 4) {
				hourInMinutes = 237;
			}
		}
		if (Ma30Interval.isMa30(hourInMinutes, mode)) {
			if (counter == 29) {
				counter = 0;
			} else {
				counter++;
			}
		}
		double price = 0.0;
		if (testingMode) {
			DataPriceTestingMode actualPrice = reader.getNextPrice();
//			System.out.println("XXXXX: actualPrice" + actualPrice.getLow());
			price = actualPrice.getOpen();
//			System.out.println("Get new FLUX price : " + price);
			reader.setActualPrice(actualPrice);
		} else {
			price = Double.parseDouble(client.getPrice(coin + pairCoin).getPrice());
		}
		return new MaPrice(counter, price);
	}

	private String getLocalHour() {
//		   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
		LocalDateTime now = LocalDateTime.now();
		// System.out.println(dtf.format(now));
		return dtf.format(now);
	}
}
