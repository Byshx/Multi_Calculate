package Sort_Multithread;

import java.util.concurrent.CountDownLatch;

public class BubbleSort extends Sort{

	private CountDownLatch countDownLatch = null;
	private int[] array;
	private int loc1, loc2;
	private boolean running = true;

	public BubbleSort(CountDownLatch countDownLatch, int[] array, int loc1, int loc2) {
		// TODO Auto-generated constructor stub
		this.countDownLatch = countDownLatch;
		this.array = array;
		this.loc1 = loc1;
		this.loc2 = loc2;
	}

	public void sort() {
		for (int i = loc2 - 1; i >= loc1 && running; i--) {
			boolean b = false;
			for (int j = 0; j < i && running; j++) {
				if (array[j] > array[j + 1]) {
					swap(array, j, j + 1);
					b = true;
				}
			}
			if (!b)
				break;
		}
	}

	private void swap(int[] array, int i, int j) {
		int a = array[i];
		array[i] = array[j];
		array[j] = a;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sort();
		countDownLatch.countDown();
	}	
	
	public void Stop() {
		running = false;
	}

}
