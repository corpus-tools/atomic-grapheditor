/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Collections;
import java.util.List;

import org.corpus_tools.atomic.grapheditor.visuals.AbstractRelationVisual;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class AbstractRelationPart extends AbstractContentPart<Connection>{
	
	private static final String START_ROLE = "START";
    private static final String END_ROLE = "END";

    @Override
    protected void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
        // find a anchor provider, which must be registered in the module
        // be aware to use the right interfaces (Provider is used a lot)
        @SuppressWarnings("serial")
        Provider<? extends IAnchor> adapter = anchorage
                .getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
                }));
        if (adapter == null) {
            throw new IllegalStateException("No adapter  found for <" + anchorage.getClass() + "> found.");
        }
        IAnchor anchor = adapter.get();

        if (role.equals(START_ROLE)) {
            getVisual().setStartAnchor(anchor);
        } else if (role.equals(END_ROLE)) {
            getVisual().setEndAnchor(anchor);
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Override
    protected Connection doCreateVisual() {
        return new AbstractRelationVisual();
    }

    @Override
    protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
        if (role.equals(START_ROLE)) {
            getVisual().setStartPoint(getVisual().getStartPoint());
        } else if (role.equals(END_ROLE)) {
            getVisual().setEndPoint(getVisual().getEndPoint());
        } else {
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    @Override
    protected SetMultimap<SNode, String> doGetContentAnchorages() {
        SetMultimap<SNode, String> anchorages = HashMultimap.create();

        anchorages.put((SNode) getContent().getSource(), START_ROLE);
        anchorages.put((SNode) getContent().getTarget(), END_ROLE);

        return anchorages;
    }

    @Override
    protected List<? extends Object> doGetContentChildren() {
        return Collections.emptyList();
    }

    @Override
    protected void doRefreshVisual(Connection visual) {
        // nothing to do here
    }

    @Override
    public SRelation getContent() {
        return (SRelation) super.getContent();
    }

}