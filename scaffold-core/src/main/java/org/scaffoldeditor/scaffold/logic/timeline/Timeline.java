package org.scaffoldeditor.scaffold.logic.timeline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Represents a game timeline.
 * @author Igrium
 */
public class Timeline implements Iterable<TimelineEvent> {

	protected final List<TimelineEvent> events = new ArrayList<TimelineEvent>();
	
	/**
	 * Create an empty timeline.
	 */
	public Timeline() {
		
	}
	
	/**
	 * Create a timeline from a map of events.
	 * @param events Events.
	 */
	public Timeline(Map<Integer, String> events) {
		for (int frame : events.keySet()) {
			this.events.add(new TimelineEvent(frame, events.get(frame)));
		}
	}
	
	/**
	 * Create a timeline from a list of events; each frame from the event index in the list.
	 * @param events Event list.
	 */
	public Timeline(List<TimelineEvent> events) {
		for (int i = 0; i < events.size(); i++) {
			this.events.add(events.get(i));
		}
	}

	@Override
	public Iterator<TimelineEvent> iterator() {
		Collections.sort(events);
		
		return new Iterator<TimelineEvent>() {	
			int index = 0;
			
			@Override
			public boolean hasNext() {
				return index < events.size();
			}

			@Override
			public TimelineEvent next() {
				TimelineEvent e = events.get(index);
				index++;
				return e;
			}
			
		};
	}
	
	/**
	 * Get all the events on a particular frame.
	 * @param frame Frame to search.
	 * @return Events on frame.
	 */
	public Collection<TimelineEvent> get(int frame) {
		Collection<TimelineEvent> frameEvents = new ArrayList<TimelineEvent>();
		for (TimelineEvent e : this) {
			if (e.frame == frame) {
				frameEvents.add(e);
			}
		}
		
		return frameEvents;
	}
	
	/**
	 * Add a new TimelineEvent.
	 * @param event Event to add.
	 */
	public void put(TimelineEvent event) {
		events.add(event);
	}
	
	/**
	 * Add a new event.
	 * @param frame Frame to add to.
	 * @param name Event name.
	 */
	public void put(int frame, String name) {
		events.add(new TimelineEvent(frame, name));
	}
	
	/**
	 * Remove an event from the timeline.
	 * @param event
	 */
	public void remove(TimelineEvent event) {
		events.removeAll(Collections.singleton(event));
	}
	
	/**
	 * Remove an event from the timeline.
	 * @param frame Frame of event to remove.
	 * @param name Name of event to remove.
	 */
	public void remove(int frame, String name) {
		remove(new TimelineEvent(frame, name));
	}
	
	/**
	 * Remove all events with a matching name.
	 * @param name Name of events to remove.
	 */
	public void removeAll(String name) {
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i).name.matches(name)) {
				events.remove(i);
				i--;
			}
		}
	}
	
	/**
	 * Clear all events at a certian frame.
	 * @param frame Frame to clear.
	 */
	public void clearFrame(int frame) {
		if (frame < 0) {
			return;
		}
		
		for (TimelineEvent e : this) {
			if (e.frame == frame) {
				remove(e);
			}
		}
	}
	
	/**
	 * Clear all events from the timeline.
	 */
	public void clear() {
		events.clear();
	}
	
	/**
	 * Get the length of the timeline (number AFTER last frame with an event)
	 * @return Timeline length.
	 */
	public int length() {
		int greatestFrame = 0;
		
		for (TimelineEvent e : this) {
			if (e.frame > greatestFrame) {
				greatestFrame = e.frame;
			}
		}
		
		return greatestFrame+1;
	}
	
	/**
	 * Serialize this timeline into a JSONObject.
	 * @return Serialized timeline.
	 */
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		
		JSONArray events = new JSONArray();
		for (TimelineEvent e : this) {
			events.put(e.serialize());
		}
		object.put("events", events);
		
		return object;
	}
	
	/**
	 * Unserialize a Timeline from a JSONObject.
	 * @param object Object to unserialize.
	 * @return Unserialized timeline.
	 */
	public static Timeline unserialize(JSONObject object) {
		Timeline timeline = new Timeline();
		
		JSONArray events = object.getJSONArray("events");
		for (Object o : events) {
			JSONObject event = (JSONObject) o;
			if (event != null) {
				timeline.put(TimelineEvent.unserialize(event));
			}
		}
		
		return timeline;
	}
}
