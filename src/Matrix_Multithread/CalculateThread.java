package Matrix_Multithread;

import java.util.concurrent.CountDownLatch;

import MultiCalculate.GetXYData;

public class CalculateThread extends Thread {

	private static int dim = 0, thread = 0;
	private static long time = 0;
	private static Matrix_Calculate matrix_Calculate = new Matrix_Calculate();
	private static GetXYData getXYData = null;
	private int loc1, loc2;
	private CountDownLatch countDownLatch = null;

	public CalculateThread(int loc1, int loc2, CountDownLatch countDownLatch) {
		// TODO Auto-generated constructor stub
		this.loc1 = loc1;
		this.loc2 = loc2;
		this.countDownLatch = countDownLatch;
	}

	public CalculateThread(GetXYData getXYData) {
		// TODO Auto-generated constructor stub
		CalculateThread.getXYData = getXYData;
	}

	public void run() {
		matrix_Calculate.Calculate(loc1, loc2);
		countDownLatch.countDown();
	}

	public static void setConfig(int dim, int thread) {
		matrix_Calculate.InitMatrix(dim);
		CalculateThread.dim = dim;
		CalculateThread.thread = thread;
	}

	public static void execute() {
		int part = dim / thread;
		int remainder = dim % thread;
		CalculateThread[] threads = null;
		CountDownLatch countDownLatch = null;
		if (remainder == 0) {
			threads = new CalculateThread[thread];
			countDownLatch = new CountDownLatch(thread);
		} else {
			threads = new CalculateThread[thread + 1];
			countDownLatch = new CountDownLatch(thread + 1);
		}
		int start = 0;
		int i;
		long a = System.currentTimeMillis();
		for (i = 0; i < thread; i++) {
			threads[i] = new CalculateThread(start, start + part, countDownLatch);
			threads[i].start();
			start += part;
		}
		if (remainder != 0) {
			threads[i] = new CalculateThread(start, start + remainder, countDownLatch);
			threads[i].start();
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		time = b - a;
		getXYData.getData(thread, time);
	}

}
