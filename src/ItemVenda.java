package src;
public class ItemVenda {

    private final String codigo;
    private final String nome;
    private final long precoUnitarioCentavos;
    private final int quantidade;

    public ItemVenda(String codigo, String nome, long precoUnitarioCentavos, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade vendida deve ser maior que zero.");
        }
        this.codigo = codigo;
        this.nome = nome;
        this.precoUnitarioCentavos = precoUnitarioCentavos;
        this.quantidade = quantidade;
    }

    public ItemVenda comMaisQuantidade(int extra) {
        return new ItemVenda(codigo, nome, precoUnitarioCentavos, quantidade + extra);
    }

    public long subtotalCentavos() {
        return precoUnitarioCentavos * quantidade;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public long getPrecoUnitarioCentavos() {
        return precoUnitarioCentavos;
    }

    public int getQuantidade() {
        return quantidade;
    }
}