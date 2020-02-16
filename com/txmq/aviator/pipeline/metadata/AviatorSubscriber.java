package com.txmq.aviator.pipeline.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.txmq.aviator.pipeline.ReportingEvents;

/**
 * Metadata element used to identify methods that handle Exo reporting events.  These 
 * subscribers are typically used to notify invoking applications of the status of a request.
 * 
 * Methods decorated witht his annotation should conform to the following signature:
 * 
 * public void onMessageReceived(ExoMessage<T> message, U state);
 * 
 * where T is the type of payload in the ExoMessage the receiving method processes, and U
 * is the type of your application's state object (ExoState descendent).
 *  
 * @author craigdrabik
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AviatorSubscriber {
	String namespace();
	String transactionType();
	ReportingEvents[] events();
}
