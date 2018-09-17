package com.txmq.exo.messaging.rest;

import com.txmq.exo.messaging.AviatorCoreTransactionTypes;
import com.txmq.exo.messaging.ExoMessage;
import com.txmq.exo.pipeline.PlatformEvents;
import com.txmq.exo.pipeline.metadata.ExoHandler;

import java.util.List;

import com.txmq.exo.core.ExoState;

/**
 * Implements the Endpoints API announcement transaction.
 */
public class EndpointsTransactions {
	@ExoHandler(namespace=AviatorCoreTransactionTypes.NAMESPACE, 
				transactionType=AviatorCoreTransactionTypes.ANNOUNCE_NODE, 
				events= {PlatformEvents.executeConsensus})
	public void announceNode(ExoMessage<?> message, ExoState state) {
		state.addEndpoint((String) message.payload);
	}
	
	@ExoHandler(namespace=AviatorCoreTransactionTypes.NAMESPACE, 
				transactionType=AviatorCoreTransactionTypes.LIST_ENDPOINTS, 
				events= {PlatformEvents.messageReceived})
	public List<String> listEndpoints(ExoMessage<?> message, ExoState state) {
		message.interrupt();
		return state.getEndpoints();
	}
}
