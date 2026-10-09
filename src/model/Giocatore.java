package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un giocatore della partita, che sia l'utente o la CPU.
 * Gestisce la mano posseduta al momento, le carte acquisite nelle prese e il relativo punteggio.
 * In modalità {@link ModalitaPartita#SQUADRE} viene assegnata una squadra tramite {@link #setSquadra(int)}.
 */
public class Giocatore {

    private final String nome;
    private final boolean utente;
    private int squadra;
    private final List<Carta> mano;
    private final List<Carta> cartePrese;
    
    /**
     * Costruisce un giocatore con il nome e il flag che indica se il giocatore è l'utente.
     *
     * @param nome nome del giocatore
     * @param utente {@code true} se il giocatore è l'utente
     */
    public Giocatore(String nome, boolean utente) {
        this.nome = nome;
        this.utente = utente;
        this.squadra = -1;
        this.mano = new ArrayList<>();
        this.cartePrese = new ArrayList<>();
    }
    
    /**
     * Restituisce il nome del giocatore.
     *
     * @return nome del giocatore
     */
    public String getNome() { 
    	return nome; 
    }
    
    /**
     * Indica se il giocatore è l'utente.
     *
     * @return {@code true} se il giocatore è l'utente
     */
    public boolean isUtente() { 
    	return utente; 
    }
    
    /**
     * In modalità a squadre, restituisce l'indice della squadra assegnata (-1 se non assegnata).
     *
     * @return indice della squadra
     */
    public int getSquadra() { 
    	return squadra; 
    }
    
    /**
     * In modalità a squadre, assegna il giocatore a una squadra.
     *
     * @param squadra indice della squadra (0 o 1)
     */
    public void setSquadra(int squadra) { 
    	this.squadra = squadra; 
    }
    
    /**
     * Restituisce la lista delle carte attualmente in mano al giocatore.
     *
     * @return mano del giocatore
     */
    public List<Carta> getMano() { 
    	return mano; 
    }
    
    /**
     * Restituisce la lista delle carte prese dal giocatore.
     *
     * @return carte prese dal giocatore
     */
    public List<Carta> getCartePrese() { 
    	return cartePrese; 
    }
    
    /**
     * Aggiunge una carta alla mano del giocatore (pescata dal mazzo).
     *
     * @param carta carta da aggiungere alla mano
     */
    public void pescaCarta(Carta carta) { 
    	mano.add(carta); 
    }
    
    /**
     * Rimuove una carta dalla mano del giocatore (giocata sul tavolo).
     *
     * @param carta carta da rimuovere dalla mano
     */
    public void buttaCarta(Carta carta) { 
    	mano.remove(carta); 
    }
    
    /**
     * Aggiunge una carta alle prese acquisite dopo aver vinto un turno.
     *
     * @param carta carta da aggiungere alle prese
     */
    public void aggiungiCartePrese(Carta carta) { 
    	cartePrese.add(carta); 
    }
    
    /**
     * Calcola e restituisce il totale dei punti delle carte acquisite nelle prese.
     *
     * @return punteggio totale del giocatore
     */
    public int getPuntiTotali() {
        return cartePrese.stream().mapToInt(Carta::getPunto).sum();
    }
    
    /**
     * Svuota la mano e le carte prese, riportando il giocatore allo stato iniziale.
     */
    public void reset() {
    	mano.clear();
    	cartePrese.clear();
    }
}