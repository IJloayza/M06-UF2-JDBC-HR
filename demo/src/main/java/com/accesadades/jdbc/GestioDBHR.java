package com.accesadades.jdbc;

import java.io.IOException;
import java.io.InputStream;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class GestioDBHR {
//Com veurem, aquesta booleana controla si volem sortir de l'aplicació.
    static boolean sortirapp = false;
        
        public static void main(String[] args) {

        // Carregar propietats des de l'arxiu
        Properties properties = new Properties();
        try (InputStream input = GestioDBHR.class.getClassLoader().getResourceAsStream("config.properties")) {
        //try (FileInputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);

            // Obtenir les credencials com a part del fitxer de propietats
            String dbUrl = properties.getProperty("db.url");
            String dbUser = properties.getProperty("db.username");
            String dbPassword = properties.getProperty("db.password");

            // Conectar amb MariaDB
            try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                System.out.println("Conexió exitosa");

                String File_create_script = "db_scripts/DB_Schema_HR.sql" ;

                InputStream input_sch = GestioDBHR.class.getClassLoader().getResourceAsStream(File_create_script);

                CRUDHR crudbhr = new CRUDHR(connection);
                //Aquí farem la creació de la database i de les taules, a més d'inserir dades
                crudbhr.CreateDatabase(input_sch);
                while (sortirapp == false) {
                    MenuOptions(crudbhr,connection);
                }

            } catch (Exception e) {
                System.err.println("Error al conectar: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error al carregar fitxer de propietats: " + e.getMessage());
        }
    }

    public static void MenuOptions(CRUDHR crudbhr, Connection connection) 
    throws NumberFormatException, IOException, SQLException, InterruptedException {

        Terminal terminal = TerminalBuilder.builder().system(true).build();

        String message = "";
        message = "==================";
        printScreen(terminal, message);

        message = "CONSULTA BD Renfe";
        printScreen(terminal, message);

        message = "==================";
        printScreen(terminal, message);


        message = "OPCIONS";
        printScreen(terminal, message);

        message = "1. CARREGAR TAULA";
        printScreen(terminal, message);

        message = "2. CONSULTAR TOTES LES DADES";
        printScreen(terminal, message);

        message = "3. ALTRES CONSULTES";
        printScreen(terminal, message);

        message = "4. INSERIR NOU REGISTRE";
        printScreen(terminal, message);

        message = "5. MODIFICAR UN REGISTRE";
        printScreen(terminal, message);

        message = "6. ESBORRAR UN REGISTRE";
        printScreen(terminal, message);

        message = "7. GENERAR UN XML DELS REGISTRES";
        printScreen(terminal, message);

        message = "9. SORTIR";
        printScreen(terminal, message);


        message = "Introdueix l'opcio tot seguit >> ";
        for (char c : message.toCharArray()) {
            terminal.writer().print(c);
            terminal.flush();
            Thread.sleep(7);
        }

        int opcio = Integer.parseInt(Std.readLine());

        switch(opcio) {
            case 1:
                String File_data_script = "db_scripts/DB_Data_HR.sql" ;
    
                InputStream input_data = GestioDBHR.class.getClassLoader().getResourceAsStream(File_data_script);

                if (crudbhr.CreateDatabase(input_data) == true) {
                    System.out.println("Registres duplicats");
                } else {
                    System.out.println("Registres inserits amb éxit");
                }

                break;
            case 2:
                //Preguntem de quina taula volem mostrar
                MenuSelect(crudbhr,connection);
                break;

            case 3:
                MenuSelectAltres(crudbhr,connection);
                break;

            case 4:
                MenuInsert(crudbhr,connection);
                break;
            case 5:
                MenuUpdate(crudbhr, connection);
                break;
            case 6:
                MenuDelete(crudbhr, connection);
                break;
            case 7:
                
                break;
            case 9:
                //sortim
                System.out.println("Adéu!!");
                sortirapp = true;
                break;
            default:
                System.out.print("Opcio no vàlida");
                MenuOptions(crudbhr,connection);
        }
    
    }

    private static void printScreen(Terminal terminal, String message) throws InterruptedException {
        for (char c : message.toCharArray()) {
            terminal.writer().print(c);
            terminal.flush();
            Thread.sleep(10);
        }
        System.out.println();
    }

    public static void MenuSelect(CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {
        //Mostrar totes les dades de TRAINS 
        boolean dispOptions = true;
        int offset = 0;
        while (dispOptions) {

            System.out.println("Mostrant les dades a la taula TREN");
            boolean more = crudbhr.ReadAllTrains("TREN", offset);
            if(more){
                System.out.println("Vols veure els seguents 10?");
                if (Std.readLine().matches("[sS]")) {
                    offset += 10;
                }
            }else {
                System.out.println("No quedan mès registres per llegir");
                dispOptions = false;
                offset = 0;
                break;
            }
        }
    }

    public static void MenuSelectAltres(CRUDHR crudbhr,Connection connection) throws SQLException, NumberFormatException, IOException {
        //Demanar id del train que vols veure
        int opcio = 0;
        boolean dispOptions = true;
        while (dispOptions) {
            System.out.println("Quina consulta vols fer?");
            System.out.println("1. Tren per id");
            System.out.println("2. Tren per lletra del nom");
            System.out.println("3. Sortir de l'elecció");

            System.out.print("Introdueix l'opció tot seguit >> ");

            opcio = Integer.parseInt(Std.readLine());

            switch(opcio) {
                case 1:
                    System.out.println("Introdueix la id del tren >> ");
                    int idDept = Integer.parseInt(Std.readLine());
                    crudbhr.ReadTrainsId("TREN", idDept);
                    break;
                case 2:
                    System.out.println("Introdueix alguna lletra del nom del tren >> ");
                    String letterTrain = Std.readLine();
                    crudbhr.ReadTrainLike("TREN", letterTrain);
                    break;
                case 3:
                    dispOptions = false;
            }

        }

    }

    public static void MenuInsert(CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {
        //Demanar totes les dades del train
        boolean insertMore = true;

        while (insertMore) {

            boolean dadaValida = true;

            System.out.println("Introdueix els detalls del nou tren");

            int id = 0;

            while (dadaValida) {
                System.out.print("Quina és la id (PK) del tren? >> ");

                try {

                    id = Integer.parseInt(Std.readLine());

                    if (id <= 0) {
                        System.out.println("Format numèric no vàlid");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Format numèric no vàlid");
                }

            }

            System.out.print("Introdueix el nom del tren >> ");
            String name = Std.readLine();

            System.out.print("Introdueix la capacitat del nou tren >> ");
            int capacity = Integer.parseInt(Std.readLine());

            dadaValida = true;

            Train train = new Train(id, name, capacity);

            crudbhr.InsertTrain("TREN", train);

            System.out.println("Vols afegir un altre tren?");

            if (!Std.readLine().matches("[sS]")) {
                insertMore = false;
            }

        }
                            
    }

    public static void MenuUpdate(CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {
        boolean updateMore = true;
        while(updateMore){
            boolean dadaValida = true;

            System.out.println("Introdueix els detalls del tren a modificar");

            int id = 0;

            while (dadaValida) {
                System.out.print("Quina és la id (PK) del tren? >> ");

                try {

                    id = Integer.parseInt(Std.readLine());

                    if (id <= 0) {
                        System.out.println("Format numèric no vàlid");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Format numèric no vàlid");
                }
            }

            System.out.print("Introdueix el nou nom del tren >> ");
            String name = Std.readLine();

            System.out.print("Introdueix la nova capacitat del tren >> ");
            int capacity = Integer.parseInt(Std.readLine());

            dadaValida = true;

            Train train = new Train(id, name, capacity);

            crudbhr.UpdateTrainId("TREN", train);
            System.out.println("Vols modificar un altre tren?");

            if (!Std.readLine().matches("[sS]")) {
                updateMore = false;
            }
        }
    }

    public static void MenuDelete(CRUDHR crudbhr,Connection connection) 
    throws SQLException, NumberFormatException, IOException {
        boolean deleteMore = true;
        while(deleteMore){
            boolean dadaValida = true;

            System.out.println("Introdueix els detalls del tren a eliminar");

            int id = 0;

            while (dadaValida) {
                System.out.print("Quina és la id (PK) del tren? >> ");

                try {

                    id = Integer.parseInt(Std.readLine());

                    if (id <= 0) {
                        System.out.println("Format numèric no vàlid");

                    } else {
                        dadaValida = false;
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Format numèric no vàlid");
                }
            }

            crudbhr.DeleteTrainId("TREN", id);
            System.out.println("Vols eliminar un altre tren?");

            if (!Std.readLine().matches("[sS]")) {
                deleteMore = false;
            }
        }
    }
}
