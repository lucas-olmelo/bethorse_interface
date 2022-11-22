package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

public class Aposta {
    
    public String getDados(String cavalo){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        String dados = "";
        
        try{
            
            String sql = "SELECT idCavalo FROM cavalo WHERE nomeCavalo = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cavalo);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                int idCav = rs.getInt("idCavalo");
                
                String query = "SELECT * FROM cavalo WHERE idCavalo = ?";
                ps = con.prepareStatement(query);
                ps.setInt(1, idCav);
                ResultSet res = ps.executeQuery();
                
                while(res.next()){
                    int idade = res.getInt("idade");
                    String peso = res.getString("pesoCavalo");
                    String vit = res.getString("qtdeVitoria");
                    dados = idade + ", " +peso +", " +vit;
                }
            }
            
        } catch (Exception e ){
            System.out.println(e);
        }
        return dados;
    }
    
    public float calcularOdds(String dados){
        float porcent = 0;
        float odds = 0;
        String[] cav = dados.split(", ");
        
        int idade = Integer.parseInt(cav[0]);
        float peso = Float.parseFloat(cav[1]);
        int vit = Integer.parseInt(cav[2]);
        if(idade < 11 && idade >=5){
            porcent = 5;
        }
        else if(idade < 21 && idade >=11){
            porcent = 15;
        }
        else if(idade >=21){
            porcent = 8;
        }
        
        if(peso>=200 && peso < 400){
            porcent += 15;
        }
        else if(peso < 500 && peso >=400){
            porcent += 10;
        }
        else if(peso >=500){
            porcent += 5;
        }
        //vitorias
        vit *= 5;
        porcent +=vit;
        odds = 100/porcent;
        return odds;
    }
    
    DecimalFormat df = new DecimalFormat("0.00");
    
    public void criarAposta(long cpf, float valor, String corrida, String cavalo){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        String [] corName = corrida.split(" -->");
        
        String race = corName[0];
        
        try {
            
            String query = "SELECT idCorrida FROM corrida WHERE nomeCorrida = ?;";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, race);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                int idCor = rs.getInt("idCorrida");
                
                String req = "SELECT idCavalo FROM cavalo WHERE nomeCavalo = ?;";
                ps = con.prepareStatement(req);
                ps.setString(1, cavalo);
                ResultSet res = ps.executeQuery();
            
                while(res.next()){
                    int idCav = res.getInt("idCavalo");

                    String sql = "SELECT idCavalo_Corrida FROM CAVALO_CORRIDA WHERE idCorrida = ? AND idCavalo = ?;";
                    ps = con.prepareStatement(sql);
                    ps.setInt(1, idCor);
                    ps.setInt(2, idCav);
                    ResultSet result = ps.executeQuery();
                    
                    while(result.next()){
                        int idCc = result.getInt("idCavalo_Corrida");
                        String insere = "INSERT INTO aposta VALUES(null, ?, ?, ?);";
                        ps = con.prepareStatement(insere);
                        ps.setLong(1, cpf);
                        ps.setInt(2, idCc);
                        ps.setFloat(3, valor);
                        ps.execute();
                    }
                }
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }
    
    public void saldoCorrida(String nome){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        float total = 0;
        
        try{
            String sql = "SELECT idCorrida FROM Corrida WHERE nomeCorrida = ?";
            PreparedStatement pstm = con.prepareStatement(sql);
            pstm.setString(1, nome);
            ResultSet rs = pstm.executeQuery();
            
            while (rs.next()){
                int id = rs.getInt("idCorrida");
                String cc = "SELECT idCavalo_Corrida FROM CAVALO_CORRIDA WHERE idCorrida = " + id;
                pstm = con.prepareStatement(cc);
                ResultSet res = pstm.executeQuery();
                
                while(res.next()){
                    int cod = res.getInt("idCavalo_Corrida");
                    String ap = "SELECT VALOR FROM APOSTA WHERE idCc = "+cod;
                    pstm = con.prepareStatement(ap);
                    ResultSet set = pstm.executeQuery();
                    
                    while(set.next()){
                        total += set.getFloat("valor");
                        String up = "UPDATE corrida SET VALORTOTAL = ? WHERE idCorrida = ?";
                        pstm = con.prepareStatement(up);
                        pstm.setFloat(1, total);
                        pstm.setInt(2, id);
                        pstm.execute();
                    }
                }
            }
        }catch(SQLException e){
            System.out.println(e);
        }
    }
}
