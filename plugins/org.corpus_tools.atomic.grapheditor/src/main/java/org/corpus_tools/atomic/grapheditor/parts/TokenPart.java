/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Collections;
import java.util.List;
import org.corpus_tools.atomic.grapheditor.constants.GEProcConstants;
import org.corpus_tools.atomic.grapheditor.visuals.TokenVisual;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.paint.Color;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TokenPart extends AbstractContentPart<TokenVisual> {
	
	private static final double Y_DEFAULT = 0d;

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create(); // FIXME
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList(); // FIXME
	}

	@Override
    protected TokenVisual doCreateVisual() {
        return new TokenVisual();
    }

	@Override
	protected void doRefreshVisual(TokenVisual visual) {
		SToken token = getContent();
		double x = 0;
		if (getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) != null) {
			x = getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME).getValue_SFLOAT();
		}
		double y = Y_DEFAULT;
		Rectangle rec = new Rectangle(x, y, 20, 20);

		visual.setText(((SDocumentGraph) getContent().getGraph()).getText((SToken) getContent()));
		visual.setTokenId(token.getId().split("#")[1]); // TODO FIXME?
		visual.setAnnotations(token.getAnnotations());

		visual.setColor(Color.LIGHTGOLDENRODYELLOW);

		visual.setPrefSize(rec.getWidth(), rec.getHeight());
		visual.getParent().layout();

		visual.setTranslateX(rec.getX());
		visual.setTranslateY(rec.getY());
	}

	@Override
	public SToken getContent() {
		return (SToken) super.getContent();
	}

}
