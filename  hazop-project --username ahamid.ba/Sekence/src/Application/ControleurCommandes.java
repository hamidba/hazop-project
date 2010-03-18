package Application;


import java.awt.Point;


public class ControleurCommandes{
	
	private Historique historique;

	//on crée un controleur par fenetre d'edition de diagramme
	//Chaque controleur crée son propre historique
	public ControleurCommandes() {
		this.historique = new Historique();
	}

	public Boolean genererCommande(CommandeDefaisable commande) {
		//On construit la commande avec en parametre le diagramme (c'est la seule entitée modifiable dans le projet)
		//sinon on aurait fait quelquechose de plus générique avec un contexte) et on l'effectue
		if(commande == null)
			return false;
		
		commande.executer();
		historique.ajouterCommande(commande);
		System.out.println("Ajoute de la commande " + commande.getClass() + " dans l'historique");
		return true;
	}
	
	public void annulerCommande()
	{
		//on recupere la derniere commande de l'historique et on effectue son annulation
		CommandeDefaisable cmd = historique.getDerniereCommande();
		if (cmd != null)
		{
			cmd.defaire();
			System.out.println("Annulation de la commande " + cmd.getClass() );
		}
	}
	
	public void refaireCommande()
	{
		//on recupere la derniere commande de l'historique et on effectue son annulation
		CommandeDefaisable cmd = historique.getPremiereCommandeRefaire();
		if (cmd != null)
		{
			cmd.refaire();
			System.out.println("On refait la commande " + cmd.getClass() );
		}
	}
}