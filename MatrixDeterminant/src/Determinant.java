public class Determinant {
	public static double calculate(double matrix[][], int size) {
		double determinant = 1;
		for (int i = 0; i < size; i++)
			determinant *= matrix[i][i];
		return determinant;
	}
}
