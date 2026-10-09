package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.Observable;

/**
 * Profilo persistente del giocatore utente, implementato come Singleton.
 * Estende {@link Observable} per notificare la view riguardo gli aggiornamenti del {@code MenuPanel}.
 * <p>
 * Conserva nickname, avatar e statistiche di gioco (partite giocate, vinte, pareggiate e perse).
 * I dati sono serializzati su file tramite {@link #save()} e ripristinati al successivo avvio con {@link #load()}.
 * </p>
 */
@SuppressWarnings("deprecation")
public class ProfiloUtente extends Observable implements Serializable {
	
	private static ProfiloUtente instance;
    private static final long serialVersionUID = 1L;
    private static final String SAVE_FILE = "resources/profile/profile.dat";

    private String nickname = "Utente";
    private String avatar = "";
    private int partiteGiocate = 0;
    private int partiteVinte = 0;
    private int partitePareggiate = 0;
    private int partitePerse = 0;
    
    /** 
     * Costruttore privato: l'istanziazione avviene solo tramite {@link #getInstance()}. 
     */
    private ProfiloUtente() {}
    
    /**
     * Restituisce l'istanza singleton del profilo utente, caricandola da file se disponibile.
     *
     * @return unica istanza {@code ProfiloUtente}
     */
    public static ProfiloUtente getInstance() {
        if (instance == null) {
        	instance = load();
        }
        return instance;
    }
    
    /**
     * Restituisce il nickname del giocatore.
     *
     * @return nickname corrente
     */
    public String getNickname() { 
    	return nickname; 
    }
    
    /**
     * Imposta il nickname del giocatore, notifica l'observer e salva il profilo.
     *
     * @param nickname nuovo nickname
     */
    public void setNickname(String nickname) { 
    	this.nickname = nickname; 
    	notifyObservers(Eventi.PROFILO_AGGIORNATO); 
    	save(); 
    }
    
    /**
     * Restituisce il percorso del file immagine dell'avatar.
     *
     * @return path dell'avatar
     */
    public String getPathAvatar() { 
    	return avatar; 
    }
    
    /**
     * Imposta il percorso dell'avatar, notifica l'observer e salva il profilo.
     *
     * @param pathAvatar percorso del file immagine
     */
    public void setPathAvatar(String pathAvatar) { 
    	this.avatar = pathAvatar; 
    	notifyObservers(Eventi.PROFILO_AGGIORNATO); 
    	save(); 
    }
    
    /**
     * Restituisce il numero totale di partite giocate.
     *
     * @return partite giocate
     */
    public int getPartiteGiocate() { 
    	return partiteGiocate; 
    }
    
    /**
     * Restituisce il numero di partite vinte.
     *
     * @return partite vinte
     */
    public int getPartiteVinte() { 
    	return partiteVinte; 
    }
    
    /**
     * Restituisce il numero di partite pareggiate.
     *
     * @return partite pareggiate
     */
    public int getPartitePareggiate() { 
    	return partitePareggiate; 
    }
    
    /**
     * Restituisce il numero di partite perse.
     *
     * @return partite perse
     */
    public int getPartitePerse() { 
    	return partitePerse; 
    }
    
    /**
     * Incrementa partite giocate e vinte, notifica l'observer e salva il profilo.
     */
    public void aggiornaVittorie() { 
    	partiteGiocate++; 
    	partiteVinte++;  
    	notifyObservers(Eventi.PROFILO_AGGIORNATO); 
    	save(); 
    }
    
    /**
     * Incrementa partite giocate e pareggiate, notifica l'observer e salva il profilo.
     */
    public void aggiornaPareggiate() { 
    	partiteGiocate++; 
    	partitePareggiate++; 
    	notifyObservers(Eventi.PROFILO_AGGIORNATO); 
    	save(); 
    }
    
    /**
     * Incrementa partite giocate e perse, notifica l'observer e salva il profilo.
     */
    public void aggiornaSconfitte() { 
    	partiteGiocate++; 
    	partitePerse++; 
    	notifyObservers(Eventi.PROFILO_AGGIORNATO); 
    	save(); 
    }
    
    /**
     * Tenta di deserializzare il profilo dal file di salvataggio.
     * Se il file non esiste o è corrotto, restituisce un nuovo profilo vuoto.
     *
     * @return profilo caricato da file o nuovo profilo predefinito
     */
    public static ProfiloUtente load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (ProfiloUtente) ois.readObject();
        } catch (Exception e) {
            return new ProfiloUtente();
        }
    }
    
    /**
     * Serializza il profilo corrente nel file di salvataggio, creando la directory se necessario.
     */
    public void save() {
        try {
            new File("resources").mkdirs();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
                oos.writeObject(this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Marca il modello come modificato e notifica tutti gli observer registrati.
     *
     * @param arg evento notificato agli observer
     */
    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}