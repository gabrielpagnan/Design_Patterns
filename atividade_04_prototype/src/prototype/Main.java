package prototype;

/**
 * Cliente / classe de teste do padrao Prototype.
 */
public class Main {

    public static void main(String[] args) {
        RegistroDePrototipos registro = new RegistroDePrototipos();

        titulo("1. CLIENTE PEDE COPIAS PELO NOME (sem dar 'new Inimigo(...)')");
        System.out.println("Modelos registrados: " + registro.nomesDisponiveis());
        for (String nome : registro.nomesDisponiveis()) {
            System.out.println("  " + registro.getPrototipo(nome));
        }

        titulo("2. VARIACAO SOBRE A BASE: um GUERREIRO ELITE");
        // O cast so existe porque InimigoPrototype declara apenas clonar().
        // Num sistema real os setters de dominio estariam no proprio contrato.
        Inimigo elite = (Inimigo) registro.getPrototipo("guerreiro");
        elite.setTipo("Guerreiro Elite");
        elite.setVida(300);
        elite.setDano(45);
        elite.getArma().setNome("Espada Rúnica");
        elite.getArma().setBonusDano(40);

        System.out.println("Elite (ajustado) : " + elite);
        System.out.println("Prototipo original: " + registro.espiarPrototipo("guerreiro"));
        System.out.println("  -> o modelo do registro continua 120/18 com Espada Longa: INTACTO.");

        titulo("3. OS CLONES SAO OBJETOS DISTINTOS");
        Inimigo mago1 = (Inimigo) registro.getPrototipo("mago");
        Inimigo mago2 = (Inimigo) registro.getPrototipo("mago");
        Inimigo prototipoMago = (Inimigo) registro.espiarPrototipo("mago");

        System.out.println("mago1          -> " + mago1);
        System.out.println("mago2          -> " + mago2);
        System.out.println("prototipo mago -> " + prototipoMago);
        System.out.println("  mago1 == mago2          ? " + (mago1 == mago2));
        System.out.println("  mago1 == prototipo      ? " + (mago1 == prototipoMago));
        System.out.println("  identityHashCode iguais ? "
                + (System.identityHashCode(mago1) == System.identityHashCode(mago2)));

        titulo("4. COPIA PROFUNDA: cada clone tem a SUA Arma");
        System.out.println("Antes:");
        System.out.println("  mago1.arma     -> " + mago1.getArma());
        System.out.println("  mago2.arma     -> " + mago2.getArma());
        System.out.println("  prototipo.arma -> " + prototipoMago.getArma());
        System.out.println("  mago1.arma == mago2.arma ? " + (mago1.getArma() == mago2.getArma()));

        mago1.getArma().setNome("Cajado AMALDIÇOADO");
        mago1.getArma().setBonusDano(999);

        System.out.println("Depois de mexer SO na arma do mago1:");
        System.out.println("  mago1.arma     -> " + mago1.getArma() + "  <-- alterado");
        System.out.println("  mago2.arma     -> " + mago2.getArma() + "  <-- intacto");
        System.out.println("  prototipo.arma -> " + prototipoMago.getArma() + "  <-- intacto");

        titulo("5. BONUS: como seria com COPIA RASA (mesma instancia de Arma)");
        Inimigo arqueiroA = (Inimigo) registro.getPrototipo("arqueiro");
        Inimigo arqueiroB = copiaRasa(arqueiroA); // copia campo a campo, SEM clonar a Arma

        System.out.println("arqueiroA -> " + arqueiroA);
        System.out.println("arqueiroB -> " + arqueiroB);
        System.out.println("  Objetos distintos?      " + (arqueiroA != arqueiroB));
        System.out.println("  Mas dividem a Arma?     " + (arqueiroA.getArma() == arqueiroB.getArma()));

        arqueiroB.getArma().setNome("Arco Longo");
        arqueiroB.getArma().setBonusDano(60);

        System.out.println("Depois de mexer SO na arma do arqueiroB:");
        System.out.println("  arqueiroB.arma -> " + arqueiroB.getArma());
        System.out.println("  arqueiroA.arma -> " + arqueiroA.getArma() + "  <-- MUDOU SOZINHO (bug do aliasing)");
    }

    /**
     * Copia rasa proposital: copia os campos, mas passa a MESMA referencia
     * de Arma. E exatamente o que Object.clone() faz por padrao no Java.
     */
    private static Inimigo copiaRasa(Inimigo base) {
        return new Inimigo(base.getTipo(), base.getVida(), base.getDano(), base.getArma());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=".repeat(72));
        System.out.println(texto);
        System.out.println("=".repeat(72));
    }
}
