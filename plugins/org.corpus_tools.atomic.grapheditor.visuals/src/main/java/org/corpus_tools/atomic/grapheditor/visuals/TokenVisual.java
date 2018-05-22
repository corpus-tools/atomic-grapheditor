/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.visuals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TokenVisual extends Region {

	private static final Logger log = LogManager.getLogger(NodeVisual.class);

	/**
	 * Must be the same value as
	 * org.corpus_tools.atomic.grapheditor.constants.GEProcConstants.HORIZONTAL_PADDING!
	 */
	private static final double HORIZONTAL_PADDING = 5d;
	private static final double VERTICAL_PADDING = 5d;
	private static final double VERTICAL_SPACING = 5d;

	private static final Double MIN_WIDTH = 100d;

	private Text textText;
	private Text tokenIdText;
	private Text annotationsText;
	private GeometryNode<RoundedRectangle> shape;
	private VBox labelVBox;

	private TextFlow annotationsFlow;

	public TokenVisual() {
		shape = new GeometryNode<>(new RoundedRectangle(0, 0, 70, 30, 8, 8));
		shape.setStroke(Color.BLACK);

		labelVBox = new VBox(VERTICAL_SPACING);
		labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));

		shape.prefWidthProperty().bind(widthProperty());
		shape.prefHeightProperty().bind(heightProperty());
		labelVBox.prefWidthProperty().bind(widthProperty());
		labelVBox.prefHeightProperty().bind(heightProperty());

		textText = new Text();
		textText.setTextOrigin(VPos.TOP);
		textText.setTextAlignment(TextAlignment.CENTER);
		textText.setFill(Color.RED);

		tokenIdText = new Text();
		tokenIdText.setTextOrigin(VPos.TOP);

		annotationsText = new Text();
		annotationsText.setTextOrigin(VPos.TOP);
		annotationsFlow = new TextFlow(annotationsText);
		annotationsFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));

		labelVBox.getChildren().addAll(textText, tokenIdText, annotationsFlow);

		setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

		getChildren().addAll(new Group(shape), new Group(labelVBox));
	}

	@Override
	public double computeMinHeight(double width) {
		return labelVBox.minHeight(width);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.corpus_tools.atomic.grapheditor.visuals.NodeVisual#computeMinWidth(
	 * double)
	 */
	/**
	 * If you change something here, it must also be changed in the
	 * pre-calculation of token widths in
	 * Subgraph.calculateTokenLayout(Set<SToken>)!
	 */
	@Override
	public double computeMinWidth(double height) {
		List<Double> widths = new ArrayList<>();
		widths.add(textText.getLayoutBounds().getWidth() + (HORIZONTAL_PADDING * 2));
		widths.add(tokenIdText.getLayoutBounds().getWidth() + (HORIZONTAL_PADDING * 2));
//		widths.add(annotationsText.getLayoutBounds().getWidth() + (HORIZONTAL_PADDING * 2));
		widths.add(annotationsFlow.getLayoutBounds().getWidth() + (HORIZONTAL_PADDING * 2));
		// double ttWidth = textText.getLayoutBounds().getWidth() +
		// (HORIZONTAL_PADDING * 2);
		// double dtWidth = tokenIdText.getLayoutBounds().getWidth() +
		// (HORIZONTAL_PADDING * 2);
		// double minWidth = dtWidth > ttWidth ? dtWidth : ttWidth;
		// return minWidth < 100d ? 100d : minWidth;
		Double max = widths.stream().max(Double::compare).get();
		return max < MIN_WIDTH ? MIN_WIDTH : max;
	}

	@Override
	protected double computePrefHeight(double width) {
		return minHeight(width);
	}

	@Override
	protected double computePrefWidth(double height) {
		return minWidth(height);
	}

	@Override
	public Orientation getContentBias() {
		return Orientation.HORIZONTAL;
	}

	public Text getDescriptionText() {
		return tokenIdText;
	}

	public GeometryNode<?> getGeometryNode() {
		return shape;
	}

	public Text getTitleText() {
		return textText;
	}

	public void setColor(Color color) {
		shape.setFill(color);
	}

	public void setTokenId(String id) {
		this.tokenIdText.setText(id);
	}

	public void setText(String text) {
		this.textText.setText(text);
	}

	public void setAnnotations(Set<SAnnotation> annotations) {
		this.annotationsText.setText(annotations.toString());

	}

}
