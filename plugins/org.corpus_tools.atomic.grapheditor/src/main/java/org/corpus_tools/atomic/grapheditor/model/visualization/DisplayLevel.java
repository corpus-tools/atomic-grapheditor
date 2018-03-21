/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model.visualization;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class DisplayLevel {

	private final List<DisplaySpan> displaySpans;
	private final BitSet occupancySet;
	private final Set<String> textIDs;

	public DisplayLevel() {
		this.displaySpans = new ArrayList<>();
		this.occupancySet = new BitSet();
		this.textIDs = new HashSet<>();
	}

	public boolean addDisplaySpan(DisplaySpan event) {
		BitSet eventOccupance = new BitSet(event.getRight().intValue());
		eventOccupance.set(event.getLeft().intValue(), event.getRight().intValue() + 1, true);
		if (occupancySet.intersects(eventOccupance)) {
			return false;
		}
		// set all bits to true that are covered by the other event
		occupancySet.or(eventOccupance);
		displaySpans.add(event);

		if (event.getId() != null && !event.getId().isEmpty()) {
			textIDs.add(event.getId());
		}

		return true;
	}

	public boolean merge(DisplayLevel other) throws IllegalArgumentException {
		if (canMerge(other)) {
			occupancySet.or(other.occupancySet);
			for (DisplaySpan e : other.displaySpans) {
				displaySpans.add(e);
			}
			return true;
		}
		else {
			return false;
		}
	}

	private boolean canMerge(DisplayLevel other) {
		return !occupancySet.intersects(other.occupancySet);
	}

	/**
	 * @return the displaySpans
	 */
	public final List<DisplaySpan> getDisplaySpans() {
		return displaySpans;
	}

}
