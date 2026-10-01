package iterator;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Playlist playlist = new Playlist();
        playlist.adicionar(new Faixa("Faixa A", "Artista X", 210, true));
        playlist.adicionar(new Faixa("Faixa B", "Artista Y", 180, false));
        playlist.adicionar(new Faixa("Faixa C", "Artista Z", 240, true));

        Player player = new Player();
        Recomendador recomendador = new Recomendador();
        RelatorioPlaylist relatorio = new RelatorioPlaylist();

        relatorio.resumo(playlist);
        recomendador.sugerirFavoritas(playlist);

        System.out.println("Ordem original: " + listar(playlist.criarIterador()));
        player.tocarEmbaralhado(playlist);
        System.out.println("Ordem depois do shuffle: " + listar(playlist.criarIterador()));
    }

    private static List<Faixa> listar(Iterador<Faixa> iterador) {
        List<Faixa> resultado = new ArrayList<>();
        while (iterador.temProxima()) {
            resultado.add(iterador.proxima());
        }
        return resultado;
    }
}
