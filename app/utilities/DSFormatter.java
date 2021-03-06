package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import datadefinitions.InvalidDomain;
import datadefinitions.NoCrawlDomain;
import datadefinitions.StringExtraction;
import datadefinitions.StringMatchUtils;
import datadefinitions.HomepagePath;
import datadefinitions.ValidQueryMatch;
import datadefinitions.newdefinitions.DefunctDomain;
import datadefinitions.newdefinitions.DefunctPath;
import datadefinitions.newdefinitions.SharedDomain;
import play.Logger;

public class DSFormatter {

	final static String HAS_HTTP_REGEX= "(?i:http.*)";
	private final static String OK_ENDINGS_REGEX = ".*(?i:index\\.htm|index\\.html|index\\.cfm|index\\.asp|index\\.php|index\\.shtml|default\\.htm|default\\.html|"
			+ "default\\.asp|default\\.aspx|default\\.cfm|default\\.php|home/|Home\\.aspx|home\\.html)";
	final static String SLASHLESS_DOMAIN = "(?i:\\.com|\\.net|\\.biz|\\.us|\\.cc|\\.org|\\.info|\\.ca|\\.me|\\.car)";
	final static String SLASHED_DOMAIN = "(?i:\\.com/|\\.net/|\\.biz/|\\.us/|\\.cc/|\\.org/|\\.info/|\\.ca/|\\.me/|\\.car/)";
	private final static String SLASHLESS_DOMAIN_ENDING = ".*" + SLASHLESS_DOMAIN + "$";
	private final static String SLASHED_DOMAIN_ENDING = ".*" + SLASHED_DOMAIN + "$";
	
	private final static String WINDOWS_ILLEGAL_CHARACTERS = "[\\/:\"*?<>|]+";
	
	private final static Map<String, String> fullToAbbr = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	private final static Map<String, String> abbrToFull = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	static{
		fullToAbbr.put("Alabama","AL");
		fullToAbbr.put("Alaska","AK");
		fullToAbbr.put("Alberta","AB");
		fullToAbbr.put("American Samoa","AS");
		fullToAbbr.put("Arizona","AZ");
		fullToAbbr.put("Arkansas","AR");
		fullToAbbr.put("Armed Forces (AE)","AE");
		fullToAbbr.put("Armed Forces Americas","AA");
		fullToAbbr.put("Armed Forces Pacific","AP");
		fullToAbbr.put("British Columbia","BC");
		fullToAbbr.put("California","CA");
		fullToAbbr.put("Colorado","CO");
		fullToAbbr.put("Connecticut","CT");
		fullToAbbr.put("Delaware","DE");
		fullToAbbr.put("District Of Columbia","DC");
		fullToAbbr.put("Florida","FL");
		fullToAbbr.put("Georgia","GA");
		fullToAbbr.put("Guam","GU");
		fullToAbbr.put("Hawaii","HI");
		fullToAbbr.put("Idaho","ID");
		fullToAbbr.put("Illinois","IL");
		fullToAbbr.put("Indiana","IN");
		fullToAbbr.put("Iowa","IA");
		fullToAbbr.put("Kansas","KS");
		fullToAbbr.put("Kentucky","KY");
		fullToAbbr.put("Louisiana","LA");
		fullToAbbr.put("Maine","ME");
		fullToAbbr.put("Manitoba","MB");
		fullToAbbr.put("Maryland","MD");
		fullToAbbr.put("Massachusetts","MA");
		fullToAbbr.put("Michigan","MI");
		fullToAbbr.put("Minnesota","MN");
		fullToAbbr.put("Mississippi","MS");
		fullToAbbr.put("Missouri","MO");
		fullToAbbr.put("Montana","MT");
		fullToAbbr.put("Nebraska","NE");
		fullToAbbr.put("Nevada","NV");
		fullToAbbr.put("New Brunswick","NB");
		fullToAbbr.put("New Hampshire","NH");
		fullToAbbr.put("New Jersey","NJ");
		fullToAbbr.put("New Mexico","NM");
		fullToAbbr.put("New York","NY");
		fullToAbbr.put("Newfoundland","NF");
		fullToAbbr.put("North Carolina","NC");
		fullToAbbr.put("North Dakota","ND");
		fullToAbbr.put("Northwest Territories","NT");
		fullToAbbr.put("Nova Scotia","NS");
		fullToAbbr.put("Nunavut","NU");
		fullToAbbr.put("Ohio","OH");
		fullToAbbr.put("Oklahoma","OK");
		fullToAbbr.put("Ontario","ON");
		fullToAbbr.put("Oregon","OR");
		fullToAbbr.put("Pennsylvania","PA");
		fullToAbbr.put("Prince Edward Island","PE");
		fullToAbbr.put("Puerto Rico","PR");
		fullToAbbr.put("Quebec","QC");
		fullToAbbr.put("Rhode Island","RI");
		fullToAbbr.put("Saskatchewan","SK");
		fullToAbbr.put("South Carolina","SC");
		fullToAbbr.put("South Dakota","SD");
		fullToAbbr.put("Tennessee","TN");
		fullToAbbr.put("Texas","TX");
		fullToAbbr.put("Utah","UT");
		fullToAbbr.put("Vermont","VT");
		fullToAbbr.put("Virgin Islands","VI");
		fullToAbbr.put("Virginia","VA");
		fullToAbbr.put("Washington","WA");
		fullToAbbr.put("West Virginia","WV");
		fullToAbbr.put("Wisconsin","WI");
		fullToAbbr.put("Wyoming","WY");
		fullToAbbr.put("Yukon Territory","YT");
		fullToAbbr.keySet().stream()
			.forEach((key) -> abbrToFull.put(fullToAbbr.get(key), key));
	}
	
	public static String toString(Exception e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return e.getMessage() + " : " + sw.toString();
	}
	
	public static boolean equals(String first, String second) {
		if(first == null && second == null)
			return true;
		if(first != null & second == null)
			return false;
		return first.equals(second);
	}
	
	public static String standardizePhone(String phone) {
		if(phone == null) {
			return null;
		}
		phone = phone.replaceAll("[^0-9]", "");
		if(phone.length() == 11 && phone.startsWith("1")){
			phone = phone.substring(1, phone.length());
		}
		return phone;
	}
	
	public static String standardizeStreetAddress(String street){
		if(street == null) {
			return null;
		}
		street = street.toLowerCase();
		street = street.replaceAll("s\\.", "s ");
		street = street.replaceAll("n\\.", "n ");
		street = street.replaceAll("w\\.", "w ");
		street = street.replaceAll("e\\.", "e ");
		street = street.replaceAll("[^a-zA-Z0-9\\s]", "");
		street = street.replaceAll("[\\s]+", " ");
		street = street.replaceAll("\\bus highway", "us");
		street = street.replaceAll("\\bave\\b", "avenue");
		street = street.replaceAll("\\bav\\b", "avenue");
		street = street.replaceAll("\\brd\\b", "road");
		street = street.replaceAll("\\bpkwy\\b", "parkway");
		street = street.replaceAll("\\bst$", "street");
		street = street.replaceAll("\\bct$", "court");
		street = street.replaceAll("\\bblvd\\b", "boulevard");
		street = street.replaceAll("\\brte\\b", "route");
		street = street.replaceAll("\\brt\\b", "route");
		street = street.replaceAll("\\bhwy\\b", "highway");
		street = street.replaceAll("\\btpke\\b", "turnpike");
		street = street.replaceAll("\\btpk\\b", "turnpike");
		street = street.replaceAll("\\bpl\\b", "place");
		street = street.replaceAll("\\bcntry\\b", "country");
		street = street.replaceAll("\\bterr\\b", "terrace");
		street = street.replaceAll("\\bbldg\\b", "building");
		street = street.replaceAll("\\bste\\b", "suite");
		street = street.replaceAll("\\bterr\\b", "terrace");
		street = street.replaceAll("\\bn\\b", "north");
		street = street.replaceAll("\\bs\\b", "south");
		street = street.replaceAll("\\be\\b", "east");
		street = street.replaceAll("\\bw\\b", "west");
		return street;
	}
	
	public static String standardizeState(String state) {
		if(state == null) {
			return null;
		}
		state = state.toUpperCase().trim();
		
		String tempState = abbrToFull.get(state);
		if(tempState != null){
			return state;
		}
		return stateFullToAbbr(state);
	}
	
	public static String standardizeCountry(String country){
		if(country == null) {
			return "Country Unknown";
		}
		return country
				.toLowerCase()
				.trim()
				.replaceAll("\\bus\\b", "United States")
				.replaceAll("\\busa\\b", "United States")
				.replaceAll("\\bu\\.s\\.\\b", "United States")
				.replaceAll("\\bunited states\\b", "United States")
				.replaceAll("\\bcan\\b", "Canada")
				.replaceAll("\\bcanada\\b", "Canada")
				.replaceAll("\\bca\\b", "Canada");
	}
	
	public static String standardizeCanadaPostal(String postal){
		if(postal == null){
			return null;
		}
		return postal.toUpperCase()
				.replace(" ", "");
	}
	
	public static String standardizeZip(String zip) {
		if(zip == null){
			return null;
		}
		String[] parts = zip.trim().split("[ -]");
		while(parts[0].length() < 5){
			parts[0] = "0" + parts[0];
		}
		return parts[0];
	}
	
	public static String stateFullToAbbr(String stateFull){
		return fullToAbbr.getOrDefault(stateFull, "XX");
	}
	
	public static String stateAbbrToFull(String stateAbbr){
		return abbrToFull.getOrDefault(stateAbbr, "Unknown State");
	}
	
	public static String getLastSegment(String original) {
		int lastIndex = original.lastIndexOf("/");
		if(lastIndex > 0){
			return original.substring(lastIndex + 1, original.length());
		}
		else
			return "";
	}
	
	public static String truncate(String original, int length) {
		if(original == null)
			return original;
		
		if(needsTruncation(original, length)){

			for(char testing : original.toCharArray()){
				if(Character.isSurrogate(testing))
					Logger.error("Found surrogate character!");
			}
			if(original.length() > length){
				String trunc = "~" + original.substring(0, length-1); 
				Logger.debug("Truncating String : " + trunc);
				return trunc;
			}
		}
		
		
		return original;
	}
	
	public static boolean isTruncated(String original) {
		if(original.startsWith("TRUNCATED")){
			return true;
		}
		return false;
	}
	
	public static boolean needsTruncation(String original, int length) {
		if(original == null || original.length() <= length)
			return false;
		return true;
	}
	
	public static boolean isEmpty(String original) {
		if(original == null || original.equals(""))
			return true;
		return false;
	}
	
	public static boolean isBlank(String original) {
		if(isEmpty(original) || isEmpty(original.trim()))
			return true;
		return false;
	}
	
	public static String regularize(String original) {
		String changed = UrlUtils.toHttp(original);
		changed = UrlUtils.removeQueryString(changed);
		return changed;
	}
	
	public static boolean isApprovedUrl(String original) {
		if(original.matches(OK_ENDINGS_REGEX))
			return true;
		if(original.matches(SLASHLESS_DOMAIN_ENDING))
			return true;
		if(original.matches(SLASHED_DOMAIN_ENDING))
			return true;
		
		return false;
	}
	
	public static boolean isApprovedPath(String original) {
		if(original == null) {
			return false;
		}
		String path = original.toLowerCase();
		for(HomepagePath match : HomepagePath.values()) {
			if(path.equals(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isApprovedPath(URL url) {
		return isApprovedPath(url.getPath());
	}
	public static boolean isApprovedQuery(String original) {
		if(original == null) {
			return true;
		}
		for(ValidQueryMatch match : ValidQueryMatch.values()) {
			if(original.matches(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isApprovedQuery(URL url) {
		return isApprovedQuery(url.getQuery());
	}
	
	public static boolean isLanguagePath(String original) {
		if(original == null) {
			return false;
		}
		String path = original.toLowerCase();
		for(HomepagePath match : HomepagePath.langValues()) {
			if(path.equals(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isLanguagePath(URL url) {
		return isLanguagePath(url.getPath());
	}
	public static boolean isLanguageQuery(String original) {
		if(original == null) {
			return false;
		}
		String query = original.toLowerCase();
		for(ValidQueryMatch match : ValidQueryMatch.langValues()) {
			if(query.equals(match.definition)) {
				return true;
			}
		}
		return false;
	}
	public static boolean isLanguageQuery(URL url) {
		return isLanguageQuery(url.getQuery());
	}
	
	public static boolean isApprovedDomain(String original) {
		if(original == null) {
			return false;
		}
		String domain = original.toLowerCase();
		for(InvalidDomain match : InvalidDomain.values()) {
			if(domain.matches(match.definition)) {
				return false;
			}
		}
		return true;
	}
	public static boolean isApprovedDomain(URL url) {
		return isApprovedDomain(url.getHost());
	}
	
	public static String sqlify(String original) {
		
		String formatted = original.replaceAll("'", "\\'");original.replaceAll("\\", "\\\\");
		formatted = formatted.replaceAll("\\", "\\\\");
		
		return formatted;
	}
	
	public static String deSqlify(String original) {
		String formatted = original.replaceAll("\\\\", "\\");
		formatted = formatted.replaceAll("\\'", "'");
		
		return formatted;
	}
	
	public static String encode(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new AssertionError("UTF-8 is unknown");
		}
	}
	
	public static String decode(String url) {
		try {
			return URLDecoder.decode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("error decoding");
			throw new AssertionError("UTF-8 is unknown");
		}
	}
	
	public static String makeSafeFilePath(String path) {
		if(path == null || "".equals(path)){
			path = "nofilename" + String.valueOf(System.currentTimeMillis());
		}
		String safe = encode(path);
		safe = safe.replaceAll(WINDOWS_ILLEGAL_CHARACTERS, "_");
		safe = truncate(safe);
		safe = safe.toLowerCase();
		return safe;
	}
	
	public static String truncate(String path) {
		return truncate(path, 80);
	}
	
	public static String parePath(String urlString) {
		try{
			URL url = new URL(urlString);
			String rebuilt = url.getProtocol() + "://" + url.getHost() + "/";
			if(!StringUtils.isEmpty(url.getQuery())){
				rebuilt += "?" + url.getQuery();
			}
			return rebuilt;
		} catch(MalformedURLException e) {
			throw new IllegalArgumentException("Bad urlString, cannot pare path : " + urlString);
		}
	}
	
	public static String pareQuery(String urlString) {
		try{
			URL url = new URL(urlString);
			return url.getProtocol() + "://" + url.getHost() + url.getPath();
		} catch(MalformedURLException e) {
			throw new IllegalArgumentException("Bad urlString, cannot pare query : " + urlString);
		}
	}
	
	public static String getDomain(String url) {
		String domain;
		url = UrlUtils.toHttp(url);
		
		try {
			URL formal = new URL(url);
			domain = formal.getHost();
			domain = UrlUtils.removeWww(domain);
		} catch (MalformedURLException e) {
			domain = "ERROR";
		}
		
		return domain;
	}
	
	public static boolean hasBadUrlCharacters(String urlString) {
		if(urlString.contains("@") || urlString.contains(" ") || urlString.contains("\\n") || urlString.contains("\\t")){
			return true;
		}
		return false;
	}
	
	public static boolean isEmail(String urlString) {
		Matcher matcher = StringExtraction.EMAIL_ADDRESS.getPattern().matcher(urlString);
		if(matcher.find()){
			return true;
		}
		return false;
	}
	
	public static boolean isDefunctDomain(URL url) {
		return StringMatchUtils.equalsAny(DefunctDomain.values(), UrlUtils.removeWww(url.getHost()));
	}
	
	public static boolean isDefunctPath(URL url) {
		return StringMatchUtils.equalsAny(DefunctPath.values(), url.getPath());
	}
	
	public static boolean isNotStandardHomepagePath(URL url) {
		if(!StringUtils.isEmpty(url.getQuery()) && StringUtils.isEmpty(url.getPath())){		//Blank path is only ok with queries
			return false;
		}
		if(isSharedDomain(url)){
			return false;
		}
		return !StringMatchUtils.equalsAny(HomepagePath.values(), url.getPath());
	}
	
	public static boolean isUncrawlableDomain(URL url) {
		return StringMatchUtils.equalsAny(NoCrawlDomain.values(), UrlUtils.removeWww(url.getHost()));
	}
	
	public static boolean isSharedDomain(URL url) {
		return StringMatchUtils.equalsAny(SharedDomain.values(), UrlUtils.removeWww(url.getHost()));
	}
	
	public static boolean isBadQuery(URL url) {
		if(StringUtils.isEmpty(url.getQuery())){
			return false;
		}
		return !StringMatchUtils.matchesAny(ValidQueryMatch.values(), url.toString());		//Send the entire URL text, not just the query, since query matching uses the whole thing
	}
}
