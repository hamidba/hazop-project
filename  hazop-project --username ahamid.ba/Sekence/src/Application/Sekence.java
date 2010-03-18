
package Application;

public class Sekence
{
   public static void main(String[] args)
   {
      
      FenetreEditeur frame = makeFrame();
      frame.setVisible(true);
      frame.readArgs(args);
   }
   

   public static FenetreEditeur makeFrame()
   {
      FenetreEditeur frame = new FenetreEditeur(Sekence.class);
      
      return frame;
   }
   
   private static final String JAVA_VERSION = "1.4";
}