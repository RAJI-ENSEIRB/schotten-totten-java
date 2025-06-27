package com.schottenTotten.model;

public class Carte {
    private final int valeur;
    private final String couleur;
    
    public Carte(int valeur, String couleur) {
        this.valeur = valeur;
        this.couleur = couleur;
    }
    
    public int getValeur() {
        return valeur;
    }
    
    public String getCouleur() {
        return couleur;
    }
    
    @Override
    public String toString() {
        return valeur + " de " + couleur;
    }
}