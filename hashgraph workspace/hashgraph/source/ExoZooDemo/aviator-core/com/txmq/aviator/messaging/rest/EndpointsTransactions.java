package com.txmq.aviator.messaging.rest;

import java.util.List;

import com.txmq.aviator.core.AviatorState;
import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.PlatformEvents;
import com.txmq.aviator.pipeline.metadata.AviatorHandler;

/**
 * Implements the Endpoints API announcement transaction.
 */
public class EndpointsTransactions {
	@AviatorHandler(namespace=AviatorCoreTransactionTypes.NAMESPACE, 
				transactionType=AviatorCoreTransactionTypes.ANNOUNCE_NODE, 
				events= {PlatformEvents.executeConsensus})
	public void announceNode(AviatorMessage<?> message, AviatorState state) {
		state.addEndpoint((String) message.payload);
	}
	
	@AviatorHandler(namespace=AviatorCoreTransactionTypes.NAMESPACE, 
				transactionType=AviatorCoreTransactionTypes.LIST_ENDPOINTS, 
				events= {PlatformEvents.messageReceived})
	public List<String> listEndpoints(AviatorMessage<?> message, AviatorState state) {
		message.interrupt();
		return state.getEndpoints();
	}
}
