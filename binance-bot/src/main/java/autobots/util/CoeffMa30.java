package autobots.util;

import java.util.ArrayList;

public class CoeffMa30 {

	private ArrayList<Double> listMa30 = new ArrayList<Double>();
	private int coeffMa30;
	private int indexMa30;
	private int index0;
	private int index1;
	private int index2;
	private int index3;

	public CoeffMa30() {
		super();
		this.listMa30 = new ArrayList<Double>();
		listMa30.add(0.0);
		listMa30.add(0.0);
		listMa30.add(0.0);
		listMa30.add(0.0);
		this.index0 = 0;
		this.index1 = 1;
		this.index2 = 2;
		this.index3 = 3;
		this.coeffMa30 = 0;
		this.indexMa30 = 0;
	}

	public int addMa30(double currentMa30) {
		listMa30.add(indexMa30, currentMa30);
		indexMa30 = (indexMa30 == 3) ? 0 : indexMa30++;
		switch (indexMa30) {
		case 0:
			indexMa30++;
			index0 = 3;
			index1 = 0;
			index2 = 1;
			index3 = 2;
			break;
		case 1:
			indexMa30++;
			index0 = 2;
			index1 = 3;
			index2 = 0;
			index3 = 1;
			break;
		case 2:
			indexMa30++;
			index0 = 1;
			index1 = 2;
			index2 = 3;
			index3 = 0;
			break;
		case 3:
			indexMa30 = 0;
			index0 = 0;
			index1 = 1;
			index2 = 2;
			index3 = 3;
			break;
		default:
			// code block
		}
		coeffMa30 = 0;
		if ((listMa30.get(index3) < listMa30.get(index2)) && (listMa30.get(index2) < listMa30.get(index1))
				&& listMa30.get(index1) < (listMa30.get(index0))) {
			coeffMa30 = -1;
		} else if ((listMa30.get(index3) > listMa30.get(index2)) && (listMa30.get(index2) > listMa30.get(index1))
				&& listMa30.get(index1) > (listMa30.get(index0))) {

		}
		return coeffMa30;
	}

}
