/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.parts;

import org.corpus_tools.atomic.grapheditor.SubgraphAnchorProvider;
import org.corpus_tools.atomic.grapheditor.parts.AbstractRelationPart;
import org.corpus_tools.atomic.grapheditor.parts.StructuredNodePart;
import org.corpus_tools.atomic.grapheditor.parts.SubgraphPartFactory;
import org.corpus_tools.atomic.grapheditor.parts.TokenPart;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.behaviors.HoverBehavior;
import org.eclipse.gef.mvc.fx.handlers.FocusAndSelectOnClickHandler;
import org.eclipse.gef.mvc.fx.handlers.HoverOnHoverHandler;
import org.eclipse.gef.mvc.fx.parts.DefaultHoverFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.parts.DefaultSelectionFeedbackPartFactory;
import org.eclipse.gef.mvc.fx.providers.ShapeBoundsProvider;
import org.eclipse.gef.mvc.fx.providers.ShapeOutlineProvider;

import com.google.inject.multibindings.MapBinder;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GraphEditorModule extends MvcFxModule {
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.mvc.fx.MvcFxModule#bindIContentPartFactoryAsContentViewerAdapter(com.google.inject.multibindings.MapBinder)
	 */
	@Override
    protected void bindIContentPartFactoryAsContentViewerAdapter(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
        // bind MindMapPartsFactory adapter to the content viewer
        adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SubgraphPartFactory.class);
    }

    /**
     * // TODO Add description
     * 
     * @param adapterMapBinder
     */
    protected void bindSubgraphNodePartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
        // bind anchor provider used to create the connection anchors
        adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(SubgraphAnchorProvider.class);

        // bind a geometry provider, which is used in our anchor provider
        adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(ShapeOutlineProvider.class);
        
        // provides a hover feedback to the shape, used by the HoverBehavior
        // https://www.itemis.com/en/gef/tutorials/part-2-gef-mvc/
        AdapterKey<?> role = AdapterKey.role(DefaultHoverFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER);
        adapterMapBinder.addBinding(role).to(ShapeOutlineProvider.class);

		// provides a selection feedback to the shape
        // https://www.itemis.com/en/gef/tutorials/part-2-gef-mvc/
		role = AdapterKey.role(DefaultSelectionFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER);
		adapterMapBinder.addBinding(role).to(ShapeBoundsProvider.class);
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.mvc.fx.MvcFxModule#configure()
     */
    @Override
    protected void configure() {
        super.configure();
        bindSubgraphNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), StructuredNodePart.class));
        bindSubgraphNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), TokenPart.class));
        bindSubgraphNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), AbstractRelationPart.class));
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.mvc.fx.MvcFxModule#bindAbstractContentPartAdapters(com.google.inject.multibindings.MapBinder)
     * 
     * https://www.itemis.com/en/gef/tutorials/part-2-gef-mvc/
     */
    @Override
    protected void bindAbstractContentPartAdapters(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
        super.bindAbstractContentPartAdapters(adapterMapBinder);

        // binding the HoverOnHoverPolicy to every part
        // if a mouse is moving above a part it is set i the HoverModel
        adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverOnHoverHandler.class);

        // add the focus and select policy to every part, listening to clicks
        // and changing the focus and selection model
        adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(FocusAndSelectOnClickHandler.class);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.gef.mvc.fx.MvcFxModule#bindIRootPartAdaptersForContentViewer(com.google.inject.multibindings.MapBinder)
     * 
     * https://www.itemis.com/en/gef/tutorials/part-2-gef-mvc/
     */
    @Override
    protected void bindIRootPartAdaptersForContentViewer(MapBinder<AdapterKey<?>, Object> adapterMapBinder) {
        super.bindIRootPartAdaptersForContentViewer(adapterMapBinder);

        // binding a Hover Behavior to the root part. it will react to
        // HoverModel changes and render the hover part
        adapterMapBinder.addBinding(AdapterKey.defaultRole()).to(HoverBehavior.class);
    }


}
