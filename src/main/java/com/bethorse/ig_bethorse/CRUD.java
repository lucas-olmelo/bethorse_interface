package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class CRUD {
    
    int flagl = 0;
    int flagc = 0;

    public int getFlagl() {
        return flagl;
    }

    public void setFlagl(int flagl) {
        this.flagl = flagl;
    }

    public int getFlagc() {
        return flagc;
    }

    public void setFlagc(int flagc) {
        this.flagc = flagc;
    }
    

    public void Logar(Connection con, String email, String senha){
        
        try{
            String sql = "SELECT * FROM usuario WHERE email = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, email);
            ResultSet result = pstm.executeQuery();

            while (result.next()){
                if (result.getString("senha").equals(senha)){
                    if (result.getString("tipo").toLowerCase().equals("dono")){
                        usuDono dono = new usuDono();
                        dono.setVisible(true);
                        dono.setLocationRelativeTo(null);
                        AddCred ad = new AddCred();
                        float saldo = ad.pegarSaldo(con, email);
                        dono.recebe(con, email, saldo);
                        this.setFlagl(1);
                    } else if (result.getString("tipo").toLowerCase().equals("apostador")){
                        usuApostador ap = new usuApostador();
                        ap.setVisible(true);
                        ap.setLocationRelativeTo(null);
                        AddCred ad = new AddCred();
                        float saldo = ad.pegarSaldo(con, email);
                        ap.recebe(con, email, saldo);
                        this.setFlagl(1);
                    }
                }
                
            }

        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Erro!!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    public void cadastro(Connection con, long cpf, String usuario, String email, String senha, String telefone, int tipo){
        
        try{
            int idCred = 0;
            String cred = "INSERT INTO creditos (idCredito, saldo) VALUES (null, 0)";
            PreparedStatement ps = con.prepareStatement(cred);
            ps.execute();
            
            String query = "select max(idCredito) from creditos;";
            PreparedStatement stat = con.prepareStatement(query);
            ResultSet rs = stat.executeQuery();
            if (rs.next()){
                idCred = rs.getInt(1);
            }
            
            String tipoUser = "";
            
            switch (tipo) {
                case 1:
                    tipoUser = "dono";
                    break;
                case 2:
                    tipoUser = "apostador";
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Insira um tipo de usuário!", "Inforrmações inválidas!", JOptionPane.ERROR_MESSAGE);
                    break;
            }
            
            String sql = "INSERT INTO usuario (cpf, nome, email, senha, telefone, tipo, idCredito) VALUES (?, ?, ?, ?, ?, ?, ?);";
            
            PreparedStatement pstm = con.prepareStatement(sql);

            Validacoes val = new Validacoes();
            if (val.isCPF(String.valueOf(cpf))){
                pstm.setLong(1, cpf);
            }
            pstm.setString(2, usuario);
            pstm.setString(3, email);
            pstm.setString(4, senha);
            pstm.setString(5, telefone);
            pstm.setString(6, tipoUser);
            pstm.setInt(7, idCred);
            pstm.execute();

            JOptionPane.showMessageDialog(null, "Cadastro realizado!", "Cadastro", JOptionPane.INFORMATION_MESSAGE);
            this.setFlagc(1);
            
        } catch (SQLException e) {
            //JOptionPane.showMessageDialog(null, "Verifique suas informações e tente novamente!", "Inforrmações inválidas!", JOptionPane.ERROR_MESSAGE);
            System.out.println(e);
        }
    }
}
