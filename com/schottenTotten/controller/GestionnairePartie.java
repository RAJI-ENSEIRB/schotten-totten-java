/*
package com.schottenTotten.controller;

import com.schottenTotten.model.*;
import com.schottenTotten.controller.variantes.*;
import java.util.*;
import java.util.stream.Collectors;

public class GestionnairePartie {
    // Classe interne pour représenter un coup
    private class MeilleurCoup {
        int indexCarte;
        int numeroBorne;

        MeilleurCoup(int indexCarte, int numeroBorne) {
            this.indexCarte = indexCarte;
            this.numeroBorne = numeroBorne;
        }
    }

    private final List<Carte> pioche;
    private final List<Borne> bornes;
    private final Joueur joueur1;
    private final Joueur joueur2;
    private Joueur joueurCourant;
    private boolean partieTerminee;

    public GestionnairePartie(VarianteJeu variante, String nomJoueur1, String nomJoueur2) {
        this.joueur1 = new Joueur(nomJoueur1, true);
        this.joueur2 = new Joueur(nomJoueur2, !nomJoueur2.equals("IA"));
        this.joueurCourant = joueur1;
        
        GestionnaireVariante gestionnaireVariante = new GestionnaireVariante(variante);
        this.pioche = gestionnaireVariante.getPaquet();
        
        this.bornes = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            bornes.add(new Borne(i));
        }
        
        // Distribuer les cartes initiales
        for (int i = 0; i < 6; i++) {
            joueur1.ajouterCarte(pioche.remove(0));
            joueur2.ajouterCarte(pioche.remove(0));
        }
    }

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

        // Si c'est au tour de l'IA, jouer automatiquement
        if (!joueurCourant.estHumain()) {
            jouerTourIA();
        }

        return true;
    }
/*
    private void jouerTourIA() {
        MeilleurCoup meilleurCoup = trouverMeilleurCoupStrategique();
        if (meilleurCoup != null) {
            jouerCarte(meilleurCoup.indexCarte, meilleurCoup.numeroBorne);
        }
    }

    private MeilleurCoup trouverMeilleurCoupStrategique() {
        List<Carte> mainIA = joueurCourant.getMain();
        MeilleurCoup meilleurCoup = null;
        int meilleurScore = -1;

        for (int i = 0; i < mainIA.size(); i++) {
            Carte carteActuelle = mainIA.get(i);
            
            for (Borne borne : bornes) {
                if (borne.getProprietaire() != null) continue;
                
                int score = evaluerPotentielCoup(carteActuelle, borne, mainIA);
                
                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurCoup = new MeilleurCoup(i, borne.getNumero());
                }
            }
        }
        
        return meilleurCoup;
    }

    private int evaluerPotentielCoup(Carte carte, Borne borne, List<Carte> mainComplete) {
        int score = 0;
        List<Carte> cartesIA = new ArrayList<>(borne.getCartesJoueur2());
        cartesIA.add(carte);

        if (peutFormerSuiteColoree(cartesIA, mainComplete)) {
            score += 1000;
        }
        else if (peutFormerBrelan(cartesIA, mainComplete)) {
            score += 800;
        }
        else if (peutFormerSuite(cartesIA, mainComplete)) {
            score += 600;
        }
        else if (peutFormerCouleur(cartesIA, mainComplete)) {
            score += 400;
        }

        score += evaluerDistributionStrategique(borne);
        score += evaluerBloquageAdversaire(carte, borne);

        return score;
    }

    private int evaluerDistributionStrategique(Borne borne) {
        int score = 0;
        
        if (borne.getCartesJoueur2().isEmpty()) {
            score += 50;
        }
        
        if (borne.getCartesJoueur2().size() == 2) {
            score += 200;
        }
        
        int numBorne = borne.getNumero();
        if (numBorne > 1 && bornes.get(numBorne-2).getCartesJoueur2().size() >= 2) {
            score -= 50;
        }
        if (numBorne < 9 && bornes.get(numBorne).getCartesJoueur2().size() >= 2) {
            score -= 50;
        }
        
        return score;
    }

    private int evaluerBloquageAdversaire(Carte carte, Borne borne) {
        List<Carte> cartesAdversaire = borne.getCartesJoueur1();
        if (cartesAdversaire.isEmpty()) return 0;
        
        int score = 0;
        
        if (cartesAdversaire.size() == 2) {
            score += 300;
            if (peutFormerSuiteColoree(cartesAdversaire, new ArrayList<>())) score += 200;
            else if (peutFormerBrelan(cartesAdversaire, new ArrayList<>())) score += 150;
            else if (peutFormerSuite(cartesAdversaire, new ArrayList<>())) score += 100;
        }
        
        return score;
    }

    private boolean peutFormerSuiteColoree(List<Carte> cartes, List<Carte> mainRestante) {
        if (cartes.isEmpty()) return false;
        String couleur = cartes.get(0).getCouleur();
        List<Carte> cartesMemeCouleur = cartes.stream()
                .filter(c -> c.getCouleur().equals(couleur))
                .collect(Collectors.toList());
        
        List<Carte> potentielles = mainRestante.stream()
                .filter(c -> c.getCouleur().equals(couleur))
                .collect(Collectors.toList());
                
        return peutFormerSuite(cartesMemeCouleur, potentielles);
    }

    private boolean peutFormerBrelan(List<Carte> cartesActuelles, List<Carte> mainRestante) {
        if (cartesActuelles.isEmpty()) return false;
        
        Map<Integer, Long> compteurValeurs = new HashMap<>();
        
        // Compter les cartes actuelles
        for (Carte c : cartesActuelles) {
            compteurValeurs.merge(c.getValeur(), 1L, Long::sum);
            if (compteurValeurs.get(c.getValeur()) >= 3) return true;
        }
        
        // Ajouter les cartes de la main
        for (Carte c : mainRestante) {
            compteurValeurs.merge(c.getValeur(), 1L, Long::sum);
            if (compteurValeurs.get(c.getValeur()) >= 3) return true;
        }
        
        return false;
    }

    private boolean peutFormerSuite(List<Carte> cartesActuelles, List<Carte> mainRestante) {
        List<Integer> valeurs = new ArrayList<>();
        valeurs.addAll(cartesActuelles.stream().map(Carte::getValeur).collect(Collectors.toList()));
        valeurs.addAll(mainRestante.stream().map(Carte::getValeur).collect(Collectors.toList()));
        Collections.sort(valeurs);
        
        // Vérifier toutes les séquences possibles de 3 nombres consécutifs
        for (int i = 0; i < valeurs.size() - 2; i++) {
            if (valeurs.get(i + 1) == valeurs.get(i) + 1 && 
                valeurs.get(i + 2) == valeurs.get(i) + 2) {
                return true;
            }
        }
        
        return false;
    }

    private boolean peutFormerCouleur(List<Carte> cartesActuelles, List<Carte> mainRestante) {
        Map<String, Integer> compteurCouleurs = new HashMap<>();
        
        // Compter les couleurs des cartes actuelles
        for (Carte c : cartesActuelles) {
            compteurCouleurs.merge(c.getCouleur(), 1, Integer::sum);
            if (compteurCouleurs.get(c.getCouleur()) >= 3) return true;
        }
        
        // Ajouter les couleurs des cartes en main
        for (Carte c : mainRestante) {
            compteurCouleurs.merge(c.getCouleur(), 1, Integer::sum);
            if (compteurCouleurs.get(c.getCouleur()) >= 3) return true;
        }
        
        return false;
    }

    private boolean estCoupValide(int indexCarte, int numeroBorne) {
        if (indexCarte < 0 || indexCarte >= joueurCourant.getMain().size()) {
            return false;
        }
        if (numeroBorne < 1 || numeroBorne > bornes.size()) {
            return false;
        }
        Borne borne = bornes.get(numeroBorne - 1);
        return !borne.estComplete() && borne.getProprietaire() == null;
    }

    private void verifierFinPartie() {
        boolean toutesRevendiquees = bornes.stream()
            .allMatch(b -> b.getProprietaire() != null);
            
        long bornesJoueur1 = bornes.stream()
            .filter(b -> b.getProprietaire() == this.joueur1)
            .count();
        long bornesJoueur2 = bornes.stream()
            .filter(b -> b.getProprietaire() == this.joueur2)
            .count();
            
        partieTerminee = toutesRevendiquees || bornesJoueur1 >= 5 || bornesJoueur2 >= 5;
    }

    
    public Joueur determinerGagnant() {
        if (!partieTerminee) {
            return null;
        }

        long bornesJoueur1 = bornes.stream()
            .filter(b -> b.getProprietaire() == this.joueur1)
            .count();
        long bornesJoueur2 = bornes.stream()
            .filter(b -> b.getProprietaire() == this.joueur2)
            .count();

        if (bornesJoueur1 == bornesJoueur2) {
            return null; // Match nul
        }
        return bornesJoueur1 > bornesJoueur2 ? joueur1 : joueur2;
    }

    // Getters nécessaires
    public Joueur getJoueurCourant() { return joueurCourant; }
    public List<Borne> getBornes() { return bornes; }
    public boolean estPartieTerminee() { return partieTerminee; }
    public Joueur getJoueur1() { return joueur1; }
    public Joueur getJoueur2() { return joueur2; }
    public List<Carte> getPioche() { return pioche; }
}*/




package com.schottenTotten.controller;

import com.schottenTotten.model.*;
import com.schottenTotten.controller.variantes.*;
import com.schottenTotten.ai.StrategieIA;
import com.schottenTotten.ai.NiveauIA;  // Ajout de l'import pour NiveauIA
import java.util.*;
import java.util.stream.Collectors;

public class GestionnairePartie {
    private final List<Carte> pioche;
    private final List<Borne> bornes;
    private final Joueur joueur1;
    private final Joueur joueur2;
    private Joueur joueurCourant;
    private boolean partieTerminee;
    private StrategieIA strategieIA;

    public GestionnairePartie(VarianteJeu variante, String nomJoueur1, String nomJoueur2, NiveauIA niveau) {
        this.joueur1 = new Joueur(nomJoueur1, true);
        this.joueur2 = new Joueur(nomJoueur2, !nomJoueur2.equals("IA"));
        this.joueurCourant = joueur1;
        
        GestionnaireVariante gestionnaireVariante = new GestionnaireVariante(variante);
        this.pioche = gestionnaireVariante.getPaquet();
        
        this.bornes = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            bornes.add(new Borne(i));
        }
        
        for (int i = 0; i < 6; i++) {
            joueur1.ajouterCarte(pioche.remove(0));
            joueur2.ajouterCarte(pioche.remove(0));
        }

        // Initialisation de l'IA avec les bornes
        if (!joueur2.estHumain()) {
            this.strategieIA = new StrategieIA(joueur2, niveau, bornes);
        }
    }

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

        if (!partieTerminee && !joueurCourant.estHumain()) {
            jouerTourIA();
        }

        return true;
    }

    private void jouerTourIA() {
        if (strategieIA != null) {
            StrategieIA.Coup coup = strategieIA.determinerMeilleurCoup();
            if (coup != null) {
                jouerCarte(coup.indexCarte, coup.numeroBorne);
            }
        }
    }

    private boolean estCoupValide(int indexCarte, int numeroBorne) {
        if (partieTerminee) {
            return false;
        }
        if (indexCarte < 0 || indexCarte >= joueurCourant.getMain().size()) {
            return false;
        }
        if (numeroBorne < 1 || numeroBorne > bornes.size()) {
            return false;
        }
        
        Borne borne = bornes.get(numeroBorne - 1);
        return !borne.estComplete() && borne.getProprietaire() == null;
    }

    private void verifierFinPartie() {
        // Vérifier si toutes les bornes sont revendiquées
        boolean toutesRevendiquees = bornes.stream()
                .allMatch(b -> b.getProprietaire() != null);

        // Ou si un joueur a gagné 5 bornes
        long bornesJoueur1 = bornes.stream()
                .filter(b -> b.getProprietaire() == this.joueur1)
                .count();
        long bornesJoueur2 = bornes.stream()
                .filter(b -> b.getProprietaire() == this.joueur2)
                .count();

        partieTerminee = toutesRevendiquees || bornesJoueur1 >= 5 || bornesJoueur2 >= 5;
    }

    public Joueur determinerGagnant() {
        if (!partieTerminee) {
            return null;
        }

        long bornesJoueur1 = bornes.stream()
                .filter(b -> b.getProprietaire() == this.joueur1)
                .count();
        long bornesJoueur2 = bornes.stream()
                .filter(b -> b.getProprietaire() == this.joueur2)
                .count();

        if (bornesJoueur1 == bornesJoueur2) {
            return null; // Match nul
        }
        return bornesJoueur1 > bornesJoueur2 ? joueur1 : joueur2;
    }

    // Getters
    public Joueur getJoueurCourant() { return joueurCourant; }
    public List<Borne> getBornes() { return Collections.unmodifiableList(bornes); }
    public boolean estPartieTerminee() { return partieTerminee; }
    public Joueur getJoueur1() { return joueur1; }
    public Joueur getJoueur2() { return joueur2; }
    public List<Carte> getPioche() { return Collections.unmodifiableList(pioche); }
}