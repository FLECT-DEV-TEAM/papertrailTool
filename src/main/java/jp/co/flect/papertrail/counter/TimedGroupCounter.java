package jp.co.flect.papertrail.counter;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import jp.co.flect.papertrail.Counter;
import jp.co.flect.papertrail.CounterRow;
import jp.co.flect.papertrail.Event;

public abstract class TimedGroupCounter extends AbstractCounter implements Comparator<String> {
	
	private String allName;
	private Map<String, TimedNumberCounter> map = new HashMap<String, TimedNumberCounter>();
	
	public TimedGroupCounter(String name, String allName) {
		super(name);
		this.allName = allName;
		TimedNumberCounter all = new TimedNumberCounter(allName);
		this.map.put(allName, all);
	}
	
	public Type getType() { return Type.Time;}
	
	protected abstract String getGroupName(Event e);
	protected abstract int getGroupNumber(Event e);
	
	public void add(Event e) {
		String name = getGroupName(e);
		int n = getGroupNumber(e);
		if (name == null || n < 0) {
			return;
		}
		TimedNumberCounter counter = this.map.get(name);
		if (counter == null) {
			counter = new TimedNumberCounter(name);
			this.map.put(name, counter);
		}
		counter.add(e, n);
		this.map.get(this.allName).add(e, n);
	}
	
	public List<CounterRow> getData() {
		ArrayList<CounterRow> ret = new ArrayList<CounterRow>();
		List<String> names = new ArrayList<String>(this.map.keySet());
		Collections.sort(names, this);
		for (String group : names) {
			Counter counter = this.map.get(group);
			ret.addAll(counter.getData());
		}
		return ret;
	}
	
	public String toString(String prefix, String delimita) {
		StringBuilder buf = new StringBuilder();
		buf.append(prefix).append(getName()).append("\n");
		List<String> names = new ArrayList<String>(this.map.keySet());
		Collections.sort(names, this);
		for (String name : names) {
			TimedNumberCounter counter = this.map.get(name);
			buf.append(prefix).append(counter.toString("    ", delimita)).append("\n");
		}
		buf.setLength(buf.length() - 1);
		return buf.toString();
	}
	
	public int compare(String s1, String s2) {
		if (this.allName.equals(s1)) {
			return -1;
		} else if (this.allName.equals(s2)) {
			return 1;
		}
		return s1.compareTo(s2);
	}
	
}