package newpackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.*;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author LENOVO
 */
public class QueryLMDWindow extends JFrame {

    private JTextArea queryTextArea;
    private JButton executeButton;
    private JTable resultTable;
    private JScrollPane tableScrollPane;
    private JComboBox<String> databaseComboBox;
    private JComboBox<String> tableComboBox; // Ahora visible desde el principio

public QueryLMDWindow(Connection connection) {
        setTitle("Consulta LMD");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel en la parte superior (norte) para la selección de base de datos y el TextArea
        JPanel inputPanel = new JPanel(new BorderLayout());

        // JComboBox para seleccionar la base de datos
        databaseComboBox = new JComboBox<>();
        // Llena el JComboBox con los nombres de las bases de datos disponibles
        fillDatabaseComboBox(connection);
        inputPanel.add(databaseComboBox, BorderLayout.WEST);

        // JComboBox para seleccionar la tabla
        tableComboBox = new JComboBox<>();
        tableComboBox.setVisible(true); // Ocultar inicialmente
        inputPanel.add(tableComboBox, BorderLayout.SOUTH);

        // TextArea para ingresar la consulta SQL
        queryTextArea = new JTextArea();
        queryTextArea.setRows(10);
        inputPanel.add(new JScrollPane(queryTextArea), BorderLayout.CENTER);

        // Botón para ejecutar la consulta
        executeButton = new JButton("Ejecutar");
        executeButton.addActionListener(e -> executeQuery(connection));
        inputPanel.add(executeButton, BorderLayout.EAST);

        databaseComboBox.addActionListener(e -> {
            fillTableComboBox(connection);
            tableComboBox.setVisible(true); // Mostrar al seleccionar una base de datos
        });

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabla en el centro para mostrar los resultados
        resultTable = new JTable();
        tableScrollPane = new JScrollPane(resultTable);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
    }

    private void fillDatabaseComboBox(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            // Consulta para obtener los nombres de las bases de datos
            String databaseQuery = "SHOW DATABASES";
            statement.execute(databaseQuery);
            // Recopila los resultados en el JComboBox
            while (statement.getResultSet().next()) {
                String databaseName = statement.getResultSet().getString(1);
                databaseComboBox.addItem(databaseName);
            }
        } catch (SQLException ex) {
            // Manejo de errores
        }
    }

    private void fillTableComboBox(Connection connection) {
        String selectedDatabase = (String) databaseComboBox.getSelectedItem();

        if (selectedDatabase != null) {
            try (Statement statement = connection.createStatement()) {
                // Obtener los nombres de las tablas de la base de datos seleccionada
                String showTablesQuery = "SHOW TABLES FROM " + selectedDatabase;
                statement.execute(showTablesQuery);

                ResultSet resultSet = statement.getResultSet();
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();

                while (resultSet.next()) {
                    String tableName = resultSet.getString(1);
                    model.addElement(tableName);
                }

                tableComboBox.setModel(model);
            } catch (SQLException ex) {
                // Manejo de errores
            }
        }
    }

    private void executeQuery(Connection connection) {
        String selectedDatabase = (String) databaseComboBox.getSelectedItem();
        String query = queryTextArea.getText();

        try {
            String useDatabaseQuery = "USE " + selectedDatabase;
            try (Statement statement = connection.createStatement()) {
                statement.execute(useDatabaseQuery);
            }

            String trimmedQuery = query.trim();

            if (trimmedQuery.toUpperCase().startsWith("SELECT")) {
                // Procesa la consulta SELECT
                processSelectQuery(connection, trimmedQuery);
            } else if (trimmedQuery.toUpperCase().startsWith("INSERT")
                    || trimmedQuery.toUpperCase().startsWith("UPDATE")
                    || trimmedQuery.toUpperCase().startsWith("DELETE")) {
                // Procesa las consultas INSERT, UPDATE, DELETE
                processModificationQuery(connection, trimmedQuery);
            } else {
                JOptionPane.showMessageDialog(this, "Consulta no admitida. Por favor, ingrese una consulta INSERT, UPDATE, DELETE o SELECT.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
        }
    }

    private String extractTableName(String query) {
        String tableName = null;
        String trimmedQuery = query.trim().toUpperCase();

        if (trimmedQuery.startsWith("INSERT INTO")) {
            int startIndex = "INSERT INTO".length();
            int endIndex = trimmedQuery.indexOf("(");
            if (endIndex > startIndex) {
                tableName = query.substring(startIndex, endIndex).trim();
            }
        } else if (trimmedQuery.startsWith("UPDATE")) {
            int startIndex = "UPDATE".length();
            int endIndex = trimmedQuery.indexOf("SET");
            if (endIndex > startIndex) {
                tableName = query.substring(startIndex, endIndex).trim();
            }
        } else if (trimmedQuery.startsWith("DELETE FROM")) {
            int startIndex = "DELETE FROM".length();
            tableName = query.substring(startIndex).trim();
        }

        return tableName;
    }

    private void updateTableData(Connection connection, String tableName) {
        if (tableName != null) {
            try {
                // Obtener los datos de la tabla
                String selectQuery = "SELECT * FROM " + tableName;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery);

                // Crear un modelo de tabla y mostrar los resultados
                TableModel tableModel = new DefaultResultSetTableModel(resultSet);
                resultTable.setModel(tableModel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar los resultados de SELECT: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(ex.getMessage());
            }
        }
    }

    private List<String> splitInsertQueries(String query) {
        List<String> insertQueries = new ArrayList<>();
        String[] queries = query.split(";");

        for (String q : queries) {
            q = q.trim();
            if (!q.isEmpty() && (q.toUpperCase().startsWith("INSERT"))) {
                insertQueries.add(q);
            }
        }

        return insertQueries;
    }

    private List<String> splitDeleteQueries(String query) {
        List<String> deleteQueries = new ArrayList<>();
        String[] queries = query.split(";");

        for (String q : queries) {
            q = q.trim();
            if (!q.isEmpty() && (q.toUpperCase().startsWith("DELETE"))) {
                // Verifica si la sentencia DELETE contiene una cláusula WHERE
                if (q.toUpperCase().contains("WHERE")) {
                    // Si contiene WHERE, agrega la sentencia completa
                    deleteQueries.add(q);
                } else {
                    // Si no contiene WHERE, agrega la sentencia con una cláusula WHERE falsa
                    deleteQueries.add(q + " WHERE 1=1");
                }
            }
        }

        return deleteQueries;
    }

    private List<String> splitQueries(String query) {
        List<String> queries = new ArrayList<>();
        String[] queryArray = query.split(";");

        for (String q : queryArray) {
            q = q.trim();
            if (!q.isEmpty()) {
                queries.add(q);
            }
        }

        return queries;
    }

    private List<String> splitUpdateQueries(String query) {
        List<String> updateQueries = new ArrayList<>();
        String[] queries = query.split(";");

        for (String q : queries) {
            q = q.trim();
            if (!q.isEmpty() && (q.toUpperCase().startsWith("UPDATE"))) {
                // Verifica si la sentencia UPDATE contiene una cláusula WHERE
                if (q.toUpperCase().contains("WHERE")) {
                    // Si contiene WHERE, agrega la sentencia completa
                    updateQueries.add(q);
                } else {
                    // Si no contiene WHERE, agrega la sentencia con una cláusula WHERE falsa
                    updateQueries.add(q + " WHERE 1=1");
                }
            }
        }

        return updateQueries;
    }

    private void updateTableDataAfterModification(Connection connection, String dbName, String tableName) {
        if (tableName != null && dbName != null) {
            try {
                // Obtener los datos de la tabla
                String selectQuery = "SELECT * FROM " + dbName + "." + tableName;
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectQuery);

                // Crear un modelo de tabla y mostrar los resultados
                TableModel tableModel = new DefaultResultSetTableModel(resultSet);
                resultTable.setModel(tableModel);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al actualizar los resultados de SELECT: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(ex.getMessage());
            }
        }
    }

    // Método para analizar y procesar consultas SELECT más complejas
    private void processSelectQuery(Connection connection, String query) {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            // Crea un modelo de tabla para mostrar los resultados
            TableModel tableModel = new DefaultResultSetTableModel(resultSet);
            resultTable.setModel(tableModel);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta SELECT: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(ex.getMessage());
        }
    }

    private void processModificationQuery(Connection connection, String query) {
        List<String> queries = splitQueries(query);

        int totalAffectedRows = 0;

        for (String singleQuery : queries) {
            try {
                if (singleQuery.toUpperCase().startsWith("SELECT")) {
                    executeSelectQuery(connection, singleQuery);
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(singleQuery)) {
                        int affectedRows = preparedStatement.executeUpdate();
                        totalAffectedRows += affectedRows;
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        System.out.println(ex.getMessage());
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println(ex.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this, "Se han afectado en total " + totalAffectedRows + " filas.", "Consulta Exitosa", JOptionPane.INFORMATION_MESSAGE);

        // Obtén el nombre de la tabla seleccionada
        String tableName = (String) tableComboBox.getSelectedItem();

        // Obtén el nombre de la base de datos seleccionada
        String dbName = (String) databaseComboBox.getSelectedItem();

        // Actualiza la tabla con los resultados de SELECT después de la operación INSERT, UPDATE o DELETE
        updateTableDataAfterModification(connection, dbName, tableName);
    }

    private void executeSelectQuery(Connection connection, String query) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
            // Crea un modelo de tabla para mostrar los resultados
            TableModel tableModel = new DefaultResultSetTableModel(resultSet);
            resultTable.setModel(tableModel);
        }
    }
}

class DefaultResultSetTableModel extends AbstractTableModel {

    private List<String> columnNames = new ArrayList<>();
    private List<List<Object>> data = new ArrayList<>();

    public DefaultResultSetTableModel(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            while (resultSet.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(resultSet.getObject(i));
                }
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    @Override
    public String getColumnName(int col) {
        return columnNames.get(col);
    }
}
