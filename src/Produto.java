package src;
public class Produto {

    public static final long PRECO_MAXIMO_CENTAVOS = 100_000_000L;
    public static final int QUANTIDADE_MAXIMA = 1_000_000;

    private final String codigo;
    private String nome;
    private long precoCentavos;
    private int quantidade;

    public Produto(String codigo, String nome, long precoCentavos, int quantidade) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("O código não pode ser vazio.");
        }
        validarDados(nome, precoCentavos, quantidade);
        this.codigo = codigo.trim().toUpperCase();
        this.nome = nome.trim();
        this.precoCentavos = precoCentavos;
        this.quantidade = quantidade;
    }

    public void atualizar(String nome, long precoCentavos, int quantidade) {
        validarDados(nome, precoCentavos, quantidade);
        this.nome = nome.trim();
        this.precoCentavos = precoCentavos;
        this.quantidade = quantidade;
    }

    public void baixarEstoque(int unidades) {
        if (unidades <= 0) {
            throw new IllegalArgumentException("A quantidade a retirar deve ser maior que zero.");
        }
        if (unidades > quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente para " + nome + ".");
        }
        quantidade -= unidades;
    }

    private static void validarDados(String nome, long precoCentavos, int quantidade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        if (precoCentavos <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        if (precoCentavos > PRECO_MAXIMO_CENTAVOS) {
            throw new IllegalArgumentException(
                    "O preço máximo permitido é " + Moeda.formatar(PRECO_MAXIMO_CENTAVOS) + ".");
        }
        if (quantidade < 0 || quantidade > QUANTIDADE_MAXIMA) {
            throw new IllegalArgumentException(
                    "A quantidade deve estar entre 0 e " + QUANTIDADE_MAXIMA + ".");
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public long getPrecoCentavos() {
        return precoCentavos;
    }

    public int getQuantidade() {
        return quantidade;
    }
}