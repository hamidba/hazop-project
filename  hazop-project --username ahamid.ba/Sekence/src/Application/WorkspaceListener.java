package Application;

import java.awt.Component;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import Vue.Graph;

/**
 * Classe implémentant le listener du workspace
 * 
 * @author Hamidou
 *
 */
class WorkspaceListener implements TreeSelectionListener {
    
	Workspace model;
    FenetreEditeur editeur;

    public WorkspaceListener( Workspace mdl, FenetreEditeur fenetre ) 
    {
        model = mdl;
        editeur = fenetre;
    }
    
    
    public void valueChanged( TreeSelectionEvent e ) {

        File fileSysEntity = (File)e.getPath().getLastPathComponent();
        
        if ( fileSysEntity.isDirectory() ) return;        
        
        //La selection se base sur un noeud 
		try
		{			
			//Test des composants pour recuperer le JDesktopPane
			
			Component[] listComponents = editeur.getContentPane().getComponents();
			JDesktopPane desk = null;
			
			for (int i=0; i< listComponents.length; i++)
			{
				if (listComponents[i].getClass().equals(JDesktopPane.class))
				{
					desk = (JDesktopPane) listComponents[i];
				}
			}			
				
			//Recuperation des instances de graph dans un tableau
			JInternalFrame[] listFrames = desk.getAllFrames();
			boolean notExiste = true;	
			
			//On veut vérifier si le graph qu'on veut ouvert l'est déja 
			for (int i=0; i< listFrames.length; i++)
			{				
				if (listFrames[i].getTitle().equals(fileSysEntity.getName()))
				{
					notExiste = false;
				}
			}
			
			if(notExiste) //Graph non ouvert : Donc on peut l'ouvrir 
			{
				//Recuperation et convert du fichier
				XMLDecoder reader = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileSysEntity)));		
				Graph graph = (Graph) reader.readObject();
				FenetreEditionDiagramme frame = new FenetreEditionDiagramme(graph);
				editeur.addInternalFrame(frame);
				frame.setFileName(fileSysEntity.getName());
			}			
		}
		catch (FileNotFoundException e2) 
		{			
			e2.printStackTrace();
		}		
   }
}