package com.txmq.exo.messaging.rest;

import javax.ws.rs.container.AsyncResponse;

import com.txmq.exo.messaging.AviatorCoreTransactionTypes;
import com.txmq.exo.messaging.ExoNotification;
import com.txmq.exo.pipeline.ReportingEvents;
import com.txmq.exo.pipeline.metadata.ExoSubscriber;
import com.txmq.exo.pipeline.subscribers.ExoSubscriberBase;
import com.txmq.exozoodemo.ZooDemoTransactionTypes;

public class TransactionTypesMapResponder extends ExoSubscriberBase<AsyncResponse> {
	@ExoSubscriber(	namespace=AviatorCoreTransactionTypes.NAMESPACE,
			transactionType=AviatorCoreTransactionTypes.GET_TRANSACTION_TYPES, 
			events={ReportingEvents.transactionComplete})
	public void getTransactionTypesMapCompleted(ExoNotification<?> notification) {
		AsyncResponse responder = this.getResponder(notification);
		if (responder != null) {
			responder.resume(notification);
		}
	}
}
