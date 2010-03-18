package Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Vue.AppelMethode;
import Vue.BandeVieMethode;
import Vue.Edge;
import Vue.Graph;
import Vue.Grid;
import Vue.InterfaceNoeud;
import Vue.LienNote;
import Vue.LigneDeVie;
import Vue.Note;
import Vue.RetourMethode;

/**
 * Panel pour dessiner un diagramme
 */
public class GraphPanel extends JPanel
{
	private ControleurCommandes controleur;
	private Graph graph;
	private Grid grid;
	private FenetreEditionDiagramme frame;
	private BarreOutils toolBar;

	private double zoom;
	private double gridSize;
	private boolean hideGrid;
	private boolean modified;
	private Object lastSelected;
	private Set selectedItems;
	private Point2D lastMousePoint;
	private Point2D mouseDownPoint;
	private int dragMode;

	private static final int DRAG_NONE = 0;
	private static final int DRAG_MOVE = 1;
	private static final int DRAG_RUBBERBAND = 2;
	private static final int DRAG_LASSO = 3;
	private static final int GRID = 10;
	private static final int CONNECT_THRESHOLD = 8;
	private static final Color PURPLE = new Color(0.7f, 0.4f, 0.7f);

	public GraphPanel(BarreOutils aToolBar, ControleurCommandes controleurCommandes)
	{
		grid = new Grid();
		gridSize = GRID;
		grid.setGrid((int) gridSize, (int) gridSize);
		zoom = 1;
		toolBar = aToolBar;
		setBackground(Color.WHITE);
		controleur = controleurCommandes;
		selectedItems = new HashSet();

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent event)
			{
				if(event.getButton() == MouseEvent.BUTTON3)
				{
					controleur.annulerCommande();
					return;
				}
				requestFocus();
				final Point2D mousePoint = new Point2D.Double(event.getX() / zoom, event.getY() / zoom);
				boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
				InterfaceNoeud n = graph.findNode(mousePoint);
				Edge e = graph.findEdge(mousePoint);
				
				// On a cliquŽ sur le graph donc on recupere l'option qui Žtait
				// selectionnŽe
				Object tool = toolBar.getSelectedTool();
				if (event.getClickCount() > 1
						|| (event.getModifiers() & InputEvent.BUTTON1_MASK) == 0)
				{
					if (e != null)
					{
						setSelectedItem(e);
						editSelected();
					} else if (n != null)
					{
						setSelectedItem(n);
						editSelected();
					}

				}
				// Si aucune option n'est selectionnŽe
				else if (tool == null)
				{
					if (e != null)
					{
						setSelectedItem(e);
					} else if (n != null)
					{
						if (isCtrl)
							addSelectedItem(n);
						else if (!selectedItems.contains(n))
							setSelectedItem(n);
						dragMode = DRAG_MOVE;
					} else
					{
						if (!isCtrl)
							clearSelection();
						dragMode = DRAG_LASSO;
					}
				} else if (tool instanceof InterfaceNoeud)
				{
					
					InterfaceNoeud prototype = (InterfaceNoeud) tool;
					InterfaceNoeud newNode = (InterfaceNoeud) prototype.clone();
					boolean added = false;
					CommandeAjouterElementAuGraphe cmd = new CommandeAjouterElementAuGraphe(graph);
					cmd.setParametres(newNode, mousePoint);
					added = controleur.genererCommande(cmd);
					
					if (added)
					{
						setModified(true);
						setSelectedItem(newNode);
						dragMode = DRAG_MOVE;
					} else if (n != null)
					{
						if (isCtrl)
							addSelectedItem(n);
						else if (!selectedItems.contains(n))
							setSelectedItem(n);
						dragMode = DRAG_MOVE;
					}
				} else if (tool instanceof Edge)
				{
					if (n != null)
						dragMode = DRAG_RUBBERBAND;
				}

				lastMousePoint = mousePoint;
				mouseDownPoint = mousePoint;
				repaint();
			}

			public void mouseReleased(MouseEvent event)
			{
				Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
						event.getY() / zoom);
				Object tool = toolBar.getSelectedTool();
				if (dragMode == DRAG_RUBBERBAND)
				{
					System.out.println("Drag_rubberBand");
					Edge prototype = (Edge) tool;
					Edge newEdge = (Edge) prototype.clone();
					if (mousePoint.distance(mouseDownPoint) > CONNECT_THRESHOLD
							&& graph.connect(newEdge, mouseDownPoint,
									mousePoint))
					{
						setModified(true);
						setSelectedItem(newEdge);
					}
				} else if (dragMode == DRAG_MOVE)
				{
					graph.layout();
					setModified(true);
				}
				dragMode = DRAG_NONE;

				revalidate();
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent event)
			{
				Point2D mousePoint = new Point2D.Double(event.getX() / zoom,
						event.getY() / zoom);
				boolean isCtrl = (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;

				if (dragMode == DRAG_MOVE
						&& lastSelected instanceof InterfaceNoeud)
				{
					System.out.println("Drag_move");
					InterfaceNoeud lastNode = (InterfaceNoeud) lastSelected;
					Rectangle2D bounds = lastNode.getBounds();
					double dx = mousePoint.getX() - lastMousePoint.getX();
					double dy = mousePoint.getY() - lastMousePoint.getY();

					Iterator iter = selectedItems.iterator();
					while (iter.hasNext())
					{
						Object selected = iter.next();
						if (selected instanceof InterfaceNoeud)
						{
							InterfaceNoeud n = (InterfaceNoeud) selected;
							bounds.add(n.getBounds());
						}
					}
					dx = Math.max(dx, -bounds.getX());
					dy = Math.max(dy, -bounds.getY());

					iter = selectedItems.iterator();
					while (iter.hasNext())
					{
						Object selected = iter.next();
						if (selected instanceof InterfaceNoeud)
						{
							InterfaceNoeud n = (InterfaceNoeud) selected;
							n.translate(dx, dy);
						}
					}
				} else if (dragMode == DRAG_LASSO)
				{
					System.out.println("Drag_move");
					double x1 = mouseDownPoint.getX();
					double y1 = mouseDownPoint.getY();
					double x2 = mousePoint.getX();
					double y2 = mousePoint.getY();
					Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(
							x1, x2), Math.min(y1, y2), Math.abs(x1 - x2), Math
							.abs(y1 - y2));
					Iterator iter = graph.getNodes().iterator();
					while (iter.hasNext())
					{
						InterfaceNoeud n = (InterfaceNoeud) iter.next();
						Rectangle2D bounds = n.getBounds();
						if (!isCtrl && !lasso.contains(n.getBounds()))
						{
							removeSelectedItem(n);
						} else if (lasso.contains(n.getBounds()))
						{
							addSelectedItem(n);
						}
					}
				}

				lastMousePoint = mousePoint;
				repaint();
			}
		});
	}

	public void editSelected()
	{
		System.out.println("Editselected");
		Object edited = lastSelected;
		if (lastSelected == null)
		{
			if (selectedItems.size() == 1)
				edited = selectedItems.iterator().next();
			else
				return;
		}

		FenetreProprietes sheet = new FenetreProprietes(edited, this);
		sheet.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent event)
			{
				graph.layout();
				repaint();
			}
		});
		JOptionPane.showInternalMessageDialog(this, sheet, "PropriŽtŽs",
				JOptionPane.QUESTION_MESSAGE);
		setModified(true);
	}

	public void removeSelected()
	{
		System.out.println("Removeselected");
		Iterator iter = selectedItems.iterator();
		while (iter.hasNext())
		{
			Object selected = iter.next();
			CommandeDefaisable cmd = null;
			if (selected instanceof InterfaceNoeud)
			{
				System.out.println("Supprimer noeud");
				cmd = new CommandeSupprimerNoeudAuGraph(graph);
				((CommandeSupprimerNoeudAuGraph)cmd).setParams((InterfaceNoeud) selected);
				
				
			} else if (selected instanceof Edge)
			{
				System.out.println("Supprimer lien");
				cmd = new CommandeSupprimerLienAuGraph(graph);
				((CommandeSupprimerLienAuGraph)cmd).setParams((Edge) selected);
			}
			
			controleur.genererCommande(cmd);
		}
		if (selectedItems.size() > 0)
			setModified(true);
		repaint();
	}

	public void setGraph(Graph aGraph)
	{
		graph = aGraph;
		setModified(false);
		revalidate();
		repaint();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.scale(zoom, zoom);
		Rectangle2D bounds = getBounds();
		Rectangle2D graphBounds = graph.getBounds(g2);
		if (!hideGrid)
			grid.draw(g2, new Rectangle2D.Double(0, 0, Math.max(bounds
					.getMaxX()
					/ zoom, graphBounds.getMaxX()), Math.max(bounds.getMaxY()
					/ zoom, graphBounds.getMaxY())));
		graph.draw(g2, grid);

		Iterator iter = selectedItems.iterator();
		Set toBeRemoved = new HashSet();
		while (iter.hasNext())
		{
			Object selected = iter.next();

			if (!graph.getNodes().contains(selected)
					&& !graph.getEdges().contains(selected))
			{
				toBeRemoved.add(selected);
			} else if (selected instanceof InterfaceNoeud)
			{
				Rectangle2D grabberBounds = ((InterfaceNoeud) selected)
						.getBounds();
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds
						.getMinY());
				drawGrabber(g2, grabberBounds.getMinX(), grabberBounds
						.getMaxY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds
						.getMinY());
				drawGrabber(g2, grabberBounds.getMaxX(), grabberBounds
						.getMaxY());
			} else if (selected instanceof Edge)
			{
				Line2D line = ((Edge) selected).getConnectionPoints();
				drawGrabber(g2, line.getX1(), line.getY1());
				drawGrabber(g2, line.getX2(), line.getY2());
			}
		}

		iter = toBeRemoved.iterator();
		while (iter.hasNext())
			removeSelectedItem(iter.next());

		if (dragMode == DRAG_RUBBERBAND)
		{
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			g2.draw(new Line2D.Double(mouseDownPoint, lastMousePoint));
			g2.setColor(oldColor);
		} else if (dragMode == DRAG_LASSO)
		{
			Color oldColor = g2.getColor();
			g2.setColor(PURPLE);
			double x1 = mouseDownPoint.getX();
			double y1 = mouseDownPoint.getY();
			double x2 = lastMousePoint.getX();
			double y2 = lastMousePoint.getY();
			Rectangle2D.Double lasso = new Rectangle2D.Double(Math.min(x1, x2),
					Math.min(y1, y2), Math.abs(x1 - x2), Math.abs(y1 - y2));
			g2.draw(lasso);
			g2.setColor(oldColor);
		}
	}

	public static void drawGrabber(Graphics2D g2, double x, double y)
	{
		final int SIZE = 5;
		Color oldColor = g2.getColor();
		g2.setColor(PURPLE);
		g2.fill(new Rectangle2D.Double(x - SIZE / 2, y - SIZE / 2, SIZE, SIZE));
		g2.setColor(oldColor);
	}

	public Dimension getPreferredSize()
	{
		Rectangle2D bounds = graph.getBounds((Graphics2D) getGraphics());
		return new Dimension((int) (zoom * bounds.getMaxX()),
				(int) (zoom * bounds.getMaxY()));
	}

	public void changeZoom(int steps)
	{
		final double FACTOR = Math.sqrt(2);
		for (int i = 1; i <= steps; i++)
			zoom *= FACTOR;
		for (int i = 1; i <= -steps; i++)
			zoom /= FACTOR;
		revalidate();
		repaint();
	}

	public void changeGridSize(int steps)
	{
		final double FACTOR = Math.sqrt(2);
		for (int i = 1; i <= steps; i++)
			gridSize *= FACTOR;
		for (int i = 1; i <= -steps; i++)
			gridSize /= FACTOR;
		grid.setGrid((int) gridSize, (int) gridSize);
		graph.layout();
		repaint();
	}

	public void selectNext(int n)
	{
		ArrayList selectables = new ArrayList();
		selectables.addAll(graph.getNodes());
		selectables.addAll(graph.getEdges());
		if (selectables.size() == 0)
			return;
		java.util.Collections.sort(selectables, new java.util.Comparator()
		{
			public int compare(Object obj1, Object obj2)
			{
				double x1;
				double y1;
				if (obj1 instanceof InterfaceNoeud)
				{
					Rectangle2D bounds = ((InterfaceNoeud) obj1).getBounds();
					x1 = bounds.getX();
					y1 = bounds.getY();
				} else
				{
					Point2D start = ((Edge) obj1).getConnectionPoints().getP1();
					x1 = start.getX();
					y1 = start.getY();
				}
				double x2;
				double y2;
				if (obj2 instanceof InterfaceNoeud)
				{
					Rectangle2D bounds = ((InterfaceNoeud) obj2).getBounds();
					x2 = bounds.getX();
					y2 = bounds.getY();
				} else
				{
					Point2D start = ((Edge) obj2).getConnectionPoints().getP1();
					x2 = start.getX();
					y2 = start.getY();
				}
				if (y1 < y2)
					return -1;
				if (y1 > y2)
					return 1;
				if (x1 < x2)
					return -1;
				if (x1 > x2)
					return 1;
				return 0;
			}
		});
		int index;
		if (lastSelected == null)
			index = 0;
		else
			index = selectables.indexOf(lastSelected) + n;
		while (index < 0)
			index += selectables.size();
		index %= selectables.size();
		setSelectedItem(selectables.get(index));
		repaint();
	}

	public boolean isModified()
	{
		return modified;
	}

	public void setModified(boolean newValue)
	{
		modified = newValue;

		if (frame == null)
		{
			Component parent = this;
			do
			{
				parent = parent.getParent();
			} while (parent != null
					&& !(parent instanceof FenetreEditionDiagramme));
			if (parent != null)
				frame = (FenetreEditionDiagramme) parent;
		}
		if (frame != null)
		{
			String title = frame.getFileName();
			if (title != null)
			{
				if (modified)
				{
					if (!frame.getTitle().endsWith("*"))
						frame.setTitle(title + "*");
				} else
					frame.setTitle(title);
			}
		}
	}

	private void addSelectedItem(Object obj)
	{
		lastSelected = obj;
		selectedItems.add(obj);
	}

	private void removeSelectedItem(Object obj)
	{
		System.out.println("RemoveselectedItem");
		if (obj == lastSelected)
			lastSelected = null;
		selectedItems.remove(obj);
	}

	private void setSelectedItem(Object obj)
	{
		selectedItems.clear();
		lastSelected = obj;
		if (obj != null)
			selectedItems.add(obj);
	}

	private void clearSelection()
	{
		selectedItems.clear();
		lastSelected = null;
	}

	public void setHideGrid(boolean newValue)
	{
		hideGrid = newValue;
		repaint();
	}

	public boolean getHideGrid()
	{
		return hideGrid;
	}
}