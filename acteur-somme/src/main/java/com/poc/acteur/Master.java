package com.poc.acteur;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.routing.RoundRobinPool;
import akka.routing.Router;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author david
 * 
 */
public class Master extends UntypedActor {

	final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	/**
	 * La liste de nombres en entree
	 */
	private final List<Integer> inputNumbers;
	
	/**
	 * La liste de nombres transform�s
	 */
	private final List<Integer> transformedNumbers;

	/**
	 * Debut du traitement
	 */
	private  long start = System.currentTimeMillis();

	ActorRef router = null;
	

	
	/**
	 * Reference vers la future pour lui renvoyer le resultat
	 */
	private ActorRef initialSender = null;

	public Master(int nbWorkers, List<Integer> numbers) {
		transformedNumbers = new ArrayList<Integer>();
		this.inputNumbers = numbers;
		router = createRouter(nbWorkers);
		log.info("master and router created with " + nbWorkers + " workers");
		start = System.currentTimeMillis();
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof com.poc.acteur.StartMessage) {
			initialSender = getSender();

			// le routeur dispatche les differents elements de la liste au workers
			for (Integer number : inputNumbers) {
				router.tell(number, getSelf());

			}
			
			//reception du resultat du traitement
		} else if (message instanceof ResultMessage) {
			ResultMessage result = (ResultMessage) message;
			transformedNumbers.add(result.getValue());
			
			//on detecte quand on a fini le traitement
			if (transformedNumbers.size() == inputNumbers.size()) {
				
				//calcule la duree du traitement
				Duration duration = Duration.create(System.currentTimeMillis()
						- start, TimeUnit.MILLISECONDS);
				log.info(transformedNumbers.size()
						+ " numbers computed in " + duration);
				
				//on renvoie le resultat a la future
				initialSender.tell(transformedNumbers, getSelf());
			}
		}
	}
	
	/**
	 * Cree un routeur contenant le pool de workers
	 * @param nbWorkers
	 * @return
	 */
	private ActorRef createRouter (int nbWorkers) {
		return
				getContext().actorOf(new RoundRobinPool(nbWorkers).props(Props.create(Worker.class)),
						"router2");

	}
}
