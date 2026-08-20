package prototype;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Prototype Registry: guarda modelos ja configurados e entrega
 * COPIAS deles sob demanda. O cliente pede pelo nome ("guerreiro")
 * e nunca conhece a classe concreta Inimigo.
 */
public class RegistroDePrototipos {

    private final Map<String, InimigoPrototype> prototipos = new LinkedHashMap<>();

    public RegistroDePrototipos() {
        carregarPadroes();
    }

    /** Monta uma unica vez o "codigo de construcao" caro de cada tipo. */
    private void carregarPadroes() {
        registrar("guerreiro", new Inimigo("Guerreiro", 120, 18, new Arma("Espada Longa", 12)));
        registrar("mago",      new Inimigo("Mago",       70, 30, new Arma("Cajado Arcano", 25)));
        registrar("arqueiro",  new Inimigo("Arqueiro",   85, 22, new Arma("Arco Curto", 15)));
        registrar("chefe",     new Inimigo("Chefe",     500, 60, new Arma("Machado Sombrio", 80)));
    }

    public void registrar(String nome, InimigoPrototype prototipo) {
        prototipos.put(nome.toLowerCase(), prototipo);
    }

    /**
     * Ponto central do padrao: devolve SEMPRE um clone,
     * nunca o prototipo guardado no Map.
     */
    public InimigoPrototype getPrototipo(String nome) {
        InimigoPrototype prototipo = prototipos.get(nome.toLowerCase());
        if (prototipo == null) {
            throw new IllegalArgumentException("Prototipo nao registrado: " + nome);
        }
        return prototipo.clonar();
    }

    /** Acesso ao modelo original, so para a demonstracao provar que ele nao muda. */
    InimigoPrototype espiarPrototipo(String nome) {
        return prototipos.get(nome.toLowerCase());
    }

    public Set<String> nomesDisponiveis() {
        return prototipos.keySet();
    }
}
