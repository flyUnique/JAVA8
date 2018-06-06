package reactor;

import org.junit.Test;

/**
 * @author fly
 * Created on 2018/5/14.
 */
public class StrategyTest {
	public interface ValidationStrategy {
		boolean execute(String s);
	}

	public class IsAllLowerCase implements ValidationStrategy {
		@Override
		public boolean execute(String s) {
			return s.matches("[a-z]+");
		}
	}

	public class IsNumeric implements ValidationStrategy {
		@Override
		public boolean execute(String s) {
			return s.matches("\\d+");
		}
	}

	public class Validator {
		private final ValidationStrategy strategy;

		public Validator(ValidationStrategy v) {
			this.strategy = v;
		}

		public boolean validate(String s) {
			return strategy.execute(s);
		}
	}

	@Test
	public void test() {
		Validator numericValidator = new Validator(new IsNumeric());
		boolean b1 = numericValidator.validate("aaaa");
		Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
		boolean b2 = lowerCaseValidator.validate("bbbb");
	}

	@Test
	public void lambdaTest() {
		Validator numericValidator = new Validator((String s) -> s.matches("[a-z]+"));
		boolean b1 = numericValidator.validate("aaaa");
		Validator lowerCaseValidator = new Validator((String s) -> s.matches("\\d+"));
		boolean b2 = lowerCaseValidator.validate("bbbb");
	}
}
