/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model.visualization;

import java.util.LinkedList;
import java.util.List;

import org.corpus_tools.salt.common.SSpan;

/**
 * // TODO Add description
 * 
 * An event has a left and a right border.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class DisplaySpan {

	private String id;
	private double left;
	private double right;
	private List<String> coveredIDs;
	private SSpan span;

	/**
	 * // TODO Add description
	 * @param span 
	 * 
	 * @param left
	 * @param right
	 * @param value
	 */
	public DisplaySpan(SSpan span, double left, double right) {
	    this.left = left;
	    this.right = right;
	    this.span = span;
	    this.id = span.getId();
	    this.coveredIDs = new LinkedList<>();
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @return the left
	 */
	public final Double getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public final Double getRight() {
		return right;
	}

	/**
	 * @return the coveredIDs
	 */
	public final List<String> getCoveredIDs() {
		return coveredIDs;
	}

	/**
	 * @param coveredIDs the coveredIDs to set
	 */
	public final void setCoveredIDs(List<String> coveredIDs) {
		this.coveredIDs = coveredIDs;
	}

	/**
	 * @return the span
	 */
	public final SSpan getSpan() {
		return span;
	}

}
