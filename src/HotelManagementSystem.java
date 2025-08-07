

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HotelManagementSystem {

    // Database connection details
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // Database connection method
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Guest model class
    public static class Guest {
        private int guestId;
        private String name;
        private String contactInfo;
        private String nationality;
        private String gender;
        private String reservationHistory;

        // Getters and setters
        public int getGuestId() {
            return guestId;
        }

        public void setGuestId(int guestId) {
            this.guestId = guestId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContactInfo() {
            return contactInfo;
        }

        public void setContactInfo(String contactInfo) {
            this.contactInfo = contactInfo;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getReservationHistory() {
            return reservationHistory;
        }

        public void setReservationHistory(String reservationHistory) {
            this.reservationHistory = reservationHistory;
        }
    }

    // Guest form class for adding a guest
    public static class GuestForm extends JFrame {
        private static final long serialVersionUID = 1L;

        private JTextField guestIdField;
        private JTextField nameField;
        private JTextField contactInfoField;
        private JTextField nationalityField;
        private JTextField genderField;
        private JTextField reservationHistoryField;
        private JButton saveButton;

        public GuestForm() {
            setTitle("Guest Form");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            guestIdField = new JTextField(20);
            nameField = new JTextField(20);
            contactInfoField = new JTextField(20);
            nationalityField = new JTextField(20);
            genderField = new JTextField(20);
            reservationHistoryField = new JTextField(20);
            saveButton = new JButton("Save");

            JPanel panel = new JPanel(new GridLayout(7, 2));
            panel.add(new JLabel("Guest ID:"));
            panel.add(guestIdField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Contact Info:"));
            panel.add(contactInfoField);
            panel.add(new JLabel("Nationality:"));
            panel.add(nationalityField);
            panel.add(new JLabel("Gender:"));
            panel.add(genderField);
            panel.add(new JLabel("Reservation History:"));
            panel.add(reservationHistoryField);
            panel.add(new JLabel());
            panel.add(saveButton);

            add(panel);

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveGuest();
                }
            });
        }

        private void saveGuest() {
            int guestId = Integer.parseInt(guestIdField.getText());
            String name = nameField.getText();
            String contactInfo = contactInfoField.getText();
            String nationality = nationalityField.getText();
            String gender = genderField.getText();
            String reservationHistory = reservationHistoryField.getText();

            try (Connection conn = getConnection()) {
                String sql = "INSERT INTO Guest (Guest_ID, Name, Contact_Info, Nationality, Gender, Reservation_History) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, guestId);
                stmt.setString(2, name);
                stmt.setString(3, contactInfo);
                stmt.setString(4, nationality);
                stmt.setString(5, gender);
                stmt.setString(6, reservationHistory);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Guest saved successfully!");
                guestIdField.setText("");
                nameField.setText("");
                contactInfoField.setText("");
                nationalityField.setText("");
                genderField.setText("");
                reservationHistoryField.setText("");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Guest update form class
    public static class GuestUpdateForm extends JFrame {
        private static final long serialVersionUID = 2L;

        private JTextField guestIdField;
        private JTextField nameField;
        private JTextField contactInfoField;
        private JTextField nationalityField;
        private JTextField genderField;
        private JTextField reservationHistoryField;
        private JButton updateButton;

        public GuestUpdateForm() {
            setTitle("Update Guest");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            guestIdField = new JTextField(20);
            nameField = new JTextField(20);
            contactInfoField = new JTextField(20);
            nationalityField = new JTextField(20);
            genderField = new JTextField(20);
            reservationHistoryField = new JTextField(20);
            updateButton = new JButton("Update");

            JPanel panel = new JPanel(new GridLayout(7, 2));
            panel.add(new JLabel("Guest ID:"));
            panel.add(guestIdField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Contact Info:"));
            panel.add(contactInfoField);
            panel.add(new JLabel("Nationality:"));
            panel.add(nationalityField);
            panel.add(new JLabel("Gender:"));
            panel.add(genderField);
            panel.add(new JLabel("Reservation History:"));
            panel.add(reservationHistoryField);
            panel.add(new JLabel());
            panel.add(updateButton);

            add(panel);

            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateGuest();
                }
            });
        }

        private void updateGuest() {
            int guestId = Integer.parseInt(guestIdField.getText());
            String name = nameField.getText();
            String contactInfo = contactInfoField.getText();
            String nationality = nationalityField.getText();
            String gender = genderField.getText();
            String reservationHistory = reservationHistoryField.getText();

            try (Connection conn = getConnection()) {
                String sql = "UPDATE Guest SET Name=?, Contact_Info=?, Nationality=?, Gender=?, Reservation_History=? WHERE Guest_ID=?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setString(2, contactInfo);
                stmt.setString(3, nationality);
                stmt.setString(4, gender);
                stmt.setString(5, reservationHistory);
                stmt.setInt(6, guestId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Guest updated successfully!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Guest not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Guest table class for displaying guests
    public static class GuestTable extends JFrame {
        private static final long serialVersionUID = 3L;

        private JTable table;
        private JButton updateButton;
        private JButton deleteButton;
        private JButton insertButton;

        public GuestTable() {
            setTitle("Guest List");
            setSize(800, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            table = new JTable();
            loadGuestData();

            JScrollPane scrollPane = new JScrollPane(table);

            updateButton = new JButton("Update");
            deleteButton = new JButton("Delete");
            insertButton = new JButton("Insert");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(insertButton);

            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openUpdateForm();
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteGuest();
                }
            });

            insertButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openInsertForm();
                }
            });
        }

        private void loadGuestData() {
            try (Connection conn = getConnection()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Guest");

                DefaultTableModel model = new DefaultTableModel(new String[]{"Guest ID", "Name", "Contact Info", "Nationality", "Gender", "Reservation History"}, 0);
                while (rs.next()) {
                    int guestId = rs.getInt("Guest_ID");
                    String name = rs.getString("Name");
                    String contactInfo = rs.getString("Contact_Info");
                    String nationality = rs.getString("Nationality");
                    String gender = rs.getString("Gender");
                    String reservationHistory = rs.getString("Reservation_History");
                    model.addRow(new Object[]{guestId, name, contactInfo, nationality, gender, reservationHistory});
                }
                table.setModel(model);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        private void openUpdateForm() {
            GuestUpdateForm updateForm = new GuestUpdateForm();
            updateForm.setVisible(true);
        }

        private void deleteGuest() {
            String guestIdStr = JOptionPane.showInputDialog(this, "Enter Guest ID to delete:");
            if (guestIdStr != null && !guestIdStr.isEmpty()) {
                int guestId = Integer.parseInt(guestIdStr);
                try (Connection conn = getConnection()) {
                    String sql = "DELETE FROM Guest WHERE Guest_ID = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, guestId);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Guest deleted successfully!");
                        loadGuestData();
                    } else {
                        JOptionPane.showMessageDialog(this, "Guest not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        private void openInsertForm() {
            GuestForm insertForm = new GuestForm();
            insertForm.setVisible(true);
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GuestTable().setVisible(true);
            }
        });
    }
}