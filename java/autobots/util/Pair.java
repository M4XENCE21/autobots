package autobots.util;

import java.util.ArrayList;

public class Pair {
	private String pair1Pair2Name;
	private String pair1Name;
	private String pair2Name;
	private ArrayList<Double> percentageTriggerBuyList;
	private ArrayList<Double> percentageTriggerSellList;
	private ArrayList<Double> percentageBuyList;
	private ArrayList<Double> percentageSellList;

	public String getPair1Pair2Name() {
		return pair1Pair2Name;
	}

	public String getPair1Name() {
		return pair1Name;
	}

	public String getPair2Name() {
		return pair2Name;
	}

	public void setPair1Name(String pair1Name) {
		this.pair1Name = pair1Name;
	}

	public void setPair2Name(String pair2Name) {
		this.pair2Name = pair2Name;
	}

	public Pair(String pair1Name, String pair2Name, ArrayList<Double> percentageTriggerBuyList,
			ArrayList<Double> percentageTriggerSellList, ArrayList<Double> percentageBuyList,
			ArrayList<Double> percentageSellList) {
		this.pair1Pair2Name = pair1Name + pair2Name;
		this.pair1Name = pair1Name;
		this.pair2Name = pair2Name;
		this.percentageTriggerBuyList = percentageTriggerBuyList;
		this.percentageTriggerSellList = percentageTriggerSellList;
		this.percentageBuyList = percentageBuyList;
		this.percentageSellList = percentageSellList;
	}

	public ArrayList<Double> getPercentageTriggerBuyList() {
		return percentageTriggerBuyList;
	}

	public ArrayList<Double> getPercentageTriggerSellList() {
		return percentageTriggerSellList;
	}

	public ArrayList<Double> getPercentageBuyList() {
		return percentageBuyList;
	}

	public ArrayList<Double> getPercentageSellList() {
		return percentageSellList;
	}

	public void setPair1Pair2Name(String pair1Pair2Name) {
		this.pair1Pair2Name = pair1Pair2Name;
	}

	public void setPercentageBuyList(ArrayList<Double> percentageBuyList) {
		this.percentageBuyList = percentageBuyList;
	}

	public void setPercentageSellList(ArrayList<Double> percentageSellList) {
		this.percentageSellList = percentageSellList;
	}

}
