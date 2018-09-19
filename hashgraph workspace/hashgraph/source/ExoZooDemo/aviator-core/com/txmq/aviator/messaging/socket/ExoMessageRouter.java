package com.txmq.aviator.messaging.socket;

import com.txmq.aviator.core.AviatorState;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.transactionrouter.AviatorRouter;

public class ExoMessageRouter extends AviatorRouter<ExoMessageHandler> {
	public Object routeMessage(AviatorMessage<?> message, AviatorState state) throws ReflectiveOperationException {
		return this.invokeHandler(message.transactionType, message, state);
	}
}
