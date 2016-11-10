package agarbagefolder;

import java.util.UUID;

import akka.actor.UntypedActor;
import async.monitoring.AsyncMonitor;
import async.work.TypedWorkOrder;
import async.work.TypedWorkResult;
import play.Logger;

public class SingleStepWorker extends UntypedActor {
	
	protected Long uuid = UUID.randomUUID().getLeastSignificantBits();
	

	@Override
	public void onReceive(Object work) throws Exception {
		try{
			TypedWorkOrder workOrder = (TypedWorkOrder) work;
			System.out.println("Performing work : " + workOrder.getWorkType() + " Thread name : " + Thread.currentThread().getName());
			AsyncMonitor.instance().addWip(workOrder.getWorkType().toString(), uuid);
			TypedWorkResult workResult = processWorkOrder(workOrder);
			AsyncMonitor.instance().finishWip(workOrder.getWorkType().toString(), uuid);
			getSender().tell(workResult, getSelf());
		}
		catch(Exception e){
			Logger.error("Error in Single Step Worker : " + e);
			System.out.println("Error in Single Step Worker : " + e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void postRestart(Throwable reason) throws Exception {
		Logger.error("Worker restarting: " + reason);
		preStart();
	}
	
	public TypedWorkResult processWorkOrder(TypedWorkOrder workOrder) {
		return null;
	}
	
	

}