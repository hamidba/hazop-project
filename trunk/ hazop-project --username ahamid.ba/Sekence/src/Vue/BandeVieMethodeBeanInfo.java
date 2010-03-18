package Vue;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
   The bean info for the CallEdge type.
*/
public class BandeVieMethodeBeanInfo extends SimpleBeanInfo
{
   public PropertyDescriptor[] getPropertyDescriptors()
   {
      try
      {
         return new PropertyDescriptor[]
            {
               new PropertyDescriptor("middleLabel", BandeVieMethode.class),
               new PropertyDescriptor("signal", BandeVieMethode.class)
            };
      }
      catch (IntrospectionException exception)
      {
         return null;
      }
   }
}