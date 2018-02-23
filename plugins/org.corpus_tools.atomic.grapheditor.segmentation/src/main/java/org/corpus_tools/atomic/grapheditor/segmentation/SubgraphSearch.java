/**
 * 
 */
package org.corpus_tools.atomic.grapheditor.segmentation;

import annis.service.objects.MatchGroup;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.util.SaltUtil;
import org.corpus_tools.search.service.SearchService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SubgraphSearch {
	
	private final SDocumentGraph graph;
	
	/**
	 * // TODO Add description
	 * 
	 * @param graph
	 */
	public SubgraphSearch(SDocumentGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * // TODO Add description
	 * @return 
	 * 
	 * @throws NullPointerException
	 */
	public SortedSet<SAnnotation> searchAnnotations() throws NullPointerException {
		final SortedSet<SAnnotation> annotationNames = new TreeSet<>();
		if (graph == null) {
			throw new NullPointerException("Document graph is null");
		}
		Iterator<SAnnotation> it = graph.iterator_SAnnotation();
		while (it.hasNext()) {
			SAnnotation anno = it.next();
			if (anno.getContainer() instanceof SSpan) {
				annotationNames.add(anno);
			}
		}
		return annotationNames;
	}
	
	public MatchGroup getSubgraphsForAnnotation(String qualifiedName, String value) {
		SearchService search = new SearchService();
		// Only search within the graph, i.e.:
		// Delete index first
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if(page != null) {
			page.saveAllEditors(true);
		}
		
		Job job = new Job("Re-indexing documents") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				// get all documents of workspace
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = workspace.getRoot();
				
				Multimap<String, URI> docList = LinkedHashMultimap.create();
				
				SubMonitor monitorDelete = SubMonitor.convert(monitor, root.getProjects().length);
				monitorDelete.setTaskName("Deleting old corpora from index");
				int indexProjects = 0;
				for(IProject p : root.getProjects()) {
					// delete all old documents first					
					search.deleteCorpus(p.getName());
					monitorDelete.worked(indexProjects++);
				}
				
				SubMonitor monitorImport = SubMonitor.convert(monitor, docList.size());
				monitorImport.setTaskName("Indexing document");
				// index each document
				search.addDocument(graph.getDocument().getName(), graph);
				monitorImport.worked(1);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		
		MatchGroup result = search.find(qualifiedName + "=\""+ value + "\" & node & #1 _i_ #2");
		return result;
	}
	
	

}
