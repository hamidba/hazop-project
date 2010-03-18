package Application;

import Vue.DiagrammeSequence;
import Vue.Graph;
import Vue.InterfaceNoeud;

public class CommandeSupprimerNoeudAuGraph extends CommandeDefaisable
{
	private InterfaceNoeud noeud;
	
	//permet de sauvegarder et de restaurer l'état avant suppression
	private Graph etatPrecedent;
	public CommandeSupprimerNoeudAuGraph(Graph graph)
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
		diagramme.removeNode(noeud);
	}

	public void setParams(InterfaceNoeud noeudASupprimer)
	{
		noeud = noeudASupprimer;
	}

}
