package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta il mazzo da 40 carte usato nel gioco di Briscola.
 * Le carte vengono generate automaticamente alla creazione e mescolate casualmente.
 * Il mazzo opera come una pila: {@link #pescaCarta()} pesca dall'alto e {@link #pescaBriscola()} legge
 * la carta in fondo, senza rimuoverla.
 */
public class Mazzo {

    private final List<Carta> carte = new ArrayList<>();
    
    /**
     * Costruisce il mazzo generando tutte e 40 le carte e mescolandole.
     */
    public Mazzo() {
        reset();
    }

    /**
     * Ricrea tutte e 40 le carte (10 valori × 4 semi) e le mescola casualmente.
     */
    public void reset() {
        carte.clear();
        for (Seme seme : Seme.values()) {
            for (int valore = 1; valore <= 10; valore++) {
                carte.add(new Carta(valore, seme));
            }
        }
        Collections.shuffle(carte);
    }
    
    /**
     * Rimuove e restituisce la carta in cima al mazzo.
     *
     * @return la carta pescata, oppure {@code null} se il mazzo è vuoto
     */
    public Carta pescaCarta() {
        if (carte.isEmpty()) {
        	return null;
        }
        return carte.remove(carte.size() - 1);
    }
    
    /**
     * Restituisce senza rimuoverla la carta in fondo al mazzo (la briscola).
     *
     * @return la carta briscola, oppure {@code null} se il mazzo è vuoto
     */
    public Carta pescaBriscola() {
        if (carte.isEmpty()) {
        	return null;
        }
        return carte.get(0);
    }
    
    /**
     * Indica se il mazzo è privo di carte.
     *
     * @return {@code true} se non ci sono carte rimaste
     */
    public boolean isEmpty() { 
    	return carte.isEmpty(); 
    }
    
    /**
     * Restituisce il numero di carte ancora presenti nel mazzo.
     *
     * @return numero di carte rimanenti
     */
    public int size() { 
    	return carte.size(); 
    }
}