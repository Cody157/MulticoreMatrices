import java.text.DecimalFormat;
import java.util.concurrent.locks.ReentrantLock;

public class MainMethod {

	static final int MAX_VAL = 9;
	static final int MATRIX_SIZE = 50;
	static final int THREADS = 4;
	static double matrix[][];

	public static void main(String[] args) {

		int i, j, csCount;
		int csSize = MATRIX_SIZE / THREADS;
		if (MATRIX_SIZE % THREADS == 0)
			csCount = THREADS;
		else
			csCount = THREADS + 1;

		ReentrantLock[] lock = new ReentrantLock[csCount];
		for (i = 0; i < csCount; i++) {
			lock[i] = new ReentrantLock();
		}
		matrix = new double[MATRIX_SIZE][MATRIX_SIZE];
		ThreadPool.tr = new Thread[THREADS];

		for (i = 0; i < MATRIX_SIZE; i++) {
			for (j = 0; j < MATRIX_SIZE; j++) {
				matrix[i][j] = 2 * (Math.random() - 0.5) * MAX_VAL;
			}
		}
		printMatrix();
		
		//Start Timer Here
		Timer.setStartTime();
		Gauss r = new Gauss(matrix, MATRIX_SIZE, 0, lock, csSize,
				THREADS);
		ThreadPool.tr[0] = new Thread(r);
		ThreadPool.tr[0].start();

	}

	static void printMatrix() {
		String str = "\t";
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		for (int i = 0; i < MATRIX_SIZE; i++) {
			for (int j = 0; j < MATRIX_SIZE; j++) {
				if (matrix[i][j] < 0)
					str += df.format(matrix[i][j]) + "\t";
				else
					str += " " + df.format(matrix[i][j]) + "\t";
			}
			System.out.println(str);
			str = "\t";
		}
	}
}
