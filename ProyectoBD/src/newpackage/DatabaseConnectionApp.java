package newpackage;

/**
 *
 * @author LENOVO
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import newpackage.TableInfoWindow;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.util.Arrays;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class DatabaseConnectionApp {

    Connection connection;
    JFrame frame;
    JComboBox<String> databaseComboBox;
    JTextField hostTextField;
    JTextField portTextField;
    JTextField userTextField;
    JPasswordField passwordField;
    JTextField dbNameTextField; // Nuevo campo para el nombre de la base de datos
    final String[] selectedTable = {null};  // Declarar selectedTable como final
    final List<DefaultTableModel> tableModels = new ArrayList<>();
    final List<JTextField> tableNameFields = new ArrayList<>();
    DefaultListModel<String> schemaListModel = new DefaultListModel<>();
    JList<String> schemaList = new JList<>(schemaListModel);
    DefaultListModel<String> tableListModel = new DefaultListModel<>();
    JList<String> tableList = new JList<>(tableListModel);
    private JTextField urlTextField;
    private JTextField bdAccessTextField;
    private JTextField tableNameFieldCreate;
    private JTextArea columnDefinitionArea;
    private JButton createTableButton;
    private JTextArea sqlPreviewArea;
    private JTextArea sqlPreviewAreaCreate;
    private JTextArea sqlPreviewAreaDrop;
    private JTextField tableNameFieldAlter;
    private JTextArea alterStatementArea;
    private JButton alterTableButton;
    private JTextField tableNameFieldDrop;
    private JButton dropTableButton;
    Access objeto = new Access();

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                DatabaseConnectionApp window = new DatabaseConnectionApp();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public DatabaseConnectionApp() {
        creaBD();
    }

    private void creaBD() {

        frame = new JFrame("Database Connection");
        frame.setBounds(100, 100, 400, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblSelectDatabase = new JLabel("Selecciona el gestor de bases de datos:");
        lblSelectDatabase.setBounds(20, 20, 250, 20);
        frame.getContentPane().add(lblSelectDatabase);

        databaseComboBox = new JComboBox<>(new String[]{"MySQL", "Access"});
        databaseComboBox.setBounds(250, 20, 100, 20);
        frame.getContentPane().add(databaseComboBox);

        JLabel lblHost = new JLabel("Host:");
        lblHost.setBounds(20, 60, 100, 20);
        frame.getContentPane().add(lblHost);

        hostTextField = new JTextField();
        hostTextField.setBounds(120, 60, 230, 20);
        frame.getContentPane().add(hostTextField);

        JLabel lblPort = new JLabel("Puerto:");
        lblPort.setBounds(20, 90, 100, 20);
        frame.getContentPane().add(lblPort);

        portTextField = new JTextField("3306");
        portTextField.setBounds(120, 90, 230, 20);
        frame.getContentPane().add(portTextField);

        // Crea un filtro para el Document del JTextField del puerto
        ((AbstractDocument) portTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = portTextField.getText();
                String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                // Valida si el nuevo texto es "3306", "3307" o "3308"
                if (!newText.equals("3306") && !newText.equals("3307") && !newText.equals("3308")) {
                    return;
                }

                super.replace(fb, offset, length, text, attrs);
            }
        });

        // Agrega un DocumentListener para detectar cuando el usuario cambia el foco
        portTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                // El usuario ha editado el campo, pero no hacemos nada en este momento
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                // El usuario ha editado el campo, pero no hacemos nada en este momento
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // El usuario ha terminado de editar el campo
                String portValue = portTextField.getText();
                if (!portValue.equals("3306") && !portValue.equals("3307") && !portValue.equals("3308")) {
                    // Muestra una alerta de puerto no válido
                    JOptionPane.showMessageDialog(frame, "El puerto no es válido. Debe ser 3306, 3307 o 3308.", "Puerto Inválido", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(20, 120, 100, 20);
        frame.getContentPane().add(lblUser);

        userTextField = new JTextField();
        userTextField.setBounds(120, 120, 230, 20);
        frame.getContentPane().add(userTextField);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(20, 150, 100, 20);
        frame.getContentPane().add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(120, 150, 230, 20);
        frame.getContentPane().add(passwordField);

        JLabel lblDbName = new JLabel("Nombre de la base de datos (DNS):");
        lblDbName.setBounds(20, 180, 200, 20);
        frame.getContentPane().add(lblDbName);

        dbNameTextField = new JTextField();
        dbNameTextField.setBounds(220, 180, 130, 20);
        frame.getContentPane().add(dbNameTextField);

        // Componentes para Access
        JLabel lblInstruccion = new JLabel("Coloque la dirección URL de su archivo de ACCESS");
        lblInstruccion.setBounds(20, 50, 400, 20);
        lblInstruccion.setVisible(false);
        frame.getContentPane().add(lblInstruccion);

        JLabel lblEjemplo = new JLabel("EJEMPLO: C:\\Users\\Usuario\\archivo.accdb");
        lblEjemplo.setBounds(20, 80, 400, 20);
        lblEjemplo.setVisible(false);
        frame.getContentPane().add(lblEjemplo);

        ////////////////////////////////////////
        JLabel lblBDaccess = new JLabel("Nombre de la base de datos:");
        lblBDaccess.setBounds(20, 110, 200, 20);
        lblBDaccess.setVisible(false);
        frame.getContentPane().add(lblBDaccess);

        bdAccessTextField = new JTextField();
        bdAccessTextField.setBounds(220, 110, 130, 20);
        frame.getContentPane().add(bdAccessTextField);
        bdAccessTextField.setVisible(false); // Inicialmente oculto

        JLabel lblUrl = new JLabel("URL:");
        lblUrl.setBounds(20, 140, 100, 20);
        frame.getContentPane().add(lblUrl);
        lblUrl.setVisible(false); // Inicialmente oculto

        urlTextField = new JTextField();
        urlTextField.setBounds(120, 140, 230, 20);
        frame.getContentPane().add(urlTextField);
        urlTextField.setVisible(false); // Inicialmente oculto

        JButton btnConnect = new JButton("Conectar");
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String selectedDatabase = databaseComboBox.getSelectedItem().toString();
                if ("MySQL".equals(selectedDatabase)) {
                    // Conexión a MySQL
                    String host = hostTextField.getText();
                    String port = portTextField.getText();
                    String user = userTextField.getText();
                    char[] password = passwordField.getPassword();
                    String passwordStr = new String(password);
                    String dbName = dbNameTextField.getText();

                    String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
                    try {
                        connection = DriverManager.getConnection(jdbcUrl, user, passwordStr);
                        JOptionPane.showMessageDialog(null, "Conexión exitosa a MySQL");
                        muestraBDTabla(connection);

                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error al conectar a MySQL");
                        System.out.println(ex.getMessage());
                    }
                } else if ("Access".equals(selectedDatabase)) {
                    // Conexión a Access
                    String accessUrl = urlTextField.getText();
                    String accessBD = bdAccessTextField.getText();
                    Connection connectionA = null;

                    try {
                        // Ruta de la base de datos de Access. Debes proporcionar la ruta correcta a tu archivo ACCDB.
                        String dbUrlAccess = "jdbc:ucanaccess://" + accessUrl + "\\" + accessBD + ".accdb";

                        // Establece la conexión
                        connectionA = DriverManager.getConnection(dbUrlAccess);

                        if (connectionA != null) {
                            //System.out.println("Conexión exitosa a la base de datos de Access");
                            JOptionPane.showMessageDialog(null, "Conexión exitosa a la base de datos de Access");
                            // Realiza operaciones en la base de datos aquí
                            objeto.muestraCreateAccess(connectionA, urlTextField, bdAccessTextField);
                            objeto.muestraAlterAccess(connectionA, urlTextField, bdAccessTextField);
                            objeto.muestraDropAccess(connectionA, urlTextField, bdAccessTextField);
                            // Cierra la conexión cuando hayas terminado
                            connectionA.close();
                        } else {
                            System.out.println("No se pudo establecer la conexión.");
                        }
                    } catch (SQLException ex) {
                        System.err.println("Error al conectar a la base de datos: " + ex.getMessage());
                    } finally {
                        try {
                            if (connectionA != null && !connectionA.isClosed()) {
                                connectionA.close();
                            }
                        } catch (SQLException ex) {
                            System.err.println("Error al cerrar la conexión: " + ex.getMessage());
                        }
                    }
                }
            }

        });

        btnConnect.setBounds(120, 220, 120, 30);
        frame.getContentPane().add(btnConnect);

        // Botón "Cancelar"
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnCancel.setBounds(250, 220, 100, 30);
        frame.getContentPane().add(btnCancel);

        // ActionListener para el JComboBox
        databaseComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedDatabase = databaseComboBox.getSelectedItem().toString();
                if ("MySQL".equals(selectedDatabase)) {
                    lblHost.setVisible(true);
                    hostTextField.setVisible(true);
                    lblPort.setVisible(true);
                    portTextField.setVisible(true);
                    lblUser.setVisible(true);
                    userTextField.setVisible(true);
                    lblPassword.setVisible(true);
                    passwordField.setVisible(true);
                    lblDbName.setVisible(true);
                    dbNameTextField.setVisible(true);

                    // Ocultar componentes de Access
                    lblInstruccion.setVisible(false);
                    lblBDaccess.setVisible(false);
                    bdAccessTextField.setVisible(false);
                    lblUrl.setVisible(false);
                    urlTextField.setVisible(false);
                    lblInstruccion.setVisible(false);
                    lblEjemplo.setVisible(false);

                } else if ("Access".equals(selectedDatabase)) {
                    lblHost.setVisible(false);
                    hostTextField.setVisible(false);
                    lblPort.setVisible(false);
                    portTextField.setVisible(false);
                    lblUser.setVisible(false);
                    userTextField.setVisible(false);
                    lblPassword.setVisible(false);
                    passwordField.setVisible(false);
                    lblDbName.setVisible(false);
                    dbNameTextField.setVisible(false);

                    // Mostrar componentes de Access
                    lblInstruccion.setVisible(true);
                    lblUrl.setVisible(true);
                    urlTextField.setVisible(true);
                    lblInstruccion.setVisible(true);
                    lblEjemplo.setVisible(true);
                    lblBDaccess.setVisible(true);
                    bdAccessTextField.setVisible(true);
                }
            }
        });

    }

    public void conecta(Connection connection) {

        if (connection != null) {
            // Crea y muestra la ventana de consulta LMD
            new QueryLMDWindow(connection).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "No se ha establecido una conexión a la base de datos.");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void muestraBDTabla(Connection connection) throws SQLException {

        // Crear la nueva ventana
        JFrame schemaFrame = new JFrame("Esquemas de Base de Datos");
        schemaFrame.setBounds(100, 100, 800, 350);
//////////////////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////
        // Crear el menú superior
        JMenuBar menuBar = new JMenuBar();
        JMenu schemaMenu = new JMenu("Crear Esquema");
        JMenuItem createSchemaItem = new JMenuItem("Diseño LDD");
        schemaMenu.add(createSchemaItem);

        JMenu schemaMenu2 = new JMenu("Sentencia SQL");
        JMenuItem sentenciaSchemaItem = new JMenuItem("Query LMD");
        schemaMenu.add(sentenciaSchemaItem);

        menuBar.add(schemaMenu);
        menuBar.add(schemaMenu2);
        schemaFrame.setJMenuBar(menuBar);

        createSchemaItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ventana de diálogo para ingresar el nombre del esquema y la cantidad de tablas
                JFrame schemaCreationFrame = new JFrame("Creación de Esquema");
                schemaCreationFrame.setBounds(100, 100, 300, 150);
                schemaCreationFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                schemaCreationFrame.getContentPane().setLayout(new GridLayout(3, 2));

                JLabel schemaNameLabel = new JLabel("Nombre del Esquema:");
                JTextField schemaNameField = new JTextField();

                // Agrega un DocumentFilter para validar el nombre del esquema
                ((AbstractDocument) schemaNameField.getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                        if (isValidSchemaName(string)) {
                            super.insertString(fb, offset, string, attr);
                        } else {
                            // Mostrar una alerta si el nombre del esquema es inválido
                            JOptionPane.showMessageDialog(schemaCreationFrame, "Nombre de esquema inválido. No se permiten espacios ni caracteres especiales, excepto guion bajo.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                        if (isValidSchemaName(text)) {
                            super.replace(fb, offset, length, text, attrs);
                        } else {
                            // Mostrar una alerta si el nombre del esquema es inválido
                            JOptionPane.showMessageDialog(schemaCreationFrame, "Nombre de esquema inválido. No se permiten espacios ni caracteres especiales, excepto guion bajo.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    // Función para validar el nombre del esquema
                    private boolean isValidSchemaName(String text) {
                        return !text.matches(".*[\\s!@#$%^&*()+\\=\\[\\]{};':\"\\\\|,.<>/?].*");
                    }
                });

                JLabel tableCountLabel = new JLabel("Cantidad de Tablas:");
                JTextField tableCountField = new JTextField();
                JButton createSchemaButton = new JButton("Crear Esquema");

                schemaCreationFrame.add(schemaNameLabel);
                schemaCreationFrame.add(schemaNameField);
                schemaCreationFrame.add(tableCountLabel);
                schemaCreationFrame.add(tableCountField);
                schemaCreationFrame.add(new JLabel()); // Espacio en blanco
                schemaCreationFrame.add(createSchemaButton);

                schemaCreationFrame.setVisible(true);

                createSchemaButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String schemaName = schemaNameField.getText();
                        int tableCount = Integer.parseInt(tableCountField.getText());
//                        int tableCount = 1;

                        if (schemaName.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Por favor, ingresa un nombre de esquema válido.");
                            return;
                        }

                        // Lógica para crear la nueva base de datos
                        String jdbcUrl = "jdbc:mysql://" + hostTextField.getText() + ":" + portTextField.getText() + "/";
                        try (Connection connection = DriverManager.getConnection(jdbcUrl, userTextField.getText(), new String(passwordField.getPassword()))) {
                            Statement statement = connection.createStatement();

                            // Crear la nueva base de datos
                            String createSchemaSQL = "CREATE DATABASE " + schemaName;
                            statement.executeUpdate(createSchemaSQL);

                            // Crear las tablas en la nueva base de datos
                            for (int i = 1; i <= tableCount; i++) {
                                String tableName = "Tabla " + i; // Puedes personalizar el nombre de la tabla
                                // Agregar el nombre de la tabla al esquema
                                schemaListModel.addElement(tableName);
                                // Crear una instancia de customTableInfoWindow para cada tabla
                                CustomTableInfoWindow customTableInfoWindow = new CustomTableInfoWindow(connection, tableName, schemaName, tableCount);
                                customTableInfoWindow.setVisible(true);
                            }

                            JOptionPane.showMessageDialog(null, "Base de datos y tablas creadas con éxito");

                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al crear la base de datos y las tablas");
                            System.out.println(ex.getMessage());
                        }
                        schemaCreationFrame.dispose(); // Cerrar la ventana de diálogo
                    }
                });
            }
        });

        sentenciaSchemaItem.addActionListener(e -> {
            if (connection != null) {
                // Crea y muestra la ventana de consulta LMD
                new QueryLMDWindow(connection).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "No se ha establecido una conexión a la base de datos.");
            }
        });

        schemaMenu.add(createSchemaItem);
        schemaMenu2.add(sentenciaSchemaItem);

//////////////////////////////////////////////////////////////////////////////////////////////////
        // Crear un SplitPane para dividir la ventana en dos partes
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200); // Ajustar la ubicación del divisor

        // Panel izquierdo para los esquemas
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> schemaListModel = new DefaultListModel<>();
        JList<String> schemaList = new JList<>(schemaListModel);

        // Consulta para obtener los esquemas de la base de datos
        String query = "SHOW DATABASES";
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String schemaName = resultSet.getString(1);
                schemaListModel.addElement(schemaName);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al obtener los esquemas");
            System.out.println(ex.getMessage());
        }

        leftPanel.add(new JScrollPane(schemaList), BorderLayout.CENTER);

        // Panel derecho para las tablas
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel schemaLabel = new JLabel("Tablas del Esquema: ");
        rightPanel.add(schemaLabel, BorderLayout.NORTH);

        DefaultListModel<String> tableListModel = new DefaultListModel<>();
        JList<String> tableList = new JList<>(tableListModel);

        rightPanel.add(new JScrollPane(tableList), BorderLayout.CENTER);

        // Botón "DROP" para eliminar la tabla seleccionada
        JButton dropTableButton = new JButton("DROP");
        dropTableButton.addActionListener(e -> dropSelectedTable(schemaList.getSelectedValue(), tableList.getSelectedValue()));

        rightPanel.add(dropTableButton, BorderLayout.SOUTH); // Agregar el botón "DROP" al panel derecho

        // Agregar los paneles al SplitPane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        schemaFrame.add(splitPane);
        schemaFrame.setVisible(true);

        schemaList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedSchema = schemaList.getSelectedValue();
                    if (selectedSchema != null) {
                        schemaLabel.setText("Tablas del Esquema: " + selectedSchema);
                        tableListModel.clear();
                        // Conectar a la base de datos MySQL nuevamente
                        String jdbcUrl = "jdbc:mysql://" + hostTextField.getText() + ":" + portTextField.getText() + "/" + selectedSchema;
                        try (Connection schemaConnection = DriverManager.getConnection(jdbcUrl, userTextField.getText(), new String(passwordField.getPassword()))) {
                            // Consulta para obtener las tablas dentro del esquema seleccionado
                            String tableQuery = "SHOW TABLES";
                            try (Statement tableStatement = schemaConnection.createStatement(); ResultSet tableResultSet = tableStatement.executeQuery(tableQuery)) {
                                while (tableResultSet.next()) {
                                    String tableName = tableResultSet.getString(1);
                                    tableListModel.addElement(tableName);
                                }
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Error al obtener las tablas");
                                System.out.println(ex.getMessage());
                            }
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al conectar al esquema");
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
        });

        tableList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedSchema = schemaList.getSelectedValue();
                    String selectedTable = tableList.getSelectedValue(); // Obtener la tabla seleccionada
                    if (selectedSchema != null && selectedTable != null) {
                        schemaLabel.setText("Tablas del Esquema: " + selectedSchema);
                        tableListModel.clear();
                        // Conectar a la base de datos MySQL nuevamente
                        String jdbcUrl = "jdbc:mysql://" + hostTextField.getText() + ":" + portTextField.getText() + "/" + selectedSchema;
                        try (Connection schemaConnection = DriverManager.getConnection(jdbcUrl, userTextField.getText(), new String(passwordField.getPassword()))) {
                            // Consulta para obtener las tablas dentro del esquema seleccionado
                            String tableQuery = "SHOW TABLES";
                            try (Statement tableStatement = schemaConnection.createStatement(); ResultSet tableResultSet = tableStatement.executeQuery(tableQuery)) {
                                while (tableResultSet.next()) {
                                    String tableName = tableResultSet.getString(1);
                                    tableListModel.addElement(tableName);
                                }
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Error al obtener las tablas");
                                System.out.println(ex.getMessage());
                            }

                            // Crear una instancia de TableInfoWindow para mostrar la información de la tabla seleccionada
                            new TableInfoWindow(connection, selectedTable, selectedSchema).setVisible(true); // Usar selectedTable aquí
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al conectar al esquema");
                            System.out.println(ex.getMessage());
                        } //finally {
//                            // Cierre de la conexión en el bloque finally
//                            try {
//                                if (connection != null && !connection.isClosed()) {
//                                    System.out.println("--------------Conexión al esquema cerrada correctamente.");
//                                }
//                            } catch (SQLException ex) {
//                                System.err.println("Error al cerrar la conexión al esquema: " + ex.getMessage());
//                            }
//                        }
                    }
                }
            }
        });
    }

    public void dropSelectedTable(String schemaName, String tableName) {

        if (schemaName != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar la base de datos?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String dropSchemaSQL = "DROP DATABASE " + schemaName;
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(dropSchemaSQL);
                    JOptionPane.showMessageDialog(null, "Base de datos eliminada exitosamente.");
                    schemaListModel.removeElement(schemaName); // Eliminar el esquema de la lista
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar la base de datos");
                    System.out.println(ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un esquema antes de eliminar.");
        }

        if (schemaName != null && tableName != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar la tabla?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String dropTableSQL = "DROP TABLE " + schemaName + "." + tableName;
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(dropTableSQL);
                    JOptionPane.showMessageDialog(null, "Tabla eliminada exitosamente.");
                    tableListModel.removeElement(tableName); // Eliminar la tabla de la lista
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar la tabla");
                    System.out.println(ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Selecciona un esquema y una tabla antes de eliminar.");
        }

    }

//    public Connection getSQLConnection(String host, String port, String user, char[] password, String dbName) {
//        Connection connection = null;
//
//        // Conexión a MySQL
//        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
//
//        try {
//            connection = DriverManager.getConnection(jdbcUrl, user, new String(password));
//        } catch (SQLException ex) {
//            System.err.println("Error al conectar a MySQL: " + ex.getMessage());
//        }
//
//        return connection;
//    }
    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public JComboBox<String> getDatabaseComboBox() {
        return databaseComboBox;
    }

    public void setDatabaseComboBox(JComboBox<String> databaseComboBox) {
        this.databaseComboBox = databaseComboBox;
    }

    public JTextField getHostTextField() {
        return hostTextField;
    }

    public void setHostTextField(JTextField hostTextField) {
        this.hostTextField = hostTextField;
    }

    public JTextField getPortTextField() {
        return portTextField;
    }

    public void setPortTextField(JTextField portTextField) {
        this.portTextField = portTextField;
    }

    public JTextField getUserTextField() {
        return userTextField;
    }

    public void setUserTextField(JTextField userTextField) {
        this.userTextField = userTextField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(JPasswordField passwordField) {
        this.passwordField = passwordField;
    }

    public JTextField getDbNameTextField() {
        return dbNameTextField;
    }

    public void setDbNameTextField(JTextField dbNameTextField) {
        this.dbNameTextField = dbNameTextField;
    }

    //-----------------------------------------------------------------------------
    public String getHost() {
        return hostTextField.getText();
    }

    public String getPort() {
        return portTextField.getText();
    }

    public String getUser() {
        return userTextField.getText();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public String getDatabaseName() {
        return dbNameTextField.getText();
    }

}

class TableInfoWindow extends JFrame {

    //Connection connection2;
    String schemaName;
    JTable table;
    DefaultTableModel tableModel;
    boolean isEditable;
    boolean isFirstRow = true;

    DatabaseConnectionApp databaseApp = new DatabaseConnectionApp();
    Connection connection = databaseApp.getConnection();
    String host = databaseApp.getHost();
    String port = databaseApp.getPort();
    String user = databaseApp.getUser();
    char[] password = databaseApp.getPassword();
    String dbName = databaseApp.getDatabaseName();

//    private DatabaseConnectionApp databaseApp;
//    Connection connection = databaseApp.getSQLConnection(host, port, user, password, dbName);
    public TableInfoWindow(Connection connection, String tableName, String schemaName) {
        this.connection = connection;
//        databaseApp = new DatabaseConnectionApp();
        //Connection connection = databaseApp.getConnection();
        this.schemaName = schemaName; // Asignar el nombre del esquema
        setTitle("Información de la Tabla: " + tableName);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicialmente, la tabla es solo para ver, no para editar
        isEditable = false;

        // Crear un modelo de tabla
        tableModel = new DefaultTableModel();

        // Crear una tabla con el modelo y asignarla a la variable de instancia
        table = new JTable(tableModel);

        // Agregar la tabla a un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        // Crear un JPanel para contener la tabla y el botón
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Añadir el JScrollPane al JPanel
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(scrollPane, gbc);

        add(panel, BorderLayout.CENTER);

        // Consulta SQL para obtener la información de la tabla, incluyendo la descripción y si es NULL o NOT NULL
        String query = "SELECT column_name, column_type, column_key, character_maximum_length, column_default, column_comment, is_nullable FROM information_schema.columns WHERE table_name = '" + tableName + "'";

        try {
            // Crear el statement
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Agregar las columnas al modelo de tabla
            tableModel.addColumn("Nombre del Atributo");
            String columnName;
            tableModel.addColumn("Tipo de Dato");
            String columnType;
            tableModel.addColumn("Tamaño");
            String columnSize;
            tableModel.addColumn("Llave Primaria");
            String columnKey;
            tableModel.addColumn("Llave Foránea");
            tableModel.addColumn("Valor Predeterminado");
            String columnDefaultValue;
            tableModel.addColumn("Descripción");
            String columnDescription;
            tableModel.addColumn("Nulo");
            String isNullable;
            tableModel.addColumn("Índice");
            String indexType;

            // Agregar las filas de datos
            while (resultSet.next()) {
                columnName = resultSet.getString("column_name");
                columnType = resultSet.getString("column_type");
                int indexOfOpenParenthesis = columnType.indexOf("("); // Buscar el índice del primer paréntesis
                if (indexOfOpenParenthesis != -1) {
                    columnType = columnType.substring(0, indexOfOpenParenthesis); // Obtener la subcadena antes del paréntesis
                }
                columnKey = resultSet.getString("column_key");
                columnSize = resultSet.getString("character_maximum_length");
                columnDefaultValue = resultSet.getString("column_default");
                columnDescription = resultSet.getString("column_comment");
                isNullable = resultSet.getString("is_nullable");

                indexType = "NO"; // Por defecto, no tiene índice
                if (columnKey != null) {
                    if (columnKey.equals("PRI")) {
                        indexType = "UNIQUE";
                    } else if (columnKey.equals("MUL")) {
                        indexType = "MULTIPLE";
                    } else if (columnKey.equals("SPATIAL")) {
                        indexType = "SPATIAL";
                    } else if (columnKey.equals("FULLTEXT")) {
                        indexType = "FULLTEXT";
                    } else if (columnKey.equals("UNI")) {
                        indexType = "UNIQUE";
                    }
                }

                // Agregar una fila al modelo de tabla
                tableModel.addRow(new Object[]{columnName, columnType, columnSize, columnKey.equals("PRI"), columnKey.equals("MUL"), columnDefaultValue, columnDescription, isNullable, indexType});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }

        // Botón para habilitar la edición
        JButton modifyAttributesButton = new JButton("Modificar Atributos");
        modifyAttributesButton.addActionListener(e -> toggleEditAttributes());

        // Añadir el botón al JPanel con un espacio
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0); // Agregar espacio en la parte superior
        panel.add(modifyAttributesButton, gbc);

    }

    // Método para alternar entre vista de solo lectura y edición de la tabla
    public void toggleEditAttributes() {
        isEditable = !isEditable;
        table.setEnabled(isEditable);
        tableModel.fireTableStructureChanged();

        // Si se habilita la edición, muestra los botones adicionales
        if (isEditable) {
            addAdditionalButtons();
        } else {
            removeAdditionalButtons();
        }

        // Repintar la ventana para reflejar los cambios
        revalidate();
    }

    // Método para eliminar los botones adicionales
    public void removeAdditionalButtons() {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] buttonPanelComponents = panel.getComponents();
                for (Component buttonPanelComponent : buttonPanelComponents) {
                    if (buttonPanelComponent instanceof JPanel) {
                        panel.remove(buttonPanelComponent);
                    }
                }
            }
        }
        // Repintar la ventana para reflejar los cambios
        revalidate();
    }

    // Método para agregar los botones adicionales
    public void addAdditionalButtons() {
        // Botones para modificar la tabla (Agregar Fila, ADD, MODIFY, DROP)
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        JButton addRowButton = new JButton("Agregar Fila");
        addRowButton.addActionListener(e -> addEmptyRow());
        buttonPanel.add(addRowButton);

        JButton addButton = new JButton("ADD");
        addButton.addActionListener(e -> {
            try {
                addRow();
            } catch (SQLException ex) {
                Logger.getLogger(TableInfoWindow.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println(ex.getMessage());
            }
        });
        buttonPanel.add(addButton);

        JButton modifyButton = new JButton("MODIFY");
        modifyButton.addActionListener(e -> {
            try {
                modifyRow();
            } catch (SQLException ex) {
                Logger.getLogger(TableInfoWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonPanel.add(modifyButton);

        JButton dropButton = new JButton("DROP");
        dropButton.addActionListener(e -> {
            try {
                dropRow();
            } catch (SQLException ex) {
                Logger.getLogger(TableInfoWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        buttonPanel.add(dropButton);
    }

    public void addEmptyRow() {
        if (isFirstRow || isRowValid()) {
            // Agregar una fila vacía al modelo de la tabla
            tableModel.addRow(new Object[]{"", "", "", "true", "false", "", ""});
            isFirstRow = false; // Desactiva la bandera después de agregar la primera fila
        } else {
            JOptionPane.showMessageDialog(this, "Debes completar al menos la informacion minima del atributo para generar otro", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isRowValid() {
        int lastRow = tableModel.getRowCount() - 1;
        int filledCellCount = 0;

        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            // Comprueba si la última fila tiene contenido en la celda
            if (tableModel.getValueAt(lastRow, col) != null && !tableModel.getValueAt(lastRow, col).toString().trim().isEmpty()) {
                filledCellCount++;
            }
        }

        return filledCellCount >= 4;
    }

    public void addRow() throws SQLException {
        int row = 0;

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No se han agregado atributos para añadir.");
            return;
        }

        String h = schemaName; // Reemplaza "nombre_del_esquema" por el nombre real de tu esquema
        String tableName = getTitle().substring("Información de la Tabla: ".length());

        // Obtiene la última fila en el modelo de la tabla
        int lastRow = tableModel.getRowCount() - 1;
        String columnName = (String) tableModel.getValueAt(lastRow, 0);
        String columnType = (String) tableModel.getValueAt(lastRow, 1);
        String columnSize = (String) tableModel.getValueAt(lastRow, 2);
        String isPrimaryKey = (String) tableModel.getValueAt(lastRow, 3);
        String isForeignKey = (String) tableModel.getValueAt(lastRow, 4);
        String defaultValue = (String) tableModel.getValueAt(lastRow, 5);
        String columnDescription = (String) tableModel.getValueAt(lastRow, 6);
        String isNullable = (String) tableModel.getValueAt(lastRow, 7);
        String index = (String) tableModel.getValueAt(lastRow, 8);

        // Realiza las validaciones de los datos ingresados por el usuario
        boolean isDataValid = true;
        if (!isColumnNameValid(columnName)) {
            JOptionPane.showMessageDialog(this, "El nombre de columna no es válido. No debe contener espacios ni caracteres especiales.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isColumnTypeValid(columnType)) {
            JOptionPane.showMessageDialog(this, "El tipo de dato no es válido. Debe ser int, double, numeric, char, varchar o date.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isColumnSizeValid(columnType, columnSize)) {
            JOptionPane.showMessageDialog(this, "El tamaño no es válido. Debe contener solo números.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isPrimaryKeyValid(isPrimaryKey)) {
            JOptionPane.showMessageDialog(this, "La configuración de clave primaria no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isForeignKeyValid(isForeignKey)) {
            JOptionPane.showMessageDialog(this, "La configuración de clave foránea no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isDefaultValueValid(defaultValue)) {
            JOptionPane.showMessageDialog(this, "El valor predeterminado no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isColumnDescriptionValid(columnDescription)) {
            JOptionPane.showMessageDialog(this, "La descripción de columna no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isNullableValid(isNullable)) {
            JOptionPane.showMessageDialog(this, "La configuración de nulo no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isIndex(index)) {
            JOptionPane.showMessageDialog(this, "No es un indice valido.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }

        if (isDataValid) {
            // Construye la sentencia ALTER TABLE con todas las características
            StringBuilder alterStatement = new StringBuilder("USE ");
            alterStatement.append(h);
            alterStatement.append(";");
            alterStatement.append("ALTER TABLE ");
            alterStatement.append(tableName);
            alterStatement.append(" ADD ");
            alterStatement.append(columnName);
            alterStatement.append(" ");
            alterStatement.append(columnType);
            if (columnSize != null && !columnSize.isEmpty()) {
                alterStatement.append("(");
                alterStatement.append(columnSize);
                alterStatement.append(")");
            }
            if (isPrimaryKey.equals("true")) {
                // Añadir clave primaria (usando el nombre de la primera columna)
                alterStatement.append(", PRIMARY KEY (");
                alterStatement.append((String) tableModel.getValueAt(lastRow, 0)); // Nombre de la primera columna
                alterStatement.append(")");
            }

            // Restricción de índice
            if (index.equals("UNIQUE") || index.equals("MULTIPLE") || index.equals("FULLTEXT") || index.equals("SPARTIAL")) {
                alterStatement.append(", INDEX (");
                alterStatement.append((String) tableModel.getValueAt(lastRow, 0)); // Nombre de la columna actual
                alterStatement.append(")");
            }

            if (isForeignKey.equals("true")) {
                int confirmF = JOptionPane.showConfirmDialog(null, "¿Deseas agregar una llave foranea?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirmF == JOptionPane.YES_OPTION) {
                    String referencedTable = JOptionPane.showInputDialog("Ingrese el nombre de la tabla referenciada para la llave foránea:");
                    String nombreConstraint = JOptionPane.showInputDialog("Ingrese el nombre del constraint para la llave foránea:");
                    if (referencedTable != null && !referencedTable.isEmpty() || nombreConstraint != null && !nombreConstraint.isEmpty()) {
                        alterStatement.append(", CONSTRAINT " + nombreConstraint);
                        alterStatement.append(" FOREIGN KEY (");
                        alterStatement.append((String) tableModel.getValueAt(lastRow, 0)); // Nombre de la columna actual
                        alterStatement.append(") REFERENCES " + referencedTable + " (");
                        alterStatement.append((String) tableModel.getValueAt(lastRow, 0));
                        alterStatement.append(")");
                    } else {
                        JOptionPane.showMessageDialog(null, "No se proporcionó un nombre de tabla válido.");
                    }
                }
            }
            // A checar sentencia con la version de MariaDB
            if (defaultValue != null && !defaultValue.isEmpty()) {
                alterStatement.append(" DEFAULT '");
                alterStatement.append(defaultValue);
                alterStatement.append("'");
            }
            if (isNullable.equalsIgnoreCase("NO")) {
                alterStatement.append(" NOT NULL");
            }
            if (columnDescription != null && !columnDescription.isEmpty()) {
                alterStatement.append(" COMMENT '");
                alterStatement.append(columnDescription);
                alterStatement.append("'");
            }

            // Agrega el punto y coma al final de la sentencia
            alterStatement.append(";");

            System.out.println(alterStatement.toString());
            CopyableTextDialog.showCopyableTextDialog(alterStatement.toString());

            //Connection connection = databaseApp.getConnection();
            try {
                if (connection != null && !connection.isClosed()) {
                    // Tu lógica para agregar una columna
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(alterStatement.toString());
                    JOptionPane.showMessageDialog(this, "Columna agregada exitosamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar columna");
                System.out.println(ex.getMessage());
            }
        }
    }

    // Agregar este método para habilitar la edición de las celdas de la tabla
    public void enableCellEditing() {
        table.setEnabled(true);
        table.setCellSelectionEnabled(true);
        table.requestFocus();
        isEditable = true;
    }

    public void modifyRow() throws SQLException {
        if (!isEditable) {
            enableCellEditing();
            return;
        }

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para modificar.");
            return;
        }

        String h = schemaName; // Reemplaza "nombre_del_esquema" por el nombre real de tu esquema
        String tableName = getTitle().substring("Información de la Tabla: ".length());
        String columnName = (String) tableModel.getValueAt(selectedRow, 0);

        String[] options = {
            "Cambiar nombre de columna",
            "Cambiar tipo de dato",
            "Cambiar tamaño",
            "Permitir valores nulos (YES/NO)",
            "Cambiar descripción",
            "Cancelar"
        };

        int choice = JOptionPane.showOptionDialog(this,
                "Selecciona la característica que deseas modificar:",
                "Modificar Característica de Columna",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == -1 || choice == 6) {
            // El usuario canceló la operación o cerró la ventana de opciones
            return;
        }

        StringBuilder alterStatement = new StringBuilder("USE ");
        alterStatement.append(h);
        alterStatement.append(";");
        alterStatement.append("ALTER TABLE ");
        alterStatement.append(tableName);

        switch (choice) {
            case 0:
                // Cambiar nombre de columna
                String newColumnName = JOptionPane.showInputDialog(this, "Nuevo nombre de la columna:");
                if (newColumnName != null && !newColumnName.isEmpty()) {
                    if (isColumnNameValid(newColumnName)) {
                        alterStatement.append(" CHANGE ");
                        alterStatement.append(columnName);
                        alterStatement.append(" ");
                        alterStatement.append(newColumnName);
                        alterStatement.append(" ");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 1));
                    } else {
                        JOptionPane.showMessageDialog(this, "El nombre de columna no es válido. No debe contener espacios ni caracteres especiales.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case 1:
                // Cambiar tipo de dato
                String newColumnType = JOptionPane.showInputDialog(this, "Nuevo tipo de dato:");
                if (newColumnType != null && !newColumnType.isEmpty()) {
                    if (isColumnTypeValid(newColumnType)) {
                        alterStatement.append(" MODIFY ");
                        alterStatement.append(columnName);
                        alterStatement.append(" ");
                        alterStatement.append(newColumnType);
                        alterStatement.append("(");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 2));
                        alterStatement.append(")");
                    } else {
                        JOptionPane.showMessageDialog(this, "El tipo de dato no es válido. Debe ser int, double, numeric, char, varchar o date.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case 2:
                // Cambiar tamaño
                String newColumnSize = JOptionPane.showInputDialog(this, "Nuevo tamaño:");
                if (newColumnSize != null && !newColumnSize.isEmpty()) {
                    if (isColumnSizeValid(newColumnType = null, newColumnSize)) {
                        alterStatement.append(" MODIFY ");
                        alterStatement.append(columnName);
                        alterStatement.append(" ");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 1));
                        alterStatement.append("(");
                        alterStatement.append(newColumnSize);
                        alterStatement.append(")");
                    } else {
                        JOptionPane.showMessageDialog(this, "El tamaño no es válido. Debe contener solo números.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case 3:
                // Permitir valores nulos (YES/NO)
                String newColumnNullable = JOptionPane.showInputDialog(this, "¿Permitir valores nulos? (NULL/NOT NULL):");
                if (newColumnNullable != null && !newColumnNullable.isEmpty()) {
                    if (isNullableValid(newColumnNullable)) {
                        alterStatement.append(" MODIFY ");
                        alterStatement.append(columnName);
                        alterStatement.append(" ");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 1));
                        alterStatement.append("(");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 2));
                        alterStatement.append(")");
                        alterStatement.append(" ");
                        alterStatement.append(newColumnNullable);
                    } else {
                        JOptionPane.showMessageDialog(this, "El valor para permitir nulos no es válido. Debe ser YES o NO.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case 4:
                // Cambiar valor predeterminado
                String newColumnDefault = JOptionPane.showInputDialog(this, "Nuevo valor predeterminado:");
                if (newColumnDefault != null) {
                    if (isDefaultValueValid(newColumnDefault)) {
                        alterStatement.append(" MODIFY ");
                        alterStatement.append(columnName);
                        alterStatement.append(" ");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 1));
                        alterStatement.append("(");
                        alterStatement.append((String) tableModel.getValueAt(selectedRow, 2));
                        alterStatement.append(")");
                        alterStatement.append(" DEFAULT '");
                        alterStatement.append(newColumnDefault);
                        alterStatement.append("'");
                    } else {
                        JOptionPane.showMessageDialog(this, "El valor predeterminado no es válido. Debe ser NULL o estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                break;
            case 5:
                // Cambiar descripción
                String newColumnDescription = JOptionPane.showInputDialog(this, "Nueva descripción:");
                if (newColumnDescription != null) {
                    alterStatement.append(" MODIFY ");
                    alterStatement.append(columnName);
                    alterStatement.append(" ");
                    alterStatement.append((String) tableModel.getValueAt(selectedRow, 1));
                    alterStatement.append("(");
                    alterStatement.append((String) tableModel.getValueAt(selectedRow, 2));
                    alterStatement.append(")");
                    alterStatement.append(" COMMENT '");
                    alterStatement.append(newColumnDescription);
                    alterStatement.append("'");
                }
                break;
            default:
                // No debería llegar aquí
                break;
        }

        // Agrega el punto y coma al final de la sentencia
        alterStatement.append(";");

        System.out.println(alterStatement.toString());
        CopyableTextDialog.showCopyableTextDialog(alterStatement.toString());

        //Connection connection = databaseApp.getSQLConnection();
        try {
            if (connection != null && !connection.isClosed()) {
                // Tu lógica para agregar una columna
                Statement statement = connection.createStatement();
                statement.executeUpdate(alterStatement.toString());
                JOptionPane.showMessageDialog(this, "Característica de columna modificada exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar la característica de columna");
            System.out.println(ex.getMessage());
        }
    }

    private boolean isColumnNameValid(String columnName) {
        // Validación de nombre de columna (solo guiones bajos y letras)
        return columnName.matches("^[a-zA-Z_]+$");
    }

    private boolean isColumnTypeValid(String columnType) {
        // Validación de tipo de dato
        String[] tiposValidos = {"int", "double", "numeric", "char", "varchar", "date"};
        return Arrays.asList(tiposValidos).contains(columnType);
    }

    private boolean isColumnSizeValid(String columnType, String columnSize) {
        // Si el tipo de dato es int, double, numeric o date, el tamaño debe estar vacío o ser nulo
        if ("int".equalsIgnoreCase(columnType) || "double".equalsIgnoreCase(columnType)
                || "numeric".equalsIgnoreCase(columnType) || "date".equalsIgnoreCase(columnType)) {
            return columnSize == null || columnSize.isEmpty();
        }

        // Para otros tipos de datos, validación de tamaño (solo números)
        return columnSize.matches("^[0-9]+$");
    }

    private boolean isPrimaryKeyValid(String isPrimaryKey) {
        // Validación de clave primaria (solo "true" o "false")
        return "true".equalsIgnoreCase(isPrimaryKey) || "false".equalsIgnoreCase(isPrimaryKey);
    }

    private boolean isForeignKeyValid(String isForeignKey) {
        // Validación de clave foránea (solo "true" o "false")
        return "true".equalsIgnoreCase(isForeignKey) || "false".equalsIgnoreCase(isForeignKey);
    }

    private boolean isDefaultValueValid(String defaultValue) {
        // Validación de valor predeterminado (solo "NULL" o valor vacío)
        return "NULL".equalsIgnoreCase(defaultValue) || defaultValue.isEmpty();
    }

    private boolean isColumnDescriptionValid(String columnDescription) {
        // Validación de descripción de columna (puede personalizarse según tus necesidades)
        return true; // Agregar la validación adecuada
    }

    private boolean isNullableValid(String isNullable) {
        // Validación de nulo (solo "YES" o "NO")
        return "YES".equalsIgnoreCase(isNullable) || "NO".equalsIgnoreCase(isNullable);
    }

    private boolean isIndex(String index) {
        // Validación de nulo (solo "YES" o "NO")
        return "NO".equalsIgnoreCase(index) || "UNIQUE".equalsIgnoreCase(index) || "MULTIPLE".equalsIgnoreCase(index) || "SPATIAL".equalsIgnoreCase(index) || "FULLTEXT".equalsIgnoreCase(index);
    }

    public void dropRow() throws SQLException {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila para eliminar.");
            return;
        }

        String columnName = (String) tableModel.getValueAt(selectedRow, 0);
        String tableName = getTitle().substring("Información de la Tabla: ".length());
        String h = schemaName;

        // Elimina la columna si no es una clave primaria ni un índice válido
        String dropColumnStatement = "USE " + h + ";" + "ALTER TABLE " + tableName + " DROP COLUMN " + columnName + ";";
        executeSQLStatement(h, dropColumnStatement);
        System.out.println(dropColumnStatement.toString());
        CopyableTextDialog.showCopyableTextDialog(dropColumnStatement.toString());

        // Verifica si es un índice, clave primaria o columna
        String index = (String) tableModel.getValueAt(selectedRow, 8);
        String isPrimaryKey = (String) tableModel.getValueAt(selectedRow, 3);

        if (isPrimaryKey.equals("true")) {
            // Elimina la restricción de clave primaria
            String alterPrimaryKeyStatement = "USE " + h + ";" + "ALTER TABLE " + tableName + " DROP PRIMARY KEY;";
            executeSQLStatement(h, alterPrimaryKeyStatement);
            System.out.println(alterPrimaryKeyStatement.toString());
            CopyableTextDialog.showCopyableTextDialog(alterPrimaryKeyStatement.toString());
        } else if ("UNIQUE".equals(index) || "MULTIPLE".equals(index) || "SPATIAL".equals(index) || "FULLTEXT".equals(index)) {
            // Elimina el índice si es único, múltiple, espacial o de texto completo
            String dropIndexStatement = "USE " + h + ";" + "ALTER TABLE " + tableName + " DROP INDEX " + columnName + ";";
            executeSQLStatement(h, dropIndexStatement);
            System.out.println(dropIndexStatement.toString());
            CopyableTextDialog.showCopyableTextDialog(dropIndexStatement.toString());
        }

        // Elimina la fila de la tabla
        tableModel.removeRow(selectedRow);
    }

    public void executeSQLStatement(String schema, String sqlStatement) throws SQLException {

        //Connection connection = databaseApp.getSQLConnection();
        // Asegúrate de que la instancia de DatabaseConnectionApp tenga los valores configurados
        if (connection != null && !connection.isClosed()) {
            // Tu lógica para agregar una columna
            try {
                Statement statement = connection.createStatement();
                statement.executeUpdate("USE " + schema + ";");
                statement.executeUpdate(sqlStatement);
                JOptionPane.showMessageDialog(this, "Columna o índice eliminado exitosamente.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar columna o índice");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
        }

    }
}

class CustomTableInfoWindow extends TableInfoWindow {

    public int tableCount;
    int selectedRow = table.getSelectedRow();
    private JTextField tableNameField; // Declarar tableNameField como un campo de instancia
    String h = schemaName; // Reemplaza "nombre_del_esquema" por el nombre real de tu esquema
    boolean isFirstRow = true;
//    private DatabaseConnectionApp databaseApp;
//    Connection connection = databaseApp.getSQLConnection(host, port, user, password, dbName);

    public CustomTableInfoWindow(Connection connection, String tableName, String schemaName, int tableCount) {
        super(connection, tableName, schemaName);
        this.tableCount = tableCount;
        addCustomComponents();
        databaseApp = new DatabaseConnectionApp();
        // Botones para modificar la tabla (Agregar Fila y Ejecutar)
        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

//        JButton addTableButton = new JButton("Agregar Tabla"); // Agregar el botón "Agregar Tabla"
//        addTableButton.addActionListener(e -> addAdditionalTable()); // Agregar el ActionListener
//        buttonPanel.add(addTableButton);
        JButton addRowButton = new JButton("Agregar Fila");
        addRowButton.addActionListener(e -> addEmptyRow());
        buttonPanel.add(addRowButton);

        JButton executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(e -> {
            try {
                executeChanges();
            } catch (SQLException ex) {
                Logger.getLogger(CustomTableInfoWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }); // Agregar este método
        buttonPanel.add(executeButton);

    }

    public void addCustomComponents() {
        // Agrega los componentes personalizados aquí, como los labels "Tabla 1:", "Tabla 2:", y los campos de texto.
        // También puedes personalizar la disposición de los botones según tus necesidades.
        for (int i = 1; i <= tableCount; i++) {
            JPanel tablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel tableLabel = new JLabel("Tabla :");
            this.tableNameField = new JTextField(20); // Ajusta el tamaño según sea necesario

            // Agregar la validación al JTextField
            ((AbstractDocument) this.tableNameField.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    if (isValidTableName(string)) {
                        super.insertString(fb, offset, string, attr);
                    } else {
                        // Mostrar una alerta si el nombre de la tabla es inválido
                        JOptionPane.showMessageDialog(CustomTableInfoWindow.this, "Nombre de tabla inválido. No se permiten espacios ni caracteres especiales.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (isValidTableName(text)) {
                        super.replace(fb, offset, length, text, attrs);
                    } else {
                        // Mostrar una alerta si el nombre de la tabla es inválido
                        JOptionPane.showMessageDialog(CustomTableInfoWindow.this, "Nombre de tabla inválido. No se permiten espacios ni caracteres especiales.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

                // Función para validar el nombre de la tabla
                private boolean isValidTableName(String text) {
                    return !text.matches(".*[\\s!@#$%^&*()+\\=\\[\\]{};':\"\\\\|,.<>/?].*");
                }
            });

            tablePanel.add(tableLabel);
            tablePanel.add(this.tableNameField);
            add(tablePanel, BorderLayout.NORTH);
        }

        // Quitar los botones no deseados de "TableInfoWindow"
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] buttonPanelComponents = panel.getComponents();
                for (Component buttonPanelComponent : buttonPanelComponents) {
                    if (buttonPanelComponent instanceof JButton) {
                        JButton button = (JButton) buttonPanelComponent;
                        if (!button.getText().equals("Agregar Fila") && !button.getText().equals("Ejecutar")) {
                            panel.remove(buttonPanelComponent);
                        }
                    }
                }
            }
        }
        // Repintar la ventana para reflejar los cambios
        revalidate();
    }

//    public void addAdditionalTable() {
//        // Crear una nueva instancia de CustomTableInfoWindow con un valor predeterminado de 1 tabla
//        String newTableName = "Tabla"; // Puedes personalizar el nombre de la nueva tabla
//        CustomTableInfoWindow newTableInfoWindow = new CustomTableInfoWindow(connection, newTableName, h, 1);
//        newTableInfoWindow.setVisible(true);
//    }
    public void addEmptyRow() {
        if (isFirstRow || isRowValid()) {
            // Agregar una fila vacía al modelo de la tabla
            tableModel.addRow(new Object[]{"", "", "", "true", "false", "", ""});
            isFirstRow = false; // Desactiva la bandera después de agregar la primera fila
        } else {
            JOptionPane.showMessageDialog(this, "Debes completar al menos la informacion minima del atributo para generar otro", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isRowValid() {
        int lastRow = tableModel.getRowCount() - 1;
        int filledCellCount = 0;

        for (int col = 0; col < tableModel.getColumnCount(); col++) {
            // Comprueba si la última fila tiene contenido en la celda
            if (tableModel.getValueAt(lastRow, col) != null && !tableModel.getValueAt(lastRow, col).toString().trim().isEmpty()) {
                filledCellCount++;
            }
        }

        return filledCellCount >= 4;
    }

    public void executeChanges() throws SQLException {
        int row = 0;

        StringBuilder createTableSQL = new StringBuilder("USE ");
    createTableSQL.append(h);
    createTableSQL.append(";");
    createTableSQL.append("CREATE TABLE ");
    createTableSQL.append(getTableNameFromTextField());
    createTableSQL.append(" (");

    for (row = 0; row < tableModel.getRowCount(); row++) {
        String columnName = (String) tableModel.getValueAt(row, 0);
        String columnType = (String) tableModel.getValueAt(row, 1);
        String columnSize = (String) tableModel.getValueAt(row, 2);
        String isPrimaryKey = (String) tableModel.getValueAt(row, 3);
        String isForeignKey = (String) tableModel.getValueAt(row, 4);
        String defaultValue = (String) tableModel.getValueAt(row, 5);
        String columnDescription = (String) tableModel.getValueAt(row, 6);
        String isNullable = (String) tableModel.getValueAt(row, 7);
        String indexT = (String) tableModel.getValueAt(row, 8);
        
        // Realiza las validaciones de los datos ingresados por el usuario
        boolean isDataValid = true;
        if (!isColumnNameValid(columnName)) {
            JOptionPane.showMessageDialog(this, "El nombre de columna no es válido. No debe contener espacios ni caracteres especiales.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isColumnTypeValid(columnType)) {
            JOptionPane.showMessageDialog(this, "El tipo de dato no es válido. Debe ser int, double, numeric, char, varchar o date.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isColumnSizeValid(columnType, columnSize)) {
            JOptionPane.showMessageDialog(this, "El tamaño no es válido. Debe contener solo números.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isPrimaryKeyValid(isPrimaryKey)) {
            JOptionPane.showMessageDialog(this, "La configuración de clave primaria no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isForeignKeyValid(isForeignKey)) {
            JOptionPane.showMessageDialog(this, "La configuración de clave foránea no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isDefaultValueValid(defaultValue)) {
            JOptionPane.showMessageDialog(this, "El valor predeterminado no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isColumnDescriptionValid(columnDescription)) {
            JOptionPane.showMessageDialog(this, "La descripción de columna no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isNullableValid(isNullable)) {
            JOptionPane.showMessageDialog(this, "La configuración de nulo no es válida.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        if (!isIndex(indexT)) {
            JOptionPane.showMessageDialog(this, "No es un indice valido.", "Error", JOptionPane.ERROR_MESSAGE);
            isDataValid = false;
        }
        
         // Agregar columna a la sentencia SQL
        createTableSQL.append(columnName);
        createTableSQL.append(" ");
        createTableSQL.append(columnType);

        // Tamaño
        if (columnSize != null && !columnSize.isEmpty()) {
            createTableSQL.append("(");
            createTableSQL.append(columnSize);
            createTableSQL.append(")");
        }

        // Descripción de columna
        if (columnDescription != null && !columnDescription.isEmpty()) {
            createTableSQL.append(" COMMENT '");
            createTableSQL.append(columnDescription);
            createTableSQL.append("'");
        }

        // Restricción NULL o NOT NULL
        if (isNullable.equals("NO")) {
            createTableSQL.append(" NOT NULL");
        }

        if (row < tableModel.getRowCount() - 1) {
            createTableSQL.append(", ");
        }
    }

    // Añadir clave primaria
    createTableSQL.append(", PRIMARY KEY (");
    createTableSQL.append((String) tableModel.getValueAt(0, 0)); // Nombre de la primera columna
    createTableSQL.append(")");

    // Añadir índices
    for (row = 0; row < tableModel.getRowCount(); row++) {
        int confirmIN = JOptionPane.showConfirmDialog(null, "¿Deseas agregar un índice?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmIN == JOptionPane.YES_OPTION) {
            // Restricción de índice
            if (tableModel.getValueAt(row, 8) != null) {
                createTableSQL.append(", INDEX (");
                createTableSQL.append((String) tableModel.getValueAt(row, 0)); // Nombre de la columna actual
                createTableSQL.append(")");
            }
        }
    }

    // Añadir clave foránea
    for (row = 0; row < tableModel.getRowCount(); row++) {
        int confirmF = JOptionPane.showConfirmDialog(null, "¿Deseas agregar una llave foránea?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirmF == JOptionPane.YES_OPTION) {
            String referencedTable = JOptionPane.showInputDialog("Ingrese el nombre de la tabla referenciada para la llave foránea:");
            String nombreConstraint = JOptionPane.showInputDialog("Ingrese el nombre del constraint para la llave foránea:");
            if (referencedTable != null && !referencedTable.isEmpty() || nombreConstraint != null && !nombreConstraint.isEmpty()) {
                createTableSQL.append(", CONSTRAINT " + nombreConstraint);
                createTableSQL.append(" FOREIGN KEY (");
                createTableSQL.append((String) tableModel.getValueAt(row, 0)); // Nombre de la columna actual
                createTableSQL.append(") REFERENCES " + referencedTable + " (");
                createTableSQL.append((String) tableModel.getValueAt(row, 0));
                createTableSQL.append(")");
            } else {
                JOptionPane.showMessageDialog(null, "No se proporcionó un nombre de tabla válido.");
            }
        }
    }

    createTableSQL.append(");");

    System.out.println(createTableSQL.toString());
    CopyableTextDialog.showCopyableTextDialog(createTableSQL.toString());

            //Connection connection = databaseApp.getSQLConnection();
            // Asegúrate de que la instancia de DatabaseConnectionApp tenga los valores configurados
            if (connection != null && !connection.isClosed()) {
                // Tu lógica para agregar una columna
                try {
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(createTableSQL.toString());
                    JOptionPane.showMessageDialog(null, "Tabla creada con éxito");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al crear la tabla");
                    System.out.println(ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
            }
    }

    private boolean isColumnNameValid(String columnName) {
        // Validación de nombre de columna (solo guiones bajos y letras)
        return columnName.matches("^[a-zA-Z_]+$");
    }

    private boolean isColumnTypeValid(String columnType) {
        // Validación de tipo de dato
        String[] tiposValidos = {"int", "double", "numeric", "char", "varchar", "date"};
        return Arrays.asList(tiposValidos).contains(columnType);
    }

    private boolean isColumnSizeValid(String columnType, String columnSize) {
        // Si el tipo de dato es int, double, numeric o date, el tamaño debe estar vacío o ser nulo
        if ("int".equalsIgnoreCase(columnType) || "double".equalsIgnoreCase(columnType)
                || "numeric".equalsIgnoreCase(columnType) || "date".equalsIgnoreCase(columnType)) {
            return columnSize == null || columnSize.isEmpty();
        }

        // Para otros tipos de datos, validación de tamaño (solo números)
        return columnSize.matches("^[0-9]+$");
    }

    private boolean isPrimaryKeyValid(String isPrimaryKey) {
        // Validación de clave primaria (solo "true" o "false")
        return "true".equalsIgnoreCase(isPrimaryKey) || "false".equalsIgnoreCase(isPrimaryKey);
    }

    private boolean isForeignKeyValid(String isForeignKey) {
        // Validación de clave foránea (solo "true" o "false")
        return "true".equalsIgnoreCase(isForeignKey) || "false".equalsIgnoreCase(isForeignKey);
    }

    private boolean isDefaultValueValid(String defaultValue) {
        // Validación de valor predeterminado (solo "NULL" o valor vacío)
        return "NULL".equalsIgnoreCase(defaultValue) || defaultValue.isEmpty();
    }

    private boolean isColumnDescriptionValid(String columnDescription) {
        // Validación de descripción de columna (puede personalizarse según tus necesidades)
        return true; // Agregar la validación adecuada
    }

    private boolean isNullableValid(String isNullable) {
        // Validación de nulo (solo "YES" o "NO")
        return "YES".equalsIgnoreCase(isNullable) || "NO".equalsIgnoreCase(isNullable);
    }

    private boolean isIndex(String index) {
        // Validación de nulo (solo "YES" o "NO")
        return "NO".equalsIgnoreCase(index) || "UNIQUE".equalsIgnoreCase(index) || "MULTIPLE".equalsIgnoreCase(index) || "SPATIAL".equalsIgnoreCase(index) || "FULLTEXT".equalsIgnoreCase(index);
    }

// Método para obtener el nombre de la tabla desde el campo de texto
    private String getTableNameFromTextField() {
        return tableNameField.getText();
    }

}

class CopyableTextDialog {

    public static void showCopyableTextDialog(String text) {
        JTextArea textArea = new JTextArea(text);
        JButton copyButton = new JButton("Copiar");
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringSelection stringSelection = new StringSelection(textArea.getText());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                JOptionPane.showMessageDialog(null, "Texto copiado al portapapeles.");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JScrollPane(textArea));
        panel.add(copyButton);

        JOptionPane.showMessageDialog(null, panel, "Texto Copiable", JOptionPane.PLAIN_MESSAGE);
    }
}
