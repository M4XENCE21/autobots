package autobots.connectors;

import java.io.FileWriter;
import java.io.IOException;

import autobots.connectors.accountFake.AccountFake;
import autobots.parsing.Parser;

public class ConnectorFake {

	/** File containing logs. */
	private FileWriter log;

	/** Binance account, with all balances. */
	private AccountFake account;

	public ConnectorFake(FileWriter log) {
		this.log = log;
	}

	public void connect() throws IOException {
		Parser.write(log, "========== Initialisation ==========");
		// connection API
		account = new AccountFake();
		Parser.write(log, "account : " + account);
		Parser.write(log, "========== Initialisation FIN ==========");
	}

	public FileWriter getLog() {
		return log;
	}

	public AccountFake getAccount() {
		return account;
	}

}
