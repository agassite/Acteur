package com.poc.acteur;

import java.util.List;

import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Programme principal
 *
 */
public class App 
{
	
    public static void main( String[] args ) throws Exception
    {
    	  
    	final ActorSystem system = ActorSystem.create("mySystem");  
    	final LoggingAdapter log = Logging.getLogger(system, system);
    	final Config config = ConfigFactory.load();
    	
    	//taille du pool de worker
    	final int poolWorkerSize = config.getInt("poolWorkerSize");
    	
    	//liste d'entier a traiter
    	final List<Integer> numbers = config.getIntList("inputList");
    	
    	
    	final FunctionListProcessor processor = new FunctionListProcessor();
    	final List <Integer> listeResultat = processor.doubleList(numbers, poolWorkerSize, system);
    	log.info("resultat= " + listeResultat);
		system.shutdown();
    	
    }
}
