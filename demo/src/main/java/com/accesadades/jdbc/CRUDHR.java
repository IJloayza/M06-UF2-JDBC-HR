package com.accesadades.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class CRUDHR {
    private Connection connection;
    public CRUDHR(Connection conn){
        connection = conn;
    }

    public boolean CreateDatabase(InputStream input) 
    throws IOException, ConnectException, SQLException {

        boolean dupRecord = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
            StringBuilder sqlStatement = new StringBuilder();
            String line;

            try (Statement statement = connection.createStatement()) {
                while ((line = br.readLine()) != null) {
                // Ignorar comentaris i línies buides
                    line = line.trim();
                        
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("//") || line.startsWith("/*")) {
                            continue;
                    }

                    // Acumular la línea al buffer
                    sqlStatement.append(line);

                    // el caràcter ";" es considera terminació de sentència SQL
                    if (line.endsWith(";")) {
                    // Eliminar el ";" i executar la instrucción
                        String sql = sqlStatement.toString().replace(";", "").trim();
                        statement.execute(sql);

                        // Reiniciar el buffer para la siguiente instrucción
                        sqlStatement.setLength(0);
                    }
                }
            } catch (SQLException sqle) {
                if (!sqle.getMessage().contains("Duplicate entry")) {
                    System.err.println(sqle.getMessage());
                    sqle.printStackTrace();
                } else {
                    dupRecord = true;
                    br.readLine();
                }
            }
        }

        return dupRecord;
    }

    public void InsertTrain(String TableName, Train train) 
    throws ConnectException, SQLException {

        String query = "INSERT INTO " + TableName + " (id, Nombre, Capacitat) VALUES (?,?,?)";

//recuperem valor inicial de l'autocommit
        boolean autocommitvalue = connection.getAutoCommit();

//el modifiquem a false
        connection.setAutoCommit(false);

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setInt(1, train.getTrainId());
            prepstat.setString(2, train.getName());
            prepstat.setInt(3, train.getCapacity());

            prepstat.executeUpdate();

//Fem el commit
            connection.commit();

            System.out.println("Tren afegit amb èxit");

//deixem l'autocommit com estava
            connection.setAutoCommit(autocommitvalue);
        
        } catch (SQLException sqle) {
            if (!sqle.getMessage().contains("Duplicate entry")) {
                System.err.println(sqle.getMessage());
            } else {
                System.out.println("Registre duplicat");
            }

            connection.rollback();
        }

    }

//Read sense prepared statements, mostra tots els registres
    public boolean ReadAllTrains(String TableName, int offset) throws ConnectException, SQLException {
        String query = "SELECT * FROM " + TableName + " LIMIT 10 OFFSET ?;";
        boolean sigue = false;
        try (PreparedStatement prep = connection.prepareStatement(query)) {
            prep.setInt(1, offset);
    
            try (ResultSet rset = prep.executeQuery()) {
                //Mirar si hi ha mès registres
                if (rset.isBeforeFirst()) {
                    int colNum = getColumnNames(rset);
    
                    if (colNum > 0) {
                        recorrerRegistres(rset, colNum);
                        sigue = true;
                    }
                }
            }
        }catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
        return sigue;
    }

    public ResultSet ReadAllTrains(String TableName) throws ConnectException, SQLException {
        String query = "SELECT * FROM " + TableName + ";";
        try (PreparedStatement prep = connection.prepareStatement(query)) {
            ResultSet rset = prep.executeQuery();
            return rset;
        }catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
            return null;
        }
    }

    public void ReadTrainsId(String TableName, int id) 
    throws ConnectException, SQLException {

        String query = "SELECT * FROM " + TableName + " WHERE id = ?";
        
        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setInt(1, id);
            ResultSet rset = prepstat.executeQuery();

            int colNum = getColumnNames(rset);

            //Si el nombre de columnes és >0 procedim a llegir i mostrar els registres
            if (colNum > 0) {

                recorrerRegistres(rset,colNum);

            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public void ReadTrainLike(String TableName, String likeString) 
    throws ConnectException, SQLException {

        String query = "SELECT * FROM " + TableName + " WHERE Nombre LIKE ?";

        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setString(1,"%" + likeString + "%");
            ResultSet rset = prepstat.executeQuery();

            int colNum = getColumnNames(rset);

            //Si el nombre de columnes és >0 procedim a llegir i mostrar els registres
            if (colNum > 0) {

                recorrerRegistres(rset,colNum);

            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }


//Aquest mètode auxiliar podria ser utileria del READ, mostra el nom de les columnes i quantes n'hi ha
    public static int getColumnNames(ResultSet rs) throws SQLException {
        
        int numberOfColumns = 0;
        
        if (rs != null) {   
            ResultSetMetaData rsMetaData = rs.getMetaData();
            numberOfColumns = rsMetaData.getColumnCount();   
        
            for (int i = 1; i < numberOfColumns + 1; i++) {  
                String columnName = rsMetaData.getColumnName(i);
                System.out.print(columnName + ", ");
            }
        }
        
        System.out.println();

        return numberOfColumns;
        
    }

    public void UpdateTrainId(String TableName, Train train) 
    throws ConnectException, SQLException {

        String query = "UPDATE "+ TableName +" SET Nombre = ?, Capacitat = ? WHERE id = ?;";
        
        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setString(1, train.getName());
            prepstat.setInt(2, train.getCapacity());
            prepstat.setInt(3, train.getTrainId());
            //executeUpdate no retorna res
            prepstat.executeUpdate();
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public void DeleteTrainId(String TableName, int id) 
    throws ConnectException, SQLException {

        String query = "DELETE FROM "+ TableName +" WHERE id = ?;";
        
        try (PreparedStatement prepstat = connection.prepareStatement(query)) {

            prepstat.setInt(1, id);

            prepstat.executeUpdate();
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
        }
    }

    public void recorrerRegistres(ResultSet rs, int ColNum) throws SQLException {
        if (!rs.isBeforeFirst()) {
            System.out.println("El ResultSet está vacío.");
            return;
        }

        while(rs.next()) {
            for(int i = 1 ; i <= ColNum; i++) {
                if(i == ColNum) {
                    System.out.println(rs.getString(i));
                } else {
            
                System.out.print(rs.getString(i)+ ", ");
                }
            } 
        }
            
    }

    public int ultimID(String tableName){
        String query= "SELECT id FROM " + tableName +" ORDER BY id DESC LIMIT 1;";
        try (PreparedStatement prepstat = connection.prepareStatement(query)) {
            ResultSet rs = prepstat.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return id;
            } else {
                return 0;
            }
        } catch (SQLException sqle) {
            System.err.println(sqle.getMessage());
            return 0;
        }
    }
        
}
