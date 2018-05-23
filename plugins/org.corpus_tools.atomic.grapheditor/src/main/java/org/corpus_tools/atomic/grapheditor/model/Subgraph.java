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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.constants.GEProcConstants;
import org.corpus_tools.atomic.grapheditor.model.visualization.AnnotationSet;
import org.corpus_tools.atomic.grapheditor.model.visualization.DisplayCoordinates;
import org.corpus_tools.atomic.grapheditor.model.visualization.DisplayLevel;
import org.corpus_tools.atomic.grapheditor.model.visualization.LevelExtractor;
import org.corpus_tools.atomic.util.AnnotationUtil;
import org.corpus_tools.atomic.grapheditor.model.visualization.DisplaySpan;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class Subgraph {

	private static final double Y_MARGIN_DEFAULT = 100d;

	private static final double MARGIN_DEFAULT = 20d;

	private static final double X_DEFAULT = 100d;

	private static final Logger log = LogManager.getLogger(Subgraph.class);

	private static final double HORIZONTAL_PADDING = 5d;

	private Set<SToken> tokens;
	private Set<SSpan> spans;
	private Set<SStructure> structures;
	private SDocumentGraph graph;
	private Map<SToken, DisplayCoordinates> tokenCoords = new HashMap<>();
	private Map<SSpan, DisplayCoordinates> spanCoords = new HashMap<>();

	private Double maxTokenHeight;

	private LevelExtractor levelExtractor;

	/**
	 * // TODO Add description
	 * 
	 * @param graph
	 * 
	 * @param subgraphTokens
	 * @param subgraphSpans
	 * @param subgraphStructures
	 * @param data 
	 */
	public Subgraph(SDocumentGraph graph, Set<SToken> subgraphTokens, Set<SSpan> subgraphSpans,
			Set<SStructure> subgraphStructures) {
		this.tokens = subgraphTokens;
		this.spans = subgraphSpans;
		this.structures = subgraphStructures;
		this.graph = graph;
		this.levelExtractor = new LevelExtractor(tokens);
	}
	
	public boolean calculateLayout() {
		this.tokenCoords = calculateTokenLayout(tokens);
		if (spans != null && !spans.isEmpty()) {
			calculateSpanLayout(spans, tokenCoords);
		}
//		if (structures != null && !structures.isEmpty()) {
//			calculateStructureLayout(structures, tokenCoords);
//		}
		return true;
	}

	/**
	 * // TODO Add description FIXME Add annotations
	 * 
	 * @param tokens
	 * @return 
	 */
	private Map<SToken, DisplayCoordinates> calculateTokenLayout(Set<SToken> tokens) {
		List<SToken> orderedTokens = graph.getSortedTokenByText(new ArrayList<>(tokens));
		List<Double> widthsList = new ArrayList<>();
		List<Double> heightsList = new ArrayList<>();
		for (SToken t : orderedTokens) {
			// Prepare the coords map entry for this token
			// Pre-calculate widths to calculate x coords.
			// Pre-render off-screen
			Group offScreenRoot = new Group();
	        GeometryNode<RoundedRectangle> shape = new GeometryNode<>(new RoundedRectangle(0, 0, 70, 30, 8, 8));
	        
	        Set<Double> internalWidthsSet = new HashSet<>();
	        Set<Double> internalHeightsSet = new HashSet<>();
	        Set<Node> textSet = new HashSet<>();

	        Text textText = new Text(graph.getText(t));
	        textSet.add(textText);

	        Text idText = new Text(t.getId().split("#")[1]);
	        textSet.add(idText);
	        
	        Text annotationsText = new Text(t.getAnnotations().toString());
	        TextFlow annotationsFlow = new TextFlow(annotationsText);
			annotationsFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));
	        textSet.add(annotationsFlow);
	        
	        
	        
	        for (Node text : textSet) {
	        	text.applyCss();
	        	offScreenRoot.getChildren().add(text);
	        }
			for (Node text : textSet) {
				internalWidthsSet.add(text.getLayoutBounds().getWidth() + (GEProcConstants.HORIZONTAL_PADDING * 2));
				internalHeightsSet.add(text.getLayoutBounds().getHeight() + (GEProcConstants.HORIZONTAL_PADDING * 2));
			}
			double width = Collections.max(internalWidthsSet);
			double height = Collections.max(internalWidthsSet);
			width = width < GEProcConstants.MIN_TOKEN_WIDTH ? GEProcConstants.MIN_TOKEN_WIDTH : width;
			height = height < GEProcConstants.MIN_TOKEN_HEIGHT ? GEProcConstants.MIN_TOKEN_HEIGHT : height;
			widthsList.add(width);
			heightsList.add(height);
			if (t.getProcessingAnnotation(GEProcConstants.WIDTH_QNAME) == null) {
				t.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.WIDTH, new Double(width));
			}
			DisplayCoordinates coords = new DisplayCoordinates(width);
			tokenCoords.put(t, coords);
		}
		maxTokenHeight = Collections.max(heightsList);
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
		return tokenCoords;
	}

	private void calculateSpanLayout(Set<SSpan> spans, Map<SToken, DisplayCoordinates> tokenCoords) {
		LinkedHashMap<AnnotationSet, ArrayList<DisplayLevel>> levelsBySpanAnnotations = levelExtractor
				.computeSpanLevels(graph, spans, tokenCoords);
		// Sort by number of spans per level
		Comparator<Entry<AnnotationSet, ArrayList<DisplayLevel>>> byNumberOfSpan = (
				Entry<AnnotationSet, ArrayList<DisplayLevel>> o1,
				Entry<AnnotationSet, ArrayList<DisplayLevel>> o2) -> Integer.compare(o1.getValue().size(),
						o2.getValue().size());
		Map<AnnotationSet, ArrayList<DisplayLevel>> sortedLevels = levelsBySpanAnnotations.entrySet().stream().sorted(byNumberOfSpan.reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		// Write processing annotations
		int level = 1;
		for (Entry<AnnotationSet, ArrayList<DisplayLevel>> entry : sortedLevels.entrySet()) {
			for (DisplayLevel v : entry.getValue()) {
				for (DisplaySpan ds : v.getDisplaySpans()) {
					SSpan span = ds.getSpan();
					AnnotationUtil.annotateProcessing(span, GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE, new Double(ds.getLeft()));
					AnnotationUtil.annotateProcessing(span, GEProcConstants.NAMESPACE, GEProcConstants.WIDTH, new Double(ds.getRight() - ds.getLeft())); 
					AnnotationUtil.annotateProcessing(span, GEProcConstants.NAMESPACE, GEProcConstants.Y_COORDINATE, new Double(level == 1 ? maxTokenHeight + MARGIN_DEFAULT : level * Y_MARGIN_DEFAULT));
				}
				level++;
			}
		}
		
	}

	private void calculateStructureLayout(Set<SStructure> structures2, Map<SToken, DisplayCoordinates> tokenCoords2) {
//		LinkedHashMap<AnnotationSet, ArrayList<DisplayLevel>> levelsByStructureAnnotations = LevelExtractor
//				.computeStructureLevels(graph, structures, tokenCoords);
		List<SStructure> firstParentStructures = new ArrayList<>();
		Queue<SStructure> queue = new LinkedList<>(structures2);
		// Find "first parents", i.e., SStructures that only have STokens as children
		while (!queue.isEmpty()) {
			SStructure s = queue.remove();
			if (isFirstParent(s)) {
				firstParentStructures.add(s);
			}
			else {
				// Structure is not a "first parent", so add it to the queue again
//				queue.add(s);
			}
		}
		for (SStructure s : firstParentStructures) {
			for (SRelation rel : s.getOutRelations()) {
				if (rel instanceof SDominanceRelation) {
					if (rel.getTarget() instanceof SToken) {
						continue;
					}
				}
			}
		}
	}

	private boolean isFirstParent(SStructure s) {
		for (SRelation rel : s.getOutRelations()) {
			if (rel instanceof SDominanceRelation) {
				if (!(rel.getTarget() instanceof SToken)) {
					return false;
				}
			}
		}
		return true;
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

	/**
	 * @return the tokenCoords
	 */
	public final Map<SToken, DisplayCoordinates> getTokenCoords() {
		return tokenCoords;
	}

}
