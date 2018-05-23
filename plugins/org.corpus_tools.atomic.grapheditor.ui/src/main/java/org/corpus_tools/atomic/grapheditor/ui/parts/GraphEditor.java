/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.editors.GraphicalDocumentGraphEditor;
import org.corpus_tools.atomic.grapheditor.model.InfoNode;
import org.corpus_tools.atomic.grapheditor.model.Subgraph;
import org.corpus_tools.atomic.grapheditor.ui.constants.GraphEditorEventConstants;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.impl.SSpanImpl;
import org.corpus_tools.salt.common.impl.SStructureImpl;
import org.corpus_tools.salt.common.impl.STokenImpl;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

import annis.service.objects.Match;
import annis.service.objects.MatchGroup;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GraphEditor extends GraphicalDocumentGraphEditor implements EventHandler {

	private static final Logger log = LogManager.getLogger(GraphEditor.class);
	private static final String EVENT_DATA = "org.eclipse.e4.data";

	/**
	 * // TODO Add description
	 * 
	 */
	public GraphEditor() {
		super(Guice.createInjector(Modules.override(new GraphEditorModule()).with(new GraphEditorUiModule())));
		subscribeToEventBroker();
		getContentViewer().getContents().setAll(createContents());
	}

	private void subscribeToEventBroker() {
		Object service = PlatformUI.getWorkbench().getService(IEventBroker.class);
		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED, this);
		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED, this);
	}

	private List<? extends Object> createContents() {
		return Collections.singletonList(new InfoNode("Graph Editor",
				"Please select segments to annotate and click \"Annotate\"."));
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		setPartName("Graph (" + input.getName() + ")");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleEvent(Event event) {
		Object data = event.getProperty(EVENT_DATA);
		switch (event.getTopic()) {
		case GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED:
			try {
				if (data instanceof ArrayList) {
					log.trace("Setting subgraph for GraphEditor.");
					List<MatchGroup> matchGroups = null;
					try {
						matchGroups = (List<MatchGroup>) data;
					}
					catch (ClassCastException e) {
						log.error("Event data is not the expected List<MatchGroup>. This shouldn't have happened.", e);
						break;
					}
					// If the matched data is different, (re-)build the subgraph
					Subgraph subgraph = buildSubgraph(matchGroups);
					getContentViewer().getContents()
							.setAll(Collections.singletonList(subgraph));
				}
				else {
					getContentViewer().getContents().setAll(
							Collections.singletonList(new InfoNode("Subgraph updated. Contents:", data.toString())));
				}
			}
			catch (NullPointerException e) {
				/*
				 * Ignore, getContentViewer() may still return null at this
				 * point, but will be fixed in next call.
				 */
			}
			break;

		case GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED:
			/*
			 * This is left in in case the graph-setting event is
			 * faster than the initialization of the graph in 
			 * GraphicalDocumentGraphEditor.
			 */
			if (graph == null) {
				if (data instanceof SDocumentGraph) {
					log.trace("Setting graph for Graph Editor to {}.", data);
					this.graph = (SDocumentGraph) data;
				}
				else {
					log.warn("Data broadcasted on topic {} is not an instance of SDocumentGraph.",
							GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED);
				}
			}
			else {
				log.error("Ignoring event of type {}, as the graph has already been set to {}.", GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED, graph);
			}
			break;

		default:
			break;
		}

	}

	private Subgraph buildSubgraph(List<MatchGroup> data) {
		log.trace("Building subgroups");
		Set<java.net.URI> deduplicatedSaltIDs = new HashSet<>();
		for (MatchGroup matchGroup : data) {
			for (Match match : matchGroup.getMatches()) {
				deduplicatedSaltIDs.addAll(match.getSaltIDs());
			}
		}
		Set<SToken> subgraphTokens = new HashSet<>();
		Set<SSpan> subgraphSpans = new HashSet<>();
		Set<SStructure> subgraphStructures = new HashSet<>();
		for (java.net.URI id : deduplicatedSaltIDs) {
			SNode node = graph.getNode(id.toString());
			Class<? extends SNode> clazz = node.getClass();
			if (clazz == STokenImpl.class) {
				subgraphTokens.add((SToken) node);
			}
			else if (clazz == SSpanImpl.class) {
				subgraphSpans.add(((SSpan) node));
			}
			else if (clazz == SStructureImpl.class) {
				subgraphStructures.add((SStructure) node);
			}
		}
		Subgraph subgraph = new Subgraph(graph, subgraphTokens, subgraphSpans, subgraphStructures);
		subgraph.calculateLayout();
		return subgraph;
	}
	
}
