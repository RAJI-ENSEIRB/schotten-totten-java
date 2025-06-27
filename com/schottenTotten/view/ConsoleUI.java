package com.schottenTotten.view;

import com.schottenTotten.model.*;
import com.schottenTotten.controller.*;
import com.schottenTotten.controller.variantes.*;
import java.util.Scanner;
import java.util.List;

public class ConsoleUI {
    private final Scanner scanner;
    private final GestionnairePartie gestionnairePartie;
    
    public ConsoleUI() {
        this.scanner = new Scanner(System.in);
        this.gestionnairePartie = initialiserPartie();
    }
    
    private GestionnairePartie initialiserPartie() {
        System.out.println("=== Bienvenue dans Schotten-Totten ===");
        System.out.println("Choisissez une variante :");
        System.out.println("1. Normal");
        System.out.println("2. Tactique");
        System.out.println("3. Expert");
        
        int choix = lireEntier("Votre choix (1-3): ", 1, 3);
        VarianteJeu variante = VarianteJeu.values()[choix - 1];
        
        System.out.print("Nom du Joueur 1: ");
        String nomJoueur1 = scanner.nextLine();
        
        System.out.print("Nom du Joueur 2 (ou 'IA' pour jouer contre l'ordinateur): ");
        String nomJoueur2 = scanner.nextLine();
        
        return new GestionnairePartie(variante, nomJoueur1, nomJoueur2);
    }
    
    public void demarrerJeu() {
        while (!gestionnairePartie.estPartieTerminee()) {
            afficherEtatJeu();
            jouerTourHumain();
        }
    }
    
    private void afficherEtatJeu() {
        System.out.println("=== État du jeu ===");
        System.out.println("Tour de : " + gestionnairePartie.getJoueurCourant().getNom());
        
        System.out.println("Bornes :");
        for (Borne borne : gestionnairePartie.getBornes()) {
            afficherBorne(borne);
        }
        
        // Afficher la main du joueur courant
        System.out.println("Votre main :");
        List<Carte> main = gestionnairePartie.getJoueurCourant().getMain();
        for (int i = 0; i < main.size(); i++) {
            System.out.println((i + 1) + ": " + main.get(i));
        }
    }
    
    private void afficherBorne(Borne borne) {
        System.out.println("Borne " + borne.getNumero() + ":");
        System.out.println("Cartes Joueur 1: " + borne.getCartesJoueur1());
        System.out.println("Cartes Joueur 2: " + borne.getCartesJoueur2());
    }
    
    private void jouerTourHumain() {
        System.out.println("Actions disponibles :");
        System.out.println("1. Jouer une carte");
        System.out.println("2. Revendiquer une borne");
        
        int action = lireEntier("Votre choix (1-2): ", 1, 2);
        
        if (action == 1) {
            int indexCarte = lireEntier("Choisissez une carte (1-" + 
                gestionnairePartie.getJoueurCourant().getMain().size() + "): ", 
                1, gestionnairePartie.getJoueurCourant().getMain().size()) - 1;
            
            int numeroBorne = lireEntier("Choisissez une borne (1-9): ", 1, 9);
            
            gestionnairePartie.jouerCarte(indexCarte, numeroBorne);
        } else {
            int numeroBorne = lireEntier("Quelle borne voulez-vous revendiquer ? (1-9): ", 1, 9);
            if (!gestionnairePartie.revendiquerBorne(numeroBorne)) {
                System.out.println("Impossible de revendiquer cette borne.");
            }
        }
    }
    
    private int lireEntier(String message, int min, int max) {
        while (true) {
            System.out.print(message);
            try {
                String input = scanner.nextLine();
                int valeur = Integer.parseInt(input);
                if (valeur >= min && valeur <= max) {
                    return valeur;
                }
                System.out.println("Veuillez entrer un nombre entre " + min + " et " + max);
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre entre " + min + " et " + max);
            }
        }
    }
}