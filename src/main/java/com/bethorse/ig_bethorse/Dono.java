package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class Dono {
    
    int flagc = 0;
    int flagj = 0;

    public int getFlagc() {
        return flagc;
    }

    public void setFlagc(int flagc) {
        this.flagc = flagc;
    }

    public int getFlagj() {
        return flagj;
    }

    public void setFlagj(int flagj) {
        this.flagj = flagj;
    }
    
    
    public void cadastroJoquei(Connection con, long cpf, String nome, int idade, String fone, String email, float peso, float altura, long cpfDono){
        try{
            String sql = "INSERT INTO joquei VALUES(?,?,?,?,?,?,?,?)";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setLong(1, cpf);
            pstm.setString(2, nome);
            pstm.setInt(3, idade);
            pstm.setString(4, fone);
            pstm.setString(5, email);
            pstm.setFloat(6, peso);
            pstm.setFloat(7, altura);
            pstm.setLong(8, cpfDono);
            pstm.execute();
            JOptionPane.showMessageDialog(null, "Dados inseridos com sucesso!!!", "Cadastro de jóqueis", JOptionPane.INFORMATION_MESSAGE);
            this.setFlagj(1);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro!!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
            this.setFlagj(0);
        }
    } 
    
    public void cadastroCavalo(Connection con, String nome, int idade, float peso, long cpfDono, int raca){
        try{
            String sql = "INSERT INTO Cavalo VALUES(NULL, ?,?,?,0,?,?)";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, nome);
            pstm.setInt(2, idade);
            pstm.setFloat(3, peso);
            pstm.setLong(4, cpfDono);
            pstm.setInt(5, raca);
            
            pstm.execute();
            JOptionPane.showMessageDialog(null, "Dados inseridos com sucesso!!!", "Cadastro de cavalos", JOptionPane.INFORMATION_MESSAGE);
            this.setFlagc(1);
            
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro!!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
            this.setFlagc(0);
        }
    }
    
    public void trocarSenha(Connection con, long cpf, String oldSenha, String senha){
        try{
            if((oldSenha.equals(senha)) || senha.equals("".trim())){
                JOptionPane.showMessageDialog(null, "Invalidade na nova senha", "Erro!", JOptionPane.ERROR_MESSAGE);
            }else{
                String sql = "UPDATE usuario SET senha = '"+senha+"' where cpf = " + cpf + ";";
                PreparedStatement pstm = con.prepareStatement(sql);
                pstm.executeUpdate();
                JOptionPane.showMessageDialog(null, "Senha alterada com sucesso!");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Erro!!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public String mostrarSenha(Connection con, long cpf, String senha){
        String newsenha = "";
        try{
            String query = "SELECT senha FROM usuario where cpf = ?";
            PreparedStatement ptsm = con.prepareStatement(query);
            ptsm.setLong(1, cpf);
            ResultSet rs = ptsm.executeQuery();
            if(rs.next()){
                newsenha = rs.getString("senha");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Erro!!\n" + e, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
        return newsenha;
    }
    
    public int idCavalo(Connection con, long cpf, String cavalo){
        int idCav = 0;
        try{
            String sql = "SELECT * FROM Cavalo where cpfDono = ? and nomeCavalo = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setLong(1, cpf);
            pstm.setString(2, cavalo);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                idCav = rs.getInt("idCavalo");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Erro!!\n" + e, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
        return idCav;
    }
    
    public int idCorrida(Connection con, String nome){
        int idCor = 0;
        try{
            String sql = "SELECT * FROM CORRIDA WHERE NOMECORRIDA = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, nome);
            ResultSet rs = pstm.executeQuery();
            while(rs.next()){
                idCor = rs.getInt("idCorrida");
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Erro!!\n" + e, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
        return idCor;
    }
    
    public void entrarCorrida(Connection con, int cav, int cor){
        try{
            String sql = "INSERT INTO CAVALO_CORRIDA VALUES(NULL, ?, ?)";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setInt(1, cav);
            pstm.setInt(2, cor);
            pstm.execute();
            JOptionPane.showMessageDialog(null, "Inscrição aceita");
            
            String count = "SELECT count(idCorrida) FROM cavalo_corrida WHERE idCorrida = ?";
            pstm = con.prepareStatement(count);
            pstm.setInt(1, cor);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                int quant = rs.getInt("count(idCorrida)");
                
                if (quant >= 8){
                    String update = "UPDATE corrida SET statusCorrida = 'Fechada' WHERE idCorrida = ?";
                    pstm = con.prepareStatement(update);
                    pstm.setInt(1, cor);
                    pstm.execute();
                }
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null,"Erro!!\n" + e, "Erro!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public float showGastos(String tipo){
        float valor = 0;
        switch(tipo){
            case "Amadora":
                valor = 50;
            case "Profissional Amistoso":
                valor = 100;
            case "Profisional Eliminatório":
                valor = 500;
            case "Quartas de Final":
                valor = 1000;
            case "Semifinal":
                valor = 2000;
            case "Final de Campeonato":
                valor = 5000;
        }
        return valor;
    }
    
    public DefaultListModel cavalosCorrida(String race){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        DefaultListModel model = new DefaultListModel();
        int idCor = idCorrida(con, race);
        
        try{
            String sql = "SELECT idCavalo FROM cavalo_corrida WHERE idCorrida = ?;";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setInt(1, idCor);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()){
                int idCav = rs.getInt("idCavalo");
                String req = "SELECT * FROM cavalo WHERE idCavalo = ?;";
                PreparedStatement ps = con.prepareStatement(req);
                ps.setInt(1, idCav);
                ResultSet result = ps.executeQuery();
                while (result.next()){
                    String nome = result.getString("nomeCavalo");
                    String idade = result.getString("idade");
                    String peso = result.getString("pesoCavalo");
                    String vit = result.getString("qtdeVitoria");
                    int raca = result.getInt("idRaca");

                    String query = "SELECT nomeRaca FROM raca WHERE idRaca = ?;";
                    PreparedStatement stat = con.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, 
                            ResultSet.CONCUR_UPDATABLE);
                    stat.setInt(1, raca);
                    ResultSet res = stat.executeQuery();
                    String racaCav = "";
                    if (res.last()){
                        racaCav = res.getString("nomeRaca");
                    }
                    String cavalo = nome + " --> " +idade + " anos, " +peso + "kg, " +vit + " vitórias, " +racaCav;
                    model.addElement(cavalo);
                }
            }
        } catch (Exception e){
            System.out.println(e);
        }
        return model;
    }
    
    public boolean verificaCorrida(int id){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        boolean statusb = false;
        
        try{
            String sql = "SELECT statusCorrida FROM corrida WHERE idCorrida = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet result = ps.executeQuery();
            
            while (result.next()){
                String status = result.getString("statusCorrida");
                
                if (status == "Aberta"){
                    statusb =  true;
                } else {
                    statusb =  false;
                }
            }
        } catch (SQLException e){
            System.out.println(e);
        }
        System.out.println(statusb);
        return statusb;
    }
}
                
