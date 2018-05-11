package steam;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

import bean.Dish;
import static java.util.stream.Collectors.toList;

/**
 * @author fly
 * Created on 2017/3/2.
 */
public class SteamTest {

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

	int b = 1;

	@Test
	public void filter() {
		List<Dish> dishList = menu.stream()
//				.filter(Dish::isVegetarian)
				.filter((Dish dish) -> dish.isVegetarian())
				.collect(toList());
		int a = 1;
		List l = new ArrayList();
		menu.stream()
				.parallel()
				.sequential()
				.parallel()
//				.filter(Dish::isVegetarian)
				.filter((Dish dish) -> {
					b++;
					System.out.println(a);
					l.add(1);

					return dish.isVegetarian();
				})
				.filter(dish -> dish.isVegetarian())
				.forEach(dish -> l.add(1));
//		a =2;
		menu.parallelStream().sequential();
	}

	@Test
	public void distinct() {
		List<Dish> dishList = menu.stream()
				.filter(Dish::isVegetarian)
				.distinct()
				.collect(toList());
	}

	@Test
	public void limit() {
		List<Dish> dishes = menu.stream()
				.filter(d -> d.getCalories() > 300)
				.limit(3)
				.collect(toList());
	}

	@Test
	public void skip() {
		List<Dish> dishes = menu.stream()
				.filter(d -> d.getCalories() > 300)
				.skip(2)
				.collect(toList());
	}

	@Test
	public void map() {
		List<String> dishNames = menu.stream()
				.map(dish -> dish.getName())
//				.map(Dish::getName)
				.collect(toList());

		menu.stream()
				.map(dish -> dish.getType()).forEach(System.out::println);
	}

	public void flatMap() {
		String words = "hello world";
		List<String> uniqueCharacters =
				Arrays.stream(words.split(" "))
						.map(w -> w.split(""))
						.flatMap(Arrays::stream)
						.distinct()
						.collect(Collectors.toList());

		Stream<Stream<String>> streamStream = Arrays.stream(words.split(" "))
				.map(w -> w.split("")).map(Arrays::stream);

		Stream<String> stringStream = Arrays.stream(words.split(" "))
				.map(w -> w.split("")).flatMap(Arrays::stream);

		List<Integer> numbers1 = Arrays.asList(1, 2, 3);
		List<Integer> numbers2 = Arrays.asList(3, 4);
		List<int[]> pairs =
				numbers1.stream()
						.flatMap(i -> numbers2.stream()
								.map(j -> new int[]{i, j})
						)
						.collect(toList());
	}

	@Test
	public void match() {
		//短路操作
		boolean b = menu.stream().anyMatch(Dish::isVegetarian);

		menu.stream().allMatch(d -> d.getCalories() < 1000);

		menu.stream().noneMatch(d -> d.getCalories() >= 1000);
	}

	@Test
	public void find() {
		Optional<Dish> dish = menu.stream()
				.filter(Dish::isVegetarian)
				.findAny();
		dish.isPresent();
		Dish dish1 = dish.get();

		List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
		Optional<Integer> firstSquareDivisibleByThree =
				someNumbers.stream()
						.map(x -> x * x)
						.filter(x -> x % 3 == 0)
						.findFirst();
	}

	@Test
	public void reduce() {
		// []
		int sum = IntStream.rangeClosed(1, 10).reduce(0, Integer::sum);
		int max = IntStream.rangeClosed(1, 10).reduce(0, Integer::max);
		int min = IntStream.rangeClosed(1, 10).reduce(0, Integer::min);

		OptionalInt optionalSum = IntStream.rangeClosed(1, 10).reduce(Integer::sum);
	}

	@Test
	public void sort() {
		menu.stream().sorted(Comparator.comparing(Dish::getCalories));
	}

	@Test
	public void primative() {
		IntStream intStream = menu.stream()
				.mapToInt(Dish::getCalories);
		Stream<Integer> stream = menu.stream()
				.mapToInt(Dish::getCalories)
				.boxed();

		OptionalInt maxCalories = menu.stream()
				.mapToInt(Dish::getCalories)
				.max();
	}

	@Test
	public void steam() {
		Stream<String> stream = Stream.of("Java 8 ", "Lambdas ", "In ", "Action");

		Stream<String> emptyStream = Stream.empty();

		int[] numbers = {2, 3, 5, 7, 11, 13};
		int sum = Arrays.stream(numbers).sum();

		long uniqueWords = 0;
		try (Stream<String> lines =
					 Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
			uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
					.distinct()
					.count();
		} catch (IOException e) {

		}
	}

	@Test
	public void iterate() {
		Stream.iterate(0, n -> n + 2)
				.limit(10)
				.forEach(System.out::println);

		//斐波纳契元组
		Stream.iterate(new int[]{0, 1},
				t -> new int[]{t[1], t[0]+t[1]})
				.limit(20)
				.forEach(t -> System.out.println("(" + t[0] + "," + t[1] +")"));

		Stream.iterate(new int[]{0, 1},
				t -> new int[]{t[1],t[0] + t[1]})
				.limit(10)
				.map(t -> t[0])
				.forEach(System.out::println);
	}

	@Test
	public void generate() {
		Stream.generate(Math::random)
				.limit(5)
				.forEach(System.out::println);
	}

}
