package com.schottenTotten.model;

import java.util.*;
import com.schottenTotten.ai.*;

public class JeuNormal implements IJeu {
    protected final List<Carte> pioche;
    protected final List<Borne> bornes;
    protected final Joueur joueur1;
    protected final Joueur joueur2;
    protected Joueur joueurCourant;
    protected boolean partieTerminee;
    protected StrategieIA strategieIA;

    public JeuNormal(String nomJoueur1, String nomJoueur2, NiveauIA niveau) {
        this.joueur1 = new Joueur(nomJoueur1, true);
        this.joueur2 = new Joueur(nomJoueur2, !nomJoueur2.equals("IA"));
        this.joueurCourant = joueur1;
        this.pioche = creerPiocheStandard();
        this.bornes = new ArrayList<>();
        
        if (!joueur2.estHumain()) {
            this.strategieIA = new StrategieIA(joueur2, niveau, bornes);
        }
        
        initialiser();
    }

    @Override
    public void initialiser() {
        for (int i = 1; i <= 9; i++) {
            bornes.add(new Borne(i));
        }

        for (int i = 0; i < 6; i++) {
            joueur1.ajouterCarte(pioche.remove(0));
            joueur2.ajouterCarte(pioche.remove(0));
        }
    }

    protected List<Carte> creerPiocheStandard() {
        List<Carte> paquetTemp = new ArrayList<>();
        String[] couleurs = {"Rouge", "Bleu", "Vert", "Orange", "Violet", "Jaune"};
        
        for (String couleur : couleurs) {
            for (int valeur = 1; valeur <= 9; valeur++) {
                paquetTemp.add(new Carte(valeur, couleur));
            }
        }
        Collections.shuffle(paquetTemp);
        return paquetTemp;
    }

    @Override
    public boolean jouerCarte(int indexCarte, int numeroBorne) {
        if (!estCoupValide(indexCarte, numeroBorne)) {
            return false;
        }

        Carte carte = joueurCourant.jouerCarte(indexCarte);
        Borne borne = bornes.get(numeroBorne - 1);
        borne.ajouterCarte(carte, joueurCourant, joueur1);

        if (!pioche.isEmpty()) {
            joueurCourant.ajouterCarte(pioche.remove(0));
        }

        verifierFinPartie();
        joueurCourant = (joueurCourant == joueur1) ? joueur2 : joueur1;

        return true;
    }

    @Override
    public void jouerTourIA() {
        if (strategieIA != null && !joueurCourant.estHumain()) {
            StrategieIA.Coup coup = strategieIA.determinerMeilleurCoup();
            if (coup != null) {
                jouerCarte(coup.indexCarte, coup.numeroBorne);
            }
        }
    }

    protected boolean estCoupValide(int indexCarte, int numeroBorne) {
        // Vérifications de base
        if (partieTerminee) {
            throw new IllegalStateException("La partie est terminée");
        }
        if (indexCarte < 0 || indexCarte >= joueurCourant.getMain().size()) {
            throw new IllegalArgumentException("Index de carte invalide");
        }
        if (numeroBorne < 1 || numeroBorne > bornes.size()) {
            throw new IllegalArgumentException("Numéro de borne invalide");
        }
        
        // Vérification de la pioche
        if (pioche.isEmpty() && joueurCourant.getMain().isEmpty()) {
            throw new IllegalStateException("Plus de cartes disponibles");
        }

        Borne borne = bornes.get(numeroBorne - 1);
        if (borne.getProprietaire() != null) {
            throw new IllegalStateException("Cette borne est déjà revendiquée");
        }
        if (borne.estComplete()) {
            throw new IllegalStateException("Cette borne est complète");
        }

        return true;
    }

    protected void verifierFinPartie() {
        // Vérifier si toutes les bornes sont revendiquées
        boolean toutesRevendiquees = bornes.stream()
                .allMatch(b -> b.getProprietaire() != null);
                
        // Compter les bornes pour chaque joueur
        long bornesJoueur1 = bornes.stream()
                .filter(b -> b.getProprietaire() == this.joueur1)
                .count();
        long bornesJoueur2 = bornes.stream()
                .filter(b -> b.getProprietaire() == this.joueur2)
                .count();
                
        // Vérifier les bornes adjacentes
        boolean victoireAdjacente = false;
        
        // Pour chaque série de 3 bornes consécutives possibles
        for (int i = 0; i < bornes.size() - 2; i++) {
            Joueur proprietaire1 = bornes.get(i).getProprietaire();
            Joueur proprietaire2 = bornes.get(i + 1).getProprietaire();
            Joueur proprietaire3 = bornes.get(i + 2).getProprietaire();
            
            // Si les trois bornes ont le même propriétaire (non null)
            if (proprietaire1 != null && 
                proprietaire1 == proprietaire2 && 
                proprietaire2 == proprietaire3) {
                victoireAdjacente = true;
                break;
            }
        }
        
        // La partie se termine si :
        // - Toutes les bornes sont revendiquées, ou
        // - Un joueur a gagné 5 bornes, ou
        // - Un joueur a gagné 3 bornes adjacentes
        partieTerminee = toutesRevendiquees || 
                        bornesJoueur1 >= 5 || 
                        bornesJoueur2 >= 5 || 
                        victoireAdjacente;
    }

    @Override
    public Joueur determinerGagnant() {
        if (!partieTerminee) return null;

        long bornesJoueur1 = bornes.stream()
            .filter(b -> b.getProprietaire() == this.joueur1)
            .count();
        long bornesJoueur2 = bornes.stream()
            .filter(b -> b.getProprietaire() == this.joueur2)
            .count();

        if (bornesJoueur1 == bornesJoueur2) return null;
        return bornesJoueur1 > bornesJoueur2 ? joueur1 : joueur2;
    }

    // Getters
    @Override public boolean estPartieTerminee() { return partieTerminee; }
    @Override public Joueur getJoueurCourant() { return joueurCourant; }
    @Override public List<Borne> getBornes() { return Collections.unmodifiableList(bornes); }
    @Override public List<Carte> getPioche() { return Collections.unmodifiableList(pioche); }
    @Override public Joueur getJoueur1() { return joueur1; }
    @Override public Joueur getJoueur2() { return joueur2; }
}