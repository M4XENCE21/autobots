package autobots.connectors;

import java.io.IOException;

import autobots.connectors.accountFake.AccountFake;
import autobots.connectors.accountFake.AssetBalanceFake;
import autobots.parsing.ParserForTests;

public class ConnectorFake extends AConnector {

	/** Binance account, with all balances. */
	private AccountFake account;

	public ConnectorFake() throws IOException {
		super();
		ParserForTests.write(log, "========== Initialisation ==========");
		account = new AccountFake();
		account.setAssetBalance(new AssetBalanceFake("ETH", "2.5", "0"));
		account.setAssetBalance(new AssetBalanceFake("USDT", "5000", "0"));
		ParserForTests.write(log, "account : " + account);
		ParserForTests.write(log, "========== Initialisation FIN ==========");
	}

	public AccountFake getAccount() {
		return account;
	}

}
