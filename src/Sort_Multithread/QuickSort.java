package Sort_Multithread;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class QuickSort extends Sort implements Runnable {

	private CountDownLatch countDownLatch = null;
	private int[] array;
	private int loc1, loc2;
	private boolean running = true;

	public QuickSort(CountDownLatch countDownLatch, int[] array, int loc1, int loc2) {
		// TODO Auto-generated constructor stub
		this.countDownLatch = countDownLatch;
		this.array = array;
		this.loc1 = loc1;
		this.loc2 = loc2 - 1;
	}

	public void sort() {
		ArrayList<Node> arrayList = new ArrayList<Node>();
		arrayList.add(new Node(loc1, loc2));
		while (!arrayList.isEmpty() && running) {
			Node node = arrayList.remove(0);
			int l = node.loc1;
			int r = node.loc2;
			int left = l;
			int right = r;
			if (l < r) {
				int k = array[left];
				while (left < right && running) {
					while (left < right && array[right] >= k) {
						right--;
					}
					if (left < right) {
						array[left++] = array[right];
					}
					while (left < right && array[left] < k) {
						left++;
					}
					if (left < right) {
						array[right--] = array[left];
					}
				}
				array[left] = k;
				if (l < left - 1)
					arrayList.add(new Node(l, left - 1));
				if (left + 1 < r)
					arrayList.add(new Node(left + 1, r));
			}
		}
	}

	class Node {
		int loc1, loc2;

		public Node(int loc1, int loc2) {
			// TODO Auto-generated constructor stub
			this.loc1 = loc1;
			this.loc2 = loc2;
		}
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
