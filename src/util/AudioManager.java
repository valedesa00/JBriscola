package util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Gestore degli effetti sonori dell'applicazione, implementato come Singleton.
 * Permette di riprodurre file audio in formato .wav tramite il metodo {@link #play(String)}.
 */
public class AudioManager {

    private static AudioManager instance;
    
    /** 
     * Costruttore privato: l'istanziazione avviene solo tramite {@link #getInstance()}. 
     */
    private AudioManager() {}
    
    /**
     * Restituisce l'istanza singleton dell'AudioManager.
     *
     * @return unica istanza di {@code AudioManager}
     */
    public static AudioManager getInstance() {
        if (instance == null) {
        	instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * Carica e riproduce il file audio specificato in modo asincrono.
     * Se il file non viene trovato o il formato non è supportato, stampa lo stack trace
     * senza interrompere l'applicazione.
     *
     * @param filename percorso del file audio .wav da riprodurre
     */
    public void play(String filename) {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filename));
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (FileNotFoundException e) { 
        	e.printStackTrace();
        } catch (IOException e) { 
        	e.printStackTrace(); 
        } catch (UnsupportedAudioFileException e) { 
        	e.printStackTrace(); 
        } catch (LineUnavailableException e) { 
        	e.printStackTrace(); 
        }
    }
}