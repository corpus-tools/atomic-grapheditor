/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model;

import org.eclipse.gef.geometry.planar.Rectangle;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class InfoNode {
	
	public Rectangle rect = new Rectangle();

	private String title;
	private String description;

	public InfoNode(String title, String description) {
		this.title = title;
		this.description = description;
		rect.setBounds(10, 10, 200, 200);
	}

	/**
	 * @return the rect
	 */
	public final Rectangle getRect() {
		return rect;
	}

	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

}
