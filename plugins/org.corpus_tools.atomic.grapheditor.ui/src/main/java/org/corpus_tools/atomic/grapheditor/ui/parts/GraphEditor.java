/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.model.InfoNode;
import org.corpus_tools.atomic.grapheditor.model.Subgraph;
import org.corpus_tools.atomic.grapheditor.ui.GraphEditorEventConstants;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.common.impl.SSpanImpl;
import org.corpus_tools.salt.common.impl.STokenImpl;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.util.SaltUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
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
public class GraphEditor extends AbstractFXEditor implements EventHandler {

	private static final Logger log = LogManager.getLogger(GraphEditor.class);
	private static final String EVENT_DATA = "org.eclipse.e4.data";
	private SDocumentGraph graph;
	private boolean dirty;
	private Subgraph currentSubgraph;

	public GraphEditor() {
		super(Guice.createInjector(Modules.override(new GraphEditorModule()).with(new GraphEditorUiModule())));
		subscribeToEventBroker();
		getContentViewer().getContents().setAll(createContents());
	}

	private void subscribeToEventBroker() {
		Object service = PlatformUI.getWorkbench().getService(IEventBroker.class);
		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED, this);
		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED, this);
//		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_EDITOR_INPUT_UPDATED, this);
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

	@Override
	public void handleEvent(Event event) {
		log.error("CAUGHT EVENT! graph NULL? " + (graph == null) + " -- subgraph NULL? " + (currentSubgraph == null));
		Object data = event.getProperty(EVENT_DATA);
		switch (event.getTopic()) {
		case GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED:
			currentSubgraph = new Subgraph();
			try {
				if (data instanceof ArrayList) {
					log.trace("Setting subgraph for GraphEditor.");
					Subgraph subgraph = null;
					List<MatchGroup> matchGroups = null;
					try {
						matchGroups = (List<MatchGroup>) data;
					}
					catch (ClassCastException e) {
						log.error("Event data is not the expected List<MatchGroup>. This shouldn't have happened.", e);
						break;
					}
					try {
						subgraph = buildSubgraph(matchGroups);
					}
					catch (NullPointerException e) {
						// FIXME This should really not throw an NPE once it's
						// fully implemented!
						log.trace("Graph Editor does not have an IViewer. Possibly because the part is not visible.");
					}
					getContentViewer().getContents()
							.setAll(Collections.singletonList(new InfoNode("UPDATED. Contents:", data.toString())));
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
			log.trace("BEFORE GRAPH SET: GRAPH = " + graph);
			if (data instanceof SDocumentGraph) {
				log.trace("Setting graph for Graph Editor to {}.", data);
				this.graph = (SDocumentGraph) data;
			}
			else {
				log.warn("Data broadcasted on topic {} is not an instance of SDocumentGraph.",
						GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED);
			}
			break;

//		case GraphEditorEventConstants.TOPIC_EDITOR_INPUT_UPDATED:
//			if (data instanceof FileEditorInput) {
//				log.trace("Setting Graph Editor input to the updated input {}.", data);
//				setInput((FileEditorInput) data);
//			}
//			break;

		default:
			break;
		}

	}

	private Subgraph buildSubgraph(List<MatchGroup> data) {
		log.error(">>>>>>>>>>>>> {}", graph);
		Set<java.net.URI> deduplicatedSaltIDs = new HashSet<>();
		for (MatchGroup matchGroup : data) {
			for (Match match : matchGroup.getMatches()) {
				deduplicatedSaltIDs.addAll(match.getSaltIDs());
			}
		}
		Set<SToken> subgraphTokens = new HashSet<>();
		Set<SSpan> subgraphSpans = new HashSet<>();
		for (java.net.URI id : deduplicatedSaltIDs) {
			SNode node = graph.getNode(id.toString());
			Class<? extends SNode> clazz = node.getClass();
			if (clazz == STokenImpl.class) {
				subgraphTokens.add((SToken) node);
			}
			else if (clazz == SSpanImpl.class) {
				subgraphSpans.add(((SSpan) node));
			}
		}
		log.trace("TOKENS: ({}) {}", subgraphTokens.size(), subgraphTokens);
		for (SToken t : subgraphTokens) {
			log.trace("        {}", graph.getText(t));
		}
		log.trace("SPANS: ({}) {}", subgraphSpans.size(), subgraphSpans);
		Subgraph subgraph = new Subgraph();
		return subgraph;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IEditorInput input = getEditorInput();
		if (input instanceof FileEditorInput && ((FileEditorInput) input).getFile().getFileExtension().equals("salt")) {
			log.trace("Attempting to save editor input to {}.", ((FileEditorInput) input).getPath());
			IPath resPath = ((FileEditorInput) getEditorInput()).getPath();
			SaltUtil.saveDocumentGraph(graph, URI.createFileURI(resPath.toOSString()));
			setDirty(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * @param dirty
	 *            the dirty to set
	 */
	public final void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	@Override
	public void setFocus() {
		super.setFocus();
	}

}
