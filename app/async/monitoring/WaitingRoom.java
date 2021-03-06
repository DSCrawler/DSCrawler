package async.monitoring;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import akka.actor.ActorRef;
import newwork.WorkOrder;

public class WaitingRoom {
	
	private final Map<Long, ActorRef> customers = Collections.synchronizedMap(new HashMap<Long, ActorRef>());
	private final LinkedList<BackOrder> backOrders = new LinkedList<BackOrder>();
	
	private String name = "Default Waiting Room";
	private Long uuid = UUID.randomUUID().getLeastSignificantBits();
	private int totalAdded = 0;
	private int totalFinished = 0;
	private WaitingRoomStatus roomStatus = WaitingRoomStatus.ACTIVE;
	
	public enum WaitingRoomStatus {
		ACTIVE, PAUSED, FINISHED
	}
	
	public class BackOrder {
		final WorkOrder workOrder;
		final ActorRef customer;
		BackOrder(WorkOrder workOrder, ActorRef customer) {
			this.workOrder = workOrder;
			this.customer = customer;
		}
		public WorkOrder getWorkOrder() {
			return workOrder;
		}
		public ActorRef getCustomer() {
			return customer;
		}
	}
	
	public WaitingRoom() {
		this.name = "Unnamed waiting room " + uuid;
	}
	
	public WaitingRoom(String name) {
		this.name = name;
	}
	
	//Functionally act like adding to a set
	public  boolean add(Long uuid, ActorRef customer){
//		System.out.println("Customer entered waiting room : " + uuid);  
		synchronized (customers){
			if(customers.containsKey(uuid)) {
				return false;
			}
			customers.put(uuid, customer);
			totalAdded++;
			return true;
		}
	}
	
	public ActorRef remove(Long uuid){
//		System.out.println("Customer leaving waiting room : " + uuid);
		synchronized(customers) {
			if(customers.containsKey(uuid)){
				totalFinished++;
			}
			return customers.remove(uuid);
		}
	}

	public  boolean contains(ActorRef customer) {
		return customers.containsKey(customer);
	}
	
	public  int size() {
		return customers.size();
	}
	
	public int getTotalAdded(){
		return totalAdded;
	}
	
	public int getTotalFinished(){
		return totalFinished;
	}
	
	public  void clear() {
		customers.clear();
	}
	
	public boolean isEmpty(){
		return size() < 1;
	}

	public WaitingRoomStatus getRoomStatus() {
		return roomStatus;
	}

	public void setRoomStatus(WaitingRoomStatus roomStatus) {
		this.roomStatus = roomStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean storeBackOrder(WorkOrder workOrder, ActorRef customer){
		BackOrder backOrder = new BackOrder(workOrder, customer);
		synchronized(backOrders){
			return backOrders.add(backOrder);
		}
	}
	
	public BackOrder retrieveBackOrder(){
		synchronized(backOrders){
			return backOrders.remove();
		}
	}
	
	public int backOrderCount(){
		synchronized(backOrders){
			return backOrders.size();
		}
	}
}
