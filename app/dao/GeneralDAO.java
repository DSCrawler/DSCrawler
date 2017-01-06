package dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

import play.db.jpa.JPA;

public class GeneralDAO {

	
	public static Integer getSingleInt(String query, boolean isNative) {
		return Integer.parseInt(GeneralDAO.getSingleString(query, isNative));
	}

	public static String getSingleString(String query, boolean isNative) {
		return GeneralDAO.getSingleObject(query, isNative).toString();
	}

	public static Object getSingleObject(String query, boolean isNative) {
		if(isNative) {
			return JPA.em().createNativeQuery(query).getSingleResult();
		}
		return JPA.em().createQuery(query).getSingleResult();
	}
	
	public static <T> List<T> getFieldList(Class<T> clazz, Class<?> parentEntityClazz, String fieldName){
		Map<String, Object> parameters = new HashMap<String, Object>();
		return getFieldList(clazz, parentEntityClazz, fieldName, parameters);
	}
	
	public static <T> List<T> getFieldList(Class<T> clazz, Class<?> parentEntityClazz, String fieldName, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFieldList(clazz, parentEntityClazz, fieldName, parameters);
	}
	
	public static <T> List<T> getFieldList(Class<T> clazz, Class<?> parentEntityClazz, String fieldName,  Map<String, Object> parameters){
		String queryString = "select t." + fieldName + " from " + parentEntityClazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			queryString += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		TypedQuery<T> q = JPA.em().createQuery(queryString, clazz);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getResultList();
	}
	
	public static <T> T getFirst(Class<T> clazz, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirst(clazz, parameters);
	}
	public static <T> T getFirst(Class<T> clazz, Map<String, Object> parameters){
		String query = "from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		
		TypedQuery<T> q = JPA.em().createQuery(query, clazz);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setMaxResults(1);
		List<T> results = q.getResultList();
		if(results.size() > 0){
			return results.get(0);
		}
		return null;
	}
	
	public static <T> T getFirstOrNew(Class<T> clazz, String valueName, Object value) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getFirstOrNew(clazz, parameters);
	}
	public static <T> T getFirstOrNew(Class<T> clazz, Map<String, Object> parameters) {
		T entity = getFirst(clazz, parameters);
		if(entity != null)
			return entity;
		try{
			return clazz.newInstance();
		} catch(Exception e) {
			throw new IllegalArgumentException("Can't instantiate class with illegal constructors : " + clazz.getSimpleName() + " : " + e.getMessage());
		}
	}
	
	public static <T> List<T> getList(Class<T> clazz, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getList(clazz, parameters);
	}
	public static <T> List<T> getList(Class<T> clazz, Map<String, Object> parameters){
		String query = "from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		
		TypedQuery<T> q = JPA.em().createQuery(query, clazz);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getResultList();
	}
	
	public static <T> Long getCount(Class<T> clazz, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCount(clazz, parameters);
	}
	public static <T> Long getCount(Class<T> clazz, Map<String, Object> parameters){
		String query = "select count(t) from " + clazz.getSimpleName() + " t";
		String delimiter = " where t.";
		for(String key : parameters.keySet()) {
			query += delimiter + key + " = :" + key;
			delimiter = " and t.";
		}
		
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
}
