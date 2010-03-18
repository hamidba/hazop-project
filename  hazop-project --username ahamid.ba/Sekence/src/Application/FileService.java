package Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ResourceBundle;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import Outils.FiltreExtension;


public abstract class FileService
{

   public static synchronized FileService getInstance(File initialDirectory)
   {
      if (service != null) return service;
      try
      {
         service = new JFileChooserService(initialDirectory);
         return service;
      }
      catch (SecurityException exception)
      {}
      try
      {
         service = (FileService) Class.forName("outils.JNLPFileService").newInstance();
         return service;
      }
      catch (Throwable exception)
      {}
      return null;
   }

   public abstract Open open(String defaultDirectory, String defaultFile, FiltreExtension extensions) throws IOException;
   public abstract Save save(String defaultDirectory, String defaultFile, FiltreExtension extensions, String removeExtension, String addExtension) throws IOException;
   
   private static FileService service;

   public interface Open
   {
      InputStream getInputStream() throws IOException ;

      String getName() throws IOException ;
   }

   public interface Save
   {
      OutputStream getOutputStream() throws IOException ;
      String getName() throws IOException ;
   }

   private static class JFileChooserService extends FileService
   {
      public JFileChooserService(File initialDirectory)
      {
         fileChooser = new JFileChooser();
         fileChooser.setCurrentDirectory(initialDirectory);
      }

      public FileService.Open open(String defaultDirectory, String defaultFile, 
         FiltreExtension filter) throws FileNotFoundException
      {
         fileChooser.resetChoosableFileFilters();
         fileChooser.setFileFilter(filter);
         if (defaultDirectory != null)
            fileChooser.setCurrentDirectory(new File(defaultDirectory));
         if (defaultFile == null)             
            fileChooser.setSelectedFile(null);
         else
            fileChooser.setSelectedFile(new File(defaultFile));         
         int response = fileChooser.showOpenDialog(null);         
         if (response == JFileChooser.APPROVE_OPTION)
            return new Open(fileChooser.getSelectedFile());
         else
            return new Open(null);
      }

      public FileService.Save save(String defaultDirectory, String defaultFile, 
         FiltreExtension filter, String removeExtension, String addExtension) throws FileNotFoundException
      {
         fileChooser.resetChoosableFileFilters();
         fileChooser.setFileFilter(filter);
         if (defaultDirectory == null)
            fileChooser.setCurrentDirectory(new File("."));
         else
            fileChooser.setCurrentDirectory(new File(defaultDirectory));
         if (defaultFile != null)
         {
            File f = new File(editExtension(defaultFile, removeExtension, addExtension));                  
            fileChooser.setSelectedFile(f);
         }
         else 
            fileChooser.setSelectedFile(new File(""));
         int response = fileChooser.showSaveDialog(null);         
         if (response == JFileChooser.APPROVE_OPTION)
         {
            File f = fileChooser.getSelectedFile();
            if (addExtension != null && f.getName().indexOf(".") < 0) // no extension supplied
               f = new File(f.getPath() + addExtension);
            if (!f.exists()) return new Save(f);
            
            int result = JOptionPane.showConfirmDialog(
                  null,
                  "Voulez vous vraiment remplacer le fichier?", 
                  null,
                  JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) 
               return new Save(f);                       
         }
         
         return new Save(null);
      }

      public class Open implements FileService.Open
      {
         public Open(File f) throws FileNotFoundException
         {
            if (f != null)
            {
               name = f.getPath();
               in = new FileInputStream(f);
            }
         }

         public String getName() { return name; }
         public InputStream getInputStream() { return in; }

         private String name;
         private InputStream in;
      }

      public class Save implements FileService.Save
      {
         public Save(File f) throws FileNotFoundException
         {
            if (f != null)
            {
               name = f.getPath();
               out = new FileOutputStream(f);
            }
         }

         public String getName() { return name; }
         public OutputStream getOutputStream() { return out; }

         private String name;
         private OutputStream out;
      }
    
      private JFileChooser fileChooser;
   }

   public static String editExtension(String original,
   		String toBeRemoved, String desired)
   {
   	if (original == null) return null;
   	int n = desired.indexOf('|');
   	if (n >= 0) desired = desired.substring(0, n);
      String path = original;
   	if (!path.toLowerCase().endsWith(desired.toLowerCase()))
      {   		
   		if (toBeRemoved != null && path.toLowerCase().endsWith(
   				toBeRemoved.toLowerCase()))
   			path = path.substring(0, path.length() - toBeRemoved.length());
         path = path + desired;
      }
   	return path;      
   }
}
