package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Barang;

public class BarangDAO {

    public boolean simpan(Barang b) {
        String sql = "INSERT INTO barang (kode_barang, nama_barang, kategori, stok) VALUES (?, ?, ?, ?)";
        try (Connection c = Koneksi.getConnection()) {
            if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, b.getKodeBarang());
                ps.setString(2, b.getNamaBarang());
                ps.setString(3, b.getKategori());
                ps.setInt(4, b.getStok());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal simpan: " + e.getMessage());
            return false;
        }
    }

    public boolean ubah(Barang b) {
        String sql = "UPDATE barang SET nama_barang = ?, kategori = ?, stok = ? WHERE kode_barang = ?";
        try (Connection c = Koneksi.getConnection()) {
            if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, b.getNamaBarang());
                ps.setString(2, b.getKategori());
                ps.setInt(3, b.getStok());
                ps.setString(4, b.getKodeBarang());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal ubah: " + e.getMessage());
            return false;
        }
    }

    public boolean hapus(String kode) {
        String sql = "DELETE FROM barang WHERE kode_barang = ?";
        try (Connection c = Koneksi.getConnection()) {
            if (c == null) return false;
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setString(1, kode);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Gagal hapus: " + e.getMessage());
            return false;
        }
    }

    public List<Barang> tampilSemua() {
        List<Barang> list = new ArrayList<>();
        String sql = "SELECT kode_barang, nama_barang, kategori, stok FROM barang ORDER BY kode_barang";
        try (Connection c = Koneksi.getConnection()) {
            if (c == null) return list;
            try (PreparedStatement ps = c.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Barang(rs.getString("kode_barang"),
                            rs.getString("nama_barang"),
                            rs.getString("kategori"),
                            rs.getInt("stok")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal baca data: " + e.getMessage());
        }
        return list;
    }
}