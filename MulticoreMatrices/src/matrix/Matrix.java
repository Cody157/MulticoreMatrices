package matrix;

import java.util.ArrayList;
import java.util.Random;

public class Matrix
{
	
	private Double[][] matrix;
	private int row;
	private int col;
	
	/**
	 * Constructor for initializing a matrix.
	 * Initializes all elements to 0.
	 * @param row : number of rows in the matrix.
	 * @param col : number of columns in the matrix.
	 */
	public Matrix(int numRows, int numCols)
	{
		row = numRows;
		col = numCols;
		matrix = new Double[row][col];
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				matrix[i][j] = new Double(0);
			}
		}
	}
	
	/**
	 * Simplified constructor for square matrices.
	 * @param row : the number of rows and columns in the matrix.
	 */
	public Matrix(int row)
	{
		this(row, row);
	}
	
	/**
	 * Constructs and fills matrix all at once.
	 * @param mat
	 */
	public Matrix (Double[][] mat)
	{
		row = mat.length;
		col = mat[0].length;
		matrix = new Double[row][col];
		matrixCopy(mat, matrix);
	}
	
	/**
	 * Constructs and fills matrix all at once.
	 * @param mat
	 */
	public Matrix(double[][] mat)
	{
		row = mat.length;
		col = mat[0].length;
		matrix = new Double[row][col];
		initMatrix(mat);
	}
	
	/**
	 * Allows users to create matrix based off already existing 2D array.
	 * @param mat : the 2D array of Doubles to represent the matrix.
	 */
	public void initMatrix(Double[][] mat)
	{
		matrixCopy(mat, matrix);
	}
		
	/**
	 * Allows users to create matrix based off already existing 2D array.
	 * Exists because, while a double can be cast to a Double, a double[]
	 * cannot be case to a Double[]
	 * @param mat : the 2D array of doubles to represent the matrix.
	 */
	public void initMatrix(double[][] mat)
	{
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				matrix[i][j] = mat[i][j];
			}
		}
	}
	
	/**
	 * Returns a deep copy of the matrix array.
	 */
	public Double[][] clone()
	{
		Double[][] ret = new Double[row][col];
		matrixCopy(matrix, ret);
		return ret;
	}
	
	/**
	 * @return the number of rows in the matrix.
	 */
	public int rows()
	{
		return row;
	}
	
	/**
	 * @return the number of columns in the matrix.
	 */
	public int cols()
	{
		return col;
	}
	
	public Double get(int i, int j)
	{
		return matrix[i][j];
	}
	
	/**
	 * Creates a matrix with random elements. The caller specifies the number of rows and columns
	 * for the matrix to have, the minimum and maximum values it can contain, and the decimal precision
	 * of each of these elements.
	 * @param numRows the number of rows of the new randomized Matrix
	 * @param numColumns the number of columns of the new randomized Matrix
	 * @param min the double minimum value of an element that can be put in the matrix
	 * @param max the double maximum value of an element that can be put in the matrix
	 * @return a Matrix object with randomized entries
	 */
	public static Matrix randomMatrix(int numRows, int numColumns, double min, double max)
	{
		Matrix mat = new Matrix(numRows, numColumns);
		Random rand = new Random();
		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < numColumns; j++)
			{
				mat.matrix[i][j] = new Double(rand.nextDouble() * (max - min) + min);
			}
		}
		return mat;
	}
	
	/**
	 * Creates a matrix with random elements. The caller specifies the number of rows and columns
	 * for the matrix to have, the minimum and maximum values it can contain, and the decimal precision
	 * of each of these elements.
	 * @param numRows the number of rows of the new randomized Matrix
	 * @param numColumns the number of columns of the new randomized Matrix
	 * @param min the double minimum value of an element that can be put in the matrix
	 * @param max the double maximum value of an element that can be put in the matrix
	 * @return a Matrix object with randomized entries
	 */
	public static Matrix randomMatrix(int numRows, int numColumns, double min, double max, int scale)
	{
		Matrix mat = new Matrix(numRows, numColumns);
		Random rand = new Random();
		int num = 1;
		for (int i = 0; i < scale; i++)
		{
			num *= 10;
		}
		for (int i = 0; i < numRows; i++)
		{
			for (int j = 0; j < numColumns; j++)
			{
				Double val = new Double(rand.nextDouble() * (max - min) + min);
				val = (double) ((int) (val * num + .5)) / num;;
				mat.matrix[i][j] = val;
			}
		}
		return mat;
	}

	
	/**
	 * Creates a triangular matrix with random elements. The caller specifies the number of rows and columns
	 * for the matrix to have, the minimum and maximum values it can contain, and the decimal precision
	 * of each of these elements.
	 * By definition, a triangular matrix is square. This means the dimension given represents both the number of columns and rows.
	 * The 'type' parameter allows the user to determine whether the matrix is upper triangular with positive input, lower triangular with negative input, or diagonal with 0 input.
	 * The minimum and maximum values (inclusive) multiplied by the precision must be representable as integers.
	 * Note: although precision and decimal places are referenced here, all numbers in the matrix will be
	 * rational.
	 * @param dimension the number of rows and columns of the new randomized Matrix
	 * @param type type > 0, upper triangular; type < 0, lower triangular; type == 0, diagonal
	 * @param min the double minimum value of an element that can be put in the matrix
	 * @param max the double maximum value of an element that can be put in the matrix
	 * @return a Matrix object with randomized entries
	 */
	public static Matrix randomTriangularMatrix(int dimension, int type, double min, double max)
	{
		Matrix mat = new Matrix(dimension, dimension);
		Random rand = new Random();
		
		if (type < 0) //lower triangular matrix
		{
			for (int i = 0; i < dimension; i++)
			{
				for (int j = 0; j < dimension; j++)
				{
					if (i < j)
						mat.matrix[i][j] = new Double(0);
					else
						mat.matrix[i][j] = new Double(rand.nextDouble() * (max - min) + min);
				}
			}
		}
		else if (type > 0) //upper triangular matrix
		{
			for (int i = 0; i < dimension; i++)
			{
				for (int j = 0; j < dimension; j++)
				{
					if (i > j)
						mat.matrix[i][j] = new Double(0);
					else
						mat.matrix[i][j] = new Double(rand.nextDouble() * (max - min) + min);
				}
			}
		}
		else if (type == 0) //diagonal matrix
		{
			for (int i = 0; i < dimension; i++)
			{
				for (int j = 0; j < dimension; j++)
				{
					if (i != j)
						mat.matrix[i][j] = new Double(0);
					else
						mat.matrix[i][j] = new Double(rand.nextDouble() * (max - min) + min);
				}
			}
		}

		return mat;
	}

	
	/**
	 * Creates a deep copy of a matrix by copying each element of src into
	 * the same position in dest.
	 * Assumes both arrays are the same size.
	 * @param src
	 * @param dest
	 */
	public static void matrixCopy(Double[][] src, Double[][] dest)
	{
		for (int i = 0; i < src.length; i++)
		{
			System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
		}
	}
	
	/**
	 * Creates a simple string version of the matrix where
	 * each element has default precision.
	 */
	public String toString()
	{
		return toString(10);
	}
	
	/**
	 * Creates a string version of the matrix where
	 * each element is displayed with given precision.
	 * @param precision : number of digits to display for each element.
	 * @return
	 */
	public String toString(int precision)
	{
		String mat = "";
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				mat += String.format("% ." + precision + "f\t", matrix[i][j]);
			}
			mat += "\n";
		}
		return mat;
	}
	
	/**
	 * Interchanges two rows, that is if given row1 and row2, all the values of
	 * row1 are placed in row2 and vice versa.
	 * @param the first row to be interchanged
	 * @param the second row to be interchanged
	 * @return -1 if the two rows are interchanged or 1 if they are not, that is it returns 1 if
	 * row1 and row2 are the same.
	 */
	private int rowInterchange(Double[][] mat, int row1, int row2)
	{
		if (row1 == row2)
			return 1;
		Double[] temp = mat[row1];
		mat[row1] = mat[row2];
		mat[row2] = temp;
		return -1;
	}
	
	/**
	 * Scales a specific row of a matrix by a given amount and returns the value
	 * of the scaleFactor.
	 * @param rowNum the number of the row to scale, between 0 and n-1 for a matrix with n rows
	 * @param scaleFactor the factor by which the row is to be scaled
	 * @return the value by which the row was scaled.
	 */
	private double rowScale(Double[][] mat, int rowNum, double scaleFactor)
	{
		for (int i = 0; i < mat[0].length; i++)
		{
			mat[rowNum][i] = mat[rowNum][i] * scaleFactor + 0.0;
		}
		return scaleFactor;
	}

	/**
	 * Adds rows indexed by rowTo and rowFrom together, where rowFrom
	 * is scaled by scaleFactor, element by element and stores the result in rowTo.
	 * @param rowTo       : one row to be added, and destination.
	 * @param rowFrom     : one row to be scaled and added.
	 * @param scaleFactor : amount to scale rowFrom.
	 * @return            : 1 unless rowTo = rowFrom, in which case it returns scaleFactor + 1.
	 */
	private double rowAdd(Double[][] mat, int rowTo, int rowFrom, double scaleFactor)
	{
		if (rowTo == rowFrom)
			return rowScale(mat, rowTo, scaleFactor + 1);
		for (int i = 0; i < mat[0].length; i++)
		{
			mat[rowTo][i] = mat[rowTo][i] + mat[rowFrom][i] * scaleFactor + 0.0;
		}
		return 1.0;
	}
	
	/**
	 * Returns the index of the row you should swap with if there is a valid
	 * row to swap, that is if a row below the current row has a non-zero entry in column col.
	 * If there is on such row, returns -1.
	 * @param mat : the Double[][] to search for row for swapping
	 * @param row : the current row
	 * @param col : the current column
	 * @return
	 */
	private int determineRowSwap(Double[][] mat, int row, int col)
	{
		for (int i = row + 1; i < mat.length; i++)
		{
			if (mat[i][col] != 0)
				return i;
		}
		return -1;
	}

	/**
	 * Returns the reduced row echelon form of this matrix.
	 * @return
	 */
	public Matrix rref()
	{
		Double[][] mat = new Double[row][col];
		matrixCopy(matrix, mat);
		
		int j = 0;
		for (int i = 0; i < row && j < col; i++)
		{
			for (; j < col; j++)
			{
				if (mat[i][j] == 0) // pivot can't be 0
				{
					int swap = determineRowSwap(mat, i, j);
					if (swap == -1) // no pivot in this column
						continue;
					else
						rowInterchange(mat, i, swap); // swap with row with pivot in this column
				}
				rowScale(mat, i, 1 / mat[i][j]); // make pivot 1
				mat[i][j] = 1.0; // ensure it is one, regardless of rounding error
				for (int k = 0; k < row; k++) // eliminate all other values in this column
				{
					if (k == i)	// don't alter current row
						continue;
					rowAdd(mat, k, i, -mat[k][j]);
					mat[k][j] = 0.0; // Ensure it is 0, regardless of rounding error. 
				}
				break;
			}
			j++;
		}
		
		return new Matrix(mat);
	}
	
	/**
	 * Returns the inverse of this matrix if it has one, or null otherwise.
	 * @return
	 */
	public Matrix inverse()
	{
		if (row != col) // Can't invert a matrix that's not square.
			return null;
		
		Double[][] mat = new Double[row][col * 2];
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++) // copy in matrix to first half of mat;
			{
				mat[i][j] = matrix[i][j];
			}
			for (int j = 0; j < col; j++) // copy row*row identity matrix into second half of mat
			{
				mat[i][j + col] = i == j ? 1.0 : 0.0;
			}
		}
		
		Matrix rref = new Matrix(mat);
		rref = rref.rref();
		
		for (int i = 0; i < row; i++) // first half of matrix should now be identity if invertible
		{
			for (int j = 0; j < col; j++)
			{
				if (j == i && rref.matrix[i][j] != 1.0) //should have pivots along diagonal
					return null;
				else if (j != i && rref.matrix[i][j] != 0.0) //should be zero outside of diagonal
					return null;
			}
		}
		
		Double[][] ret = new Double[row][col];
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				ret[i][j] = rref.matrix[i][j + col];
			}
		}
		return new Matrix(ret);

	}
	
	/**
	 * Returns the determinant of a matrix. If the matrix
	 * is not square, returns null.
	 * @return
	 */
	public Double determinant()
	{
		if (row != col)
			return null;
		
		double det = 1;
		
		Double[][] mat = new Double[row][col];
		matrixCopy(matrix, mat);
		
		// The following code is a near copy of rref.
		// Some differences will include computing the determinant as we go, 
		// and only finding the row echelon form, not reduced row echelon form.
		for (int i = 0, j = 0; i < row && j < col; i++, j++)
		{
			if (mat[i][j] == 0) // pivot can't be 0
			{
				int swap = determineRowSwap(mat, i, j);
				if (swap == -1) // no pivot in this column
					return 0.0;
				else
					det *= rowInterchange(mat, i, swap); // swap with row with pivot in this column
			}
			for (int k = i + 1; k < row; k++) // eliminate all other values in this column below this row
			{
				rowAdd(mat, k, i, -mat[k][j] / mat[i][j]);
				mat[k][j] = 0.0;
			}
		}
		
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < i; j++)
			{
				if (mat[i][j] != 0.0) // not invertible
					return 0.0;
			}
		}
		
		for (int i = 0; i < row; i++)
			det *= mat[i][i];

		return det;
	}
	
	/**
	 * Determines the LUP decomposition of a matrix. 
	 * @return an array of Matrix objects where arr[0] = L and arr[1] = U in the determined LU decomposition.
	 * 	If a permutation is required for this matrix, it further includes arr[2] = P.
	 *		Returns null if matrix is either rectangular or singular. 	
	 */
	public Matrix[] LU()
	{
		if (col != row)
			return null;
		
		Double[][] L = new Double[row][row];
		Double[][] U = new Double[row][col];
		ArrayList<Integer[]> swaps = new ArrayList<Integer[]>();
		
		matrixCopy(matrix, U); //init U
		for (int i = 0; i < row; i++)
		{
			L[i][i] = 1.0;
			for (int j = i + 1; j < row; j++)
			{
				L[i][j] = 0.0;
			}
		}
		
		for (int i = 0, j = 0; i < row && j < col; i++, j++)
		{
			if (U[i][j] == 0) // pivot can't be 0
			{
				int swap = determineRowSwap(U, i, j);
				if (swap == -1) // no pivot in this column
				{
					return null;
				}
				else
				{
					Integer[] temp = {i, swap};
					swaps.add(temp);
					rowInterchange(U, i, swap); // swap with row with pivot in this column
				}
			}
			for (int k = i + 1; k < row; k++) // eliminate all other values in this column below this row
			{
				L[k][j] = U[k][j] / U[i][j];
				rowAdd(U, k, i, -U[k][j] / U[i][j]);
				U[k][j] = 0.0; // Ensure it is 0, regardless of rounding error.
			}
		}
		
		if (swaps.size() > 0)
		{
			Double[][] P = new Double[row][col];
			for (int i = 0; i < row; i++)
			{
				for (int j = 0; j < col; j++)
				{
					if (i != j)
						P[i][j] = 0.0;
					else
						P[i][j] = 1.0;
				}
			}
			for (int i = 0; i < swaps.size(); i++)
			{
				Integer[] temp = swaps.get(i);
				rowInterchange(P, temp[0], temp[1]);
			}
			Matrix[] LUP = new Matrix[3];
			LUP[0] = new Matrix(L);
			LUP[1] = new Matrix(U);
			LUP[2] = new Matrix(P);
			return LUP;
		}
		else
		{
			Matrix[] LU = new Matrix[2];
			LU[0] = new Matrix(L);
			LU[1] = new Matrix(U);
			return LU;
		}
	}

	
	/**
	 * Determines the LU decomposition of a matrix. 
	 * This is an extended LU decomposition in that it can determine a factorization for any matrix, including
	 * 	rectangular matrices, singular matrices, and any matrix that requires row swapping when determining its row echelon form.
	 * There are two extensions that allows this:
	 * 	1. L need not be lower triangular, it only need be a permutation of a lower triangular matrix.
	 * 	2. U may have 0 entries on the diagonal.
	 * Note also that L is a permutation of a *unitary* triangular matrix in this algorithm, so the
	 * 	decomposition is unique for any matrix that has at least m - 1 pivots, where m is the number of rows in
	 * 	matrix and the number of pivots is equal to the number of rows in the row echelon form that have non-zero elements.
	 * 	Notice also that U is a row echelon form of the matrix, so the number of pivots is merely the number of rows in U
	 * 	that have non-zero elements.
	 * 
	 * As a result, this algorithm guarantees the following:
	 * 	1. L will be a permutation of a lower triangular matrix with 1s along the main diagonal.
	 * 	2. U will be an upper triangular matrix where the elements on the main diagonal may or may not be 0.
	 * @return an array of Matrix objects where arr[0] = L and arr[1] = U in the determined LU decomposition.
	 */
	public Matrix[] LUextended()
	{
		Double[][] L = new Double[row][row];
		Double[][] U = new Double[row][col];
		ArrayList<Integer[]> swaps = new ArrayList<Integer[]>();
		
		matrixCopy(matrix, U); //init U
		for (int i = 0; i < row; i++)
		{
			L[i][i] = 1.0;
			for (int j = i + 1; j < row; j++)
			{
				L[i][j] = 0.0;
			}
		}
		
		for (int i = 0, j = 0; i < row && j < col; i++, j++)
		{
			if (U[i][j] == 0) // pivot can't be 0
			{
				int swap = determineRowSwap(U, i, j);
				if (swap == -1) // no pivot in this column
				{
					i--;
					continue; // Essentially just go to next column in same row and try agian.
				}
				else
				{
					Integer[] temp = {i, swap};
					swaps.add(temp);
					rowInterchange(U, i, swap); // swap with row with pivot in this column
				}
			}
			for (int k = i + 1; k < row; k++) // eliminate all other values in this column below this row
			{
				L[k][j] = U[k][j] / U[i][j];
				rowAdd(U, k, i, -U[k][j] / U[i][j]);
				U[k][j] = 0.0; // Ensure it is 0, regardless of rounding error.
			}
		}
		
		for (int i = 0; i < swaps.size(); i++)
		{
			Integer[] temp = swaps.get(i);
			rowInterchange(L, temp[0], temp[1]);
		}
		
		Matrix[] LU = new Matrix[2];
		LU[0] = new Matrix(L);
		LU[1] = new Matrix(U);
		return LU;
	}
	
	public static Matrix multiply(Matrix x, Matrix y)
	{
		if (x.col != y.row)
			return null;
		Double[][] mat = new Double[x.row][y.col];
		for (int i = 0; i < x.row; i++)
		{
			for (int j = 0;  j < y.col; j++)
			{
				Double sum = 0.0;
				for (int k = 0; k < x.col; k++)
				{
					sum += x.matrix[i][k] * y.matrix[k][j];
				}
				mat[i][j] = sum;
			}
		}
		return new Matrix(mat);
		
	}
	
}