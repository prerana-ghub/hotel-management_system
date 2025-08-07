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
import java.sql.Date;

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
        public int getGuestId() { return guestId; }
        public void setGuestId(int guestId) { this.guestId = guestId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getContactInfo() { return contactInfo; }
        public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
        public String getNationality() { return nationality; }
        public void setNationality(String nationality) { this.nationality = nationality; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String getReservationHistory() { return reservationHistory; }
        public void setReservationHistory(String reservationHistory) { this.reservationHistory = reservationHistory; }
    }

    // Guest form class for adding a guest
    public static class GuestForm extends JFrame {
        private JTextField guestIdField;
        private JTextField nameField;
        private JTextField contactInfoField;
        private JTextField nationalityField;
        private JComboBox<String> genderComboBox;
        private JTextField reservationHistoryField;
        private JButton saveButton;
        private GuestTable parentTable;

        public GuestForm(GuestTable parentTable) {
            this.parentTable = parentTable;
            setTitle("Add New Guest");
            setSize(400, 350);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            guestIdField = new JTextField(20);
            nameField = new JTextField(20);
            contactInfoField = new JTextField(20);
            nationalityField = new JTextField(20);
            genderComboBox = new JComboBox<>(new String[]{"M", "F", "Other"});
            reservationHistoryField = new JTextField(20);
            saveButton = new JButton("Save Guest");

            panel.add(new JLabel("Guest ID:"));
            panel.add(guestIdField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Contact Info:"));
            panel.add(contactInfoField);
            panel.add(new JLabel("Nationality:"));
            panel.add(nationalityField);
            panel.add(new JLabel("Gender:"));
            panel.add(genderComboBox);
            panel.add(new JLabel("Reservation History:"));
            panel.add(reservationHistoryField);
            panel.add(new JLabel(""));
            panel.add(saveButton);

            add(panel);

            saveButton.addActionListener(e -> saveGuest());
        }

        private void clearFields() {
            guestIdField.setText("");
            nameField.setText("");
            contactInfoField.setText("");
            nationalityField.setText("");
            genderComboBox.setSelectedIndex(0);
            reservationHistoryField.setText("");
        }

        private void saveGuest() {
            try {
                // Validate input
                if (nameField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                        "Name cannot be empty",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int guestId = Integer.parseInt(guestIdField.getText());
                String name = nameField.getText();
                String contactInfo = contactInfoField.getText();
                String nationality = nationalityField.getText();
                String gender = (String) genderComboBox.getSelectedItem();
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
                    if (parentTable != null) {
                        parentTable.loadGuestData();  // Refresh the guest table
                    }
                    clearFields();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                if (ex.getMessage().contains("Duplicate entry")) {
                    JOptionPane.showMessageDialog(this,
                        "Guest ID already exists. Please use a different ID.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Error saving guest: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid Guest ID. Please enter a valid number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Guest update form class
    public static class GuestUpdateForm extends JFrame {
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
            try {
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
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating guest: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Guest ID", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Guest table class for displaying guests
    public static class GuestTable extends JFrame {
        private JTable table;
        private JButton updateButton;
        private JButton deleteButton;
        private JButton insertButton;
        private JButton viewReservationsButton;

        public GuestTable() {
            setTitle("Guest List");
            setSize(800, 500);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            table = new JTable();
            loadGuestData();

            JScrollPane scrollPane = new JScrollPane(table);

            updateButton = new JButton("Update");
            deleteButton = new JButton("Delete");
            insertButton = new JButton("Insert");
            viewReservationsButton = new JButton("View Reservations");

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(updateButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(insertButton);
            buttonPanel.add(viewReservationsButton);

            setLayout(new BorderLayout());
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            updateButton.addActionListener(e -> openUpdateForm());
            deleteButton.addActionListener(e -> deleteGuest());
            insertButton.addActionListener(e -> openInsertForm());
            viewReservationsButton.addActionListener(e -> viewGuestReservations());
        }

        private void loadGuestData() {
            try (Connection conn = getConnection()) {
                String sql = "SELECT g.*, " +
                           "(SELECT GROUP_CONCAT(CONCAT(h.Name, ' - Room ', r.Hotel_ID) SEPARATOR ', ') " +
                           "FROM Reservation r " +
                           "JOIN Hotel h ON r.Hotel_ID = h.Hotel_ID " +
                           "WHERE r.Guest_ID = g.Guest_ID) as Current_Reservations " +
                           "FROM Guest g";
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Guest ID", "Name", "Contact Info", "Nationality", "Gender", "Reservation History", "Current Reservations"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("Guest_ID"),
                        rs.getString("Name"),
                        rs.getString("Contact_Info"),
                        rs.getString("Nationality"),
                        rs.getString("Gender"),
                        rs.getString("Reservation_History"),
                        rs.getString("Current_Reservations")
                    });
                }
                table.setModel(model);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading guest data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private void viewGuestReservations() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Please select a guest to view reservations",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int guestId = (Integer) table.getValueAt(selectedRow, 0);
            
            try (Connection conn = getConnection()) {
                String sql = "SELECT r.Reservation_ID, h.Name as Hotel_Name, r.Hotel_ID, " +
                           "r.Check_in_Date, h.Location, h.Contact_Info " +
                           "FROM Reservation r " +
                           "JOIN Hotel h ON r.Hotel_ID = h.Hotel_ID " +
                           "WHERE r.Guest_ID = ?";
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, guestId);
                ResultSet rs = stmt.executeQuery();

                // Create a new window to display reservations
                JFrame reservationFrame = new JFrame("Reservations for Guest ID: " + guestId);
                reservationFrame.setSize(800, 300);
                reservationFrame.setLocationRelativeTo(this);

                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Reservation ID", "Hotel", "Hotel ID", "Check-in Date", "Location", "Hotel Contact"}, 0);

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("Reservation_ID"),
                        rs.getString("Hotel_Name"),
                        rs.getInt("Hotel_ID"),
                        rs.getDate("Check_in_Date"),
                        rs.getString("Location"),
                        rs.getString("Contact_Info")
                    });
                }

                JTable reservationTable = new JTable(model);
                reservationTable.setEnabled(false);
                JScrollPane scrollPane = new JScrollPane(reservationTable);
                reservationFrame.add(scrollPane);
                reservationFrame.setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading reservations: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private void openUpdateForm() {
            GuestUpdateForm updateForm = new GuestUpdateForm();
            updateForm.setVisible(true);
        }

        private void deleteGuest() {
            String guestIdStr = JOptionPane.showInputDialog(this, "Enter Guest ID to delete:");
            if (guestIdStr != null && !guestIdStr.isEmpty()) {
                try {
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
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting guest: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Guest ID", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void openInsertForm() {
            GuestForm insertForm = new GuestForm(this);
            insertForm.setVisible(true);
        }
    }

    // Reservation Form
    public static class ReservationForm extends JFrame {
        private JTextField reservationIdField;
        private JTextField guestIdField;
        private JTextField hotelIdField;
        private JTextField checkInDateField;
        private JButton bookButton;

        public ReservationForm() {
            setTitle("Make Reservation");
            setSize(400, 250);  // Reduced size since we removed room field
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            reservationIdField = new JTextField(20);
            guestIdField = new JTextField(20);
            hotelIdField = new JTextField(20);
            checkInDateField = new JTextField(20);
            bookButton = new JButton("Book Room");

            JPanel panel = new JPanel(new GridLayout(5, 2));  // Reduced grid size
            panel.add(new JLabel("Reservation ID:"));
            panel.add(reservationIdField);
            panel.add(new JLabel("Guest ID:"));
            panel.add(guestIdField);
            panel.add(new JLabel("Hotel ID:"));
            panel.add(hotelIdField);
            panel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
            panel.add(checkInDateField);
            panel.add(new JLabel());
            panel.add(bookButton);

            add(panel);

            // Add input validation for date format
            checkInDateField.setToolTipText("Enter date in YYYY-MM-DD format");
            
            // Add field validation on focus lost
            checkInDateField.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusLost(java.awt.event.FocusEvent evt) {
                    try {
                        if (!checkInDateField.getText().isEmpty()) {
                            Date.valueOf(checkInDateField.getText());
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(ReservationForm.this,
                            "Invalid date format. Please use YYYY-MM-DD",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            bookButton.addActionListener(e -> makeReservation());
        }

        private void clearFields() {
            reservationIdField.setText("");
            guestIdField.setText("");
            hotelIdField.setText("");
            checkInDateField.setText("");
        }

        private boolean isHotelValid(Connection conn, int hotelId) throws SQLException {
            String sql = "SELECT COUNT(*) FROM Hotel WHERE Hotel_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, hotelId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        }

        private boolean isGuestValid(Connection conn, int guestId) throws SQLException {
            String sql = "SELECT COUNT(*) FROM Guest WHERE Guest_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, guestId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        }

        private void makeReservation() {
            try {
                int reservationId = Integer.parseInt(reservationIdField.getText());
                int guestId = Integer.parseInt(guestIdField.getText());
                int hotelId = Integer.parseInt(hotelIdField.getText());
                Date checkInDate = Date.valueOf(checkInDateField.getText());

                try (Connection conn = getConnection()) {
                    // Validate guest exists
                    if (!isGuestValid(conn, guestId)) {
                        JOptionPane.showMessageDialog(this,
                            "Guest ID " + guestId + " does not exist. Please enter a valid Guest ID.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Validate hotel exists
                    if (!isHotelValid(conn, hotelId)) {
                        JOptionPane.showMessageDialog(this,
                            "Hotel ID " + hotelId + " does not exist. Please enter a valid Hotel ID.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Begin transaction
                    conn.setAutoCommit(false);
                    try {
                        // Insert reservation
                        String reservationSql = "INSERT INTO Reservation (Reservation_ID, Guest_ID, Hotel_ID, Check_in_Date) VALUES (?, ?, ?, ?)";
                        PreparedStatement reservationStmt = conn.prepareStatement(reservationSql);
                        reservationStmt.setInt(1, reservationId);
                        reservationStmt.setInt(2, guestId);
                        reservationStmt.setInt(3, hotelId);
                        reservationStmt.setDate(4, checkInDate);
                        reservationStmt.executeUpdate();

                        conn.commit();
                        JOptionPane.showMessageDialog(this, 
                            "Reservation made successfully!", 
                            "Reservation Confirmed", 
                            JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    } catch (SQLException ex) {
                        conn.rollback();
                        throw ex;
                    } finally {
                        conn.setAutoCommit(true);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Reservation failed: " + ex.getMessage(), 
                    "Reservation Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Invalid input. Please check your entries.", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Staff Form for adding staff members
    public static class StaffForm extends JFrame {
        private JTextField staffIdField;
        private JTextField nameField;
        private JTextField ageField;
        private JTextField contactInfoField;
        private JTextField salaryField;
        private JTextField departmentIdField;
        private JButton saveButton;

        public StaffForm() {
            setTitle("Add Staff Member");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            staffIdField = new JTextField(20);
            nameField = new JTextField(20);
            ageField = new JTextField(20);
            contactInfoField = new JTextField(20);
            salaryField = new JTextField(20);
            departmentIdField = new JTextField(20);
            saveButton = new JButton("Save");

            JPanel panel = new JPanel(new GridLayout(7, 2));
            panel.add(new JLabel("Staff ID:"));
            panel.add(staffIdField);
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Age:"));
            panel.add(ageField);
            panel.add(new JLabel("Contact Info:"));
            panel.add(contactInfoField);
            panel.add(new JLabel("Salary:"));
            panel.add(salaryField);
            panel.add(new JLabel("Department ID:"));
            panel.add(departmentIdField);
            panel.add(new JLabel());
            panel.add(saveButton);

            add(panel);

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    saveStaff();
                }
            });
        }

        private boolean isDepartmentValid(Connection conn, int departmentId) throws SQLException {
            String sql = "SELECT COUNT(*) FROM Department WHERE Department_ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, departmentId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        }

        private void saveStaff() {
            try {
                int staffId = Integer.parseInt(staffIdField.getText());
                String name = nameField.getText();
                int age = Integer.parseInt(ageField.getText());
                String contactInfo = contactInfoField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                int departmentId = Integer.parseInt(departmentIdField.getText());

                // Validate salary before database operation
                if (salary < 500) {
                    JOptionPane.showMessageDialog(this,
                        "Salary cannot be less than 500",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = getConnection()) {
                    // Check if department exists
                    if (!isDepartmentValid(conn, departmentId)) {
                        JOptionPane.showMessageDialog(this,
                            "Department ID " + departmentId + " does not exist. Please enter a valid Department ID.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    conn.setAutoCommit(false);
                    try {
                        String sql = "INSERT INTO Staff (Staff_ID, Name, Age, Contact_Info, Salary, Department_ID) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, staffId);
                        stmt.setString(2, name);
                        stmt.setInt(3, age);
                        stmt.setString(4, contactInfo);
                        stmt.setDouble(5, salary);
                        stmt.setInt(6, departmentId);
                        stmt.executeUpdate();

                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Staff member saved successfully!");
                        clearFields();
                    } catch (SQLException ex) {
                        conn.rollback();
                        if (ex.getMessage().toLowerCase().contains("foreign key")) {
                            JOptionPane.showMessageDialog(this,
                                "Invalid Department ID. Please enter a valid Department ID.",
                                "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        } else if (ex.getSQLState().equals("45000")) {
                            JOptionPane.showMessageDialog(this,
                                "Database validation failed: " + ex.getMessage(),
                                "Validation Error",
                                JOptionPane.ERROR_MESSAGE);
                        } else {
                            throw ex;
                        }
                    } finally {
                        conn.setAutoCommit(true);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error saving staff member: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid input. Please check your entries.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private void clearFields() {
            staffIdField.setText("");
            nameField.setText("");
            ageField.setText("");
            contactInfoField.setText("");
            salaryField.setText("");
            departmentIdField.setText("");
        }
    }

    // Room Availability Viewer
    public static class RoomAvailabilityViewer extends JFrame {
        private JTable table;
        private JButton refreshButton;

        public RoomAvailabilityViewer() {
            setTitle("Room Availability");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            // Create main panel
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Create button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            refreshButton = new JButton("Refresh");
            buttonPanel.add(refreshButton);

            // Create table
            table = new JTable();
            JScrollPane scrollPane = new JScrollPane(table);

            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            add(mainPanel);

            // Add listeners
            refreshButton.addActionListener(e -> loadRoomData());

            // Initial load
            loadRoomData();
        }

        private void loadRoomData() {
            try (Connection conn = getConnection()) {
                String sql = "SELECT r.Room_No, r.Category, r.Rent, r.Status " +
                           "FROM Room r " +
                           "ORDER BY r.Room_No";

                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                // Create table model
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Room No", "Category", "Rate", "Status"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("Room_No"),
                        rs.getString("Category"),
                        rs.getDouble("Rent"),
                        rs.getString("Status")
                    });
                }

                table.setModel(model);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error loading room data: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Database Creator
    public static class DatabaseCreator extends JFrame {
        private JTextArea sqlOutput;
        private JButton createTablesButton;

        public DatabaseCreator() {
            setTitle("Database Creator");
            setSize(600, 400);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            createTablesButton = new JButton("Create Database Tables");
            sqlOutput = new JTextArea();
            sqlOutput.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(sqlOutput);

            mainPanel.add(createTablesButton, BorderLayout.NORTH);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            add(mainPanel);

            createTablesButton.addActionListener(e -> createTables());
        }

        private void createTables() {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    String[] createTableStatements = {
                        "CREATE TABLE IF NOT EXISTS Guest (" +
                        "Guest_ID INT PRIMARY KEY," +
                        "Name VARCHAR(100)," +
                        "Contact_Info VARCHAR(255)," +
                        "Nationality VARCHAR(50)," +
                        "Gender VARCHAR(10)," +
                        "Reservation_History VARCHAR(255)" +
                        ")",

                        "CREATE TABLE IF NOT EXISTS Hotel (" +
                        "Hotel_ID INT PRIMARY KEY," +
                        "Name VARCHAR(100)," +
                        "Location VARCHAR(255)," +
                        "Num_Rooms INT," +
                        "Rating DECIMAL(3,1)," +
                        "Contact_Info VARCHAR(255)" +
                        ")",

                        "CREATE TABLE IF NOT EXISTS Room (" +
                        "Room_No INT PRIMARY KEY," +
                        "Category VARCHAR(50)," +
                        "Rent DECIMAL(10,2)," +
                        "Status VARCHAR(20)" +
                        ")",

                        "CREATE TABLE IF NOT EXISTS Department (" +
                        "Department_ID INT PRIMARY KEY," +
                        "D_Head VARCHAR(100)," +
                        "D_Role VARCHAR(100)," +
                        "Staff_Count INT," +
                        "Contact_Info VARCHAR(255)" +
                        ")",

                        "CREATE TABLE IF NOT EXISTS Staff (" +
                        "Staff_ID INT PRIMARY KEY," +
                        "Name VARCHAR(100)," +
                        "Age INT," +
                        "Contact_Info VARCHAR(255)," +
                        "Salary DECIMAL(10,2)," +
                        "Department_ID INT," +
                        "FOREIGN KEY (Department_ID) REFERENCES Department(Department_ID)" +
                        ")",

                        "CREATE TABLE IF NOT EXISTS Reservation (" +
                        "Reservation_ID INT PRIMARY KEY," +
                        "Guest_ID INT," +
                        "Hotel_ID INT," +
                        "Check_in_Date DATE," +
                        "FOREIGN KEY (Guest_ID) REFERENCES Guest(Guest_ID)," +
                        "FOREIGN KEY (Hotel_ID) REFERENCES Hotel(Hotel_ID)" +
                        ")"
                    };

                    StringBuilder output = new StringBuilder();
                    for (String sql : createTableStatements) {
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.execute();
                            output.append("Successfully created table: ").append(sql.substring(0, sql.indexOf("("))).append("\n");
                        }
                    }

                    // Create triggers
                    String[] triggerStatements = {
                        "DROP TRIGGER IF EXISTS update_room_status",
                        "CREATE TRIGGER update_room_status " +
                        "AFTER INSERT ON Reservation " +
                        "FOR EACH ROW " +
                        "BEGIN " +
                        "UPDATE Room SET Status = 'Booked' " +
                        "WHERE Room_No = NEW.Hotel_ID; " +
                        "END;",

                        "DROP TRIGGER IF EXISTS prevent_guest_deletion",
                        "CREATE TRIGGER prevent_guest_deletion " +
                        "BEFORE DELETE ON Guest " +
                        "FOR EACH ROW " +
                        "BEGIN " +
                        "DECLARE reservation_count INT; " +
                        "SELECT COUNT(*) INTO reservation_count " +
                        "FROM Reservation " +
                        "WHERE Guest_ID = OLD.Guest_ID; " +
                        "IF reservation_count > 0 THEN " +
                        "SIGNAL SQLSTATE '45000' " +
                        "SET MESSAGE_TEXT = 'Cannot delete guest with active reservations'; " +
                        "END IF; " +
                        "END;",

                        "DROP TRIGGER IF EXISTS update_hotel_rating",
                        "CREATE TRIGGER update_hotel_rating " +
                        "AFTER INSERT ON Reservation " +
                        "FOR EACH ROW " +
                        "BEGIN " +
                        "UPDATE Hotel " +
                        "SET Rating = LEAST(Rating + 0.1, 5.0) " +
                        "WHERE Hotel_ID = NEW.Hotel_ID; " +
                        "END;"
                    };

                    for (String sql : triggerStatements) {
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.execute();
                            if (sql.startsWith("CREATE")) {
                                output.append("Successfully created trigger\n");
                            }
                        }
                    }

                    conn.commit();
                    sqlOutput.setText(output.toString() + "\nAll tables and triggers created successfully!");
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (SQLException ex) {
                sqlOutput.setText("Error creating tables: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // Main Menu
    public static class MainMenu extends JFrame {
        public MainMenu() {
            setTitle("Hotel Management System");
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            // Initialize database with sample data
            initializeDatabase();

            JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));  // Updated grid layout
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JButton guestManagementButton = new JButton("Guest Management");
            JButton makeReservationButton = new JButton("Make Reservation");
            JButton addStaffButton = new JButton("Add Staff Member");
            JButton roomAvailabilityButton = new JButton("View Room Availability");
            JButton initializeDatabaseButton = new JButton("Reset Database");
            JButton createDatabaseButton = new JButton("Create Database Tables");

            guestManagementButton.addActionListener(e -> new GuestTable().setVisible(true));
            makeReservationButton.addActionListener(e -> new ReservationForm().setVisible(true));
            addStaffButton.addActionListener(e -> new StaffForm().setVisible(true));
            roomAvailabilityButton.addActionListener(e -> new RoomAvailabilityViewer().setVisible(true));
            createDatabaseButton.addActionListener(e -> new DatabaseCreator().setVisible(true));
            initializeDatabaseButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(this,
                    "This will reset the database with sample data. Continue?",
                    "Reset Database",
                    JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    initializeDatabase();
                    JOptionPane.showMessageDialog(this, "Database initialized with sample data!");
                }
            });

            panel.add(guestManagementButton);
            panel.add(makeReservationButton);
            panel.add(addStaffButton);
            panel.add(roomAvailabilityButton);
            panel.add(createDatabaseButton);
            panel.add(initializeDatabaseButton);

            add(panel);
        }

        private void initializeDatabase() {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // Clear existing data
                    String[] clearTables = {
                        "DELETE FROM Reservation",
                        "DELETE FROM Staff",
                        "DELETE FROM Room",
                        "DELETE FROM Guest",
                        "DELETE FROM Department",
                        "DELETE FROM Hotel"
                    };

                    for (String sql : clearTables) {
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.executeUpdate();
                        }
                    }

                    // Insert sample departments
                    String insertDepartment = "INSERT INTO Department (Department_ID, D_Head, D_Role, Staff_Count, Contact_Info) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertDepartment)) {
                        Object[][] departments = {
                            {1, "John Smith", "Housekeeping", 10, "housekeeping@hotel.com"},
                            {2, "Mary Johnson", "Front Desk", 5, "frontdesk@hotel.com"},
                            {3, "Robert Brown", "Maintenance", 8, "maintenance@hotel.com"}
                        };
                        for (Object[] dept : departments) {
                            stmt.setInt(1, (Integer)dept[0]);
                            stmt.setString(2, (String)dept[1]);
                            stmt.setString(3, (String)dept[2]);
                            stmt.setInt(4, (Integer)dept[3]);
                            stmt.setString(5, (String)dept[4]);
                            stmt.executeUpdate();
                        }
                    }

                    // Insert sample hotels
                    String insertHotel = "INSERT INTO Hotel (Hotel_ID, Name, Location, Num_Rooms, Rating, Contact_Info) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertHotel)) {
                        Object[][] hotels = {
                            {1, "Luxury Hotel", "Downtown", 50, 4.5, "luxury@hotel.com"},
                            {2, "Beach Resort", "Beachfront", 100, 4.8, "beach@hotel.com"},
                            {3, "Business Hotel", "City Center", 75, 4.2, "business@hotel.com"}
                        };
                        for (Object[] hotel : hotels) {
                            stmt.setInt(1, (Integer)hotel[0]);
                            stmt.setString(2, (String)hotel[1]);
                            stmt.setString(3, (String)hotel[2]);
                            stmt.setInt(4, (Integer)hotel[3]);
                            stmt.setDouble(5, (Double)hotel[4]);
                            stmt.setString(6, (String)hotel[5]);
                            stmt.executeUpdate();
                        }
                    }

                    // Insert sample rooms
                    String insertRoom = "INSERT INTO Room (Room_No, Category, Rent, Status) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertRoom)) {
                        Object[][] rooms = {
                            {101, "Deluxe", 200.00, "Available"},
                            {102, "Suite", 350.00, "Available"},
                            {103, "Standard", 150.00, "Available"},
                            {201, "Deluxe", 200.00, "Available"},
                            {202, "Suite", 350.00, "Available"},
                            {203, "Standard", 150.00, "Available"}
                        };
                        for (Object[] room : rooms) {
                            stmt.setInt(1, (Integer)room[0]);
                            stmt.setString(2, (String)room[1]);
                            stmt.setDouble(3, (Double)room[2]);
                            stmt.setString(4, (String)room[3]);
                            stmt.executeUpdate();
                        }
                    }

                    // Insert sample guests
                    String insertGuest = "INSERT INTO Guest (Guest_ID, Name, Contact_Info, Nationality, Gender, Reservation_History) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertGuest)) {
                        Object[][] guests = {
                            {1, "Alice Wilson", "alice@email.com", "USA", "F", "First time"},
                            {2, "Bob Miller", "bob@email.com", "UK", "M", "Regular"},
                            {3, "Carol Davis", "carol@email.com", "Canada", "F", "VIP"}
                        };
                        for (Object[] guest : guests) {
                            stmt.setInt(1, (Integer)guest[0]);
                            stmt.setString(2, (String)guest[1]);
                            stmt.setString(3, (String)guest[2]);
                            stmt.setString(4, (String)guest[3]);
                            stmt.setString(5, (String)guest[4]);
                            stmt.setString(6, (String)guest[5]);
                            stmt.executeUpdate();
                        }
                    }

                    conn.commit();
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error initializing database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenu().setVisible(true);
        });
    }
}