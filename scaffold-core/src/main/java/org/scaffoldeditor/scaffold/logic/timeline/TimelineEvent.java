package org.scaffoldeditor.scaffold.logic.timeline;

import org.json.JSONObject;

/**
 * Represents a single event in a timeline.
 */
public class TimelineEvent implements Comparable<TimelineEvent> {
	public final int frame;
	public final String name;
	
	/**
	 * Create a new timeline event.
	 * @param frame Event frame.
	 * @param name Event name.
	 */
	public TimelineEvent(int frame, String name) {
		this.frame = frame;
		this.name = name;
	}

	@Override
	public int compareTo(TimelineEvent o) {
		return frame - o.frame;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!obj.getClass().isAssignableFrom(this.getClass())) {
			return false;
		} else {
			TimelineEvent other = (TimelineEvent) obj;
			return (other.frame == this.frame && other.name.matches(this.name));
		}
	}
	
	public JSONObject serialize() {
		JSONObject object = new JSONObject();
		object.put("frame", frame);
		object.put("name", name);
		return object;
	}
	
	public static TimelineEvent unserialize(JSONObject object) {
		return new TimelineEvent(object.getInt("frame"), object.getString("name"));
	}
}
