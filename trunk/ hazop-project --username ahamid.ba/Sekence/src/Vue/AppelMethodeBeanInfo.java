package Vue;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
   The bean info for the CallNode type.
*/
public class AppelMethodeBeanInfo extends SimpleBeanInfo
{
   public PropertyDescriptor[] getPropertyDescriptors()
   {
      try
      {
         return new PropertyDescriptor[]
            {
               new PropertyDescriptor("openBottom", AppelMethode.class),
               new PropertyDescriptor("implicitParameter", AppelMethode.class)
            };
      }
      catch (IntrospectionException exception)
      {
         return null;
      }
   }
}