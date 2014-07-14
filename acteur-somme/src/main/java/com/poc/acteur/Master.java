package com.poc.acteur;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;

/**
 * 
 * @author david
 * 
 */
public class Master extends UntypedActor {

	/**
	 * La liste de nombres en entree
	 */
	private final List<Integer> inputNumbers;
	
	/**
	 * La liste de nombres transformés
	 */
	private final List<Integer> transformedNumbers;
	
	private final Router router;
	
	private final long start = System.currentTimeMillis();
	
	/**
	 * Reference vers la future pour lui renvoyer le resultat
	 */
	private ActorRef initialSender = null;

	public Master(int nbWorkers, List<Integer> numbers) {
		transformedNumbers = new ArrayList<Integer>();
		this.inputNumbers = numbers;
		router = createRouter(nbWorkers);
		System.out.println("master and router created");
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof com.poc.acteur.StartMessage) {
			initialSender = getSender();
			System.out.println("master received a start event");
			for (Integer number : inputNumbers) {
				// le routeur dispatche les differents elements de la liste au workers
				router.route(number, getSelf());
			}
			
			//reception du resultat du traitement
		} else if (message instanceof ResultMessage) {
			System.out.println("master received result event");
			ResultMessage result = (ResultMessage) message;
			transformedNumbers.add(result.getValue());
			
			//on detecte quand on a fini le traitement
			if (transformedNumbers.size() == inputNumbers.size()) {
				
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
	
	/**
	 * Cree un routeur contenant le pool de workers
	 * @param nbWorkers
	 * @return
	 */
	private Router createRouter (int nbWorkers) {
		Router router = null;
		final List<Routee> routees = new ArrayList<Routee>();
		 for (int i = 0; i < nbWorkers; i++) {
			 final ActorRef workerRouter = getContext().actorOf(Props.create(Worker.class));
			 getContext().watch(workerRouter);
			 routees.add(new ActorRefRoutee(workerRouter));
		 }
		 router = new Router(new RoundRobinRoutingLogic(), routees);
		 return router;
	}
}
