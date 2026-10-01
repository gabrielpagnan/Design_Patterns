package iterator;

import java.util.List;
import java.util.NoSuchElementException;

class IteradorPlaylist implements Iterador<Faixa> {
    private final List<Faixa> faixas;
    private int posicao;

    IteradorPlaylist(List<Faixa> faixas) {
        this.faixas = List.copyOf(faixas);
    }

    @Override
    public boolean temProxima() {
        return posicao < faixas.size();
    }

    @Override
    public Faixa proxima() {
        if (!temProxima()) {
            throw new NoSuchElementException("Não há mais faixas na travessia.");
        }
        return faixas.get(posicao++);
    }
}
