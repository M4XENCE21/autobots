package autobots.connectors;

import java.io.FileWriter;
import java.io.IOException;

import autobots.parsing.Parser;

public class AConnector {

	/** File containing logs. */
	protected FileWriter log;

	public AConnector() throws IOException {
		log = new FileWriter(Parser.createFile("traces", ".txt"));
	}

	public FileWriter getLog() {
		return log;
	}
}
