package newpackage;

/**
 *
 * @author LENOVO
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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
import newpackage.TableInfoWindow;

public class Access {

    private JFrame frame;
    private JFrame create;
    private JFrame alter;
    private JFrame drop;
    private JComboBox<String> databaseComboBox;
    private JTextField hostTextField;
    private JTextField portTextField;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JTextField dbNameTextField;
    //private JTextField urlTextField;
    //private JTextField bdAccessTextField;
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



    //*************************************************************************************************************
    public void muestraDropAccess(Connection connection, JTextField urlTextField, JTextField bdAccessTextField) throws SQLException {
        drop = new JFrame("Eliminar Tabla desde GUI");
        drop.setBounds(100, 100, 600, 500);
        drop.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drop.getContentPane().setLayout(null);
        drop.setVisible(true);

        JLabel lblTableName = new JLabel("Nombre de la tabla a eliminar:");
        lblTableName.setBounds(10, 10, 200, 20);
        drop.getContentPane().add(lblTableName);

        tableNameFieldDrop = new JTextField();
        tableNameFieldDrop.setBounds(10, 40, 250, 20);
        drop.getContentPane().add(tableNameFieldDrop);

        dropTableButton = new JButton("Eliminar Tabla");
        dropTableButton.setBounds(10, 80, 150, 30);
        drop.getContentPane().add(dropTableButton);

        // Agregar un área de texto para la vista previa
        sqlPreviewArea = new JTextArea();
        sqlPreviewArea.setEditable(false);
        sqlPreviewArea.setBounds(10, 240, 400, 100);
        alter.getContentPane().add(sqlPreviewArea);
        sqlPreviewArea.setVisible(true);

        dropTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameFieldDrop.getText();

                // Validación del nombre de la tabla
                if (tableName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El nombre de la tabla no puede estar vacío.");
                    return;
                }

                try (Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + urlTextField.getText() + "\\" + bdAccessTextField.getText() + ".accdb"); Statement statement = connection.createStatement()) {

                    // Validación de existencia de la tabla
                    if (!tableExists(connection, tableName)) {
                        JOptionPane.showMessageDialog(null, "La tabla especificada no existe en la base de datos.");
                        return;
                    }

                    // Ejecutar la instrucción DROP TABLE
                    String dropTableSQL = "DROP TABLE " + tableName;
                    sqlPreviewArea.setText(dropTableSQL);
                    statement.executeUpdate(dropTableSQL);
                    JOptionPane.showMessageDialog(null, "Tabla eliminada con éxito en Access.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar la tabla en Access: " + ex.getMessage());
                }
            }
        });

    }

    public void muestraAlterAccess(Connection connection, JTextField urlTextField, JTextField bdAccessTextField) throws SQLException {
        alter = new JFrame("Modificar Tabla desde GUI");
        alter.setBounds(100, 100, 600, 500);
        alter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        alter.getContentPane().setLayout(null);
        alter.setVisible(true);

        JLabel lblTableName = new JLabel("Nombre de la tabla:");
        lblTableName.setBounds(10, 10, 150, 20);
        alter.getContentPane().add(lblTableName);

        tableNameFieldAlter = new JTextField();
        tableNameFieldAlter.setBounds(170, 10, 200, 20);
        alter.getContentPane().add(tableNameFieldAlter);

        JLabel lblAlterStatement = new JLabel("Instrucción de Modificación:");
        lblAlterStatement.setBounds(10, 40, 200, 20);
        alter.getContentPane().add(lblAlterStatement);

        alterStatementArea = new JTextArea();
        alterStatementArea.setBounds(10, 70, 400, 100);
        alter.getContentPane().add(alterStatementArea);

        alterTableButton = new JButton("Modificar Tabla");
        alterTableButton.setBounds(10, 200, 150, 30);
        alter.getContentPane().add(alterTableButton);

        // Agregar un área de texto para la vista previa
        sqlPreviewArea = new JTextArea();
        sqlPreviewArea.setEditable(false);
        sqlPreviewArea.setBounds(10, 240, 400, 100);
        alter.getContentPane().add(sqlPreviewArea);
        sqlPreviewArea.setVisible(true);

        alterTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameFieldAlter.getText();
                String alterStatement = alterStatementArea.getText();

                // Validación del nombre de la tabla
                if (tableName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "El nombre de la tabla no puede estar vacío.");
                    return;
                }

                // Validación de la instrucción ALTER TABLE
                if (alterStatement.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "La instrucción ALTER TABLE no puede estar vacía.");
                    return;
                }

                try (Connection connection = DriverManager.getConnection("jdbc:ucanaccess://" + urlTextField.getText() + "\\" + bdAccessTextField.getText() + ".accdb"); Statement statement = connection.createStatement()) {

                    // Validación de existencia de la tabla
                    if (!tableExists(connection, tableName)) {
                        JOptionPane.showMessageDialog(null, "La tabla especificada no existe en la base de datos.");
                        return;
                    }

                    // Ejecutar la instrucción ALTER TABLE
                    String alterTableSQL = "ALTER TABLE " + tableName + " " + alterStatement;
                    sqlPreviewArea.setText(alterTableSQL);
                    statement.executeUpdate(alterTableSQL);
                    JOptionPane.showMessageDialog(null, "Tabla modificada con éxito en Access.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al modificar la tabla en Access: " + ex.getMessage());
                }
            }
        });

    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        // Validación de existencia de la tabla
        // Puedes personalizar esta lógica según tu base de datos
        try (Statement statement = connection.createStatement()) {
            statement.executeQuery("SELECT * FROM " + tableName);
            return true;
        } catch (SQLException ex) {
            return false;
        }

    }

    public void muestraCreateAccess(Connection connectionA, JTextField urlTextField, JTextField bdAccessTextField) throws SQLException {
        create = new JFrame("Crear Tabla desde GUI");
        create.setBounds(100, 100, 600, 500);
        create.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        create.getContentPane().setLayout(null);
        create.setVisible(true);

        JLabel lblTableName = new JLabel("Nombre de la tabla:");
        lblTableName.setBounds(10, 10, 150, 20);
        create.getContentPane().add(lblTableName);

        tableNameFieldCreate = new JTextField();
        tableNameFieldCreate.setBounds(170, 10, 200, 20);
        create.getContentPane().add(tableNameFieldCreate);

        JLabel lblColumnDefinition = new JLabel("Definición de Columnas:");
        lblColumnDefinition.setBounds(10, 40, 200, 20);
        create.getContentPane().add(lblColumnDefinition);

        columnDefinitionArea = new JTextArea();
        columnDefinitionArea.setBounds(10, 70, 400, 150);
        create.getContentPane().add(columnDefinitionArea);

        createTableButton = new JButton("Crear Tabla");
        createTableButton.setBounds(10, 230, 120, 30);
        create.getContentPane().add(createTableButton);

        // Agregar un área de texto para la vista previa
        sqlPreviewAreaCreate = new JTextArea();
        sqlPreviewAreaCreate.setEditable(false);
        sqlPreviewAreaCreate.setBounds(10, 265, 400, 150);
        create.getContentPane().add(sqlPreviewAreaCreate);
        sqlPreviewAreaCreate.setVisible(true);

        createTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameFieldCreate.getText();
                String columnDefinitions = columnDefinitionArea.getText();

                // Generar la sentencia SQL de creación de la tabla
                String createTableSQL = "CREATE TABLE " + tableName + " (" + columnDefinitions + ");";

                // Mostrar la vista previa de la sentencia SQL
                sqlPreviewAreaCreate.setText(createTableSQL);

                try (Connection connectionA = DriverManager.getConnection("jdbc:ucanaccess://" + urlTextField.getText() + "\\" + bdAccessTextField.getText() + ".accdb"); 
                        Statement statement = connectionA.createStatement()) {
                    // Ejecutar la sentencia SQL para crear la tabla
                    statement.executeUpdate(createTableSQL);
                    JOptionPane.showMessageDialog(null, "Tabla creada con éxito en Access.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al crear la tabla en Access: " + ex.getMessage());
                }

            }
        });

    }
}
