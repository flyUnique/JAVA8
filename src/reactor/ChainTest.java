package reactor;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import org.junit.Test;

/**
 * @author fly
 * Created on 2018/5/14.
 */
public class ChainTest {
	public abstract class ProcessingObject<T> {

		protected ProcessingObject<T> successor;

		public void setSuccessor(ProcessingObject<T> successor) {
			this.successor = successor;
		}

		public T handle(T input) {
			T r = handleWork(input);
			if (successor != null) {
				return successor.handle(r);
			}
			return r;
		}

		abstract protected T handleWork(T input);
	}

	public class HeaderTextProcessing extends ProcessingObject<String> {
		public String handleWork(String text) {
			return "From Raoul, Mario and Alan: " + text;
		}
	}

	public class SpellCheckerProcessing extends ProcessingObject<String> {
		public String handleWork(String text) {
			return text.replaceAll("labda", "lambda");
		}
	}

	@Test
	public void test() {
		ProcessingObject<String> p1 = new HeaderTextProcessing();
		ProcessingObject<String> p2 = new SpellCheckerProcessing();

		p1.setSuccessor(p2);

		String result = p1.handle("Aren't labdas really sexy?!!");
		System.out.println(result);
	}

	@Test
	public void lambdaTest() {
		UnaryOperator<String> headerProcessing = (String text) -> "From Raoul, Mario and Alan: " + text;

		UnaryOperator<String> spellCheckerProcessing = (String text) -> text.replaceAll("labda", "lambda");

		Function<String, String> pipeline = headerProcessing.andThen(spellCheckerProcessing);

		String result = pipeline.apply("Aren't labdas really sexy?!!");
	}
}
