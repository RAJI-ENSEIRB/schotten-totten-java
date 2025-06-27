package com.schottenTotten.model;

import java.util.List;

public interface IJeu {
    void initialiser();
    boolean jouerCarte(int indexCarte, int numeroBorne);
    void jouerTourIA();
    boolean estPartieTerminee();
    Joueur getJoueurCourant();
    Joueur getJoueur1();
    Joueur getJoueur2();
    List<Borne> getBornes();
    List<Carte> getPioche();
    Joueur determinerGagnant();
}