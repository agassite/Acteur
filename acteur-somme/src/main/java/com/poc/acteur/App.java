package com.poc.acteur;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.util.Timeout;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public static final int POOL_WORKER = 1;
    public static void main( String[] args ) throws Exception
    {
    	List<Integer> numbers = Arrays.asList(1, 5, 15, 22, 66, 55);  
    	ActorSystem system = ActorSystem.create("mySystem");  
    	ActorRef master = system.actorOf(Props.create(Master.class,POOL_WORKER,numbers));
    	com.poc.acteur.Start startMessage = new com.poc.acteur.Start(); 
    	//master.tell(startMessage, master);
    	
    	Timeout timeout = new Timeout(Duration.create("30 seconds").toMillis());
    	Future future = akka.pattern.Patterns.ask(master,startMessage, timeout);
    	List<Integer> result = (List<Integer>) Await.result(future, timeout.duration());
    	System.out.println("resultat= " + result);
    	system.shutdown();  
        /*Future<String> f1 = future(new Callable<String>() {  
          public String call() throws Exception {  
            Thread.sleep(4000);  
            return "You String is ready !!";  
          }  
        }, system.dispatcher());  
        Timeout timeout = new Timeout(Duration.parse("30 seconds"));  
        String result = Await.result(f1, timeout.duration());  
        System.out.println(result);*/
    	/*final int nbWorkers = 6;  
    	List<Integer> numbers = Arrays.asList(1, 5, 15, 22, 66, 55);  
    	ActorSystem system = ActorSystem.create("mySystem");  
    	ActorRef master = system.actorOf(new Props(new UntypedActorFactory() {  
    	  public UntypedActor create() {  
    	    return new Master(nbWorkers, numbers);  
    	  }  
    	}), "master");  
    	Start startMessage = new Start();  
    	master.tell(startMessage);*/
    }
}
