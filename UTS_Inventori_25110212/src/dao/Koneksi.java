package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

public class Koneksi {
    private static final String URL = "jdbc:mysql://localhost:3306/db_inventoridesnita";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                    "Driver MySQL (mysql-connector-java) belum ditambahkan ke Libraries proyek.",
                    "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Tidak dapat terhubung ke database db_inventori.\n"
                    + "Pastikan MySQL (XAMPP) sudah berjalan dan skrip database sudah dijalankan.\n\n"
                    + "Detail: " + e.getMessage(),
                    "Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
}