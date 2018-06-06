package api;

import java.util.*;
import java.util.stream.IntStream;

import org.junit.Test;

/**
 * @author fly
 * Created on 2018/4/28.
 */
public class NewAPITest {

	@Test
	public void map() {
		Map<String, String> map = new HashMap();
//		map.put("hello", null);
		System.out.println(map);
		System.out.println(map.getOrDefault("hello", "helloDefault"));
		map.forEach((key, value) -> {
			// do iterator
			System.out.println(key + " " + value);
		});
		System.out.println(map);
		map.compute("hello", (o, o2) -> o + o2);
		System.out.println(map);
		map.computeIfAbsent("hello", s -> "hello" + s);
		System.out.println(map);
		map.computeIfPresent("hello", (s, s2) -> s + s2);
		System.out.println(map);
		map.computeIfAbsent("world", s -> "hello " + s);
		map.putIfAbsent("hi", "hi");
		map.merge("world", "world", (s, s2) -> s + s2);
		System.out.println(map);
		map.remove("hello", "hello");
		System.out.println(map);
		map.replace("hello", "hello");
		map.replace("hello", "hello1", "hello1");
		map.replaceAll((s, s2) -> s + s2);
		System.out.println(map);
	}

	@Test
	public void list() {
		List<String> list = new ArrayList<>();
//		IntStream.range(1, 10).forEach(System.out::println);
//		IntStream.rangeClosed(1, 10).forEach(System.out::println);
		IntStream.rangeClosed(1, 100).forEach(value -> list.add(String.valueOf(value)));
		System.out.println(list);
		list.replaceAll(s -> "hello" + s);
		System.out.println(list);
		list.sort(Comparator.reverseOrder());
		System.out.println(list);
		list.removeIf(String::isEmpty);
	}

}
