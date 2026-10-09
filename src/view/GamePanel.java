package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import model.Carta;
import model.Eventi;
import model.Giocatore;
import model.ModalitaPartita;
import model.Partita;
import util.AudioManager;

/**
 * Pannello Swing che costituisce il campo da gioco principale di JBriscola.
 * Implementa {@link Observer} per ricevere notifiche dal modello {@link Partita}.
 */
@SuppressWarnings({ "serial", "deprecation" })
public class GamePanel extends JPanel implements Observer {

    private final Partita partita = Partita.getInstance();
    private Consumer<Carta> onCartaGiocata;

    private JPanel panelGiocatoriCPU;
    private JPanel panelTavolo;
    private JPanel panelInfoMazzo;
    private JPanel panelCarteMazzo;
    private JPanel panelManoUtente;
    private JPanel panelCartePrese;
    private JPanel panelStatus;
    private CartaComponent mazzoComponent;
    private CartaComponent briscolaComponent;
    private CartaComponent cartePreseComponent;
    private JLabel labelMazzo;
    private JLabel labelBriscola;
    private JLabel labelRisultato;
    private JLabel labelStatus;
    private JLabel labelCartePrese;

    /**
     * Richiama il builder del pannello.
     */
    public GamePanel() {
        buildLayout();
    }

    /**
     * Imposta il callback invocato quando l'utente clicca una carta della propria mano.
     *
     * @param callback azione da eseguire con la carta selezionata
     */
    public void setOnCartaGiocata(Consumer<Carta> callback) {
        this.onCartaGiocata = callback;
    }
    
    /**
     * Costruisce il layout dell'area di gioco: area CPU in alto, tavolo e info mazzo al centro,
     * mano dell'utente, carte prese e barra di stato in basso.
     */
    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(0, 100, 0));

        // NORTH - CPU
        panelGiocatoriCPU = new JPanel();
        panelGiocatoriCPU.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 8));
        panelGiocatoriCPU.setOpaque(false);
        add(panelGiocatoriCPU, BorderLayout.NORTH);

        // CENTER - tavolo + mazzo/briscola
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);

        panelTavolo = new JPanel();
        panelTavolo.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 10));
        panelTavolo.setOpaque(false);
        panelTavolo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Tavolo", 0, 0, null, Color.WHITE));
        centerPanel.add(panelTavolo, BorderLayout.CENTER);

        panelInfoMazzo = new JPanel();
        panelInfoMazzo.setLayout(new BoxLayout(panelInfoMazzo, BoxLayout.Y_AXIS));
        panelInfoMazzo.setOpaque(false);
        
        labelMazzo = new JLabel("Mazzo: - ");
        labelMazzo.setForeground(Color.WHITE);
        labelMazzo.setFont(labelMazzo.getFont().deriveFont(Font.BOLD));
        labelBriscola = new JLabel("Briscola: - ");
        labelBriscola.setForeground(Color.WHITE);
        labelBriscola.setFont(labelBriscola.getFont().deriveFont(Font.BOLD));
        panelCarteMazzo = new JPanel();
        panelCarteMazzo.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0));
        panelCarteMazzo.setOpaque(false);
        briscolaComponent = new CartaComponent(null, false);
        briscolaComponent.setVisible(false);
        mazzoComponent = new CartaComponent(null, false);
        mazzoComponent.setVisible(true);
        panelCarteMazzo.add(briscolaComponent);
        panelCarteMazzo.add(mazzoComponent);     
        
        panelInfoMazzo.add(labelMazzo);
        panelInfoMazzo.add(Box.createVerticalStrut(8));
        panelInfoMazzo.add(labelBriscola);
        panelInfoMazzo.add(Box.createVerticalStrut(8));
        panelInfoMazzo.add(panelCarteMazzo);
        centerPanel.add(panelInfoMazzo, BorderLayout.EAST);

        
        JPanel panelSpacerCenter = new JPanel();
        panelSpacerCenter.setOpaque(false);
        panelSpacerCenter.setPreferredSize(panelInfoMazzo.getPreferredSize());
        centerPanel.add(panelSpacerCenter, BorderLayout.WEST);

        labelRisultato = new JLabel("", SwingConstants.CENTER);
        labelRisultato.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelRisultato.setForeground(new Color(228, 155, 15));
        labelRisultato.setOpaque(true);
        labelRisultato.setBackground(new Color(0, 0, 0, 80));
        labelRisultato.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        labelRisultato.setVisible(false);
        centerPanel.add(labelRisultato, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // SOUTH - mano utente + status
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);

        panelManoUtente = new JPanel();
        panelManoUtente.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 8));
        panelManoUtente.setOpaque(false);
        panelManoUtente.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "La tua mano", 0, 0, null, Color.WHITE));
        bottomPanel.add(panelManoUtente, BorderLayout.CENTER);

        panelCartePrese = new JPanel();
        panelCartePrese.setLayout(new BoxLayout(panelCartePrese, BoxLayout.Y_AXIS));
        panelCartePrese.setOpaque(false);
        labelCartePrese = new JLabel("Prese: -                 ");
        labelCartePrese.setForeground(Color.WHITE);
        labelCartePrese.setFont(labelCartePrese.getFont().deriveFont(Font.BOLD));
        labelCartePrese.setAlignmentX(CENTER_ALIGNMENT);
        cartePreseComponent = new CartaComponent(null, false);
        cartePreseComponent.setVisible(false);
        cartePreseComponent.setAlignmentX(CENTER_ALIGNMENT);   
        panelCartePrese.add(labelCartePrese);
        panelCartePrese.add(Box.createVerticalStrut(8));
        panelCartePrese.add(cartePreseComponent);
        bottomPanel.add(panelCartePrese, BorderLayout.EAST);

        JPanel panelSpacerSouth = new JPanel();
        panelSpacerSouth.setOpaque(false);
        panelSpacerSouth.setPreferredSize(panelCartePrese.getPreferredSize());
        bottomPanel.add(panelSpacerSouth, BorderLayout.WEST);

        panelStatus = new JPanel();
        panelStatus.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelStatus.setOpaque(false);
        labelStatus = new JLabel("Scegli la modalità di gioco e inizia a giocare", SwingConstants.CENTER);
        labelStatus.setForeground(Color.WHITE);
        labelStatus.setFont(labelStatus.getFont().deriveFont(Font.BOLD));
        panelStatus.add(labelStatus);
        bottomPanel.add(panelStatus, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Ridisegna completamente l'interfaccia leggendo lo stato attuale dal modello.
     */
    public void renderAll() {
        renderPanelGiocatoriCPU();
        renderPanelTavolo();
        renderPanelInfoMazzo();
        renderPanelManoUtente();
        renderPanelCartePrese();
        revalidate();
        repaint();
    }
    
    /**
     * Aggiorna l'area superiore mostrando le carte coperte dei giocatori CPU.
     * In modalità a squadre evidenzia in verde il giocatore alleato.
     * Applica un bordo giallo al nome del vincitore dell'ultimo turno.
     */
    private void renderPanelGiocatoriCPU() {
        panelGiocatoriCPU.removeAll();
        Giocatore giocatoreVincente = partita.isTurnoInAttesa() ? partita.getGiocatoreVincenteTurno() : null;
        boolean isSquadre = partita.getModalitaPartita() == ModalitaPartita.SQUADRE;

        partita.getGiocatori().stream()
                .filter(g -> !g.isUtente())
                .forEach(cpu -> {
                    JPanel panelSlotCPU = new JPanel();
                    panelSlotCPU.setLayout(new BoxLayout(panelSlotCPU, BoxLayout.Y_AXIS));
                    panelSlotCPU.setOpaque(false);
                    
                    boolean isVincente = cpu == giocatoreVincente;
                    boolean isAlleato = isSquadre && cpu.getSquadra() == partita.getGiocatoreUtente().getSquadra();
                    String etichetta = cpu.getNome() + (isAlleato ? " [alleato]" : "") + " (" + cpu.getMano().size() + ")";
                    JLabel labelNomeGiocatore = new JLabel(etichetta);
                    labelNomeGiocatore.setAlignmentX(CENTER_ALIGNMENT);

                    if (isVincente) {
                    	labelNomeGiocatore.setForeground(isAlleato ? new Color(100, 255, 100) : new Color(228, 155, 15));
                        labelNomeGiocatore.setFont(labelNomeGiocatore.getFont().deriveFont(Font.BOLD, 15f));
                        labelNomeGiocatore.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.YELLOW, 2, true), BorderFactory.createEmptyBorder(2, 8, 2, 8)));
                    } else if (isAlleato) {
                        labelNomeGiocatore.setForeground(new Color(100, 255, 100));
                    } else {
                        labelNomeGiocatore.setForeground(new Color(228, 155, 15));
                    }

                    panelSlotCPU.add(labelNomeGiocatore);
                    JPanel panelCarteCPU = new JPanel();
                    panelCarteCPU.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
                    panelCarteCPU.setOpaque(false);
                    cpu.getMano().forEach(c -> panelCarteCPU.add(new CartaComponent(c, false)));
                    panelSlotCPU.add(panelCarteCPU);
                    panelGiocatoriCPU.add(panelSlotCPU);
                });
    }
    
    /**
     * Aggiorna l'area centrale del tavolo mostrando le carte già giocate nel turno attuale,
     * ciascuna accompagnata dal nome del giocatore che l'ha giocata.
     */
    private void renderPanelTavolo() {
        panelTavolo.removeAll();
        List<Giocatore> giocatoriTurno = partita.getGiocatoriTurno();
        List<Carta> carteTurno = partita.getCarteTurno();
        for (int i = 0; i < carteTurno.size(); i++) {
            JPanel panelSlotGiocatore = new JPanel();
            panelSlotGiocatore.setLayout(new BoxLayout(panelSlotGiocatore, BoxLayout.Y_AXIS));
            panelSlotGiocatore.setOpaque(false);          
            JLabel labelNomeGiocatore = new JLabel(giocatoriTurno.get(i).getNome());
            labelNomeGiocatore.setForeground(Color.YELLOW);
            labelNomeGiocatore.setAlignmentX(CENTER_ALIGNMENT);
            panelSlotGiocatore.add(labelNomeGiocatore);
            CartaComponent cc = new CartaComponent(carteTurno.get(i), true);
            cc.setAlignmentX(CENTER_ALIGNMENT);
            panelSlotGiocatore.add(cc);
            panelTavolo.add(panelSlotGiocatore);
        }
    }
    
    /**
     * Aggiorna le label laterali con il numero di carte rimanenti nel mazzo
     * e il nome e il colore della carta di briscola, mostrando mazzo e briscola.
     */
    private void renderPanelInfoMazzo() {
    	int dimensioniMazzo = partita.getDimensioniMazzo();
    	Carta cartaBriscola = partita.getCartaBriscola();
        labelMazzo.setText("Mazzo: " + dimensioniMazzo + " carte rimanenti ");
        
        if (cartaBriscola != null) {
            labelBriscola.setText("Briscola: " + partita.getCartaBriscola().getNome() + " ");
            labelBriscola.setForeground(partita.getCartaBriscola().getSeme().getColore());
            briscolaComponent.setCarta(cartaBriscola, true);
        }
        briscolaComponent.setVisible(dimensioniMazzo > 0);
        mazzoComponent.setVisible(dimensioniMazzo > 0);
    }
    
    /**
     * Aggiorna l'area inferiore con le carte della mano dell'utente.
     * Se è il turno dell'utente vi è hover e onClick su ogni carta.
     */
    private void renderPanelManoUtente() {
        panelManoUtente.removeAll();

        Giocatore giocatoreUtente = partita.getGiocatoreUtente();
        boolean isTurnoUtente = partita.isTurnoUtente() && !partita.isTurnoInAttesa() && !partita.isGameOver();

        giocatoreUtente.getMano().forEach(c -> {
            CartaComponent cc = new CartaComponent(c, true);
            if (isTurnoUtente) {
                cc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                cc.addMouseListener(new MouseAdapter() {
                    @Override 
                    public void mouseEntered(MouseEvent e) { 
                    	cc.setFocus(true); 
                    }
                    @Override 
                    public void mouseExited(MouseEvent e)  { 
                    	cc.setFocus(false); 
                    }
                    @Override 
                    public void mouseClicked(MouseEvent e) {
                        if (onCartaGiocata != null) {
                        	onCartaGiocata.accept(c);
                        }
                    }
                });
            }
            panelManoUtente.add(cc);
        });
    }
    
    /**
     * Aggiorna la label con il numero di carte prese dall'utente (o dalla sua squadra),
     * mostrando le carte prese.
     */
    private void renderPanelCartePrese() {
    	int numeroCartePrese = partita.getNumeroCartePreseUtente();
    	labelCartePrese.setText("Prese: " + numeroCartePrese + "                 ");
    	cartePreseComponent.setVisible(numeroCartePrese > 0);
    } 
    
    /**
     * Mostra nella label di risultato il vincitore dell'ultimo turno, la carta con cui ha vinto
     * e i punti presenti sul tavolo. Il colore varia in base all'appartenenza alla squadra dell'utente.
     */
    public void mostraEsitoTurno() {
        Giocatore giocatoreVincente = partita.getGiocatoreVincenteTurno();
        Carta cartaVincente = partita.getCartaVincenteTurno();
        List<Carta> carteTurno = partita.getCarteTurno();
        int puntiTurno = carteTurno.stream().mapToInt(Carta::getPunto).sum();
        if (giocatoreVincente == null || cartaVincente == null) {
        	return;
        }
        boolean isSquadraUtenteVincente = partita.getModalitaPartita() == ModalitaPartita.SQUADRE ? giocatoreVincente.getSquadra() == partita.getGiocatoreUtente().getSquadra() : giocatoreVincente.isUtente();
        String msg = (giocatoreVincente.isUtente() ? "Hai " : giocatoreVincente.getNome() + " ha ") + "preso con " + cartaVincente.getNome() + " (" + puntiTurno + " punti sul tavolo)";
        labelRisultato.setText(msg);
        labelRisultato.setForeground(isSquadraUtenteVincente ? new Color(100, 255, 100) : new Color(228, 155, 15));
        labelRisultato.setVisible(true);
    }
    
    /**
     * Mostra nella label di risultato l'esito finale della partita, con i punti 
     * totali realizzati dal giocatore o la squadra vincente.
     */
    public void mostraEsitoPartita() {
    	String msg;
    	Color colore;
    	if (partita.getModalitaPartita() == ModalitaPartita.SQUADRE) {
        	int puntiAlleati = partita.getPuntiSquadraUtente();
        	int puntiAvversari = partita.getPuntiSquadraAvversaria();	
        	if (puntiAlleati >= 61) {
        		msg = "🏆 Vittoria! La tua squadra ha realizzato un totale di " + puntiAlleati + " punti";
        		colore = new Color(125, 249, 255);
        		AudioManager.getInstance().play("resources/audio/vittoria.wav");
        	} else if (puntiAvversari >= 61) {
        		msg = "Sconfitta. La squadra avversaria ha realizzato un totale di " + puntiAvversari + " punti";
        		colore = new Color(196, 30, 58);
        		AudioManager.getInstance().play("resources/audio/sconfitta.wav");
        	} else {
        		msg = "Pareggio! Entrambe le squadre hanno realizzato un totale di " + puntiAlleati + " punti";
        		colore = new Color(228, 155, 15);
        		AudioManager.getInstance().play("resources/audio/pareggio.wav");
        	}
        } else {
        	int puntiUtente = partita.getGiocatoreUtente().getPuntiTotali();
        	int puntiCPU = partita.getGiocatori().get(1).getPuntiTotali();
        	if (puntiUtente > puntiCPU) {
        		msg = "🏆 Hai vinto! Hai realizzato un totale di " + puntiUtente + " punti";
        		colore = new Color(125, 249, 255);
        		AudioManager.getInstance().play("resources/audio/vittoria.wav");
        	} else if (puntiCPU > puntiUtente) {
        		Giocatore vincitoreCPU = partita.getGiocatori().get(1);
        		msg = "Hai perso. Vince " + vincitoreCPU.getNome() + " realizzando un totale di " + puntiCPU + " punti";
        		colore = new Color(196, 30, 58);
        		AudioManager.getInstance().play("resources/audio/sconfitta.wav");
        	} else {
        		Giocatore vincitoreCPU = partita.getGiocatori().get(1);
        		msg = "Pareggio! Sia tu che " + vincitoreCPU.getNome() + " avete realizzato un totale di " + puntiUtente + " punti";
        		colore = new Color(228, 155, 15);
        		AudioManager.getInstance().play("resources/audio/pareggio.wav");
        	}
        }
    	labelRisultato.setText(msg);
    	labelRisultato.setForeground(colore);
        labelRisultato.setVisible(true);
    }
    
    /**
     * Nasconde e svuota la label di esito (turno o partita).
     */
    public void nascondiEsito() {
        labelRisultato.setVisible(false);
        labelRisultato.setText("");
    }
    
    /**
     * Aggiorna il testo della barra di stato. L'aggiornamento viene eseguito sull'EDT.
     *
     * @param msg testo da visualizzare nella barra di stato
     */
    public void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> labelStatus.setText(msg));
    }
    
    /**
     * Riporta il pannello di gioco alle condizioni iniziali dell'applicazione:
     * svuota tutte le aree, azzera le label di mazzo e briscola riportando il mazzo coperto e quella delle carte prese,
     * nasconde il risultato e ripristina il messaggio di benvenuto nella barra di stato.
     */
    public void resetToIniziale() {
        panelGiocatoriCPU.removeAll();
        panelTavolo.removeAll();
        panelManoUtente.removeAll();
        briscolaComponent.setVisible(false);
        mazzoComponent.setVisible(true);
        cartePreseComponent.setVisible(false);
        labelMazzo.setText("Mazzo: - ");
        labelBriscola.setText("Briscola: - ");
        labelBriscola.setForeground(Color.WHITE);
        labelCartePrese.setText("Prese: -                 ");
        nascondiEsito();
        setStatus("Scegli la modalità di gioco e inizia a giocare");
        revalidate();
        repaint();
    }

    /**
     * Riceve le notifiche da {@link Partita} e aggiorna la view di conseguenza.
     * 
     * @param o modello che ha notificato il cambiamento
     * @param arg evento notificato
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof Eventi)) {
        	return;
        }
        SwingUtilities.invokeLater(() -> {
            switch ((Eventi) arg) {
                case PARTITA_INIZIATA:
                    nascondiEsito();
                    setStatus("Partita iniziata! " + (partita.isTurnoUtente() ? "Tocca a te." : ""));
                    renderAll();
                    break;
                case CARTA_GIOCATA:
                    renderAll();
                    setStatus(partita.isTurnoUtente() ? "Tocca a te!" : "");
                    break;
                case VINCITORE_DETERMINATO:
                    renderAll();
                    mostraEsitoTurno();
                    setStatus("");
                    break;
                case TURNO_CONCLUSO:
                    nascondiEsito();
                    renderAll();
                    setStatus(partita.isTurnoUtente() ? "Tocca a te!" : "");
                    break;
                case GAME_OVER:
                    nascondiEsito();
                    mostraEsitoPartita();
                    renderAll();
                    setStatus("Partita finita.");
                    break;
                default:
                    break;
            }
        });
    }
}