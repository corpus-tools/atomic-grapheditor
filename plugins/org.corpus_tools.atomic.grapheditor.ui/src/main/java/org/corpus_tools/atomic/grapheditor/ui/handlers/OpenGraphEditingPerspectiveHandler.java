
package org.corpus_tools.atomic.grapheditor.ui.handlers;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.ui.parts.SegmentationView;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class OpenGraphEditingPerspectiveHandler {

	private static final Logger log = LogManager.getLogger(OpenGraphEditingPerspectiveHandler.class);

	private static final String perspectiveId = "org.corpus_tools.atomic.grapheditor.ui.perspective.graphediting";
	private static final String segmentationPartId = "org.corpus_tools.atomic.grapheditor.ui.part.segmentation";

	/**
	 * // TODO Add description
	 * 
	 * TODO Use something other than index-based checking (e.g., iterating the
	 * perspective stack) to look for the perspective, as other plugins
	 * can easily add something else to index 0 and thus break this
	 * logic.
	 * 
	 * @param window
	 * @param application
	 * @param modelService
	 * @param partService
	 */
	@Execute
	public void changePerspective(MWindow window, MApplication application, EModelService modelService,
			EPartService partService) {

		/*
		 * At startup, the graph editing perspective is not in the main
		 * perspective stack, hence needs to be copied there.
		 */
		MPerspective graphPerspective = null;
		List<MUIElement> snips = application.getSnippets();
		for (MUIElement snip : snips) {
			if (snip.getElementId().equals(perspectiveId)) {
				if (snip instanceof MPerspective) {
					graphPerspective = (MPerspective) snip;
				}
			}
		}
		/* 
		 * If the perspective has been found in the snippets, add it
		 * to the main perspective stack (at index 0) and show it.
		 */
		if (graphPerspective != null) {
			MPerspective activePerspective = modelService.getActivePerspective(window);
			MPerspectiveStack perspectiveStack = (MPerspectiveStack) (MElementContainer<?>) activePerspective
					.getParent();
			perspectiveStack.getChildren().add(0, graphPerspective);
			partService.switchPerspective(graphPerspective);
		}
		/*
		 * Perspective is null, hence hasn't been found in snippets.
		 * This should mean that it had already been moved onto the
		 * main perspective stack at index 0. Check if that's the case
		 * and if so, show it.
		 */
		else {
			MPerspective activePerspective = modelService.getActivePerspective(window);
			MPerspectiveStack perspectiveStack = (MPerspectiveStack) (MElementContainer<?>) activePerspective
					.getParent();
			graphPerspective = perspectiveStack.getChildren().get(0);
			if (graphPerspective.getElementId().equals(perspectiveId)) {
				partService.switchPerspective(graphPerspective);
			}
			/*
			 * Could not find the correct perspective, hence show messsage to 
			 * user to just use the perspective switcher in the GUI.
			 */
			else {
				MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Cannot open graphed editing perspective", "Cannot open graph editing perspective. Please open it from the perspective switcher in the toolbar to proceed.");
			}
		}
		/*
		 *  Activate the segmentation part to avoid it not picking up
		 *  selections before it has been first activated.
		 */
		partService.showPart(segmentationPartId, PartState.ACTIVATE);
		partService.requestActivation();
	}
}