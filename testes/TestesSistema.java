package testes;

import src.Estoque;
import src.Moeda;
import src.Produto;
import src.RegistroVendas;
import src.Venda;

public class TestesSistema {

    private static int aprovadas = 0;
    private static int reprovadas = 0;

    public static void main(String[] args) {
        cenario1CadastroListagemEBusca();
        cenario2CodigoDuplicado();
        cenario3DadosInvalidos();
        cenario4VendaValidaComVariosProdutos();
        cenario5EstoqueInsuficiente();
        cenario6RecusaNaoAlteraNenhumEstoque();
        cenario7ProdutoInexistente();
        cenario8MesmoProdutoDuasVezes();
        cenario9VendaNaoPodeSerRegistradaDuasVezes();
        cenario10EdicaoERemocao();
        cenario11EstoqueBaixo();
        cenario12ConversaoDeMoeda();

        System.out.println();
        System.out.println("Verificações aprovadas: " + aprovadas);
        System.out.println("Verificações reprovadas: " + reprovadas);
        if (reprovadas > 0) {
            System.exit(1);
        }
    }

    private static void cenario1CadastroListagemEBusca() {
        System.out.println("Cenário 1 - Cadastro, listagem e busca");
        Estoque estoque = criarEstoquePadrao();
        verificar(estoque.listar().size() == 3, "lista possui 3 produtos");
        verificar(estoque.buscarPorCodigo("p001") != null, "busca por código ignora maiúsculas/minúsculas");
        verificar(estoque.buscarPorCodigo("P999") == null, "código inexistente retorna null");
        verificar(estoque.buscarPorNome("MOCH").size() == 1, "busca parcial por nome encontra Mochila");
        verificar(estoque.buscarPorNome("xyz").isEmpty(), "nome inexistente retorna lista vazia");
    }

    private static void cenario2CodigoDuplicado() {
        System.out.println("Cenário 2 - Código duplicado");
        Estoque estoque = criarEstoquePadrao();
        verificar(tentarCadastrar(estoque, "P001", "Outro", 100, 1) != null, "código P001 repetido é recusado");
        verificar(tentarCadastrar(estoque, " p001 ", "Outro", 100, 1) != null, "código repetido com espaços/minúsculas é recusado");
        verificar(estoque.listar().size() == 3, "lista continua com 3 produtos");
    }

    private static void cenario3DadosInvalidos() {
        System.out.println("Cenário 3 - Preço, quantidade, nome e código inválidos");
        Estoque estoque = criarEstoquePadrao();
        verificar(tentarCadastrar(estoque, "P010", "Item", -100, 1) != null, "preço negativo recusado");
        verificar(tentarCadastrar(estoque, "P010", "Item", 0, 1) != null, "preço zero recusado");
        verificar(tentarCadastrar(estoque, "P010", "Item", 100, -1) != null, "quantidade negativa recusada");
        verificar(tentarCadastrar(estoque, "P010", "  ", 100, 1) != null, "nome vazio recusado");
        verificar(tentarCadastrar(estoque, "  ", "Item", 100, 1) != null, "código vazio recusado");
        verificar(tentarCadastrar(estoque, "P010", "Item", Produto.PRECO_MAXIMO_CENTAVOS + 1, 1) != null, "preço acima do máximo recusado");
        verificar(tentarCadastrar(estoque, "P010", "Item", 100, 0) == null, "quantidade zero é aceita");
        verificar(estoque.listar().size() == 4, "somente o cadastro válido entrou na lista");
    }

    private static void cenario4VendaValidaComVariosProdutos() {
        System.out.println("Cenário 4 - Venda válida com vários produtos");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Venda venda = registro.iniciarVenda();
        verificar(tentarAdicionar(registro, venda, "P001", 2) == null, "item 1 adicionado");
        verificar(tentarAdicionar(registro, venda, "P002", 10) == null, "item 2 adicionado");
        verificar(tentarRegistrar(registro, venda) == null, "venda registrada");
        verificar(estoque.buscarPorCodigo("P001").getQuantidade() == 8, "estoque de P001 caiu de 10 para 8");
        verificar(estoque.buscarPorCodigo("P002").getQuantidade() == 40, "estoque de P002 caiu de 50 para 40");
        verificar(estoque.buscarPorCodigo("P003").getQuantidade() == 3, "estoque de P003 não mudou");
        verificar(venda.totalCentavos() == 6000, "total da venda é 6000 centavos (R$ 60,00)");
        verificar(registro.getHistorico().size() == 1, "histórico possui 1 venda");
        verificar(registro.faturamentoTotalCentavos() == 6000, "faturamento total é 6000 centavos");
    }

    private static void cenario5EstoqueInsuficiente() {
        System.out.println("Cenário 5 - Estoque insuficiente");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Venda venda = registro.iniciarVenda();
        verificar(tentarAdicionar(registro, venda, "P003", 4) != null, "pedir 4 de P003 (estoque 3) é recusado");
        verificar(venda.estaVazia(), "item recusado não entra na venda");
        verificar(tentarRegistrar(registro, venda) != null, "venda vazia não pode ser registrada");
        verificar(estoque.buscarPorCodigo("P003").getQuantidade() == 3, "estoque de P003 continua 3");
        verificar(registro.getHistorico().isEmpty(), "histórico continua vazio");
        verificar(registro.faturamentoTotalCentavos() == 0, "faturamento continua zero");
    }

    private static void cenario6RecusaNaoAlteraNenhumEstoque() {
        System.out.println("Cenário 6 - Venda recusada no registro não altera nenhum estoque");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Venda venda = registro.iniciarVenda();
        tentarAdicionar(registro, venda, "P001", 2);
        tentarAdicionar(registro, venda, "P003", 2);
        estoque.buscarPorCodigo("P003").atualizar("Mochila", 8990, 1);
        verificar(tentarRegistrar(registro, venda) != null, "registro recusado porque P003 ficou com 1 unidade");
        verificar(estoque.buscarPorCodigo("P001").getQuantidade() == 10, "P001 continua com 10 (nada foi baixado)");
        verificar(estoque.buscarPorCodigo("P003").getQuantidade() == 1, "P003 continua com 1");
        verificar(registro.getHistorico().isEmpty(), "venda recusada não entra no histórico");
        verificar(registro.faturamentoTotalCentavos() == 0, "faturamento continua zero");
    }

    private static void cenario7ProdutoInexistente() {
        System.out.println("Cenário 7 - Produto inexistente e quantidade inválida");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Venda venda = registro.iniciarVenda();
        String erro = tentarAdicionar(registro, venda, "X999", 1);
        verificar(erro != null && erro.contains("inexistente"), "código X999 recusado com mensagem de produto inexistente");
        verificar(tentarAdicionar(registro, venda, "P001", 0) != null, "quantidade zero recusada");
        verificar(tentarAdicionar(registro, venda, "P001", -3) != null, "quantidade negativa recusada");
        verificar(venda.estaVazia(), "nenhum item entrou na venda");
        verificar(estoque.buscarPorCodigo("P001").getQuantidade() == 10, "estoque de P001 intacto");
    }

    private static void cenario8MesmoProdutoDuasVezes() {
        System.out.println("Cenário 8 - Mesmo produto adicionado mais de uma vez");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Venda venda = registro.iniciarVenda();
        verificar(tentarAdicionar(registro, venda, "P003", 2) == null, "primeiras 2 unidades de P003 aceitas");
        verificar(tentarAdicionar(registro, venda, "P003", 2) != null, "mais 2 excedem o estoque (2 + 2 > 3)");
        verificar(tentarAdicionar(registro, venda, "p003", 1) == null, "mais 1 unidade cabe (2 + 1 = 3)");
        verificar(venda.getItens().size() == 1, "os lançamentos viraram um único item");
        verificar(venda.quantidadeDoProduto("P003") == 3, "quantidade acumulada é 3");
        verificar(venda.totalCentavos() == 26970, "total é 26970 centavos (R$ 269,70)");
    }

    private static void cenario9VendaNaoPodeSerRegistradaDuasVezes() {
        System.out.println("Cenário 9 - Venda não pode ser registrada duas vezes");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Venda venda = registro.iniciarVenda();
        tentarAdicionar(registro, venda, "P001", 3);
        verificar(tentarRegistrar(registro, venda) == null, "primeiro registro aceito");
        verificar(tentarRegistrar(registro, venda) != null, "segundo registro da mesma venda recusado");
        verificar(estoque.buscarPorCodigo("P001").getQuantidade() == 7, "estoque baixou apenas uma vez (10 - 3)");
        verificar(registro.getHistorico().size() == 1, "histórico possui apenas 1 venda");
    }

    private static void cenario10EdicaoERemocao() {
        System.out.println("Cenário 10 - Edição, remoção e histórico preservado");
        Estoque estoque = criarEstoquePadrao();
        RegistroVendas registro = new RegistroVendas(estoque);
        Produto caderno = estoque.buscarPorCodigo("P001");
        caderno.atualizar("Caderno Grande", 1500, 20);
        verificar(caderno.getNome().equals("Caderno Grande") && caderno.getPrecoCentavos() == 1500
                && caderno.getQuantidade() == 20, "edição válida altera os três campos");

        String erro = null;
        try {
            caderno.atualizar("Nome Novo", -1, 5);
        } catch (IllegalArgumentException e) {
            erro = e.getMessage();
        }
        verificar(erro != null, "edição com preço negativo é recusada");
        verificar(caderno.getNome().equals("Caderno Grande") && caderno.getQuantidade() == 20,
                "edição recusada não altera nenhum campo");

        Venda venda = registro.iniciarVenda();
        tentarAdicionar(registro, venda, "P002", 5);
        tentarRegistrar(registro, venda);
        estoque.remover("P002");
        verificar(estoque.buscarPorCodigo("P002") == null, "P002 foi removido do estoque");
        verificar(registro.getHistorico().get(0).getItens().get(0).getNome().equals("Caneta"),
                "histórico mantém o nome do produto removido");
        verificar(registro.faturamentoTotalCentavos() == 1750, "faturamento mantém 5 x R$ 3,50");

        erro = null;
        try {
            estoque.remover("P002");
        } catch (IllegalArgumentException e) {
            erro = e.getMessage();
        }
        verificar(erro != null, "remover produto inexistente é recusado");
    }

    private static void cenario11EstoqueBaixo() {
        System.out.println("Cenário 11 - Produtos abaixo do limite");
        Estoque estoque = criarEstoquePadrao();
        verificar(estoque.produtosAbaixoDoLimite(10).size() == 1, "limite 10: só P003 (3 unidades) está abaixo");
        verificar(estoque.produtosAbaixoDoLimite(11).size() == 2, "limite 11: P001 (10) e P003 (3)");
        verificar(estoque.produtosAbaixoDoLimite(0).isEmpty(), "limite 0: nenhum produto");
        String erro = null;
        try {
            estoque.produtosAbaixoDoLimite(-1);
        } catch (IllegalArgumentException e) {
            erro = e.getMessage();
        }
        verificar(erro != null, "limite negativo é recusado");
    }

    private static void cenario12ConversaoDeMoeda() {
        System.out.println("Cenário 12 - Conversão de valores");
        verificar(Moeda.paraCentavos("12,50") == 1250, "\"12,50\" vira 1250");
        verificar(Moeda.paraCentavos("12.5") == 1250, "\"12.5\" vira 1250");
        verificar(Moeda.paraCentavos("10") == 1000, "\"10\" vira 1000");
        verificar(Moeda.paraCentavos("0,05") == 5, "\"0,05\" vira 5");
        verificar(Moeda.formatar(1250).equals("R$ 12,50"), "1250 vira \"R$ 12,50\"");
        verificar(Moeda.formatar(5).equals("R$ 0,05"), "5 vira \"R$ 0,05\"");
        verificar(tentarConverter("abc") != null, "\"abc\" é recusado");
        verificar(tentarConverter("-5") != null, "\"-5\" é recusado");
        verificar(tentarConverter("12,345") != null, "\"12,345\" (3 casas) é recusado");
        verificar(tentarConverter("") != null, "texto vazio é recusado");
    }

    private static Estoque criarEstoquePadrao() {
        Estoque estoque = new Estoque();
        estoque.cadastrar(new Produto("P001", "Caderno", 1250, 10));
        estoque.cadastrar(new Produto("P002", "Caneta", 350, 50));
        estoque.cadastrar(new Produto("P003", "Mochila", 8990, 3));
        return estoque;
    }

    private static String tentarCadastrar(Estoque estoque, String codigo, String nome, long preco, int quantidade) {
        try {
            estoque.cadastrar(new Produto(codigo, nome, preco, quantidade));
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static String tentarAdicionar(RegistroVendas registro, Venda venda, String codigo, int quantidade) {
        try {
            registro.adicionarItem(venda, codigo, quantidade);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static String tentarRegistrar(RegistroVendas registro, Venda venda) {
        try {
            registro.registrar(venda);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static String tentarConverter(String texto) {
        try {
            Moeda.paraCentavos(texto);
            return null;
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private static void verificar(boolean condicao, String descricao) {
        if (condicao) {
            aprovadas++;
            System.out.println("  [OK]    " + descricao);
        } else {
            reprovadas++;
            System.out.println("  [FALHA] " + descricao);
        }
    }
}