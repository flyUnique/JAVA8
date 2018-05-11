package collectors;

import java.util.*;

import org.junit.Test;

import bean.Dish;
import static java.util.stream.Collectors.*;

/**
 * @author fly
 * Created on 2018/1/12.
 */
public class CollectorsTest {

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
	public void count() {
		menu.stream().collect(counting());

		menu.stream().count();
	}

	@Test
	public void compare() {
		Comparator<Dish> dishCaloriesComparator = Comparator.comparingInt(Dish::getCalories);

		Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));

	}

	@Test
	public void sum() {
		menu.stream().collect(summingInt(Dish::getCalories));
		menu.stream().collect(averagingDouble(Dish::getCalories));

		IntSummaryStatistics summaryStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
	}

	@Test
	public void join() {
		menu.stream().map(Dish::getName).collect(joining());

		menu.stream().map(Dish::getName).collect(joining(", "));
		String collect = Arrays.asList("1,2,3,4,5,6".split(",")).stream().collect(joining("::"));
		System.out.println(collect);
//		menu.stream().collect(joining());
	}

	@Test
	public void reduce() {
		menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));

		Optional<Dish> mostCalorieDish = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
	}

	public enum CaloricLevel {DIET, NORMAL, FAT}

	@Test
	public void group() {
		Map<Dish.Type, List<Dish>> dishesByType =
				menu.stream().collect(groupingBy(Dish::getType));

		Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(
				groupingBy(dish -> {
					if (dish.getCalories() <= 400) {
						return CaloricLevel.DIET;
					} else if (dish.getCalories() <= 700) {
						return CaloricLevel.NORMAL;
					} else {
						return CaloricLevel.FAT;
					}
				}));


		Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
				menu.stream().collect(
						groupingBy(Dish::getType,
								groupingBy(dish -> {
									if (dish.getCalories() <= 400) {
										return CaloricLevel.DIET;
									} else if (dish.getCalories() <= 700) {
										return CaloricLevel.NORMAL;
									} else {
										return CaloricLevel.FAT;
									}
								})
						)
				);

		Map<Dish.Type, Long> typesCount = menu.stream().collect(
				groupingBy(Dish::getType, counting()));

		menu.stream()
				.collect(groupingBy(Dish::getType,
						maxBy(Comparator.comparingInt(Dish::getCalories))));

		menu.stream()
				.collect(groupingBy(Dish::getType,
						collectingAndThen(
								maxBy(Comparator.comparingInt(Dish::getCalories)),
								Optional::get)));

		Map<Dish.Type, Integer> totalCaloriesByType =
				menu.stream().collect(groupingBy(Dish::getType,
						summingInt(Dish::getCalories)));

		menu.stream().collect(
				groupingBy(Dish::getType, mapping(
						dish -> {
							if (dish.getCalories() <= 400) {
								return CaloricLevel.DIET;
							} else if (dish.getCalories() <= 700) {
								return CaloricLevel.NORMAL;
							} else {
								return CaloricLevel.FAT;
							}
						},
						toSet())));

	}

	@Test
	public void partition() {
		Map<Boolean, List<Dish>> partitionedMenu =
				menu.stream().collect(partitioningBy(Dish::isVegetarian));
	}

	@Test
	public void test() {
		Long a = null;
		System.out.println(0L != a);
	}
}
