/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Random;

import org.corpus_tools.atomic.grapheditor.constants.GEProcConstants;
import org.corpus_tools.atomic.grapheditor.visuals.NodeVisual;
import org.corpus_tools.atomic.grapheditor.visuals.TokenVisual;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.gef.geometry.planar.Rectangle;

import javafx.scene.paint.Color;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TokenPart extends NodePart {
	
	private static final double Y_DEFAULT = 0d;

	@Override
    protected NodeVisual doCreateVisual() {
        return new TokenVisual();
    }

	@Override
	protected void doRefreshVisual(NodeVisual visual) {
		// updating the visuals texts and position

		SToken node = getContent();
		// TODO Rectangle rec = node.getBounds();
		Random r = new Random();
		double randomValue = 10 + (400 - 10) * r.nextDouble();
		double x = randomValue;
		if (getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) != null) {
			x = getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME).getValue_SFLOAT();
		}
		double y = Y_DEFAULT;
		Rectangle rec = new Rectangle(x, y, 20, 20);

		visual.setTitle(((SDocumentGraph) getContent().getGraph()).getText((SToken) getContent()));
		visual.setDescription(node.getId().split("#")[1]); // TODO FIXME?

		visual.setColor(Color.LIGHTGOLDENRODYELLOW);

		visual.setPrefSize(rec.getWidth(), rec.getHeight());
		// visual.setMinSize(rec.getWidth(), rec.getHeight());
		// perform layout pass so that visual is resized to its preferred size
		visual.getParent().layout();

		visual.setTranslateX(rec.getX());
		visual.setTranslateY(rec.getY());
	}

	@Override
	public SToken getContent() {
		return (SToken) super.getContent();
	}

}
