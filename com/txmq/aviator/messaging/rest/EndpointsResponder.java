package com.txmq.aviator.messaging.rest;

import javax.ws.rs.container.AsyncResponse;

import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorNotification;
import com.txmq.aviator.pipeline.ReportingEvents;
import com.txmq.aviator.pipeline.metadata.AviatorSubscriber;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberBase;

public class EndpointsResponder extends AviatorSubscriberBase<AsyncResponse> {
	@AviatorSubscriber(	namespace=AviatorCoreTransactionTypes.NAMESPACE,
			transactionType=AviatorCoreTransactionTypes.LIST_ENDPOINTS, 
			events={ReportingEvents.transactionComplete})
	public void listEndpointsCompleted(AviatorNotification<?> notification) {
		AsyncResponse responder = this.getResponder(notification);
		if (responder != null) {
			responder.resume(notification);
		}
	}
}
