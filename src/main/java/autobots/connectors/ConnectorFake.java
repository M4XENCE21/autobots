package autobots.connectors;

import java.io.FileWriter;
import java.io.IOException;

import autobots.connectors.accountFake.AccountFake;
import autobots.connectors.accountFake.AssetBalanceFake;
import autobots.parsing.Parser;

public class ConnectorFake {

	/** File containing logs. */
	private FileWriter log;

	/** Binance account, with all balances. */
	private AccountFake account;

	public ConnectorFake(FileWriter log) {
		this.log = log;
		Parser.write(log, "========== Initialisation ==========");
		account = new AccountFake();
		account.setAssetBalance(new AssetBalanceFake("ETH", "2.5", "0"));
		account.setAssetBalance(new AssetBalanceFake("USDT", "5000", "0"));
		Parser.write(log, "account : " + account);
		Parser.write(log, "========== Initialisation FIN ==========");
	}

	public void connect() throws IOException {

	}

	public FileWriter getLog() {
		return log;
	}

	public AccountFake getAccount() {
		return account;
	}

}
