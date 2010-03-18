package Vue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import Outils.MultiLineString;

/**
   A note node in a UML diagram.
*/
public class Note extends RectangularNode
{
   /**
      Construct a note node with a default size and color
   */
   public Note()
   {
      setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
      text = new MultiLineString();
      text.setJustification(MultiLineString.LEFT);
      color = DEFAULT_COLOR;
   }

   public boolean addEdge(Edge e, Point2D p1, Point2D p2)
   {
      PointNode end = new PointNode();
      end.translate(p2.getX(), p2.getY());
      e.connect(this, end);
      return super.addEdge(e, p1, p2);
   }

   public void removeEdge(Graph g, Edge e)
   {
      if (e.getStart() == this) g.removeNode(e.getEnd());
   }

   public void layout(Graph g, Graphics2D g2, Grid grid)
   {
      Rectangle2D b = text.getBounds(g2); // getMultiLineBounds(name, g2);
      Rectangle2D bounds = getBounds();
      b = new Rectangle2D.Double(bounds.getX(),
         bounds.getY(), 
         Math.max(b.getWidth(), DEFAULT_WIDTH),
         Math.max(b.getHeight(), DEFAULT_HEIGHT));
      grid.snap(b);
      setBounds(b);
   }

   /**
      Gets the value of the text property.
      @return the text inside the note
   */
   public MultiLineString getText()
   {
      return text;
   }

   /**
      Sets the value of the text property.
      @param newValue the text inside the note
   */
   public void setText(MultiLineString newValue)
   {
      text = newValue;
   }

   /**
      Gets the value of the color property.
      @return the background color of the note
   */
   public Color getColor()
   {
      return color;
   }

   /**
      Sets the value of the color property.
      @param newValue the background color of the note
   */
   public void setColor(Color newValue)
   {
      color = newValue;
   }
   
   public void draw(Graphics2D g2)
   {
      super.draw(g2);
      Color oldColor = g2.getColor();
      g2.setColor(color);

      Shape path = getShape();
      g2.fill(path);
      g2.setColor(oldColor);
      g2.draw(path);

      Rectangle2D bounds = getBounds();
      GeneralPath fold = new GeneralPath();
      fold.moveTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
      fold.lineTo((float)bounds.getMaxX() - FOLD_X, (float)bounds.getY() + FOLD_X);
      fold.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
      fold.closePath();
      oldColor = g2.getColor();
      g2.setColor(g2.getBackground());
      g2.fill(fold);
      g2.setColor(oldColor);      
      g2.draw(fold);      
      
      text.draw(g2, getBounds());
   }
   
   public Shape getShape()
   {
      Rectangle2D bounds = getBounds();
      GeneralPath path = new GeneralPath();
      path.moveTo((float)bounds.getX(), (float)bounds.getY());
      path.lineTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
      path.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
      path.lineTo((float)bounds.getMaxX(), (float)bounds.getMaxY());
      path.lineTo((float)bounds.getX(), (float)bounds.getMaxY());
      path.closePath();
      return path;
   }

   public Object clone()
   {
      Note cloned = (Note)super.clone();
      cloned.text = (MultiLineString)text.clone();
      return cloned;
   }

   private MultiLineString text;
   private Color color;

   private static int DEFAULT_WIDTH = 60;
   private static int DEFAULT_HEIGHT = 40;
   private static Color DEFAULT_COLOR = new Color(0.9f, 0.9f, 0.6f); // pale yellow
   private static int FOLD_X = 8;
   private static int FOLD_Y = 8;
}