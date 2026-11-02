package view;

import dao.BarangDAO;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Barang;

public class FormBarang extends JFrame {

    private final JTextField txtKode = new JTextField(15);
    private final JTextField txtNama = new JTextField(15);
    private final JComboBox<String> cmbKategori =
            new JComboBox<>(new String[]{"Komputer", "Jaringan", "Alat Ukur"});
    private final JTextField txtStok = new JTextField(15);

    private final JButton btnSimpan = new JButton("Simpan");
    private final JButton btnUbah = new JButton("Ubah");
    private final JButton btnHapus = new JButton("Hapus");
    private final JButton btnBersihkan = new JButton("Bersihkan");

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Kode", "Nama Barang", "Kategori", "Stok"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable tabel = new JTable(tableModel);
    private final BarangDAO dao = new BarangDAO();

    public FormBarang() {
        setTitle("Pengelolaan Data Barang Laboratorium");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel pnlInput = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 8, 4, 8);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        tambahBaris(pnlInput, g, 0, "Kode Barang", txtKode);
        tambahBaris(pnlInput, g, 1, "Nama Barang", txtNama);
        tambahBaris(pnlInput, g, 2, "Kategori", cmbKategori);
        tambahBaris(pnlInput, g, 3, "Stok", txtStok);

        JPanel pnlTombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        pnlTombol.add(btnSimpan);
        pnlTombol.add(btnUbah);
        pnlTombol.add(btnHapus);
        pnlTombol.add(btnBersihkan);

        JPanel pnlAtas = new JPanel(new BorderLayout());
        pnlAtas.add(pnlInput, BorderLayout.CENTER);
        pnlAtas.add(pnlTombol, BorderLayout.SOUTH);

        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(pnlAtas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);

        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersihkan.addActionListener(e -> bersihkan());
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });

        tampilData();

        setSize(600, 480);
        setLocationRelativeTo(null);
    }

    private void tambahBaris(JPanel p, GridBagConstraints g, int baris, String label, JComponent comp) {
        g.gridx = 0; g.gridy = baris; g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(comp, g);
    }

    private void tampilData() {
        tableModel.setRowCount(0);
        List<Barang> list = dao.tampilSemua();
        for (Barang b : list) {
            tableModel.addRow(new Object[]{
                b.getKodeBarang(), b.getNamaBarang(), b.getKategori(), b.getStok()});
        }
    }

    private void isiFormDariTabel() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        txtKode.setText(tableModel.getValueAt(row, 0).toString());
        txtNama.setText(tableModel.getValueAt(row, 1).toString());
        cmbKategori.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        txtStok.setText(tableModel.getValueAt(row, 3).toString());
        txtKode.setEditable(false);
    }

    private Barang validasiInput() {
        String kode = txtKode.getText().trim();
        String nama = txtNama.getText().trim();
        String stokStr = txtStok.getText().trim();

        if (kode.isEmpty()) {
            pesanError("Kode barang tidak boleh kosong.");
            txtKode.requestFocus();
            return null;
        }
        if (nama.isEmpty()) {
            pesanError("Nama barang tidak boleh kosong.");
            txtNama.requestFocus();
            return null;
        }
        int stok;
        try {
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException ex) {
            pesanError("Stok harus berupa angka.");
            txtStok.requestFocus();
            return null;
        }
        if (stok < 0) {
            pesanError("Stok tidak boleh kurang dari 0.");
            txtStok.requestFocus();
            return null;
        }
        return new Barang(kode, nama, cmbKategori.getSelectedItem().toString(), stok);
    }

    private void simpan() {
        Barang b = validasiInput();
        if (b == null) return;
        if (dao.simpan(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan.");
            tampilData();
            bersihkan();
        } else {
            pesanError("Data gagal disimpan. Kode barang mungkin sudah ada atau koneksi bermasalah.");
        }
    }

    private void ubah() {
        if (txtKode.getText().trim().isEmpty()) {
            pesanError("Pilih data di tabel terlebih dahulu.");
            return;
        }
        Barang b = validasiInput();
        if (b == null) return;
        if (dao.ubah(b)) {
            JOptionPane.showMessageDialog(this, "Data berhasil diubah.");
            tampilData();
            bersihkan();
        } else {
            pesanError("Data gagal diubah. Pastikan kode barang sudah ada di database.");
        }
    }

    private void hapus() {
        String kode = txtKode.getText().trim();
        if (kode.isEmpty()) {
            pesanError("Pilih data di tabel terlebih dahulu.");
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus barang dengan kode " + kode + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (pilihan != JOptionPane.YES_OPTION) return;

        if (dao.hapus(kode)) {
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            tampilData();
            bersihkan();
        } else {
            pesanError("Data gagal dihapus.");
        }
    }

    private void bersihkan() {
        txtKode.setText("");
        txtNama.setText("");
        txtStok.setText("");
        cmbKategori.setSelectedIndex(0);
        txtKode.setEditable(true);
        tabel.clearSelection();
        txtKode.requestFocus();
    }

    private void pesanError(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FormBarang().setVisible(true));
    }
}