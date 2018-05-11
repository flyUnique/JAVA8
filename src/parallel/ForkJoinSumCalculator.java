package parallel;

import java.util.concurrent.RecursiveTask;

/**
 * Created by fly on 2017/9/30.
 */
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

	private final long[] numbers;
	private final int start;
	private final int end;

	public static final long THRESHOLD = 10_000L;

	public ForkJoinSumCalculator(long[] numbers) {
		this(numbers, 0, numbers.length);
	}

	private ForkJoinSumCalculator(long[] numbers, int start, int end) {
		this.numbers = numbers;
		this.start = start;
		this.end = end;
	}

	@Override
	protected Long compute() {
		int length = end - start;
		if (length <= THRESHOLD) {
			return computeSequentially();
		}
		ForkJoinSumCalculator left = new ForkJoinSumCalculator(numbers, start, start + length / 2);
		left.fork();
		ForkJoinSumCalculator right = new ForkJoinSumCalculator(numbers, start + length / 2, end);
		Long rightResult = right.compute();
		Long leftResult = left.join();
		return rightResult + leftResult;
	}

	private long computeSequentially() {
		long sum = 0;
		for (int i = start; i < end; i++) {
			sum += numbers[i];
		}
		return sum;
	}
}
