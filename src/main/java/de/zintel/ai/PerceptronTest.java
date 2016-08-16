/**
 * 
 */
package de.zintel.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Friedemann
 *
 */
public class PerceptronTest {

	/**
	 * 
	 */
	public PerceptronTest() {
	}

	public static void main(String[] args) throws Exception {

		Double[] tp1 = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0,
				1.0, 1.0 };
		Double[] tp2 = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0 };
		Double[] tp3 = { 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0,
				1.0, 1.0 };
		Double[] tn1 = { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 1.0 };
		Double[] tn2 = { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
				0.0, 0.0 };
		Double[] tn3 = { 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0,
				0.0, 0.0 };
		Double[] tm1 = { 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0,
				1.0, 1.0 };
		Double[] tm2 = { 1.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0,
				0.0, 0.0 };
		Double[] tm3 = { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0 };
		Double[] tm4 = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0 };
		Double[] tm5 = { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 1.0 };
		Double[] tm6 = { 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 1.0 };
		List<Double[]> trainingpositiv = new ArrayList<>();
		trainingpositiv.add(tp1);
		trainingpositiv.add(tp2);
		trainingpositiv.add(tp3);
		List<Double[]> trainignegativ = new ArrayList<>();
		trainignegativ.add(tn1);
		trainignegativ.add(tn2);
		trainignegativ.add(tn3);

		Double[][] trainings = { tp1, tp2, tp2, tn1, tn2, tn3 };
		Double[][] tests = { tm1, tm2, tm3, tm4, tm5, tm6 };

		final Perceptron perceptron = new Perceptron(25, false);
		perceptron.init(trainingpositiv, trainignegativ);

		for (int i = 0; i < tests.length; i++) {
			System.out.println("trainingsmuster" + i + ": " + perceptron.classify(trainings[i]));
		}

		for (int i = 0; i < tests.length; i++) {
			System.out.println("testmuster" + i + ": " + perceptron.classify(tests[i]));
		}

		if (!perceptron.train(trainingpositiv, trainignegativ)) {
			System.out.println("could not train");
			return;
		}

		for (int i = 0; i < tests.length; i++) {
			System.out.println("testmuster" + i + ": " + perceptron.classify(tests[i]));
		}

	}

}
