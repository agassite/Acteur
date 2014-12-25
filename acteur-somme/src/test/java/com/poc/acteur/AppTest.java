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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
	
	public static final int POOL_WORKER = 8;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp ()
    {
    	LoggingAdapter log = null;
    	
    	try {
	    	//liste d'entier a traiter
	    	List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8);  
	    	ActorSystem system = ActorSystem.create("mySystem");  
	    	log = Logging.getLogger(system, system);
	    	
	    	//creation du master
	    	ActorRef master = system.actorOf(Props.create(Master.class,POOL_WORKER,numbers));
	    	
			// Creation de la future, Acteur implicite auquel notre master pourra repondre
			Future<Object> future = Patterns
					.ask(master, new StartMessage(), 30000);
	    	
			List <Integer> listeResultat = (List<Integer>) Await.result(future,
					Duration.create(30000, TimeUnit.MILLISECONDS));
	    	log.info("resultat= " + listeResultat);
			system.shutdown();
    	} catch (Exception ex) {
    		log.error("error: " + ex.getMessage());
    	}
    }
}
