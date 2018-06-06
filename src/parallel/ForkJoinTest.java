package parallel;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * @author fly
 * Created on 2018/5/12.
 */
public class ForkJoinTest {

	static ForkJoinPool forkJoinPool = new ForkJoinPool();

	public static void main(String[] args) {
		System.out.println(forkJoinSum(1000_000));
	}

	public static long forkJoinSum(long n) {
		long[] numbers = LongStream.rangeClosed(1, n).toArray();
		ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
		return forkJoinPool.invoke(task);
	}

}
