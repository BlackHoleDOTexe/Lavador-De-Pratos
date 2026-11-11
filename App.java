package lavadordepratos;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    // Level.FINE mostra cada passo dos trabalhadores; Level.INFO e o padrao
    private static final Level NIVEL_LOG = Level.INFO;

    private static final int TEMPO_DE_TRABALHO_MS = 60_000;
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    private final Escorredor escorredor;
    private final Lavador[] lavadores;
    private final Thread[] threadsLavadores;
    private final Thread[] threadsEnxugadores;

    public App(int quantidadeLavadores, int quantidadeEnxugadores, int capacidade) {
        escorredor = new Escorredor(capacidade);
        lavadores = new Lavador[quantidadeLavadores];
        threadsLavadores = new Thread[quantidadeLavadores];
        threadsEnxugadores = new Thread[quantidadeEnxugadores];

        for (int i = 0; i < quantidadeLavadores; i++) {
            lavadores[i] = new Lavador("Lavador " + (i + 1), escorredor);
            threadsLavadores[i] = new Thread(lavadores[i]);
        }
        for (int i = 0; i < quantidadeEnxugadores; i++) {
            Enxugador enxugador = new Enxugador("Enxugador " + (i + 1), escorredor);
            threadsEnxugadores[i] = new Thread(enxugador);
        }
    }

    public void iniciar() {
        LOGGER.info("Iniciando os trabalhos");
        for (Thread thread : threadsLavadores) {
            thread.start();
        }
        for (Thread thread : threadsEnxugadores) {
            thread.start();
        }
    }

    public void parar() throws InterruptedException {
        LOGGER.info("Encerrando os trabalhos");

        // Primeiro param os lavadores, enquanto os enxugadores ainda liberam espaco
        for (Lavador lavador : lavadores) {
            lavador.parar();
        }
        for (Thread thread : threadsLavadores) {
            thread.join();
        }

        // Depois os enxugadores esvaziam o escorredor e encerram
        escorredor.encerrar();
        for (Thread thread : threadsEnxugadores) {
            thread.join();
        }

        LOGGER.info("Trabalhos encerrados com " + escorredor.getQuantidade() + " pratos no escorredor");
    }

    private static void configurarLog() {
        // Mostra cada mensagem em uma linha: hora, nivel e texto
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tT %4$s: %5$s%n");

        Logger raiz = Logger.getLogger("");
        raiz.setLevel(NIVEL_LOG);
        for (Handler handler : raiz.getHandlers()) {
            handler.setLevel(NIVEL_LOG);
        }
    }

    // Uso: java lavadordepratos.App [lavadores enxugadores capacidade]
    public static void main(String[] args) throws InterruptedException {
        configurarLog();

        int quantidadeLavadores = 2;
        int quantidadeEnxugadores = 3;
        int capacidade = 5;
        if (args.length == 3) {
            quantidadeLavadores = Integer.parseInt(args[0]);
            quantidadeEnxugadores = Integer.parseInt(args[1]);
            capacidade = Integer.parseInt(args[2]);
        }

        App app = new App(quantidadeLavadores, quantidadeEnxugadores, capacidade);
        app.iniciar();
        Thread.sleep(TEMPO_DE_TRABALHO_MS);
        app.parar();
    }
}
