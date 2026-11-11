package lavadordepratos;

import java.util.Random;
import java.util.logging.Logger;

public class Enxugador implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Enxugador.class.getName());
    private static final int INTERVALO_CONTAGEM = 10_000_000;

    private final String nome;
    private final Escorredor escorredor;
    private final Random random = new Random();
    private boolean done;

    public Enxugador(String nome, Escorredor escorredor) {
        this.nome = nome;
        this.escorredor = escorredor;
    }

    @Override
    public void run() {
        LOGGER.info(nome + " iniciou");
        try {
            while (!done) {
                // Se o escorredor estiver vazio, espera aqui dentro
                Prato prato = escorredor.retirar();

                if (prato == null) {
                    // Escorredor vazio e lavadores encerrados: nada mais a enxugar
                    done = true;
                } else {
                    LOGGER.fine(nome + " retirou o " + prato + " do escorredor");
                    enxugar();
                    LOGGER.fine(nome + " enxugou o " + prato);
                }
            }
        } catch (InterruptedException e) {
            LOGGER.warning(nome + " foi interrompido");
        }
        LOGGER.info(nome + " encerrou");
    }

    private void enxugar() {
        // Sorteia 3 a 10 intervalos: de 30.000.000 a 100.000.000
        int contagem = (3 + random.nextInt(8)) * INTERVALO_CONTAGEM;
        for (int i = 0; i < contagem; i++) {
            // Sem esta chamada o Java elimina o laco vazio e ele nao gasta tempo
            Thread.onSpinWait();
        }
    }
}
