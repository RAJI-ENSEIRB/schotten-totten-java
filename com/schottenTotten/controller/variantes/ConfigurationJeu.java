package com.schottenTotten.controller.variantes;

import java.util.ArrayList;
import java.util.List;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.model.Borne;

public class ConfigurationJeu {
    private final int nombreCartes;
    private final int nombreBornes;
    private final boolean cartesTactiques;
    private final int cartesParMain;
    private final int maxCartesParBorne;
    private final boolean revendicationMultiple;
    
    public static class Builder {
        private int nombreCartes = 54;        // Par défaut (9 valeurs × 6 couleurs)
        private int nombreBornes = 9;         // Par défaut
        private boolean cartesTactiques = false;
        private int cartesParMain = 6;        // Par défaut
        private int maxCartesParBorne = 3;    // Par défaut
        private boolean revendicationMultiple = false;
        
        public Builder nombreCartes(int val) {
            nombreCartes = val;
            return this;
        }
        
        public Builder nombreBornes(int val) {
            nombreBornes = val;
            return this;
        }
        
        public Builder cartesTactiques(boolean val) {
            cartesTactiques = val;
            return this;
        }
        
        public Builder cartesParMain(int val) {
            cartesParMain = val;
            return this;
        }
        
        public Builder maxCartesParBorne(int val) {
            maxCartesParBorne = val;
            return this;
        }
        
        public Builder revendicationMultiple(boolean val) {
            revendicationMultiple = val;
            return this;
        }
        
        public ConfigurationJeu build() {
            return new ConfigurationJeu(this);
        }
    }
    
    private ConfigurationJeu(Builder builder) {
        nombreCartes = builder.nombreCartes;
        nombreBornes = builder.nombreBornes;
        cartesTactiques = builder.cartesTactiques;
        cartesParMain = builder.cartesParMain;
        maxCartesParBorne = builder.maxCartesParBorne;
        revendicationMultiple = builder.revendicationMultiple;
    }
    
    public int getNombreCartes() { return nombreCartes; }
    public int getNombreBornes() { return nombreBornes; }
    public boolean hasCartesTactiques() { return cartesTactiques; }
    public int getCartesParMain() { return cartesParMain; }
    public int getMaxCartesParBorne() { return maxCartesParBorne; }
    public boolean isRevendicationMultiple() { return revendicationMultiple; }
}