package com.schottenTotten.model;

import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.model.Borne;

public class CarteTactique extends Carte {
    public enum TypeTactique {
        ESPION("Espion", "Permet de voir les cartes de l'adversaire"),
        RUSE("Ruse", "Permet de jouer une carte supplémentaire"),
        BOUCLIER("Bouclier", "Protège une borne contre la revendication"),
        STRATEGE("Stratège", "Permet de déplacer une carte déjà posée"),
        MERCENAIRE("Mercenaire", "Compte comme un joker pour la couleur"),
        BANDIT("Bandit", "Permet de voler une carte de l'adversaire");
        
        private final String nom;
        private final String description;
        
        TypeTactique(String nom, String description) {
            this.nom = nom;
            this.description = description;
        }
        
        public String getNom() { return nom; }
        public String getDescription() { return description; }
    }
    
    private final TypeTactique type;
    
    public CarteTactique(TypeTactique type) {
        super(0, "Tactique");  // Les cartes tactiques n'ont pas de valeur ni de couleur standard
        this.type = type;
    }
    
    public TypeTactique getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return "Carte Tactique: " + type.getNom();
    }
}