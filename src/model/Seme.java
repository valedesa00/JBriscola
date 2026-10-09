package model;

import java.awt.Color;

/**
 * Enumerazione dei quattro semi del mazzo di carte da Briscola.
 * Ogni costante espone il nome per esteso, un simbolo Unicode e il colore grafico associato.
 */
public enum Seme {
	
	/** 
	 * Seme delle Coppe, visualizzato in arancione. 
	 */
    COPPE("Coppe", "⊍", new Color(236, 88, 0)),
    
    /** 
     * Seme dei Denari, visualizzato in oro. 
     */
    DENARI("Denari", "❂", new Color(255, 215, 0)),
    
    /** 
     * Seme delle Spade, visualizzato in blu scuro. 
     */
    SPADE("Spade", "⚔", new Color(0, 0, 139)),
    
    /** 
     * Seme dei Bastoni, visualizzato in verde. 
     */
    BASTONI("Bastoni", "↟", new Color(50, 205, 50));

    private final String nome;
    private final String simbolo;
    private final Color colore;
    
    /**
     * Costruisce una costante di seme con nome, simbolo e colore.
     *
     * @param nome nome per esteso del seme
     * @param simbolo carattere Unicode rappresentativo
     * @param colore colore grafico associato al seme
     */
    Seme(String nome, String simbolo, Color colore) {
        this.nome = nome;
        this.simbolo = simbolo;
        this.colore = colore;
    }
    
    /**
     * Restituisce il nome per esteso del seme.
     *
     * @return nome del seme
     */
    public String getNome() { 
    	return nome; 
    }
    
    /**
     * Restituisce il simbolo associato al seme, usato nel rendering delle carte.
     *
     * @return simbolo del seme
     */
    public String getSimbolo() { 
    	return simbolo; 
    }
    
    /**
     * Restituisce il colore grafico associato al seme, usato nel rendering delle carte.
     *
     * @return colore del seme
     */
    public Color getColore() { 
    	return colore; 
    }
}