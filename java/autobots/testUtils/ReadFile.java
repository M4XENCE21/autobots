package autobots.testUtils;

import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import autobots.parsing.Parser;

public class ReadFile {
	private int index;
	private Scanner myReader;
	private DataPriceTestingMode actualPrice;
	private boolean breaker = false;

	public DataPriceTestingMode getActualPrice() {
		return actualPrice;
	}

	public boolean isBreaker() {
		return breaker;
	}

	public void setBreaker(boolean breaker) {
		this.breaker = breaker;
	}

	public void setActualPrice(DataPriceTestingMode actualPrice) {
		this.actualPrice = actualPrice;
	}

	public ReadFile(int index) {
		this.index = index;

		try {
			File myObj = new File(Parser.relativePath + "FLUXUSDT-1h-2021-12.csv");
			myReader = new Scanner(myObj);
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public DataPriceTestingMode getNextPrice() {
		double open = -1;
		double high = -1;
		double low = -1;
		double close = -1;
		if (myReader.hasNextLine()) {
			String data = myReader.nextLine();
			String[] dataUsefull = data.split(",");
			int count = 0;

			for (String str : dataUsefull) {
				if (count == 1) {
					open = Double.parseDouble(str);
				} else if (count == 2) {
					high = Double.parseDouble(str);
				} else if (count == 3) {
					low = Double.parseDouble(str);
				} else if (count == 4) {
					close = Double.parseDouble(str);
				}
				count++;
			}
			index++;
			// transformer en double
		}
		if (open < 0) {
			breaker = true;
		}
		DataPriceTestingMode result = new DataPriceTestingMode(open, high, low, close);
		return result; // retourner le prix
	}

	public void closeReader() {
		myReader.close();
	}
}