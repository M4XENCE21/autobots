package autobots.basic;

import java.util.ArrayList;

import autobots.connectors.ConnectorFake;
import autobots.parsing.Parser;
import autobots.parsing.ParserForTests;

public class TradingForTest {

	private ArrayList<OrderForTest> listOfBuyOrders;
	private ArrayList<OrderForTest> listOfSellOrders;
	private int currentPriceIndex;

	public TradingForTest(ArrayList<OrderForTest> listOfBuyingOrders, ArrayList<OrderForTest> listOfSellingOrders) {
		super();
		this.listOfBuyOrders = listOfBuyingOrders;
		this.listOfSellOrders = listOfSellingOrders;
		this.currentPriceIndex = 0;
	}

	public ArrayList<OrderForTest> getListOfBuyOrders() {
		return listOfBuyOrders;
	}

	public void addBuyOrder(OrderForTest newOrder) {
		this.listOfBuyOrders.add(newOrder);
	}

	public void removeBuyOrder(ConnectorFake connector, OrderDate date, String symbol1, String symbol2) {
		for (int i = 0; i < listOfBuyOrders.size(); i++) {
			if (listOfBuyOrders.get(i).getOrderDate().getDate().compareTo(date.getDate()) <= 0
					&& (isSamePair(symbol1, symbol2, listOfBuyOrders, i))) {
				double lockedBalance = Double.parseDouble(
						connector.getAccount().getAssetBalance(listOfBuyOrders.get(i).getSymbol2()).getLocked());
				double newFree = (listOfBuyOrders.get(i).getQuantity() * listOfBuyOrders.get(i).getPrice());
				double freeBalance = Double.parseDouble(
						connector.getAccount().getAssetBalance(listOfBuyOrders.get(i).getSymbol2()).getFree());
				connector.getAccount().getAssetBalance(listOfBuyOrders.get(i).getSymbol2())
						.setFree(String.valueOf(freeBalance + newFree));
				connector.getAccount().getAssetBalance(listOfBuyOrders.get(i).getSymbol2())
						.setLocked(String.valueOf(lockedBalance - newFree));
				Parser.write(connector.getLog(), "Ordre annulé - pair : " + listOfBuyOrders.get(i).getSymbol1()
						+ listOfBuyOrders.get(i).getSymbol2() + " | quantité : " + listOfBuyOrders.get(i).getQuantity()
						+ " | prix : " + listOfBuyOrders.get(i).getPrice() + " | date : "
						+ listOfBuyOrders.get(i).getOrderDate().getDateString());
				listOfBuyOrders.remove(i);
			}
		}
	}

	public ArrayList<OrderForTest> getListOfSellOrders() {
		return listOfSellOrders;
	}

	public void addSellOrder(OrderForTest newOrder) {
		this.listOfSellOrders.add(newOrder);
	}

	public void removeSellOrder(ConnectorFake connector, OrderDate date, String symbol1, String symbol2) {
		for (int i = 0; i < listOfSellOrders.size(); i++) {
			if ((listOfSellOrders.get(i).getOrderDate().getDate().compareTo(date.getDate()) <= 0)
					&& (isSamePair(symbol1, symbol2, listOfSellOrders, i))) {
				double lockedBalance = Double.parseDouble(
						connector.getAccount().getAssetBalance(listOfSellOrders.get(i).getSymbol1()).getLocked());
				double newFree = listOfSellOrders.get(i).getQuantity();
				double freeBalance = Double.parseDouble(
						connector.getAccount().getAssetBalance(listOfSellOrders.get(i).getSymbol1()).getFree());
				connector.getAccount().getAssetBalance(listOfSellOrders.get(i).getSymbol1())
						.setFree(String.valueOf(freeBalance + newFree));
				connector.getAccount().getAssetBalance(listOfSellOrders.get(i).getSymbol1())
						.setLocked(String.valueOf(lockedBalance - newFree));
				Parser.write(connector.getLog(), "Ordre annulé - pair : " + listOfSellOrders.get(i).getSymbol1()
						+ listOfSellOrders.get(i).getSymbol2() + " | quantité : "
						+ listOfSellOrders.get(i).getQuantity() + " | prix : " + listOfSellOrders.get(i).getPrice()
						+ " | date : " + listOfSellOrders.get(i).getOrderDate().getDateString());
				listOfSellOrders.remove(i);
			}
		}
	}

	private boolean isSamePair(String symbol1, String symbol2, ArrayList<OrderForTest> list, int i) {
		boolean sameSymbol1 = (list.get(i).getSymbol1().equalsIgnoreCase(symbol1));
		boolean sameSymbol2 = (list.get(i).getSymbol2().equalsIgnoreCase(symbol2));
		return sameSymbol1 && sameSymbol2;
	}

	public double getBalance(String pair, ConnectorFake connector) {

		return Double.parseDouble(connector.getAccount().getAssetBalance(pair).getFree());
	}

	public void buyOrder(ConnectorFake connector, String symbolBought, String symbolBuying, double price,
			double quantity) {

		// determiner quantity a partir de balances
		String freeQuantity = connector.getAccount().getAssetBalance(symbolBuying).getFree();
		String lockedQuantity = connector.getAccount().getAssetBalance(symbolBuying).getLocked();

		// On vérifie que l'ordre est réalisable
		if ((price * quantity) > Double.parseDouble(freeQuantity)) {
			Parser.write(connector.getLog(),
					"[ERREUR] : balance de " + symbolBuying + " insuffisante - quantité voulue : " + price * quantity
							+ " - quantité dispo :" + freeQuantity + "| date : " + ParserForTests.getDate());
			return;
		}

		// On ajoute l'ordre dans la liste des ordres
		OrderForTest order = new OrderForTest(symbolBought, symbolBuying, price, quantity);
		addBuyOrder(order);
		// On MAJ les balances
		connector.getAccount().getAssetBalance(symbolBought)
				.setFree(String.valueOf(Double.parseDouble(freeQuantity) - (price * quantity)));
		connector.getAccount().getAssetBalance(symbolBuying)
				.setLocked(String.valueOf(Double.parseDouble(lockedQuantity) + (price * quantity)));
		Parser.write(connector.getLog(), "Nouvel ordre d'achat de " + quantity + " " + symbolBought + " au prix de "
				+ price + " " + symbolBuying + " | date : " + order.getOrderDate().getDateString());

	}

	public void sellOrder(ConnectorFake connector, String symbolSold, String symbolBuying, double price,
			double quantity) {

		// determiner quantity a partir de balances
		String freeQuantity = connector.getAccount().getAssetBalance(symbolSold).getFree();
		String lockedQuantity = connector.getAccount().getAssetBalance(symbolSold).getLocked();

		// On vérifie que l'ordre est réalisable
		if (quantity > Double.parseDouble(freeQuantity)) {
			Parser.write(connector.getLog(),
					"[ERREUR] : balance de " + symbolSold + " insuffisante - quantité voulue : " + quantity
							+ " - quantité dispo :" + freeQuantity + "| date : " + ParserForTests.getDate());
			return;
		}

		// On ajoute l'ordre dans la liste des ordres
		OrderForTest order = new OrderForTest(symbolSold, symbolBuying, price, quantity);
		addSellOrder(order);

		// On MAJ les balances
		connector.getAccount().getAssetBalance(symbolSold)
				.setFree(String.valueOf(Double.parseDouble(freeQuantity) - quantity));
		connector.getAccount().getAssetBalance(symbolSold)
				.setLocked(String.valueOf(Double.parseDouble(lockedQuantity) + quantity));
		Parser.write(connector.getLog(), "Nouvel ordre de vente de " + quantity + " " + symbolSold + " au prix de "
				+ price + " " + symbolBuying + " | date : " + order.getOrderDate().getDateString());

	}

	public void cancelOrders(ConnectorFake connector, String symbol1, String symbol2) {
		OrderDate orderDate = new OrderDate();
		removeBuyOrder(connector, orderDate, symbol1, symbol2);
		removeSellOrder(connector, orderDate, symbol1, symbol2);
	}

	public double getPrice(String symbol1, String symbol2) {
		return 0; // @TODO

	}

}
