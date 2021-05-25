package org.scaffoldeditor.scaffold.util.event;

public interface EventListener<T> extends java.util.EventListener {
	void eventFired(T event);
}
