/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 *
 * Constant definition for events within this plugin.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface GraphEditorEventConstants {
	
	String TOPIC_SUBGRAPH = "TOPIC_SUBGRAPH";
	String TOPIC_SUBGRAPH_ALLTOPICS = "TOPIC_SUBGRAPH/*";
	String TOPIC_SUBGRAPH_CHANGED = "TOPIC_SUBGRAPH/CHANGED";
	
	String TOPIC_GRAPH = "TOPIC_GRAPH";
	String TOPIC_GRAPH_ALLTOPICS = "TOPIC_GRAPH/*";
	String TOPIC_GRAPH_ACTIVE_GRAPH_CHANGED = "TOPIC_GRAPH/ACTIVE_GRAPH/CHANGED";

}
