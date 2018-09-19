package com.txmq.aviator.messaging.rest;

import java.io.IOException;
import java.io.Serializable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorTransactionType;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.ReportingEvents;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberManager;

@Path("/exo/0.2.0") //TODO:  Remove HashgraphZoo prefix, give the internal APIs their own
public class TransactionTypesMapApi {
	private AviatorSubscriberManager subscriberManager = new AviatorSubscriberManager();
	
	@GET
	@Path("/transactiontypes")
	@Produces(MediaType.APPLICATION_JSON)
	public void getTransactionTypesMap(@Suspended AsyncResponse response) {
		AviatorMessage<Serializable> message = new AviatorMessage<Serializable>(
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
