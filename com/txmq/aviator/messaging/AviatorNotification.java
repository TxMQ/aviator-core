package com.txmq.aviator.messaging;

import java.io.Serializable;

import com.txmq.aviator.pipeline.PipelineStatus;
import com.txmq.aviator.pipeline.ReportingEvents;

public class AviatorNotification<T extends Serializable> extends AviatorMessage<T> {
	public AviatorMessage<?> triggeringMessage;
	public ReportingEvents event;
	public PipelineStatus status;
	public String nodeName;
	
	public AviatorNotification() {
		
	}
	
	public AviatorNotification(ReportingEvents event, T payload, PipelineStatus status, AviatorMessage<?> triggeringMessage, String nodeName) {
		this.event = event;
		this.payload = payload;
		this.status = status;
		this.transactionType = triggeringMessage.transactionType;
		this.triggeringMessage = triggeringMessage;
		this.nodeName = nodeName;
	}
}
