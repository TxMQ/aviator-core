package com.txmq.aviator.messaging.rest;

import javax.ws.rs.container.AsyncResponse;

import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorNotification;
import com.txmq.aviator.pipeline.ReportingEvents;
import com.txmq.aviator.pipeline.metadata.AviatorSubscriber;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberBase;

public class TransactionTypesMapResponder extends AviatorSubscriberBase<AsyncResponse> {
	@AviatorSubscriber(	namespace=AviatorCoreTransactionTypes.NAMESPACE,
			transactionType=AviatorCoreTransactionTypes.GET_TRANSACTION_TYPES, 
			events={ReportingEvents.transactionComplete})
	public void getTransactionTypesMapCompleted(AviatorNotification<?> notification) {
		AsyncResponse responder = this.getResponder(notification);
		if (responder != null) {
			responder.resume(notification);
		}
	}
}
