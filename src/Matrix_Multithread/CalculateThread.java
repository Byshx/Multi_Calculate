package Matrix_Multithread;

import java.util.concurrent.CountDownLatch;

import MultiCalculate.GetXYData;

//也可实现接口，推荐使用接口
public class CalculateThread extends Thread {

	private static int dim = 0, thread = 0;
	private static long time = 0;
	private static Matrix_Calculate matrix_Calculate = new Matrix_Calculate();
	private static GetXYData getXYData = null;
	private static CalculateThread[] threads = null;
	// 计算范围的首尾值
	private int loc1, loc2;

	// 线程生成中止变量
	private static boolean running = true;

	// 线程计数器
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
		// 每一部分的大小
		int part = dim / thread;
		// 不能整除则将多余部分另建一个线程
		int remainder = dim % thread;
		CountDownLatch countDownLatch = null;
		// 判断是否可以整除
		if (remainder == 0) {
			threads = new CalculateThread[thread];
			countDownLatch = new CountDownLatch(thread);
		} else {
			// 不能整除，线程+1
			threads = new CalculateThread[thread + 1];
			countDownLatch = new CountDownLatch(thread + 1);
		}
		// 开始变量
		int start = 0;
		int i;
		long a = System.currentTimeMillis();
		for (i = 0; i < thread && running; i++) {
			threads[i] = new CalculateThread(start, start + part, countDownLatch);
			threads[i].start();
			start += part;
		}
		if (remainder != 0 && running) {
			threads[i] = new CalculateThread(start, start + remainder, countDownLatch);
			threads[i].start();
		}

		// 线程如果在运行则可以继续下去

		if (running) {
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long b = System.currentTimeMillis();
			time = b - a;
			// 将数据传给GetXYData类用来更新折线图
			getXYData.getData(thread, time);
		}
	}

	public static void Interrupt() {
		// 线程生成中止
		running = false;
		// 已生成线程计算中止
		matrix_Calculate.Interrupt();
	}

	public static void Recover() {
		// 线程恢复为可生成
		running = true;
		// 数组可创建、可计算
		matrix_Calculate.Recover();
	}

}
