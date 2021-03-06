package async.functionalwork;

import java.util.function.Consumer;

import newwork.WorkResult;

public class ConsumerWorkOrder<T> extends FunctionalWorkOrder<Consumer<T>> {

	protected T input;
	protected Consumer<T> consumer;
	
	public ConsumerWorkOrder(Consumer<T> consumer, T input){
		this.input = input;
		this.consumer = consumer;
	}
	
	@Override
	public WorkResult doWork() {
		consumer.accept(input);
		return new WorkResult(this);
	}

}
