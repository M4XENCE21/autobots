package autobots.old;

public class Ma30Interval {
	protected final static int hour = 60;
	protected final static int min = 57;
	protected final static int max = 2;

	protected static int[] min1hHours = new int[] { min, (min + hour), (min + 2 * hour), (min + 3 * hour),
			(min + 4 * hour), (min + 5 * hour), (min + 6 * hour), (min + 7 * hour), (min + 8 * hour), (min + 9 * hour),
			(min + 10 * hour), (min + 11 * hour), (min + 12 * hour), (min + 13 * hour), (min + 14 * hour),
			(min + 15 * hour), (min + 16 * hour), (min + 17 * hour), (min + 18 * hour), (min + 19 * hour),
			(min + 20 * hour), (min + 21 * hour), (min + 22 * hour), (min + 23 * hour) };

	protected static int[] max1hHours = new int[] { (max + hour), (max + 2 * hour), (max + 3 * hour), (max + 4 * hour),
			(max + 5 * hour), (max + 6 * hour), (max + 7 * hour), (max + 8 * hour), (max + 9 * hour), (max + 10 * hour),
			(max + 11 * hour), (max + 12 * hour), (max + 13 * hour), (max + 14 * hour), (max + 15 * hour),
			(max + 16 * hour), (max + 17 * hour), (max + 18 * hour), (max + 19 * hour), (max + 20 * hour),
			(max + 21 * hour), (max + 22 * hour), (max + 23 * hour), max };

	protected static int[] min4hHours = new int[] { (min + hour), (min + 5 * hour), (min + 9 * hour), (min + 13 * hour),
			(min + 17 * hour), (min + 21 * hour) };

	protected static int[] max4hHours = new int[] { (max + 6 * hour), (max + 10 * hour), (max + 14 * hour),
			(max + 18 * hour), (max + 22 * hour), (max + 2 * hour) };

	public static boolean isMa30(int hourInMinutes, int mode) {
		switch (mode) {
		case 1:
			for (int i = 0; i < min1hHours.length; i++) {
				if ((min1hHours[i] <= hourInMinutes) && (max1hHours[i] >= hourInMinutes)) {
					return true;
				}
			}
			return false;
		case 4:
			for (int i = 0; i < min4hHours.length; i++) {
				if ((min4hHours[i] <= hourInMinutes) && (max4hHours[i] >= hourInMinutes)) {
					return true;
				}
			}
			return false;
		default:
			return false;
		}

	}
}
