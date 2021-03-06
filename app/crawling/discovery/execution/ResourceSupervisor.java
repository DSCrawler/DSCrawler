package crawling.discovery.execution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.monitoring.WaitingRoom;
import crawling.discovery.entities.Resource;
import crawling.discovery.entities.SourcePool;
import crawling.discovery.entities.SourceQualification;
import crawling.discovery.entities.SourceQualification.QualificationStatus;
import newwork.WorkStatus;

public class ResourceSupervisor extends UntypedActor{

	protected final ResourceContext context;
	
	private final Router router;
	private final WaitingRoom waitingRoom;
	
	public ResourceSupervisor(ResourceContext resourceContext) {
		this.context = resourceContext;
		this.waitingRoom = new WaitingRoom("Waiting room for " + resourceContext);
		
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < resourceContext.getNumWorkers(); i++) {
	      ActorRef r = getContext().actorOf(Props.create(ResourceWorker.class, context).withDispatcher("akka.worker-dispatcher"));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees); 
	}
	
	/************** sub classes should implement these methods ********************/
	
	
	/*************** end sub class encouraged implementations *******************/
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof ResourceWorkOrder){
			processResourceWorkOrder((ResourceWorkOrder) message);
		} else if(message instanceof ResourceWorkResult){
			processResourceWorkResult((ResourceWorkResult) message);
		} else {
			System.out.println("Received unknown message in ResourceSupervisor : " + message);
		}
	}
	
	protected void processResourceWorkResult(ResourceWorkResult workResult) {
		ActorRef sender = waitingRoom.remove(workResult.getUuid());
		if(workResult.getWorkStatus() == WorkStatus.COMPLETE){
//			System.out.println("Work successful in supervisor (plan " + context.getPlanId() + "): " + workResult.getSource());
		}
		sender.tell(workResult, getSelf());
	}
	
	protected void processResourceWorkOrder(ResourceWorkOrder workOrder) {
		assignWork(workOrder);
	}
	
	protected void assignWork(ResourceWorkOrder workOrder){
//		System.out.println("Supervisor assigning source : " + workOrder.getSource());
		if(!waitingRoom.add(workOrder.getUuid(), getSender())){
			System.out.println("duplicate work");
			//TODO figure out what to do when duplicate work order is sent in
			return;
		}
		router.route(workOrder, getSelf());
	}
}
