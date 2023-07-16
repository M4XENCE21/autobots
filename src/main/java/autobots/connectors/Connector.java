package autobots.connectors;

import java.io.FileWriter;
import java.io.IOException;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.account.Account;

import autobots.parsing.Parser;

public class Connector {

	/** File containing logs. */
	private FileWriter log;

	/** Binance API factory. */
	private BinanceApiClientFactory factory;

	/** Binance API client. */
	private BinanceApiRestClient client;

	/** Binance account, with all balances. */
	private Account account;

	@SuppressWarnings("deprecation")
	public Connector(String API_KEY, String SECRET) throws IOException {
		log = new FileWriter(Parser.createFile("traces", ".txt"));
		Parser.write(log, "========== Initialisation ==========");
		// connection API
		factory = BinanceApiClientFactory.newInstance(API_KEY, SECRET);
		client = factory.newRestClient();
		account = client.getAccount();
		Parser.write(log, "account : " + account);
		Parser.write(log, "========== Initialisation FIN ==========");
	}

	public void testConnectivity() {
		// Test connectivity
		client.ping();
		// Check server time
		long serverTime = client.getServerTime();
		System.out.println("server time : " + serverTime);
		System.out.println("system time - server time : " + (System.currentTimeMillis() - serverTime));
	}

	public FileWriter getLog() {
		return log;
	}

	public BinanceApiClientFactory getFactory() {
		return factory;
	}

	public BinanceApiRestClient getClient() {
		return client;
	}

	public Account getAccount() {
		return account;
	}
}
