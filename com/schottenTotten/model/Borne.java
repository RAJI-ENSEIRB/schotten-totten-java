package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Borne {
    private final int numero;
    private final List<Carte> cartesJoueur1;
    private final List<Carte> cartesJoueur2;
    private Joueur proprietaire;
    private static final int MAX_CARTES = 3;
    
    // Attributs pour les effets tactiques
    private boolean revele;
    private boolean protegee;
    private boolean deplacementAutorise;
    private boolean jokerActif;
    private boolean volPossible;
    
    public Borne(int numero) {
        this.numero = numero;
        this.cartesJoueur1 = new ArrayList<>();
        this.cartesJoueur2 = new ArrayList<>();
        this.proprietaire = null;
        this.revele = false;
        this.protegee = false;
        this.deplacementAutorise = false;
        this.jokerActif = false;
        this.volPossible = false;
    }

    public void ajouterCarte(Carte carte, Joueur joueur, Joueur joueur1) {
        if (proprietaire != null) {
            throw new IllegalStateException("Cette borne a déjà été revendiquée");
        }

        if (joueur == joueur1) {
            if (cartesJoueur1.size() >= MAX_CARTES) {
                throw new IllegalStateException("Maximum de cartes atteint pour le joueur 1");
            }
            cartesJoueur1.add(carte);
        } else {
            if (cartesJoueur2.size() >= MAX_CARTES) {
                throw new IllegalStateException("Maximum de cartes atteint pour le joueur 2");
            }
            cartesJoueur2.add(carte);
        }

        // Vérifier si la borne peut être revendiquée seulement si les deux joueurs ont 3 cartes
        if (cartesJoueur1.size() == MAX_CARTES && cartesJoueur2.size() == MAX_CARTES) {
            verifierRevendication(joueur1, joueur);
        }
    }

    private void verifierRevendication(Joueur joueur1, Joueur joueur2) {
        int valeurCombiJ1 = evaluerCombinaison(cartesJoueur1);
        int valeurCombiJ2 = evaluerCombinaison(cartesJoueur2);

        if (valeurCombiJ1 > valeurCombiJ2) {
            proprietaire = joueur1;
        } else if (valeurCombiJ2 > valeurCombiJ1) {
            proprietaire = joueur2;
        }
    }

    private int evaluerCombinaison(List<Carte> cartes) {
        if (cartes.size() != MAX_CARTES) return -1;

        if (estSuiteColoree(cartes)) return 600;
        if (estBrelan(cartes)) return 500;
        if (estSuite(cartes)) return 400;
        if (estCouleur(cartes)) return 300;
        
        return cartes.stream().mapToInt(Carte::getValeur).sum();
    }

    private boolean estSuiteColoree(List<Carte> cartes) {
        return estSuite(cartes) && estCouleur(cartes);
    }

    private boolean estBrelan(List<Carte> cartes) {
        if (cartes.size() != MAX_CARTES) return false;
        int premiereValeur = cartes.get(0).getValeur();
        return cartes.stream().allMatch(c -> c.getValeur() == premiereValeur);
    }

    private boolean estSuite(List<Carte> cartes) {
        if (cartes.size() != MAX_CARTES) return false;
        List<Integer> valeurs = cartes.stream()
            .map(Carte::getValeur)
            .sorted()
            .collect(Collectors.toList());
        return valeurs.get(1) == valeurs.get(0) + 1 && 
               valeurs.get(2) == valeurs.get(1) + 1;
    }

    private boolean estCouleur(List<Carte> cartes) {
        if (cartes.size() != MAX_CARTES) return false;
        String couleur = cartes.get(0).getCouleur();
        return cartes.stream().allMatch(c -> c.getCouleur().equals(couleur));
    }

    public boolean peutAjouterCarte(Joueur joueur, Joueur joueur1) {
        List<Carte> cartesJoueur = (joueur == joueur1) ? cartesJoueur1 : cartesJoueur2;
        return cartesJoueur.size() < MAX_CARTES && proprietaire == null;
    }

    // Getters et setters pour les effets tactiques
    public void setRevele(boolean revele) {
        this.revele = revele;
    }
    
    public void setProtegee(boolean protegee) {
        this.protegee = protegee;
    }
    
    public void setDeplacementAutorise(boolean deplacementAutorise) {
        this.deplacementAutorise = deplacementAutorise;
    }
    
    public void setJokerActif(boolean jokerActif) {
        this.jokerActif = jokerActif;
    }
    
    public void setVolPossible(boolean volPossible) {
        this.volPossible = volPossible;
    }
    
    public boolean estRevele() {
        return revele;
    }
    
    public boolean estProtegee() {
        return protegee;
    }
    
    public boolean peutDeplacer() {
        return deplacementAutorise;
    }
    
    public boolean aJokerActif() {
        return jokerActif;
    }
    
    public boolean peutVoler() {
        return volPossible;
    }

    public boolean estComplete() {
        return cartesJoueur1.size() == MAX_CARTES && cartesJoueur2.size() == MAX_CARTES;
    }
    
    public void setProprietaire(Joueur proprietaire) {
        if (this.proprietaire != null) {
            throw new IllegalStateException("Cette borne a déjà un propriétaire");
        }
        this.proprietaire = proprietaire;
    }
    
    public Joueur getProprietaire() {
        return proprietaire;
    }
    
    public int getNumero() {
        return numero;
    }
    
    public List<Carte> getCartesJoueur1() {
        return Collections.unmodifiableList(cartesJoueur1);
    }
    
    public List<Carte> getCartesJoueur2() {
        return Collections.unmodifiableList(cartesJoueur2);
    }
}