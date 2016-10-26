package Matrix_Multithread;

import java.util.Random;

public class Matrix_Calculate {

	private int[][] matrix = null;

	public void InitMatrix(int dimension) {
		// 生成数组
		matrix = new int[dimension][dimension];
		Random random = new Random();

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				matrix[i][j] = random.nextInt(10000);
			}
		}
	}

	@SuppressWarnings("unused")
	public void Calculate(int loc1, int loc2) {
		for (int i = loc1; i < loc2; i++) {
			int count = 0;
			for (int j = 0; j < matrix.length; j++) {
				for (int k = 0; k < matrix.length; k++) {
					count += matrix[i][k] * matrix[k][j];
				}
				// 赋值
				// ....
			}
		}
	}

}
