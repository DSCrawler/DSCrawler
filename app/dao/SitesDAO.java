package dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import persistence.MobileCrawl;
import persistence.Site;
import persistence.SiteCrawl;
import play.db.jpa.JPA;

public class SitesDAO {
	
	private static final long MONTH_IN_MS = 1000 * 60 * 60 * 24 * 31;
	
	
	private static final Date STALE_DATE;
	static {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, - 1);
		STALE_DATE = calendar.getTime();
	}
	
	
	public static List<String> getDuplicateHomepages(int count, int offset) {
		String query = "select homepage from Site group by homepage having count(*) > 1";
		List<String> dups = JPA.em().createNativeQuery(query).setMaxResults(count).setFirstResult(offset).getResultList();
		return dups;
	}
	public static List<String> getDuplicateDomains(int count, int offset) {
		String query = "select domain from Site group by domain having count(*) > 1";
		List<String> dups = JPA.em().createNativeQuery(query).setMaxResults(count).setFirstResult(offset).getResultList();
		return dups;
	}
	
	
	public static Integer getCount(boolean franchise) {
		String query = "select count(*) from Site s where s.franchise = " + franchise;
		return GeneralDAO.getSingleInt(query, false);
	}
	public static List<Site> getAll(boolean franchise, int count, int offset) {
		String query = "from Site s where s.franchise = " + franchise;
		return SitesDAO.getList(query, false, count, offset);
	}
	
	
	
	public static Long getOldHomepagesCount(long crawlSetId){ 
		System.out.println("stale date : " + STALE_DATE);
		String query = "select count(s) from CrawlSet cs join cs.sites s" +  
				" where (s.redirectResolveDate not between :staleDate and current_date or s.redirectResolveDate = null) and cs.crawlSetId = :crawlSetId";
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		q.setParameter("crawlSetId", crawlSetId);
		q.setParameter("staleDate", STALE_DATE, TemporalType.DATE);
		
		return q.getSingleResult();
	}
	public static List<Site> getOldHomepages(long crawlSetId, int count, int offset) {
		Date monthAgo = new Date(System.currentTimeMillis() - MONTH_IN_MS);
		String query = "select s from CrawlSet cs join cs.sites s" +  
				" where (s.redirectResolveDate > :monthAgo) or s.redirectResolveDate = null) and cs.crawlSetId = :crawlSetId";
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		q.setParameter("crawlSetId", crawlSetId);
		q.setParameter("monthAgo", monthAgo, TemporalType.DATE);
		
		return q.setFirstResult(offset).setMaxResults(count).getResultList();
	}
	
	
	
	public static Long getOldCrawlsCount(long crawlSetId) {
		Date now = new Date(System.currentTimeMillis());
		Date monthAgo = new Date(now.getTime() -  MONTH_IN_MS);
		String query = "select count(ccc) from CrawlSet cs join cs.completedCrawls ccc where crawlSetId = :crawlSetId and ccc.crawlDate > :monthAgo";
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		q.setParameter("crawlSetId", crawlSetId);
		q.setParameter("monthAgo", monthAgo, TemporalType.DATE);
		
		return q.getSingleResult();
	}
	//TODO
//	public static List<Site> getOldCrawls(long crawlSetId, int count, int offset) {
//		String query = "select * from Site where siteId in ( " +
//				" SELECT s.siteId FROM ds.site s " +
//				" join site_siteCrawl ssc on ssc.site_siteId = s.siteId " +
//				" join siteCrawl sc on ssc.crawls_sitecrawlId = sc.sitecrawlid " +
//				" where s.franchise = " + franchise +
//				" group by s.siteId having max(sc.crawlDate) < DATE_SUB(NOW(), INTERVAL 2 MONTH)) ";
//		return SitesDAO.getList(query, true, count, offset);
//	}
//	
	
	
	public static Integer getNoCrawlsCount(boolean franchise) {
		String query = "select count(*) from Site s where s.crawls is empty and franchise = " + franchise;
		return GeneralDAO.getSingleInt(query, false);
	}
	
	public static List<Site> getNoCrawls(boolean franchise, int count, int offset) {
		String query = "from Site s where s.crawls is empty and franchise = " + franchise;
		return SitesDAO.getList(query, false, count, offset);
	}
	

	
	public static long getCount(String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCount(parameters);
	}
	public static long getCount(Map<String, Object> parameters) {
		String query = "select count(s) from Site s where s.siteId > 0";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
	public static List<Site> getList(String valueName, Object value, int count, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getList(parameters, count, offset);
	}
	public static List<Site> getList(Map<String, Object> parameters, int count, int offset){
		String query = "from Site s where s.siteId > 0";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(count);
		return q.getResultList();
	}
	
	
	
	public static long getCrawlSetCount(long crawlSetId, String valueName, Object value){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCrawlSetCount(crawlSetId, parameters);
	}
	public static long getCrawlSetCount(long crawlSetId, Map<String, Object> parameters) {
		String query = "select count(s) from CrawlSet cs join cs.sites s where cs.crawlSetId = :crawlSetId ";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Long> q = JPA.em().createQuery(query, Long.class);
		q.setParameter("crawlSetId", crawlSetId);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		return q.getSingleResult();
	}
	public static List<Site> getCrawlSetList(long crawlSetId, String valueName, Object value, int count, int offset){
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(valueName , value);
		return getCrawlSetList(crawlSetId, parameters, count, offset);
	}
	public  static List<Site> getCrawlSetList(long crawlSetId, Map<String, Object> parameters, int count, int offset){
		String query = "select s from CrawlSet cs join cs.sites s where cs.crawlSetId = :crawlSetId ";
		for(String key : parameters.keySet()) {
			query += " and s." + key + " = :" + key;
		}
		
		TypedQuery<Site> q = JPA.em().createQuery(query, Site.class);
		q.setParameter("crawlSetId", crawlSetId);
		for(Entry<String, Object> entry : parameters.entrySet()) {
			q.setParameter(entry.getKey(), entry.getValue());
		}
		q.setFirstResult(offset);
		q.setMaxResults(count);
		return q.getResultList();
	}

	
	
	public static List<Site> getList(String query, boolean isNative, int count, int offset) {
		Query q;
		if(isNative) {
			q = JPA.em().createNativeQuery(query, Site.class);
		}
		else {
			q = JPA.em().createQuery(query, Site.class);
		}
		
		if(count > 0){
			q.setMaxResults(count);
		}
		if(offset > 0){
			q.setFirstResult(offset);
		}
		
		return q.getResultList();
	}
}
