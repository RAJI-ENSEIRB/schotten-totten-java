package com.schottenTotten.controller.variantes;

import com.schottenTotten.model.Carte;
import com.schottenTotten.model.CarteTactique;
import com.schottenTotten.model.Joueur;
import com.schottenTotten.model.Borne;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestionnaireVariante {
    private final ConfigurationJeu config;
    private final List<Carte> paquet;
    private final List<CarteTactique> cartesTactiques;
    
    public GestionnaireVariante(VarianteJeu variante) {
        this.config = creerConfiguration(variante);
        this.paquet = new ArrayList<>();
        this.cartesTactiques = new ArrayList<>();
        initialiserPaquet();
    }
    
    private ConfigurationJeu creerConfiguration(VarianteJeu variante) {
        switch (variante) {
            case NORMAL:
                return new ConfigurationJeu.Builder()
                    .nombreCartes(54)
                    .nombreBornes(9)
                    .cartesTactiques(false)
                    .cartesParMain(6)
                    .maxCartesParBorne(3)
                    .revendicationMultiple(false)
                    .build();
                    
            case TACTIQUE:
                return new ConfigurationJeu.Builder()
                    .nombreCartes(60)
                    .nombreBornes(9)
                    .cartesTactiques(true)
                    .cartesParMain(7)
                    .maxCartesParBorne(3)
                    .revendicationMultiple(false)
                    .build();
                    
            case EXPERT:
                return new ConfigurationJeu.Builder()
                    .nombreCartes(60)
                    .nombreBornes(9)
                    .cartesTactiques(true)
                    .cartesParMain(7)
                    .maxCartesParBorne(4)
                    .revendicationMultiple(true)
                    .build();
                    
            default:
                throw new IllegalArgumentException("Variante inconnue: " + variante);
        }
    }
    
    private void initialiserPaquet() {
        // Création des cartes normales
        creerCartesNormales();
        
        // Ajouter les cartes tactiques si nécessaire
        if (config.hasCartesTactiques()) {
            creerCartesTactiques();
            paquet.addAll(cartesTactiques);
        }
        
        // Mélanger le paquet
        Collections.shuffle(paquet);
    }
    
    private void creerCartesNormales() {
        String[] couleurs = {"Rouge", "Bleu", "Vert", "Orange", "Violet", "Jaune"};
        for (String couleur : couleurs) {
            for (int valeur = 1; valeur <= 9; valeur++) {
                paquet.add(new Carte(valeur, couleur));
            }
        }
    }
    
    private void creerCartesTactiques() {
        for (CarteTactique.TypeTactique type : CarteTactique.TypeTactique.values()) {
            CarteTactique carteTactique = new CarteTactique(type);
            cartesTactiques.add(carteTactique);
        }
    }
    
    public List<Carte> getPaquet() {
        return new ArrayList<>(paquet);
    }
    
    public boolean peutJouerCarteTactique(CarteTactique carte, Joueur joueur, Borne borne) {
        if (!config.hasCartesTactiques() || borne.getProprietaire() != null) {
            return false;
        }
        
        switch (carte.getType()) {
            case ESPION:
                return true;
            case RUSE:
                return joueur.getMain().size() > 1;
            case BOUCLIER:
                return !borne.estComplete();
            case STRATEGE:
                return !borne.getCartesJoueur1().isEmpty() || !borne.getCartesJoueur2().isEmpty();
            case MERCENAIRE:
                return !borne.estComplete();
            case BANDIT:
                return !borne.getCartesJoueur1().isEmpty() || !borne.getCartesJoueur2().isEmpty();
            default:
                return false;
        }
    }
    
    public void appliquerCarteTactique(CarteTactique carte, Joueur joueur, Borne borne) {
        if (!peutJouerCarteTactique(carte, joueur, borne)) {
            throw new IllegalStateException("Cette carte tactique ne peut pas être jouée maintenant");
        }
        
        // L'implémentation des effets spécifiques peut être ajoutée ici si nécessaire
    }
    
    public ConfigurationJeu getConfiguration() {
        return config;
    }
    
    public boolean paquetEstVide() {
        return paquet.isEmpty();
    }
    
    public Carte piocherCarte() {
        if (paquet.isEmpty()) {
            throw new IllegalStateException("Le paquet est vide");
        }
        return paquet.remove(0);
    }
    
    public int getNombreCartesRestantes() {
        return paquet.size();
    }
}