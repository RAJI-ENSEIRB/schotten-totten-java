package com.schottenTotten.model;

import com.schottenTotten.ai.NiveauIA;
import com.schottenTotten.controller.variantes.VarianteJeu;

public class JeuFactory {
        public static IJeu creerJeu(VarianteJeu variante, String joueur1, String joueur2, NiveauIA niveau) {
        if (variante == null) {
            throw new IllegalArgumentException("La variante ne peut pas être null");
        }
        if (joueur1 == null || joueur1.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du joueur 1 est invalide");
        }
        if (joueur2 == null || joueur2.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du joueur 2 est invalide");
        }
        
        switch (variante) {
            case NORMAL:
                return new JeuNormal(joueur1, joueur2, niveau);
            case TACTIQUE:
                return new JeuTactique(joueur1, joueur2, niveau);
            case EXPERT:
                return new JeuExpert(joueur1, joueur2, niveau);
            default:
                throw new IllegalArgumentException("Variante inconnue : " + variante);
        }
    }
}