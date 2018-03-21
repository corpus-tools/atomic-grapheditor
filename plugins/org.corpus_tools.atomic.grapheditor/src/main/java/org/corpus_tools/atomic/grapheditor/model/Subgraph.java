/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.constants.GEProcConstants;
import org.corpus_tools.atomic.grapheditor.model.visualization.AnnotationSet;
import org.corpus_tools.atomic.grapheditor.model.visualization.DisplayCoordinates;
import org.corpus_tools.atomic.grapheditor.model.visualization.DisplayLevel;
import org.corpus_tools.atomic.grapheditor.model.visualization.LevelExtractor;
import org.corpus_tools.atomic.grapheditor.model.visualization.DisplaySpan;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class Subgraph {

	private static final int Y_MARGIN_DEFAULT = 100;

	private static final double MARGIN_DEFAULT = 20d;

	private static final double X_DEFAULT = 100d;

	private static final Logger log = LogManager.getLogger(Subgraph.class);

	private Set<SToken> tokens;
	private Set<SSpan> spans;
	private Set<SStructure> structures;
	private SDocumentGraph graph;
	private Map<SToken, DisplayCoordinates> tokenCoords = new HashMap<>();
	private Map<SSpan, DisplayCoordinates> spanCoords = new HashMap<>();

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
		if (spans != null && !spans.isEmpty()) {
			calculateSpanLayout(spans, tokenCoords);
		}
	}

	/**
	 * // TODO Add description FIXME Add annotations
	 * 
	 * @param tokens
	 */
	private void calculateTokenLayout(Set<SToken> tokens) {
		List<SToken> orderedTokens = graph.getSortedTokenByText(new ArrayList<>(tokens));
		List<Double> widthsList = new ArrayList<>();
		for (SToken t : orderedTokens) {
			// Prepare the coords map entry for this token
			// Pre-calculate widths to calculate x coords.
			// Pre-render off-screen
			Group offScreenRoot = new Group();
	        Scene offScreen = new Scene(offScreenRoot, 1024, 768);
//	        offScreenRoot.getChildren().addAll(untransformed, line1, line2);
	        Set<Double> internalWidthsSet = new HashSet<>();
	        Set<Text> textSet = new HashSet<>();
	        Text textText = new Text(graph.getText(t));
	        
	        textSet.add(textText);
	        Text idText = new Text(t.getId().split("#")[1]);
//			idText.setStyle("-fx-font-size: 24;");
	        textSet.add(idText);
	        for (SAnnotation a : t.getAnnotations()) {
	        	textSet.add(new Text(a.getQName() + ":" + a.getValue_STEXT()));
	        }
	        for (Text text : textSet) {
	        	text.setStyle("-fx-font-size: 48;");
	        	text.applyCss();
	        	offScreenRoot.getChildren().add(text);
	        }
			for (Text text : textSet) {
				internalWidthsSet.add(text.getLayoutBounds().getWidth() + (GEProcConstants.HORIZONTAL_PADDING * 2));

			}
			double width = Collections.max(internalWidthsSet);
			width = width < GEProcConstants.MIN_TOKEN_WIDTH ? GEProcConstants.MIN_TOKEN_WIDTH : width;
			widthsList.add(width);
			if (t.getProcessingAnnotation(GEProcConstants.WIDTH_QNAME) == null) {
				t.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.WIDTH, new Double(width));
			}
			tokenCoords.put(t, new DisplayCoordinates(width));
		}
		List<Double> xList = new ArrayList<>();
		// Hardcoded x coord of first token at 100d
		xList.add(X_DEFAULT);
		SToken firstToken = orderedTokens.get(0);
		if (firstToken.getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) == null) {
			firstToken.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE,
					new Double(X_DEFAULT));
			tokenCoords.get(firstToken).setLeft(X_DEFAULT);
		}
		for (int i = 1; i < widthsList.size(); i++) {
			Double predecessorWidth = widthsList.get(i - 1);
			Double predecessorX = xList.get(i - 1);
			// Hardcoded margin of 20d FIXME: Make user-settable
			double newX = predecessorX + predecessorWidth + MARGIN_DEFAULT;
			xList.add(i, newX);
			SToken tokenAtIndex = orderedTokens.get(i);
			if (tokenAtIndex.getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) == null) {
				tokenAtIndex.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE,
						new Double(newX));
			}
			tokenCoords.get(tokenAtIndex).setLeft(newX);
		}
	}

	private void calculateSpanLayout(Set<SSpan> spans, Map<SToken, DisplayCoordinates> tokenCoords) {
		LinkedHashMap<AnnotationSet, ArrayList<DisplayLevel>> levelsBySpanAnnotations = LevelExtractor
				.computeSpanLevels(graph, spans, tokenCoords);
		// Sort by number of spans per level
		Comparator<Entry<AnnotationSet, ArrayList<DisplayLevel>>> byNumberOfSpan = (
				Entry<AnnotationSet, ArrayList<DisplayLevel>> o1,
				Entry<AnnotationSet, ArrayList<DisplayLevel>> o2) -> Integer.compare(o1.getValue().size(),
						o2.getValue().size());
		Map<AnnotationSet, ArrayList<DisplayLevel>> sortedLevels = levelsBySpanAnnotations.entrySet().stream().sorted(byNumberOfSpan.reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		// Write processing annotations
		int level = 1;
		double yDefault = Y_MARGIN_DEFAULT;
		for (Entry<AnnotationSet, ArrayList<DisplayLevel>> entry : sortedLevels.entrySet()) {
			for (DisplayLevel v : entry.getValue()) {
				for (DisplaySpan ds : v.getDisplaySpans()) {
					SSpan span = ds.getSpan();
					span.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE, new Double(ds.getLeft()));
					span.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.WIDTH, new Double(ds.getRight() - ds.getLeft()));
					span.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.Y_COORDINATE, new Double(level * yDefault));
				}
				level++;
			}
		}
		
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
