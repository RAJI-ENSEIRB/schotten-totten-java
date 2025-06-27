package com.schottenTotten.ai;

import com.schottenTotten.model.*;
import java.util.*;
import java.util.stream.Collectors;

public class StrategieIA {
    private final Joueur joueurIA;
    private final NiveauIA niveau;
    private final List<Borne> bornes;

    public static class Coup {
        public final int indexCarte;
        public final int numeroBorne;

        public Coup(int indexCarte, int numeroBorne) {
            this.indexCarte = indexCarte;
            this.numeroBorne = numeroBorne;
        }
    }

    public StrategieIA(Joueur joueurIA, NiveauIA niveau, List<Borne> bornes) {
        this.joueurIA = joueurIA;
        this.niveau = niveau;
        this.bornes = bornes;
    }

   



    public Coup determinerMeilleurCoup() {
        List<Carte> mainIA = joueurIA.getMain();
        if (mainIA.isEmpty()) return null;

        Coup meilleurCoup = null;
        int meilleurScore = Integer.MIN_VALUE;
        
        // Limiter le nombre de bornes à évaluer
        List<Borne> bornesDisponibles = bornes.stream()
            .filter(b -> b.getProprietaire() == null && !b.estComplete())
            .collect(Collectors.toList());

        for (int i = 0; i < mainIA.size(); i++) {
            Carte carteActuelle = mainIA.get(i);
            
            for (Borne borne : bornesDisponibles) {
                if (borne.getCartesJoueur2().size() >= 3) continue;
                
                int score = evaluerPotentielCoup(carteActuelle, borne, mainIA);
                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurCoup = new Coup(i, borne.getNumero());
                }
            }
        }

        // Si aucun coup trouvé, jouer la première carte possible
        if (meilleurCoup == null && !bornesDisponibles.isEmpty()) {
            return new Coup(0, bornesDisponibles.get(0).getNumero());
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
        
        for (Carte c : cartesActuelles) {
            compteurValeurs.merge(c.getValeur(), 1L, Long::sum);
            if (compteurValeurs.get(c.getValeur()) >= 3) return true;
        }
        
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
        
        for (Carte c : cartesActuelles) {
            compteurCouleurs.merge(c.getCouleur(), 1, Integer::sum);
            if (compteurCouleurs.get(c.getCouleur()) >= 3) return true;
        }
        
        for (Carte c : mainRestante) {
            compteurCouleurs.merge(c.getCouleur(), 1, Integer::sum);
            if (compteurCouleurs.get(c.getCouleur()) >= 3) return true;
        }
        
        return false;
    }
}
