package matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MultiMatrix {

	private static ExecutorService threadPool;
	private Double[][] matrix;
	private int row;
	private int col;
	private List<Future> futures;
	private int numThreads;

	public MultiMatrix(Double[][] mat, int numThreads)
	{
		threadPool = Executors.newFixedThreadPool(numThreads, new DaemonThreadFactory());
//		threadPool = Executors.newCachedThreadPool(new DaemonThreadFactory());
		row = mat.length;
		col = mat[0].length;
		matrix = new Double[row][col];
		matrixCopy(mat, matrix);
		futures = new ArrayList<Future>(row);
		this.numThreads = numThreads;
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
	 * Creates a deep copy of a matrix by copying each element of src into
	 * the same position in dest.
	 * Assumes both arrays are the same size.
	 * @param src
	 * @param dest
	 */
	public static void matrixCopy(Double[][] src, Double[][] dest)
	{
		List<Future> futures = new ArrayList<Future>(src.length);
		for (int i = 0; i < src.length; i++)
		{
			futures.add(threadPool.submit(new CopyThread(src[i], dest[i])));
		}
		try {
			for (Future f : futures)
				f.get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
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
		List<Future<String>> rowStrings = new ArrayList<Future<String>>(row);

		for (int i = 0; i < row; i++)
		{
			rowStrings.add(i, threadPool.submit(new StringThread(i, precision)));
		}
		try {
			for (int i = 0; i < row; i++)
			{
				mat += rowStrings.get(i).get();
			}
		} catch (InterruptedException e) {
			System.err.println("Error: Thread Interrupted.");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception.");
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
	private double rowScale(Double[] row, double scaleFactor)
	{
		for (int i = 0; i < row.length; i++)
		{
			//			mat[rowNum][i] = mat[rowNum][i] * scaleFactor + 0.0;
			futures.add(threadPool.submit(new ScaleThread(row, i, scaleFactor)));
		}
		try {
			for (Future f : futures)
				f.get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
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
		List<Future> futures = new ArrayList<Future>(col);
		if (rowTo == rowFrom)
			return rowScale(mat[rowTo], scaleFactor + 1) + 0.0;
		for (int i = 0; i < mat[0].length; i++)
		{
			//			mat[rowTo][i] = mat[rowTo][i] + mat[rowFrom][i] * scaleFactor + 0.0;
			futures.add(threadPool.submit(new AddThread(mat, rowTo, rowFrom, i, scaleFactor)));
		}
		try {
			for (Future f : futures)
				f.get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
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
	public MultiMatrix rref()
	{
		Double[][] mat = this.clone();

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
				rowScale(mat[i], 1 / mat[i][j]); // make pivot 1
				mat[i][j] = 1.0; // ensure it is one, regardless of rounding error
				for (int k = 0; k < row; k++) // eliminate all other values in this column
				{
					if (k == i)	// don't alter current row
						continue;
					futures.add(threadPool.submit(new RowAddThread(mat, k, i, j, -mat[k][j])));
					//					rowAdd(mat, k, i, -mat[k][j]);
					//					mat[k][j] = 0.0; // Ensure it is 0, regardless of rounding error. 
				}
				try {
					for (Future f : futures)
						f.get();
				} catch (InterruptedException e) {
					System.err.println("Error: Interrupted Exception");
				} catch (ExecutionException e) {
					System.err.println("Error: Execution Exception");
				}
				break;
			}
			j++;
		}

		return new MultiMatrix(mat, numThreads);
	}
	
	/**
	 * Returns the inverse of this matrix if it has one, or null otherwise.
	 * @return
	 */
	public MultiMatrix inverse()
	{
		if (row != col) // Can't invert a matrix that's not square.
			return null;
		
		Double[][] mat = new Double[row][col * 2];
		for (int i = 0; i < row; i++)
		{
			futures.add(threadPool.submit(new CopyThread(matrix[i], mat[i])));
			futures.add(threadPool.submit(new IdentityCopyThread(mat[i], i)));
		}
		try {
			for (Future f : futures)
				f.get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
		}
		
		MultiMatrix rref = new MultiMatrix(mat, numThreads);
		rref = rref.rref();
		
		List<Future<Boolean>> invertible = new ArrayList<Future<Boolean>>(row);
		for (int i = 0; i < row; i++) // first half of matrix should now be identity if invertible
		{
			invertible.add(threadPool.submit(new InvertibleThread(rref.matrix[i], i)));
		}
		try {
			for (Future<Boolean> f : invertible)
				if(f.get() == false)
					return null;
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
		}

		Double[][] ret = new Double[row][col];
		for (int i = 0; i < row; i++)
		{
			futures.add(threadPool.submit(new CopyInverseThread(rref.matrix[i], ret[i])));
		}
		try {
			for (Future f : futures)
				f.get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
		}

		return new MultiMatrix(ret, numThreads);
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
				futures.add(threadPool.submit(new RowAddThread(mat, k, i, j, -mat[k][j] / mat[i][j])));
//				det *= rowAdd(mat, k, i, -mat[k][j] / mat[i][j]);
			}
			try {
				for (Future f : futures)
					f.get();
			} catch (InterruptedException e) {
				System.err.println("Error: Interrupted Exception");
			} catch (ExecutionException e) {
				System.err.println("Error: Execution Exception");
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
	public MultiMatrix[] LU()
	{
		if (col != row)
			return null;
		
		Double[][] L = new Double[row][row];
		Double[][] U = new Double[row][col];
		ArrayList<Integer[]> swaps = new ArrayList<Integer[]>();
		
		for (int i = 0; i < row; i++)
		{
			futures.add(threadPool.submit(new CopyThread(matrix[i], U[i])));
			futures.add(threadPool.submit(new LowerCopyThread(L[i], i)));
		}
		
		try {
			for (Future f : futures)
				f.get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
		}

		for (int i = 0; i < row; i++)
		{
			if (U[i][i] == 0) // pivot can't be 0
			{
				int swap = determineRowSwap(U, i, i);
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
				futures.add(threadPool.submit(new LUAddThread(L, U, k, i)));
			}
			try {
				for (Future f : futures)
					f.get();
			} catch (InterruptedException e) {
				System.err.println("Error: Interrupted Exception");
			} catch (ExecutionException e) {
				System.err.println("Error: Execution Exception");
			}
		}
		
		MultiMatrix[] LUP;
		List<Future<MultiMatrix>> lup;
		if (swaps.size() > 0)
		{
			Double[][] P = new Double[row][col];
			for (int i = 0; i < row; i++)
			{
				futures.add(threadPool.submit(new LowerCopyThread(P[i], i)));
			}
			try {
				for (Future f : futures)
					f.get();
			} catch (InterruptedException e) {
				System.err.println("Error: Interrupted Exception");
			} catch (ExecutionException e) {
				System.err.println("Error: Execution Exception");
			}

			for (int i = 0; i < swaps.size(); i++)
			{
				Integer[] temp = swaps.get(i);
				rowInterchange(P, temp[0], temp[1]);
			}
			LUP = new MultiMatrix[3];
			lup = new ArrayList<Future<MultiMatrix>>(3);
			lup.add(threadPool.submit(new NewMatrixThread(L)));
			lup.add(threadPool.submit(new NewMatrixThread(U)));
			lup.add(threadPool.submit(new NewMatrixThread(P)));
		}
		else
		{
			LUP = new MultiMatrix[2];
			lup = new ArrayList<Future<MultiMatrix>>(2);
			lup.add(threadPool.submit(new NewMatrixThread(L)));
			lup.add(threadPool.submit(new NewMatrixThread(U)));
		}
		try {
			for (int i = 0; i < LUP.length; i++)
				LUP[i] = lup.get(i).get();
		} catch (InterruptedException e) {
			System.err.println("Error: Interrupted Exception");
		} catch (ExecutionException e) {
			System.err.println("Error: Execution Exception");
		}
		
		return LUP;

	}

	private class NewMatrixThread implements Callable<MultiMatrix>
	{
		Double[][] mat;
		
		public NewMatrixThread(Double[][] mat)
		{
			this.mat = mat;
		}
		
		public MultiMatrix call()
		{
			return new MultiMatrix(mat, numThreads);
		}
	}
	
	private class LUAddThread implements Runnable
	{
		Double L[][];
		Double U[][];
		int rowTo;
		int rowFrom;
		
		public LUAddThread(Double[][] L, Double[][] U, int rowTo, int rowFrom)
		{
			this.L = L;
			this.U = U;
			this.rowTo = rowTo;
			this.rowFrom = rowFrom;
		}
		
		public void run()
		{
			L[rowTo][rowFrom] = U[rowTo][rowFrom] / U[rowFrom][rowFrom];
			rowAdd(U, rowTo, rowFrom, -U[rowTo][rowFrom] / U[rowFrom][rowFrom]);
			U[rowTo][rowFrom] = 0.0; // Ensure it is 0, regardless of rounding error.
		}
		
	}
	
	private class LowerCopyThread implements Runnable
	{
		Double[] dest;
		int rowNum;
		
		public LowerCopyThread(Double[] dest, int rowNum)
		{
			this.dest = dest;
			this.rowNum = rowNum;
		}
		
		public void run()
		{
			for (int j = 0; j < dest.length; j++)
			{
				dest[j] = rowNum == j ? 1.0 : 0.0;
			}
		}
	}

	private class CopyInverseThread implements Runnable
	{
		Double[] dest;
		Double[] src;

		public CopyInverseThread(Double[] src, Double[] dest)
		{
			this.dest = dest;
			this.src = src;
		}

		public void run()
		{
			System.arraycopy(src, dest.length, dest, 0, dest.length);
		}
	}

	private class InvertibleThread implements Callable<Boolean>
	{
		Double[] row;
		int numRow;
		int length;
		
		public InvertibleThread(Double[] row, int numRow)
		{
			this.row = row;
			this.numRow = numRow;
			length = row.length / 2;
		}
		
		public Boolean call()
		{
			for (int j = 0; j < length; j++)
			{
				if ((j != numRow && row[j] != 0.0) || (j == numRow && row[j] != 1.0))
					return false;
			}
			return true;
		}
	}
	
	private class RowAddThread implements Runnable
	{
		Double[][] mat;
		int rowTo;
		int rowFrom;
		int col;
		double scaleFactor;

		public RowAddThread(Double[][] mat, int rowTo, int rowFrom, int col, double scaleFactor)
		{
			this.mat = mat;
			this.rowTo = rowTo;
			this.rowFrom = rowFrom;
			this.col = col;
			this.scaleFactor = scaleFactor;
		}

		public void run()
		{
			rowAdd(mat, rowTo, rowFrom, scaleFactor);
			mat[rowTo][col] = 0.0;
		}

	}

	private class AddThread implements Runnable
	{
		Double[][] mat;
		int rowTo;
		int rowFrom;
		int colNum;
		double scaleFactor;

		public AddThread(Double[][] mat, int rowTo, int rowFrom, int colNum, double scaleFactor)
		{
			this.mat = mat;
			this.rowTo = rowTo;
			this.rowFrom = rowFrom;
			this.scaleFactor = scaleFactor;
			this.colNum = colNum;
		}

		public void run()
		{
			mat[rowTo][colNum] = mat[rowTo][colNum] + mat[rowFrom][colNum] * scaleFactor + 0.0;
		}
	}

	private class ScaleThread implements Runnable
	{
		Double[] row;
		int colNum;
		double scaleFactor;

		ScaleThread(Double[] row, int colNum, double scaleFactor)
		{
			this.row = row;
			this.colNum = colNum;
			this.scaleFactor = scaleFactor;
		}

		public void run()
		{
			row[colNum] = row[colNum] * scaleFactor + 0.0;
		}
	}

	private class IdentityCopyThread implements Runnable
	{
		Double[] dest;
		int rowNum;
		int length;
		
		public IdentityCopyThread(Double[] dest, int rowNum)
		{
			this.dest = dest;
			this.rowNum = rowNum;
			length = dest.length / 2;
		}
		
		public void run()
		{
			for (int j = 0; j < length; j++) // copy row*row identity matrix into second half of mat
			{
				dest[j + length] = rowNum == j ? 1.0 : 0.0;
			}
		}
	}
	
	private static class CopyThread implements Runnable
	{
		Double[] dest;
		Double[] src;

		public CopyThread(Double[] src, Double[] dest)
		{
			this.dest = dest;
			this.src = src;
		}

		public void run()
		{
			System.arraycopy(src, 0, dest, 0, src.length);
		}
	}

	private class StringThread implements Callable<String>
	{
		int rowNum;
		int precision;

		StringThread(int rowNum, int precision)
		{
			this.rowNum = rowNum;
			this.precision = precision;
		}

		public String call()
		{
			String rowString = "";
			for (int i = 0; i < col; i++)
			{
				rowString += String.format("% ." + precision + "f\t", matrix[rowNum][i]);
			}
			rowString += "\n";
			return rowString;
		}
	}

	public boolean equalsMat(Matrix mat)
	{
		if (this == null && mat == null)
			return true;
		if (this == null || mat == null)
			return false;
		
		if (this.row != mat.rows() || this.col != mat.cols())
			return false;
		for (int i = 0; i < row; i++)
		{
			for (int j = 0; j < col; j++)
			{
				if (!matrix[i][j].equals(mat.get(i, j)))
					return false;
			}
		}
		return true;
	}
}