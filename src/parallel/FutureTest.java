package parallel;

import java.util.concurrent.*;

/**
 * @author fly
 * Created on 2018/5/19.
 */
public class FutureTest {

	public void test() {
		ExecutorService executor = Executors.newCachedThreadPool();
		Future<Double> future = executor.submit(() -> doSomeLongComputation());
		doSomethingElse();
		try {
			System.out.println(future.get(1, TimeUnit.SECONDS));
		} catch (ExecutionException ee) {
			// 计算抛出一个异常
		} catch (InterruptedException ie) {
			// 当前线程在等待过程中被中断
		} catch (TimeoutException te) {
			// 在Future对象完成之前超过已过期
		}
	}

	private void doSomethingElse() {
		System.out.println("I'm doing something else!!!");
	}

	private Double doSomeLongComputation() {
		return 0.001 + 100000000;
	}
}
