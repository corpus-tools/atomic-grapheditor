/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.visuals;

import org.eclipse.gef.fx.nodes.Connection;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class AbstractRelationVisual extends Connection {
	
	public static class ArrowHead extends Polygon {
        public ArrowHead() {
            super(0, 0, 10, 3, 10, -3);
        }
    }

    public AbstractRelationVisual() {
        ArrowHead endDecoration = new ArrowHead();
        endDecoration.setFill(Color.BLACK);
        setEndDecoration(endDecoration);
    }

}
