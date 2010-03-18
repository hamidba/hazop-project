package Application;

import Vue.DiagrammeSequence;
import Vue.Edge;
import Vue.Graph;
import Vue.InterfaceNoeud;

public class CommandeSupprimerLienAuGraph extends CommandeDefaisable
{
	private Edge lien;
	
	//permet de sauvegarder et de restaurer l'état avant suppression
	private Graph etatPrecedent;
	public CommandeSupprimerLienAuGraph(Graph graph)
	{
		super(graph);
	}
	@Override
	public void defaire()
	{
		//On sauve le contexte courant dans un diagramme temporaire
		DiagrammeSequence tmp = new DiagrammeSequence();
		tmp.setGraph(diagramme);
		//on restaure le contexte précédent
		diagramme.setGraph(etatPrecedent);
		//On sauvegarde l'ancien contexte
		etatPrecedent = new DiagrammeSequence();
		etatPrecedent.setGraph(tmp);
		
	}

	@Override
	public void refaire()
	{
		executer();
	}

	@Override
	public boolean estActivable()
	{
		return false;
	}

	@Override
	public void executer()
	{
		//On sauve le contexte
		etatPrecedent = new DiagrammeSequence();
		etatPrecedent.setGraph(diagramme);
		//on supprime le noeud
		diagramme.removeEdge(lien);
	}

	public void setParams(Edge lienASupprimer)
	{
		lien = lienASupprimer;
	}

}
