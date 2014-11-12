package matrix;


public class Main
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Double[][] mat = new Double[3][3];
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{ 
				mat[i][j] = (double) i + j;
				if (i == 0 && j == 2) mat[i][j]++;
			}
		}
		
		Matrix matrix = Matrix.randomMatrix(3, 3, 0, 10);
		System.out.println(matrix.toString(3));
		Matrix rref = matrix.rref();
		System.out.println(rref.toString(3));
		Matrix inverse = matrix.inverse();
		System.out.println(inverse.toString(3));
		Double determinant = matrix.determinant();
		System.out.println(determinant);
	}

}
