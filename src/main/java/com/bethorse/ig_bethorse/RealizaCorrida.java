package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;


public class RealizaCorrida {
    
    ConexaoBD conexao = new ConexaoBD();
    Connection con = conexao.connectionMySql();
    
    DefaultListModel model = new DefaultListModel();
    String corrida;

    public String getCorrida() {
        return corrida;
    }

    public void setCorrida(String corrida) {
        this.corrida = corrida;
    }

    public DefaultListModel getModel() {
        return model;
    }

    public void setModel(DefaultListModel model) {
        this.model = model;
    }
    
    DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    
    public ArrayList pegarCavalos(String corrida){
        ArrayList cavalos = new ArrayList();
        try {
            
            String sqlID = "SELECT idCorrida FROM corrida WHERE nomeCorrida = ?";
            PreparedStatement ps = con.prepareStatement(sqlID);
            ps.setString(1, corrida);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()){
                int idCor = rs.getInt("idCorrida");
                
                String sqlCavs = "SELECT idCavalo FROM cavalo_corrida WHERE idCorrida = ?";
                ps = con.prepareStatement(sqlCavs);
                ps.setInt(1, idCor);
                ResultSet res = ps.executeQuery();
                
                while (res.next()){
                    int idCav = res.getInt("idCavalo");
                    String nomes = "SELECT nomeCavalo From Cavalo where idCavalo = ?";
                    ps = con.prepareStatement(nomes);
                    ps.setInt(1, idCav);
                    ResultSet r = ps.executeQuery();
                    while(r.next()){
                        String nome = r.getString("nomeCavalo");
                        cavalos.add(nome);
                    }
                }
            }
            
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return cavalos;
    }
    
    public ArrayList ranking(String corrida){
        Aposta ap = new Aposta();
        
        ArrayList rank = new ArrayList();
        rank = pegarCavalos(corrida);
        Collections.shuffle(rank);
        
        for (int i = 0; i < 8; i++){
            getModel().addElement((i + 1) +"º - " +rank.get(i));
        }
        
        setCorrida(corrida);
        ap.saldoCorrida(corrida);
        returnSaldo(rank);
        perdedores(rank);
        
        try{
            String estCorrida = "UPDATE corrida SET statusCorrida = 'Concluída' WHERE nomeCorrida = ?";
            PreparedStatement ps = con.prepareStatement(estCorrida);
            ps.setString(1, corrida);
            ps.execute();
        } catch (SQLException e){
            System.out.println(e);
        }
        
        return rank;
    }
    
    public void returnSaldo(ArrayList rank){
        
        AddCred ac = new AddCred();
        Aposta apost = new Aposta();
        
        getModel().addElement(" ");
        
        try{
            String sql = "SELECT * FROM corrida WHERE nomeCorrida = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, getCorrida());
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                int idCorrida = rs.getInt("idCorrida");
                float valorTotal = rs.getFloat("valorTotal");
                
                String req = "SELECT * FROM cavalo WHERE nomeCavalo = ?;";
                ps = con.prepareStatement(req);
                ps.setString(1, (String) rank.get(0));
                ResultSet res = ps.executeQuery();

                while(res.next()){
                    int horse = res.getInt("idCavalo");
                    String quantVit = res.getString("qtdeVitoria");
                    long cpfDono = res.getLong("cpfDono");
                    
                    String cc = "SELECT * FROM CAVALO_CORRIDA WHERE idCorrida = ? and idCavalo = ?";
                    ps = con.prepareStatement(cc);
                    ps.setInt(1, idCorrida);
                    ps.setInt(2, horse);
                    ResultSet kk = ps.executeQuery();
                    
                    while(kk.next()){
                        int idCc = kk.getInt("idCAVALO_CORRIDA");
                        
                        String ap = "SELECT * FROM APOSTA WHERE idCc = " + idCc;
                        ps = con.prepareStatement(ap);
                        ResultSet result = ps.executeQuery();
                        
                        while(result.next()){
                            long cpfAp = result.getLong("cpfApostador");
                            float valor = result.getFloat("valor");
                            
                            String pegarAp = "SELECT * FROM apostador WHERE cpfApostador = ?";
                            ps = con.prepareStatement(pegarAp);
                            ps.setLong(1, cpfAp);
                            ResultSet rset = ps.executeQuery();

                            while (rset.next()){
                                String email = rset.getString("emailAp");
                                int tipo = rset.getInt("idtipoUser");

                                String dados = apost.getDados((String) rank.get(0));
                                float odd = apost.calcularOdds(dados);
                                
                                getModel().addElement("Apostas vencedoras: " +cpfAp +" --> " +df.format(valor) +", " +df.format(valor*odd));
                                
                                int vit = Integer.parseInt(quantVit);
                                vit += 1;

                                String sqlVit = "UPDATE cavalo SET qtdeVitoria = ? WHERE idCavalo = ?";
                                ps = con.prepareStatement(sqlVit);
                                ps.setInt(1, vit);
                                ps.setInt(2, horse);
                                ps.execute();
                                
                                ac.Adicionar(con, email, valor*odd);
                            }
                        }
                        double lucro = valorTotal * 0.3;
                                
                        String pegarDono = "SELECT * FROM dono WHERE cpfDono = ?";
                        ps = con.prepareStatement(pegarDono);
                        ps.setLong(1, cpfDono);
                        ResultSet thiagoAmici = ps.executeQuery();

                        while (thiagoAmici.next()){
                            String emailDn = thiagoAmici.getString("emailDono");
                            String nomeDn = thiagoAmici.getString("nomeDono");
                            ac.Adicionar(con, emailDn, (float) lucro);
                            getModel().addElement("Dono vencedor: " +nomeDn +" (" +cpfDono +") " +df.format(lucro));

                        }
                    }
                }
            }
        }catch(NumberFormatException | SQLException e){
            System.out.println(e);
        }
    }
    
    public void perdedores(ArrayList rank){
        
        try{
            float valorTotal = 0;
            
            String sql = "SELECT * from Corrida where nomeCorrida = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, getCorrida());
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                valorTotal = rs.getFloat("valorTotal");
                
                for (int i = 1; i < 8; i++){
                    String req = "SELECT * FROM cavalo WHERE nomeCavalo = ?;";
                    ps = con.prepareStatement(req);
                    ps.setString(1, (String) rank.get(i));
                    ResultSet res = ps.executeQuery();

                    while(res.next()){
                        long cpfDn = res.getLong("cpfDono");
                        
                        double lucro = 0;
                        
                        switch (i) {
                            case 1:
                                lucro = valorTotal * 0.15;
                                break;
                            case 2:
                                lucro = valorTotal * 0.1;
                                break;
                            default:
                                lucro = valorTotal * 0.05;
                                break;
                        }
                        String pegarDono = "SELECT * FROM dono WHERE cpfDono = ?";
                        ps = con.prepareStatement(pegarDono);
                        ps.setLong(1, cpfDn);
                        ResultSet thiagoAmici = ps.executeQuery();

                        while (thiagoAmici.next()){
                            String emailDn = thiagoAmici.getString("emailDono");
                            String nomeDn = thiagoAmici.getString("nomeDono");
                            
                            AddCred ac = new AddCred();
                            ac.Adicionar(con, emailDn, (float) lucro);
                            getModel().addElement("Dono " +(i + 1) +"º lugar: " +nomeDn +" (" +cpfDn +") " +df.format(lucro));
                        }
                    }
                }
            }
            double lucro_bh = valorTotal * 0.2;
            JOptionPane.showMessageDialog(null, "Todos os saldos foram adicionados aos seus respectivos donos."
                    + "\nLucro da BetHorse (Os incríveis Lins, Melo e Pedro): " +df.format(lucro_bh), "Sucesso!", 
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e){
            System.out.println(e);
        }
    }
}
