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

public class Master extends UntypedActor {
	  private List<Integer> numbers;
	  private List<Integer> transformedNumbers;
	  private final ActorRef workerRouter;
	  long start = System.currentTimeMillis();
	  private ActorRef initialSender = null;

	  public Master(int nbWorkers, List<Integer> numbers) {
	    transformedNumbers = new ArrayList<Integer>();
	    this.numbers = numbers;
	    //create the worker dispatcher
	   // workerRouter = this.getContext().actorOf(new Props(Worker.class).withRouter(new RoundRobinRouter(nbWorkers)), "workerRouter");
	    workerRouter = this.getContext().actorOf(Props.create(Worker.class).withRouter(new RoundRobinRouter(nbWorkers)));
	    // workerRouter = this.getContext().actorOf(new Props(Worker.class).withRouter(new RoundRobinRouter(nbWorkers)), "workerRouter");
	    System.out.println("master and router created");
	  }
	  
	  @Override
	  public void onReceive(Object message) {
	    if(message instanceof com.poc.acteur.Start){
	    	initialSender = getSender();
	      System.out.println("master received a start event");
	      //dispatch
	      for (Integer number : numbers){
	        // send to a worker
	        workerRouter.tell(number, getSelf());
	      }
	    }
	    else if(message instanceof Result){
	      System.out.println("master received result event");
	      Result result = (Result) message;
	      //join
	      transformedNumbers.add(result.value);
	      if(transformedNumbers.size() == numbers.size()){
	        Duration duration = Duration.create(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
	        System.out.println(transformedNumbers.size() + " numbers computed in " + duration);
	        //stop master and its workers
	        initialSender.tell(transformedNumbers, getSelf());
	        getContext().stop(getSelf());
	        getContext().system().shutdown();
	      }
	    }
	  }
}

