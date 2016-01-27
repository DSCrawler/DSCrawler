package async.work.googleplaces;

import async.work.WorkOrder;
import async.work.WorkType;

public class PlacesPageWorkOrder extends WorkOrder {

	protected String placesId;
	
	public PlacesPageWorkOrder(String placesId) {
		super(WorkType.PLACES_PAGE_FETCH);
		this.placesId = placesId;
	}

	public String getPlacesId() {
		return placesId;
	}

	public void setPlacesId(String placesId) {
		this.placesId = placesId;
	}
}
