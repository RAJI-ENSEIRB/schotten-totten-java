package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Joueur {
    private final String nom;
    private final List<Carte> main;
    private boolean estHumain;
    private boolean carteSupplementaire;
    
    public Joueur(String nom, boolean estHumain) {
        this.nom = nom;
        this.main = new ArrayList<>();
        this.estHumain = estHumain;
        this.carteSupplementaire = false;
    }
    
    public void setCarteSupplementaire(boolean carteSupplementaire) {
        this.carteSupplementaire = carteSupplementaire;
    }
    
    public boolean peutJouerCarteSupplementaire() {
        return carteSupplementaire;
    }
    
    public void ajouterCarte(Carte carte) {
        if (carte == null) {
            throw new IllegalArgumentException("La carte ne peut pas être null");
        }
        if (main.size() >= 7) {  // limite max de cartes en main
            throw new IllegalStateException("Main pleine");
        }
        main.add(carte);
    }
        
    public Carte jouerCarte(int index) {
        if (index < 0 || index >= main.size()) {
            throw new IllegalArgumentException("Index de carte invalide");
        }
        return main.remove(index);
    }
    
    public void setEstHumain(boolean estHumain) {
        this.estHumain = estHumain;
    }

    public List<Carte> getMain() {
        return Collections.unmodifiableList(main);
    }
    
    public String getNom() {
        return nom;
    }
    
    public boolean estHumain() {
        return estHumain;
    }
}