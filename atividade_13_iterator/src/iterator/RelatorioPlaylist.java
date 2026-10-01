package iterator;

public class RelatorioPlaylist {
    public void resumo(Playlist playlist) {
        int totalFaixas = 0;
        int totalSegundos = 0;
        Iterador<Faixa> iterador = playlist.criarIterador();

        while (iterador.temProxima()) {
            Faixa faixa = iterador.proxima();
            totalFaixas++;
            totalSegundos += faixa.getDuracaoSegundos();
        }

        System.out.printf("Total de faixas: %d | duracao: %d min%n",
                totalFaixas, totalSegundos / 60);
    }
}
