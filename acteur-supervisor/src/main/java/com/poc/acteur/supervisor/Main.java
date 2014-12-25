package com.poc.acteur.supervisor;

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
public class Main {

	public static void main(String[] args) throws Exception {
		
		ActorSystem system = ActorSystem.create("faultTolerance");
		LoggingAdapter log = Logging.getLogger(system, system);
		
		//creation de l'acteur superviseur
		ActorRef supervisor = system.actorOf(
				Props.create(SupervisorActor.class), "supervisor");
		
		
		ResultMessageRequest request = new ResultMessageRequest();
		
		//Envoi d'un entier au superviseur
		log.info("Envoi de  l'entier 2 ...");
		supervisor.tell(new IntegerMessage (2),ActorRef.noSender());
		
		//Future pour recuperer  reponse provenant du superviseur
		Future<Object> future = Patterns
				.ask(supervisor, request, 5000);
		Integer result = (Integer) Await.result(
				future, Duration.create(5000, TimeUnit.MILLISECONDS));

		//La valeur attendu est 4/2 -> 2
		log.info("Valeur recue-> {}", result);
		assert result.equals(Integer.valueOf(2));
		
		log.info("Envoi de  l'entier 0 ...");
		supervisor.tell(new IntegerMessage (0),ActorRef.noSender());
		
		//Future pour recuperer  reponse provenant du superviseur
		 future = Patterns
				.ask(supervisor, request, 5000);
		 result = (Integer) Await.result(
				future, Duration.create(5000, TimeUnit.MILLISECONDS));

		log.info("Valeur recue-> {}", result);
		
		//L'exception ArithmetiqueException a ete declenchee par le worker
		//Le worker continue donc la valeur renvoyee est toujours 2
		assert result.equals(Integer.valueOf(2));
		
		log.info("Envoi de  l'entier wrappe avec une valeur null ...");
		supervisor.tell(new IntegerMessage (null),ActorRef.noSender());
		
		//Future pour recuperer  reponse provenant du superviseur
		 future = Patterns
				.ask(supervisor, request, 5000);
		 result = (Integer) Await.result(
				future, Duration.create(5000, TimeUnit.MILLISECONDS));

		//L'exception NullPointerException a ete declenchee par le worker
		//Le worker est redemaree la valeur renvoyee est la valeur initiale 4
		log.info("Valeur recue-> {}", result);
		assert result.equals(Integer.valueOf(4));

		
		log.info("Worker Actor shutdown !");
		system.shutdown();

	}
}
