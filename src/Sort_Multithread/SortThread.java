package Sort_Multithread;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;
import MultiCalculate.GetXYData;

public class SortThread {

	private int size = 0, thread = 0;
	private String method;
	private long time = 0;
	private InitArray initArray = null;
	private GetXYData getXYData = null;
	private Thread[] threads = null;
	private Sort[] objects = null;

	// 线程终止变量
	private boolean running = true;

	public SortThread(GetXYData getXYData) {
		// TODO Auto-generated constructor stub
		this.getXYData = getXYData;
	}

	public void setConfig(int size, int thread, String method) {
		initArray = new InitArray(size);
		this.size = size;
		this.thread = thread;
		this.method = method;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() {
		CountDownLatch countDownLatch = null;
		int[] array = initArray.getArray();
		int part = size / thread;
		int remainder = size % thread;
		Class c = null;
		try {
			// 使用反射，用类的名称创建实例，有包的话需带上包名，如下“Sort_Multithread.类名”
			c = Class.forName("Sort_Multithread." + method);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (remainder == 0) {
			objects = (Sort[]) Array.newInstance(c, size);
			countDownLatch = new CountDownLatch(thread);
			threads = new Thread[size];
		} else {

			objects = (Sort[]) Array.newInstance(c, size + 1);
			countDownLatch = new CountDownLatch(thread);
			threads = new Thread[size + 1];
		}
		// 含参数的构造器，需用以下反射方法来调用
		Class[] paramTypes = { CountDownLatch.class, int[].class, int.class, int.class };
		Object[] params = { countDownLatch, array, 0, 0 };

		// 使用容器装载参数
		Constructor constructor = null;
		try {
			constructor = c.getConstructor(paramTypes);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int i = 0;
		int start = 0;

		// 开始计时
		long a = System.currentTimeMillis();

		// 多线程排序开始
		for (i = 0; i < thread && running; i++) {
			try {
				params[2] = start;
				params[3] = start + part;
				start += part;
				// 产生多个实例
				objects[i] = (Sort) constructor.newInstance(params);
				threads[i] = new Thread(objects[i]);
				threads[i].start();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 处理多余数段
		if (remainder != 0 && running) {
			try {
				params[2] = start;
				params[3] = start + remainder;
				objects[i] = (Sort) constructor.newInstance(params);
				threads[i] = new Thread(objects[i]);
				threads[i].start();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// 如果程序正常运行而没有强行终止则进行线程等待，直至所有线程都完成任务

		if (running) {
			// 等待其他线程
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 使用归并排序
			merging(array, part);

			long b = System.currentTimeMillis();
			time = b - a;

			// 将数据传出
			getXYData.getData(thread, time);
		}
	}

	private static void merging(int[] number, int k) {
		while (k < number.length) {
			int i = 0;
			for (; i < number.length - k; i += k) {
				Merge(number, i, i + k / 2, i + k);
			}
			if (i - k + 1 != number.length) {
				Merge(number, i - k, (i - k + number.length - 1) / 2, number.length - 1);
			}
			k *= 2;
		}
	}

	// 归并方法
	private static void Merge(int[] number, int i, int mid, int j) {
		int[] tmp = new int[j - i + 1];
		int tmpi = i, tmpj = mid + 1;
		int count = 0;
		while (tmpi <= mid && tmpj <= j) {
			if (number[tmpi] > number[tmpj]) {
				tmp[count++] = number[tmpj++];
			} else {
				tmp[count++] = number[tmpi++];
			}
		}

		while (tmpi <= mid) {
			tmp[count++] = number[tmpi++];
		}

		while (tmpj <= j) {
			tmp[count++] = number[tmpj++];
		}

		count = 0;

		for (; i <= j; i++) {
			number[i] = tmp[count++];
		}
	}

	// 重置程序状态（在中断程序后再运行时需要恢复程序状态）
	public void Recover() {
		// 恢复线程创建
		running = true;
	}

	// 中断运行中的线程
	public void Interrupt() {
		// 终止线程创建
		running = false;

		// 将正在运行的线程终止
		if (objects != null)
			for (int i = objects.length - 1; i >= 0; i--) {
				if (threads[i] != null)
					objects[i].Stop();
			}
	}

}
