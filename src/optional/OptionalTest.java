package optional;

import java.util.Optional;

import org.junit.Test;

/**
 * @author fly
 * Created on 2018/4/28.
 */
public class OptionalTest {

	@Test
	public void create() {
		Optional.empty();
		Optional.of(new Person());
		Optional.ofNullable(null);
	}

	@Test
	public void map() {
		Optional<Insurance> optionalInsurance = Optional.ofNullable(new Insurance());
		Optional<String> optional = optionalInsurance.map(Insurance::getName);
	}

	@Test
	public void flatMap() {
		Optional<Person> optPerson = Optional.of(new Person());
		Optional<String> name =
				optPerson.flatMap(Person::getCar)
						.flatMap(Car::getInsurance)
						.map(Insurance::getName);
	}

	@Test
	public void get() {
		Optional<Person> optionalPerson = Optional.of(new Person());
		Person person = optionalPerson.get();
		Person person1 = optionalPerson.orElseGet(Person::new);
	}

	public class Person {
		private Optional<Car> car;

		public Optional<Car> getCar() {
			return car;
		}
	}

	public class Car {
		private Optional<Insurance> insurance;

		public Optional<Insurance> getInsurance() {
			return insurance;
		}
	}

	public class Insurance {
		private String name;

		public String getName() {
			return name;
		}
	}
}
