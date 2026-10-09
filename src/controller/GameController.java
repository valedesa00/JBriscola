package controller;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import model.Carta;
import model.Evolvibile;
import model.ModalitaPartita;
import model.Partita;
import model.ProfiloUtente;
import util.AudioManager;
import view.MainFrame;

/**
 * Controller principale dell'applicazione JBriscola.
 */
public class GameController {

    private static final int DELAY_CPU = 1000;
    private static final int DELAY_TURNO = 3000;
    private static final int DELAY_PARTITA = 5000;

    private final Partita partita;
    
    /**
     * Memorizza il Singleton del modello.
     */
    public GameController() {
        this.partita = Partita.getInstance();
    }

    /**
     * Punto di ingresso:
     * <ol>
     *   <li>Ottiene i Singleton di model e view.</li>
     *   <li>Registra la view come Observer del model.</li>
     *   <li>Aggancia gli ActionListener ai componenti della view.</li>
     *   <li>Esegue il game loop sul thread principale.</li>
     * </ol>
     *
     * @param args argomenti da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
        
    	GameController controller = new GameController();
        Partita partita = Partita.getInstance();
        ProfiloUtente profiloUtente = ProfiloUtente.getInstance();
        MainFrame view = MainFrame.getInstance();
        
        // registra gli Observer
        partita.addObserver(view.getGamePanel());
        profiloUtente.addObserver(view.getMenuPanel());

        // aggancia ActionListener ai componenti della View
        view.getMenuPanel().getButtonSingolo().addActionListener(e -> controller.iniziaNuovaPartita(profiloUtente.getNickname(), ModalitaPartita.SINGOLO));
        view.getMenuPanel().getButtonSquadre().addActionListener(e -> controller.iniziaNuovaPartita(profiloUtente.getNickname(), ModalitaPartita.SQUADRE));
        view.getGamePanel().setOnCartaGiocata(carta -> controller.cartaGiocata(carta));
        
        Evolvibile model = partita;

        // game loop
        try {
            while (true) {
            	// attende che inizi una nuova partita
                model.aspettaInizioPartita();                                           

                while (!model.isGameOver()) {
                    if (model.isTurnoInAttesa()) {
                    	// pausa per mostrare vincitore turno
                        Thread.sleep(DELAY_TURNO);                          
                        // chiude il turno sull'EDT
                        SwingUtilities.invokeAndWait(model::concludiTurno);      
                    } else if (model.isTurnoUtente()) {
                    	// attende mossa utente
                        model.aspettaMossaUtente();                               
                    } else {
                    	// pausa tra mosse CPU
                        Thread.sleep(DELAY_CPU);                             
                        AudioManager.getInstance().play("resources/audio/cartaGiocata.wav");
                        // mossa CPU sull'EDT
                        SwingUtilities.invokeAndWait(model::evolvi);             
                    }
                }
                // pausa per mostrare vincitore partita
                Thread.sleep(DELAY_PARTITA);
                // reset pannello partita
                SwingUtilities.invokeAndWait(MainFrame.getInstance().getGamePanel()::resetToIniziale);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
    }

    // azioni view

    /**
     * Avvia una nuova partita. Chiamato dall'ActionListener agganciato nel {@code main}.
     *
     * @param nomeUtente nome del giocatore utente
     * @param modalitaPartita modalità di gioco scelta
     */
    public void iniziaNuovaPartita(String nomeUtente, ModalitaPartita modalitaPartita) {
        partita.iniziaNuovaPartita(nomeUtente, modalitaPartita);
    }

    /**
     * Gestisce la carta giocata dall'utente. Chiamato dal callback impostato nel {@code main}.
     *
     * @param carta carta selezionata dall'utente
     */
    public void cartaGiocata(Carta carta) {
        if (!partita.isTurnoUtente() || partita.isGameOver()) {
            return;
        }
        AudioManager.getInstance().play("resources/audio/cartaGiocata.wav");
        partita.giocaCarta(partita.getGiocatoreUtente(), carta);
    }
}
