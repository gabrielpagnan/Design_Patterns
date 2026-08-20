import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PRODUTO do padrao Builder.
 *
 * Objeto imutavel: todos os campos sao final, nao existem setters e a lista de
 * vegetais e copiada defensivamente na construcao e exposta como somente leitura.
 * O construtor e privado, entao a unica forma de criar um Lanche e atraves de
 * {@link Lanche.Builder}.
 */
public class Lanche {

    // --- campos obrigatorios ---
    private final String pao;
    private final String proteina;

    // --- campos opcionais ---
    private final String queijo;
    private final List<String> vegetais;
    private final String molho;
    private final boolean bemPassado;
    private final String observacoes;

    /** Construtor privado: apenas o Builder instancia o Lanche. */
    private Lanche(Builder builder) {
        this.pao = builder.pao;
        this.proteina = builder.proteina;
        this.queijo = builder.queijo;
        // copia defensiva + lista imutavel: o Builder pode ser reutilizado sem
        // afetar os lanches ja construidos
        this.vegetais = Collections.unmodifiableList(new ArrayList<>(builder.vegetais));
        this.molho = builder.molho;
        this.bemPassado = builder.bemPassado;
        this.observacoes = builder.observacoes;
    }

    // --- apenas getters, nenhum setter ---

    public String getPao() {
        return pao;
    }

    public String getProteina() {
        return proteina;
    }

    public String getQueijo() {
        return queijo;
    }

    public List<String> getVegetais() {
        return vegetais;
    }

    public String getMolho() {
        return molho;
    }

    public boolean isBemPassado() {
        return bemPassado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    /** Descricao legivel do lanche, omitindo os opcionais nao informados. */
    public String getDescricao() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pao: ").append(pao).append('\n');
        sb.append("  Proteina: ").append(proteina);
        sb.append(bemPassado ? " (bem passada)" : " (ao ponto)").append('\n');

        if (queijo != null) {
            sb.append("  Queijo: ").append(queijo).append('\n');
        }
        if (!vegetais.isEmpty()) {
            sb.append("  Vegetais: ").append(String.join(", ", vegetais)).append('\n');
        }
        if (molho != null) {
            sb.append("  Molho: ").append(molho).append('\n');
        }
        if (observacoes != null) {
            sb.append("  Obs.: ").append(observacoes).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDescricao();
    }

    /**
     * BUILDER: classe interna estatica.
     *
     * E estatica porque nao precisa (e nao pode precisar) de uma instancia de
     * Lanche para existir -- ela e justamente quem cria essa instancia.
     * Acumula o estado mutavel; o produto so nasce no build().
     */
    public static class Builder {

        // obrigatorios (sem default: validados no build)
        private String pao;
        private String proteina;

        // opcionais (com defaults sensatos)
        private String queijo;
        private final List<String> vegetais = new ArrayList<>();
        private String molho;
        private boolean bemPassado = false;
        private String observacoes;

        public Builder setPao(String pao) {
            this.pao = pao;
            return this;
        }

        public Builder setProteina(String proteina) {
            this.proteina = proteina;
            return this;
        }

        public Builder setQueijo(String queijo) {
            this.queijo = queijo;
            return this;
        }

        /** Passo acumulativo: pode ser chamado varias vezes. */
        public Builder addVegetal(String vegetal) {
            if (vegetal != null && !vegetal.isBlank()) {
                this.vegetais.add(vegetal);
            }
            return this;
        }

        public Builder setMolho(String molho) {
            this.molho = molho;
            return this;
        }

        public Builder setBemPassado(boolean bemPassado) {
            this.bemPassado = bemPassado;
            return this;
        }

        public Builder setObservacoes(String observacoes) {
            this.observacoes = observacoes;
            return this;
        }

        /**
         * Valida os campos obrigatorios e entrega o produto pronto.
         *
         * @throws IllegalStateException se pao ou proteina nao foram informados
         */
        public Lanche build() {
            if (pao == null || pao.isBlank()) {
                throw new IllegalStateException(
                        "Nao e possivel montar o lanche: o pao e obrigatorio.");
            }
            if (proteina == null || proteina.isBlank()) {
                throw new IllegalStateException(
                        "Nao e possivel montar o lanche: a proteina e obrigatoria.");
            }
            return new Lanche(this);
        }
    }
}
