package src;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final int id;
    private final LocalDateTime dataHora;
    private final ArrayList<ItemVenda> itens = new ArrayList<>();

    public Venda(int id) {
        this.id = id;
        this.dataHora = LocalDateTime.now();
    }

    public void adicionarItem(Produto produto, int quantidade) {
        for (int i = 0; i < itens.size(); i++) {
            ItemVenda existente = itens.get(i);
            if (existente.getCodigo().equals(produto.getCodigo())) {
                itens.set(i, existente.comMaisQuantidade(quantidade));
                return;
            }
        }
        itens.add(new ItemVenda(produto.getCodigo(), produto.getNome(), produto.getPrecoCentavos(), quantidade));
    }

    public int quantidadeDoProduto(String codigo) {
        for (ItemVenda item : itens) {
            if (item.getCodigo().equals(codigo)) {
                return item.getQuantidade();
            }
        }
        return 0;
    }

    public long totalCentavos() {
        long total = 0;
        for (ItemVenda item : itens) {
            total += item.subtotalCentavos();
        }
        return total;
    }

    public boolean estaVazia() {
        return itens.isEmpty();
    }

    public String resumo() {
        StringBuilder texto = new StringBuilder();
        texto.append(String.format("Venda #%d - %s%n", id, dataHora.format(FORMATO_DATA)));
        for (ItemVenda item : itens) {
            texto.append(String.format("  %-8s %-20.20s %d x %s = %s%n",
                    item.getCodigo(),
                    item.getNome(),
                    item.getQuantidade(),
                    Moeda.formatar(item.getPrecoUnitarioCentavos()),
                    Moeda.formatar(item.subtotalCentavos())));
        }
        texto.append("  Total: ").append(Moeda.formatar(totalCentavos()));
        return texto.toString();
    }

    public int getId() {
        return id;
    }

    public List<ItemVenda> getItens() {
        return new ArrayList<>(itens);
    }
}