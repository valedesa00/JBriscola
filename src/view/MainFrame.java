package view;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * Finestra principale dell'applicazione JBriscola, implementata come Singleton.
 * Compone il {@link MenuPanel} a sinistra e il {@link GamePanel} al centro.
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

    private static MainFrame instance;

    private final MenuPanel menuPanel;
    private final GamePanel gamePanel;
    
    /** 
     * Costruttore privato: l'istanziazione avviene solo tramite {@link #getInstance()}. 
     * Richiama il builder con i settaggi principali e poi instanzia i sotto-pannelli.
     */
    private MainFrame() {
        buildUI();
        menuPanel = new MenuPanel();
        gamePanel = new GamePanel();
        add(menuPanel, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);
        setVisible(true);
    }
    
    /**
     * Restituisce l'istanza singleton del main frame.
     * Deve essere chiamato sull'EDT.
     * 
     * @return unica istanza {@code MainFrame}
     */
    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
        }
        return instance;
    }
    
    /** 
     * @return pannello del menu di gioco 
     */
    public MenuPanel getMenuPanel() { 
    	return menuPanel; 
    }
    
    /** 
     * @return pannello del campo di gioco 
     */
    public GamePanel getGamePanel() { 
    	return gamePanel; 
    }
    
    /**
     * Costruisce il layout della finestra: menu a sinistra con area utente 
     * e bottoni di avvio partita, campo di gioco al centro.
     */
    private void buildUI() {
        setTitle("JBriscola");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        getContentPane().setPreferredSize(new Dimension(1200, 600));
        pack();
        setLocationRelativeTo(null);
    }
}