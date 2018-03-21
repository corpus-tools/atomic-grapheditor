/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model.visualization;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.corpus_tools.salt.core.SAnnotation;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class AnnotationSet {

	private final Set<String> annotationNames;

	public AnnotationSet(Set<SAnnotation> annotations) {
		this.annotationNames = new HashSet<>();
		for (SAnnotation anno : annotations) {
			annotationNames.add(anno.getQName());
		}
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AnnotationSet)) {
			return false;
		}
		if (object == this) {
			return true;
		}
		AnnotationSet rhs = (AnnotationSet) object;
		return new EqualsBuilder().append(annotationNames, rhs.getAnnotationNames()).isEquals();

	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
				append(this.annotationNames).toHashCode();
	}

	/**
	 * @return the annotationNames
	 */
	public final Set<String> getAnnotationNames() {
		return annotationNames;
	}

}
