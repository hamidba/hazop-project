package Vue;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
   A class that supplies convenience implementations for 
   a number of methods in the Edge interface
*/
abstract class AbstractEdge implements Edge
{  
   public Object clone()
   {
      try
      {
         return super.clone();
      }
      catch (CloneNotSupportedException exception)
      {
         return null;
      }
   }

   public void connect(InterfaceNoeud s, InterfaceNoeud e)
   {  
      start = s;
      end = e;
   }

   public InterfaceNoeud getStart()
   {
      return start;
   }

   public InterfaceNoeud getEnd()
   {
      return end;
   }

   public Rectangle2D getBounds(Graphics2D g2)
   {
      Line2D conn = getConnectionPoints();      
      Rectangle2D r = new Rectangle2D.Double();
      r.setFrameFromDiagonal(conn.getX1(), conn.getY1(),
         conn.getX2(), conn.getY2());
      return r;
   }

   public Line2D getConnectionPoints()
   {
      Rectangle2D startBounds = start.getBounds();
      Rectangle2D endBounds = end.getBounds();
      Point2D startCenter = new Point2D.Double(
         startBounds.getCenterX(), startBounds.getCenterY());
      Point2D endCenter = new Point2D.Double(
         endBounds.getCenterX(), endBounds.getCenterY());
      Direction toEnd = new Direction(startCenter, endCenter);
      return new Line2D.Double(
         start.getConnectionPoint(toEnd),
         end.getConnectionPoint(toEnd.turn(180)));
   }

   private InterfaceNoeud start;
   private InterfaceNoeud end;
}
