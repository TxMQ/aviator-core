package com.txmq.exo.messaging.rest;

import java.util.Map;

import com.txmq.exo.core.ExoState;
import com.txmq.exo.messaging.AviatorCoreTransactionTypes;
import com.txmq.exo.messaging.AviatorTransactionType;
import com.txmq.exo.messaging.ExoMessage;
import com.txmq.exo.messaging.AviatorTransactionType.NamespaceEntry;
import com.txmq.exo.pipeline.PlatformEvents;
import com.txmq.exo.pipeline.metadata.ExoHandler;

public class TransactionTypesMapTransaction {

	@ExoHandler(namespace=AviatorCoreTransactionTypes.NAMESPACE,
				transactionType=AviatorCoreTransactionTypes.GET_TRANSACTION_TYPES, 
				events={PlatformEvents.messageReceived})
	public Map<Integer, AviatorTransactionType.NamespaceEntry> getTransactionTypesMap(ExoMessage<?> message, ExoState state) {
		message.interrupt();
		return AviatorTransactionType.getTransactionTypesMap();
	}
}
