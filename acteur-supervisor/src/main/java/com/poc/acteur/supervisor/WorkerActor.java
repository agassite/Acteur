package com.poc.acteur.supervisor;



import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Acteur implementant la logique metier
 * @author david
 *
 */
public class WorkerActor extends UntypedActor {
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private int state = 4;

	@Override
	public void preStart() {
		log.info("Starting WorkerActor instance hashcode # {}", this.hashCode());
	}

	public void onReceive(Object o) throws Exception {
		  if (o instanceof IntegerMessage) {
			IntegerMessage message = (IntegerMessage) o;
			//recupere l'entier à traiter
			int value = message.getNombre().intValue(); 
			state = state/value;
		} else if (o instanceof ResultMessageRequest) {
			getSender().tell(state, ActorRef.noSender());
		} 
	}

	@Override
	public void postStop() {
		log.info("Stopping WorkerActor instance hashcode # {}", this.hashCode());
		//getContext().system().shutdown();

	}
}
