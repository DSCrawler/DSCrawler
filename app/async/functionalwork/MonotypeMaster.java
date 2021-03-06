package async.functionalwork;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.DeadLetterActorRef;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import async.monitoring.Lobby;
import async.monitoring.WaitingRoom;
import async.monitoring.WaitingRoom.WaitingRoomStatus;
import newwork.MasterWorkOrder;
import newwork.TerminateWhenFinished;
import newwork.WorkOrder;
import newwork.WorkResult;
import newwork.WorkStatus;
import places.CanadaPostal;
import places.ZipLocation;
import play.Logger;

public class MonotypeMaster extends UntypedActor {
	 

	protected final int numWorkers;
	protected Router router;
	protected Class<?> clazz;
	protected WaitingRoom waitingRoom;
	protected ActorRef notifyWhenFinished = null;
	
	public MonotypeMaster(int numWorkers, Class<?> clazz) {
		this.clazz = clazz;
		System.out.println("Monotype master starting with " + numWorkers + " workers of type " + clazz);
		this.numWorkers = numWorkers;
		
		this.waitingRoom = new WaitingRoom("Waiting room for " + clazz);
		Lobby.add(waitingRoom);
		List<Routee> routees = new ArrayList<Routee>();
	    for (int i = 0; i < this.numWorkers; i++) {
	      ActorRef r = getContext().actorOf(Props.create(clazz).withDispatcher("akka.worker-dispatcher"));
	      getContext().watch(r);
	      routees.add(new ActorRefRoutee(r));
	    }
	    router = new Router(new RoundRobinRoutingLogic(), routees); 
	    
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
//		System.out.println("received message");
		if(message instanceof WorkOrder){
			processWorkOrder((WorkOrder) message);
		} else if(message instanceof WorkResult) {
			processWorkResult((WorkResult) message);
		}else if (message instanceof ZipLocation || message instanceof CanadaPostal){
			router.route(message, getSelf());
		} else if(message instanceof Terminated) {
			Logger.error("Monotype Master (" + this.clazz + ") received terminated worker");
			router = router.removeRoutee(((Terminated) message).actor());
			ActorRef worker = getContext().actorOf(Props.create(this.clazz));
			getContext().watch(worker);
			router = router.addRoutee(new ActorRefRoutee(worker));
		}
	}
	
	public void processWorkOrder(WorkOrder workOrder){
//		System.out.println("processing work order : " + workOrder.getUuid());
		if(workOrder instanceof MasterWorkOrder){
			doWork((MasterWorkOrder) workOrder);
		} else if(workOrder instanceof TerminateWhenFinished){
			this.notifyWhenFinished = getSender();
		} else {
			assignWork(workOrder, getSender());
		}
	}
	
	public void assignWork(WorkOrder workOrder, ActorRef customer) {
//		System.out.println("Assigning work");
		if(!waitingRoom.add(workOrder.getUuid(), customer)){
			System.out.println("duplicate work");
			//TODO figure out what to do when duplicate work order is sent in
			return;
		}
		router.route(workOrder, getSelf());
	}
	
	public void doWork(MasterWorkOrder workSet){
		throw new UnsupportedOperationException("Default Monotype Master cannot do work.  Extend the class to add this functionality");
	}
	
	public void processWorkResult(WorkResult workResult) {
		ActorRef customer = waitingRoom.remove(workResult.getUuid());
		if(workResult.getWorkStatus() == WorkStatus.ERROR){
			Logger.error("Work resulted in error : " + workResult.getError());
			System.out.println("***********************Work resulted in error!  Check logs for details.");
		}
		if(customer == null || customer.equals(getSelf()) || DeadLetterActorRef.class.isAssignableFrom(customer.getClass())){
			//TODO figure out what to do when customer didn't leave a number
		}else {
			customer.tell(workResult, getSelf());
		}
		if(waitingRoom.size() == 0 && this.notifyWhenFinished != null) {
			doShutdown();
		}
	}
	
	public void doShutdown(){
		System.out.println("Shutting down MonotypeMaster and children");
		waitingRoom.setRoomStatus(WaitingRoomStatus.FINISHED);
		context().stop(getSelf());
	}
}
