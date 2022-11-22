package com.bethorse.ig_bethorse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class BuscarInfo {

    //Método que busca os cavalos de um dono no BD, e retorna um ListModel para o jList
    public DefaultListModel pegarCavalos(long cpf){
        
        String[] cav = new String[5];
        String cavalo = "";
        
        DefaultListModel model = new DefaultListModel();
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        try{
            String req = "SELECT * FROM cavalo WHERE cpfDono = ?;";
            PreparedStatement pstm = con.prepareStatement(req);
            pstm.setLong(1, cpf);
            
            ResultSet rs = pstm.executeQuery();
            
            while(rs.next()){
                cav[0] = rs.getString("nomeCavalo");
                cav[1] = rs.getString("idade");
                cav[2] = rs.getString("pesoCavalo");
                cav[3] = rs.getString("qtdeVitoria");
                int raca = rs.getInt("idRaca");
                
                String sql = "SELECT nomeRaca FROM raca WHERE idRaca = ?;";
                PreparedStatement stat = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                stat.setInt(1, raca);
                ResultSet res = stat.executeQuery();
                if (res.last()){
                    cav[4] = res.getString("nomeRaca");
                }
                
                cavalo = cav[0] + " --> " +cav[1] + " anos, " +cav[2] + "kg, " +cav[3] + " vitórias, " +cav[4];
                
                model.addElement(cavalo);
            }
            
        } catch (SQLException e){
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "CPF inválido");
        }
        cbd.closeConnectionMySql(con);
        return model;
    }
    
    //Método que busca os três cavalos com mais vitórias de um dono no BD e retorna um ListModel para o jList
    public DefaultListModel top3Cavalos(long cpf){
        
        String[] cav = new String[4];
        String cavalo = "";
        
        DefaultListModel model = new DefaultListModel();
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        try{
            String req = "select * from cavalo where cpfDono = ? order by qtdeVitoria desc limit 3;";
            PreparedStatement pstm = con.prepareStatement(req);
            pstm.setLong(1, cpf);
            
            ResultSet rs = pstm.executeQuery();
            
            while(rs.next()){
                cav[0] = rs.getString("nomeCavalo");
                cav[1] = rs.getString("qtdeVitoria");
                
                String nomeDono = "SELECT nomeDono FROM dono WHERE cpfDono = ?";
                PreparedStatement stat = con.prepareStatement(nomeDono, ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                stat.setLong(1, cpf);
                ResultSet res = stat.executeQuery();
                if (res.last()){
                    cav[2] = res.getString("nomeDono");
                }
                
                int raca = rs.getInt("idRaca");
                
                String sql = "select nomeRaca from raca where idRaca = ?;";
                PreparedStatement st = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                st.setInt(1, raca);
                ResultSet result = st.executeQuery();
                if (result.last()){
                    cav[3] = result.getString("nomeRaca");
                }
                
                cavalo = cav[0] + " (" + cav[3] +") --> " +cav[1] + " vitórias. Dono: " +cav[2]+"\n";
                model.addElement(cavalo);
            }
            
        } catch (SQLException e){
            System.out.println(e);
        }
        cbd.closeConnectionMySql(con);
        return model;
    }
    
    //Método que busca os jóqueis de um dono no BD e retorna um ListModel para o jList
    public DefaultListModel buscarJoqueis(long cpf){
        
        String joquei = "";
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        DefaultListModel model = new DefaultListModel();
        
        try{
            String req = "SELECT * FROM joquei WHERE cpfDono = ?";
            PreparedStatement pstm = con.prepareStatement(req);
            
            pstm.setLong(1, cpf);
            
            ResultSet rs = pstm.executeQuery();
            
            while(rs.next()){
                
                String nome = rs.getString("nomeJoquei");
                int idade = rs.getInt("idade");
                float peso = rs.getFloat("pesoJoq");
                float altura = rs.getFloat("altura");
                
                joquei = nome + " -->" + idade + " anos, " + peso +"kg, " + altura + "m";
                model.addElement(joquei);
            }

        } catch(SQLException e){
            System.out.println(e);
        }
        cbd.closeConnectionMySql(con);
        return model;
    }
    
    //Método que busca as corridas marcadas no BD e retorna um ListModel para o jList
    public DefaultListModel buscarCorridas(String status){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        DefaultListModel model = new DefaultListModel();
        String correr = "";
        
        try{
            String query;
            PreparedStatement pstm;
            
            if (status.equals("")){
                query = "SELECT * FROM corrida";
                pstm = con.prepareStatement(query);
            } else {
                query = "SELECT * FROM corrida WHERE statusCorrida = ?;";
                pstm = con.prepareStatement(query);
                pstm.setString(1, status);
            }
            
            ResultSet rs = pstm.executeQuery();
            
            while(rs.next()){
                
                String nomeLocal = "";
                String tipoC = "";
                
                String nome = rs.getString("nomeCorrida");
                String distancia = rs.getString("distancia");
                int local = rs.getInt("idLocal");
                
                String sql = "SELECT nomeLocal FROM lugar WHERE idLocal = ?;";
                PreparedStatement st = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                st.setInt(1, local);
                ResultSet result = st.executeQuery();
                if (result.last()){
                    nomeLocal = result.getString("nomeLocal");
                }
                
                int tipo = rs.getInt("idTipo");
                
                String req = "SELECT tipoCorrida FROM tipo_corrida WHERE idTipo = ?;";
                PreparedStatement ps = con.prepareStatement(req, ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                ps.setInt(1, tipo);
                ResultSet res = ps.executeQuery();
                if (res.last()){
                    tipoC = res.getString("tipoCorrida");
                }
                
                correr = nome + " -->" + distancia + ", " + tipoC + ", " + nomeLocal;
                model.addElement(correr);
            }
        } catch (SQLException e){
            System.out.println(e);
        }
        cbd.closeConnectionMySql(con);
        return model;
    }
    
    //Método que busca os locais das corridas no BD e retorna um ListModel para o jList
    public DefaultListModel buscarLocais(){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        DefaultListModel model = new DefaultListModel();
        
        try{
            
            String sql = "SELECT * FROM lugar;";
            PreparedStatement st = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = st.executeQuery();
            while (rs.last()){
                model.addElement(rs.getString("nomeLocal"));
            }
            
        } catch (SQLException e){
            System.out.println(e);
        }
        cbd.closeConnectionMySql(con);
        return model;
    }
    
    //Método que busca as apostas feitas por um apostador no BD e retorna um ListModel para o jList
    //TODO alterar para nova tabela
    public DefaultListModel getApostas(String nome){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        DefaultListModel model = new DefaultListModel();
        
        try{
            
            String req = "SELECT cpf FROM usuario WHERE nome = ?";
            PreparedStatement st = con.prepareStatement(req);
            st.setString(1, nome);
            ResultSet result = st.executeQuery();
            
            while(result.next()){
                long cpf = result.getLong("cpf");
                
                String sql = "SELECT * FROM aposta WHERE cpfApostador = ?;";
                st = con.prepareStatement(sql);
                st.setLong(1, cpf);
                ResultSet rs = st.executeQuery();

                while (rs.next()){
                    int idCc = rs.getInt("idCc");
                    float valor = rs.getFloat("valor");
                    
                    String sqlCc = "SELECT idCorrida, idCavalo FROM cavalo_corrida WHERE idCavalo_corrida = ?";
                    st = con.prepareStatement(sqlCc);
                    st.setLong(1, idCc);
                    ResultSet reset = st.executeQuery();
                    
                    while (reset.next()){
                        int idCor = reset.getInt("idCorrida");
                        int idCav = reset.getInt("idCavalo");
                        
                        String query = "SELECT nomeCorrida, idTipo FROM corrida WHERE idCorrida = ?;";
                        st = con.prepareStatement(query);
                        st.setLong(1, idCor);
                        ResultSet res = st.executeQuery();

                        while (res.next()){
                            String corrida = res.getString("nomeCorrida");
                            int id = res.getInt("idTipo");
                    
                            String type = "SELECT * FROM tipo_corrida WHERE idtipo = " + id;
                            PreparedStatement ps = con.prepareStatement(type);
                            ResultSet kk = ps.executeQuery();

                            while(kk.next()){
                                String nomeTipo = kk.getString("tipoCorrida");
                                
                                String sqlCav = "SELECT nomeCavalo FROM cavalo WHERE idCavalo = ?;";
                                st = con.prepareStatement(sqlCav);
                                st.setLong(1, idCav);
                                ResultSet rset = st.executeQuery();

                                while (rset.next()){
                                    String cavalo = rset.getString("nomeCavalo");

                                    String aposta = corrida +" (" +nomeTipo +") --> " + cavalo + ", " +df.format(valor);
                                    model.addElement(aposta);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e){
            System.out.println(e);
        }
        return model;
    }
    
    DecimalFormat df = new DecimalFormat("R$ #,##0.00");
    
    //Método que busca as apostas feitas em uma corrida no BD e retorna um ListModel para o jList
    public DefaultListModel getApostasCorridas(String nome){
        
        ConexaoBD cbd = new ConexaoBD();
        Connection con = cbd.connectionMySql();
        
        String [] corName = nome.split(" -->");
        
        String race = corName[0];
        
        DefaultListModel model = new DefaultListModel();
        
        try{
            
            String req = "SELECT idCorrida FROM corrida WHERE nomeCorrida = ?";
            PreparedStatement st = con.prepareStatement(req);
            st.setString(1, race);
            ResultSet result = st.executeQuery();
            
            while(result.next()){
                int idCor = result.getInt("idCorrida");
                
                String sql = "SELECT idCavalo_corrida, idCavalo FROM cavalo_corrida WHERE idCorrida = ?;";
                st = con.prepareStatement(sql);
                st.setLong(1, idCor);
                ResultSet rs = st.executeQuery();

                while (rs.next()){
                    int idCc = rs.getInt("idCavalo_corrida");
                    int idCav = rs.getInt("idCavalo");
                    
                    String query = "SELECT * FROM aposta WHERE idCc = ?;";
                    st = con.prepareStatement(query);
                    st.setLong(1, idCc);
                    ResultSet res = st.executeQuery();
                    
                    while (res.next()){
                        long cpf = res.getLong("cpfApostador");
                        float valor = res.getFloat("valor");

                        String sqlCav = "SELECT nomeCavalo, cpfDono FROM cavalo WHERE idCavalo = ?;";
                        st = con.prepareStatement(sqlCav);
                        st.setLong(1, idCav);
                        ResultSet reset = st.executeQuery();
                        
                        while (reset.next()){
                            String cavalo = reset.getString("nomeCavalo");
                            long cpfd = reset.getLong("cpfDono");
                            
                            String aposta = cpf +" --> " +cavalo +"(" +cpfd +"), " +df.format(valor);
                            model.addElement(aposta);
                        }
                    }
                }
            }
        } catch (SQLException e){
            System.out.println(e);
        }
        return model;
    }
    
}
