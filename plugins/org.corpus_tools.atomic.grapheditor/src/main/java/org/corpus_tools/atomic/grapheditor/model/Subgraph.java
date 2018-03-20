/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model;

import java.util.ArrayList; 
import java.util.List;
import java.util.Set;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class Subgraph {
	
	private Set<SToken> tokens;
	private Set<SSpan> spans;
	private Set<SStructure> structures;

	/**
	 * // TODO Add description
	 * 
	 * @param subgraphTokens
	 * @param subgraphSpans
	 * @param subgraphStructures
	 */
	public Subgraph(Set<SToken> subgraphTokens, Set<SSpan> subgraphSpans, Set<SStructure> subgraphStructures) {
		this.tokens = subgraphTokens;
		this.spans = subgraphSpans;
		this.structures = subgraphStructures;
	}

	/**
	 * @return the tokens
	 */
	public Set<SToken> getTokens() {
		return tokens;
	}

	/**
	 * @return the spans
	 */
	public Set<SSpan> getSpans() {
		return spans;
	}

	/**
	 * @return the structures
	 */
	public final Set<SStructure> getStructures() {
		return structures;
	}
	
	/**
	 * // TODO Add description
	 * 
	 * @return
	 */
	public List<SNode> getNodes() {
		List<SNode> nodeList = new ArrayList<>();
		nodeList.addAll(tokens);
		nodeList.addAll(spans);
		nodeList.addAll(structures);
		return nodeList;
	}

}
