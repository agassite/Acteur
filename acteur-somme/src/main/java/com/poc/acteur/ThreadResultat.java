package com.poc.acteur;

import java.util.List;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.util.Timeout;

/**
 * Thread déclarant une future pour récupérer le résultat et l'afficher
 * @author david
 *
 */
public class ThreadResultat implements Runnable {
	
	private ActorRef master;
	private ActorSystem system;
	
	public ThreadResultat (ActorRef master,ActorSystem system) {
		this.master = master;
		this.system = system;
	}

	public void run() {
		try {
			Timeout timeout = new Timeout(Duration.create("30 seconds").toMillis());
	
			// Acteur implicite auquel notre master pourra repondre
			Future future = akka.pattern.Patterns
					.ask(master, new StartMessage(), timeout);
			
			//une fois le traitement terminé on recupere la reponse
			List<Integer> result = (List<Integer>) Await.result(future,
					timeout.duration());
			System.out.println("resultat= " + result);
			System.out.println("nb threads = " + Thread.activeCount());
			system.shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
