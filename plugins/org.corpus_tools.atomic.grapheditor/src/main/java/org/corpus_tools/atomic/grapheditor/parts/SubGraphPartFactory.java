/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Map; 

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.model.InfoNode;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import com.google.inject.Injector;

import javafx.scene.Node;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SubGraphPartFactory implements IContentPartFactory {

	private static final Logger log = LogManager.getLogger(SubGraphPartFactory.class);
	
	@Inject
    private Injector injector;

    @Override
    public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
    	
    	if (content instanceof InfoNode) {
			return injector.getInstance(InfoNodePart.class);
		} else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
    }
}

