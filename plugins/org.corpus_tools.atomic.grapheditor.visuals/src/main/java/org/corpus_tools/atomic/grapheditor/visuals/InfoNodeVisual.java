/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.visuals;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class InfoNodeVisual extends Pane {
	
	Text infoTitle = new Text();
	Text infoDescription = new Text();

	public InfoNodeVisual() {
		super();
		setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
		setBorder(new Border(new BorderStroke(null, BorderStrokeStyle.SOLID, null, BorderWidths.DEFAULT)));
		BorderPane bp = new BorderPane();
		bp.setTop(infoTitle);
		bp.setCenter(infoDescription);
		bp.setPadding(new Insets(2, 2, 2, 2));
		getChildren().add(bp);
	}

	public void setInfoTitle(String name) {
		this.infoTitle.setText(name);
	}

	public void setInfoDescription(String name) {
		this.infoDescription.setText(name);
	}

}
