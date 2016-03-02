package persistence;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import persistence.converters.GeneralMatchConverter;
import persistence.converters.SchedulerConverter;
import persistence.converters.WebProviderConverter;
import utilities.DSFormatter;
import datadefinitions.GeneralMatch;
import datadefinitions.Scheduler;
import datadefinitions.WebProvider;
import datadefinitions.newdefinitions.WPAttribution;

//Lazy fetch all collections

@Entity
public class SiteCrawl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected long siteCrawlId;
	
	
	@ManyToOne(cascade=CascadeType.DETACH)
	@JoinTable(name="site_sitecrawl", 
			joinColumns={@JoinColumn(name="crawls_siteCrawlId")},
		    inverseJoinColumns={@JoinColumn(name="Site_siteId")})
//	@Transient
	private Site site;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String seed;
	@Column(nullable = true, columnDefinition="varchar(4000)")
	private String resolvedSeed;
	
	@Column(nullable = false, columnDefinition="boolean default true")
	private boolean followNonUnique = true;
	
	private Date crawlDate;
	
	private int crawlDepth;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean homepageCrawl = false;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, orphanRemoval= true)
	@JoinTable(name="sitecrawl_pagecrawl",
		joinColumns={@JoinColumn(name="SiteCrawl_siteCrawlId")},
		inverseJoinColumns={@JoinColumn(name="pageCrawls_pageCrawlId")})
	private Set<PageCrawl> pageCrawls = new HashSet<PageCrawl>();
	
	@OneToOne
	private PageCrawl newInventoryPage;
	@OneToOne
	private PageCrawl usedInventoryPage;
	
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> allLinks = new HashSet<String>();
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> intrasiteLinks = new HashSet<String>();
	@Column(nullable = true, columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> uniqueCrawledPageUrls = new HashSet<String>();		//Involves chopping off query strings
	
	
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> crawledUrls = new HashSet<String>();		//Includes failed urls
	@Column(columnDefinition="varchar(4000)")
	@ElementCollection(fetch=FetchType.LAZY)
	private Set<String> failedUrls = new HashSet<String>();
	
	@Column(nullable = true, columnDefinition="varchar(1000)")
	private String storageFolder;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean maxPagesReached = false;
	@Column(nullable = false, columnDefinition="int(11) default 0")
	private int numRepeatedUrls = 0;
	@Column(nullable = false, columnDefinition="int(11) default 0")
	private int numRetrievedFiles = 0;
	@Column(nullable = false, columnDefinition="int(11) default 0")
	private int numLargeFiles = 0;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean smallCrawlApproved = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	private boolean reviewLater = false;

	@Convert(converter = WebProviderConverter.class)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<WebProvider> webProviders = new HashSet<WebProvider>();
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<WPAttribution> wpAttributions = new HashSet<WPAttribution>();
	
	@Convert(converter = SchedulerConverter.class)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<Scheduler> schedulers = new HashSet<Scheduler>();
	
	@Convert(converter = GeneralMatchConverter.class)
	@ElementCollection(fetch=FetchType.LAZY)
	protected Set<GeneralMatch> generalMatches = new HashSet<GeneralMatch>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected Set<ExtractedString> extractedStrings = new HashSet<ExtractedString>();
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	protected Set<ExtractedUrl> extractedUrls = new HashSet<ExtractedUrl>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval=true)
	protected Set<Staff> allStaff = new HashSet<Staff>();
	
	@Convert(converter = WebProviderConverter.class)
	@Column(nullable = true)
	protected WebProvider inferredWebProvider;
	
	
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean crawlingDone = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean docAnalysisDone = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean amalgamationDone = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean textAnalysisDone = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean metaAnalysisDone = false;
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean inferencesDone = false;
	 
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean filesMoved = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean filesDeleted = false;
	
	@Column(nullable = false, columnDefinition="boolean default false")
	protected boolean maybeDuplicate = false;
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	protected Set<FBPage> fbPages = new HashSet<FBPage>();
	
	@ManyToMany(cascade=CascadeType.ALL)
	private List<Metatag> metatags = new ArrayList<Metatag>();
	
	@ManyToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	private List<ImageTag> imageTags = new ArrayList<ImageTag>();
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval=true)
	private Set<InventoryNumber> inventoryNumbers = new HashSet<InventoryNumber>();
	
	private int uniqueUrlScore = 0;
	private int uniqueTitleScore = 0;
	private int uniqueH1Score = 0;
	private int uniqueMetaDescriptionScore = 0;
	
	private int contentUrlScore = 0;
	private int contentTitleScore = 0;
	private int contentH1Score = 0;
	private int contentSRPH1Score = 0;
	private int contentMetaDescriptionScore = 0;
	
	private int lengthTitleScore = 0;
	private int lengthMetaDescriptionScore = 0;
	
	private int altImageScore = 0;
	private int urlReadableScore = 0;
	
	
//	@Column(columnDefinition="varchar(1000)")
//	@ElementCollection(fetch=FetchType.LAZY)
//	private Set<String> h1s = new HashSet<String>();
//	
//	@Column(columnDefinition="varchar(4000)")
//	@ElementCollection(fetch=FetchType.LAZY)
//	private Set<String> titles = new HashSet<String>();
	
	
	public SiteCrawl(String seed) {
		this.setSeed(seed);
	}
	
	private SiteCrawl() {
		
	}
	
	public void lazyInit() {
		allStaff.size();
		extractedUrls.size();
		extractedStrings.size();
		generalMatches.size();
		schedulers.size();
		webProviders.size();
		inventoryNumbers.size();
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		if(seed == null || seed.length() > 4000){
			throw new IllegalArgumentException("Can't set URL with length > 4000 as seed of SiteCrawl");
		}
		this.seed = seed;
	}

	public String getResolvedSeed() {
		return resolvedSeed;
	}

	public void setResolvedSeed(String resolvedSeed) {
		if(resolvedSeed == null || resolvedSeed.length() > 4000){
			throw new IllegalArgumentException("Can't set URL with length > 4000 as resolved seed of SiteCrawl");
		}
		this.resolvedSeed = resolvedSeed;
	}

	public Date getCrawlDate() {
		return crawlDate;
	}

	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}

	

	public String getStorageFolder() {
		return storageFolder;
	}

	public void setStorageFolder(String storageFolder) {
		this.storageFolder = storageFolder;
	}

	public boolean isMaxPagesReached() {
		return maxPagesReached;
	}

	public void setMaxPagesReached(boolean maxPagesReached) {
		this.maxPagesReached = maxPagesReached;
	}

	public int getNumRetrievedFiles() {
		return numRetrievedFiles;
	}

	public void setNumRetrievedFiles(int numRetrievedFiles) {
		this.numRetrievedFiles = numRetrievedFiles;
	}

	public int getNumLargeFiles() {
		return numLargeFiles;
	}

	public void setNumLargeFiles(int numLargeFiles) {
		this.numLargeFiles = numLargeFiles;
	}

	public Set<WebProvider> getWebProviders() {
		return webProviders;
	}

	public void setWebProviders(Set<WebProvider> webProviders) {
		this.webProviders.clear();
		this.webProviders.addAll(webProviders);
	}

	public Set<Scheduler> getSchedulers() {
		return schedulers;
	}

	public void setSchedulers(Set<Scheduler> schedulers) {
		this.schedulers.clear();
		this.schedulers.addAll(schedulers);
	}

	public Set<GeneralMatch> getGeneralMatches() {
		return generalMatches;
	}

	public void setGeneralMatches(Set<GeneralMatch> generalMatches) {
		this.generalMatches.clear();
		this.generalMatches.addAll(generalMatches);
	}

	public Set<ExtractedString> getExtractedStrings() {
		return extractedStrings;
	}

	public void setExtractedStrings(Set<ExtractedString> extractedStrings) {
		this.extractedUrls.clear();
		this.extractedUrls.addAll(extractedUrls);
	}
	
	public void addExtractedStrings(Set<ExtractedString> extractedStrings) {
		this.extractedStrings.addAll(extractedStrings);
	}

	public Set<ExtractedUrl> getExtractedUrls() {
		return extractedUrls;
	}

	public void setExtractedUrls(Set<ExtractedUrl> extractedUrls) {
		this.extractedUrls.clear();
		this.extractedUrls.addAll(extractedUrls);
	}
	public void addExtractedUrls(Set<ExtractedUrl> extractedUrls) {
		this.extractedUrls.addAll(extractedUrls);
	}

	public Set<Staff> getAllStaff() {
		return allStaff;
	}

	public void setAllStaff(Set<Staff> allStaff) {
		this.allStaff.clear();
		this.allStaff.addAll(allStaff);
	}
	
	public void addStaff(Set<Staff> someStaff) {
		this.allStaff.addAll(someStaff);
	}

	public boolean isCrawlingDone() {
		return crawlingDone;
	}

	public void setCrawlingDone(boolean crawlingDone) {
		this.crawlingDone = crawlingDone;
	}

	public boolean isDocAnalysisDone() {
		return docAnalysisDone;
	}

	public void setDocAnalysisDone(boolean docAnalysisDone) {
		this.docAnalysisDone = docAnalysisDone;
	}

	public boolean isAmalgamationDone() {
		return amalgamationDone;
	}

	public void setAmalgamationDone(boolean amalgamationDone) {
		this.amalgamationDone = amalgamationDone;
	}

	public boolean isTextAnalysisDone() {
		return textAnalysisDone;
	}

	public void setTextAnalysisDone(boolean textAnalysisDone) {
		this.textAnalysisDone = textAnalysisDone;
	}

	public boolean isInferencesDone() {
		return inferencesDone;
	}

	public void setInferencesDone(boolean inferencesDone) {
		this.inferencesDone = inferencesDone;
	}

	public boolean isFilesDeleted() {
		return filesDeleted;
	}

	public void setFilesDeleted(boolean filesDeleted) {
		this.filesDeleted = filesDeleted;
	}

	public int getNumRepeatedUrls() {
		return numRepeatedUrls;
	}

	public void setNumRepeatedUrls(int numRepeatedUrls) {
		this.numRepeatedUrls = numRepeatedUrls;
	}

	public long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}

	public boolean isFilesMoved() {
		return filesMoved;
	}

	public void setFilesMoved(boolean filesMoved) {
		this.filesMoved = filesMoved;
	}

	public WebProvider getInferredWebProvider() {
		return inferredWebProvider;
	}

	public void setInferredWebProvider(WebProvider inferredWebProvider) {
		this.inferredWebProvider = inferredWebProvider;
	}

	public boolean isMaybeDuplicate() {
		return maybeDuplicate;
	}

	public void setMaybeDuplicate(boolean maybeDuplicate) {
		this.maybeDuplicate = maybeDuplicate;
	}

	public Set<FBPage> getFbPages() {
		return fbPages;
	}

	public void setFbPages(Set<FBPage> fbPages) {
		this.fbPages.clear();
		this.fbPages.addAll(fbPages);
	}
	
	public void addFbPage(FBPage fbPage) {
		this.fbPages.add(fbPage);
	}

	public boolean isHomepageCrawl() {
		return homepageCrawl;
	}

	public void setHomepageCrawl(boolean homepageCrawl) {
		this.homepageCrawl = homepageCrawl;
	}

	public boolean isSmallCrawlApproved() {
		return smallCrawlApproved;
	}

	public void setSmallCrawlApproved(boolean smallCrawlApproved) {
		this.smallCrawlApproved = smallCrawlApproved;
	}

	public boolean isReviewLater() {
		return reviewLater;
	}

	public void setReviewLater(boolean reviewLater) {
		this.reviewLater = reviewLater;
	}

	public boolean isFollowNonUnique() {
		return followNonUnique;
	}

	public void setFollowNonUnique(boolean followNonUnique) {
		this.followNonUnique = followNonUnique;
	}

	public Set<String> getAllLinks() {
		return allLinks;
	}

	public void setAllLinks(Set<String> allLinks) {
		this.allLinks.clear();
		for(String item : allLinks){
			this.allLinks.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public boolean addLink(String link) {
		return this.allLinks.add(DSFormatter.truncate(link, 4000));
	}

	public Set<String> getIntrasiteLinks() {
		return intrasiteLinks;
	}

	public void setIntrasiteLinks(Set<String> intrasiteLinks) {
		this.intrasiteLinks.clear();
		for(String item : intrasiteLinks){
			this.intrasiteLinks.add(DSFormatter.truncate(item, 4000));
		}
	}

	public boolean addIntrasiteLink(String intrasiteLink) {
		return this.intrasiteLinks.add(DSFormatter.truncate(intrasiteLink, 4000));
	}

	public Set<String> getUniqueCrawledPageUrls() {
		return uniqueCrawledPageUrls;
	}

	public void setUniqueCrawledPageUrls(Set<String> uniqueCrawledPageUrls) {
		this.uniqueCrawledPageUrls.clear();
		for(String item : uniqueCrawledPageUrls){
			this.uniqueCrawledPageUrls.add(DSFormatter.truncate(item, 4000));
		}
	}
	
	public boolean addUniqueCrawledPageUrl(String uniqueCrawledPageUrl) {
		return this.uniqueCrawledPageUrls.add(DSFormatter.truncate(uniqueCrawledPageUrl, 4000));
	}
	
	public Set<String> getCrawledUrls() {
		return crawledUrls;
	}

	public void setCrawledUrls(Set<String> crawledUrls) {
		this.crawledUrls.clear();
		for(String url : crawledUrls){
			this.crawledUrls.add(DSFormatter.truncate(url, 4000));
		}
	}
	
	public boolean addCrawledUrl(String crawledUrl) {
		return this.crawledUrls.add(DSFormatter.truncate(crawledUrl, 4000));
	}

	public Set<String> getFailedUrls() {
		return failedUrls;
	}

	public void setFailedUrls(Set<String> failedUrls) {
		this.failedUrls.clear();
		for(String url : failedUrls){
			this.failedUrls.add(DSFormatter.truncate(url, 4000));
		}
	}
	
	public boolean addFailedUrl(String failedUrl) {
		return this.failedUrls.add(DSFormatter.truncate(failedUrl, 4000));
	}

	public int getCrawlDepth() {
		return crawlDepth;
	}

	public void setCrawlDepth(int crawlDepth) {
		this.crawlDepth = crawlDepth;
	}

	public Set<PageCrawl> getPageCrawls() {
		return pageCrawls;
	}

	public void setPageCrawls(Set<PageCrawl> pageCrawls) {
		this.pageCrawls.clear();
		this.pageCrawls.addAll(pageCrawls);
	}
	
	public void addPageCrawl(PageCrawl pageCrawl) {
		this.pageCrawls.add(pageCrawl);
	}
	
	public List<ImageTag> getImageTags() {
		return imageTags;
	}

	public void setImageTags(Collection<ImageTag> imageTags) {
		this.imageTags.clear();
		this.imageTags.addAll(imageTags);
	}
	
	public boolean addImageTag(ImageTag imageTag) {
		return imageTags.add(imageTag);
	}
	
	public List<Metatag> getMetatags() {
		return metatags;
	}

	public void setMetatags(Collection<Metatag> metatags) {
		this.metatags.clear();
		this.metatags.addAll(metatags);
	}
	
	public boolean addMetatag(Metatag metatag) {
		return this.metatags.add(metatag);
	}

	public boolean isMetaAnalysisDone() {
		return metaAnalysisDone;
	}

	public void setMetaAnalysisDone(boolean metaAnalysisDone) {
		this.metaAnalysisDone = metaAnalysisDone;
	}
	
	public int getUniqueUrlScore() {
		return uniqueUrlScore;
	}

	public void setUniqueUrlScore(int uniqueUrlScore) {
		this.uniqueUrlScore = uniqueUrlScore;
	}

	public int getUniqueTitleScore() {
		return uniqueTitleScore;
	}

	public void setUniqueTitleScore(int uniqueTitleScore) {
		this.uniqueTitleScore = uniqueTitleScore;
	}

	public int getUniqueH1Score() {
		return uniqueH1Score;
	}

	public void setUniqueH1Score(int uniqueH1Score) {
		this.uniqueH1Score = uniqueH1Score;
	}

	public int getUniqueMetaDescriptionScore() {
		return uniqueMetaDescriptionScore;
	}

	public void setUniqueMetaDescriptionScore(int uniqueMetaDescriptionScore) {
		this.uniqueMetaDescriptionScore = uniqueMetaDescriptionScore;
	}

	public int getContentUrlScore() {
		return contentUrlScore;
	}

	public void setContentUrlScore(int contentUrlScore) {
		this.contentUrlScore = contentUrlScore;
	}

	public int getContentTitleScore() {
		return contentTitleScore;
	}

	public void setContentTitleScore(int contentTitleScore) {
		this.contentTitleScore = contentTitleScore;
	}

	public int getContentH1Score() {
		return contentH1Score;
	}

	public void setContentH1Score(int contentH1Score) {
		this.contentH1Score = contentH1Score;
	}

	public int getContentSRPH1Score() {
		return contentSRPH1Score;
	}

	public void setContentSRPH1Score(int contentSRPH1Score) {
		this.contentSRPH1Score = contentSRPH1Score;
	}

	public int getContentMetaDescriptionScore() {
		return contentMetaDescriptionScore;
	}

	public void setContentMetaDescriptionScore(int contentMetaDescriptionScore) {
		this.contentMetaDescriptionScore = contentMetaDescriptionScore;
	}

	public int getLengthTitleScore() {
		return lengthTitleScore;
	}

	public void setLengthTitleScore(int lengthTitleScore) {
		this.lengthTitleScore = lengthTitleScore;
	}

	public int getLengthMetaDescriptionScore() {
		return lengthMetaDescriptionScore;
	}

	public void setLengthMetaDescriptionScore(int lengthMetaDescriptionScore) {
		this.lengthMetaDescriptionScore = lengthMetaDescriptionScore;
	}

	public int getAltImageScore() {
		return altImageScore;
	}

	public void setAltImageScore(int altImageScore) {
		this.altImageScore = altImageScore;
	}

	public int getUrlReadableScore() {
		return urlReadableScore;
	}

	public void setUrlReadableScore(int urlReadableScore) {
		this.urlReadableScore = urlReadableScore;
	}
	
	public Set<InventoryNumber> getInventoryNumbers() {
		return inventoryNumbers;
	}

	public void setInventoryNumbers(Set<InventoryNumber> inventoryNumbers) {
		this.inventoryNumbers.clear();
		this.inventoryNumbers.addAll(inventoryNumbers);
	}
	public void addInventoryNumbers(Set<InventoryNumber> inventoryNumbers) {
		this.inventoryNumbers.addAll(inventoryNumbers);
	}

	public Set<WPAttribution> getWpAttributions() {
		return wpAttributions;
	}

	public void setWpAttributions(Set<WPAttribution> wpAttributions) {
		this.wpAttributions = wpAttributions;
	}

	public void initPageData() {
		pageCrawls.size();
	}
	
	public void initSiteCrawlData() {
		allLinks.size();
		intrasiteLinks.size();
		uniqueCrawledPageUrls.size();
		crawledUrls.size();
		failedUrls.size();
		webProviders.size();
		schedulers.size();
		generalMatches.size();
		extractedStrings.size();
		extractedUrls.size();
		allStaff.size();
		fbPages.size();
		metatags.size();
		imageTags.size();
		inventoryNumbers.size();
	}
	
	public void initAll() {
		initPageData();
		initSiteCrawlData();
	}
	
}
