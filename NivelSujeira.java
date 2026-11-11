package lavadordepratos;

public enum NivelSujeira {

    BAIXO(30_000_000),
    MEDIO(50_000_000),
    ENGORDURADO(100_000_000);

    // Ate quanto o lavador conta para lavar um prato deste nivel
    private final int contagemLavagem;

    NivelSujeira(int contagemLavagem) {
        this.contagemLavagem = contagemLavagem;
    }

    public int getContagemLavagem() {
        return contagemLavagem;
    }
}
