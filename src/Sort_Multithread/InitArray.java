package Sort_Multithread;

import java.util.Random;

public class InitArray {
	private int[] array = null;

	public InitArray(int size) {
		// TODO Auto-generated constructor stub
		array = new int[size];
		InitArray();
	}

	public void InitArray() {
		Random random = new Random();
		for (int i = 0; i < array.length; i++) {
			array[i] = random.nextInt(Integer.MAX_VALUE);
		}
	}

	public int[] getArray() {
		return array;
	}
}
