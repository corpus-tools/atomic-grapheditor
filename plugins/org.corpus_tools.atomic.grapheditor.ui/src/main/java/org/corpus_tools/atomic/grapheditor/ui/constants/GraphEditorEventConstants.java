/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.constants;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 *
 * Constant definition for events within this plugin.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface GraphEditorEventConstants {
	
	final String TOPIC_SUBGRAPH = "TOPIC_SUBGRAPH";
	final String TOPIC_SUBGRAPH_ALLTOPICS = "TOPIC_SUBGRAPH/*";
	final String TOPIC_SUBGRAPH_CHANGED = "TOPIC_SUBGRAPH/CHANGED";
	
	final String TOPIC_GRAPH = "TOPIC_GRAPH";
	final String TOPIC_GRAPH_ALLTOPICS = "TOPIC_GRAPH/*";
	final String TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED = "TOPIC_GRAPH/ACTIVE_GRAPH/CHANGED";
	
	final String TOPIC_EDITOR_INPUT = "TOPIC_EDITOR_INPUT";
	final String TOPIC_EDITOR_INPUT_ALLTOPICS = "TOPIC_EDITOR_INPUT/*";
	final String TOPIC_EDITOR_INPUT_UPDATED = "TOPIC_EDITOR_INPUT/UPDATED";

}
