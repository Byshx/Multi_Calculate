package Matrix_Multithread;

import java.util.Random;

import MultiCalculate.Thread_ChangeState;

public class Matrix_Calculate implements Thread_ChangeState {

	private int[][] matrix = null;

	// 数组生成终止变量
	private boolean running = true;

	public void InitMatrix(int dimension) {
		// 生成数组
		matrix = new int[dimension][dimension];
		Random random = new Random();

		for (int i = 0; i < matrix.length && running; i++) {
			for (int j = 0; j < matrix[0].length && running; j++) {
				matrix[i][j] = random.nextInt(10000);
			}
		}
	}

	@SuppressWarnings("unused")
	public void Calculate(int loc1, int loc2) {
		for (int i = loc1; i < loc2 && running; i++) {
			int count = 0;
			for (int j = 0; j < matrix.length && running; j++) {
				for (int k = 0; k < matrix.length && running; k++) {
					count += matrix[i][k] * matrix[k][j];
				}
				// 赋值
				// ....
			}
		}
	}

	@Override
	public void Interrupt() {
		// TODO Auto-generated method stub
		// 数组生成和计算中止
		running = false;
	}

	@Override
	public void Recover() {
		// TODO Auto-generated method stub

		// 由于该类在CalculateThread类里静态创建，因此需要将数组的计算和生成设为TRUE
		running = true;
	}

}
