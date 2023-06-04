package autobots.strategies;

public abstract class Strategy {

	/** Initialize indicators or other data required before starting. */
	abstract void initialize();

	/** Run the strategy */
	abstract void run();
}
