/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.parts;

import org.corpus_tools.atomic.grapheditor.parts.SubGraphPartFactory;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GraphEditorModule extends MvcFxModule {
	
	@Override
	protected void configure() {
		super.configure();
		bindIContentPartFactory();
	}

	protected void bindIContentPartFactory() {
		binder().bind(IContentPartFactory.class).to(SubGraphPartFactory.class);
	}


}
