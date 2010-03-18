package Application;

import java.awt.Dimension;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")

/**
 * Classe permettant d'afficher le workspace sur la fenetre de travail
 * 
 * @author Hamidou
 * @version 1
 */
public class FenetreWorkspace extends JPanel 
{
	JTree arbre;	
	static DefaultMutableTreeNode root;
	DefaultMutableTreeNode defaultChild;
	
	
	public FenetreWorkspace()
	{
		super();
		defaultChild = new DefaultMutableTreeNode("Default diagram");
		root = new DefaultMutableTreeNode("Mon Projet");	
		root.add(defaultChild);
		DefaultMutableTreeNode Child = new DefaultMutableTreeNode("Bla");
		root.add(Child);
		arbre = new JTree(root);			
		this.add(arbre);		
	}
	
	public void setNomDiagramme(String nomDiagramme)
	{
		DefaultMutableTreeNode Child = new DefaultMutableTreeNode(nomDiagramme);
		root.add(Child);
		arbre.scrollPathToVisible(new TreePath(root.getPath()));

		System.out.println("ok");
	}
	
	public void setDimension(Dimension d)
	{
		setPreferredSize(d);
	}

}
