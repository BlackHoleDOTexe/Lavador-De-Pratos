package lavadordepratos;

import java.util.logging.Logger;

public class Lavador implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Lavador.class.getName());

    private final String nome;
    private final Escorredor escorredor;

    // volatile: escrito pela thread principal e lido pela thread do lavador
    private volatile boolean done;

    public Lavador(String nome, Escorredor escorredor) {
        this.nome = nome;
        this.escorredor = escorredor;
    }

    @Override
    public void run() {
        LOGGER.info(nome + " iniciou");
        try {
            while (!done) {
                Prato prato = new Prato();
                LOGGER.fine(nome + " pegou o " + prato);

                lavar(prato);
                LOGGER.fine(nome + " lavou o " + prato);

                // Se o escorredor estiver cheio, espera aqui dentro
                escorredor.colocar(prato);
                LOGGER.fine(nome + " colocou o " + prato + " no escorredor");
            }
        } catch (InterruptedException e) {
            LOGGER.warning(nome + " foi interrompido");
        }
        LOGGER.info(nome + " encerrou");
    }

    public void parar() {
        done = true;
    }

    private void lavar(Prato prato) {
        int contagem = prato.getNivelSujeira().getContagemLavagem();
        for (int i = 0; i < contagem; i++) {
            // Sem esta chamada o Java elimina o laco vazio e ele nao gasta tempo
            Thread.onSpinWait();
        }
    }
}
