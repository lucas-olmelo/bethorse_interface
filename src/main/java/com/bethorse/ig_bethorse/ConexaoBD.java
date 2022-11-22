package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBD {
    
    private static Connection conexao_MySql = null;

    private static String LINK = "jdbc:mysql://localhost:3306/bethorse";

    private static final String usuario = "root";
    private static final String senha = "Senai123";

    public Connection connectionMySql() {
        Connection conexao_MySql = null;
        try {
            conexao_MySql = DriverManager.getConnection(LINK, usuario, senha);

            System.out.println("Conexão OK!");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return conexao_MySql;

    }
    
    public void closeConnectionMySql(Connection con) {
        try {
            if (con != null) {
                con.close();
                System.out.println("Fechamento OK");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ocorreu um problema para encerrar a conexão com o BD.", e);
        }
    }
}
