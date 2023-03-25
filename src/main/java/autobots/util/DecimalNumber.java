package autobots.util;

import java.text.DecimalFormat;

public enum DecimalNumber {
	ONE(new DecimalFormat("##.0")),

	TWO(new DecimalFormat("##.00")),

	THREE(new DecimalFormat("##.000")),

	FOUR(new DecimalFormat("##.0000")),

	FIVE(new DecimalFormat("##.00000")),

	SIX(new DecimalFormat("##.000000"));

	private DecimalFormat decimalFormat;

	DecimalNumber(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}

	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

}
