package async.tasks;

import java.util.Map.Entry;

import async.registration.ContextItem;
import async.registration.RegistryEntry;
import async.registration.WorkerRegistry;
import async.work.WorkOrder;
import async.work.WorkResult;
import persistence.tasks.Task;

public class TaskWorkUtil {

	public static WorkOrder buildWorkOrder(Task task) {
		WorkOrder workOrder = new WorkOrder(task.getWorkType());
		
		RegistryEntry entry = WorkerRegistry.getInstance().getRegistrant(workOrder.getWorkType());
		
		for(ContextItem requiredItem : entry.getRequiredContextItems()){
			String name = requiredItem.getName();
			boolean nullable = requiredItem.isNullable();
			
			String item = task.getContextItem(name);
			if(item == null && nullable == false){
				throw new IllegalStateException("Cannot build WorkOrder for task " + task.getTaskId() + " without context item : " + name);
			}
			workOrder.addContextItem(name, item);
		}
		return workOrder;
	}
	
	public static void importResultContextItems(WorkResult workResult, Task task) {
		for(Entry<String, String> entry : workResult.getContextItems().entrySet()){
			task.addContextItem(entry.getKey(), entry.getValue());
		}
	}
}
