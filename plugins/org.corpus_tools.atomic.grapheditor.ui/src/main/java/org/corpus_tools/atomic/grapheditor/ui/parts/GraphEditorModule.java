/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.ui.parts;

import org.corpus_tools.atomic.grapheditor.SubgraphAnchorProvider; 
import org.corpus_tools.atomic.grapheditor.parts.StructuredNodePart;
import org.corpus_tools.atomic.grapheditor.parts.SubgraphPartFactory;
import org.corpus_tools.atomic.grapheditor.parts.TokenPart;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.inject.AdapterMaps;
import org.eclipse.gef.mvc.fx.MvcFxModule;
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
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.mvc.fx.MvcFxModule#configure()
     */
    @Override
    protected void configure() {
        super.configure();
        bindSubgraphNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), StructuredNodePart.class));
        bindSubgraphNodePartAdapters(AdapterMaps.getAdapterMapBinder(binder(), TokenPart.class));
    }


}
