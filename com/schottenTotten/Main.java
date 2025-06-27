
package com.schottenTotten;

import com.schottenTotten.model.*;
import com.schottenTotten.controller.variantes.*;
import com.schottenTotten.ai.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Test du jeu Schotten-Totten ===");
        IJeu jeu = JeuFactory.creerJeu(VarianteJeu.NORMAL, "Joueur1", "IA", NiveauIA.MOYEN);
        
        System.out.println("\nJoueurs :");
        System.out.println("- " + jeu.getJoueur1().getNom());
        System.out.println("- " + jeu.getJoueur2().getNom());
        
        System.out.println("\nNombre de cartes en main :");
        System.out.println("- " + jeu.getJoueur1().getNom() + ": " + jeu.getJoueur1().getMain().size());
        System.out.println("- " + jeu.getJoueur2().getNom() + ": " + jeu.getJoueur2().getMain().size());
        
        System.out.println("\nJeu initialisé avec succès !");
    }
}
