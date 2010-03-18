package Vue;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
   A method call node in a scenario diagram.
*/
public class AppelMethode extends RectangularNode
{
   /**
      Construct a call node with a default size
   */
   public AppelMethode()
   {
      setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
   }

   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Color oldColor = g2.getColor();
      g2.setColor(Color.WHITE);
      g2.fill(getBounds());
      g2.setColor(oldColor);
      if (openBottom)
      {
         Rectangle2D b = getBounds();
         double x1 = b.getX();
         double x2 = x1 + b.getWidth();
         double y1 = b.getY();
         double y3 = y1 + b.getHeight();
         double y2 = y3 - CALL_YGAP;
         g2.draw(new Line2D.Double(x1, y1, x2, y1));
         g2.draw(new Line2D.Double(x1, y1, x1, y2));
         g2.draw(new Line2D.Double(x2, y1, x2, y2));
         Stroke oldStroke = g2.getStroke();
         g2.setStroke(new BasicStroke(1.0f, 
                         BasicStroke.CAP_ROUND, 
                         BasicStroke.JOIN_ROUND, 
                         0.0f, 
                         new float[] { 5.0f, 5.0f }, 0.0f));
         g2.draw(new Line2D.Double(x1, y2, x1, y3));
         g2.draw(new Line2D.Double(x2, y2, x2, y3));
         g2.setStroke(oldStroke);
      }
      else
         g2.draw(getBounds());
   }

   /**
      Gets the implicit parameter of this call.
      @return the implicit parameter node
   */
   public LigneDeVie getImplicitParameter()
   {
      return implicitParameter;
   }

   /**
      Sets the implicit parameter of this call.
      @param newValue the implicit parameter node
   */
   public void setImplicitParameter(LigneDeVie newValue)
   {
      implicitParameter = newValue;
   }

   public Point2D getConnectionPoint(Direction d)
   {
      if (d.getX() > 0)
         return new Point2D.Double(getBounds().getMaxX(),
            getBounds().getMinY());
      else
         return new Point2D.Double(getBounds().getX(),
            getBounds().getMinY());
   }

   public boolean addEdge(Edge e, Point2D p1, Point2D p2)
   {
      InterfaceNoeud end = e.getEnd();
      if (end == null) return false;

      if (e instanceof RetourMethode) // check that there is a matching call 
         return end == getParent();
         
      if (!(e instanceof BandeVieMethode)) return false;
      
      InterfaceNoeud n = null;
      if (end instanceof AppelMethode) 
      {
         // check for cycles
         InterfaceNoeud parent = this; 
         while (parent != null && end != parent) 
            parent = parent.getParent();
         
         if (end.getParent() == null && end != parent)
         {
            n = end;
         }
         else
         {
            AppelMethode c = new AppelMethode();
            c.implicitParameter = ((AppelMethode)end).implicitParameter;
            e.connect(this, c);
            n = c;
         }
      }
      else if (end instanceof LigneDeVie)
      {
         if (((LigneDeVie)end).getTopRectangle().contains(p2))
         {
            n = end;
            ((BandeVieMethode)e).setMiddleLabel("\u00ABcreate\u00BB");
         }
         else
         {
            AppelMethode c = new AppelMethode();
            c.implicitParameter = (LigneDeVie)end;
            e.connect(this, c);
            n = c;
         }
      }
      else return false;

      int i = 0;
      List calls = getChildren();
      while (i < calls.size() && ((InterfaceNoeud)calls.get(i)).getBounds().getY() <= p1.getY()) 
         i++;
      addChild(i, n);
      return true;
   }

   public void removeEdge(Graph g, Edge e)
   {
      if (e.getStart() == this)
         removeChild(e.getEnd());
   }

   public void removeNode(Graph g, InterfaceNoeud n)
   {
      if (n == getParent() || n == implicitParameter)
         g.removeNode(this);
   }
   
   private static Edge findEdge(Graph g, InterfaceNoeud start, InterfaceNoeud end)
   {
      Collection edges = g.getEdges();
      Iterator iter = edges.iterator(); 
      while (iter.hasNext())
      {
         Edge e = (Edge) iter.next();
         if (e.getStart() == start && e.getEnd() == end) return e;
      }
      return null;
   }

   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      
      if (implicitParameter == null) return;
      double xmid = implicitParameter.getBounds().getCenterX();

      for (AppelMethode c = (AppelMethode)getParent(); 
           c != null; c = (AppelMethode)c.getParent())
         if (c.implicitParameter == implicitParameter)
            xmid += getBounds().getWidth() / 2;

      translate(xmid - getBounds().getCenterX(), 0);
      double ytop = getBounds().getY() + CALL_YGAP;

      List calls = getChildren();
      for (int i = 0; i < calls.size(); i++)
      {
         InterfaceNoeud n = (InterfaceNoeud) calls.get(i);
         if (n instanceof LigneDeVie) // <<create>>
         {
            n.translate(0, ytop - ((LigneDeVie) n).getTopRectangle().getCenterY());
            ytop += ((LigneDeVie)n).getTopRectangle().getHeight() / 2 + CALL_YGAP;
         }
         else if (n instanceof AppelMethode)
         {  
            Edge callEdge = findEdge(g, this, n);
            // compute height of call edge
            if (callEdge != null)
            {
               Rectangle2D edgeBounds = callEdge.getBounds(g2);
               ytop += edgeBounds.getHeight() - CALL_YGAP;
            }
            
            n.translate(0, ytop - n.getBounds().getY());
            n.layout(g, g2, grid);
            if (((AppelMethode) n).signaled)
               ytop += CALL_YGAP;
            else
               ytop += n.getBounds().getHeight() + CALL_YGAP;
         }
      }
      if (openBottom) ytop += 2 * CALL_YGAP;
      Rectangle2D b = getBounds();
      
      double minHeight = DEFAULT_HEIGHT;
      Edge returnEdge = findEdge(g, this, getParent());
      if (returnEdge != null)
      {
         Rectangle2D edgeBounds = returnEdge.getBounds(g2);
         minHeight = Math.max(minHeight, edgeBounds.getHeight());         
      }
      setBounds(new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth(), 
            Math.max(minHeight, ytop - b.getY())));
   }

   public boolean addNode(InterfaceNoeud n, Point2D p)
   {
      return n instanceof PointNode;
   }

   /**
      Sets the signaled property.
      @param newValue true if this node is the target of a signal edge
   */      
   public void setSignaled(boolean newValue) { signaled = newValue; }

   /**
      Gets the openBottom property.
      @return true if this node is the target of a signal edge
   */
   public boolean isOpenBottom() { return openBottom; }

   /**
      Sets the openBottom property.
      @param newValue true if this node is the target of a signal edge
   */      
   public void setOpenBottom(boolean newValue) { openBottom = newValue; }


   private LigneDeVie implicitParameter;
   private boolean signaled;
   private boolean openBottom;
   
   private static int DEFAULT_WIDTH = 16;
   private static int DEFAULT_HEIGHT = 30;
   public static int CALL_YGAP = 20;
}