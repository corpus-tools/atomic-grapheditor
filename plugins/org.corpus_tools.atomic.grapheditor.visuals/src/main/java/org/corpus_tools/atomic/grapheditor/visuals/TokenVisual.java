/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.visuals;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.RoundedRectangle;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TokenVisual extends Region {
	
	private static final double VERTICAL_SPACING = 5d;
	private static final double VERTICAL_PADDING = 10d;
	private static final double HORIZONTAL_PADDING = 20d;
	private GeometryNode<RoundedRectangle> shape;
	private VBox labelVBox;
	private Text nameText;
	private Text annotationText;
	private TextFlow annotationFlow;
	
	public TokenVisual() {
		// create background shape
		shape = new GeometryNode<>(new RoundedRectangle(0, 0, 70, 30, 8, 8));
		shape.setFill(Color.LIGHTSALMON);
		shape.setStroke(Color.BLACK);
		
		// create vertical box for title and description
        labelVBox = new VBox(VERTICAL_SPACING);
        labelVBox.setPadding(new Insets(VERTICAL_PADDING, HORIZONTAL_PADDING, VERTICAL_PADDING, HORIZONTAL_PADDING));
        
        // ensure shape and labels are resized to fit this visual
        shape.prefWidthProperty().bind(widthProperty());
        shape.prefHeightProperty().bind(heightProperty());
        labelVBox.prefWidthProperty().bind(widthProperty());
        labelVBox.prefHeightProperty().bind(heightProperty());
        
        // create token name text
        nameText = new Text();
        nameText.setTextOrigin(VPos.TOP);
        
        // create annotation text
        annotationText = new Text();
        annotationText.setTextOrigin(VPos.TOP);
        
        // use TextFlow to enable wrapping of the annotation text within the
        // label bounds
        annotationFlow = new TextFlow(annotationText);
        // only constrain the width, so that the height is computed in
        // dependence on the width
        annotationFlow.maxWidthProperty().bind(shape.widthProperty().subtract(HORIZONTAL_PADDING * 2));
        

        // vertically lay out name and annotation
        labelVBox.getChildren().addAll(nameText, annotationFlow);
        
        // ensure name is always visible (see also #computeMinWidth(double) and
        // #computeMinHeight(double) methods)
        setMinSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        
        // wrap shape and VBox in Groups so that their bounds-in-parent is
        // considered when determining the layout-bounds of this visual
        getChildren().addAll(new Group(shape), new Group(labelVBox));
	}
	
    @Override
    public double computeMinHeight(double width) {
        // ensure name is always visible
        // annotationFlow.minHeight(width) +
        // nameText.getLayoutBounds().getHeight() + VERTICAL_PADDING * 2;
        return labelVBox.minHeight(width);
    }

    @Override
    public double computeMinWidth(double height) {
        // ensure name is always visible
        return nameText.getLayoutBounds().getWidth() + HORIZONTAL_PADDING * 2;
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

    public Text getAnnotationText() {
        return annotationText;
    }

    public GeometryNode<?> getGeometryNode() {
        return shape;
    }

    public Text getNameText() {
        return nameText;
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public void setAnnotation(String annotation) {
        this.annotationText.setText(annotation);
    }

    public void setName(String name) {
        this.nameText.setText(name);
    }
	
}
