package src;
import java.util.ArrayList;
import java.util.List;

public class RegistroVendas {

    private final Estoque estoque;
    private final ArrayList<Venda> vendas = new ArrayList<>();

    public RegistroVendas(Estoque estoque) {
        this.estoque = estoque;
    }

    public Venda iniciarVenda() {
        return new Venda(vendas.size() + 1);
    }

    public void adicionarItem(Venda venda, String codigo, int quantidade) {
        validarVendaAberta(venda);
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade vendida deve ser maior que zero.");
        }
        Produto produto = estoque.buscarPorCodigo(codigo);
        if (produto == null) {
            throw new IllegalArgumentException("Produto inexistente: " + codigo.trim() + ".");
        }
        int disponivel = Math.max(0, produto.getQuantidade() - venda.quantidadeDoProduto(produto.getCodigo()));
        if (quantidade > disponivel) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente para " + produto.getNome() + ". Disponível: " + disponivel + ".");
        }
        venda.adicionarItem(produto, quantidade);
    }

    public void registrar(Venda venda) {
        validarVendaAberta(venda);
        if (venda.estaVazia()) {
            throw new IllegalArgumentException("A venda precisa ter ao menos um item.");
        }
        for (ItemVenda item : venda.getItens()) {
            Produto produto = estoque.buscarPorCodigo(item.getCodigo());
            if (produto == null) {
                throw new IllegalArgumentException("Produto inexistente: " + item.getCodigo() + ".");
            }
            if (item.getQuantidade() > produto.getQuantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para " + produto.getNome() + ". Disponível: "
                                + produto.getQuantidade() + ".");
            }
        }
        for (ItemVenda item : venda.getItens()) {
            estoque.buscarPorCodigo(item.getCodigo()).baixarEstoque(item.getQuantidade());
        }
        vendas.add(venda);
    }

    public List<Venda> getHistorico() {
        return new ArrayList<>(vendas);
    }

    public long faturamentoTotalCentavos() {
        long total = 0;
        for (Venda venda : vendas) {
            total += venda.totalCentavos();
        }
        return total;
    }

    private void validarVendaAberta(Venda venda) {
        if (venda.getId() != vendas.size() + 1) {
            throw new IllegalArgumentException("Esta venda não está mais aberta.");
        }
    }
}