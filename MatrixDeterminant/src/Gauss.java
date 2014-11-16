import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

public class Gauss implements Runnable {

	double matrix[][];
	int size, currentRow, csSize, threads;
	ReentrantLock[] lock;

	public Gauss(double m[][], int n, int currentRow,
			ReentrantLock[] lock, int csSize, int threads) {

		this.matrix = m;
		this.size = n;
		this.currentRow = currentRow;
		this.lock = lock;
		this.csSize = csSize;
		this.threads = threads;
	}

	public void run() {

		final int STEP = 4;
		double val;

		if (currentRow == size) {
			double determinant = Determinant.calculate(matrix, size);
			
			//End Timer Here
			Timer.setEndTime();
			DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(2);
			
			//Prints out the result
			System.out.println();
			
			System.out.println("Determinant = " + determinant);
			System.out.println("Total Time Taken = " + Timer.getDuration() + " ms");
			return;
		}

		lock[currentRow / csSize].lock();
		for (int i = currentRow + 1; i < size; i++) {

			if (i % csSize == 0) {
				lock[i / csSize - 1].unlock();
				lock[i / csSize].lock();
			}

			val = -matrix[i][currentRow] / matrix[currentRow][currentRow];
			matrix[i][currentRow] = 0;
			for (int j = currentRow + 1; j < size; j++)
				matrix[i][j] += val * matrix[currentRow][j];

			if (i == currentRow + STEP) {
				if (currentRow + 1 >= threads && threads > 1)
					try {
						ThreadPool.tr[(currentRow + 1) % threads].join();
					} catch (InterruptedException e) {
					}
				Gauss r = new Gauss(matrix, size, currentRow + 1,
						lock, csSize, threads);
				ThreadPool.tr[(currentRow + 1) % threads] = new Thread(r);
				ThreadPool.tr[(currentRow + 1) % threads].start();
			}
		}

		lock[(size - 1) / csSize].unlock();
		if (size - 1 < currentRow + STEP) {
			if (currentRow + 1 >= threads && threads > 1)
				try {
					ThreadPool.tr[(currentRow + 1) % threads].join();
				} catch (InterruptedException e) {
				}
			Gauss r = new Gauss(matrix, size, currentRow + 1, lock,
					csSize, threads);
			ThreadPool.tr[(currentRow + 1) % threads] = new Thread(r);
			ThreadPool.tr[(currentRow + 1) % threads].start();
		}
	}
}
