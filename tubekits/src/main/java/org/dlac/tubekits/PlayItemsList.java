package org.dlac.tubekits;

import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("serial")
public class PlayItemsList extends LinkedList<PlayItem> implements Queue<PlayItem> {
	
	public String toJSON() {
		String res = "[ ";
		for (PlayItem playItem : this) {
			res += playItem.toJSON() + ", ";
		}
		if (res.length() > 2) res = res.substring(0, res.length()-2);
		res += " ]";
		return res;
	}
}
