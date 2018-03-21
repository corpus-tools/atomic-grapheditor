/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.constants.GEProcConstants;
import org.corpus_tools.atomic.grapheditor.visuals.NodeVisual;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class Subgraph {

	private static final Logger log = LogManager.getLogger(Subgraph.class);

	private Set<SToken> tokens;
	private Set<SSpan> spans;
	private Set<SStructure> structures;
	private SDocumentGraph graph;

	/**
	 * // TODO Add description
	 * 
	 * @param graph
	 * 
	 * @param subgraphTokens
	 * @param subgraphSpans
	 * @param subgraphStructures
	 */
	public Subgraph(SDocumentGraph graph, Set<SToken> subgraphTokens, Set<SSpan> subgraphSpans,
			Set<SStructure> subgraphStructures) {
		this.tokens = subgraphTokens;
		this.spans = subgraphSpans;
		this.structures = subgraphStructures;
		this.graph = graph;

		calculateTokenLayout(tokens);
		calculateSpanLayout(spans);
	}

	/**
	 * // TODO Add description
	 * FIXME Add annotations
	 * 
	 * @param tokens
	 */
	private void calculateTokenLayout(Set<SToken> tokens) {
		List<SToken> orderedTokens = graph.getSortedTokenByText(new ArrayList<>(tokens));
		List<Double> widthsList = new ArrayList<>();
		for (SToken t : orderedTokens) {
			// Pre-calculate widths to calculate x coords.
			Set<Double> internalWidthsSet = new HashSet<>();
			Set<Text> textSet = new HashSet<>();
			textSet.add(new Text(graph.getText(t)));
			textSet.add(new Text(t.getId().split("#")[1]));
			for (SAnnotation a : t.getAnnotations()) {
				textSet.add(new Text(a.getQName() + ":" + a.getValue_STEXT()));
			}
			for (Text text : textSet) {
				text.applyCss();
			}
			for (Text text : textSet) {
				internalWidthsSet.add(text.getLayoutBounds().getWidth());
				
			}
			double width = Collections.max(internalWidthsSet);
			widthsList.add(width);
			if (t.getProcessingAnnotation(GEProcConstants.WIDTH_QNAME) == null) {
				t.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.WIDTH,
						new Double(width));
			}
		}
		List<Double> xList = new ArrayList<>();
		// Hardcoded x coord of first token at 100d
		xList.add(100d);
		SToken firstToken = orderedTokens.get(0);
		if (firstToken.getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) == null) {
			firstToken.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE,
					new Double(100d));
		}
		for (int i = 1; i < widthsList.size(); i++) {
			Double predecessorWidth = widthsList.get(i - 1);
			Double predecessorX = xList.get(i - 1);
			// Hardcoded margin of 20d FIXME: Make user-settable
			double newX = predecessorX + predecessorWidth + 20d;
			xList.add(i, newX);
			SToken tokenAtIndex = orderedTokens.get(i);
			if (tokenAtIndex.getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) == null) {
				tokenAtIndex.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE,
						new Double(newX));
			}
		}
	}

	private void calculateSpanLayout(Set<SSpan> spans) {
		// TODO Auto-generated method stub
	
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
