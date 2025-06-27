package com.schottenTotten.controller.variantes;

public enum VarianteJeu {
    NORMAL("Variante normale"),
    TACTIQUE("Variante tactique"),
    EXPERT("Variante expert");
    
    private final String nom;
    
    VarianteJeu(String nom) {
        this.nom = nom;
    }
    
    public String getNom() {
        return nom;
    }
}