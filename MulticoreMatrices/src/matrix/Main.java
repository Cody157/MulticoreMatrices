package matrix;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Main
{
	static FileWriter fw;
	static BufferedWriter bw;

	public static void main(String[] args)
	{
		Timer timer = new Timer();
		long multi, single;

		try{
			fw = new FileWriter("Output");
			bw = new BufferedWriter(fw);

//			Double[][] mat = new Double[3][3];
//			for (int i = 0; i < 3; i++)
//			{
//				for (int j = 0; j < 3; j++)
//				{ 
//					mat[i][j] = (double) i + j;
//					if (i == 0 && j == 2)
//						mat[i][j]++;
//				}
//			}

			Double[][] mat = Matrix.randomMatrix(100, 100, -10, 10, 5).clone();

			printMat(mat);
			bw.newLine();

			timer.start();
			Matrix matrix = new Matrix(mat);
			timer.stop();
			System.out.println("Create: " + timer.duration() / 1000000.);
			timer.start();
			MultiMatrix matrixM = new MultiMatrix(mat, 100);
			timer.stop();
			System.out.println("Create M: " + timer.duration() / 1000000.);
			bw.write("matrixM\n" + matrixM + "\n");
			if (!matrixM.equalsMat(matrix))
				bw.write("matrix\n" + matrix + "\n");

			timer.start();
			Matrix rref = matrix.rref();
			timer.stop();
			System.out.println("rref: " + timer.duration() / 1000000.);
			timer.start();
			MultiMatrix rrefM = matrixM.rref();
			timer.stop();
			System.out.println("rref M: " + timer.duration() / 1000000.);

			bw.write("rrefM\n" + rrefM + "\n");
			if (!rrefM.equalsMat(rref))
				bw.write("rref\n" + rref + "\n");

			timer.start();
			Matrix inverse = matrix.inverse();
			timer.stop();
			System.out.println("inverse: " + timer.duration() / 1000000.);
			timer.start();
			MultiMatrix inverseM = matrixM.inverse();
			timer.stop();
			System.out.println("inverse M: " + timer.duration() / 1000000.);

			if (inverse == null && inverseM == null)
				bw.write("Not invertible M\n" + "\n");
			else if (inverse == null || inverseM == null)
			{
				bw.write("inverseM\n" + inverseM + "\n");
				bw.write("inverse\n" + inverse + "\n");
			}
			else if (inverseM.equalsMat(inverse))
				bw.write("inverseM\n" + inverseM + "\n");
			else
			{
				bw.write("inverseM\n" + inverseM + "\n");
				bw.write("inverse\n" + inverse + "\n");
			}

			timer.start();
			Double determinant = matrix.determinant();
			timer.stop();
			System.out.println("determinant: " + timer.duration() / 1000000.);
			timer.start();
			Double determinantM = matrixM.determinant();
			timer.stop();
			System.out.println("determinant M: " + timer.duration() / 1000000.);

			bw.write("DeterminantM\n" + determinantM + "\n");
			if (determinant == null && determinantM == null)
			{
				
			}
			else if (determinant == null || determinantM == null)
			{
				bw.write("Determinant\n" + determinant);
			}
			else if (!determinantM.equals(determinant))
				bw.write("Determinant\n" + determinant + "\n");
			bw.newLine();
			
			timer.start();
			Matrix[] LU = matrix.LU();
			timer.stop();
			System.out.println("LU: " + timer.duration() / 1000000.);
			timer.start();
			MultiMatrix[] LUM = matrixM.LU();
			timer.stop();
			System.out.println("LU M: " + timer.duration() / 1000000.);

			if (LU == null && LUM == null)
			{
				bw.write("No LUP factorization M\n" + "\n");
			}
			else if (LU == null)
			{
				bw.write("LM\n" + LUM[0]);
				bw.write("UM\n" + LUM[1]);
				if (LUM.length == 3)
					bw.write("PM\n" + LUM[2] + "\n");
				bw.write("No LUP factorization\n" + "\n");
			}
			else if (LUM == null)
			{
				bw.write("No LUP factorization M\n" + "\n");
				bw.write("L\n" + LU[0] + "\n");
				bw.write("U\n" + LU[1] + "\n");
				if (LU.length == 3)
					bw.write("P\n" + LU[2] + "\n");
			}
			else
			{
				bw.write("LM\n" + LUM[0] + "\n");
				if (!LUM[0].equalsMat(LU[0]))
					bw.write("L\n" + LU[0] + "\n");
				bw.write("UM\n" + LUM[1] + "\n");
				if (!LUM[1].equalsMat(LU[1]))
					bw.write("U\n" + LU[1] + "\n");
				if (LUM.length == 3)
					bw.write("PM\n" + LUM[2] + "\n");
				else
					bw.write("No P Matrix M" + "\n");
				if (LU.length != LUM.length)
				{
					if (LU.length == 3)
						bw.write("P\n" + LU[2] + "\n");
					else
						bw.write("No P Matrix" + "\n");
				}
				else
				{
					if (LU.length == 3 && !LUM[2].equalsMat(LU[2]))
						bw.write("P\n" + LU[2] + "\n");
				}
			}

			if(LUM != null)
			{
				Matrix mul = Matrix.multiply(new Matrix(LUM[0].clone()), new Matrix(LUM[1].clone()));
				if (LUM.length == 3)
					mul = Matrix.multiply(new Matrix(LUM[2].clone()), mul);
				bw.write("LUP = \n" + mul + "\n");
			}
			bw.write("Done" + "\n");
			bw.close();
		} catch (IOException e) {
			System.out.println("Couldn't find file.");
			return;
		}
	}

	public static void printMat(Double[][] mat) throws IOException
	{
		String matrix = "{";
		for (int i = 0; i < mat.length; i++)
		{
			matrix += "{";
			for (int j = 0; j < mat[i].length; j++)
			{
				if (j != mat[i].length - 1)
					matrix += mat[i][j] + ", ";
				else
					matrix += mat[i][j];
			}
			if (i != mat.length - 1)
				matrix += "}, ";
			else
				matrix += "}";
		}
		matrix += "}";
		bw.write(matrix + "\n");
	}

}