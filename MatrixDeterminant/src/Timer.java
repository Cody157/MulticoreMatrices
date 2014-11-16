
public class Timer {
	private static long startTime;
	private static long endTime;
	private static long duration;
	
	public static void setStartTime(){
		startTime = System.nanoTime();
	}
	public static void setEndTime(){
		endTime = System.nanoTime();
		duration = (endTime - startTime)/1000000;
	}
	public static long getDuration(){
		return duration;
	}
}
