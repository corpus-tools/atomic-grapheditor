
package org.corpus_tools.atomic.grapheditor.ui.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.atomic.grapheditor.ui.GraphEditorEventConstants;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import annis.service.objects.MatchGroup;

public class TestPart {

	private Label label;
	private Label label2;

	@PostConstruct
	public void postConstruct(Composite parent) {
	label = new Label(parent, SWT.BORDER);
	label.setText("GRAPH: I don't know.");
	label2 = new Label(parent, SWT.BORDER);
	label2.setText("SUBGRAPH: I don't know.");
		
	}

	@Inject
	@Optional
	private void subscribeTopicGraphUpdated
	(@UIEventTopic(GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED)
	SDocumentGraph graph) {
		label.setText("GRAPH: " + graph.getId());
	}
	
	@Inject
	@Optional
	private void subscribeTopicMathgroupGraphUpdated
	(@UIEventTopic(GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED)
	List<MatchGroup> matchgroups) {
		StringBuilder sBuilder = new StringBuilder("");
		for (MatchGroup m : matchgroups) {
			sBuilder.append(m.getMatches().toString() + "\n");
		}
		label2.setText("SUBGRAPH: " + sBuilder.toString());
	}

}