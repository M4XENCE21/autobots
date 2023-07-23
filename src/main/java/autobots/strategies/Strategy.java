package autobots.strategies;

public abstract class Strategy {

	@SuppressWarnings("unused")
	private String[] csv;

	/** Initialize indicators or other data required before starting. */
	Strategy(String[] csv) {
		this.csv = csv;
	}

	/** Run the strategy */
	abstract void run();
}
