/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.paint.Color;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class StructurePart extends StructuredNodePart {
	
	public StructurePart() {
		super(Color.MINTCREAM, Color.PURPLE);
	}


	private static final Logger log = LogManager.getLogger(StructurePart.class);

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

//    @Override
//    protected void doRefreshVisual(NodeVisual visual) {
//        // updating the visuals texts and position
//
//        SNode node = getContent();
////      TODO  Rectangle rec = node.getBounds();
//        Random r = new Random();
//        double randomValue = 10 + (400 - 10) * r.nextDouble();
//        double x = randomValue;
//        double y = 200;
//        double width = 20; 
//        if (getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) != null) {
//			x = getContent().getProcessingAnnotation(GEProcConstants.XCOORD_QNAME).getValue_SFLOAT();
//		}
//        if (getContent().getProcessingAnnotation(GEProcConstants.WIDTH_QNAME) != null) {
//			width = getContent().getProcessingAnnotation(GEProcConstants.WIDTH_QNAME).getValue_SFLOAT();
//		}
//        if (getContent().getProcessingAnnotation(GEProcConstants.YCOORD_QNAME) != null) {
//			y = getContent().getProcessingAnnotation(GEProcConstants.YCOORD_QNAME).getValue_SFLOAT();
//		}
//        Rectangle rec = new Rectangle(x, y, width, 20);
//
//       	visual.setTitle(node.getId().split("#")[1]); // TODO FIXME?
//       	visual.setDescription(node.getAnnotations().toString()); // TODO FIXME
//        visual.setColor(Color.LIGHTGREY);
//        
//        visual.setPrefSize(rec.getWidth(), rec.getHeight());
//        // perform layout pass so that visual is resized to its preferred size
//        visual.getParent().layout();
//
//        visual.setTranslateX(rec.getX());
//        visual.setTranslateY(rec.getY());
//    }
//

}
