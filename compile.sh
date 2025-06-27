#!/bin/bash

# Nettoyage
rm -rf bin
mkdir -p bin

# Définir le classpath pour inclure JUnit
CLASSPATH="lib/*:bin"

# Compilation des classes
echo "Compilation des classes de base..."
javac -d bin com/schottenTotten/model/*.java

echo "Compilation des variantes..."
javac -d bin com/schottenTotten/controller/variantes/*.java

echo "Compilation des contrôleurs..."
javac -d bin com/schottenTotten/controller/*.java
javac -d bin com/schottenTotten/ai/*.java

# Compilation des tests
echo "Compilation des tests..."
javac -cp $CLASSPATH -d bin com/schottenTotten/tests/TestSuite.java

# Compilation de l'interface graphique
echo "Compilation de l'interface graphique..."
javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls -d bin com/schottenTotten/view/SchottenTottenGUI.java

if [ $? -eq 0 ]; then
    echo "Compilation terminée avec succès."
    echo "Lancement du jeu..."
    java --module-path /usr/share/openjfx/lib --add-modules javafx.controls -cp bin com.schottenTotten.view.SchottenTottenGUI
else
    echo "Erreur lors de la compilation de l'interface graphique"
    exit 1
fi