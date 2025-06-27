package com.schottenTotten.model;

import java.util.*;
import com.schottenTotten.ai.*;

public class JeuTactique extends JeuNormal {
    public JeuTactique(String nomJoueur1, String nomJoueur2, NiveauIA niveau) {
        super(nomJoueur1, nomJoueur2, niveau);
    }

    @Override
    protected List<Carte> creerPiocheStandard() {
        List<Carte> pioche = super.creerPiocheStandard();
        ajouterCartesTactiques(pioche);
        return pioche;
    }

    protected void ajouterCartesTactiques(List<Carte> pioche) {
        // Ajouter les cartes tactiques
        for (CarteTactique.TypeTactique type : CarteTactique.TypeTactique.values()) {
            pioche.add(new CarteTactique(type));
        }
        Collections.shuffle(pioche);
    }

    @Override
    public boolean jouerCarte(int indexCarte, int numeroBorne) {
        if (!estCoupValide(indexCarte, numeroBorne)) {
            return false;
        }

        Carte carte = joueurCourant.getMain().get(indexCarte);
        if (carte instanceof CarteTactique) {
            return jouerCarteTactique((CarteTactique)carte, numeroBorne);
        } else {
            return super.jouerCarte(indexCarte, numeroBorne);
        }
    }

    private boolean jouerCarteTactique(CarteTactique carte, int numeroBorne) {
        // Implémentation des effets des cartes tactiques
        Borne borne = bornes.get(numeroBorne - 1);
        
        switch (carte.getType()) {
            case ESPION:
                // Voir les cartes de l'adversaire
                return true;
            case RUSE:
                // Jouer une carte supplémentaire
                return true;
            case BOUCLIER:
                // Protéger une borne
                return true;
            default:
                return false;
        }
    }
}