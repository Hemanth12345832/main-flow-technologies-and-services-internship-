import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mahan
 */
public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/mysql?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root"; // your MySQL username
        String password = "hemuboss$12345"; // your MySQL password

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println(" Connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println(" Connection failed!");
            e.printStackTrace();
        }
    }
}
