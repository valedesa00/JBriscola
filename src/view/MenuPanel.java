package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import model.Eventi;
import model.ProfiloUtente;

/**
 * Pannello laterale dell'applicazione JBriscola.
 * Compone le informazioni del profilo (modificabili) e
 * la scelta della modalità per iniziare a giocare.
 * Implementa {@link Observer} per ricevere notifiche dal modello {@link ProfiloUtente}.
 */
@SuppressWarnings({ "serial", "deprecation" })
public class MenuPanel extends JPanel implements Observer {
	
	private final ProfiloUtente profiloUtente = ProfiloUtente.getInstance();
    
	private JPanel panelProfilo;
	private JPanel panelModalita;
    private JLabel labelNickname;
    private JLabel labelAvatar;
    private JLabel labelPartite;
    private JLabel labelRecord;
    private JButton buttonModifica;
    private JButton buttonSingolo;
    private JButton buttonSquadre;
    
    /**
     * Richiama il builder del pannello e sincronizza i dati.
     */
    public MenuPanel() {
        buildLayout();
        refresh();
    }
    
    /** 
     * @return bottone per modalità in singolo, per agganciare l'ActionListener nel {@code main} 
     */
    public JButton getButtonSingolo() { 
    	return buttonSingolo; 
    }

    /** 
     * @return bottone per modalità a squadre, per agganciare l'ActionListener nel {@code main} 
     */
    public JButton getButtonSquadre() { 
    	return buttonSquadre; 
    }

    /**
     * Costruisce il layout del menu: area utente in alto, 
     * bottoni per selezione modalità di gioco in basso.
     */
    public void buildLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 4));
        setPreferredSize(new Dimension(260, 0));
        
        // NORTH — profilo utente
        panelProfilo = new JPanel();
        panelProfilo.setLayout(new BoxLayout(panelProfilo, BoxLayout.Y_AXIS));
        panelProfilo.setBorder(BorderFactory.createTitledBorder("Profilo"));
        
        labelAvatar = new JLabel();
        labelAvatar.setAlignmentX(CENTER_ALIGNMENT);
        labelNickname = new JLabel();
        labelNickname.setAlignmentX(CENTER_ALIGNMENT);
        labelNickname.setForeground(new Color(100, 149, 237));
        labelPartite = new JLabel();
        labelPartite.setAlignmentX(CENTER_ALIGNMENT);
        labelRecord = new JLabel();
        labelRecord.setAlignmentX(CENTER_ALIGNMENT);
        buttonModifica = new JButton("Modifica");  
        buttonModifica.setAlignmentX(CENTER_ALIGNMENT);
        buttonModifica.addActionListener(e -> apriPanelModifica());

        panelProfilo.add(labelAvatar);
        panelProfilo.add(Box.createVerticalStrut(8));
        panelProfilo.add(labelNickname);
        panelProfilo.add(Box.createVerticalStrut(16));
        panelProfilo.add(labelPartite);
        panelProfilo.add(Box.createVerticalStrut(8));
        panelProfilo.add(labelRecord);
        panelProfilo.add(Box.createVerticalStrut(16));
        panelProfilo.add(buttonModifica);
        panelProfilo.add(Box.createVerticalStrut(8));
        
        add(panelProfilo, BorderLayout.NORTH);

        // SOUTH — selezione modalità di gioco     
        panelModalita = new JPanel();
        panelModalita.setLayout(new FlowLayout(FlowLayout.CENTER, 18, 4));
        panelModalita.setBorder(BorderFactory.createTitledBorder("Modalità di gioco"));
        
        buttonSingolo = new JButton("Singolo");
        buttonSquadre = new JButton("Squadre");

        panelModalita.add(buttonSingolo);
        panelModalita.add(buttonSquadre);

        add(panelModalita, BorderLayout.SOUTH);
    }

    /**
     * Aggiorna tutte le label con i dati correnti del profilo utente.
     * Carica e ridimensiona l'immagine avatar se il percorso è disponibile.
     */
    public void refresh() {
        labelNickname.setText(profiloUtente.getNickname());
        labelPartite.setText("Giocate: " + profiloUtente.getPartiteGiocate());
        labelRecord.setText("Vinte: " + profiloUtente.getPartiteVinte() + " | Pareggiate: " + profiloUtente.getPartitePareggiate() + " | Perse: " + profiloUtente.getPartitePerse());

        String pathAvatar = profiloUtente.getPathAvatar();
        if (pathAvatar != null && !pathAvatar.isEmpty()) {
            ImageIcon immagineProfilo = new ImageIcon(new ImageIcon(pathAvatar).getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH));
            labelAvatar.setIcon(immagineProfilo);
            labelAvatar.setText("");
        } else {
            labelAvatar.setText("👤");
            labelAvatar.setFont(new Font("SansSerif", Font.PLAIN, 40));
        }
    }

    /**
     * Pannello che si apre cliccando il bottone di modifica e permette di 
     * inserire il nickname e l'immagine di profilo.
     * Le modifiche vengono applicate al {@link ProfiloUtente} solo alla conferma.
     */
    private void apriPanelModifica() {
        JTextField campoNickname = new JTextField(profiloUtente.getNickname(), 16);
        JLabel campoAvatar = new JLabel(profiloUtente.getPathAvatar());
        JButton buttonImmagine = new JButton("Scegli immagine...");

        buttonImmagine.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                campoAvatar.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        });

        JPanel panelDettagli = new JPanel(new GridLayout(0, 2, 6, 6));
        panelDettagli.add(new JLabel("Nickname:"));
        panelDettagli.add(campoNickname);
        panelDettagli.add(new JLabel("Avatar:"));
        panelDettagli.add(buttonImmagine);

        int conferma = JOptionPane.showConfirmDialog(this, panelDettagli, "Modifica profilo", JOptionPane.OK_CANCEL_OPTION);
        if (conferma == JOptionPane.OK_OPTION) {
            profiloUtente.setNickname(campoNickname.getText().trim());
            if (!campoAvatar.getText().isEmpty()) {
                profiloUtente.setPathAvatar(campoAvatar.getText());
            }
        }
    }
    
    /**
     * Riceve le notifiche da {@link ProfiloUtente} e aggiorna la view di conseguenza.
     *
     * @param o modello che ha notificato il cambiamento
     * @param arg evento notificato
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg == Eventi.PROFILO_AGGIORNATO) {
            SwingUtilities.invokeLater(this::refresh);
        }
    }
}