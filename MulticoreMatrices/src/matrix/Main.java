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
//				if (i == 0 && j == 2)
//					mat[i][j]++;
			}
		}
		
//		Matrix matrix = Matrix.randomMatrix(3, 3, 0, 10);
		Matrix matrix = new Matrix(mat);
		System.out.println("matrix\n" + matrix.toString(3));
		Matrix rref = matrix.rref();
		System.out.println("rref\n" + rref.toString(3));
		Matrix inverse = matrix.inverse();
		if (inverse != null)
			System.out.println("inverse\n" + inverse.toString(3));
		else
			System.out.println("Not invertible\n");
		Double determinant = matrix.determinant();
		System.out.println("Determinant\n" + determinant);
		Matrix[] LU = matrix.LU();
		System.out.println("L\n" + LU[0].toString(3));
		System.out.println("U\n" + LU[1].toString(3));
	}

}
