/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.model.Subgraph;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.Node;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SubgraphPart extends AbstractContentPart<Group> {
	
	private static final Logger log = LogManager.getLogger(SubgraphPart.class);

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		// Nothing to anchor to.
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		// Subgraph.getContent() returns nodes *and* relations
		return getContent().getContent();
	}

	@Override
	protected Group doCreateVisual() {
		/* 
		 * Just a generic group, as this is only the container
		 * for nodes and edges. 
		 */
		return new Group();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		// TODO Auto-generated method stub
		// TODO Probably do the refresh here when new
	}
	
	@Override
    protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
        getVisual().getChildren().add(child.getVisual());
    }
	
	@Override
    protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
        getVisual().getChildren().remove(child.getVisual());
    }
	
	@Override
	public Subgraph getContent() {
		return (Subgraph) super.getContent();
	}

}
