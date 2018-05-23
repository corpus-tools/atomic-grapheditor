/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.model.visualization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class LevelExtractor {

	private static final Logger log = LogManager.getLogger(LevelExtractor.class);
	private Set<SToken> subgraphTokens;
	
	/**
	 * // TODO Add description
	 * 
	 * @param tokens
	 */
	public LevelExtractor(Set<SToken> tokens) {
		this.subgraphTokens = tokens;
	}

	/**
	 * // TODO Add description
	 * 
	 * @param graph
	 * @param spans
	 * @param tokenCoords
	 * @return
	 */
	public LinkedHashMap<AnnotationSet, ArrayList<DisplayLevel>> computeSpanLevels(SDocumentGraph graph,
			Set<SSpan> spans, Map<SToken, DisplayCoordinates> tokenCoords) {
		LinkedHashMap<AnnotationSet, ArrayList<DisplayLevel>> levelsByAnnotation = new LinkedHashMap<>();
		// Create a new list of levels for each set of qualified annotation names
		for (SSpan span : spans) {
			levelsByAnnotation.put(new AnnotationSet(span.getAnnotations()), new ArrayList<DisplayLevel>());
		}
		for (SSpan span : spans) {
			// Use only the tokens that are also in the text!
			List<SToken> tokens = graph.getOverlappedTokens(span);
			boolean retainTokens = tokens.retainAll(subgraphTokens);
			log.trace("List of tokens to calculate coordinates for is not full list of spanned tokens for span {}: {}.", span.getIdentifier(), retainTokens);
			List<SToken> sortedTokens = graph.getSortedTokenByText(tokens);
			addAnnotationsForSpan(span, tokenCoords, sortedTokens, levelsByAnnotation);
		}
		
			// Get the left-most and right-most coordinates for each span
//			double left = tokenCoords.get(sortedTokens.get(0)).getLeft();
//			double right = tokenCoords.get(sortedTokens.get(sortedTokens.size() - 1)).getRight();

//		}

		// Merge rows
		for (Map.Entry<AnnotationSet, ArrayList<DisplayLevel>> e : levelsByAnnotation.entrySet()) {
			mergeAllRowsIfPossible(e.getValue());
		}
		return levelsByAnnotation;
	}

//	/**
//	 * // TODO Add description
//	 * 
//	 * @param graph
//	 * @param structures
//	 * @param tokenCoords
//	 * @return
//	 */
//	public static LinkedHashMap<AnnotationSet, ArrayList<DisplayLevel>> computeStructureLevels(SDocumentGraph graph,
//			Set<SStructure> structures, Map<SToken, DisplayCoordinates> tokenCoords) {
//
//		for (SStructure structure : structures) {
////			List<SToken> tokens = graph.getOverlappedTokens(structure);
////			List<SToken> sortedTokens = graph.getSortedTokenByText(tokens);
////			addAnnotationsForStructure(structure, tokenCoords, sortedTokens, levelsByAnnotation);
//		}
//		
//			// Get the left-most and right-most coordinates for each span
////			double left = tokenCoords.get(sortedTokens.get(0)).getLeft();
////			double right = tokenCoords.get(sortedTokens.get(sortedTokens.size() - 1)).getRight();
//
////		}
//
////		// Merge rows
////		for (Map.Entry<AnnotationSet, ArrayList<DisplayLevel>> e : levelsByAnnotation.entrySet()) {
////			mergeAllRowsIfPossible(e.getValue());
////		}
//
////		return levelsByAnnotation;
//	}

	private void addAnnotationsForSpan(SSpan span, Map<SToken, DisplayCoordinates> tokenCoords, List<SToken> sortedTokens, LinkedHashMap<AnnotationSet,ArrayList<DisplayLevel>> levelsByAnnotation) {
		for (Entry<SToken, DisplayCoordinates> entry : tokenCoords.entrySet()) {
		}
		SToken firstToken = sortedTokens.get(0);
		SToken lastToken = sortedTokens.get(sortedTokens.size() - 1);
		DisplayCoordinates firstCoords = tokenCoords.get(firstToken);
		DisplayCoordinates lastCoords = tokenCoords.get(lastToken);
		double left = firstCoords.getLeft();
		double right = lastCoords.getRight();
		DisplaySpan displaySpan = new DisplaySpan(span, left, right);
		// FIXME: Don't calculate for all annotations, calculate for set of
		// annotations!
		AnnotationSet annoSet = new AnnotationSet(span.getAnnotations());
		ArrayList<DisplayLevel> levels = levelsByAnnotation.get(annoSet);
		if (levels != null) {
			// only do something if the annotation was defined before

			// 1. give each set of annotations of each span an own row
			DisplayLevel level = new DisplayLevel();

			for (SToken t : sortedTokens) {
				displaySpan.getCoveredIDs().add(t.getId());
			}
			level.addDisplaySpan(displaySpan);
			levels.add(level);
		}
	}

	private void mergeAllRowsIfPossible(ArrayList<DisplayLevel> levels) {
		/*
		 * Use fixed seed in order to get consistent results (with random
		 * properties)
		 */
		Random rand = new Random(5711l);
		int tries = 0;
		// this should be enough to be quite sure we don't miss any
		// optimalization
		// possibility
		final int maxTries = levels.size() * 2;

		// do this loop until we successfully merged everything into one row
		// or we give up until too much tries
		while (levels.size() > 1 && tries < maxTries) {
			// choose two random entries
			int oneIdx = rand.nextInt(levels.size());
			int secondIdx = rand.nextInt(levels.size());
			if (oneIdx == secondIdx) {
				// try again if we choose the same rows by accident
				continue;
			}

			DisplayLevel one = levels.get(oneIdx);
			DisplayLevel second = levels.get(secondIdx);

			if (one.merge(second)) {
				// remove the second one since it is merged into the first
				levels.remove(secondIdx);

				// success: reset counter
				tries = 0;
			}
			else {
				// increase counter to avoid endless loops if no improvement is
				// possible
				tries++;
			}
		}
	}

}
