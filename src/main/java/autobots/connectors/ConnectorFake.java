package autobots.connectors;

import java.io.FileWriter;
import java.io.IOException;

import autobots.connectors.accountFake.AccountFake;
import autobots.connectors.accountFake.AssetBalanceFake;
import autobots.parsing.ParserForTests;

public class ConnectorFake {

	/** File containing logs. */
	private FileWriter log;

	/** Binance account, with all balances. */
	private AccountFake account;

	public ConnectorFake() throws IOException {
		log = new FileWriter(ParserForTests.createFile("traces", ".txt"));
		ParserForTests.write(log, "========== Initialisation ==========");
		account = new AccountFake();
		account.setAssetBalance(new AssetBalanceFake("ETH", "2.5", "0"));
		account.setAssetBalance(new AssetBalanceFake("USDT", "5000", "0"));
		ParserForTests.write(log, "account : " + account);
		ParserForTests.write(log, "========== Initialisation FIN ==========");
	}

	public FileWriter getLog() {
		return log;
	}

	public AccountFake getAccount() {
		return account;
	}

}
