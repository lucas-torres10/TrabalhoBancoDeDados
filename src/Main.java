import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String driver = "org.postgresql.Driver";
        String user = "postgres";
        String senha = "postgres";


        String url = "jdbc:postgresql://localhost:5432/estoque-produto";
        System.out.println("Inciando Programa de Estoque de Produtos");
        System.out.println("Iniciando conexao com banco de dados");

        try {
            Class.forName(driver);
            Connection con = null;
            con = DriverManager.getConnection(url, user, senha);
            System.out.println("Conexão realizada com sucesso.");
            EstoqueController produto = new EstoqueController(con);
            menu(produto);

        } catch (Exception e) {
            System.out.println("Falhou");
            System.err.print(e.getMessage());
        }
        scanner.close();


    }

    public static void menu(EstoqueController produto) {
        int op;
        do {

            System.out.println(
                            "1 - Cadastrar Produto\n" +
                            "2 - Listar Produtos\n" +
                            "3 - Deletar Produto \n" +
                            "4 - Atualizar Produto\n" +
                            "5 - SAIR"
            );
            op = scanner.nextInt();

            switch (op) {
                case 1:
                    produto.cadastrarProduto();
                    break;
                case 2:
                    produto.consultarProdutosFornecedores();
                    break;

                case 3:
                    produto.deletarProduto();
                    break;
                case 4:
                    produto.atualizarProduto();
                    break;
                case 5:
                    System.out.println("Encerrando Aplicação");
                   break;

                default:
                    System.out.println("Opção inválida!");
                    break;


            }


        } while (op != 5);
    }


}