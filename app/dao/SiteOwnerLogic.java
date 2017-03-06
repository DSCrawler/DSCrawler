package dao;

import persistence.Site;
import urlcleanup.SiteOwner;

public class SiteOwnerLogic {

	public static void assignUnresolvedSiteThreadsafe(SiteOwner owner){
		if(owner.getWebsiteString() == null){
			owner.setUnresolvedSite(null);
			return;
		}
		owner.setUnresolvedSite(SitesDAO.getOrNewThreadsafe(owner.getWebsiteString()));
	}
	
	public static void assignUnresolvedSite(SiteOwner owner){
		if(owner.getWebsiteString() == null){
			owner.setUnresolvedSite(null);
			return;
		}
		owner.setUnresolvedSite(SitesDAO.getOrNew(owner.getWebsiteString()));
	}
	
	public static void forwardSite(SiteOwner owner) {
		try{
			Site redirectEndpoint = SitesDAO.getRedirectEndpoint(owner.getUnresolvedSite(), true);
			owner.setResolvedSite(redirectEndpoint);
		} catch(StackOverflowError e) {
			System.out.println("caught stackoverflow error while forwarding site.  Unresolved site : " + owner.getUnresolvedSite().getSiteId());
		}
		
	}
}