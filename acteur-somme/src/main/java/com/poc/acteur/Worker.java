package com.poc.acteur;

import akka.actor.UntypedActor;

public class Worker extends UntypedActor {
	
	private static final int TIMER = 4000;

	@Override
	public void onReceive(Object message) {
		System.out.println("worker received an event");
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
