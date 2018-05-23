/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.tests;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.corpus_tools.atomic.grapheditor.model.visualization.LongestDistanceTraverser;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SGraph.GRAPH_TRAVERSE_TYPE;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Unit tests for {@link LongestDistanceTraverser}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class LongestDistanceTraverserTest {
	
	private LongestDistanceTraverser fixture;
	private SDocumentGraph graph;
	private SStructure s4;
	private SStructure s5;
	private SStructure s3;
	private SStructure s2;
	private SStructure s1;

	/**
	 * Sets the fixture and creates the {@link SDocumentGraph} to test on.
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new LongestDistanceTraverser());
		createGraph();
	}

	/**
	 * Test method for {@link org.corpus_tools.atomic.grapheditor.model.visualization.LongestDistanceTraverser#getDistance()}.
	 * 
	 * Build a graph with the following setup and
	 * start traversals from nodes S1, S2, S3, S4
	 * which should yield the following distances:
	 * 
	 * - S1: 4
	 * - S2: 3
	 * - S3: 2
	 * - S4: 1
	 * 
	 * ````
	 * 
	 *                      +--+
	 *                      |S1|
	 *                  +---+--++
	 *                  |       |
	 *                  |       |
	 *                +-v+      |
	 *                |S2|      |
	 *            +---+-++      |
	 *            |     |       |
	 *            |     |       |
	 *          +-v+    |       |
	 *          |S3|    |       |
	 *      +---+--++   |       |
	 *      |       |   |       |
	 *      |       |   |       |
	 *    +-v+      |  +v-+     |
	 *    |S4|      |  |S5|     |
	 *   ++--++     | ++--++    |
	 *   |    |     | |    |    |
	 *   |    |     | |    |    |
	 * +-v+  +v-+  +v-v  +-v+  +v-+
	 * |T1|  |T2|  |T3|  |T4|  |T5|
	 * +--+  +--+  +--+  +--+  +--+
	 * 	 
	 * ````
	 */
	@Test
	public final void testGetDistanceForS1() {
		graph.traverse(Arrays.asList(new SNode[] {s1}), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "TDDF", getFixture());
		assertEquals(Integer.valueOf(4), getFixture().getDistance());
	}

	/**
	 * Cf. {@link LongestDistanceTraverserTest#testGetDistanceForS1()}.
	 */
	@Test
	public final void testGetDistanceForS2() {
		graph.traverse(Arrays.asList(new SNode[] {s2}), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "TDDF", getFixture());
		assertEquals(Integer.valueOf(3), getFixture().getDistance());
	}

	/**
	 * Cf. {@link LongestDistanceTraverserTest#testGetDistanceForS1()}. 
	 */
	@Test
	public final void testGetDistanceForS3() {
		graph.traverse(Arrays.asList(new SNode[] {s3}), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "TDDF", getFixture());
		assertEquals(Integer.valueOf(2), getFixture().getDistance());
	}

	/**
	 * Cf. {@link LongestDistanceTraverserTest#testGetDistanceForS1()}. 
	 */
	@Test
	public final void testGetDistanceForS4() {
		graph.traverse(Arrays.asList(new SNode[] {s4}), GRAPH_TRAVERSE_TYPE.TOP_DOWN_DEPTH_FIRST, "TDDF", getFixture());
		assertEquals(Integer.valueOf(1), getFixture().getDistance());
	}

	/*
	 * Build the graph detailed under #testGetDistance().
	 * 
	 * Tokens are:
	 *  T 1 T 2 T 3 T 4 T 5
	 * 0 1 2 3 4 5 6 7 8 9 10
	 */
	private void createGraph() {
		graph = SaltFactory.createSDocumentGraph();
		STextualDS ds = graph.createTextualDS("T1T2T3T4T5");
		SToken t1 = graph.createToken(ds, 0, 2);
		SToken t2 = graph.createToken(ds, 2, 4);
		SToken t3 = graph.createToken(ds, 4, 6);
		SToken t4 = graph.createToken(ds, 6, 8);
		SToken t5 = graph.createToken(ds, 8, 10);
		s4 = graph.createStructure(Arrays.asList(new SToken[] {t1, t2}));
		s5 = graph.createStructure(Arrays.asList(new SToken[] {t3, t4}));
		s3 = graph.createStructure(Arrays.asList(new SStructuredNode[] {s4, t3}));
		s2 = graph.createStructure(Arrays.asList(new SStructuredNode[] {s3, s5}));
		s1 = graph.createStructure(Arrays.asList(new SStructuredNode[] {s2, t5}));
	}
	
	/**
	 * Internal graph test.
	 */
	@Test
	public void testGraph() {
		assertEquals(5, graph.getTokens().size());
		assertEquals("T1", graph.getText(graph.getTokens().get(0)));
		assertEquals("T2", graph.getText(graph.getTokens().get(1)));
		assertEquals("T3", graph.getText(graph.getTokens().get(2)));
		assertEquals("T4", graph.getText(graph.getTokens().get(3)));
		assertEquals("T5", graph.getText(graph.getTokens().get(4)));
		assertEquals(5, graph.getStructures().size());
	}

	/**
	 * @return the fixture
	 */
	private final LongestDistanceTraverser getFixture() {
		return fixture;
	}

	/**
	 * @param fixture the fixture to set
	 */
	private final void setFixture(LongestDistanceTraverser fixture) {
		this.fixture = fixture;
	}
	
	@Test
	public void buiMapTest() {
		Map<String, Integer> map = new HashMap<>();
		map.put("One", 1);
		map.put("Two", 2);
		map.put("AnotherTwo", 2);
		map.put("Three", 3);
		map.put("Seven", 7);
		map.put("AnotherSeven", 7);
		map.put("Ten", 10);
		Multimap<Integer, String> mMap = HashMultimap.create();
		for (Entry<String, Integer> entry : map.entrySet()) {
			mMap.put(entry.getValue(), entry.getKey());
		}
		List<Integer> uniques = map.values().stream().distinct().sorted().collect(Collectors.toList()); 
		Map<String, Integer> fMap = new HashMap<>();
		for (int i = 0; i < uniques.size(); i++) {
			for (String structure : mMap.get(uniques.get(i))) {
				fMap.put(structure, i+1);
			}
		}
		assertEquals(fMap.get("One"), Integer.valueOf(1));
		assertEquals(fMap.get("Two"), Integer.valueOf(2));
		assertEquals(fMap.get("AnotherTwo"), Integer.valueOf(2));
		assertEquals(fMap.get("Three"), Integer.valueOf(3));
		assertEquals(fMap.get("Seven"), Integer.valueOf(4));
		assertEquals(fMap.get("AnotherSeven"), Integer.valueOf(4));
		assertEquals(fMap.get("Ten"), Integer.valueOf(5));
	}

}
