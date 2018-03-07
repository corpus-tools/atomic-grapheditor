
package org.corpus_tools.atomic.grapheditor.ui.handlers;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
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

	/**
	 * // TODO Add description
	 * 
	 * @param window
	 * @param application
	 * @param modelService
	 * @param partService
	 */
	@Execute
	public void changePerspective(MWindow window, MApplication application, EModelService modelService,
			EPartService partService) {

		MPerspective graphPerspective = null;
		List<MUIElement> snips = application.getSnippets();
		for (MUIElement snip : snips) {
			if (snip.getElementId().equals(perspectiveId)) {
				if (snip instanceof MPerspective) {
					graphPerspective = (MPerspective) snip;
				}
			}
		}
		if (graphPerspective != null) {

			MPerspective activePerspective = modelService.getActivePerspective(window);
			MPerspectiveStack perspectiveStack = (MPerspectiveStack) (MElementContainer<?>) activePerspective
					.getParent();
			perspectiveStack.getChildren().add(graphPerspective);
			partService.switchPerspective(graphPerspective);
		}
		else {
			MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Cannot open graphed editing perspetive", "Cannot open graph editing perspective. Please open it from the perspective switcher in the toolbar to proceed.");
		}
	}
}