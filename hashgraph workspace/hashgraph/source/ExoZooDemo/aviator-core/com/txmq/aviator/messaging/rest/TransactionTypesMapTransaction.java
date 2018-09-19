package com.txmq.aviator.messaging.rest;

import java.util.Map;

import com.txmq.aviator.core.AviatorState;
import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorTransactionType;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.PlatformEvents;
import com.txmq.aviator.pipeline.metadata.AviatorHandler;

public class TransactionTypesMapTransaction {

	@AviatorHandler(namespace=AviatorCoreTransactionTypes.NAMESPACE,
				transactionType=AviatorCoreTransactionTypes.GET_TRANSACTION_TYPES, 
				events={PlatformEvents.messageReceived})
	public Map<Integer, AviatorTransactionType.NamespaceEntry> getTransactionTypesMap(AviatorMessage<?> message, AviatorState state) {
		message.interrupt();
		return AviatorTransactionType.getTransactionTypesMap();
	}
}
