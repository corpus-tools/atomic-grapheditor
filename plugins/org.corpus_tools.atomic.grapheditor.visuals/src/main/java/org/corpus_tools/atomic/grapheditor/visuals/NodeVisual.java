/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.visuals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class NodeVisual extends Region {
	
	private static final Logger log = LogManager.getLogger(NodeVisual.class);
	
	private static final double HORIZONTAL_PADDING = 5d;
    private static final double VERTICAL_PADDING = 5d;
    private static final double VERTICAL_SPACING = 5d;

    private Text titleText;
    private TextFlow descriptionFlow;
    private Text descriptionText;
    private GeometryNode<RoundedRectangle> shape;
    private VBox labelVBox;

    public NodeVisual() {
        // create background shape
        shape = new GeometryNode<>(new RoundedRectangle(0, 0, 70, 30, 8, 8));
        shape.setFill(Color.LIGHTGREEN);
        shape.setStroke(Color.BLACK);

        // create vertical box for title and description
        labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));

        // ensure shape and labels are resized to fit this visual
        shape.prefWidthProperty().bind(widthProperty());
        shape.prefHeightProperty().bind(heightProperty());
        labelVBox.prefWidthProperty().bind(widthProperty());
        labelVBox.prefHeightProperty().bind(heightProperty());

        // create title text
        titleText = new Text();
        titleText.setTextOrigin(VPos.TOP);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setFill(Color.RED);
        // TODO Make font sizes user-settable
//        titleText.setStyle("-fx-font-size: 11;");

        // create description text
        descriptionText = new Text();
        descriptionText.setTextOrigin(VPos.TOP);
//        descriptionText.setStyle("-fx-font-size: 11;");

        // use TextFlow to enable wrapping of the description text within the
        // label bounds
//        descriptionFlow = new TextFlow(descriptionText);
        // only constrain the width, so that the height is computed in
        // dependence on the width
//        descriptionFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));

        // vertically lay out title and description
        labelVBox.getChildren().addAll(titleText, descriptionText);

        // ensure title is always visible (see also #computeMinWidth(double) and
        // #computeMinHeight(double) methods)
        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);

        // wrap shape and VBox in Groups so that their bounds-in-parent is
        // considered when determining the layout-bounds of this visual
        getChildren().addAll(new Group(shape), new Group(labelVBox));
    }

    @Override
    public double computeMinHeight(double width) {
        // ensure title is always visible
        // descriptionFlow.minHeight(width) +
        // titleText.getLayoutBounds().getHeight() + VERTICAL_PADDING * 2;
        return labelVBox.minHeight(width);
    }

    @Override
    public double computeMinWidth(double height) {
        // ensure title is always visible
    	double ttWidth = titleText.getLayoutBounds().getWidth() + (HORIZONTAL_PADDING * 2);
    	double dtWidth = descriptionText.getLayoutBounds().getWidth() + (HORIZONTAL_PADDING * 2);
    	return (dtWidth > ttWidth ? dtWidth : ttWidth);
    }

    @Override
    protected double computePrefHeight(double width) {
        return minHeight(width);
    }

    @Override
    protected double computePrefWidth(double height) {
        return minWidth(height);
//    	return computeMinWidth(height);
    }

    @Override
    public Orientation getContentBias() {
        return Orientation.HORIZONTAL;
    }

    public Text getDescriptionText() {
        return descriptionText;
    }

    public GeometryNode<?> getGeometryNode() {
        return shape;
    }

    public Text getTitleText() {
        return titleText;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public void setDescription(String description) {
        this.descriptionText.setText(description);
    }

    public void setTitle(String title) {
        this.titleText.setText(title);
    }

}
