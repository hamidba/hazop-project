package Application;

import java.awt.geom.Point2D;

import Vue.Graph;
import Vue.InterfaceNoeud;

public class CommandeAjouterElementAuGraphe extends CommandeDefaisable
{
	private Point2D positionSouris;
	private InterfaceNoeud lien;
	
	public CommandeAjouterElementAuGraphe(Graph diagramme)
	{
		super(diagramme);
	}
	
	public void setParametres( InterfaceNoeud lienAAjouter, Point2D souris)
	{
		positionSouris = souris;
		lien = lienAAjouter;
		
	}
	
	public void executer()
	{
		diagramme.add(lien, positionSouris);
	}

	public void defaire()
	{
		diagramme.removeNode(lien);
	}

	public void refaire()
	{
		diagramme.add(lien, positionSouris);
	}

	public boolean estActivable()
	{
		return false;
	}
}
