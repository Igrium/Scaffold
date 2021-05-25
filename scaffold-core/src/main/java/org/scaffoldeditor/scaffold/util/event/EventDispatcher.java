package org.scaffoldeditor.scaffold.util.event;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class which speeds up the process of dispatching events.
 * @author Igrium
 *
 * @param <T> Type of event to fire.
 */
public class EventDispatcher<T> {
	private List<EventListener<T>> listeners = new ArrayList<>();
	
	/**
	 * Add an event listener.
	 * @param listener Listener to add.
	 */
	public void addListener(EventListener<T> listener) {
		listeners.add(listener);
	}
	
	/**
	 * Remove an event listener.
	 * @param listener Listener to remove.
	 * @return True if this dispatcher contained the listener to remove.
	 */
	public boolean removeListener(EventListener<T> listener) {
		return listeners.remove(listener);
	}
	
	/**
	 * Fire the event.
	 * @param event Event instance to fire. Should contain event args.
	 */
	public void fire(T event) {
		for (EventListener<T> l : listeners) {
			l.eventFired(event);
		}
	}
}
