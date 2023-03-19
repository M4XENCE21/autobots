package autobots.basic;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.TimeInForce;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.NewOrder;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.request.CancelOrderRequest;
import com.binance.api.client.domain.account.request.OrderStatusRequest;

import autobots.parsing.Parser;
import autobots.testUtils.Balance;
import autobots.util.Pair;

public class tradingTest {
	private static ArrayList<Double> listOfStrategyCoeff = new ArrayList<Double>();
	private static FileWriter log = null;
	private final static boolean isTestingMode = true;
	// Variables pour le mode test :
	private static HashMap<String, Balance> balances = new HashMap<String, Balance>();

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
					Parser.write(log, "Nouvel ordre d'achat de " + quantityString + " FLUX au prix de " + priceString
							+ "| id : " + newOrderResponse.getOrderId());

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
				Parser.write(log, "Nouvel ordre de vente de " + quantityString + " FLUX au prix de " + priceString
						+ "| id : " + newOrderResponse.getOrderId());
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
						Parser.write(log,
								"Ordre annulé - pair : " + pair + " | statut : " + orderStatus + " | id : " + orderId);
						cancelOrder(client, orderId, pair);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
