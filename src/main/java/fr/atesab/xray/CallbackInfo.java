package fr.atesab.xray;

public class CallbackInfo<T> {
	private boolean cancelled = false;
	private T returnValue;
	public void setReturnValue(T value) {
		cancel();
		returnValue = value;
	}
	
	public T getReturnValue() {
		return returnValue;
	}
	
	public void cancel() {
		this.cancelled = true;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
}
