package autobots.connectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileWriter;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import autobots.parsing.ParserForTests;

public class ConnectorFakeTest {

	@Test
	public void testNominal() throws IOException {
		ConnectorFake connector = new ConnectorFake(new FileWriter(ParserForTests.createFile("traces_test", ".txt")));
		String expectedString = connector.getAccount().getAssetBalance("ETH").getFree().toString();
		assertEquals(expectedString, "2.5");
		String expectedString2 = connector.getAccount().getAssetBalance("USDT").getFree().toString();
		assertEquals(expectedString2, "5000");
	}
}
