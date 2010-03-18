package Vue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class DiagrammeSequence extends Graph
{
	public boolean add(InterfaceNoeud n, Point2D p)
	{
		if (n instanceof AppelMethode) // must be inside an object
		{
			Collection nodes = getNodes();
			boolean inside = false;
			Iterator iter = nodes.iterator();
			while (!inside && iter.hasNext())
			{
				InterfaceNoeud n2 = (InterfaceNoeud) iter.next();
				if (n2 instanceof LigneDeVie && n2.contains(p))
				{
					inside = true;
					((AppelMethode) n).setImplicitParameter((LigneDeVie) (n2));
				}
			}
			if (!inside)
				return false;
		}

		if (!super.add(n, p))
			return false;

		return true;
	}

	public void removeEdge(Edge e)
	{
		super.removeEdge(e);
		if (e instanceof BandeVieMethode
				&& e.getEnd().getChildren().size() == 0)
			removeNode(e.getEnd());
	}

	public void layout(Graphics2D g2, Grid grid)
	{
		super.layout(g2, grid);

		ArrayList topLevelCalls = new ArrayList();
		ArrayList objects = new ArrayList();
		Collection nodes = getNodes();
		Iterator iter = nodes.iterator();
		while (iter.hasNext())
		{
			InterfaceNoeud n = (InterfaceNoeud) iter.next();

			if (n instanceof AppelMethode && n.getParent() == null)
				topLevelCalls.add(n);
			else if (n instanceof LigneDeVie)
				objects.add(n);
		}

		Collection edges = getEdges();
		iter = edges.iterator();
		while (iter.hasNext())
		{
			Edge e = (Edge) iter.next();
			if (e instanceof BandeVieMethode)
			{
				InterfaceNoeud end = e.getEnd();
				if (end instanceof AppelMethode)
					((AppelMethode) end).setSignaled(((BandeVieMethode) e)
							.isSignal());
			}
		}

		double left = 0;

		// Trouve la hauteur maximale des objets
		double top = 0;
		for (int i = 0; i < objects.size(); i++)
		{
			LigneDeVie n = (LigneDeVie) objects.get(i);
			n.translate(0, -n.getBounds().getY());
			top = Math.max(top, n.getTopRectangle().getHeight());
		}

		for (int i = 0; i < topLevelCalls.size(); i++)
		{
			AppelMethode call = (AppelMethode) topLevelCalls.get(i);
			call.layout(this, g2, grid);
		}

		iter = nodes.iterator();
		while (iter.hasNext())
		{
			InterfaceNoeud n = (InterfaceNoeud) iter.next();
			if (n instanceof AppelMethode)
				top = Math.max(top, n.getBounds().getY()
						+ n.getBounds().getHeight());
		}

		top += AppelMethode.CALL_YGAP;

		for (int i = 0; i < objects.size(); i++)
		{
			LigneDeVie n = (LigneDeVie) objects.get(i);
			Rectangle2D b = n.getBounds();
			n.setBounds(new Rectangle2D.Double(b.getX(), b.getY(),
					b.getWidth(), top - b.getY()));
		}
	}

	public void draw(Graphics2D g2, Grid g)
	{
		layout(g2, g);

		Collection nodes = getNodes();
		Iterator iter = nodes.iterator();
		while (iter.hasNext())
		{
			InterfaceNoeud n = (InterfaceNoeud) iter.next();
			if (!(n instanceof AppelMethode))
				n.draw(g2);
		}

		iter = nodes.iterator();
		while (iter.hasNext())
		{
			InterfaceNoeud n = (InterfaceNoeud) iter.next();
			if (n instanceof AppelMethode)
				n.draw(g2);
		}

		Collection edges = getEdges();
		iter = edges.iterator();
		while (iter.hasNext())
		{
			Edge e = (Edge) iter.next();
			e.draw(g2);
		}
	}

	public InterfaceNoeud[] getNodePrototypes()
	{
		return NODE_PROTOTYPES;
	}

	public Edge[] getEdgePrototypes()
	{
		return EDGE_PROTOTYPES;
	}

	private static final InterfaceNoeud[] NODE_PROTOTYPES = new InterfaceNoeud[3];

	private static final Edge[] EDGE_PROTOTYPES = new Edge[3];

	static
	{
		NODE_PROTOTYPES[0] = new LigneDeVie();
		NODE_PROTOTYPES[1] = new AppelMethode();
		NODE_PROTOTYPES[2] = new Note();
		EDGE_PROTOTYPES[0] = new BandeVieMethode();
		EDGE_PROTOTYPES[1] = new RetourMethode();
		EDGE_PROTOTYPES[2] = new LienNote();
	}
}