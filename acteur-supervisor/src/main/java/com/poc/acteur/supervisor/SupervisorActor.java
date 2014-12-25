package com.poc.acteur.supervisor;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;

/**
 * Acteur superviseur (technique)
 * @author david
 *
 */
public class SupervisorActor extends UntypedActor {

	private final  ActorRef childActor;

	public SupervisorActor() {
		childActor = getContext().actorOf(Props.create(WorkerActor.class),
				"workerActor");
	}

	private static SupervisorStrategy strategy = new OneForOneStrategy(10,
			Duration.create("10 second"), new Function<Throwable, Directive>() {
				public Directive apply(Throwable t) {
					if (t instanceof ArithmeticException) {
						return resume();
					} else if (t instanceof NullPointerException) {
						return restart();
					}  else {
						return escalate();
					}
				}
			});

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

	public void onReceive(Object o) throws Exception {
		if (o instanceof ResultMessageRequest) {
			childActor.tell(o, getSender());
		} else
			childActor.tell(o,ActorRef.noSender());
	}

	public ActorRef getChildActor() {
		return childActor;
	}
	
	
}
