package autobots.connectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class ConnectorTest {
	private static final String SECRET = "xxx";
	private static final String API_KEY = "xxx";

	@Test
	public void testNominal() throws IOException {
//		Connector connector = new Connector(new FileWriter(Parser.createFile("traces_test", ".txt")));
//		connector.connect(API_KEY, SECRET);
		String expectedString = "A COMPLETER";// connector.getAccount().getAssetBalance("BNB").toString();
		assertEquals(expectedString, "A COMPLETER");
	}
}
