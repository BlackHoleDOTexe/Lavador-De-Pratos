package lavadordepratos;

import java.util.logging.Logger;

public class Escorredor {

    private static final Logger LOGGER = Logger.getLogger(Escorredor.class.getName());

    // Fila circular
    private final Prato[] pratos;
    private int inicio;      // posicao do prato mais antigo
    private int fim;         // proxima posicao livre
    private int quantidade;

    // Lavadores e enxugadores esperam em objetos separados. Assim o notify()
    // sempre acorda uma thread do tipo certo, sem precisar de notifyAll().
    private final Object esperaLavadores = new Object();
    private final Object esperaEnxugadores = new Object();

    private volatile boolean encerrado;

    public Escorredor(int capacidade) {
        pratos = new Prato[capacidade];
    }

    public void colocar(Prato prato) throws InterruptedException {
        boolean notificar;

        synchronized (esperaLavadores) {
            while (estaCheio()) {
                esperaLavadores.wait();
            }
            notificar = inserir(prato);
        }

        // Acorda um enxugador somente se estava vazio ou ficou cheio
        if (notificar) {
            synchronized (esperaEnxugadores) {
                esperaEnxugadores.notify();
            }
        }
    }

    // Retorna null quando o escorredor esta vazio e os trabalhos encerraram
    public Prato retirar() throws InterruptedException {
        Prato prato;
        boolean notificar;

        synchronized (esperaEnxugadores) {
            while (estaVazio()) {
                if (encerrado) {
                    // Acorda o proximo enxugador para ele tambem encerrar
                    esperaEnxugadores.notify();
                    return null;
                }
                esperaEnxugadores.wait();
            }
            boolean estavaCheio = estaCheio();
            prato = remover();
            notificar = estavaCheio || estaVazio();
        }

        // Acorda um lavador somente se estava cheio ou ficou vazio
        if (notificar) {
            synchronized (esperaLavadores) {
                esperaLavadores.notify();
            }
        }
        return prato;
    }

    // Chamado depois que todos os lavadores terminaram
    public void encerrar() {
        encerrado = true;
        synchronized (esperaEnxugadores) {
            esperaEnxugadores.notify();
        }
    }

    public synchronized boolean estaCheio() {
        return quantidade == pratos.length;
    }

    public synchronized boolean estaVazio() {
        return quantidade == 0;
    }

    public synchronized int getQuantidade() {
        return quantidade;
    }

    // Retorna true se o escorredor estava vazio ou ficou cheio
    private synchronized boolean inserir(Prato prato) {
        boolean estavaVazio = quantidade == 0;

        pratos[fim] = prato;
        fim = (fim + 1) % pratos.length;
        quantidade++;

        if (quantidade == pratos.length) {
            LOGGER.info("Escorredor CHEIO: " + quantidade + " pratos");
        }
        return estavaVazio || quantidade == pratos.length;
    }

    private synchronized Prato remover() {
        Prato prato = pratos[inicio];
        pratos[inicio] = null;
        inicio = (inicio + 1) % pratos.length;
        quantidade--;

        if (quantidade == 0) {
            LOGGER.info("Escorredor VAZIO: " + quantidade + " pratos");
        }
        return prato;
    }
}
