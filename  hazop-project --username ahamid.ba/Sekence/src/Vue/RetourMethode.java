package Vue;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
   An edge that joins two call nodes.
*/
public class RetourMethode extends SegmentedLineEdge
{
   public RetourMethode()
   {
      setEndArrowHead(ArrowHead.V);
      setLineStyle(LineStyle.DOTTED);
   }

   public ArrayList getPoints()
   {
      ArrayList a = new ArrayList();
      InterfaceNoeud n = getEnd();
      Rectangle2D start = getStart().getBounds();
      Rectangle2D end = getEnd().getBounds();
      if (n instanceof PointNode) // show nicely in tool bar
      {
         a.add(new Point2D.Double(end.getX(), end.getY()));
         a.add(new Point2D.Double(start.getMaxX(), end.getY()));
      }      
      else if (start.getCenterX() < end.getCenterX())
      {
         a.add(new Point2D.Double(start.getMaxX(), start.getMaxY()));
         a.add(new Point2D.Double(end.getX(), start.getMaxY()));
      }
      else
      {
         a.add(new Point2D.Double(start.getX(), start.getMaxY()));
         a.add(new Point2D.Double(end.getMaxX(), start.getMaxY()));
      }
      return a;
   }
}