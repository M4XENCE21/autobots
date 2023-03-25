package autobots.indicators;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import autobots.util.DecimalNumber;

public class BollingerBandTest {
	@Test
	public void testNominal() {
		BollingerBand bb = new BollingerBand(IndicatorsToChart.loadCsvSeriesCustom("FLUXUSDT-1h-2022-03.csv"), 14);
		DecimalNumber dn = DecimalNumber.THREE;
		String expectedString = bb.getLbb(dn) + " " + bb.getUbb(dn) + " " + bb.getMbb(dn);
		assertEquals(expectedString, "1,692 1,880 1,786");
	}
}
