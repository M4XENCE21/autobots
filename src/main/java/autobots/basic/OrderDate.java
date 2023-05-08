package autobots.basic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OrderDate {

	private DateTimeFormatter dtf;
	private LocalDateTime date;
	private String dateString;

	public OrderDate() {
		super();
		this.dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		this.date = LocalDateTime.now();
		this.dateString = dtf.format(date);
	}

	/**
	 * @return the dtf
	 */
	public DateTimeFormatter getDtf() {
		return dtf;
	}

	/**
	 * @return the date
	 */
	public LocalDateTime getDate() {
		return date;
	}

	/**
	 * @return the dateString
	 */
	public String getDateString() {
		return dateString;
	}
}
