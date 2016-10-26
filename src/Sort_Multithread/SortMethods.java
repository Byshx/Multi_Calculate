package Sort_Multithread;

public enum SortMethods {
	B("BubbleSort"), Q("QuickSort"), M("MergingSort");

	private String sign;

	private SortMethods(String sign) {
		this.sign = sign;
	}

	public String getName() {
		return sign;
	}
}
