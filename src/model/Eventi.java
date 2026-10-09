package model;

/**
 * Enumerazione degli eventi che il modello può notificare agli Observer.
 * Ogni costante rappresenta un cambiamento di stato significativo della partita o del profilo.
 */
public enum Eventi {
	
	/** 
	 * La partita è stata inizializzata e i giocatori hanno ricevuto le carte. 
	 */
    PARTITA_INIZIATA,
    
    /** 
     * Un giocatore ha giocato una carta e il turno prosegue.
     */
    CARTA_GIOCATA,
    
    /** 
     * Tutte e quattro le carte del turno sono state giocate e il vincitore è stato determinato. 
     */
    VINCITORE_DETERMINATO,
    
    /** 
     * Le carte del turno sono state assegnate al vincitore e il mazzo è stato ridistribuito. 
     */
    TURNO_CONCLUSO,
    
    /** 
     * Tutte le carte sono state giocate e la partita è terminata. 
     */
    GAME_OVER,
    
    /** 
     * Il profilo utente (nickname, avatar o statistiche) è stato aggiornato. 
     */
    PROFILO_AGGIORNATO
}