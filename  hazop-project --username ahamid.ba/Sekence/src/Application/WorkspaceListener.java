package Application;

import java.beans.XMLDecoder;
import java.io.File;
import java.io.InputStream;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import Vue.Graph;

class WorkspaceListener implements TreeSelectionListener {
    Workspace model;
    FenetreEditeur editeur;

    public WorkspaceListener( Workspace mdl, FenetreEditeur fenetre ) {
        model = mdl;
        editeur = fenetre;
    }
    public void valueChanged( TreeSelectionEvent e ) {
        File fileSysEntity = (File)e.getPath().getLastPathComponent();
        if ( fileSysEntity.isDirectory() ) return;
        
            //model.setDirectory( null );
        	
        	System.out.println(fileSysEntity.getAbsolutePath());
        	editeur.openFile();
			             
        }
}

