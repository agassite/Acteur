package com.poc.acteur;

import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;

public class FunctionListProcessor {
	
	
	/**Renvoie une liste dont les nombres sont mutiplies par 2
	 * 
	 * @param inputList Liste en entree dont on veut doubler les nombres
	 * @param nbWorker taille du pool de worker
	 * @return
	 */
	public List <Integer> doubleList (List <Integer> inputList, int nbWorker, ActorSystem system) throws Exception{
		//creation du master
    	ActorRef master = system.actorOf(Props.create(Master.class, nbWorker, inputList));
    	
		// Creation de la future, Acteur implicite auquel notre master pourra repondre
		Future<Object> future = Patterns
				.ask(master, new StartMessage(), 30000);
    	
		List <Integer> listeResultat = (List<Integer>) Await.result(future,
				Duration.create(30000, TimeUnit.MILLISECONDS));
		return listeResultat;
	}

}
