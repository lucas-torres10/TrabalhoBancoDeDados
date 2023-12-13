import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class EstoqueController {

    static Scanner scanner = new Scanner(System.in);
    private Connection conexao;

    public EstoqueController(Connection conexao) {
        this.conexao = conexao;
    }

    public void cadastrarProduto() {
        try {
            System.out.println("Digite o nome do produto:");
            String nomeProduto = scanner.nextLine();

            System.out.println("Digite a quantidade do produto:");
            int quantidadeProduto = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Digite o nome do fornecedor:");
            String nomeFornecedor = scanner.nextLine();

            System.out.println("Digite o contato do fornecedor:");
            String contatoFornecedor = scanner.nextLine();

            // Inserção do fornecedor na tabela fornecedor
            String sqlFornecedor = "INSERT INTO fornecedor (nome, contato) VALUES (?, ?)";
            int idFornecedor = -1; // Variável para armazenar o ID do fornecedor inserido
            try (PreparedStatement statementFornecedor = conexao.prepareStatement(sqlFornecedor, Statement.RETURN_GENERATED_KEYS)) {
                statementFornecedor.setString(1, nomeFornecedor);
                statementFornecedor.setString(2, contatoFornecedor);

                int rowsInsertedFornecedor = statementFornecedor.executeUpdate();
                if (rowsInsertedFornecedor > 0) {
                    System.out.println("Fornecedor cadastrado com sucesso.");
                    ResultSet generatedKeys = statementFornecedor.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        idFornecedor = generatedKeys.getInt(1); // Obtém o ID gerado para o fornecedor
                    }
                } else {
                    System.out.println("Falha ao cadastrar o fornecedor.");
                    return; // Sai do método se não for possível cadastrar o fornecedor
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Inserção do produto na tabela produto
            String sqlProduto = "INSERT INTO produto (nome, quantidade) VALUES (?, ?)";
            int idProduto = -1; // Variável para armazenar o ID do produto inserido
            try (PreparedStatement statementProduto = conexao.prepareStatement(sqlProduto, Statement.RETURN_GENERATED_KEYS)) {
                statementProduto.setString(1, nomeProduto);
                statementProduto.setInt(2, quantidadeProduto);

                int rowsInsertedProduto = statementProduto.executeUpdate();
                if (rowsInsertedProduto > 0) {
                    System.out.println("Produto cadastrado com sucesso.");
                    ResultSet generatedKeys = statementProduto.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        idProduto = generatedKeys.getInt(1); // Obtém o ID gerado para o produto
                    }
                } else {
                    System.out.println("Falha ao cadastrar o produto.");
                    return; // Sai do método se não for possível cadastrar o produto
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Associação do produto ao fornecedor na tabela produto_fornecedor
            String sqlProdutoFornecedor = "INSERT INTO produto_fornecedor (id_produto, id_fornecedor) VALUES (?, ?)";
            try (PreparedStatement statementProdutoFornecedor = conexao.prepareStatement(sqlProdutoFornecedor)) {
                statementProdutoFornecedor.setInt(1, idProduto);
                statementProdutoFornecedor.setInt(2, idFornecedor);

                int rowsInsertedProdutoFornecedor = statementProdutoFornecedor.executeUpdate();
                if (rowsInsertedProdutoFornecedor > 0) {
                    System.out.println("Produto associado ao fornecedor com sucesso.");
                } else {
                    System.out.println("Falha ao associar o produto ao fornecedor.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Certifique-se de inserir valores corretos.");
        }
    }

    public void consultarProdutosFornecedores() {
        try {
            String sql = "SELECT * FROM vw_produtos_fornecedores";

            PreparedStatement statement = conexao.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Produtos e seus fornecedores:");
            System.out.println("------------------------------");

            while (resultSet.next()) {
                int idRelacao = resultSet.getInt("id_relacao");
                String nomeProduto = resultSet.getString("nome_produto");
                int quantidadeProduto = resultSet.getInt("quantidade");
                String nomeFornecedor = resultSet.getString("nome_fornecedor");
                String contatoFornecedor = resultSet.getString("contato_fornecedor");

                System.out.println("ID Relação: " + idRelacao);
                System.out.println("Produto: " + nomeProduto + " (Quantidade: " + quantidadeProduto + ")");
                System.out.println("Fornecedor: " + nomeFornecedor + " (Contato: " + contatoFornecedor + ")");
                System.out.println("------------------------------");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void atualizarProduto() {
        consultarProdutosFornecedores();
        try {
            System.out.println("Digite o ID do produto que deseja atualizar:");
            int idProdutoAtualizar = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer do scanner

            System.out.println("Digite o novo nome do produto:");
            String novoNomeProduto = scanner.nextLine();

            System.out.println("Digite a nova quantidade do produto:");
            int novaQuantidadeProduto = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer do scanner

            // Atualização do produto na tabela produto
            String sqlUpdateProduto = "UPDATE produto SET nome = ?, quantidade = ? WHERE id = ?";
            try (PreparedStatement statementUpdateProduto = conexao.prepareStatement(sqlUpdateProduto)) {
                statementUpdateProduto.setString(1, novoNomeProduto);
                statementUpdateProduto.setInt(2, novaQuantidadeProduto);
                statementUpdateProduto.setInt(3, idProdutoAtualizar);

                int rowsUpdated = statementUpdateProduto.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Produto atualizado com sucesso.");
                } else {
                    System.out.println("Falha ao atualizar o produto. Verifique o ID.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Certifique-se de inserir um valor numérico para o ID do produto ou valores corretos para as informações a serem atualizadas.");
        }
    }



    public void deletarProduto() {
        consultarProdutosFornecedores();
        try {
            System.out.println("Digite o ID do produto que deseja deletar:");
            int idProdutoDeletar = scanner.nextInt();
            scanner.nextLine(); // Limpa o buffer do scanner

            // Recupera o ID do fornecedor associado ao produto
            int idFornecedor = -1;
            String sqlSelectFornecedor = "SELECT id_fornecedor FROM produto_fornecedor WHERE id_produto = ?";
            try (PreparedStatement statementSelectFornecedor = conexao.prepareStatement(sqlSelectFornecedor)) {
                statementSelectFornecedor.setInt(1, idProdutoDeletar);
                ResultSet resultSet = statementSelectFornecedor.executeQuery();
                if (resultSet.next()) {
                    idFornecedor = resultSet.getInt("id_fornecedor");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Excluir a referência do produto na tabela produto_fornecedor
            String sqlDeleteFornecedorProduto = "DELETE FROM produto_fornecedor WHERE id_produto = ?";
            try (PreparedStatement statementDeleteFornecedorProduto = conexao.prepareStatement(sqlDeleteFornecedorProduto)) {
                statementDeleteFornecedorProduto.setInt(1, idProdutoDeletar);
                statementDeleteFornecedorProduto.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Excluir o produto da tabela produto
            String sqlDeleteProduto = "DELETE FROM produto WHERE id = ?";
            try (PreparedStatement statementDeleteProduto = conexao.prepareStatement(sqlDeleteProduto)) {
                statementDeleteProduto.setInt(1, idProdutoDeletar);
                int rowsDeleted = statementDeleteProduto.executeUpdate();
                if (rowsDeleted > 0) {
                    System.out.println("Produto deletado com sucesso.");

                    // Se um fornecedor foi encontrado, exclui o fornecedor também
                    if (idFornecedor != -1) {
                        String sqlDeleteFornecedor = "DELETE FROM fornecedor WHERE id = ?";
                        try (PreparedStatement statementDeleteFornecedor = conexao.prepareStatement(sqlDeleteFornecedor)) {
                            statementDeleteFornecedor.setInt(1, idFornecedor);
                            statementDeleteFornecedor.executeUpdate();
                            System.out.println("Fornecedor do produto também foi deletado.");
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Falha ao deletar o produto. Verifique o ID.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Certifique-se de inserir um valor numérico para o ID do produto.");
        }
    }











}
