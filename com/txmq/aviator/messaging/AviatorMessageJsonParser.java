package com.txmq.aviator.messaging;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AviatorMessageJsonParser extends ObjectMapper {
	/**
	 * A map of transaction type to payload class.  This map is used to deserialize 
	 * transactions  that have come in through a mechanism where we wouldn't have 
	 * enough information to deserialize an ExoMessage.  The two obvious uses are 
	 * when receiving messages through a websocket, and to read in transactions 
	 * logged to a text file such as in the file-based, in-progress backup to the 
	 * block logger.
	 */
	private static Map<AviatorTransactionType, Class<?>> payloadMap;
	
	public static void registerPayloadType(AviatorTransactionType transactionType, Class<?> payloadClass) {
		if (payloadMap == null) {
			payloadMap = Collections.synchronizedMap(new HashMap<AviatorTransactionType, Class<?>>());
		}
		
		payloadMap.put(transactionType, payloadClass);
	}
	
	public AviatorMessageJsonParser() {
		super();
		
		//Configure this ObjectMapper derivative to use our custom deserializer
		SimpleModule module = new SimpleModule("ExoMessageJacksonDeserializer", new Version(1, 0, 0, null, "com.txmq", "exo"));	
		module.addDeserializer(AviatorMessage.class, new ExoMessageJacksonDeserializer());
		this.registerModule(module); 
	}
	
	private class ExoMessageJacksonDeserializer extends StdDeserializer<AviatorMessage<?>> {

	    
		protected ExoMessageJacksonDeserializer() {
			super(AviatorMessage.class);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2603203710237843037L;

		@Override
		public AviatorMessage<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
			
			ObjectMapper mapper = (ObjectMapper) parser.getCodec();  
			ObjectMapper innerMapper = new ObjectMapper();
			ObjectNode obj = (ObjectNode) mapper.readTree(parser);  
		    Iterator<Entry<String, JsonNode>> elementsIterator = obj.fields();
		    Class<?> clazz = null;
		    JsonNode payloadJsonNode = null;
		    
		    while (elementsIterator.hasNext()) {  
		    	Entry<String, JsonNode> element = elementsIterator.next();  
		    	String name = element.getKey();
		    	if (name.equals("transactionType")) {
		    		AviatorTransactionType transactionType = new AviatorTransactionType();
		    		if (element.getValue().get("ns").asInt() != 0) {
		    			transactionType.setNamespace(element.getValue().get("ns").asInt());
		    		} else {
		    			//most likely we received a string instead of an integer..  Let's try that.
		    			//Let things explode if it's neither a string or an integer.
		    			transactionType.setNamespace(element.getValue().get("ns").textValue());
		    			((ObjectNode) element.getValue()).put("ns",  transactionType.getNamespaceHash());
		    		}
		    		
		    		if (element.getValue().get("value").asInt() != 0) {
		    			transactionType.setValue(element.getValue().get("value").asInt());
		    		} else {
		    			//most likely we received a string instead of an integer..  Let's try that.
		    			//Let things explode if it's neither a string or an integer.
		    			transactionType.setValue(element.getValue().get("value").textValue());
		    			((ObjectNode) element.getValue()).put("value",  transactionType.getValueHash());
		    		}
		    		
		    		clazz = payloadMap.get(transactionType); 
		    	}
		    	
		    	if (name.equals("payload")) {
		    		payloadJsonNode = element.getValue();
		    		elementsIterator.remove();
		    	}
		    }
		    
		    //TODO:  THis implementation is incomplete..  The innerMapper needs to account for string/integer values in the namespace
		    @SuppressWarnings("unchecked")
			AviatorMessage<Serializable> result = innerMapper.treeToValue(obj, AviatorMessage.class);
		    if (clazz != null) {
		    	result.payload = (Serializable) innerMapper.treeToValue(payloadJsonNode, clazz);
		    }
		    
		    return result;
		}
		
	}
}
