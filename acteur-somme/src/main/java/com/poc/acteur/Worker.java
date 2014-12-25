package com.poc.acteur;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Worker extends UntypedActor {
	
	private static final int TIMER = 2000;
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	@Override
	public void onReceive(Object message) {
		log.info("worker received an event " + getContext().toString());
		if (message instanceof Integer) {
			int number = (Integer) message;
			int transformedNumber = tranform(number);
			// send result to master
			getSender().tell(new ResultMessage(transformedNumber), self());
		}
	}

	private int tranform(int number) {
		try {
			Thread.sleep(TIMER);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return number * 2;
	}
}
