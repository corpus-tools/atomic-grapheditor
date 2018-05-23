/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.parts;

import java.util.Map;

import javax.inject.Inject;

import org.corpus_tools.atomic.grapheditor.model.InfoNode;
import org.corpus_tools.atomic.grapheditor.model.Subgraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import com.google.inject.Injector;

import javafx.scene.Node;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SubgraphPartFactory implements IContentPartFactory {

	@Inject
	private Injector injector;

	@Override
	public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {

		if (content instanceof InfoNode) {
			return injector.getInstance(InfoNodePart.class);
		}
		else if (content instanceof Subgraph) {
			return injector.getInstance(SubgraphPart.class);
		}
		else if (content instanceof SToken) {
			return injector.getInstance(TokenPart.class);
		}
		else if (content instanceof SSpan) {
			return injector.getInstance(SpanPart.class);
		}
		else if (content instanceof SStructure) {
			return injector.getInstance(StructurePart.class);
		}
		else if (content instanceof SDominanceRelation) {
			return injector.getInstance(AbstractRelationPart.class);
		}
		else {
			throw new IllegalArgumentException(content.getClass().toString());
		}
	}
}
