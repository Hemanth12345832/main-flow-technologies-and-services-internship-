import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import java.awt.Image;
import java.awt.Graphics;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mahan
 */
public class billing extends JFrame {

    // Buyer Details
    private JTextField jTextField1; // Name
    private JTextField jTextField2; // Contact No
    private JTextField jTextField3; // Email
    private JTextField jTextField4; // Address

    // Product Details
    private JTextField jTextField5; // Product ID (not used for DB but for input)
    private JTextField jTextField6; // Product Name
    private JTextField jTextField7; // Rate
    private JTextField jTextField8; // Quantity
    private JTextField jTextField9; // Description

    // Calculation Details
    private JTextField jTextField10; // Total Amount
    private JTextField jTextField11; // Paid Amount
    private JTextField jTextField12; // Returned Amount

    // Date and Time Labels
    private JLabel jLabel4; // Date
    private JLabel jLabel5; // Time

    // Table
    private JTable jTable1;
    private DefaultTableModel tableModel;

    // Buttons
    private JButton jButton1; // Add
    private JButton jButton2; // Save
    private JButton jButton3; // Reset

    // Database Credentials
    private final String DB_URL = "jdbc:mysql://localhost:3306/myappdb";
    private final String DB_USER = "root"; // Assuming root for simplicity, change if you use 'hemuboss'
    private final String DB_PASSWORD = "hemuboss$12345";

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                // Load image directly from the specified file path
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Error loading background image from " + imagePath + ": " + e.getMessage());
                e.printStackTrace();
                // Fallback to a plain background color if image fails to load
                setBackground(new java.awt.Color(240, 240, 240)); // Light gray
            }
            // Set layout to null so that components added to this panel can be positioned absolutely
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                // Draw the image scaled to fit the panel
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // If image failed to load, draw the fallback color set in constructor
                // super.paintComponent(g) already handles painting the background color
                // if set with setBackground().
            }
        }
    }

    public billing() {
        setTitle("Billing System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        BackgroundPanel backgroundPanel = new BackgroundPanel("D:/background.jpg"); // IMPORTANT: Verify this path!
        setContentPane(backgroundPanel);

        initComponents(backgroundPanel);
        setupDateTimeUpdater();
        setupActionListeners();
        calculateReturnedAmountOnPaidAmountChange();
    }

    private void initComponents(JPanel panel) {
        // --- Buyer Details ---
        JLabel lblBuyerDetails = new JLabel("Buyer Details");
        lblBuyerDetails.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblBuyerDetails.setBounds(30, 90, 150, 25);
        add(lblBuyerDetails);

        JLabel lblName = new JLabel("Name");
        lblName.setBounds(30, 130, 80, 25);
        add(lblName);
        jTextField1 = new JTextField();
        jTextField1.setBounds(110, 130, 150, 25);
        add(jTextField1);

        JLabel lblContactNo = new JLabel("Contact No");
        lblContactNo.setBounds(280, 130, 80, 25);
        add(lblContactNo);
        jTextField2 = new JTextField();
        jTextField2.setBounds(370, 130, 150, 25);
        add(jTextField2);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(540, 130, 80, 25);
        add(lblEmail);
        jTextField3 = new JTextField();
        jTextField3.setBounds(610, 130, 150, 25);
        add(jTextField3);

        JLabel lblAddress = new JLabel("Address");
        lblAddress.setBounds(780, 130, 80, 25);
        add(lblAddress);
        jTextField4 = new JTextField();
        jTextField4.setBounds(850, 130, 120, 25);
        add(jTextField4);

        // --- Product Details ---
        JLabel lblProductDetails = new JLabel("Product Details");
        lblProductDetails.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblProductDetails.setBounds(30, 180, 150, 25);
        add(lblProductDetails);

        JLabel lblProductId = new JLabel("Product ID");
        lblProductId.setBounds(30, 220, 80, 25);
        add(lblProductId);
        jTextField5 = new JTextField();
        jTextField5.setBounds(110, 220, 100, 25);
        add(jTextField5);

        JLabel lblProductName = new JLabel("Product Name");
        lblProductName.setBounds(230, 220, 100, 25);
        add(lblProductName);
        jTextField6 = new JTextField();
        jTextField6.setBounds(330, 220, 120, 25);
        add(jTextField6);

        JLabel lblRate = new JLabel("Rate");
        lblRate.setBounds(470, 220, 50, 25);
        add(lblRate);
        jTextField7 = new JTextField();
        jTextField7.setBounds(520, 220, 80, 25);
        add(jTextField7);

        JLabel lblQuantity = new JLabel("Quantity");
        lblQuantity.setBounds(620, 220, 60, 25);
        add(lblQuantity);
        jTextField8 = new JTextField();
        jTextField8.setBounds(690, 220, 80, 25);
        add(jTextField8);

        JLabel lblDescription = new JLabel("Description");
        lblDescription.setBounds(800, 220, 80, 25);
        add(lblDescription);
        jTextField9 = new JTextField();
        jTextField9.setBounds(880, 220, 90, 25);
        add(jTextField9);

        // --- Calculation Details ---
        JLabel lblCalculationDetails = new JLabel("Calculation Details");
        lblCalculationDetails.setFont(new java.awt.Font("Segoe UI", 1, 14));
        lblCalculationDetails.setBounds(30, 480, 150, 25);
        add(lblCalculationDetails);

        JLabel lblTotalAmount = new JLabel("Total Amount");
        lblTotalAmount.setBounds(30, 520, 100, 25);
        add(lblTotalAmount);
        jTextField10 = new JTextField("0.00");
        jTextField10.setEditable(false);
        jTextField10.setBounds(140, 520, 150, 25);
        add(jTextField10);

        JLabel lblPaidAmount = new JLabel("Paid Amount");
        lblPaidAmount.setBounds(30, 560, 100, 25);
        add(lblPaidAmount);
        jTextField11 = new JTextField("0.00");
        jTextField11.setBounds(140, 560, 150, 25);
        add(jTextField11);

        JLabel lblReturnedAmount = new JLabel("Returned Amount");
        lblReturnedAmount.setBounds(30, 600, 120, 25);
        add(lblReturnedAmount);
        jTextField12 = new JTextField("0.00");
        jTextField12.setEditable(false);
        jTextField12.setBounds(140, 600, 150, 25);
        add(jTextField12);

        // --- Date and Time Labels ---
        JLabel lblDate = new JLabel("Date:");
        lblDate.setBounds(30, 20, 50, 25);
        add(lblDate);
        jLabel4 = new JLabel(); // Date label
        jLabel4.setBounds(90, 20, 150, 25);
        add(jLabel4);

        JLabel lblTime = new JLabel("Time:");
        lblTime.setBounds(30, 50, 50, 25);
        add(lblTime);
        jLabel5 = new JLabel(); // Time label
        jLabel5.setBounds(90, 50, 150, 25);
        add(jLabel5);

        // --- JTable ---
        String[] columnNames = {"Name", "Description", "Rate", "Quantity", "Total"};
        tableModel = new DefaultTableModel(columnNames, 0);
        jTable1 = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(jTable1);
        scrollPane.setBounds(30, 270, 750, 200);
        add(scrollPane);

        // --- Buttons ---
        jButton1 = new JButton("Add");
        jButton1.setBounds(800, 270, 100, 30);
        add(jButton1);

        jButton2 = new JButton("Save");
        jButton2.setBounds(800, 320, 100, 30);
        add(jButton2);

        jButton3 = new JButton("Reset");
        jButton3.setBounds(800, 370, 100, 30);
        add(jButton3);
    }

    private void setupDateTimeUpdater() {
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                jLabel4.setText(now.format(dateFormatter));
                jLabel5.setText(now.format(timeFormatter));
            }
        });
        timer.start();
    }

    private void calculateReturnedAmountOnPaidAmountChange() {
        jTextField11.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateReturnedAmount();
            }
        });

        jTextField11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateReturnedAmount();
            }
        });
    }

    private void calculateReturnedAmount() {
        try {
            BigDecimal totalAmount = new BigDecimal(jTextField10.getText());
            BigDecimal paidAmount = new BigDecimal(jTextField11.getText());
            BigDecimal returnedAmount = paidAmount.subtract(totalAmount);
            jTextField12.setText(returnedAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        } catch (NumberFormatException ex) {
            jTextField12.setText("0.00");
        }
    }

    private void setupActionListeners() {
        // Add Button
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToTable();
            }
        });

        // Save Button
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBill();
            }
        });

        // Reset Button
        jButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
    }

    private void addProductToTable() {
        String productName = jTextField6.getText().trim();
        String description = jTextField9.getText().trim();
        String rateStr = jTextField7.getText().trim();
        String quantityStr = jTextField8.getText().trim();

        if (productName.isEmpty() || rateStr.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product Name, Rate, and Quantity cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BigDecimal rate = new BigDecimal(rateStr);
            int quantity = Integer.parseInt(quantityStr);

            if (rate.compareTo(BigDecimal.ZERO) <= 0 || quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Rate and Quantity must be positive numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BigDecimal total = rate.multiply(new BigDecimal(quantity));

            // Add row to table
            Object[] row = {productName, description, rate.setScale(2, BigDecimal.ROUND_HALF_UP), quantity, total.setScale(2, BigDecimal.ROUND_HALF_UP)};
            tableModel.addRow(row);

            // Update Total Amount
            BigDecimal currentTotalAmount = new BigDecimal(jTextField10.getText());
            currentTotalAmount = currentTotalAmount.add(total);
            jTextField10.setText(currentTotalAmount.setScale(2, BigDecimal.ROUND_HALF_UP).toString());

            // Clear product details fields
            jTextField5.setText("");
            jTextField6.setText("");
            jTextField7.setText("");
            jTextField8.setText("");
            jTextField9.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Rate or Quantity. Please enter numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBill() {
        // 1. Validate Buyer Details
        String customerName = jTextField1.getText().trim();
        String contactNo = jTextField2.getText().trim();
        String email = jTextField3.getText().trim();
        String address = jTextField4.getText().trim();

        if (customerName.isEmpty() || contactNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Customer Name and Contact No are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No products added to the bill.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Get Calculation Details
        BigDecimal totalAmount, paidAmount, returnedAmount;
        try {
            totalAmount = new BigDecimal(jTextField10.getText());
            paidAmount = new BigDecimal(jTextField11.getText());
            returnedAmount = new BigDecimal(jTextField12.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount in calculation details.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Save to Database
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            String sql = "INSERT INTO bill (customerName, contactNo, email, address, productName, description, rate, quantity, total, totalAmount, paidAmount, returnedAmount, billDate, billTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            LocalDateTime now = LocalDateTime.now();
            String billDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String billTime = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pstmt.setString(1, customerName);
                pstmt.setString(2, contactNo);
                pstmt.setString(3, email.isEmpty() ? null : email); // Set null if empty
                pstmt.setString(4, address.isEmpty() ? null : address); // Set null if empty

                pstmt.setString(5, tableModel.getValueAt(i, 0).toString()); // Product Name
                pstmt.setString(6, tableModel.getValueAt(i, 1).toString().isEmpty() ? null : tableModel.getValueAt(i, 1).toString()); // Description
                pstmt.setBigDecimal(7, new BigDecimal(tableModel.getValueAt(i, 2).toString())); // Rate
                pstmt.setInt(8, (int) tableModel.getValueAt(i, 3)); // Quantity
                pstmt.setBigDecimal(9, new BigDecimal(tableModel.getValueAt(i, 4).toString())); // Total for item

                pstmt.setBigDecimal(10, totalAmount);
                pstmt.setBigDecimal(11, paidAmount);
                pstmt.setBigDecimal(12, returnedAmount);
                pstmt.setString(13, billDate);
                pstmt.setString(14, billTime);

                pstmt.addBatch(); // Add to batch for multiple inserts
            }

            pstmt.executeBatch(); // Execute all inserts
            conn.commit(); // Commit transaction

            JOptionPane.showMessageDialog(this, "Bill saved to database successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // 4. Generate PDF
            generatePdf(customerName, contactNo, email, address, totalAmount, paidAmount, returnedAmount, billDate, billTime);

            resetForm(); // Reset form after successful save and PDF generation

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving bill: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void generatePdf(String customerName, String contactNo, String email, String address, BigDecimal totalAmount, BigDecimal paidAmount, BigDecimal returnedAmount, String billDate, String billTime) {
        String path = "D:/Bill_" + customerName.replaceAll("\\s+", "_") + "_" + System.currentTimeMillis() + ".pdf";
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Company Name
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.NORMAL);
            Paragraph companyName = new Paragraph("Groce Smart", companyFont);
            companyName.setAlignment(Element.ALIGN_CENTER);
            document.add(companyName);
            document.add(Chunk.NEWLINE);

            // Bill Date and Time
            document.add(new Paragraph("Date: " + billDate + "    Time: " + billTime, FontFactory.getFont(FontFactory.HELVETICA, 10)));
            document.add(Chunk.NEWLINE);

            // Buyer Details
            document.add(new Paragraph("Buyer Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Name: " + customerName));
            document.add(new Paragraph("Contact No: " + contactNo));
            if (!email.isEmpty()) {
                document.add(new Paragraph("Email: " + email));
            }
            if (!address.isEmpty()) {
                document.add(new Paragraph("Address: " + address));
            }
            document.add(Chunk.NEWLINE);

            // Product Details Table
            document.add(new Paragraph("Products Purchased:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            PdfPTable pdfTable = new PdfPTable(jTable1.getColumnCount());
            pdfTable.setWidthPercentage(100); // Table will fill the page width

            // Add Table Headers
            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                PdfPCell header = new PdfPCell(new Phrase(jTable1.getColumnName(i)));
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                pdfTable.addCell(header);
            }

            // Add Table Rows
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                pdfTable.addCell(tableModel.getValueAt(i, 0).toString()); // Name
                pdfTable.addCell(tableModel.getValueAt(i, 1).toString()); // Description
                pdfTable.addCell(tableModel.getValueAt(i, 2).toString()); // Rate
                pdfTable.addCell(tableModel.getValueAt(i, 3).toString()); // Quantity
                pdfTable.addCell(tableModel.getValueAt(i, 4).toString()); // Total
            }
            document.add(pdfTable);
            document.add(Chunk.NEWLINE);

            // Calculation Details
            document.add(new Paragraph("Calculation Details:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
            document.add(new Paragraph("Total Amount: Rs. " + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));
            document.add(new Paragraph("Paid Amount: Rs. " + paidAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));
            document.add(new Paragraph("Returned Amount: Rs. " + returnedAmount.setScale(2, BigDecimal.ROUND_HALF_UP)));

            document.close();
            JOptionPane.showMessageDialog(this, "PDF generated successfully at: " + path, "PDF Saved", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating PDF: " + e.getMessage(), "PDF Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void resetForm() {
        // Clear Buyer Details
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");

        // Clear Product Details
        jTextField5.setText("");
        jTextField6.setText("");
        jTextField7.setText("");
        jTextField8.setText("");
        jTextField9.setText("");

        // Clear Calculation Details
        jTextField10.setText("0.00");
        jTextField11.setText("0.00");
        jTextField12.setText("0.00");

        // Clear JTable
        tableModel.setRowCount(0); // Removes all rows
    }

    public static void main(String[] args) {
        // Ensure that the JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver Registered!");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver? Include it in your library path!");
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            new billing().setVisible(true);
        });
    }
}