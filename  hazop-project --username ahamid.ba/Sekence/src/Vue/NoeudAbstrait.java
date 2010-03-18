package Vue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;


/**
   A class that supplies convenience implementations for 
   a number of methods in the Node interface
*/
public abstract class NoeudAbstrait implements InterfaceNoeud
{
   /**
      Constructs a node with no parents or children.
   */
   public NoeudAbstrait()
   {
      children = new ArrayList();
      parent = null;
   }

   public Object clone()
   {
      try
      {
         NoeudAbstrait cloned = (NoeudAbstrait)super.clone();
         cloned.children = new ArrayList(children.size());
         for (int i = 0; i < children.size(); i++)
         {
            InterfaceNoeud n = (InterfaceNoeud)children.get(i);
            cloned.children.set(i, n.clone());
            n.setParent(cloned);
         }
         return cloned;
      }
      catch (CloneNotSupportedException exception)
      {
         return null;
      }
   }

   public void translate(double dx, double dy)
   {
      for (int i = 0; i < children.size(); i++)
      {
         InterfaceNoeud n = (InterfaceNoeud)children.get(i);
         n.translate(dx, dy);
      }
   }

   public boolean addEdge(Edge e, Point2D p1, Point2D p2)
   {
      return e.getEnd() != null;
   }

   public void removeEdge(Graph g, Edge e)
   {
   }

   public void removeNode(Graph g, InterfaceNoeud e)
   {
      if (e == parent) parent = null; 
      if (e.getParent() == this) children.remove(e);
   }

   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
   }

   public boolean addNode(InterfaceNoeud n, Point2D p)
   {
      return false;
   }

   public InterfaceNoeud getParent() { return parent; }

   public void setParent(InterfaceNoeud node) { parent = node; }

   public List getChildren() { return children; }

   public void addChild(int index, InterfaceNoeud node) 
   {
      InterfaceNoeud oldParent = node.getParent();
      if (oldParent != null)
         oldParent.removeChild(node);
      children.add(index, node);
      node.setParent(this);
   }

   public void addChild(InterfaceNoeud node)
   {
      addChild(children.size(), node);
   }

   public void removeChild(InterfaceNoeud node)
   {
      if (node.getParent() != this) return;
      children.remove(node);
      node.setParent(null);
   }

   public void draw(Graphics2D g2)
   {
      Shape shape = getShape();
      if (shape == null) return;
      /*
      Area shadow = new Area(shape);
      shadow.transform(AffineTransform.getTranslateInstance(SHADOW_GAP, SHADOW_GAP));
      shadow.subtract(new Area(shape));
      */
      Color oldColor = g2.getColor();
      g2.translate(SHADOW_GAP, SHADOW_GAP);      
      g2.setColor(SHADOW_COLOR);
      g2.fill(shape);
      g2.translate(-SHADOW_GAP, -SHADOW_GAP);
      g2.setColor(g2.getBackground());
      g2.fill(shape);      
      g2.setColor(oldColor);
   }
   
   private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;
   public static final int SHADOW_GAP = 4;
   
   /**
       @return the shape to be used for computing the drop shadow
    */
   public Shape getShape() { return null; }   
   
   /**
      Adds a persistence delegate to a given encoder that
      encodes the child nodes of this node.
      @param encoder the encoder to which to add the delegate
   */
   public static void setPersistenceDelegate(Encoder encoder)
   {
      encoder.setPersistenceDelegate(NoeudAbstrait.class, new
         DefaultPersistenceDelegate()
         {
            protected void initialize(Class type, 
               Object oldInstance, Object newInstance, 
               Encoder out) 
            {
               super.initialize(type, oldInstance, 
                  newInstance, out);
               InterfaceNoeud n = (InterfaceNoeud)oldInstance;
               List children = n.getChildren();
               for (int i = 0; i < children.size(); i++)
               {
                  InterfaceNoeud c = (InterfaceNoeud)children.get(i);
                  out.writeStatement(
                     new Statement(oldInstance,
                        "addChild", new Object[]{ c }) );            
               }
            }
         });
   }
   private ArrayList children;
   private InterfaceNoeud parent;
}