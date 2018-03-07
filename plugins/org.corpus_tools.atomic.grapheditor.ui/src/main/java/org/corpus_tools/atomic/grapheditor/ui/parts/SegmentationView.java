package org.corpus_tools.atomic.grapheditor.ui.parts;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import annis.exceptions.AnnisQLSemanticsException;
import annis.exceptions.AnnisQLSyntaxException;
import annis.service.objects.Match;
import annis.service.objects.MatchGroup;

import static org.assertj.core.api.Assertions.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.corpus_tools.graphannis.API;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grapheditor.ui.parts.SegmentationView.Segment;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.util.SaltUtil;
import org.corpus_tools.search.service.SearchService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SegmentationView {

	@Inject
	private UISynchronize uiSync;

	private static final Logger log = LogManager.getLogger(SegmentationView.class);

	private static final String LBL_DEFAULT_VALUE = "Select a Salt document file.";

	@Inject
	private SearchService search;

	@Inject
	ESelectionService selectionService;

	String documentPath = null;

	private Label lblSelectedDocumentFile;

	private SDocumentGraph graph = null;

	private SortedSetMultimap<String, String> annotations = null;

	private Button btnLoadGraph;

	private IFile documentFile = null;
	private Combo comboQName;

	private Combo comboValue;

	private Button btnLoadSegmentation;

	protected String currentQName;

	protected String currentValue;

	private TableViewer viewer;

	private TableColumn segmentationTableColumn;

	private Button btnAnnotate;

	/**
	 * // TODO Add description
	 * 
	 * @param parent
	 */
	@PostConstruct
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLocation(168, 0);
		composite.setLayout(new GridLayout(3, false));

		lblSelectedDocumentFile = new Label(composite, SWT.NONE);
		lblSelectedDocumentFile.setText(LBL_DEFAULT_VALUE);
		GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(lblSelectedDocumentFile);

		/*
		 *  Click on button loads the document graph currently selected
		 *  in the navigation view
		 */
		btnLoadGraph = new Button(composite, SWT.PUSH);
		btnLoadGraph.setText("Load document");
		btnLoadGraph.setEnabled(false);
		GridDataFactory.fillDefaults().span(1, 1).grab(false, false).align(SWT.RIGHT, SWT.CENTER).applyTo(btnLoadGraph);
		btnLoadGraph.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (documentFile != null) {
					BusyIndicator.showWhile(Display.getCurrent(), () -> graph = SaltUtil
							.loadDocumentGraph(URI.createURI(documentFile.getLocationURI().toASCIIString())));
					SegmentationView.this.initializeCombos();
					SegmentationView.this.viewer.setInput(null);
				}
				else {
					MessageDialog.openError(parent.getShell(), "Cannot load graph",
							"Cannot load the document graph as the document file seems to be invalid.");
				}
			}
		});
		Label lblQName = new Label(composite, SWT.NONE);
		lblQName.setText("Annotation name");
		GridDataFactory.fillDefaults().span(1, 1).applyTo(lblQName);

		Label lblValue = new Label(composite, SWT.NONE);
		lblValue.setText("Annotation value");
		GridDataFactory.fillDefaults().span(1, 1).applyTo(lblValue);

		new Label(composite, SWT.NONE);

		/*
		 * Combobox showing all available qualified names (namespace::name)
		 * for annotations available in the document graph's nodes.
		 * On selection, sets the available *values* for the selected
		 * qualified annotation name to the values combobox. Also
		 * saves the selected qualified name in the respective field.
		 */
		comboQName = new Combo(composite, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(1, 1).grab(true, false).applyTo(comboQName);
		comboQName.setEnabled(false);
		comboQName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selection = comboQName.getItem(comboQName.getSelectionIndex());
				BusyIndicator.showWhile(Display.getCurrent(), () -> comboValue.setItems(annotations.get(selection).stream().toArray(v -> new String[v])));
				comboValue.setEnabled(true);
				SegmentationView.this.currentQName = selection;
			}
		});

		/*
		 * Combobox showing the valid values for the selected qualified annotation
		 * name. On selection, activates the load segmentation button and
		 * saves the current value in the respective field.
		 */
		comboValue = new Combo(composite, SWT.READ_ONLY);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(1, 1).grab(true, false).applyTo(comboValue);
		comboValue.setEnabled(false);
		comboValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String selection = comboValue.getItem(comboValue.getSelectionIndex());
				SegmentationView.this.currentValue = selection;
				btnLoadSegmentation.setEnabled(true);
			}
		});

		/*
		 * Button triggers searching FIXME all corpora? FIXME
		 * for nodes with qName=value, and fills the table with the
		 * results.
		 */
		btnLoadSegmentation = new Button(composite, SWT.PUSH);
		btnLoadSegmentation.setText("Load segmentation");
		GridDataFactory.fillDefaults().span(1, 1);
		btnLoadSegmentation.setEnabled(false);
		btnLoadSegmentation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String aqlQName = (currentQName.contains("::") ? currentQName.replace("::", ":") : currentQName);
				String segmentationString = aqlQName + "=\"" + currentValue + "\"";

				Job j = new Job("Retrieving segmentation") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						monitor.subTask("Searching for segmentation units");
						MatchGroup result = search.find(segmentationString);
						monitor.beginTask("Retrieving text for segmentation units", result.getMatches().size());
						uiSync.asyncExec(() -> {
							List<Segment> segments = new ArrayList<>();
							try {

								for (int i = 0; i < result.getMatches().size(); i++) {
									Match m = result.getMatches().get(i);
									List<java.net.URI> ids = m.getSaltIDs();
									assert ids.size() == 1;
									SNode node = graph.getNode(ids.get(0).toString());
									List<SToken> unsortedOverlappedTokens = graph.getOverlappedTokens(node);
									List<SToken> sortedOverlappedTokens = graph
											.getSortedTokenByText(unsortedOverlappedTokens);
									String segmentText = "";
									for (SToken tok : sortedOverlappedTokens) {
										segmentText = segmentText.concat(graph.getText(tok).concat(" "));
									}
									segments.add(new Segment(segmentText.trim(), node));
									monitor.worked(1);
								}

							}
							catch (AnnisQLSyntaxException | AnnisQLSemanticsException ex) {

								MessageDialog.openError(parent.getShell(), "Error parsing AQL", ex.getMessage());
							}
							segmentationTableColumn.setText("Segments: " + String.valueOf(segments.size()));
							SegmentationView.this.viewer.setInput(segments.toArray(new Segment[segments.size()]));
							SegmentationView.this.viewer.refresh();
						});
						return Status.OK_STATUS;
					}

				};
				j.setUser(true);
				j.setPriority(Job.LONG);
				j.schedule();
			}
		});

		viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		final TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
        segmentationTableColumn = col.getColumn();
        segmentationTableColumn.setText("Segments");
        segmentationTableColumn.setWidth(100);
        segmentationTableColumn.setResizable(false);
        segmentationTableColumn.setMoveable(false);
		col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
            	if (element instanceof Segment) {
					return element == null ? "" : ((Segment) element).getText();
				}
				return "";
            }
        });
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		
		final Table table = viewer.getTable();
		GridDataFactory.fillDefaults().span(3, 1).grab(true, true).applyTo(table);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
		    @Override
		    public void selectionChanged(SelectionChangedEvent event) {
		        IStructuredSelection selection = viewer.getStructuredSelection();
		        selectionService.setSelection(selection);
		        if (!selection.isEmpty() && selection.getFirstElement() instanceof Segment) {
		        	btnAnnotate.setEnabled(true);
		        }
		        else {
		        	btnAnnotate.setEnabled(false);
		        }
		    }
		});

		// create the columns
		// not yet implemented
		// createColumns(viewer);

		// make lines and header visible
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		btnAnnotate = new Button(composite, SWT.NONE);
		GridDataFactory.fillDefaults().span(1, 1).grab(false, false).align(SWT.RIGHT, SWT.CENTER).applyTo(btnAnnotate);
		btnAnnotate.setText("Annotate");
		btnAnnotate.setEnabled(false);
		btnAnnotate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				
				Job j = new Job("Building annotation graph for selected segments") {

					@Override
					protected IStatus run(IProgressMonitor monitor) {
						List<MatchGroup> matchGroups = new ArrayList<>();
						
						@SuppressWarnings("rawtypes")
						List selectionList = selection.toList();
						int units = selectionList.size();
						monitor.subTask("Building graph for nodes.");
						monitor.beginTask("Searching for nodes linked to segments", units);
						for (Object s : selectionList) {
							if (s instanceof Segment) {
								SNode n = ((Segment) s).getNode();
								String nodeAQL = n.getId().replaceAll("salt:/", "");
								MatchGroup matchGroup = null;
								try {
									matchGroup = search.find("annis:node_name=\"" + nodeAQL + "\" & node & #1 _i_ #2");
								}
								catch (AnnisQLSyntaxException | AnnisQLSemanticsException ex) {
									MessageDialog.openError(parent.getShell(), "Error parsing AQL", ex.getMessage());
									return null;
								}
								matchGroups.add(matchGroup);
								monitor.worked(1);
							}
						}
						log.trace("RESULTS: \n\n\n");
						for (MatchGroup g : matchGroups) {
							log.trace("MATCHGROUP " + g.getMatches().toString());
						}
//						
//						for (SNode node : selectedNodes) {
//							log.trace("NODE NAME " + node.getName());
////							MatchGroup result = search.
////									// find("node_name=\"" + node.getName()+ "\"");
////							log.trace("RESULT FOR NODE {}: {}", node.getName(), result.getMatches().size());
//							monitor.worked(1);
//						}
//						uiSync.asyncExec(() -> {
//							List<Segment> segments = new ArrayList<>();
//							try {
//
//								for (int i = 0; i < result.getMatches().size(); i++) {
//									Match m = result.getMatches().get(i);
//									List<SToken> tokenList = new ArrayList<>();
//									List<java.net.URI> ids = m.getSaltIDs();
//									assert ids.size() == 1;
//									SNode node = graph.getNode(ids.get(0).toString());
//									List<SToken> unsortedOverlappedTokens = graph.getOverlappedTokens(node);
//									List<SToken> sortedOverlappedTokens = graph
//											.getSortedTokenByText(unsortedOverlappedTokens);
//									String segmentText = "";
//									for (SToken tok : sortedOverlappedTokens) {
//										segmentText = segmentText.concat(graph.getText(tok).concat(" "));
//									}
//									segments.add(new Segment(segmentText.trim(), node));
//									monitor.worked(1);
//								}
//
//							}
//							catch (AnnisQLSyntaxException | AnnisQLSemanticsException ex) {
//
//								MessageDialog.openError(parent.getShell(), "Error parsing AQL", ex.getMessage());
//							}
//							segmentationTableColumn.setText("Segments: " + String.valueOf(segments.size()));
//							SegmentationView.this.viewer.setInput(segments.toArray(new Segment[segments.size()]));
//							SegmentationView.this.viewer.refresh();
//						});
						return Status.OK_STATUS;
					}

				};
				j.setUser(true);
				j.setPriority(Job.LONG);
				j.schedule();
				
				
				
				
			}
		});
		
	}

	protected void initializeCombos() {
		annotations = TreeMultimap.create();
		List<SSpan> spans = graph.getSpans();
		List<SStructure> structures = graph.getStructures();
		List<SNode> nodes = new ArrayList<>();
		nodes.addAll(spans);
		nodes.addAll(structures);
		Job findAnnotationsJob = new Job("Compiling list of potential segmentation annotations") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SubMonitor subMonitor = SubMonitor.convert(monitor, graph.getAnnotations().size());
				subMonitor.setTaskName("Finding annotations");
				for (SNode s : nodes) {
					Iterator<SAnnotation> it = s.iterator_SAnnotation();
					while (it.hasNext()) {
						SAnnotation anno = it.next();
						if (anno.getQName() != null && anno.getValue_STEXT() != null) {
							annotations.put(anno.getQName(), anno.getValue_STEXT());
							subMonitor.worked(1);
						}
						else {
							log.warn("Found an invalid SAnnotation object: {}.", anno);
						}
					}
				}
				Set<String> qNames = annotations.keySet();
				String[] qNameItems = qNames.stream().toArray(ns -> new String[ns]);
				uiSync.asyncExec(() -> {
					comboQName.setItems(qNameItems);
					comboQName.setEnabled(true);
					comboQName.setFocus();
					comboValue.removeAll();
					comboValue.setEnabled(false);
					segmentationTableColumn.setText("Segments");
					btnLoadSegmentation.setEnabled(false);
				});
				return Status.OK_STATUS;

			}
		};
		findAnnotationsJob.setUser(true);
		findAnnotationsJob.schedule();
	}

	@Focus
	public void setFocus() {
		btnLoadGraph.setFocus();

	}

	/**
	 * This method is kept for E3 compatiblity. You can remove it if you do not
	 * mix E3 and E4 code. <br/>
	 * With E4 code you will set directly the selection in ESelectionService and
	 * you do not receive a ISelection
	 * 
	 * @param s
	 *            the selection received from JFace (E3 mode)
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) ISelection s) {
		
		if (s == null || s.isEmpty())
			return;

		if (s instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) s;
			if (iss.size() == 1)
				setSelection(iss.getFirstElement());
			else
				setSelection(iss.toArray());
		}
	}

	/**
	 * This method manages the selection of your current object. In this example
	 * we listen to a single Object (even the ISelection already captured in E3
	 * mode). <br/>
	 * You should change the parameter type of your received Object to manage
	 * your specific selection
	 * 
	 * @param o
	 *            : the current object received
	 */
	@Inject
	@Optional
	public void setSelection(@Named(IServiceConstants.ACTIVE_SELECTION) Object o) {
		
		log.trace("Setting selection to object {} of type {}.", o, o.getClass());

		if (o instanceof ISelection) // Already captured
			return;

		// Test if label exists (inject methods are called before PostConstruct)
		if (lblSelectedDocumentFile != null)
			if (o instanceof IFile) {
				String name = ((IFile) o).getName();
				if (((IFile) o).getFileExtension().equals("salt") && !("saltProject.salt").equals(name)) {
					lblSelectedDocumentFile.setText("Selected document file: " + name.substring(0, name.length() - 5));
					documentFile = (IFile) o;
					btnLoadGraph.setEnabled(true);
				}
				else {
					lblSelectedDocumentFile.setText(LBL_DEFAULT_VALUE);
					btnLoadGraph.setEnabled(false);
				}

			}
			else {
				lblSelectedDocumentFile.setText(LBL_DEFAULT_VALUE);
				btnLoadGraph.setEnabled(false);
			}
	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class Segment {
		
		public Segment(String text, SNode node) {
			this.text = text;
			this.node = node;
		}
		
		private String text;
		private SNode node;
		
		/**
		 * @return the text
		 */
		public final String getText() {
			return text;
		}
		/**
		 * @return the node
		 */
		public final SNode getNode() {
			return node;
		}
	
	}
}
