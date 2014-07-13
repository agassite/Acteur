package com.poc.acteur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.Option;
import scala.collection.script.Start;
import scala.concurrent.duration.Duration;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import akka.routing.Router;
import akka.routing.RouterActor;
import akka.routing.RouterConfig;
import akka.routing.RoutingLogic;

/**
 * 
 * @author david
 * 
 */
public class Master extends UntypedActor {

	/**
	 * La liste de nombres en entree
	 */
	private List<Integer> numbers;
	
	/**
	 * La liste de nombres transformés
	 */
	private List<Integer> transformedNumbers;
	
	private final ActorRef workerRouter;
	
	long start = System.currentTimeMillis();
	
	/**
	 * Reference vers la future pour lui renvoyer le resultat
	 */
	private ActorRef initialSender = null;

	public Master(int nbWorkers, List<Integer> numbers) {
		transformedNumbers = new ArrayList<Integer>();
		this.numbers = numbers;
		
		// create the worker dispatcher
		workerRouter = this.getContext().actorOf(
				Props.create(Worker.class).withRouter(
						new RoundRobinRouter(nbWorkers)));
		
		
		System.out.println("master and router created");
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof com.poc.acteur.StartMessage) {
			initialSender = getSender();
			System.out.println("master received a start event");
			// dispatch
			for (Integer number : numbers) {
				// send to a worker
				workerRouter.tell(number, getSelf());
			}
		} else if (message instanceof ResultMessage) {
			System.out.println("master received result event");
			ResultMessage result = (ResultMessage) message;
			// join
			transformedNumbers.add(result.value);
			
			//on detecte quand on a fini le traitement
			if (transformedNumbers.size() == numbers.size()) {
				
				//calcule la duree du traitement
				Duration duration = Duration.create(System.currentTimeMillis()
						- start, TimeUnit.MILLISECONDS);
				System.out.println(transformedNumbers.size()
						+ " numbers computed in " + duration);
				
				//on renvoie le resultat a la future
				initialSender.tell(transformedNumbers, getSelf());
				
				// stop master and its workers
				getContext().stop(getSelf());
				getContext().system().shutdown();
			}
		}
	}
}
