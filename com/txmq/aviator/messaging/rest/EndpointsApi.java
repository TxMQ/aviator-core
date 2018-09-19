package com.txmq.aviator.messaging.rest;

import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.txmq.aviator.core.PlatformLocator;
import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorTransactionType;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.ReportingEvents;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberManager;

/**
 * This class implements a REST endpoint for retrieving a list of endpoints that the Swirld
 * exposes.  Endpoints self-report by issuing an ExoMessage, which is logged in state.
 * 
 */
@Path("/exo/0.2.0") //TODO:  Remove HashgraphZoo prefix, give the internal APIs their own
public class EndpointsApi {
	private AviatorSubscriberManager subscriberManager = new AviatorSubscriberManager();
	
	@GET
	@Path("/endpoints")
	@Produces(MediaType.APPLICATION_JSON)
	public void getEndpoints(@Suspended AsyncResponse response) {
		AviatorMessage<Serializable> transaction = 
				new AviatorMessage<Serializable>(
						new AviatorTransactionType(AviatorCoreTransactionTypes.NAMESPACE, AviatorCoreTransactionTypes.LIST_ENDPOINTS)
		);
		this.subscriberManager.registerResponder(transaction, ReportingEvents.transactionComplete, response);
		try {
			PlatformLocator.createTransaction(transaction);
		} catch (Exception e) {
			response.resume(Response.serverError().entity(e).build());
		}
	}
	
	@GET
	@Path("/shutdown")
	public Response shutdown() {
		
		AviatorMessage<Serializable> transaction = 
				new AviatorMessage<Serializable>(
						new AviatorTransactionType(AviatorCoreTransactionTypes.NAMESPACE, AviatorCoreTransactionTypes.SHUTDOWN)
		);
		try {
			PlatformLocator.createTransaction(transaction);
		} catch (Exception e) {
			return Response.serverError().entity(e).build();
		}
		
		return Response.ok().build();
	}
}
