/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.corpus_tools.salt.core.SNode;

import com.google.common.collect.Lists;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SubGraph {
	
	public static final String PROP_CHILD_ELEMENTS = "childElements";
	
	private List<SNode> childElements = Lists.newArrayList();
	
	// Property change support (bean feature)

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    

    public void addChildElement(SNode node) {
        childElements.add(node);
        pcs.firePropertyChange(PROP_CHILD_ELEMENTS, null, node);
    }

    public void addChildElement(SNode node, int idx) {
        childElements.add(idx, node);
        pcs.firePropertyChange(PROP_CHILD_ELEMENTS, null, node);
    }

    public List<SNode> getChildElements() {
        return childElements;
    }

    public void removeChildElement(SNode node) {
        childElements.remove(node);
        pcs.firePropertyChange(PROP_CHILD_ELEMENTS, node, null);
    }
}
