package Data;

import SharedClasses.LoginMessageTCP;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ClientTimeController extends Thread{

    private Statement stmt;
    private String username;
    private ResultSet rs;

    public ClientTimeController(Connection conn, String username){

        this.username = username;

        try{
            stmt = conn.createStatement(); //é a partir deste statement que se faz os comandos
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        start();
    }


    public void run(){

        while(true){

            try {
                Calendar time;

                time = GregorianCalendar.getInstance();

                rs = stmt.executeQuery("SELECT TimeStamp_Online FROM utilizador WHERE Username = \"" + username + "\";");
                rs.next();
                Timestamp Time_Online = rs.getTimestamp("TimeStamp_Online");

                if(time.getTimeInMillis() - Time_Online.getTime() >= 30000) // inativo
                {
                    stmt.executeUpdate("UPDATE utilizador SET Flag_Online = 0 WHERE Username = \"" + username + "\";");
                }


                Thread.sleep(30000);

            } catch (InterruptedException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
