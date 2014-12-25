package com.poc.acteur;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;

/**
 * Programme principal
 *
 */
public class App 
{
	
	//taille du pool des acteurs de type worker 
	public static final int POOL_WORKER = 8;
    public static void main( String[] args ) throws Exception
    {
    	//liste d'entier a traiter
    	List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8);  
    	ActorSystem system = ActorSystem.create("mySystem");  
    	LoggingAdapter log = Logging.getLogger(system, system);
    	
    	//creation du master
    	ActorRef master = system.actorOf(Props.create(Master.class,POOL_WORKER,numbers));
    	
		// Creation de la future, Acteur implicite auquel notre master pourra repondre
		Future<Object> future = Patterns
				.ask(master, new StartMessage(), 30000);
    	
		List <Integer> listeResultat = (List<Integer>) Await.result(future,
				Duration.create(30000, TimeUnit.MILLISECONDS));
    	log.info("resultat= " + listeResultat);
		system.shutdown();
    	
    }
}
