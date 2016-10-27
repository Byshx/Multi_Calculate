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

	// �߳���ֹ����
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
			// ʹ�÷��䣬��������ƴ���ʵ�����а��Ļ�����ϰ��������¡�Sort_Multithread.������
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
		// �������Ĺ��������������·��䷽��������
		Class[] paramTypes = { CountDownLatch.class, int[].class, int.class, int.class };
		Object[] params = { countDownLatch, array, 0, 0 };

		// ʹ������װ�ز���
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

		// ��ʼ��ʱ
		long a = System.currentTimeMillis();

		// ���߳�����ʼ
		for (i = 0; i < thread && running; i++) {
			try {
				params[2] = start;
				params[3] = start + part;
				start += part;
				// �������ʵ��
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

		// �����������
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

		// ��������������ж�û��ǿ����ֹ������̵߳ȴ���ֱ�������̶߳��������

		if (running) {
			// �ȴ������߳�
			try {
				countDownLatch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ʹ�ù鲢����
			merging(array, part);

			long b = System.currentTimeMillis();
			time = b - a;

			// �����ݴ���
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

	// �鲢����
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

	// ���ó���״̬�����жϳ����������ʱ��Ҫ�ָ�����״̬��
	public void Recover() {
		// �ָ��̴߳���
		running = true;
	}

	// �ж������е��߳�
	public void Interrupt() {
		// ��ֹ�̴߳���
		running = false;

		// ���������е��߳���ֹ
		if (objects != null)
			for (int i = objects.length - 1; i >= 0; i--) {
				if (threads[i] != null)
					objects[i].Stop();
			}
	}

}
