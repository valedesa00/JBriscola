package model;

/**
 * Rappresenta una singola carta del mazzo di carte da Briscola.
 * Ogni carta è identificata da un valore (1–10) e da un {@link Seme}.
 * Espone la forza e i punti delle carte, oltre che al loro nome e simbolo.
 */
public class Carta {
                             // forza:   A  2  3  4  5  6  7  F  C  R
    private static final int[] FORZA = {10, 1, 9, 2, 3, 4, 5, 6, 7, 8};
                             // punti:   A  2   3  4  5  6  7  F  C  R
    private static final int[] PUNTI = {11, 0, 10, 0, 0, 0, 0, 2, 3, 4};
    
    private static final String[] NOMI = {"Asso", "Due", "Tre", "Quattro", "Cinque", "Sei", "Sette", "Fante", "Cavallo", "Re"};
    private static final String[] SIMBOLI = {"A", "2", "3", "4", "5", "6", "7", "F", "C", "R"};
                             
    private final int valore;
    private final Seme seme;
 
    /**
     * Costruisce una carta con il valore e il seme specificati.
     *
     * @param valore valore numerico della carta (1 = Asso, …, 10 = Re)
     * @param seme seme della carta
     */
    public Carta(int valore, Seme seme) {
        this.valore = valore;
        this.seme = seme;
    }
    
    /**
     * Restituisce la forza della carta usata per determinare chi vince la presa.
     * Ordine decrescente: Asso, Tre, Re, Cavallo, Fante, 7, 6, 5, 4, 2.
     *
     * @return forza della carta
     */
    public int getForza() { 
    	return FORZA[valore - 1]; 
    }
    
    /**
     * Restituisce i punti che la carta vale ai fini del punteggio finale.
     * Asso = 11, Tre = 10, Re = 4, Cavallo = 3, Fante = 2, altri = 0.
     *
     * @return punti della carta
     */
    public int getPunto() { 
    	return PUNTI[valore - 1]; 
    }
    
    /**
     * Restituisce il nome completo della carta (es. "Asso di Coppe").
     *
     * @return nome per esteso
     */
    public String getNome() { 
    	return NOMI[valore - 1] + " di " + seme.getNome(); 
    }
    
    /**
     * Restituisce il simbolo della carta (es. "A", "3", "R").
     *
     * @return simbolo della carta
     */
    public String getSimbolo() { 
    	return SIMBOLI[valore - 1]; 
    }
    
    /**
     * Restituisce il valore numerico della carta (1–10).
     *
     * @return valore della carta
     */
    public int getValore() { 
    	return valore; 
    }  
    
    /**
     * Restituisce il seme della carta.
     *
     * @return seme della carta
     */
    public Seme getSeme() { 
    	return seme; 
    }
}