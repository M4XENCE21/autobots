package autobots.strategies;

import autobots.indicators.BollingerBand;

public abstract class Strategy {

	@SuppressWarnings("unused")
	private BollingerBand bb;

	/** Initialize indicators or other data required before starting. */
	Strategy(BollingerBand bollingerBand) {
		this.bb = bollingerBand;
	}

	/** Run the strategy */
	abstract void run();
}
