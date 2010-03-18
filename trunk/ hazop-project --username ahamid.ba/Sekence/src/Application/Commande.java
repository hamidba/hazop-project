package Application;

import Vue.Graph;



public abstract class Commande{
	
	protected Graph diagramme;

	public Commande(Graph graph) {
		super();
		this.diagramme = graph;
	}
	public Graph getDiagramme() {
		return diagramme;
	}
	public void setControleur(Graph controleur) {
		this.diagramme = controleur;
	}

	public abstract void executer();
	
	public abstract boolean estActivable();
	
	
	
}

