#Sistema de Gestão de Estoque e Vendas

Projeto em Java 17 puro que roda no terminal. Controla produtos, registra vendas com um ou vários itens e consulta histórico e faturamento. Sem frameworks, banco de dados ou interface gráfica.

#Funcionalidades

- Cadastrar, listar, buscar (por código ou nome), editar e remover produtos
- Registrar vendas com vários produtos, recusando estoque insuficiente
- Baixa automática do estoque após uma venda válida (venda recusada não altera nada)
- Histórico de vendas e faturamento total
- Listagem de produtos com estoque abaixo de um limite

#Estrutura


gestao-estoque/
├── src/       Main, Produto, Estoque, Venda, ItemVenda, RegistroVendas, Moeda
└── testes/    TestesSistema.java


#Compilar e executar


javac -encoding UTF-8 -d out src/*.java
java -cp out Main


Testes:


javac -encoding UTF-8 -d out src/*.java testes/*.java
java -cp out TestesSistema


No Windows, se os acentos aparecerem errados, rode `chcp 65001` antes de executar.

#Valores monetários

Os preços são guardados em `long`, em centavos (R$ 12,50 = `1250`), para evitar erros de arredondamento. Para converter para reais, divida por 100 (reais) e use o resto (centavos): `1250 / 100 = 12` e `1250 % 100 = 50`. A classe `Moeda` faz essa conversão nos dois sentidos.