package Vue;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
   An inivisible node that is used in the toolbar to draw an
   edge, and in notes to serve as an end point of the node
   connector.
*/
public class PointNode extends NoeudAbstrait
{
   /**
      Constructs a point node with coordinates (0, 0)
   */
   public PointNode()
   {
      point = new Point2D.Double();
   }

   public void draw(Graphics2D g2)
   {
   }

   public void translate(double dx, double dy)
   {
      point.setLocation(point.getX() + dx,
         point.getY() + dy);
   }

   public boolean contains(Point2D p)
   {
      final double THRESHOLD = 5;
      return point.distance(p) < THRESHOLD;
   }

   public Rectangle2D getBounds()
   {
      return new Rectangle2D.Double(point.getX(), 
         point.getY(), 0, 0);
   }

   public Point2D getConnectionPoint(Direction d)
   {
      return point;
   }

   private Point2D point;
}