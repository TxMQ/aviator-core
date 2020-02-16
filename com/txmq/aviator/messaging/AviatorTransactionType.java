package com.txmq.aviator.messaging;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import org.reflections.Reflections;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.txmq.aviator.messaging.annotations.TransactionType;
import com.txmq.aviator.messaging.annotations.TransactionTypes;
import com.txmq.aviator.util.hash.MurmurHash3;

public class AviatorTransactionType implements Serializable {

	/**
	 * Seed value for the hashing algorithm used internally. This value 
	 * was derived using "Aviator".hashCode() and fixed to guard against 
	 * the implementation of Java's String.hashCode() changing. 
	 */
	private static final int hashSeed = 1036411402;

	/**
	 * Convenience method wrapping the Murmur3 implementation used internally.
	 * @return a 32-bit integer hash of the supplied string
	 */
	private static int hash(String message) {
		return MurmurHash3.murmurhash3_x86_32(message, 0, message.length(), hashSeed);
	}
	
	private static BidiMap<String, Integer> namespaceHashes = new DualHashBidiMap<String, Integer>();
	
	private static BidiMap<String, Integer> transactionTypeHashes = new DualHashBidiMap<String, Integer>();
	
	private static HashSet<AviatorTransactionType> transactionTypes= new HashSet<AviatorTransactionType>();
	
	
	/**
	 * Inspects the classpath for classes annotated with @TransactionTypes and 
	 * registers public static strings as transaction types.  This behavior can 
	 * be modified to include only those items annotated with @TransactionType 
	 * if the class is annotated with @TransactionTypes(onlyAnnotatedValues=true)
	 * 
	 *  This method is called by the framework during bootstrapping - developers 
	 *  should not call this method themselves.
	 */
	//TODO:  Offer optimization through a whitelist of transaction type packages in exo-config.json
	public static void initialize(List<String> packages) throws ReflectiveOperationException {
		List<String> pkgs = new ArrayList<>(packages);
		pkgs.add("com.txmq.aviator");
		
		Reflections reflections = new Reflections(pkgs.toArray());
		Set<Class<?>> transactionTypeClasses = reflections.getTypesAnnotatedWith(TransactionTypes.class);
		for (Class<?> ttc : transactionTypeClasses) {
			TransactionTypes tta = ttc.getAnnotation(TransactionTypes.class);
			String namespace;
			if (!tta.namespace().equals("")) {
				namespace = tta.namespace();
			} else {
				namespace = ttc.getName();
			}
			int classHash = hash(namespace);
			namespaceHashes.putIfAbsent(namespace, classHash);
				
			for (Field field : ttc.getFields()) {
				if (	field.getType().equals(String.class) && 
						Modifier.isStatic(field.getModifiers()) && 
						(!tta.onlyAnnotatedValues() || field.isAnnotationPresent(TransactionType.class))
					) 
				{
					String typeValue = (String) field.get(null);
					int typeHash = hash(typeValue);
					transactionTypeHashes.putIfAbsent(typeValue, typeHash);
					transactionTypes.add(new AviatorTransactionType(namespace, typeValue));
				}
			}
		}
	}
	
	public static Map<Integer, NamespaceEntry> getTransactionTypesMap() {
		
		HashMap<Integer, NamespaceEntry> result = new HashMap<Integer, NamespaceEntry>();
		for (Integer nsHash : namespaceHashes.values()) {
			NamespaceEntry nsEntry = new AviatorTransactionType().new NamespaceEntry(nsHash, namespaceHashes.getKey(nsHash));
			result.put(nsHash, nsEntry);
		}
		
		for (AviatorTransactionType transactionType : transactionTypes) {
			NamespaceEntry nsEntry = result.get(transactionType.getNamespaceHash());
			nsEntry.transactionTypes.put(transactionType.getValueHash(), transactionType.getValue());
			//result.get(transactionType.getNamespaceHash()).transactionTypes.put(transactionType.getValueHash(), transactionType.getValue());
		}
		
		return result;
	}
	
	/**
	 * Inner class used in the transaction type map
	 */
	public class NamespaceEntry {
		public Integer namesapceHash;
		public String namespace;
		public Map<Integer, String> transactionTypes;		
		
		public NamespaceEntry(int hash, String namespace) {
			this.namesapceHash = hash;
			this.namespace = namespace;
			this.transactionTypes = new HashMap<Integer, String>();
		}
	}
	
	/*
	 * Instance stuff
	 */
	
	/**
	 * Holds the hashed value of the namespace string for this transaction type
	 */
	private int ns;
	
	/**
	 * Holds the hashed value of the transaction type value
	 */
	private int value;
	
	@JsonIgnore()
	private String _ns;
	
	@JsonIgnore() 
	private String _value;
	
	//TODO:  Test for validity
	@JsonProperty("ns")
	public void setNamespace(int namespace) {
		this.ns = namespace;
	}
	
	//TODO: test for validity
	public void setNamespace(String namespace) {
		this.ns = hash(namespace);
		this._ns = namespace;
	}
	
	@JsonProperty("ns")
	public int getNamespaceHash() {
		return this.ns;
	}
	
	@JsonIgnore
	public String getNamespace() {
		return namespaceHashes.getKey(this.ns);		
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public void setValue(String value) {
		this.value = hash(value);
		this._value = value;
	}
	
	@JsonProperty("value")
	public int getValueHash() {
		return this.value;
	}
	
	public String getValue() {
		return transactionTypeHashes.getKey(this.value);
	}
	
	public AviatorTransactionType() {
		super();
	}
	
	public AviatorTransactionType(String namespace, String value) {
		this.setNamespace(namespace);
		this.setValue(value);
	}
	
	public AviatorTransactionType(int namespace, int value) {
		this.setNamespace(namespace);
		this.setValue(value);
	}
	
	@JsonIgnore
	public boolean isValid() {
		return transactionTypes.contains(this);
	}

	@Override
	public boolean equals(Object obj) {
		AviatorTransactionType that = (AviatorTransactionType) obj;
		return this.ns == that.ns && this.value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.ns, this.value);
	}
}
