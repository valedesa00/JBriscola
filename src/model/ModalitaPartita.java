package model;

/**
 * Enumerazione delle modalità di gioco disponibili.
 * <ul>
 *   <li>{@link #SINGOLO} – utente contro un avversario CPU.</li>
 *   <li>{@link #SQUADRE} – due squadre da due: utente e CPU 2 contro CPU 1 e CPU 3.</li>
 * </ul>
 */
public enum ModalitaPartita {
	
	/** 
	 * Modalità individuale: utente vs CPU 1. 
	 */
    SINGOLO,
    
    /** 
     * Modalità a squadre: utente + CPU 2 vs CPU 1 + CPU 3.
     */
    SQUADRE
}