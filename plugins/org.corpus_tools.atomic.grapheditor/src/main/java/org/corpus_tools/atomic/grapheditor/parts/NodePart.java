/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.corpus_tools.atomic.grapheditor.constants.GEProcConstants;
import org.corpus_tools.atomic.grapheditor.visuals.NodeVisual;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
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
public class NodePart extends AbstractContentPart<NodeVisual> {

	@Override
    protected NodeVisual doCreateVisual() {
        return new NodeVisual();
    }

    @Override
    protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
        // Nothing to anchor to
        return HashMultimap.create();
    }

    @Override
    protected List<? extends Object> doGetContentChildren() {
        // we don't have any children.
        return Collections.emptyList();
    }

    @Override
    protected void doRefreshVisual(NodeVisual visual) {
        // updating the visuals texts and position

        SNode node = getContent();
//      TODO  Rectangle rec = node.getBounds();
        Random r = new Random();
        double randomValue = 10 + (400 - 10) * r.nextDouble();
        double x = randomValue;
        if (getContent() instanceof SToken) {
        	if (getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) != null) {
        		x = getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME).getValue_SFLOAT();
        	}
        }
        double y = getContent() instanceof SToken ? 100 : 200;
        Rectangle rec = new Rectangle(x, y, 100, 20);

        visual.setTitle(node.getId().split("#")[1]); // TODO FIXME?
        visual.setDescription(node.getAnnotations().toString()); // TODO FIXME
//      TODO  visual.setColor(node.getColor());
        visual.setColor(Color.ANTIQUEWHITE);
        
        visual.setPrefSize(rec.getWidth(), rec.getHeight());
        // perform layout pass so that visual is resized to its preferred size
        visual.getParent().layout();

        visual.setTranslateX(rec.getX());
        visual.setTranslateY(rec.getY());
    }

    @Override
    public SNode getContent() {
        return (SNode) super.getContent();
    }
}
