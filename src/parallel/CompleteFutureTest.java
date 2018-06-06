package parallel;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.junit.Test;

import static java.util.stream.Collectors.toList;

/**
 * @author fly
 * Created on 2018/4/28.
 */
public class CompleteFutureTest {
	Random random = new Random();

	public class Shop {

		private String name;

		public Shop(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String getPrice(String product) {
			double price = calculatePrice(product);
			Code code = Code.values()[random.nextInt(Code.values().length)];
			return String.format("%s:%.2f:%s", name, price, code);
		}

		private double calculatePrice(String product) {
			delay();
			return random.nextDouble() * product.charAt(0) + product.charAt(1);
		}

		public Future<Double> getPriceAsync(String product) {
			CompletableFuture<Double> futurePrice = new CompletableFuture<>();
			new Thread(() -> {
				try {
					double price = calculatePrice(product);
					futurePrice.complete(price);
				} catch (Exception e) {
					futurePrice.completeExceptionally(e);
				}
			}).start();
			return futurePrice;
		}

		public Future<Double> getPriceAsyncOfFactoryMethod(String product) {
			return CompletableFuture.supplyAsync(() -> calculatePrice(product));
		}
	}

	public void delay() {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testAsync() {
		Shop shop = new Shop("BestShop");
		long start = System.nanoTime();
		Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
		long invocationTime = ((System.nanoTime() - start) / 1_000_000);
		System.out.println("Invocation returned after " + invocationTime + " msecs");
		// 执行更多任务，比如查询其他商店
		doSomethingElse();
		// 在计算商品价格的同时
		try {
			double price = futurePrice.get();
			System.out.printf("Price is %.2f%n", price);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
		System.out.println("Price returned after " + retrievalTime + " msecs");
	}

	private void doSomethingElse() {
		System.out.println("I'm doing something else!!!");
	}

	List<Shop> shops = Arrays.asList(new Shop("BestPrice"),
			new Shop("LetsSaveBig"),
			new Shop("MyFavoriteShop"),
			new Shop("BuyItAll"));

	public List<String> findPrices(String product) {
		return shops.stream()
				.map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
				.collect(toList());
	}

	// join
	public List<String> findPricesAsync(String product) {
		List<CompletableFuture<String>> priceFutures = shops.stream()
				.map(
						shop -> CompletableFuture.supplyAsync(
								() -> shop.getName() + " price is " + shop.getPrice(product), executor
						)
				)
				.collect(toList());
		return priceFutures.stream()
				.map(CompletableFuture::join)
				.collect(toList());
	}

	private final Executor executor =
			Executors.newFixedThreadPool(Math.min(shops.size(), 100),
					r -> {
						Thread t = new Thread(r);
						t.setDaemon(true);
						return t;
					});

	public enum Code {
		NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

		private final int percentage;

		Code(int percentage) {
			this.percentage = percentage;
		}

		public int getPercentage() {
			return percentage;
		}
	}

	public String applyDiscount(Quote quote) {
		return quote.getShopName() + " price is " + apply(quote.getPrice(), quote.getDiscountCode());
	}

	private double apply(double price, Code code) {
		delay();
		return price * (100 - code.percentage) / 100;
	}

	public class Quote {

		private final String shopName;
		private final double price;
		private final Code discountCode;

		public Quote(String shopName, double price, Code code) {
			this.shopName = shopName;
			this.price = price;
			this.discountCode = code;
		}

		public String getShopName() {
			return shopName;
		}

		public double getPrice() {
			return price;
		}

		public Code getDiscountCode() {
			return discountCode;
		}
	}

	public Quote parse(String s) {
		String[] split = s.split(":");
		String shopName = split[0];
		double price = Double.parseDouble(split[1]);
		Code discountCode = Code.valueOf(split[2]);
		return new Quote(shopName, price, discountCode);
	}

	public List<String> findPricesOfFactoryMethod(String product) {
		List<CompletableFuture<String>> priceFutures = shops.stream()
				.map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
				.map(future -> future.thenApply(this::parse))
				// thenCompose方法允许你对两个异步操作进行流水线，第一个操作完成时，将其
				// 结果作为参数传递给第二个操作
				.map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> applyDiscount(quote), executor)))
				.collect(toList());

		return priceFutures.stream()
				.map(CompletableFuture::join)
				.collect(toList());
	}

	public void randomDelay() {
		int delay = 500 + random.nextInt(2000);
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public Stream<CompletableFuture<String>> findPricesStream(String product) {
		return shops.stream()
				.map(shop -> CompletableFuture.supplyAsync(
						() -> shop.getPrice(product), executor))
				.map(future -> future.thenApply(this::parse))
				.map(future -> future.thenCompose(quote ->
						CompletableFuture.supplyAsync(
								() -> applyDiscount(quote), executor)));
	}

	@Test
	public void thenAccept() {
		Stream<CompletableFuture<Void>> futureStream = findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println));
	}

	@Test
	public void allOf() {
		CompletableFuture[] futures = findPricesStream("myPhone")
				.map(f -> f.thenAccept(System.out::println))
				.toArray(size -> new CompletableFuture[size]);
		CompletableFuture.allOf(futures).join();
	}

	@Test
	public void test() {
		List<String> list = Arrays.asList("1");
		list.parallelStream().forEach(s -> System.out.println(Thread.currentThread().getName()));

	}

}
