package com.schottenTotten.tests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import com.schottenTotten.model.*;
import com.schottenTotten.controller.*;
import com.schottenTotten.controller.variantes.*;
import com.schottenTotten.ai.*;
import java.util.List;

public class TestSuite {

    @Nested
    class TestCartes {
        private Carte carte;

        @BeforeEach
        void setUp() {
            carte = new Carte(5, "Rouge");
        }

        @Test
        void testCreationCarte() {
            assertEquals(5, carte.getValeur());
            assertEquals("Rouge", carte.getCouleur());
        }

        @Test
        void testCartesEgales() {
            Carte autreCarte = new Carte(5, "Rouge");
            assertEquals(carte.getValeur(), autreCarte.getValeur());
            assertEquals(carte.getCouleur(), autreCarte.getCouleur());
        }
    }

    @Nested
    class TestJoueur {
        private Joueur joueur;
        private Carte carte;

        @BeforeEach
        void setUp() {
            joueur = new Joueur("Test", true);
            carte = new Carte(5, "Rouge");
        }

        @Test
        void testAjoutCarte() {
            joueur.ajouterCarte(carte);
            assertEquals(1, joueur.getMain().size());
            assertTrue(joueur.getMain().contains(carte));
        }

        @Test
        void testJouerCarte() {
            joueur.ajouterCarte(carte);
            Carte carteJouee = joueur.jouerCarte(0);
            assertEquals(carte, carteJouee);
            assertTrue(joueur.getMain().isEmpty());
        }
    }

    @Nested
    class TestBorne {
        private Borne borne;
        private Joueur joueur1;
        private Joueur joueur2;

        @BeforeEach
        void setUp() {
            borne = new Borne(1);
            joueur1 = new Joueur("Joueur1", true);
            joueur2 = new Joueur("Joueur2", true);
        }

        @Test
        void testAjoutCarteBorne() {
            Carte carte = new Carte(5, "Rouge");
            borne.ajouterCarte(carte, joueur1, joueur1);
            assertEquals(1, borne.getCartesJoueur1().size());
            assertTrue(borne.getCartesJoueur1().contains(carte));
        }

        @Test
        void testBorneComplete() {
            for (int i = 1; i <= 3; i++) {
                borne.ajouterCarte(new Carte(i, "Rouge"), joueur1, joueur1);
                borne.ajouterCarte(new Carte(i, "Bleu"), joueur2, joueur1);
            }
            assertTrue(borne.estComplete());
        }

        @Test
        void testRevendication() {
            for (int i = 1; i <= 3; i++) {
                borne.ajouterCarte(new Carte(i, "Rouge"), joueur1, joueur1);
            }
            borne.setProprietaire(joueur1);
            assertEquals(joueur1, borne.getProprietaire());
        }
    }

    @Nested
    class TestGestionPartie {
        private GestionnairePartie gestion;
        private String joueur1;
        private String joueur2;

        @BeforeEach
        void setUp() {
            joueur1 = "Joueur1";
            joueur2 = "Joueur2";
            gestion = new GestionnairePartie(VarianteJeu.NORMAL, joueur1, joueur2, null);
        }

        @Test
        void testInitialisationPartie() {
            assertNotNull(gestion.getJoueurCourant());
            assertEquals(9, gestion.getBornes().size());
            assertFalse(gestion.estPartieTerminee());
        }

        @Test
        void testConditionsVictoire() {
            List<Borne> bornes = gestion.getBornes();
            for (int i = 0; i < 5; i++) {
                bornes.get(i).setProprietaire(gestion.getJoueur1());
            }
            assertTrue(gestion.estPartieTerminee());
        }

        @Test
        void testJouerCarteInvalide() {
            assertFalse(gestion.jouerCarte(-1, 1));
            assertFalse(gestion.jouerCarte(0, 10));
        }
    }

    @Nested
    class TestVariantes {
        private GestionnaireVariante gestionnaire;

        @BeforeEach
        void setUp() {
            gestionnaire = new GestionnaireVariante(VarianteJeu.NORMAL);
        }

        @Test
        void testVarianteNormale() {
            assertFalse(gestionnaire.getConfiguration().hasCartesTactiques());
            assertEquals(54, gestionnaire.getPaquet().size());
        }

        @Test
        void testVarianteTactique() {
            gestionnaire = new GestionnaireVariante(VarianteJeu.TACTIQUE);
            assertTrue(gestionnaire.getConfiguration().hasCartesTactiques());
            assertTrue(gestionnaire.getPaquet().size() > 54);
        }
    }

    @Nested
    class TestVictoireAdjacente {
        private GestionnairePartie gestion;

        @BeforeEach
        void setUp() {
            gestion = new GestionnairePartie(VarianteJeu.NORMAL, "Joueur1", "Joueur2", null);
        }

        @Test
        void testVictoireTroisBornesAdjacentes() {
            List<Borne> bornes = gestion.getBornes();
            for (int i = 0; i < 3; i++) {
                bornes.get(i).setProprietaire(gestion.getJoueur1());
            }
            assertTrue(gestion.estPartieTerminee());
            assertEquals(gestion.getJoueur1(), gestion.determinerGagnant());
        }

        @Test
        void testPasDeBornesAdjacentesSuffisantes() {
            List<Borne> bornes = gestion.getBornes();
            bornes.get(0).setProprietaire(gestion.getJoueur1());
            bornes.get(2).setProprietaire(gestion.getJoueur1());
            bornes.get(4).setProprietaire(gestion.getJoueur1());
            assertFalse(gestion.estPartieTerminee());
        }
    }

    @Nested
    class TestExceptions {
        private Borne borne;
        private Joueur joueur;

        @BeforeEach
        void setUp() {
            borne = new Borne(1);
            joueur = new Joueur("Test", true);
        }

        @Test
        void testAjoutCarteBorneComplete() {
            for (int i = 0; i < 3; i++) {
                borne.ajouterCarte(new Carte(i + 1, "Rouge"), joueur, joueur);
            }
            
            assertThrows(IllegalStateException.class, () -> {
                borne.ajouterCarte(new Carte(4, "Rouge"), joueur, joueur);
            });
        }

        @Test
        void testRevendicationBorneDejaRevendiquee() {
            borne.setProprietaire(joueur);
            assertThrows(IllegalStateException.class, () -> {
                borne.setProprietaire(joueur);
            });
        }
    }

    @Nested
    class TestStrategieIA {
        private IJeu jeu;
        private NiveauIA niveau;

        @BeforeEach
        void setUp() {
            niveau = NiveauIA.MOYEN;
            jeu = JeuFactory.creerJeu(VarianteJeu.NORMAL, "Joueur", "IA", niveau);
        }

        @Test
        void testIAJoueCoup() {
            Joueur joueurIA = jeu.getJoueur2();
            assertFalse(joueurIA.estHumain());
            int mainInitiale = joueurIA.getMain().size();
            jeu.jouerTourIA();
            assertTrue(joueurIA.getMain().size() < mainInitiale || mainInitiale == 0);
        }

        @Test
        void testIAChoisitBorneValide() {
            Joueur joueurIA = jeu.getJoueur2();
            jeu.jouerTourIA();
            boolean auMoinsUneCarte = jeu.getBornes().stream()
                .anyMatch(b -> !b.getCartesJoueur2().isEmpty());
            assertTrue(auMoinsUneCarte);
        }
    }
}