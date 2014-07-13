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
    	List<Integer> numbers = Arrays.asList(1, 5, 15, 22, 66, 55);  
    	ActorSystem system = ActorSystem.create("mySystem");  
    	ActorRef master = system.actorOf(Props.create(Master.class,POOL_WORKER,numbers));
    	ThreadResultat threadResultat = new ThreadResultat (master,system);
    	Thread thread = new Thread (threadResultat);
    	thread.start();
    	
    }
}
