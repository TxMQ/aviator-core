package com.txmq.aviator.messaging.websocket.grizzly;

import java.io.IOException;

import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketApplication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.txmq.aviator.core.PlatformLocator;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.pipeline.subscribers.AviatorSubscriberManager;

public class AviatorWebSocketApplication extends WebSocketApplication {
	
	private AviatorSubscriberManager subscriberManager = new AviatorSubscriberManager();

	public AviatorWebSocketApplication() {
		super();
	}
	
	@Override
	public void onConnect(WebSocket socket) {
		System.out.println("Connected");
		super.onConnect(socket);
	}
	
	@Override
	public void onClose(WebSocket socket, DataFrame frame) {
		System.out.println("Closed");
		super.onClose(socket, frame);
	}
	
	@SuppressWarnings("finally")
	@Override
    public void onMessage(WebSocket socket, String frame) {
		
		//Parse the incoming message
        AviatorMessageJsonParser parser = new AviatorMessageJsonParser();
        AviatorMessage<?> message = null;
        try {
			message = parser.readValue(frame, AviatorMessage.class);
		} catch (Exception e) {
			//Uh-oh..  Try to report the failure back to the caller
			e.printStackTrace();
			AviatorMessage<String> errorResponse = new AviatorMessage<String>();
			errorResponse.payload = "Could not deserialize message: " + frame;
			try {
				socket.send(parser.writeValueAsString(errorResponse));
			} catch (JsonProcessingException e1) {
				// OK, we're screwed..  Bail out.
				System.out.println("Websocket message deserialization and error reporting failed!");
				System.out.println(frame);
				e1.printStackTrace();
			} finally {
				return;
			}
		}
        
        /*
         * We're still here, so we must have been able to deserialize the message we received.  
         * Register responders, and pass the transaction on to the platform.
         */
        try {
        	subscriberManager.registerAllAvailableResponders(message, socket);
        	PlatformLocator.createTransaction(message);
        } catch (IOException e) {
        	e.printStackTrace();
        }        
    }
}

