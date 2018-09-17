package com.txmq.exo.messaging.rest;

import java.io.IOException;
import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.txmq.exo.messaging.AviatorCoreTransactionTypes;
import com.txmq.exo.messaging.AviatorTransactionType;
import com.txmq.exo.messaging.ExoMessage;
import com.txmq.exo.pipeline.ReportingEvents;
import com.txmq.exo.pipeline.subscribers.ExoSubscriberManager;

@Path("/exo/0.2.0") //TODO:  Remove HashgraphZoo prefix, give the internal APIs their own
public class TransactionTypesMapApi {
	private ExoSubscriberManager subscriberManager = new ExoSubscriberManager();
	
	@GET
	@Path("/transactiontypes")
	@Produces(MediaType.APPLICATION_JSON)
	public void getTransactionTypesMap(@Suspended AsyncResponse response) {
		ExoMessage<Serializable> message = new ExoMessage<Serializable>(
				new AviatorTransactionType(AviatorCoreTransactionTypes.NAMESPACE, AviatorCoreTransactionTypes.GET_TRANSACTION_TYPES),
				null
		);
		
		this.subscriberManager.registerResponder(message, ReportingEvents.transactionComplete, response);
		try {
			message.submit();
		} catch (IOException e) {
			response.resume(Response.serverError().entity(e).build());
		}
	}

}
