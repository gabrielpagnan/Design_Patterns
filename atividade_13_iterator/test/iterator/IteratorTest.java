package iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IteratorTest {

    public static void main(String[] args) {
        devePercorrerNaOrdemDeInsercao();
        devePercorrerSomenteFavoritas();
        deveManterCursoresIndependentes();
        deveCriarIteradorVazioQuandoNaoHaFavoritas();
        deveEmbaralharSemAlterarAPlaylist();

        System.out.println("IteratorTest: todos os testes passaram.");
    }

    private static void devePercorrerNaOrdemDeInsercao() {
        assert titulos(criarPlaylist().criarIterador()).equals(List.of("Faixa A", "Faixa B", "Faixa C"));
    }

    private static void devePercorrerSomenteFavoritas() {
        assert titulos(criarPlaylist().criarIteradorFavoritas()).equals(List.of("Faixa A", "Faixa C"));
    }

    private static void deveManterCursoresIndependentes() {
        Playlist playlist = criarPlaylist();
        Iterador<Faixa> primeiro = playlist.criarIterador();
        Iterador<Faixa> segundo = playlist.criarIterador();

        assert primeiro.proxima().getTitulo().equals("Faixa A");
        assert primeiro.proxima().getTitulo().equals("Faixa B");
        assert segundo.proxima().getTitulo().equals("Faixa A");
    }

    private static void deveCriarIteradorVazioQuandoNaoHaFavoritas() {
        Playlist playlist = new Playlist();
        playlist.adicionar(new Faixa("Normal", "Artista", 120, false));

        assert !playlist.criarIteradorFavoritas().temProxima();
    }

    private static void deveEmbaralharSemAlterarAPlaylist() {
        Playlist playlist = criarPlaylist();
        List<String> embaralhada = titulos(playlist.criarIteradorEmbaralhado());

        assert Set.copyOf(embaralhada).equals(Set.of("Faixa A", "Faixa B", "Faixa C"));
        assert embaralhada.size() == 3;
        assert titulos(playlist.criarIterador()).equals(List.of("Faixa A", "Faixa B", "Faixa C"));
    }

    private static Playlist criarPlaylist() {
        Playlist playlist = new Playlist();
        playlist.adicionar(new Faixa("Faixa A", "Artista X", 210, true));
        playlist.adicionar(new Faixa("Faixa B", "Artista Y", 180, false));
        playlist.adicionar(new Faixa("Faixa C", "Artista Z", 240, true));
        return playlist;
    }

    private static List<String> titulos(Iterador<Faixa> iterador) {
        List<String> titulos = new ArrayList<>();
        while (iterador.temProxima()) {
            titulos.add(iterador.proxima().getTitulo());
        }
        return titulos;
    }
}
