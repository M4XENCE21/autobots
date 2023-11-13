package autobots.old;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;

import autobots.parsing.ParserForTests;
import autobots.parsing.XmlFluxUsdt;
import autobots.testUtils.Balance;
import autobots.testUtils.ReadFile;
import autobots.util.Pair;

public class autobotsV0 {

	private final static String API_KEY = "XXX";
	private final static String SECRET = "XXX";
	private static ArrayList<Long> listOfOrdersStrategy0 = new ArrayList<Long>();
	private static Double strategy0Countdown = 0.0;
	private static ArrayList<Long> listOfOrdersStrategy1 = new ArrayList<Long>();
	private static double strategy1Countdown = 0.0;
	private static boolean priceLowerThanMa30 = false;
	private static boolean previousPriceLowerThanMa30 = false;
	private static String traces = ParserForTests.createFile("traces", ".txt");
	private static ArrayList<Double> listOfStrategyCoeff = new ArrayList<Double>();
	private static FileWriter log = null;
	private static FileWriter csvResult = null;
	private final static boolean isTestingMode = true;
	// Variables pour le mode test :
	private static HashMap<String, Balance> balances = new HashMap<String, Balance>();
	// contient prix et quantité
	private static HashMap<Double, Double> listOfBuysStrategy0Test = new HashMap<Double, Double>();
	private static HashMap<Double, Double> listOfSellsStrategy0Test = new HashMap<Double, Double>();
	private static HashMap<Double, Double> listOfBuysStrategy1Test = new HashMap<Double, Double>();
	private static HashMap<Double, Double> listOfSellsStrategy1Test = new HashMap<Double, Double>();

	public static double getBalance(String pair, Account account, int strategyId) {
		if (isTestingMode) {
			return balances.get(pair).getFreeBalance() * listOfStrategyCoeff.get(strategyId);
		} else {
			return Double.parseDouble(account.getAssetBalance(pair).getFree()) * listOfStrategyCoeff.get(strategyId);
		}
	}

	public static void buyOrder(BinanceApiRestClient client, Pair pair, double pairStableBalance, double ma30,
			ArrayList<Long> listOfOrdersStrategy, HashMap<Double, Double> listOfBuysStrategyTest) {
		for (int i = 0; i < pair.getPercentageTriggerBuyList().size(); i++) {
			double price = ma30 * (1.0 - pair.getPercentageTriggerBuyList().get(i));
			double quantity = pairStableBalance * pair.getPercentageBuyList().get(i);
			DecimalFormat f = new DecimalFormat("##.00");
			String quantityString = f.format(quantity);
			String priceString = f.format(price);
			if ((quantity * price) > 15.0) {
				if (isTestingMode) {
					// determiner quantity a partir de balances
					double freeQuantity = balances.get(pair.getPair2Name()).getFreeBalance();
					double usedQuantity = balances.get(pair.getPair2Name()).getUsedBalance();
					System.out.println("Nouvel ordre d'achat de " + quantityString + " FLUX au prix de " + priceString);
					listOfBuysStrategyTest.put(price, quantity);
					balances.get(pair.getPair2Name()).setFreeBalance(freeQuantity - quantity);
					balances.get(pair.getPair2Name()).setUsedBalance(usedQuantity + quantity);

				} else {
					NewOrderResponse newOrderResponse = client.newOrder(
							NewOrder.limitBuy(pair.getPair1Name(), TimeInForce.GTC, quantityString, priceString));
					// System.out.println(newOrderResponse.getOrderId());
					listOfOrdersStrategy.add(newOrderResponse.getOrderId());
					ParserForTests.write(log, "Nouvel ordre d'achat de " + quantityString + " FLUX au prix de "
							+ priceString + "| id : " + newOrderResponse.getOrderId());

				}
			}
		}
	}

	public static void sellOrder(BinanceApiRestClient client, Pair pair, double pairStableBalance, double ma30,
			ArrayList<Long> listOfOrdersStrategy, HashMap<Double, Double> listOfSellsStrategyTest) {
		for (int i = 0; i < pair.getPercentageTriggerSellList().size(); i++) {
			double price = ma30 * (1.0 + pair.getPercentageTriggerSellList().get(i));
			double quantity = pairStableBalance * pair.getPercentageSellList().get(i);
			DecimalFormat f = new DecimalFormat("##.00");
			String quantityString = f.format(quantity);
			String priceString = f.format(price);
			if (isTestingMode) {
				// determiner quantity a partir de balances
				double freeQuantity = balances.get(pair.getPair1Name()).getFreeBalance();
				double usedQuantity = balances.get(pair.getPair1Name()).getUsedBalance();
				listOfSellsStrategyTest.put(price, quantity);
				System.out.println("Nouvel ordre de vente de " + quantityString + " FLUX au prix de " + priceString);
				balances.get(pair.getPair1Name()).setFreeBalance(freeQuantity - quantity);
				balances.get(pair.getPair1Name()).setUsedBalance(usedQuantity + quantity);
			} else {
				NewOrderResponse newOrderResponse = client.newOrder(
						NewOrder.limitSell(pair.getPair1Name(), TimeInForce.GTC, quantityString, priceString));
				// System.out.println(newOrderResponse.getOrderId());
				listOfOrdersStrategy.add(newOrderResponse.getOrderId());
				ParserForTests.write(log, "Nouvel ordre de vente de " + quantityString + " FLUX au prix de "
						+ priceString + "| id : " + newOrderResponse.getOrderId());
			}
		}
	}

	private static void cancelOrder(BinanceApiRestClient client, long orderId, Pair pair) {
		client.cancelOrder(new CancelOrderRequest(pair.getPair1Pair2Name(), orderId));
	}

	private static void cancelOrders(BinanceApiRestClient client, Pair pair, ArrayList<Long> listOfOrdersStrategy,
			HashMap<Double, Double> listOfSellsStrategyTest, HashMap<Double, Double> listOfBuysStrategyTest) {
		if (isTestingMode) {
			ArrayList<Double> ordersToDelete = new ArrayList<Double>();

			for (@SuppressWarnings("rawtypes")
			Map.Entry mapEntry2 : listOfSellsStrategyTest.entrySet()) {
				double order = (double) mapEntry2.getKey();
				ordersToDelete.add(order);
				// reajuster les free et used balance
				System.out.println("Avant annulation vente [prix : " + order + ", qutité : "
						+ listOfSellsStrategyTest.get(order) + "] :");
				System.out.println("FreeBalanceFLUX : " + balances.get("FLUX").getFreeBalance());
				System.out.println("UsedBalanceFLUX : " + balances.get("FLUX").getUsedBalance());
				System.out.println("Apres annulation vente :");
				double newFreeBalanceFLUX = balances.get("FLUX").getFreeBalance() + listOfSellsStrategyTest.get(order);
				double newUsedBalanceFLUX = balances.get("FLUX").getUsedBalance() - listOfSellsStrategyTest.get(order);
				balances.get("FLUX").setUsedBalance(newUsedBalanceFLUX);
				balances.get("FLUX").setFreeBalance(newFreeBalanceFLUX);
				System.out.println("FreeBalanceFLUX : " + balances.get("FLUX").getFreeBalance());
				System.out.println("UsedBalanceFLUX : " + balances.get("FLUX").getUsedBalance());

			}

			for (double order : ordersToDelete) {
				listOfSellsStrategyTest.remove(order);
			}
			ordersToDelete.clear();

			for (@SuppressWarnings("rawtypes")
			Map.Entry mapEntry : listOfBuysStrategyTest.entrySet()) {
				double order = (double) mapEntry.getKey();
				ordersToDelete.add(order);
				// reajuster les free et used balance
				System.out.println("Avant annulation achat [prix : " + order + ", qutité : "
						+ listOfBuysStrategyTest.get(order) + "] :");
				System.out.println("FreeBalanceUSDT : " + balances.get("USDT").getFreeBalance());
				System.out.println("UsedBalanceUSDT : " + balances.get("USDT").getUsedBalance());
				System.out.println("Apres annulation achat :");
				double newFreeBalanceUSDT = balances.get("USDT").getFreeBalance() + listOfBuysStrategyTest.get(order);
				double newUsedBalanceUSDT = balances.get("USDT").getUsedBalance() - listOfBuysStrategyTest.get(order);
				balances.get("USDT").setFreeBalance(newFreeBalanceUSDT);
				balances.get("USDT").setUsedBalance(newUsedBalanceUSDT);
				System.out.println("FreeBalanceUSDT : " + balances.get("USDT").getFreeBalance());
				System.out.println("UsedBalanceUSDT : " + balances.get("USDT").getUsedBalance());
			}

			for (double order : ordersToDelete) {
				listOfBuysStrategyTest.remove(order);
			}
			ordersToDelete.clear();
		} else {
			for (long orderId : listOfOrdersStrategy) {
				OrderStatus orderStatus = client
						.getOrderStatus(new OrderStatusRequest(pair.getPair1Pair2Name(), orderId)).getStatus();
				boolean orderIsActive = (orderStatus == OrderStatus.NEW)
						|| (orderStatus == OrderStatus.PARTIALLY_FILLED);
				if (orderIsActive) {
					try {
						ParserForTests.write(log,
								"Ordre annulé - pair : " + pair + " | statut : " + orderStatus + " | id : " + orderId);
						cancelOrder(client, orderId, pair);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		ArrayList<Double> ma30Array = null;
		ArrayList<Double> trendArray = null;
		try {
			// init FileWriter
			log = new FileWriter(traces);
			// init listOfStrategyCoeff
			listOfStrategyCoeff.add(1.0);
			listOfStrategyCoeff.add(0.0);
			ParserForTests.write(log, "========== Initialisation ==========");

			// only for testing mode
			if (isTestingMode) {
				balances.put("FLUX", new Balance(1500.0, 0.0));
				balances.put("USDT", new Balance(1000.0, 0.0));
				System.out.println("Balance FLUX : " + balances.get("FLUX").getFreeBalance());
				System.out.println("Balance USDT : " + balances.get("USDT").getFreeBalance());
			}
			// Load xml, ma30 and ma30Trending
			XmlFluxUsdt xmlFluxUsdt = ParserForTests.loadXml();
			ma30Array = ParserForTests.loadMa30(xmlFluxUsdt);
			trendArray = ParserForTests.loadMa30Trending(xmlFluxUsdt);
			ParserForTests.write(log, "ma30Array : " + ma30Array);
			ParserForTests.write(log, "trendArray : " + trendArray);
			// create csv file with wallet value evolution
			String csvFileName = ParserForTests.createFile("csvFluxUsdt", ".csv");
			csvResult = new FileWriter(csvFileName);
			// connection API
			@SuppressWarnings("deprecation")
			BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
			@SuppressWarnings("deprecation")
			BinanceApiRestClient client = factory.newRestClient();

////	Test connectivity
//client.ping();
//
////	Check server time
//long serverTime = client.getServerTime();
//System.out.println("server time : " + serverTime);
//System.out.println("system time - server time : " + (System.currentTimeMillis() - serverTime));

			// Definition des variables pour la paire FLUXUSDT
			boolean breakMa30Action = true;
			boolean countingDown = false;
			final String pair1 = "USDT";
			final String pair2 = "FLUX";
			final ArrayList<Double> percentageTriggerList = new ArrayList<Double>(Arrays.asList(0.095, 0.15, 0.2, 0.3));
			final ArrayList<Double> percentageBuyList = new ArrayList<Double>(
					Arrays.asList(0.1375, 0.1375, 0.075, 0.1625));
			final ArrayList<Double> percentageSellList = new ArrayList<Double>(Arrays.asList(0.1, 0.225, 0.125, 0.075));
			Pair FLUXUSDT = new Pair(pair2, pair1, percentageTriggerList, percentageTriggerList, percentageBuyList,
					percentageSellList);

			Account account = client.getAccount();
			ParserForTests.write(log, "account : " + account);
			double USDTBalance = getBalance(pair1, account, 0);
			double FLUXBalance = getBalance(pair2, account, 0);
			// On récupere le prix actuel du coin
			double FLUXPrice = 0.0;

			ArrayList<Double> ordersToDelete = new ArrayList<Double>();

			if (isTestingMode) {
				FLUXPrice = 1.5;

			} else {
				FLUXPrice = Double.parseDouble(client.getPrice(pair2 + pair1).getPrice());
			}
			// System.out.println(Double.parseDouble(client.getPrice(pair2 +
			// pair1).getPrice()));
			InstanceOfMA30 FLUXma30 = new InstanceOfMA30(pair2, pair1, client, isTestingMode, new ReadFile(0));
			FLUXma30.setLast30hPrices1h(ma30Array);
			FLUXma30.setMa30Trending(trendArray); // @FIXME ajouter au constructeur
			// on instancie priceLowerThanMa30 et previousPriceLowerThanMa30
			priceLowerThanMa30 = (FLUXPrice < FLUXma30.getUpdateMa30());
			ParserForTests.write(log, "ma30 : " + FLUXma30.getMa30());
			previousPriceLowerThanMa30 = priceLowerThanMa30;

			ParserForTests.write(log, "========== Initialisation FIN ==========");
			ParserForTests.write(log, "========== Début ==========");
			int testcounter = 1;
			int testCsvFileCounter = 0;

			while (true) {
				ParserForTests.write(log, "---- Tour " + testcounter++ + " ----");
				ParserForTests.write(log, "ma30 : " + FLUXma30.getLast30hPrices1h().toString());
				ParserForTests.write(log, "ma30Trending : " + FLUXma30.getMa30Trending().toString());
				if (isTestingMode) {
					Thread.sleep(100);
					csvResult.write((testCsvFileCounter) + "," + balances.get(pair2).getBalanceString() + ","
							+ balances.get(pair1).getBalanceString() + ","
							+ (Math.round(balances.get(pair2).getBalance() * FLUXPrice)
									+ balances.get(pair1).getBalance()));
					testCsvFileCounter += 5;
				} else {
					Thread.sleep(300000); // 5min
					csvResult.write(ParserForTests.getDate() + ","
							+ String.valueOf(Double.parseDouble(client.getAccount().getAssetBalance(pair2).getFree())
									+ Double.parseDouble(client.getAccount().getAssetBalance(pair2).getLocked()))
									.replace(',', '.')
							+ ","
							+ String.valueOf(Double.parseDouble(client.getAccount().getAssetBalance(pair1).getFree())
									+ Double.parseDouble(client.getAccount().getAssetBalance(pair1).getLocked()))
									.replace(',', '.')
							+ ",");
				}
				csvResult.write("\n");
				// testing mode : on verifie si le prix d'achat/vente d'un des ordres est
				// compris entre high et low du reader
				if (isTestingMode) {
					for (@SuppressWarnings("rawtypes")
					Map.Entry mapEntry2 : listOfSellsStrategy0Test.entrySet()) {
						double order = (double) mapEntry2.getKey();
						System.out.println("ordre de vente : prix : " + order + " | quantité : "
								+ listOfSellsStrategy0Test.get(order));
						if (order <= FLUXma30.getReader().getActualPrice().getHigh()) {
							// MAJ balance, retirer ordre de la liste
							System.out.println("le FLUX vaut [" + FLUXma30.getReader().getActualPrice().getLow() + " , "
									+ FLUXma30.getReader().getActualPrice().getHigh() + " ] , on vend "
									+ listOfSellsStrategy0Test.get(order) + " FLUX au prix de : " + order);
							double newUsedBalanceFLUX = balances.get("FLUX").getUsedBalance()
									- listOfSellsStrategy0Test.get(order);
							System.out.println("UsedBalanceFLUX : " + balances.get("FLUX").getUsedBalance());
							System.out.println("new UsedBalanceFLUX : " + newUsedBalanceFLUX);
							double newFreeBalanceUSDT = balances.get("USDT").getFreeBalance()
									+ (listOfSellsStrategy0Test.get(order) * order);
							System.out.println("FreeBalanceUSDT : " + balances.get("USDT").getFreeBalance());
							System.out.println("new FreeBalanceUSDT : " + newFreeBalanceUSDT);
							balances.get("FLUX").setUsedBalance(newUsedBalanceFLUX);
							balances.get("USDT").setFreeBalance(newFreeBalanceUSDT);
							System.out.println("Balance FLUX :"
									+ (balances.get("FLUX").getFreeBalance() + balances.get("FLUX").getUsedBalance()));
							System.out.println("Balance USDT :"
									+ (balances.get("USDT").getFreeBalance() + balances.get("USDT").getUsedBalance()));
							ordersToDelete.add(order);
						}
					}
					for (double order : ordersToDelete) {
						listOfSellsStrategy0Test.remove(order);
					}
					ordersToDelete.clear();

					for (@SuppressWarnings("rawtypes")
					Map.Entry mapEntry : listOfBuysStrategy0Test.entrySet()) {
						double order = (double) mapEntry.getKey();
						System.out.println("ordre d'achat : prix : " + order + " | quantité : "
								+ listOfBuysStrategy0Test.get(order));
						if (order >= FLUXma30.getReader().getActualPrice().getLow()) {
							// MAJ balance, retirer ordre de la liste
							System.out.println("le FLUX vaut [" + FLUXma30.getReader().getActualPrice().getLow() + " , "
									+ FLUXma30.getReader().getActualPrice().getHigh() + " ] , on achete " + order
									+ " | qutité : " + listOfBuysStrategy0Test.get(order));

							double newFreeBalanceFLUX = balances.get("FLUX").getFreeBalance()
									+ listOfBuysStrategy0Test.get(order);
							System.out.println("FreeBalanceFLUX : " + balances.get("FLUX").getFreeBalance());
							System.out.println("new FreeBalanceFLUX : " + newFreeBalanceFLUX);

							double newUsedBalanceUSDT = balances.get("USDT").getUsedBalance()
									- listOfBuysStrategy0Test.get(order);
							System.out.println("UsedBalanceUSDT : " + balances.get("USDT").getUsedBalance());
							System.out.println("new UsedBalanceUSDT : " + newUsedBalanceUSDT);
							balances.get("FLUX").setFreeBalance(newFreeBalanceFLUX);
							balances.get("USDT").setUsedBalance(newUsedBalanceUSDT);
							System.out.println("Balance FLUX :"
									+ (balances.get("FLUX").getFreeBalance() + balances.get("FLUX").getUsedBalance()));
							System.out.println("Balance USDT :"
									+ (balances.get("USDT").getFreeBalance() + balances.get("USDT").getUsedBalance()));
							ordersToDelete.add(order);
						}
					}

					for (double order : ordersToDelete) {
						listOfBuysStrategy0Test.remove(order);
					}
					ordersToDelete.clear();
				}
				// On MAJ les balances
				USDTBalance = getBalance(pair1, account, 0);
				// System.out.println(account.getAssetBalance(pair1).getFree());
				FLUXBalance = getBalance(pair2, account, 0);
				// on MAJ la durée de vie des ordre d'achat/vente
				if (countingDown) {
					if (isTestingMode) {
						strategy0Countdown += 1.0;
					} else {
						strategy0Countdown += 0.084;
					}
					ParserForTests.write(log, "ordersCountdown : " + strategy0Countdown);
					// environ 1/12 d'heure
					// On incrémente tous les ordres de 5min -> annulés au bout de 27h
				}
				if (strategy0Countdown > 7) {
					breakMa30Action = true;
				}
				if (strategy0Countdown > 27) {
					strategy0Countdown = 0.0;
					countingDown = false;
					cancelOrders(client, FLUXUSDT, listOfOrdersStrategy0, listOfSellsStrategy0Test,
							listOfBuysStrategy0Test);
					ParserForTests.write(log, "On annule tous les ordres");
				}

				// On récupere le prix actuel du coin
				FLUXPrice = Double.parseDouble(client.getPrice(pair2 + pair1).getPrice());
				// On MAJ priceLowerThanMa30
				if (isTestingMode) {
					System.out.println("FLUXPrice (low): " + FLUXma30.getReader().getActualPrice().getLow());
					double ma30 = FLUXma30.getUpdateMa30();
					System.out.println("Ma30 : " + ma30);
					priceLowerThanMa30 = (FLUXma30.getReader().getActualPrice().getLow() <= (1.005 * ma30));
				} else {
					ParserForTests.write(log, "FLUXPrice : " + FLUXPrice);
					priceLowerThanMa30 = (FLUXPrice <= (1.005 * FLUXma30.getUpdateMa30()));
				}
				if (FLUXma30.getReader().isBreaker()) {
					ParserForTests.write(log, "Solde USDT : "
							+ (balances.get("USDT").getFreeBalance() + balances.get("USDT").getUsedBalance()));
					ParserForTests.write(log, "Solde FLUX : "
							+ (balances.get("FLUX").getFreeBalance() + balances.get("FLUX").getUsedBalance()));
					break;
				}
				if ((priceLowerThanMa30 != previousPriceLowerThanMa30)) {
					previousPriceLowerThanMa30 = priceLowerThanMa30;
					if (breakMa30Action) {
						cancelOrders(client, FLUXUSDT, listOfOrdersStrategy0, listOfSellsStrategy0Test,
								listOfBuysStrategy0Test);
						ParserForTests.write(log, "On annule tous les ordres");
						buyOrder(client, FLUXUSDT, USDTBalance, FLUXma30.getMa30(), listOfOrdersStrategy0,
								listOfBuysStrategy0Test);
						sellOrder(client, FLUXUSDT, FLUXBalance, FLUXma30.getMa30(), listOfOrdersStrategy0,
								listOfSellsStrategy0Test);
						if (isTestingMode) {
							System.out.println("FreeBalanceFLUX : " + balances.get("FLUX").getFreeBalance());
							System.out.println("UsedBalanceFLUX : " + balances.get("FLUX").getUsedBalance());
							System.out.println("FreeBalanceUSDT : " + balances.get("USDT").getFreeBalance());
							System.out.println("UsedBalanceUSDT : " + balances.get("USDT").getUsedBalance());
						} else {
							USDTBalance = getBalance(pair1, account, 0);
							FLUXBalance = getBalance(pair2, account, 0);
							ParserForTests.write(log, "Balance FLUX : " + FLUXBalance);
							ParserForTests.write(log, "Balance USDT : " + USDTBalance);
						}
						breakMa30Action = false;
						countingDown = true;
					}
					strategy0Countdown = 0.0;
				}

			}
			ParserForTests.write(log, "========== Fin ==========");
		} catch (

		Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// @TODO : en cas d'erreur, on sauvegarde toutes les valeurs dans le XML, on
			// annule tous les ordres en cours et on releve l'exception
			ParserForTests.saveData(ma30Array, trendArray);
			// on ferme les FileWriter de log et de csvResult
			log.close();
			csvResult.close();
		}
	}
}
