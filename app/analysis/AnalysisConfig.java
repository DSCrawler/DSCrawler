package analysis;


public class AnalysisConfig {
	
	public enum AnalysisMode {
		BLOB, PAGED, MIXED
	}
	
	AnalysisMode analysisMode = AnalysisMode.PAGED;
	
	//************ Matching
	protected Boolean doBrandMatches = false;
	protected Boolean doMetaBrandMatches = false;
	protected Boolean doGeneralMatches = false;
	protected Boolean doSchedulerMatches= false;
	protected Boolean doWpClues = false;
	protected Boolean doWebProviderMatches = false;
	protected Boolean doWpAttributionMatches= false;
	protected Boolean doLinkTextMatches = false;
	
	
	//*********** Inventory
	protected Boolean doNewInventoryPage = false;
	protected Boolean doUsedInventoryPage = false;
	protected Boolean doInventoryNumbers = false;
	
	//************ Extraction
	protected Boolean extractUrls = false;
	protected Boolean extractStrings = false;
	protected Boolean extractLinks = false;
	
	
	
	//************ Conditional Aggregates
	public Boolean needsDoc(){
		return doLinkTextMatches;
	}



	public Boolean getDoBrandMatches() {
		return doBrandMatches;
	}



	public void setDoBrandMatches(Boolean doBrandMatches) {
		this.doBrandMatches = doBrandMatches;
	}



	public Boolean getDoMetaBrandMatches() {
		return doMetaBrandMatches;
	}



	public void setDoMetaBrandMatches(Boolean doMetaBrandMatches) {
		this.doMetaBrandMatches = doMetaBrandMatches;
	}



	public Boolean getDoGeneralMatches() {
		return doGeneralMatches;
	}



	public void setDoGeneralMatches(Boolean doGeneralMatches) {
		this.doGeneralMatches = doGeneralMatches;
	}



	public Boolean getDoSchedulerMatches() {
		return doSchedulerMatches;
	}



	public void setDoSchedulerMatches(Boolean doSchedulerMatches) {
		this.doSchedulerMatches = doSchedulerMatches;
	}



	public Boolean getDoWpClues() {
		return doWpClues;
	}



	public void setDoWpClues(Boolean doWpClues) {
		this.doWpClues = doWpClues;
	}



	public Boolean getDoWebProviderMatches() {
		return doWebProviderMatches;
	}



	public void setDoWebProviderMatches(Boolean doWebProviderMatches) {
		this.doWebProviderMatches = doWebProviderMatches;
	}



	public Boolean getDoWpAttributionMatches() {
		return doWpAttributionMatches;
	}



	public void setDoWpAttributionMatches(Boolean doWpAttributionMatches) {
		this.doWpAttributionMatches = doWpAttributionMatches;
	}



	public Boolean getDoLinkTextMatches() {
		return doLinkTextMatches;
	}



	public void setDoLinkTextMatches(Boolean doLinkTextMatches) {
		this.doLinkTextMatches = doLinkTextMatches;
	}

	public Boolean getDoNewInventoryPage() {
		return doNewInventoryPage;
	}

	public void setDoNewInventoryPage(Boolean doNewInventoryPage) {
		this.doNewInventoryPage = doNewInventoryPage;
	}

	public Boolean getDoUsedInventoryPage() {
		return doUsedInventoryPage;
	}

	public void setDoUsedInventoryPage(Boolean doUsedInventoryPage) {
		this.doUsedInventoryPage = doUsedInventoryPage;
	}

	public Boolean getDoInventoryNumbers() {
		return doInventoryNumbers;
	}

	public void setDoInventoryNumbers(Boolean doInventoryNumbers) {
		this.doInventoryNumbers = doInventoryNumbers;
	}

	public Boolean getExtractUrls() {
		return extractUrls;
	}

	public void setExtractUrls(Boolean extractUrls) {
		this.extractUrls = extractUrls;
	}

	public Boolean getExtractStrings() {
		return extractStrings;
	}

	public void setExtractStrings(Boolean extractStrings) {
		this.extractStrings = extractStrings;
	}

	public Boolean getExtractLinks() {
		return extractLinks;
	}

	public void setExtractLinks(Boolean extractLinks) {
		this.extractLinks = extractLinks;
	}

	public AnalysisMode getAnalysisMode() {
		return analysisMode;
	}

	public void setAnalysisMode(AnalysisMode analysisMode) {
		this.analysisMode = analysisMode;
	}
	
	
}
