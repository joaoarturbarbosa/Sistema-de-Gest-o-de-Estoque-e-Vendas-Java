package src;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static final Scanner entrada = new Scanner(System.in);
    private static final Estoque estoque = new Estoque();
    private static final RegistroVendas registro = new RegistroVendas(estoque);

    public static void main(String[] args) {
        try {
            executar();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada. Programa finalizado.");
        }
    }

    private static void executar() {
        boolean continuar = true;
        while (continuar) {
            exibirMenu();
            int opcao = lerInteiro("Opção: ");
            try {
                switch (opcao) {
                    case 1 -> cadastrarProduto();
                    case 2 -> imprimirProdutos(estoque.listar());
                    case 3 -> buscarProduto();
                    case 4 -> editarProduto();
                    case 5 -> removerProduto();
                    case 6 -> registrarVenda();
                    case 7 -> mostrarHistorico();
                    case 8 -> mostrarFaturamento();
                    case 9 -> mostrarEstoqueBaixo();
                    case 0 -> continuar = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        System.out.println("Até logo!");
    }

    private static void exibirMenu() {
        System.out.println();
        System.out.println("=== GESTÃO DE ESTOQUE E VENDAS ===");
        System.out.println("1 - Cadastrar produto");
        System.out.println("2 - Listar produtos");
        System.out.println("3 - Buscar produto");
        System.out.println("4 - Editar produto");
        System.out.println("5 - Remover produto");
        System.out.println("6 - Registrar venda");
        System.out.println("7 - Histórico de vendas");
        System.out.println("8 - Faturamento total");
        System.out.println("9 - Produtos com estoque baixo");
        System.out.println("0 - Sair");
    }

    private static void cadastrarProduto() {
        String codigo = lerTexto("Código: ");
        String nome = lerTexto("Nome: ");
        long preco = lerPreco("Preço (ex.: 12,50): ");
        int quantidade = lerInteiro("Quantidade: ");
        estoque.cadastrar(new Produto(codigo, nome, preco, quantidade));
        System.out.println("Produto cadastrado com sucesso.");
    }

    private static void buscarProduto() {
        int tipo = lerInteiro("Buscar por 1 - código ou 2 - nome: ");
        if (tipo == 1) {
            Produto produto = estoque.buscarPorCodigo(lerTexto("Código: "));
            imprimirProdutos(produto == null ? List.of() : List.of(produto));
        } else if (tipo == 2) {
            imprimirProdutos(estoque.buscarPorNome(lerTexto("Nome (ou parte dele): ")));
        } else {
            System.out.println("Opção inválida.");
        }
    }

    private static void editarProduto() {
        Produto produto = estoque.buscarPorCodigo(lerTexto("Código do produto: "));
        if (produto == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        System.out.println("Deixe em branco para manter o valor atual.");
        String nome = lerTexto("Nome [" + produto.getNome() + "]: ");
        String preco = lerTexto("Preço [" + Moeda.formatar(produto.getPrecoCentavos()) + "]: ");
        String quantidade = lerTexto("Quantidade [" + produto.getQuantidade() + "]: ");

        String novoNome = nome.isEmpty() ? produto.getNome() : nome;
        long novoPreco = preco.isEmpty() ? produto.getPrecoCentavos() : Moeda.paraCentavos(preco);
        int novaQuantidade = quantidade.isEmpty() ? produto.getQuantidade() : converterInteiro(quantidade);

        produto.atualizar(novoNome, novoPreco, novaQuantidade);
        System.out.println("Produto atualizado com sucesso.");
    }

    private static void removerProduto() {
        Produto produto = estoque.buscarPorCodigo(lerTexto("Código do produto: "));
        if (produto == null) {
            System.out.println("Produto não encontrado.");
            return;
        }
        String confirmacao = lerTexto("Remover \"" + produto.getNome() + "\"? (s/n): ");
        if (confirmacao.equalsIgnoreCase("s")) {
            estoque.remover(produto.getCodigo());
            System.out.println("Produto removido.");
        } else {
            System.out.println("Remoção cancelada.");
        }
    }

    private static void registrarVenda() {
        if (estoque.listar().isEmpty()) {
            System.out.println("Não há produtos cadastrados.");
            return;
        }
        Venda venda = registro.iniciarVenda();
        boolean adicionando = true;
        while (adicionando) {
            String codigo = lerTexto("Código do produto (ENTER para finalizar): ");
            if (codigo.isEmpty()) {
                adicionando = false;
            } else {
                int quantidade = lerInteiro("Quantidade: ");
                try {
                    registro.adicionarItem(venda, codigo, quantidade);
                    System.out.println("Item adicionado.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        }
        if (venda.estaVazia()) {
            System.out.println("Venda cancelada: nenhum item adicionado.");
            return;
        }
        System.out.println(venda.resumo());
        if (lerTexto("Confirmar venda? (s/n): ").equalsIgnoreCase("s")) {
            registro.registrar(venda);
            System.out.println("Venda registrada com sucesso.");
        } else {
            System.out.println("Venda cancelada. O estoque não foi alterado.");
        }
    }

    private static void mostrarHistorico() {
        List<Venda> vendas = registro.getHistorico();
        if (vendas.isEmpty()) {
            System.out.println("Nenhuma venda registrada.");
            return;
        }
        for (Venda venda : vendas) {
            System.out.println(venda.resumo());
            System.out.println();
        }
    }

    private static void mostrarFaturamento() {
        System.out.println("Vendas registradas: " + registro.getHistorico().size());
        System.out.println("Faturamento total: " + Moeda.formatar(registro.faturamentoTotalCentavos()));
    }

    private static void mostrarEstoqueBaixo() {
        int limite = lerInteiro("Mostrar produtos com quantidade abaixo de: ");
        imprimirProdutos(estoque.produtosAbaixoDoLimite(limite));
    }

    private static void imprimirProdutos(List<Produto> produtos) {
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto encontrado.");
            return;
        }
        System.out.printf("%-10s %-25s %14s %8s%n", "Código", "Nome", "Preço", "Qtd");
        for (Produto produto : produtos) {
            System.out.printf("%-10s %-25.25s %14s %8d%n",
                    produto.getCodigo(),
                    produto.getNome(),
                    Moeda.formatar(produto.getPrecoCentavos()),
                    produto.getQuantidade());
        }
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return entrada.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static long lerPreco(String mensagem) {
        while (true) {
            try {
                return Moeda.paraCentavos(lerTexto(mensagem));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static int converterInteiro(String texto) {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Quantidade inválida: digite um número inteiro.");
        }
    }
}