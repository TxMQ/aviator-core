package com.txmq.aviator.blocklogger.transactions;

import com.txmq.aviator.core.swirlds.AviatorSwirldsState;
import com.txmq.aviator.core.swirlds.AviatorSwirlds;
import com.txmq.aviator.messaging.AviatorCoreTransactionTypes;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.PlatformEvents;
import com.txmq.aviator.pipeline.metadata.AviatorHandler;

public class PersistenceTransactions {
	
	@AviatorHandler(	namespace="AviatorCoreTransactionTypes",
					transactionType=AviatorCoreTransactionTypes.RECOVER_STATE, 
					events= {PlatformEvents.executeConsensus})
	public AviatorMessage<?> recoverState(AviatorMessage<?> message, AviatorSwirldsState state) {
		return message;
	}
}
