/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Collections;
import java.util.List;

import org.corpus_tools.atomic.grapheditor.model.InfoNode;
import org.corpus_tools.atomic.grapheditor.visuals.InfoNodeVisual;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class InfoNodePart extends AbstractContentPart<InfoNodeVisual> {

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		return HashMultimap.create();
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected InfoNodeVisual doCreateVisual() {
		InfoNodeVisual visual = new InfoNodeVisual();
		doRefreshVisual(visual);
		return visual;
	}

	@Override
	protected void doRefreshVisual(InfoNodeVisual visual) {
		InfoNode infoNode = (InfoNode) getContent();
		visual.setLayoutX(infoNode.getRect().getX());
		visual.setLayoutY(infoNode.getRect().getY());

		visual.setInfoTitle(infoNode.getTitle());
		visual.setInfoDescription(infoNode.getDescription());
	}

}
