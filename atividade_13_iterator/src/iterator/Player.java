package iterator;

public class Player {
    public void tocarTudo(Playlist playlist) {
        tocar(playlist.criarIterador(), "Tocando: ");
    }

    public void tocarEmbaralhado(Playlist playlist) {
        tocar(playlist.criarIteradorEmbaralhado(), "Tocando (shuffle): ");
    }

    private void tocar(Iterador<Faixa> iterador, String prefixo) {
        while (iterador.temProxima()) {
            System.out.println(prefixo + iterador.proxima());
        }
    }
}
