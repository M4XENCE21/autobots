package autobots.strategies;

import java.io.FileWriter;

public abstract class Strategy {

	@SuppressWarnings("unused")
	private FileWriter log;
	@SuppressWarnings("unused")
	private String[] csv;

	/** Initialize indicators or other data required before starting. */
	Strategy(FileWriter log, String[] csv) {
		this.csv = csv;
		this.log = log;
	}

	/** Run the strategy */
	abstract void run();
}
