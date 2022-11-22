package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DadosCavalo {
    
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
                    String nome = res.getString("nomeCavalo");
                    String peso = res.getString("pesoCavalo");
                    String vit = res.getString("qtdeVitoria");
                    dados = nome + ", " +peso +", " +vit;
                }
            }
            
        } catch (Exception e ){
            System.out.println(e);
        }
        return dados;
    }
}
