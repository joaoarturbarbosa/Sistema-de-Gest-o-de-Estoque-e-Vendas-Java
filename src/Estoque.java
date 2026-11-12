package src;
import java.util.ArrayList;
import java.util.List;

public class Estoque {

    private final ArrayList<Produto> produtos = new ArrayList<>();

    public void cadastrar(Produto produto) {
        if (buscarPorCodigo(produto.getCodigo()) != null) {
            throw new IllegalArgumentException("Já existe um produto com o código " + produto.getCodigo() + ".");
        }
        produtos.add(produto);
    }

    public List<Produto> listar() {
        return new ArrayList<>(produtos);
    }

    public Produto buscarPorCodigo(String codigo) {
        String procurado = codigo.trim();
        for (Produto produto : produtos) {
            if (produto.getCodigo().equalsIgnoreCase(procurado)) {
                return produto;
            }
        }
        return null;
    }

    public List<Produto> buscarPorNome(String trecho) {
        String procurado = trecho.trim().toLowerCase();
        if (procurado.isEmpty()) {
            throw new IllegalArgumentException("Digite ao menos parte do nome.");
        }
        ArrayList<Produto> encontrados = new ArrayList<>();
        for (Produto produto : produtos) {
            if (produto.getNome().toLowerCase().contains(procurado)) {
                encontrados.add(produto);
            }
        }
        return encontrados;
    }

    public void remover(String codigo) {
        Produto produto = buscarPorCodigo(codigo);
        if (produto == null) {
            throw new IllegalArgumentException("Produto inexistente: " + codigo.trim() + ".");
        }
        produtos.remove(produto);
    }

    public List<Produto> produtosAbaixoDoLimite(int limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        ArrayList<Produto> abaixo = new ArrayList<>();
        for (Produto produto : produtos) {
            if (produto.getQuantidade() < limite) {
                abaixo.add(produto);
            }
        }
        return abaixo;
    }
}