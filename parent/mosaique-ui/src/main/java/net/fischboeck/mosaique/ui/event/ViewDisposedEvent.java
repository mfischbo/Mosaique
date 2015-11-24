package net.fischboeck.mosaique.ui.event;

public class ViewDisposedEvent<T> {

	private T object;
	
	public ViewDisposedEvent(T type) {
		this.object = type;
	}
	
	public T getObject() {
		return object;
	}
}
