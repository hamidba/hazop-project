package Vue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;


/**
   An edge that joins two call nodes.
*/
public class BandeVieMethode extends SegmentedLineEdge
{
   public BandeVieMethode()
   {
      setSignal(false);
   }

   /**
      Gets the signal property.
      @return true if this is a signal edge
   */
   public boolean isSignal() { return signal; }

   /**
      Sets the signal property.
      @param newValue true if this is a signal edge
   */      
   public void setSignal(boolean newValue) 
   { 
      signal = newValue; 
      if (signal)
         setEndArrowHead(ArrowHead.HALF_V);
      else
         setEndArrowHead(ArrowHead.V);
   }

   public ArrayList getPoints()
   {
      ArrayList a = new ArrayList();
      InterfaceNoeud n = getEnd();
      Rectangle2D start = getStart().getBounds();
      Rectangle2D end = n.getBounds();
      
      if (n instanceof AppelMethode &&
         ((AppelMethode)n).getImplicitParameter() ==
         ((AppelMethode)getStart()).getImplicitParameter())
      {
         Point2D p = new Point2D.Double(start.getMaxX(), end.getY() - AppelMethode.CALL_YGAP / 2);
         Point2D q = new Point2D.Double(end.getMaxX(), end.getY());
         Point2D s = new Point2D.Double(q.getX() + end.getWidth(), q.getY());
         Point2D r = new Point2D.Double(s.getX(), p.getY());
         a.add(p);
         a.add(r);
         a.add(s);
         a.add(q);
      }
      else if (n instanceof PointNode) // show nicely in tool bar
      {
         a.add(new Point2D.Double(start.getMaxX(), start.getY()));
         a.add(new Point2D.Double(end.getX(), start.getY()));
      }
      else     
      {
         Direction d = new Direction(start.getX() - end.getX(), 0);
         Point2D endPoint = getEnd().getConnectionPoint(d);
         
         if (start.getCenterX() < endPoint.getX())
            a.add(new Point2D.Double(start.getMaxX(),
                     endPoint.getY()));
         else
            a.add(new Point2D.Double(start.getX(),
                     endPoint.getY()));
         a.add(endPoint);
      }
      return a;
   }

   private boolean signal;
}