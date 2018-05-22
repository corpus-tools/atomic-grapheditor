/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.constants;

/**
 * Graph Editor Processing Constants
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public interface GEProcConstants {
	
	final String QNAME_SEPARATOR = "::";
	final String NAMESPACE = "o.ct.a.grapheditor.ui";
	final String X_COORDINATE = "xcoord";
	final String Y_COORDINATE = "ycoord";
	final String XCOORD_QNAME = NAMESPACE + QNAME_SEPARATOR + X_COORDINATE;
	final String YCOORD_QNAME = NAMESPACE + QNAME_SEPARATOR + Y_COORDINATE;
	final String WIDTH = "width";
	final String WIDTH_QNAME = NAMESPACE + QNAME_SEPARATOR + WIDTH;
	final double HORIZONTAL_PADDING = 5d;
	final double MIN_TOKEN_WIDTH = 100d;
	final double MIN_TOKEN_HEIGHT = 20d;
}
