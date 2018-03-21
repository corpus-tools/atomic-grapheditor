/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model.visualization;

/**
 * // TODO Add description
 * `width` must always be set
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class DisplayCoordinates {

	private double left;
	private double right;
	private final double width;
	
	@SuppressWarnings("unused")
	private DisplayCoordinates() {
		// Invisible constructor
		this.width = -1d;
	}

	/**
	 * // TODO Add description
	 * 
	 * @param width
	 */
	public DisplayCoordinates(double width) {
		this.width = width;
	}

	/**
	 * @return the left
	 */
	public final double getLeft() {
		return left;
	}

	/**
	 * @param left
	 *            the left to set
	 * @throws NullPointerException when `width` is null.
	 */
	public final void setLeft(double left) {
		this.left = left;
		this.right = this.left + this.width;
	}

	/**
	 * @return the right
	 */
	public final double getRight() {
		return right;
	}

	/**
	 * @param right
	 *            the right to set
	 */
	public final void setRight(double right) {
		this.right = right;
	}

	/**
	 * @return the width
	 */
	public final double getWidth() {
		return width;
	}

}
