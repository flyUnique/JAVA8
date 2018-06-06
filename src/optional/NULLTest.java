package optional;

import org.junit.Test;

/**
 * @author fly
 * Created on 2018/5/18.
 */
public class NULLTest {

	public class Person {
		private Car car;

		public Car getCar() {
			return car;
		}
	}

	public class Car {
		private Insurance insurance;

		public Insurance getInsurance() {
			return insurance;
		}
	}

	public class Insurance {
		private String name;

		public String getName() {
			return name;
		}
	}

	public String getCarInsuranceName(Person person) {
		return person.getCar().getInsurance().getName();
	}

	public String getNOTNULLCarInsuranceName(Person person) {
		if (person != null) {
			Car car = person.getCar();
			if (car != null) {
				Insurance insurance = car.getInsurance();
				if (insurance != null) {
					return insurance.getName();
				}
			}
		}
		return "Unknown";
	}

	public String getSimpleNOTNULLCarInsuranceName(Person person) {
		if (person == null) {
			return "Unknown";
		}
		Car car = person.getCar();
		if (car == null) {
			return "Unknown";
		}
		Insurance insurance = car.getInsurance();
		if (insurance == null) {
			return "Unknown";
		}
		return insurance.getName();
	}

	@Test
	public void nullTest() {
		getCarInsuranceName(new Person());
	}

	@Test
	public void notNULLTest() {
		getNOTNULLCarInsuranceName(new Person());
		getSimpleNOTNULLCarInsuranceName(new Person());
	}
}
