package com.poc.acteur;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() throws Exception {

		final ActorSystem system = ActorSystem.create("mySystem");
		final LoggingAdapter log = Logging.getLogger(system, system);
		final Config config = ConfigFactory.load();

		// taille du pool de worker
		final int poolWorkerSize = config.getInt("poolWorkerSize");

		// liste d'entier a traiter
		final List<Integer> numbers = config.getIntList("inputList");

		final FunctionListProcessor processor = new FunctionListProcessor();
		final List<Integer> listeResultat = processor.doubleList(numbers,
				poolWorkerSize, system);
		log.info("resultat= " + listeResultat);
		system.shutdown();

	}
}
