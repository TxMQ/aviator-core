package com.txmq.aviator.pipeline.subscribers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.MultivaluedHashMap;

import com.txmq.aviator.core.Aviator;
import com.txmq.aviator.messaging.AviatorMessage;
import com.txmq.aviator.messaging.AviatorNotification;
import com.txmq.aviator.pipeline.ReportingEvents;

public class AviatorSubscriberManager {

	private static Map<String, Map<ReportingEvents, Map<UUID, Object>>> responders;
	private static Map<Object, List<ResponderLookup>> responderLookups;
	
	//TODO:  Should allow for more than one subscriber per message, per event?
	public AviatorSubscriberManager() {
		if (responders == null) {
			responders = Collections.synchronizedMap(new HashMap<String, Map<ReportingEvents, Map<UUID, Object>>>());
		}
		
		if (responderLookups == null) {
			responderLookups = new MultivaluedHashMap<Object, ResponderLookup>();
		}
	}
	
	private Map<ReportingEvents, Map<UUID, Object>> getRespondersForNode(String nodeName) {
		if (nodeName == null) {
			nodeName = Aviator.getNodeName();
		}
		
		if (!responders.containsKey(nodeName)) {
			Map<ReportingEvents, Map<UUID, Object>> nodeMap = Collections.synchronizedMap(
					new HashMap<ReportingEvents, Map<UUID, Object>> ()
			);
			
			nodeMap.put(ReportingEvents.submitted, 
						Collections.synchronizedMap(new HashMap<UUID, Object>()));
			nodeMap.put(ReportingEvents.preConsensusResult, 
						Collections.synchronizedMap(new HashMap<UUID, Object>()));
			nodeMap.put(ReportingEvents.consensusResult, 
						Collections.synchronizedMap(new HashMap<UUID, Object>()));
			nodeMap.put(ReportingEvents.transactionComplete, 
						Collections.synchronizedMap(new HashMap<UUID, Object>()));
			
			responders.put(nodeName, nodeMap);
		}
		
		return responders.get(nodeName);
	}
	
	public synchronized void registerResponder(AviatorMessage<?> message, ReportingEvents event, Object responderInstance) {	
		String myName = Aviator.getNodeName();
		getRespondersForNode(myName).get(event).put(message.uuid, responderInstance);
		
		if (!responderLookups.containsKey(responderInstance)) {
			responderLookups.put(responderInstance, new ArrayList<ResponderLookup>());
		}
		
		responderLookups.get(responderInstance).add(new ResponderLookup(myName, event, message.uuid));
	}
	
	public synchronized void registerAllAvailableResponders(AviatorMessage<?> message, Object responderInstance) {
		List<ReportingEvents> events = Aviator
				.getPipelineRouter()
				.getRegisteredNotificationsForTransactionType(message.transactionType);
		
		for (ReportingEvents event : events) {
			this.registerResponder(message, event, responderInstance);
		}
		
	}
	
	public synchronized Object getResponder(AviatorNotification<?> notification) {
		Map<UUID, Object> eventMap = getRespondersForNode(notification.nodeName).get(notification.event);
		if (eventMap.containsKey(notification.triggeringMessage.uuid)) {
			return eventMap.get(notification.triggeringMessage.uuid);
		} else {
			return null;
		}
	}
	
	public synchronized void removeResponder(AviatorNotification<?> notification) {
		Map<UUID, Object> eventMap = getRespondersForNode(notification.nodeName).get(notification.event);
		eventMap.remove(notification.triggeringMessage.uuid);
	}	
	
	public synchronized void removeResponder(Object responder) {
		if (responderLookups.containsKey(responder)) {
			for (ResponderLookup lookup : responderLookups.get(responder)) {
				if (getRespondersForNode(lookup.node).get(lookup.event).containsKey(lookup.notificationUUID)) {
					getRespondersForNode(lookup.node).get(lookup.event).remove(lookup.notificationUUID);
				}
			}
			
			responderLookups.remove(responder);
		}
	}
	
	private class ResponderLookup {
		public String node;
		public ReportingEvents event;
		public UUID notificationUUID;
		
		public ResponderLookup(String node, ReportingEvents event, UUID notificationUUID) {
			this.event = event;
			this.notificationUUID = notificationUUID;
		}
	}
}
