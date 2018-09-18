package com.txmq.exo.messaging.rest;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.txmq.exo.core.ExoPlatformLocator;
import com.txmq.exo.messaging.AviatorTransactionType;
import com.txmq.exo.messaging.AviatorCoreTransactionTypes;
import com.txmq.exo.messaging.ExoMessage;
import com.txmq.exo.pipeline.ReportingEvents;
import com.txmq.exo.pipeline.subscribers.ExoSubscriberManager;

/**
 * This class implements a REST endpoint for retrieving a list of endpoints that the Swirld
 * exposes.  Endpoints self-report by issuing an ExoMessage, which is logged in state.
 * 
 */
@Path("/exo/0.2.0") //TODO:  Remove HashgraphZoo prefix, give the internal APIs their own
public class EndpointsApi {
	private ExoSubscriberManager subscriberManager = new ExoSubscriberManager();
	
	@GET
	@Path("/endpoints")
	@Produces(MediaType.APPLICATION_JSON)
	public void getEndpoints(@Suspended AsyncResponse response) {
		ExoMessage<Serializable> transaction = 
				new ExoMessage<Serializable>(
						new AviatorTransactionType(AviatorCoreTransactionTypes.NAMESPACE, AviatorCoreTransactionTypes.LIST_ENDPOINTS)
		);
		this.subscriberManager.registerResponder(transaction, ReportingEvents.transactionComplete, response);
		try {
			ExoPlatformLocator.createTransaction(transaction);
		} catch (Exception e) {
			response.resume(Response.serverError().entity(e).build());
		}
	}
	
	@GET
	@Path("/shutdown")
	public Response shutdown() {
		
		ExoMessage<Serializable> transaction = 
				new ExoMessage<Serializable>(
						new AviatorTransactionType(AviatorCoreTransactionTypes.NAMESPACE, AviatorCoreTransactionTypes.SHUTDOWN)
		);
		try {
			ExoPlatformLocator.createTransaction(transaction);
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}
		
		return Response.ok().build();
	}
}
