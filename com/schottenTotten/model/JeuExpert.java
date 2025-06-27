package com.schottenTotten.model;

import com.schottenTotten.ai.NiveauIA;

public class JeuExpert extends JeuTactique {
    private static final int CARTES_PAR_MAIN = 7;
    private static final int MAX_CARTES_PAR_BORNE = 4;

    public JeuExpert(String nomJoueur1, String nomJoueur2, NiveauIA niveau) {
        super(nomJoueur1, nomJoueur2, niveau);
    }

    @Override
    public void initialiser() {
        for (int i = 1; i <= 9; i++) {
            bornes.add(new Borne(i));
        }

        // Plus de cartes dans la main initiale
        for (int i = 0; i < CARTES_PAR_MAIN; i++) {
            joueur1.ajouterCarte(pioche.remove(0));
            joueur2.ajouterCarte(pioche.remove(0));
        }
    }

    @Override
    protected boolean estCoupValide(int indexCarte, int numeroBorne) {
        if (!super.estCoupValide(indexCarte, numeroBorne)) {
            return false;
        }

        // Vérifications supplémentaires pour le mode expert
        Borne borne = bornes.get(numeroBorne - 1);
        if (joueurCourant == joueur1) {
            return borne.getCartesJoueur1().size() < MAX_CARTES_PAR_BORNE;
        } else {
            return borne.getCartesJoueur2().size() < MAX_CARTES_PAR_BORNE;
        }
    }

    @Override
    protected void verifierFinPartie() {
        // Règles de fin spécifiques au mode expert
        super.verifierFinPartie();
    }
}