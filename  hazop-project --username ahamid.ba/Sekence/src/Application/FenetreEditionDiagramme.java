package Application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import Vue.Graph;


public class FenetreEditionDiagramme extends JInternalFrame
{
   public FenetreEditionDiagramme(Graph aGraph)
   {
	  ControleurCommandes controleur = new ControleurCommandes();
      graph = aGraph;
      toolBar = new BarreOutils(graph);
      panel = new GraphPanel(toolBar, controleur);
      Container contentPane = getContentPane();
      contentPane.add(toolBar, BorderLayout.NORTH);
      contentPane.add(new JScrollPane(panel), BorderLayout.CENTER);
      
      JTable table;
	  String[] nomColonnes;
	  TableModel tableH;
	  JScrollPane tableJS;
	  
	  //initialisation des parametres
	  nomColonnes = new String [] {"Message","Mots guide","Dviation","Effet", "Cause", "GravitŽ", "Solution"};
	  tableH = new TableModel(1,nomColonnes);
	  table = new JTable(tableH);	
	
	  //Ajout d'une comboBox pour la selection des mots clés d'un message
	  JComboBox motsGuide = new JComboBox();
	  motsGuide.addItem("NO OR NOT");
	  motsGuide.addItem("test");	    
	  table.setDefaultEditor(String.class, new DefaultCellEditor(motsGuide)); 

	  tableJS = new JScrollPane(table);
	  Dimension d = new Dimension(0,150);
	  tableJS.setPreferredSize(d);
	  contentPane.add(tableJS, BorderLayout.SOUTH);	
      
      
      // Ajout listener sur fermeture
      addVetoableChangeListener(new
         VetoableChangeListener()
         {
            public void vetoableChange(PropertyChangeEvent event)
               throws PropertyVetoException
            {  
               String name = event.getPropertyName();
               Object value = event.getNewValue();

               if (name.equals("closed") 
                  && value.equals(Boolean.TRUE) && panel.isModified())
               {  
                  int result
                     = JOptionPane.showInternalConfirmDialog(
                        FenetreEditionDiagramme.this, 
                        "Voulez-vous quitter sans sauvegarder?",
                        null,
                        JOptionPane.YES_NO_OPTION);

                  if (result != JOptionPane.YES_OPTION)
                     throw new PropertyVetoException(
                        "User canceled close", event);
               }
            }           
         });

      panel.setGraph(graph);
   }

   public Graph getGraph()
   {
      return graph;
   }

   public GraphPanel getGraphPanel()
   {
      return panel;
   }

   public String getFileName()
   {
      return fileName;
   }

   public void setFileName(String newValue)
   {
      fileName = newValue;
      setTitle(newValue);
   }

   private Graph graph;
   private GraphPanel panel;
   private BarreOutils toolBar;
   private String fileName;
}
