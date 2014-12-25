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
public class App {

	public static void main(String[] args) throws Exception {
		ActorSystem system = ActorSystem.create("faultTolerance");

		LoggingAdapter log = Logging.getLogger(system, system);

		
		//creation de l'acteur superviseur
		ActorRef supervisor = system.actorOf(
				Props.create(SupervisorActor.class), "supervisor");
		
		
		ResultMessageRequest request = new ResultMessageRequest();
		//Envoi d'un entier au superviseur
		log.info("Envoi de  l'entier 8 ...");
		supervisor.tell(Integer.valueOf(8),ActorRef.noSender());
		
		//Future pour recuperer  reponse provenant du superviseur
		Future<Object> future = Patterns
				.ask(supervisor, request, 5000);
		Integer result = (Integer) Await.result(
				future, Duration.create(5000, TimeUnit.MILLISECONDS));

		log.info("Value Received-> {}", result);
		assert result.equals(Integer.valueOf(8));
		
		log.info("Envoi de  l'entier 5 ...");
		supervisor.tell(Integer.valueOf(5),ActorRef.noSender());
		
		//Future pour recuperer  reponse provenant du superviseur
		 future = Patterns
				.ask(supervisor, request, 5000);
		 result = (Integer) Await.result(
				future, Duration.create(5000, TimeUnit.MILLISECONDS));

		log.info("Value Received-> {}", result);
		assert result.equals(Integer.valueOf(5));

		log.info("Envoi d'une ArithmeticException should be thrown! Our Supervisor strategy says resume !");
		supervisor.tell(new ArithmeticException("arithmetique exception"), ActorRef.noSender());

		//Future pour recuperer  reponse provenant du superviseur
		future = Patterns
						.ask(supervisor, request, 5000);
		result = (Integer) Await.result(
						future, Duration.create(5000, TimeUnit.MILLISECONDS));

		log.info("Value Received-> {}", result);
		assert result.equals(Integer.valueOf(8));

		log.info("Envoi d'une NullPointerException should be thrown! Our Supervisor strategy says restart !");
		supervisor.tell(new NullPointerException("null value!"), ActorRef.noSender());

		//Future pour recuperer  reponse provenant du superviseur
		future = Patterns.ask(supervisor, request, 5000);
		result = (Integer) Await.result(future, Duration.create(5000, TimeUnit.MILLISECONDS));

		log.info("Value Received-> {}", result);
		assert result.equals(0);
		
		//probe.watch(system.);
		log.info("Envoi d'une IllegalArgumentException should be thrown! Our Supervisor strategy says Stop !");

		supervisor.tell(new IllegalArgumentException ("wrong value!"), ActorRef.noSender());

		log.info("Worker Actor shutdown !");
		system.shutdown();

	}
}
