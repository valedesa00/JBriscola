package model;

/**
 * Interfaccia di utilità che descrive il protocollo del game loop.
 * Implementata da {@link Partita} e utilizzata dal game loop in
 * {@link controller.GameController#main} per pilotare la progressione della
 * partita in modo disaccoppiato dall'implementazione concreta del modello.
 */
public interface Evolvibile {
	
	/**
     * Blocca il thread chiamante finché l'utente non avvia una nuova partita.
     *
     * @throws InterruptedException se il thread viene interrotto durante l'attesa
     */
    void aspettaInizioPartita() throws InterruptedException;
    
    /**
     * Blocca il thread chiamante finché l'utente non gioca una carta,
     * il turno non passa a un giocatore CPU, il turno non va in attesa
     * di conferma o la partita non termina.
     *
     * @throws InterruptedException se il thread viene interrotto durante l'attesa
     */
    void aspettaMossaUtente() throws InterruptedException;
    
    /**
     * Avanza la partita eseguendo la mossa del giocatore CPU attuale.
     * Non ha effetto se è il turno dell'utente, il turno è in attesa 
     * di conferma o la partita è terminata.
     */
    void evolvi();

    /**
     * Chiude il turno attuale: assegna le carte al vincitore, fa pescare dal mazzo
     * e aggiorna il primo di mano per il turno successivo.
     */
    void concludiTurno();
    
    /**
     * Indica se è il turno del giocatore utente.
     * 
     * @return {@code true} se è il turno del giocatore utente 
     */
    boolean isTurnoUtente();

    /** 
     * Indica se la partita è in attesa si confermi la conclusione del turno.
     * 
     * @return {@code true} se il vincitore del turno è stato determinato ma il turno non è ancora concluso 
     */
    boolean isTurnoInAttesa();
    
    /**
     * Indica se la partita è terminata.
     *  
     * @return {@code true} se la partita è terminata 
     */
    boolean isGameOver();

    
}
