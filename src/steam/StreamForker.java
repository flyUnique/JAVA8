package steam;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.junit.Test;

import bean.Dish;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.reducing;

/**
 * @author fly
 */
public class StreamForker<T> {
	private final Stream<T> stream;
	private final Map<Object, Function<Stream<T>, ?>> forks = new HashMap<>();

	public StreamForker(Stream<T> stream) {
		this.stream = stream;
	}

	public StreamForker<T> fork(Object key, Function<Stream<T>, ?> f) {
		forks.put(key, f);
		return this;
	}

	public Results getResults() {
		// To be implemented
		ForkingStreamConsumer<T> consumer = build();
		try {
			stream.sequential().forEach(consumer);
		} finally {
			consumer.finish();
		}
		return consumer;
	}

	private ForkingStreamConsumer<T> build() {
		List<BlockingQueue<T>> queues = new ArrayList<>();
		Map<Object, Future<?>> actions =
				forks.entrySet().stream().reduce(
						new HashMap<>(),
						(map, e) -> {
							map.put(e.getKey(), getOperationResult(queues, e.getValue()));
							return map;
						}, (m1, m2) -> {
							m1.putAll(m2);
							return m1;
						});
		return new ForkingStreamConsumer<>(queues, actions);
	}

	private Future<?> getOperationResult(List<BlockingQueue<T>> queues,
										 Function<Stream<T>, ?> f) {
		BlockingQueue<T> queue = new LinkedBlockingQueue<>();
		queues.add(queue);
		Spliterator<T> spliterator = new BlockingQueueSpliterator<>(queue);
		Stream<T> source = StreamSupport.stream(spliterator, false);
		return CompletableFuture.supplyAsync(() -> f.apply(source));
	}

	public interface Results {
		<R> R get(Object key);
	}

	static class ForkingStreamConsumer<T> implements Consumer<T>, Results {
		static final Object END_OF_STREAM = new Object();

		private final List<BlockingQueue<T>> queues;
		private final Map<Object, Future<?>> actions;

		ForkingStreamConsumer(List<BlockingQueue<T>> queues,
							  Map<Object, Future<?>> actions) {
			this.queues = queues;
			this.actions = actions;
		}

		@Override
		public void accept(T t) {
			queues.forEach(q -> q.add(t));
		}

		void finish() {
			accept((T) END_OF_STREAM);
		}

		@Override
		public <R> R get(Object key) {
			try {
				return ((Future<R>) actions.get(key)).get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	class BlockingQueueSpliterator<T> implements Spliterator<T> {
		private final BlockingQueue<T> q;

		BlockingQueueSpliterator(BlockingQueue<T> q) {
			this.q = q;
		}

		@Override
		public boolean tryAdvance(Consumer<? super T> action) {
			T t;
			while (true) {
				try {
					t = q.take();
					break;
				} catch (InterruptedException e) {
				}
			}

			if (t != ForkingStreamConsumer.END_OF_STREAM) {
				action.accept(t);
				return true;
			}

			return false;
		}

		@Override
		public Spliterator<T> trySplit() {
			return null;
		}

		@Override
		public long estimateSize() {
			return 0;
		}

		@Override
		public int characteristics() {
			return 0;
		}
	}

	private List<Dish> menu = Arrays.asList(
			new Dish("pork", false, 800, Dish.Type.MEAT),
			new Dish("beef", false, 700, Dish.Type.MEAT),
			new Dish("chicken", false, 400, Dish.Type.MEAT),
			new Dish("french fries", true, 530, Dish.Type.OTHER),
			new Dish("rice", true, 350, Dish.Type.OTHER),
			new Dish("season fruit", true, 120, Dish.Type.OTHER),
			new Dish("pizza", true, 550, Dish.Type.OTHER),
			new Dish("prawns", false, 300, Dish.Type.FISH),
			new Dish("salmon", false, 450, Dish.Type.FISH)
	);

	@Test
	public void test() {
		Stream<Dish> menuStream = menu.stream();
		StreamForker.Results results = new StreamForker<>(menuStream)
				.fork("shortMenu", s -> s.map(Dish::getName).collect(joining(", ")))
				.fork("totalCalories", s -> s.mapToInt(Dish::getCalories).sum())
				.fork("mostCaloricDish", s -> s.collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2)).get())
				.fork("dishesByType", s -> s.collect(groupingBy(Dish::getType)))
				.getResults();
		String shortMenu = results.get("shortMenu");
		int totalCalories = results.get("totalCalories");
		Dish mostCaloricDish = results.get("mostCaloricDish");
		Map<Dish.Type, List<Dish>> dishesByType = results.get("dishesByType");

		System.out.println("Short menu: " + shortMenu);
		System.out.println("Total calories: " + totalCalories);
		System.out.println("Most caloric dish: " + mostCaloricDish);
		System.out.println("Dishes by type: " + dishesByType);
	}
}