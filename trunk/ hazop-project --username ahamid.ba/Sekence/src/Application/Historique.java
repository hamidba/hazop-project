package Application;

import java.util.ArrayList;

public class Historique
{
	
	ArrayList<Commande> liste ;
	ArrayList<Commande> listeTampon ;
	
	public Historique() {
		this.liste = new ArrayList<Commande>();
		this.listeTampon = new ArrayList<Commande>();
	}
	
	public void ajouterCommande(Commande cmd) {
		this.listeTampon.clear();
		this.liste.add(cmd);

		
	}
	
	public CommandeDefaisable getDerniereCommande() {
		System.out.println("ajout de la commande dans le tampon");
		CommandeDefaisable cmd = null;
		
		if (liste.size() == 0) {
			System.out.println("Pas de commande a annuler");
			return null;
		}
		else {
			cmd = (CommandeDefaisable) liste.get(liste.size() - 1);
			liste.remove(liste.size() - 1);
			listeTampon.add(cmd);
		}
		return cmd; 
	}
	
	public CommandeDefaisable getPremiereCommandeRefaire() {
		System.out.println("Commande refaire, ajout de la commande dans l'historique");
		
		CommandeDefaisable cmd = null;
		
		if (listeTampon.size() == 0) {
			System.out.println("Pas de commande a refaire");
			return null;
		}
		else {
			cmd = (CommandeDefaisable) listeTampon.get(listeTampon.size() - 1);
			listeTampon.remove(listeTampon.size() - 1);
			liste.add(cmd);
		}
		return cmd; 
	}

}
