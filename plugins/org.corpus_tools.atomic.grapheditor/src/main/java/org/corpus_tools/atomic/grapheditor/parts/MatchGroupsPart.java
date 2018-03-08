/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.List;

import org.corpus_tools.atomic.grapheditor.model.SubGraph;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.Node;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class MatchGroupsPart extends AbstractContentPart<Group> {

	@Override
	protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
		getVisual().getChildren().add(child.getVisual());
	}

	@Override
	protected void doAddContentChild(Object contentChild, int index) {
		if (contentChild instanceof SNode) {
			getContent().addChildElement((SNode) contentChild, index);
		}
		else {
			throw new IllegalArgumentException("contentChild has invalid type: " + contentChild.getClass());
		}
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		// TODO Auto-generated method stub
		return Lists.newArrayList(getContent().getChildElements());
	}

	@Override
	protected Group doCreateVisual() {
		// the visual is just a container for our child visuals (nodes and
		// connections)
		return new Group();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		// TODO Auto-generated method stub
		// FIXME Refreshs here!
	}
	
	@Override
    protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
        getVisual().getChildren().remove(child.getVisual());
    }
	
	@Override
    protected void doRemoveContentChild(Object contentChild) {
        if (contentChild instanceof SNode) {
            getContent().removeChildElement((SNode) contentChild);
        } else {
            throw new IllegalArgumentException("contentChild has invalid type: " + contentChild.getClass());
        }
    }
	
    @Override
    public SubGraph getContent() {
        return (SubGraph) super.getContent();
    }

}
