package analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import datadefinitions.GeneralMatch;
import datadefinitions.OEM;
import datadefinitions.StringExtraction;
import datadefinitions.newdefinitions.InventoryType;
import datadefinitions.newdefinitions.TestMatch;
import datadefinitions.newdefinitions.WPAttribution;
import global.Global;
import persistence.ImageTag;
import persistence.InventoryNumber;
import persistence.Metatag;
import persistence.PageCrawl;
import persistence.SiteCrawl;

public class PageCrawlAnalyzer {
	
	public static void runPageCrawlAnalysis(SiteCrawlAnalysis siteAnalysis, PageCrawlAnalysis pageAnalysis) throws IOException {
		AnalysisConfig config = siteAnalysis.getConfig();
		String filename = Global.getCrawlStorageFolder() + siteAnalysis.getSiteCrawl().getStorageFolder() + "/" + pageAnalysis.getPageCrawl().getFilename();
		FileInputStream inputStream = new FileInputStream(filename);
        String text = IOUtils.toString(inputStream, "UTF-8");
        inputStream.close();
		
        runTextAnalysis(config, pageAnalysis, text);
		if(config.needsDoc()){
			runDocAnalysis(config, pageAnalysis, text);
		}
		
	}
	
	public static void runTextAnalysis(AnalysisConfig config, PageCrawlAnalysis pageAnalysis, String text) {
		
		if(config.getDoGeneralMatches()){
			pageAnalysis.getGeneralMatches().addAll(TextAnalyzer.getGeneralMatches(text));
		} if(config.getDoWpAttributionMatches()){
			pageAnalysis.getWpAttributions().addAll(TextAnalyzer.getMatches(text, WPAttribution.values()));
		} if(config.getDoTestMatches()){
			pageAnalysis.getTestMatches().addAll(TextAnalyzer.getMatches(text, TestMatch.getCurrentMatches()));
		} if(config.getDoCustomText()){
			customText(config, pageAnalysis, text);
		}
	}
	
	public static void runDocAnalysis(AnalysisConfig config, PageCrawlAnalysis pageAnalysis, String text) {
		Document doc = Jsoup.parse(text);
		if(config.getDoLinkTextMatches()){
			pageAnalysis.getLinkTextMatches().addAll(DocAnalyzer.getLinkTextMatches(doc));
		} if(config.getDoCustomDoc()){
			customDoc(config, pageAnalysis, doc);
		}
	}
	
	public static void customText(AnalysisConfig config, PageCrawlAnalysis pageAnalysis, String text) {
		Set<WPAttribution> canadaProviders = new HashSet<WPAttribution>();
		canadaProviders.add(WPAttribution.VIN_SOLUTIONS);
		canadaProviders.add(WPAttribution.VIN_SOLUTIONS2);
		canadaProviders.add(WPAttribution.E_DEALER_CA);
	}
	
	public static void customDoc(AnalysisConfig config, PageCrawlAnalysis pageAnalysis, Document doc) {
		
	}
	
	
	
	
	
	
	
	public static void fullAnalysis(PageCrawl pageCrawl) throws IOException{
		fullAnalysis(pageCrawl, pageCrawl.getSiteCrawl());
	}
	
	public static void fullAnalysis(PageCrawl pageCrawl, SiteCrawl siteCrawl) throws IOException{
		if(StringUtils.isEmpty(pageCrawl.getFilename())){
			return;
		}
		
		File file = new File(Global.getCrawlStorageFolder() + "/" + siteCrawl.getStorageFolder() + "/" + pageCrawl.getFilename());
		if(file.isFile() && !FilenameUtils.getExtension(file.getName()).equals("ser")) {
			FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
	        String text = IOUtils.toString(inputStream, "UTF-8");
	        inputStream.close();
	        
	        pageCrawl.setLargeFile(file.length() > Global.getLargeFileThreshold());
	        
	        textAnalysis(pageCrawl, text);
	        docAnalysis(pageCrawl, text);
	        postDocAnalysis(pageCrawl, text);
	        metaAnalysis(pageCrawl);
		}
	}
	

	
	
	
	/************************  Text Analysis  **************************/
	
	public static void textAnalysis(PageCrawl pageCrawl, String text){
		getInventoryNumbers(pageCrawl, text);
	}	
	
	public static void getInventoryNumbers(PageCrawl pageCrawl, String text) {
//		System.out.println("getting inventory numbers");
		InventoryNumber invNumber = null;
		for(InventoryType enumElement : InventoryType.values()){
			Matcher matcher = enumElement.getPattern().matcher(text);
	    	while (matcher.find()) {
	    		if(invNumber != null){
	    			throw new IllegalStateException("Found multiple inventory values for a single page with id : " + pageCrawl.getPageCrawlId());
	    		}
	    		if(pageCrawl.getInventoryNumber() != null){
	    			invNumber = pageCrawl.getInventoryNumber();
	    			
	    		}else {
	    			invNumber = new InventoryNumber();
	    		}
	    		invNumber.setInventoryType(enumElement);
	    		invNumber.setCount(Integer.parseInt(matcher.group(1)));
	    		pageCrawl.setInventoryNumber(invNumber);
	    	}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/************************  Doc Analysis *****************************/
	
	public static void docAnalysis(PageCrawl pageCrawl, String text) {
		Document doc = Jsoup.parse(text);
		fillImages(pageCrawl, doc);
		fillMetatags(pageCrawl, doc);
	}
	
	public static void fillImages(PageCrawl pageCrawl, Document doc) {
		Elements images = doc.select("img");
		pageCrawl.setNumImages(images.size());
		for(Element image : images) {
			ImageTag imageTag = new ImageTag();
			imageTag.setRaw(image.outerHtml());
			imageTag.setAlt(image.attr("alt"));
			imageTag.setSrc(image.attr("src"));
			pageCrawl.getImageTags().add(imageTag);
		}
	}
	
	public static void fillMetatags(PageCrawl pageCrawl, Document doc) {
		Elements metatags = doc.select("meta");
		for(Element tagElement : metatags) {
			Metatag tagEntity = new Metatag();
			tagEntity.setContent(tagElement.attr("content"));
			tagEntity.setHttpEquiv(tagElement.attr("http-equiv"));
			tagEntity.setItemprop(tagElement.attr("itemprop"));
			tagEntity.setName(tagElement.attr("name"));
			tagEntity.setProperty(tagElement.attr("property"));
			tagEntity.setCharset(tagElement.attr("character_set"));
			tagEntity.setScheme(tagElement.attr("scheme"));
			tagEntity.setRaw(tagElement.outerHtml());
			pageCrawl.getMetatags().add(tagEntity);
			
			if(StringUtils.equals(tagEntity.getName(), "description") && pageCrawl.getMetaDescription() == null){
				pageCrawl.setMetaDescription(tagEntity);
			}
			if(StringUtils.equals(tagEntity.getName(), "title") && pageCrawl.getMetaTitle() == null){
				pageCrawl.setMetaTitle(tagEntity);
			}
		}
	}
	
	public static void fillH1AndTitle(PageCrawl pageCrawl, Document doc) {
		Elements headers = doc.select("h1");
		if(headers.size() > 0) {
			Element h1 = headers.first();
			pageCrawl.setH1(h1.text());
		}
		Elements titles = doc.select("title");
		if(titles.size() > 0) {
			Element title = titles.first();
			pageCrawl.setTitle(title.text());
		}
	}
	
	
	
	
	
	
	
	
	
	/************ Post Doc Analysis *********************************/
	public static void postDocAnalysis(PageCrawl pageCrawl, String text){
		getBrandMatchCounts(pageCrawl, text);
	}
	
	public static void getBrandMatchCounts(PageCrawl pageCrawl, String text){
		for(OEM oem : OEM.values()){
			Integer metaCount = 0;
			for(Metatag metatag : pageCrawl.getMetatags()){
				metaCount += StringUtils.countMatches(metatag.getRaw(), oem.definition);
			}
			pageCrawl.getMetaBrandMatchCounts().put(oem, metaCount);
			Integer count = StringUtils.countMatches(text, oem.definition);
			pageCrawl.getBrandMatchCounts().put(oem, count);
		}
	}
	
	
	
	
	/*************  Meta Analysis ******************************/
	public static void metaAnalysis(PageCrawl pageCrawl){
		countImages(pageCrawl);
		checkForMakes(pageCrawl);
		checkForStates(pageCrawl);
//		checkForCities(pageCrawl);
		checkLengths(pageCrawl);
	}
	
	public static void countImages(PageCrawl pageCrawl){
		int total = 0;
		int alt = 0;
		for(ImageTag image : pageCrawl.getImageTags()){
			total++;
			if(!StringUtils.isEmpty(image.getAlt())){
				alt++;
			}
		}
		pageCrawl.setNumImages(total);
		pageCrawl.setNumAltImages(alt);
	}
	
	public static void checkLengths(PageCrawl pageCrawl) {
		int length = StringUtils.length(pageCrawl.getTitle());
		if(pageCrawl.getTitle() != null && length >=50 && length <= 65) {
			pageCrawl.setTitleLength(true);
		}
		if(pageCrawl.getMetaDescription() != null){
			if(pageCrawl.getMetaDescription().getContent() != null && StringUtils.length(pageCrawl.getMetaDescription().getContent()) < 120){
				pageCrawl.setDescriptionLength(true);
			}
		}
	}
	
	public static void checkForMakes(PageCrawl pageCrawl){
		pageCrawl.setUrlMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getUrl()));
		pageCrawl.setTitleMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getTitle()));
		pageCrawl.setH1MakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getH1()));
		if(pageCrawl.getMetaDescription() != null){
			pageCrawl.setMetaDescriptionMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getMetaDescription().getContent()));
		}
		else {
			pageCrawl.setMetaDescriptionMakeQualifier(false);
		}
	}
	
	public static void checkForStates(PageCrawl pageCrawl) {
		pageCrawl.setUrlStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getUrl()));
		pageCrawl.setTitleStateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getTitle()));
		pageCrawl.setH1StateQualifier(AnalysisUtils.matchesPattern(StringExtraction.STATE_ABBR.getPattern(), pageCrawl.getH1()));
		if(pageCrawl.getMetaDescription() != null){
			pageCrawl.setMetaDescriptionMakeQualifier(AnalysisUtils.matchesPattern(StringExtraction.MAKE.getPattern(), pageCrawl.getMetaDescription().getContent()));
		}
		else {
			pageCrawl.setMetaDescriptionMakeQualifier(false);
		}
	}
	
	public static void checkForCities(PageCrawl pageCrawl){
		Set<String> cities = pageCrawl.getSiteCrawl().getSite().getCities();
		for(String city : cities) {
			Pattern cityPattern = Pattern.compile(city);
			boolean url = AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getUrl());
			boolean title = AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getTitle());
			boolean h1 = AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getH1());
			boolean meta = pageCrawl.getMetaDescription() != null && AnalysisUtils.matchesPattern(cityPattern, pageCrawl.getMetaDescription().getContent());

			if(url){
				pageCrawl.setUrlCityQualifier(true);
			}
			if(title){
				pageCrawl.setTitleCityQualifier(true);
			}
			if(h1){
				pageCrawl.setH1CityQualifier(true);
			}
			if(meta){
				pageCrawl.setMetaDescriptionCityQualifier(true);
			}
		}
	}
}
