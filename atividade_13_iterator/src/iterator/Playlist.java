package iterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Playlist {
    private final List<Faixa> faixas = new ArrayList<>();

    public void adicionar(Faixa faixa) {
        faixas.add(Objects.requireNonNull(faixa, "A faixa é obrigatória."));
    }

    public Iterador<Faixa> criarIterador() {
        return new IteradorPlaylist(faixas);
    }

    public Iterador<Faixa> criarIteradorEmbaralhado() {
        List<Faixa> embaralhadas = new ArrayList<>(faixas);
        Collections.shuffle(embaralhadas);
        return new IteradorPlaylist(embaralhadas);
    }

    public Iterador<Faixa> criarIteradorFavoritas() {
        List<Faixa> favoritas = new ArrayList<>();
        for (Faixa faixa : faixas) {
            if (faixa.isFavorita()) {
                favoritas.add(faixa);
            }
        }
        return new IteradorPlaylist(favoritas);
    }
}
