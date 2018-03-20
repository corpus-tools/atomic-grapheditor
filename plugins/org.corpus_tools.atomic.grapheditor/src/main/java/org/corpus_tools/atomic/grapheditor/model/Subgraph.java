/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model;

import java.util.ArrayList;
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
import org.corpus_tools.salt.core.SNode;

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

	private void calculateSpanLayout(Set<SSpan> spans) {
		// TODO Auto-generated method stub

	}

	private void calculateTokenLayout(Set<SToken> tokens) {
		List<SToken> orderedTokens = graph.getSortedTokenByText(new ArrayList<>(tokens));
		int x = 100;
		for (int i = 1; i <= orderedTokens.size(); i++) {
			SToken token = orderedTokens.get(i - 1);
			if (token.getProcessingAnnotation(GEProcConstants.XCOORD_QNAME) == null) {
				token.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.X_COORDINATE,
						new Double(x * i));
			}
		}
		calculateTokenWidths(tokens);
	}

	private void calculateTokenWidths(Set<SToken> tokens) {
		Group root = new Group();

		for (SToken t : tokens) {
			if (t.getProcessingAnnotation(GEProcConstants.WIDTH_QNAME) == null) {
				//// Scene s = new Scene(root);
				// NodeVisual v = new NodeVisual();
				// v.setTitle(graph.getText(t));
				// v.setDescription(t.getId().split("#")[1]);
				// v.setMinSize(-1.0, -1.0);
				// root.getChildren().add(v);
				// root.applyCss();
				// root.layout();
				//// v.setPrefSize(v.getShape().getWidth(),
				//// v.getShape().getHeight());
				//// v.getTitleText().getw
				// double width = v.getWidth();
				//// double height = v.getHeight();
				// log.trace(graph.getText(t) + ": " + width);
				final Text textText = new Text(graph.getText(t));
				final Text idText = new Text(t.getId().split("#")[1]);
				Scene scene1 = new Scene(new Group(textText, idText));
				textText.applyCss();
				idText.applyCss();
				final double textWidth = textText.getLayoutBounds().getWidth();
				final double idWidth = idText.getLayoutBounds().getWidth();
				double width = -1d;
				if (textWidth > idWidth) {
//					log.trace("TEXT > ID");
					width = textWidth;
				}
				else {
//					log.trace("TEXT < ID");
					width = idWidth;
				}
				final Text textText2 = new Text(graph.getText(t));
				final Text idText2 = new Text(t.getId().split("#")[1]);
				Scene scene2 = new Scene(new Group(idText, textText));
				textText2.applyCss();
				idText2.applyCss();
				final double idWidth2 = idText.getLayoutBounds().getWidth();
				final double textWidth2 = textText.getLayoutBounds().getWidth();
				double width2 = -1d;
				if (textWidth2 > idWidth2) {
//					log.trace("TEXT2 > ID2");
					width2 = textWidth2;
				}
				else {
//					log.trace("TEXT2 < ID2");
					width2 = idWidth;
				}
				double w = width2 > width ? width2 : width;
				log.trace("{} -- T1: {}, ID1: {}, T2: {}, ID2: {}, WIDTH = {}", graph.getText(t), textWidth, idWidth, textWidth2, idWidth2, w + 10d);
				t.createProcessingAnnotation(GEProcConstants.NAMESPACE, GEProcConstants.WIDTH, new Double(w));
			}
		}

		// stage.setScene(scene);
		// stage.show();

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
