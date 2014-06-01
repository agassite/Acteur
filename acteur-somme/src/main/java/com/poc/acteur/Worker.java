package com.poc.acteur;

import akka.actor.UntypedActor;

public class Worker extends UntypedActor {

	  @Override
	  public void onReceive(Object message) {
	    System.out.println("worker received an event");
	    if(message instanceof  Integer){
	      int number = (Integer) message;
	      int transformedNumber = tranform(number);
	      //send result to master
	      getSender().tell(new Result(transformedNumber), self());
	      //getSender().tell(null, null);
	    }
	  }

	  private int tranform(int number){
	    try {
	      Thread.sleep(4000);
	    } catch (InterruptedException e) {
	      e.printStackTrace();
	    }
	    return number *2;
	  }
	}
