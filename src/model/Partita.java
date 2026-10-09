package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Modello principale della partita di Briscola, implementata come Singleton. 
 * Estende {@link Observable} per notificare la view riguardo gli aggiornamenti del {@code GamePanel}.
 * <p>
 * Gestisce l'intero ciclo di vita di una partita: distribuzione delle carte, avanzamento
 * dei turni, determinazione del vincitore di ogni presa e calcolo del risultato finale.
 * Supporta due modalità tramite {@link ModalitaPartita}: individuale (1 vs 1) e a squadre (2 vs 2).
 * </p>
 */
@SuppressWarnings("deprecation")
public class Partita extends Observable implements Evolvibile {
	
	private static Partita instance;
	 
	private ModalitaPartita modalitaPartita;      // modalità di gioco selezionata
    private Mazzo mazzo;                          // mazzo di carte 
    private Carta cartaBriscola;                  // carta pescata come briscola a inizio partita
    private Seme semeBriscola;                    // seme carta pescata come briscola a inizio partita
    private Giocatore giocatoreUtente;            // giocatore reale
    private List<Giocatore> giocatori;            // lista giocatori
    private int giocatoreIniziale;                // giocatore primo di mano nel turno attuale
    private int giocatoreAttuale;                 // giocatore di mano nel turno corrente
    private List<Giocatore> giocatoriTurno;       // giocatori che hanno giocato nel turno attuale
    private List<Carta> carteTurno;               // carte giocate nel turno attuale
    private Giocatore giocatoreVincenteTurno;     // giocatore che ha vinto il turno attuale
    private Carta cartaVincenteTurno;             // carta che ha vinto il turno attuale
    private boolean turnoInAttesa = false;
    private boolean gameOver = false;

    private final Semaphore attesaPartita = new Semaphore(0);
    private final Semaphore attesaMossa = new Semaphore(0);
    
    /** 
     * Costruttore privato: l'istanziazione avviene solo tramite {@link #getInstance()}.
     */
    private Partita() {}

    /**
     * Restituisce l'istanza Singleton della partita.
     *
     * @return unica istanza di {@code Partita}
     */
    public static Partita getInstance() {
        if (instance == null) {
            instance = new Partita();
        }
        return instance;
    }
    
    /**
     * Restituisce la modalità di gioco della partita attuale.
     *
     * @return modalità della partita
     */
    public ModalitaPartita getModalitaPartita() { 
    	return modalitaPartita; 
    }
    
    /**
     * Restituisce il numero di carte ancora presenti nel mazzo.
     *
     * @return dimensione del mazzo
     */
    public int getDimensioniMazzo() { 
    	return mazzo.size(); 
    }
    
    /**
     * Restituisce la carta che fungerà da briscola (quella in fondo al mazzo, scoperta a inizio partita).
     *
     * @return carta briscola
     */
    public Carta getCartaBriscola() { 
    	return cartaBriscola; 
    }
    
    /**
     * Restituisce il seme di briscola della partita attuale.
     *
     * @return seme briscola
     */
    public Seme getSemeBriscola() { 
    	return semeBriscola; 
    }
    
    /**
     * Restituisce il giocatore utente.
     *
     * @return istanza del giocatore utente
     */
    public Giocatore getGiocatoreUtente() { 
    	return giocatoreUtente; 
    }
    
    /**
     * Restituisce la lista di tutti i giocatori (il primo è l'utente, poi i CPU).
     *
     * @return lista dei giocatori
     */
    public List<Giocatore> getGiocatori() { 
    	return giocatori; 
    }
    
    /**
     * Restituisce il giocatore che deve giocare in questo momento.
     *
     * @return giocatore attuale
     */
    public Giocatore getGiocatoreAttuale() { 
    	return giocatori.get(giocatoreAttuale); 
    }
    
    /**
     * Restituisce la lista dei giocatori che hanno già giocato durante il turno attuale.
     *
     * @return lista dei giocatori che hanno già giocato
     */
    public List<Giocatore> getGiocatoriTurno() { 
    	return giocatoriTurno; 
    }
    
    /**
     * Restituisce le carte giocate fin ora nel turno attuale.
     *
     * @return carte sul tavolo
     */
    public List<Carta> getCarteTurno() { 
    	return carteTurno; 
    }
    
    /**
     * Restituisce il giocatore che ha vinto l'ultimo turno.
     *
     * @return giocatore vincente del turno
     */
    public Giocatore getGiocatoreVincenteTurno() { 
    	return giocatoreVincenteTurno; 
    }
    
    /**
     * Restituisce la carta con cui è stata vinta l'ultima presa.
     *
     * @return carta vincente del turno
     */
    public Carta getCartaVincenteTurno() { 
    	return cartaVincenteTurno; 
    }
    
    /**
     * Indica se è il turno del giocatore utente.
     *
     * @return {@code true} se è il turno del giocatore utente 
     */
    @Override
    public boolean isTurnoUtente() { 
    	return getGiocatoreAttuale().isUtente(); 
    }
    
    /**
     * Indica se la partita è in attesa che si confermi la conclusione del turno.
     *
     * @return {@code true} se il vincitore del turno è stato determinato ma il turno non è ancora concluso
     */
    @Override
    public boolean isTurnoInAttesa() { 
    	return turnoInAttesa; 
    }
    
    /**
     * Indica se la partita è terminata.
     *
     * @return {@code true} se la partita è terminata
     */
    @Override
    public boolean isGameOver() { 
    	return gameOver; 
    }
    
    /**
     * Calcola il numero totale delle carte prese dall'utente o dalla sua squadra (in modalità a squadre).
     *
     * @return numero carte prese
     */
    public int getNumeroCartePreseUtente() {
        if (this.modalitaPartita == ModalitaPartita.SQUADRE) {
            return giocatori.stream()
                    .filter(g -> g.getSquadra() == giocatoreUtente.getSquadra())
                    .mapToInt(g -> g.getCartePrese().size())
                    .sum();
        }
        return giocatoreUtente.getCartePrese().size();
    }
    
    /**
     * Calcola il punteggio totale della squadra del giocatore utente (in modalità a squadre).
     *
     * @return punti totalizzati dalla squadra dell'utente
     */
    public int getPuntiSquadraUtente() {
    	return giocatori.stream()
    			.filter(g -> g.getSquadra() == giocatoreUtente.getSquadra())
    			.mapToInt(Giocatore::getPuntiTotali).sum();
    }
    
    /**
     * Calcola il punteggio totale della squadra avversaria (in modalità a squadre).
     *
     * @return punti totalizzati dalla squadra avversaria
     */
    public int getPuntiSquadraAvversaria() {
    	return giocatori.stream()
    			.filter(g -> g.getSquadra() != giocatoreUtente.getSquadra())
    			.mapToInt(Giocatore::getPuntiTotali).sum();
    }
    
    /**
     * Blocca il thread chiamante finché l'utente non avvia una nuova partita
     * tramite {@link Partita#iniziaNuovaPartita}.
     * 
     * @throws InterruptedException se il thread viene interrotto durante l'attesa
     */
    @Override
    public void aspettaInizioPartita() throws InterruptedException {
    	attesaPartita.acquire();
    }
    
    /**
     * Inizializza e avvia una nuova partita.
     * Crea i quattro giocatori, distribuisce tre carte ciascuno, scopre la briscola
     * e se in modalità {@link ModalitaPartita#SQUADRE} assegna le squadre.
     * Il primo di mano è estratto casualmente.
     * Al termine notifica {@link Eventi#PARTITA_INIZIATA}.
     *
     * @param nomeUtente nome del giocatore utente
     * @param modalitaPartita modalità di gioco scelta
     */
    public void iniziaNuovaPartita(String nomeUtente, ModalitaPartita modalitaPartita) {
        this.modalitaPartita = modalitaPartita;
        mazzo = new Mazzo();
        giocatoriTurno = new ArrayList<>();
        carteTurno = new ArrayList<>();
        gameOver = false;

        giocatori = new ArrayList<>();
        giocatoreUtente = new Giocatore(nomeUtente, true);
        giocatori.add(giocatoreUtente);
        giocatori.add(new Giocatore("CPU 1", false));
        
        int numeroGiocatori;
        if (modalitaPartita == ModalitaPartita.SQUADRE) {
        	giocatori.add(new Giocatore("CPU 2", false));
            giocatori.add(new Giocatore("CPU 3", false));
            giocatori.forEach(Giocatore::reset);
            numeroGiocatori = 4;
            
        	giocatoreUtente.setSquadra(0);      // utente: squadra 1
        	giocatori.get(1).setSquadra(1);     // CPU 1: squadra 2
        	giocatori.get(2).setSquadra(0);     // CPU 2: squadra 1 (alleato)
        	giocatori.get(3).setSquadra(1);     // CPU 3: squadra 2
        }
        else {
        	giocatori.forEach(Giocatore::reset);
        	numeroGiocatori = 2;
        }
        
        // si distribuiscono 3 carte ad ogni giocatore
        for (int i = 0; i < 3; i++) {
        	giocatori.forEach(p -> p.pescaCarta(mazzo.pescaCarta()));
        }
        
        // si pesca la briscola
        cartaBriscola = mazzo.pescaBriscola();
        semeBriscola = cartaBriscola.getSeme();
        
        Random rand = new Random();
        int primo = rand.nextInt(numeroGiocatori);
        giocatoreIniziale = primo;
        giocatoreAttuale = primo;

        // segnala al game loop che una nuova partita è iniziata
        attesaMossa.release();
        attesaPartita.release();

        notifyObservers(Eventi.PARTITA_INIZIATA);
    }
    
    /**
     * Blocca il thread chiamante finché l'utente non gioca una carta,
     * il turno non passa a un giocatore CPU, il turno non va in attesa
     * di conferma o la partita non termina.
     *
     * @throws InterruptedException se il thread viene interrotto durante l'attesa
     */
    @Override
    public void aspettaMossaUtente() throws InterruptedException {
    	attesaMossa.acquire();
    }
    
    /**
     * Avanza la partita eseguendo la mossa del giocatore CPU attuale.
     * Non ha effetto se è il turno dell'utente, il turno è in attesa 
     * di conferma o la partita è terminata.
     */
    @Override
    public void evolvi() {
        if (isTurnoUtente() || turnoInAttesa || gameOver) {
            return;
        }
        mossaCPU();
    }
    
    /**
     * Fa scegliere e giocare una carta al giocatore CPU attuale tramite {@link #sceltaCartaCPU(Giocatore)}.
     * Non ha effetto se il giocatore attuale è l'utente.
     */
    public void mossaCPU() {
        Giocatore giocatoreCPU = getGiocatoreAttuale();
        if (giocatoreCPU.isUtente()) {
        	return;
        }
        Carta cartaCPU = sceltaCartaCPU(giocatoreCPU);
        giocaCarta(giocatoreCPU, cartaCPU);
    }
    
    /**
     * Implementa la strategia di gioco della CPU.
     * <ul>
     *   <li>Primo di mano: preferisce carte senza punti e non di briscola.</li>
     *   <li>Ultimo di mano in modalità a squadre con compagno che sta vincendo: butta la carta 
     *       con più punti per massimizzare il bottino della squadra (escluse briscole).</li>
     *   <li>Ultimo di mano: si cerca di prendere con la carta vincente che vale più punti.</li>
     *   <li>Posizione intermedia (in modalità a squadre): si cerca di prendere ma senza buttare Assi o 3.</li>
     *   <li>Altrimenti: stessa procedura conservativa come da primo di mano.</li>
     * </ul>
     *
     * @param giocatoreCPU giocatore CPU per cui scegliere la carta
     * @return carta scelta dalla CPU
     */
    private Carta sceltaCartaCPU(Giocatore giocatoreCPU) {
        List<Carta> mano = giocatoreCPU.getMano();
        // primo di mano: buttare carte che valgono meno punti possibile, escluse briscole, Assi e 3, a meno che non si hanno alternative
        if (carteTurno.isEmpty()) {
            return mano.stream()
            		.filter(c -> c.getPunto() <= 4 && c.getSeme() != semeBriscola)
                    .min(Comparator.comparingInt(Carta::getForza))
                    .orElse(mano.stream()
                    		.filter(c -> c.getSeme() == semeBriscola)
                    		.min(Comparator.comparingInt(Carta::getForza))
                    		.orElse(mano.stream()
                    				.min(Comparator.comparingInt(Carta::getForza))
                    				.orElse(mano.get(0))));
        }
        // squadre: se ultimo di mano e il compagno sta vincendo, buttare la carta che vale più punti escluse briscole
        if (isCompagnoVincente(giocatoreCPU)) {
        	Optional<Carta> cartaMigliore = mano.stream()
        			.filter(c -> c.getSeme() != semeBriscola)
        			.max(Comparator.comparingInt(Carta::getPunto));
        	if (cartaMigliore.isPresent()) {
        		return cartaMigliore.get();
        	}
        }
        
        Carta cartaIniziale = carteTurno.get(0);
        Carta cartaDominante = carteTurno.stream()
        		.filter(c -> c.getSeme() == semeBriscola)
        		.max(Comparator.comparingInt(Carta::getForza))
        		.orElse(carteTurno.stream()
        				.filter(c -> c.getSeme() == cartaIniziale.getSeme())
        				.max(Comparator.comparingInt(Carta::getForza))
        				.orElse(carteTurno.get(0)));
        
        int puntiTurno = carteTurno.stream().mapToInt(Carta::getPunto).sum();
        // se ultimo di mano
        if ((modalitaPartita == ModalitaPartita.SINGOLO && carteTurno.size() == 1) || (modalitaPartita == ModalitaPartita.SQUADRE && carteTurno.size() == 3)) {
        	// tanti punti a terra, buttare carta vincente più alta possibile
        	if (puntiTurno >= 10) {
        		Optional<Carta> cartaVincente = mano.stream()
            			.filter(c -> cartaBatteCarta(c, cartaDominante) && c.getSeme() != semeBriscola)
            			.max(Comparator.comparingInt(Carta::getPunto));
            	if (cartaVincente.isPresent()) {
                	return cartaVincente.get();
                }
            	else {
            		cartaVincente = mano.stream()
            				.filter(c -> cartaBatteCarta(c, cartaDominante))
            				.max(Comparator.comparingInt(Carta::getPunto));
            		if (cartaVincente.isPresent()) {
                    	return cartaVincente.get();
                    }
            	}
        	}
        	else {
        		// se si gioca a squadre, con pochi punti a terra, buttare carta vincente più alta possibile escluse briscole
        		if (modalitaPartita == ModalitaPartita.SQUADRE && carteTurno.size() == 3) {
        			Optional<Carta> cartaVincente = mano.stream()
                			.filter(c -> cartaBatteCarta(c, cartaDominante) && c.getSeme() != semeBriscola)
                			.max(Comparator.comparingInt(Carta::getPunto));
                	if (cartaVincente.isPresent()) {
                    	return cartaVincente.get();
                    }
        		}
        		// se si gioca in singolo, con pochi punti a terra, buttare carta vincente più alta possibile escluse briscole e carte da zero punti
        		else {      			
        			Optional<Carta> cartaVincente = mano.stream()
                			.filter(c -> cartaBatteCarta(c, cartaDominante) && c.getSeme() != semeBriscola && c.getPunto() != 0)
                			.max(Comparator.comparingInt(Carta::getPunto));
                	if (cartaVincente.isPresent()) {
                    	return cartaVincente.get();
                    }
        		} 		
        	}
        }
        // da posizione intermedia, buttare carta vincente più bassa possibile esclusi Assi e 3
        if (modalitaPartita == ModalitaPartita.SQUADRE && carteTurno.size() >= 1) {
            Optional<Carta> cartaVincente = mano.stream()
            		.filter(c -> cartaBatteCarta(c, cartaDominante) && c.getPunto() <= 4)
            		.min(Comparator.comparingInt(Carta::getForza));
            if (cartaVincente.isPresent()) {
            	return cartaVincente.get();
            }
        }
        // senza carte vincenti stesso iter del caso da primo di mano
        return mano.stream()
        				.filter(c -> c.getPunto() <= 4 && c.getSeme() != semeBriscola)
        				.min(Comparator.comparingInt(Carta::getForza))
        				.orElse(mano.stream()
                				.filter(c -> c.getSeme() == semeBriscola)
                				.min(Comparator.comparingInt(Carta::getForza))
                				.orElse(mano.stream()
                						.min(Comparator.comparingInt(Carta::getForza))
                						.orElse(mano.get(0))));
    }
    
    /**
     * Verifica se il compagno di squadra della CPU sta momentaneamente vincendo il turno.
     * Attivo solo in modalità {@link ModalitaPartita#SQUADRE} e solo quando la CPU
     * è l'ultima a giocare nel turno (3 carte già sul tavolo).
     *
     * @param giocatoreCPU giocatore CPU da valutare
     * @return {@code true} se il compagno di squadra sta vincendo il turno attuale
     */
    private boolean isCompagnoVincente(Giocatore giocatoreCPU) {
    	if (modalitaPartita != ModalitaPartita.SQUADRE) {
    		return false;
    	}
    	if (carteTurno.size() != 3) {
    		return false;
    	}
    	Carta cartaVincente = cartaAttualmenteVincente();
    	int indiceCartaVincente = carteTurno.indexOf(cartaVincente);
    	return giocatoriTurno.get(indiceCartaVincente).getSquadra() == giocatoreCPU.getSquadra();
    }
    
    /**
     * Scansiona le carte sul tavolo e restituisce quella attualmente vincente,
     * applicando il criterio di {@link #cartaBatteCarta(Carta, Carta)}.
     *
     * @return carta che sta vincendo la presa in questo momento
     */
    private Carta cartaAttualmenteVincente() {
    	Carta cartaDominante = carteTurno.get(0);
    	for (int i = 1; i < carteTurno.size(); i++) {
    		if (cartaBatteCarta(carteTurno.get(i), cartaDominante)) {
    			cartaDominante = carteTurno.get(i);
    		}
    	}
    	return cartaDominante;
    }
    
    /**
     * Determina se la carta {@code cartaSfidante} batte la carta {@code cartaDominante} secondo le regole
     * di Briscola: le briscole battono tutto, a parità di seme vince la forza maggiore,
     * le carte con seme diverso da quello della carta iniziale non vincono mai.
     *
     * @param cartaSfidante carta che tenta di battere la carta dominante
     * @param cartaDominante carta attualmente dominante nella presa
     * @return {@code true} se {@code cartaSfidante} batte {@code cartaDominante}
     */
    private boolean cartaBatteCarta(Carta cartaSfidante, Carta cartaDominante) {
    	boolean dominanteBriscola = cartaDominante.getSeme() == semeBriscola;
        boolean sfidanteBriscola = cartaSfidante.getSeme() == semeBriscola;
        boolean sfidanteDominante = cartaSfidante.getSeme() == cartaDominante.getSeme();
        
        if (sfidanteBriscola && !dominanteBriscola) {
        	return true;
        }
        if (!sfidanteBriscola && dominanteBriscola) {
        	return false;
        }
        if (!sfidanteDominante) {
        	return false;
        }
        return cartaSfidante.getForza() > cartaDominante.getForza();
    }
    
    /**
     * Registra la carta giocata dal giocatore {@code giocatore} sul tavolo.
     * Se tutti i giocatori hanno giocato invoca {@link #determinaVincitore()},
     * altrimenti avanza il turno con {@link #avanzaTurno()}.
     *
     * @param giocatore giocatore che gioca la carta
     * @param carta carta giocata
     */
    public void giocaCarta(Giocatore giocatore, Carta carta) {
    	giocatore.buttaCarta(carta);
    	carteTurno.add(carta);
    	giocatoriTurno.add(giocatore);

        if (carteTurno.size() == giocatori.size()) {
            determinaVincitore();
        } else {
            avanzaTurno();
        }
        // segnala al game loop che una carta è stata giocata
        attesaMossa.release();
    }
    
    /**
     * Avanza l'indice del giocatore attuale al successivo in senso circolare
     * e notifica {@link Eventi#CARTA_GIOCATA} per aggiornare la view.
     */
    public void avanzaTurno() {
    	giocatoreAttuale = (giocatoreAttuale + 1) % giocatori.size();
        notifyObservers(Eventi.CARTA_GIOCATA);
    }
    
    /**
     * Esamina le carte sul tavolo e individua il vincitore del turno attuale
     * applicando il metodo {@link #cartaBatteCarta(Carta, Carta)}. 
     * Imposta {@code turnoInAttesa = true} e notifica {@link Eventi#VINCITORE_DETERMINATO}.
     */
    private void determinaVincitore() {
    	Giocatore giocatoreDominante = giocatoriTurno.get(0);
        Carta cartaDominante = carteTurno.get(0);

        for (int i = 1; i < carteTurno.size(); i++) {
        	Giocatore giocatoreSfidante = giocatoriTurno.get(i);
            Carta cartaSfidante = carteTurno.get(i);      
            if (cartaBatteCarta(cartaSfidante, cartaDominante)) {
            	giocatoreDominante = giocatoreSfidante;
            	cartaDominante = cartaSfidante;
            }
        }
    	
        giocatoreVincenteTurno = giocatoreDominante;
        cartaVincenteTurno = cartaDominante;
        turnoInAttesa = true;
        
        notifyObservers(Eventi.VINCITORE_DETERMINATO);
    }
    
    /**
     * Chiude il turno attuale: assegna le carte al vincitore, fa pescare dal mazzo
     * e aggiorna il primo di mano per il turno successivo.
     * Se il mazzo e le mani sono esauriti invoca {@link #finePartita()},
     * altrimenti notifica {@link Eventi#TURNO_CONCLUSO}.
     */
    @Override
    public void concludiTurno() {
        if (!turnoInAttesa) {
        	return;
        }
        turnoInAttesa = false;
 
        carteTurno.forEach(giocatoreVincenteTurno::aggiungiCartePrese);
        carteTurno.clear();
        giocatoriTurno.clear();
        
        // pesca dal mazzo
        List<Giocatore> giocatoriOrdinati = stabilisciOrdinePesca(giocatori.indexOf(giocatoreVincenteTurno));
        giocatoriOrdinati.forEach(g -> {
            if (!mazzo.isEmpty()) {
            	g.pescaCarta(mazzo.pescaCarta());
            }
        });
        giocatoreIniziale = giocatori.indexOf(giocatoreVincenteTurno);
        giocatoreAttuale = giocatoreIniziale;
 
        if (giocatori.stream().allMatch(g -> g.getMano().isEmpty())) {
            finePartita();
        } else {
            notifyObservers(Eventi.TURNO_CONCLUSO);
        }
    }
    
    /**
     * Costruisce la lista dei giocatori nell'ordine in cui devono pescare,
     * partendo dal vincitore e proseguendo in senso circolare.
     *
     * @param posizioneVincitoreTurno indice nella lista {@code giocatori} del vincitore
     * @return lista ordinata per la pesca
     */
    private List<Giocatore> stabilisciOrdinePesca(int posizioneVincitoreTurno) {
        List<Giocatore> giocatoriOrdinati = new ArrayList<>();
        int numeroGiocatori = giocatori.size();
        for (int i = 0; i < numeroGiocatori; i++) {
        	giocatoriOrdinati.add(giocatori.get((posizioneVincitoreTurno + i) % numeroGiocatori));
        }
        return giocatoriOrdinati;
    }
    
    /**
     * Calcola il risultato finale, aggiorna il {@link ProfiloUtente} e notifica {@link Eventi#GAME_OVER}.
     * Vince la squadra o il giocatore che totalizza più punti, 60 pari è pareggio.
     */
    private void finePartita() {
        gameOver = true;
        if (modalitaPartita == ModalitaPartita.SQUADRE) {
        	int puntiAlleati = getPuntiSquadraUtente();
        	int puntiAvversari = getPuntiSquadraAvversaria();
        	if (puntiAlleati >= 61) {
        		ProfiloUtente.getInstance().aggiornaVittorie();
        	} else if (puntiAvversari >= 61) {
        		ProfiloUtente.getInstance().aggiornaSconfitte();
        	} else {
        		ProfiloUtente.getInstance().aggiornaPareggiate();
        	}
        } else {
        	int puntiUtente = giocatoreUtente.getPuntiTotali();
        	int puntiCPU = giocatori.get(1).getPuntiTotali();
        	if (puntiUtente > puntiCPU) {
        		ProfiloUtente.getInstance().aggiornaVittorie();
        	} else if (puntiCPU > puntiUtente) {
        		ProfiloUtente.getInstance().aggiornaSconfitte();
        	} else {
        		ProfiloUtente.getInstance().aggiornaPareggiate();
        	}
        }
        notifyObservers(Eventi.GAME_OVER);
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