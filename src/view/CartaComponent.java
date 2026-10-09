package view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

import model.Carta;

/**
 * Componente Swing che disegna graficamente una singola carta da gioco.
 * Può essere visualizzata fronte (carta scoperta) o retro (carta coperta).
 * Supporta un effetto di evidenziazione tramite {@link #setFocus(boolean)},
 * usato per indicare la carta selezionata dall'utente nella propria mano.
 */
@SuppressWarnings("serial")
public class CartaComponent extends JPanel {
	
	public static final int W = 72;
    public static final int H = 112;
    
    private Carta carta;
    private boolean visibile;
    private boolean focus;
    
    /**
     * Costruisce il renderer per la carta specificata.
     *
     * @param carta carta da rappresentare graficamente
     * @param visibile {@code true} per mostrare il fronte, {@code false} per il retro
     */
    public CartaComponent(Carta carta, boolean visibile) {
        this.carta = carta;
        this.visibile = visibile;
        setPreferredSize(new Dimension(W, H));
        setOpaque(false);
    }
    
    /**
     * Aggiorna la carta associata e la visibilità, ridisegnando il componente.
     *
     * @param carta nuova carta da visualizzare
     * @param visibile {@code true} per il fronte, {@code false} per il retro
     */
    public void setCarta(Carta carta, boolean visibile) {
        this.carta = carta;
        this.visibile = visibile;
        repaint();
    }
    
    /**
     * Attiva o disattiva l'effetto di evidenziazione (bordo giallo) della carta.
     * Usato per il feedback visivo al passaggio del mouse sulle carte dell'utente.
     *
     * @param focus {@code true} per evidenziare la carta
     */
    public void setFocus(boolean focus) {
        this.focus = focus;
        repaint();
    }
    
    /**
     * Disegna la carta: fronte con simbolo, seme e colore, oppure retro a scacchi.
     * Applica anti-aliasing e, se attivo, un bordo giallo di evidenziazione.
     *
     * @param g contesto grafico fornito da Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        RoundRectangle2D rettangolo = new RoundRectangle2D.Float(2, 2, W - 4, H - 4, 10, 10);

        if (focus) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(3));
            g2d.draw(new RoundRectangle2D.Float(0, 0, W, H, 12, 12));
        }

        if (!visibile || carta == null) {       
            // retro
            g2d.setColor(Color.WHITE);
            g2d.fill(rettangolo);
            g2d.setClip(rettangolo);

            int cell = 8;
            for (int row = 0; row * cell < H; row++) {
                for (int col = 0; col * cell < W; col++) {
                    g2d.setColor((row + col) % 2 == 0 ? Color.BLACK : Color.LIGHT_GRAY);
                    g2d.fillRect(col * cell, row * cell, cell, cell);
                }
            }
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(2));
            g2d.draw(rettangolo);
            
        } else {
        	String valore = carta.getSimbolo();
        	String simbolo = carta.getSeme().getSimbolo();
        	
            // fronte
            g2d.setColor(Color.WHITE);
            g2d.fill(rettangolo);
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(rettangolo);
            Color coloreGrafica = carta.getSeme().getColore();
            g2d.setColor(coloreGrafica);

            // angolo alto a sinistra
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2d.drawString(valore, 6, 18);
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2d.drawString(simbolo, 16, 18);
            
            // valore
            g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
            FontMetrics fm = g2d.getFontMetrics();
            int dimensioneValore = (W - fm.stringWidth(valore)) / 2;
            g2d.drawString(valore, dimensioneValore, H / 2 - 4);

            // simbolo
            g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
            fm = g2d.getFontMetrics();
            int dimensioneSimbolo = (W - fm.stringWidth(simbolo)) / 2;
            g2d.drawString(simbolo, dimensioneSimbolo, H / 2 + 20);

            // angolo basso a destra
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2d.drawString(simbolo, W - 26, H - 6);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2d.drawString(valore, W - 16, H - 6);
        }
        g2d.dispose();
    }
}