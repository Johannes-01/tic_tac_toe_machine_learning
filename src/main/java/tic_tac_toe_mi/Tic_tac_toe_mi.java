/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package tic_tac_toe_mi;

import tictactoe.TicTacToe;
import tictactoe.spieler.ISpieler;
import tictactoe.spieler.beispiel.Zufallsspieler;

/**
 * Hauptprogramm zum Testen des Reinforcement Learning Spielers
 * 
 * @author johanneshaick
 */
public class Tic_tac_toe_mi {

    public static void main(String[] args) {
        System.out.println("=== Tic-Tac-Toe Reinforcement Learning ===");
        System.out.println();
        
        // Erstelle Spieler
        ISpieler zufallsSpieler = new Zufallsspieler("Zufall");
        Spieler rlSpieler = new Spieler("RL-Spieler");
        
        // Teste die Grundfunktionalität
        System.out.println("Spieler erstellt: " + rlSpieler.getName());
        System.out.println();
        
        // Einfacher Test: Ein Spiel spielen
        TicTacToe spiel = new TicTacToe();
        System.out.println("Teste ein einzelnes Spiel gegen Zufallsspieler...");
        ISpieler gewinner = spiel.neuesSpiel(rlSpieler, zufallsSpieler, 150, true);
        
        if (gewinner == null) {
            System.out.println("Ergebnis: Unentschieden");
        } else {
            System.out.println("Gewinner: " + gewinner.getName());
        }
    }
}
