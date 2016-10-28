package agarbagefolder.textanalysis;

import async.work.TypedWorkOrder;
import async.work.WorkType;

public class TextAnalysisWorkOrder extends TypedWorkOrder {

	private Long siteCrawlId;
	
	public TextAnalysisWorkOrder() {
		super(WorkType.TEXT_ANALYSIS);
	}
	public TextAnalysisWorkOrder(Long siteCrawlId) {
		super(WorkType.TEXT_ANALYSIS);
		this.siteCrawlId = siteCrawlId;
	}

	public Long getSiteCrawlId() {
		return siteCrawlId;
	}

	public void setSiteCrawlId(Long siteCrawlId) {
		this.siteCrawlId = siteCrawlId;
	}
}
