package Sort_Multithread;

import java.util.concurrent.CountDownLatch;

public class MergingSort extends Sort { // πÈ≤¢≈≈–Ú

	private CountDownLatch countDownLatch = null;
	private int[] array;
	private int loc1, loc2;
	private boolean running = true;

	public MergingSort(CountDownLatch countDownLatch, int[] array, int loc1, int loc2) {
		// TODO Auto-generated constructor stub
		this.countDownLatch = countDownLatch;
		this.array = array;
		this.loc1 = loc1;
		this.loc2 = loc2 - 1;
	}

	// µ›πÈ∑®
	public void sort(int left, int right) {
		if (left == right)
			return;
		int mid = (left + right) / 2;
		sort(left, mid); // ◊Û≈≈–Ú
		sort(mid + 1, right); // ”“≈≈–Ú
		Merge(left, mid, right); // πÈ≤¢
	}

	// ∑«µ›πÈ
	public void sort2(int loc1, int loc2) {
		int length = loc2 - loc1 + 1;
		int k = 1;
		while (k < length && running) {
			int i = 0;
			for (; i < length - k && running == true; i += k) {
				Merge(i, i + k / 2, i + k);
			}
			if (i - k + 1 != length) {
				Merge(i - k, (i - k + length - 1) / 2, length - 1);
			}
			k *= 2;
		}
	}

	private void Merge(int i, int mid, int j) {
		int[] tmp = new int[j - i + 1];
		int tmpi = i, tmpj = mid + 1;
		int count = 0;
		while (tmpi <= mid && tmpj <= j) {
			if (array[tmpi] > array[tmpj]) {
				tmp[count++] = array[tmpj++];
			} else {
				tmp[count++] = array[tmpi++];
			}
		}

		while (tmpi <= mid) {
			tmp[count++] = array[tmpi++];
		}

		while (tmpj <= j) {
			tmp[count++] = array[tmpj++];
		}

		count = 0;

		for (; i <= j; i++) {
			array[i] = tmp[count++];
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		sort2(loc1, loc2);		
		countDownLatch.countDown();
	}

	public void Stop() {
		running = false;
	}
}
