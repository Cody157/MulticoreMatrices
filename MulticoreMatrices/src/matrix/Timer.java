package matrix;

public class Timer
{
	private long startTime;
	private long endTime;
	
	public void start(){
		startTime = System.nanoTime();
	}
	
	public void stop(){
		endTime = System.nanoTime();
	}
	
	public long duration(){
		return endTime - startTime;
	}
}