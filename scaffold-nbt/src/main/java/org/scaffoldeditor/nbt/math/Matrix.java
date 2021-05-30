package org.scaffoldeditor.nbt.math;

/**
 * A bare-bones immutable data type for M-by-N matrices.
 * <br>
 * <a href="https://introcs.cs.princeton.edu/java/95linear/Matrix.java.html">
 * I totally didn't steal this from the internet.</a>
 */
public class Matrix {
	
	/**
	 * Contains standard rotational matrices where NORTH contains no rotation.
	 */
	public static class Direction {
		public static final Matrix NORTH = Matrix.identity(3);
		public static final Matrix WEST = new Matrix(new double[][] {
			{ 0, 0, 1 },
			{ 0, 1, 0 },
			{ -1, 0, 0 }
		});
		public static final Matrix SOUTH = new Matrix(new double[][] {
			{ -1, 0, 0 },
			{ 0, 1, 0 },
			{ 0, 0, -1 }
		});
		public static final Matrix EAST = new Matrix(new double[][] {
			{ 0, 0, -1 },
			{ 0, 1, 0 },
			{ 1, 0, 0 }
		});
	}
	
	private final int M; // number of rows
	private final int N; // number of columns
	private final double[][] data; // M-by-N array

	/**
	 * Create an M-by-N matrix of 0's.
	 */
	public Matrix(int M, int N) {
		this.M = M;
		this.N = N;
		data = new double[M][N];
	}

	/**
	 * Create a matrix based on a 2D array.
	 */
	public Matrix(double[][] data) {
		M = data.length;
		N = data[0].length;
		this.data = new double[M][N];
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				this.data[i][j] = data[i][j];
	}

	// copy constructor
	private Matrix(Matrix A) {
		this(A.data);
	}

	/**
	 * Create and return the N-by-N identity matrix.
	 */
	public static Matrix identity(int N) {
		Matrix I = new Matrix(N, N);
		for (int i = 0; i < N; i++)
			I.data[i][i] = 1;
		return I;
	}

	// swap rows i and j
	private void swap(int i, int j) {
		double[] temp = data[i];
		data[i] = data[j];
		data[j] = temp;
	}

	/**
	 * Create and return the transpose of the invoking matrix.
	 */
	public Matrix transpose() {
		Matrix A = new Matrix(N, M);
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				A.data[j][i] = this.data[i][j];
		return A;
	}

	public Matrix plus(Matrix B) {
		Matrix A = this;
		if (B.M != A.M || B.N != A.N)
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		Matrix C = new Matrix(M, N);
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				C.data[i][j] = A.data[i][j] + B.data[i][j];
		return C;
	}

	public Matrix minus(Matrix B) {
		Matrix A = this;
		if (B.M != A.M || B.N != A.N)
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		Matrix C = new Matrix(M, N);
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				C.data[i][j] = A.data[i][j] - B.data[i][j];
		return C;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Matrix)) return false;
		Matrix B = (Matrix) other;
		Matrix A = this;
		if (B.M != A.M || B.N != A.N)
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				if (A.data[i][j] != B.data[i][j])
					return false;
		return true;
	}

	public Matrix times(Matrix B) {
		Matrix A = this;
		if (A.N != B.M)
			throw new IllegalArgumentException("Illegal matrix dimensions.");
		Matrix C = new Matrix(A.M, B.N);
		for (int i = 0; i < C.M; i++)
			for (int j = 0; j < C.N; j++)
				for (int k = 0; k < A.N; k++)
					C.data[i][j] += (A.data[i][k] * B.data[k][j]);
		return C;
	}
	
	public double[][] getData() {
		return data.clone();
	}

	/**
	 * Return x = A^-1 b, assuming A is square and has full rank.
	 */
	public Matrix solve(Matrix rhs) {
		if (M != N || rhs.M != N || rhs.N != 1)
			throw new IllegalArgumentException("Illegal matrix dimensions.");

		// create copies of the data
		Matrix A = new Matrix(this);
		Matrix b = new Matrix(rhs);

		// Gaussian elimination with partial pivoting
		for (int i = 0; i < N; i++) {

			// find pivot row and swap
			int max = i;
			for (int j = i + 1; j < N; j++)
				if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i]))
					max = j;
			A.swap(i, max);
			b.swap(i, max);

			// singular
			if (A.data[i][i] == 0.0)
				throw new IllegalArgumentException("Matrix is singular.");

			// pivot within b
			for (int j = i + 1; j < N; j++)
				b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];

			// pivot within A
			for (int j = i + 1; j < N; j++) {
				double m = A.data[j][i] / A.data[i][i];
				for (int k = i + 1; k < N; k++) {
					A.data[j][k] -= A.data[i][k] * m;
				}
				A.data[j][i] = 0.0;
			}
		}

		// back substitution
		Matrix x = new Matrix(N, 1);
		for (int j = N - 1; j >= 0; j--) {
			double t = 0.0;
			for (int k = j + 1; k < N; k++)
				t += A.data[j][k] * x.data[k][0];
			x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
		}
		return x;

	}
	
	public Vector3d toVector() {
		if (M != 3 || N != 1) {
			throw new IllegalStateException("Only 3x1 matrices can be converted to a 3d vector!");
		}
		return new Vector3d(data[0][0], data[1][0], data[2][0]);
	}
	
	public static Matrix fromVector(Vector3d in) {
		return new Matrix(new double[][] {{in.x}, {in.y}, {in.z}});
	}
	
	public static Matrix fromVector(Vector3f in) {
		return new Matrix(new double[][] {{in.x}, {in.y}, {in.z}});
	}
}