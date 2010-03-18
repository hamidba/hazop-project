package Application;

import Vue.Graph;
import Vue.InterfaceNoeud;

public abstract class CommandeDefaisable extends Commande {

	//Constructeur de la commande defaisable, sans l'historique car il est geré par le controleur
	public CommandeDefaisable(Graph diagrammeEnEdition) {
		super(diagrammeEnEdition);
		
	}
	public abstract void defaire();
	public abstract void refaire();
	
}
