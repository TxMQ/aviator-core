package com.txmq.aviator.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Map.Entry;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AviatorConfig {

	private static final Map<String, Object> configs = new HashMap<String, Object>();
	private static boolean initialized = false;
	
	public static void loadConfiguration(String path) {
		Map<String, Class<?>> processors = new HashMap<String, Class<?>>();
		Reflections reflections = new Reflections(new ConfigurationBuilder().addUrls(ClasspathHelper.forPackage("com.txmq")));
		Set<Class<?>> processorClasses = reflections.getTypesAnnotatedWith(AviatorConfiguration.class);
		for (Class<?> processorClass : processorClasses) {
			AviatorConfiguration annotation = processorClass.getAnnotation(AviatorConfiguration.class);
			if (annotation.properties().length > 0) {
				for (String property : annotation.properties()) {
					processors.put(property, processorClass);
				}
			} else {
				processors.put(annotation.property(), processorClass);
			}
		}
		
		JsonNode jsonNode = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			jsonNode = mapper.readTree(new File(path));
			Iterator<Entry<String, JsonNode>> i = jsonNode.fields();
			while(i.hasNext()) {
				Entry<String, JsonNode> node = i.next();
				if (processors.containsKey(node.getKey())) {
					if (node.getValue().isArray()) {
						Iterator<JsonNode> childIterator = node.getValue().elements();
						List<Object> values = new ArrayList<Object>();
						while (childIterator.hasNext()) {
							values.add(mapper.treeToValue((TreeNode) childIterator.next(), processors.get(node.getKey())));							
						}
						configs.put(node.getKey(), values);
					} else {
						configs.put(node.getKey(), mapper.treeToValue((TreeNode) node.getValue(), processors.get(node.getKey())));					
					}
				} else {
					configs.put(node.getKey(), mapper.treeToValue(node.getValue(), Object.class));
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initialized = true;
	}
	
	public static Object get(String key) {
		if (!initialized) {
			loadConfiguration("aviator-config.json");
		}
		return configs.get(key);
	}
	
	public static boolean has(String key) {
		return configs.containsKey(key);
	}
}
