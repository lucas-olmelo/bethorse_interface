package com.bethorse.ig_bethorse;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

public class AddCred {
    
    int idCred = 0;
    float saldo = 0;
    DecimalFormat df = new DecimalFormat("R$ #,###.00");
    
    public void Adicionar(Connection con, String email, float valor){
        
        try{
            saldo = this.pegarSaldo(con, email);
            saldo += valor;
            
            String idcr = "UPDATE creditos SET saldo = ? WHERE idCredito = ?";
            PreparedStatement stat = con.prepareStatement(idcr);
            stat.setFloat(1, saldo);
            stat.setInt(2, idCred);
            stat.execute();
        }
            
        catch (HeadlessException | SQLException e){
            JOptionPane.showMessageDialog(null, "Erro: " +e, "Erro!!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void Remover(Connection con, String email, float valor){
        
        try{
            saldo = this.pegarSaldo(con, email);
            saldo -= valor;
            
            String idcr = "UPDATE creditos SET saldo = ? WHERE idCredito = ?";
            PreparedStatement stat = con.prepareStatement(idcr);
            stat.setFloat(1, saldo);
            stat.setInt(2, idCred);
            stat.execute();
            JOptionPane.showMessageDialog(null, "Foram gastos " +df.format(valor) +" de créditos na sua conta.\nSeu saldo agora é de " +df.format(saldo), "´Créditos gastos", JOptionPane.INFORMATION_MESSAGE);
        }
            
        catch (HeadlessException | SQLException e){
            JOptionPane.showMessageDialog(null, "Erro: " +e, "Erro!!", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public float pegarSaldo(Connection con, String email){
        try{
            String query = "SELECT IdCredito FROM usuario WHERE email = ?;";
            
            PreparedStatement stat = con.prepareStatement(query);
            stat.setString(1, email);
            ResultSet result  = stat.executeQuery();
            while (result.next()){
                idCred = result.getInt(1);
            }
            String qsal = "SELECT saldo FROM creditos WHERE idCredito = ?;";
            stat = con.prepareStatement(qsal);
            stat.setInt(1, idCred);
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                saldo = rs.getInt(1);
            }
            
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Erro: " +e, "Erro!!", JOptionPane.ERROR_MESSAGE);
        }
        return saldo;
    }
}
