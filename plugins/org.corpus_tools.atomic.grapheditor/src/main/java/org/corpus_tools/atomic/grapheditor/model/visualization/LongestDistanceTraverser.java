package org.corpus_tools.atomic.grapheditor.model.visualization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.GraphTraverseHandler;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;

/**
 * Traverses a graph from a node to find the longest distance that
 * node has from an {@link SToken}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class LongestDistanceTraverser implements GraphTraverseHandler {
	
	private Map<SStructure, Integer> distanceMap = new HashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.corpus_tools.salt.core.GraphTraverseHandler#nodeReached(org.
	 * corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE, java.lang.String,
	 * org.corpus_tools.salt.core.SNode,
	 * org.corpus_tools.salt.core.SRelation,
	 * org.corpus_tools.salt.core.SNode, long)
	 */
	@Override
	public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
			SRelation<SNode, SNode> relation, SNode fromNode, long order) {
		if (currNode instanceof SStructure) {
			Integer lastDistance = distanceMap.get(fromNode); 
			if (lastDistance != null) {
				distanceMap.put((SStructure) currNode, lastDistance + 1);
			}
			else {
				distanceMap.put((SStructure) currNode, 1);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.corpus_tools.salt.core.GraphTraverseHandler#nodeLeft(org.
	 * corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE, java.lang.String,
	 * org.corpus_tools.salt.core.SNode,
	 * org.corpus_tools.salt.core.SRelation,
	 * org.corpus_tools.salt.core.SNode, long)
	 */
	@Override
	public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode,
			SRelation<SNode, SNode> relation, SNode fromNode, long order) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.corpus_tools.salt.core.GraphTraverseHandler#checkConstraint(org.
	 * corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE, java.lang.String,
	 * org.corpus_tools.salt.core.SRelation,
	 * org.corpus_tools.salt.core.SNode, long)
	 */
	@Override
	public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId,
			SRelation<SNode, SNode> relation, SNode currNode, long order) {
		if (currNode instanceof SToken) {
			return false;
		}
		return true;
	}

	/**
	 * // TODO Add description
	 * 
	 * @return
	 */
	public Integer getDistance() {
		List<Integer> distances = new ArrayList<>(distanceMap.values());
		Collections.sort(distances);
		return distances.get(distances.size() - 1);
	}

}