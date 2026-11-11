package lavadordepratos;

import java.util.Random;

public class Prato {

    private static final Random RANDOM = new Random();
    private static int proximoNumeroSerie = 1;

    private final int numeroSerie;
    private final NivelSujeira nivelSujeira;

    public Prato() {
        numeroSerie = gerarNumeroSerie();

        // Sorteia de 0 a 99: 30% BAIXO, 60% MEDIO, 10% ENGORDURADO
        int sorteio = RANDOM.nextInt(100);
        if (sorteio < 30) {
            nivelSujeira = NivelSujeira.BAIXO;
        } else if (sorteio < 90) {
            nivelSujeira = NivelSujeira.MEDIO;
        } else {
            nivelSujeira = NivelSujeira.ENGORDURADO;
        }
    }

    // synchronized porque varios lavadores criam pratos ao mesmo tempo
    private static synchronized int gerarNumeroSerie() {
        return proximoNumeroSerie++;
    }

    public int getNumeroSerie() {
        return numeroSerie;
    }

    public NivelSujeira getNivelSujeira() {
        return nivelSujeira;
    }

    @Override
    public String toString() {
        return "Prato " + numeroSerie + " (" + nivelSujeira + ")";
    }
}
