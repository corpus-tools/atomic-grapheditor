/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.parts;

import java.util.Collections; 
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.model.InfoNode;
import org.corpus_tools.atomic.grapheditor.ui.GraphEditorEventConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.google.inject.Guice;
import com.google.inject.util.Modules;


/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GraphEditor extends AbstractFXEditor implements EventHandler {

	private static final Logger log = LogManager.getLogger(GraphEditor.class);
	private static final String EVENT_DATA = "org.eclipse.e4.data";

	public GraphEditor() {
		super(Guice.createInjector(Modules.override(new GraphEditorModule()).with(new GraphEditorUiModule())));
//		IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
//		if (getEditorInput() == null) {
//			setInput((IEditorInput) context.get("graph-editor-input"));
//		}
//		if (getEditorSite() == null) {
//			setSite((IWorkbenchPartSite) context.get("graph-editor-site"));
//		}
		
		Object service = PlatformUI.getWorkbench().getService(IEventBroker.class);
		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED, this);
		((IEventBroker) service).subscribe(GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED, this);
		getContentViewer().getContents().setAll(createContents());
	}

	private List<? extends Object> createContents() {
		return Collections.singletonList(new InfoNode("Info", "No info avaialbe"));
	}
	
	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		setPartName(input.getName());
	}

	@Override
	public void handleEvent(Event event) {
		switch (event.getTopic()) {
		case GraphEditorEventConstants.TOPIC_SUBGRAPH_CHANGED:
			getContentViewer().getContents().setAll(Collections.singletonList(new InfoNode("SUBGRAPH CHANGED", event.getProperty(EVENT_DATA).toString())));
			break;
			
		case GraphEditorEventConstants.TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED:
			getContentViewer().getContents().setAll(Collections.singletonList(new InfoNode("GRAPH CHANGED", event.getProperty(EVENT_DATA).toString())));
			break;

		default:
			break;
		}
		
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
