/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.roundrobinapp;

import com.mycompany.roundrobinapp.RoundRobinFrame.Proceso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;

/**
 *
 * @author Usuario
 */
public final class RoundRobinProcesos extends javax.swing.JFrame {
    private DefaultTableModel listoModel;
    private DefaultTableModel ejecucionModel;
    private DefaultTableModel bloqueadoModel;
    private DefaultTableModel terminadoModel;
    
    private int tiempoGlobal = 0;
    private int quantum;
    private ArrayList<Proceso> colaProcesos;
    private Timer timer;
    private int procesoActualIndex = 0;

    // Define el umbral de probabilidad para el bloqueo
    private double umbralBloqueo = 0.5; // Por ejemplo, 20% de probabilidad
    
    private ArrayList<String> procesosEnEjecucion = new ArrayList<>();
    private ArrayList<String> procesosTerminados = new ArrayList<>();
    // Declarar un conjunto para llevar un registro de procesos bloqueados pendientes de ejecución
    private Map<String, Integer> procesosBloqueadosPendientes = new HashMap<>();
    // Declarar una estructura para almacenar los tiempos originales de los procesos


    /**
     * Creates new form RoundRobinProcesos
     */
    
    public RoundRobinProcesos(ArrayList<Proceso> colaProcesos, int quantum) {
        initComponents();
        
        this.quantum = quantum;
        this.colaProcesos = colaProcesos;
        
        // Crear una columna para los nombres de los procesos en la tabla "Listo"
        listoModel = new DefaultTableModel();
        listoModel.addColumn("Nombre"); // Agrega la columna para los nombres
        listoModel.addColumn("Tiempo");

        tbListo.setModel(listoModel);

        // Crear una columna para los nombres de los procesos en la tabla "Ejecución"
        ejecucionModel = new DefaultTableModel();
        ejecucionModel.addColumn("Nombre"); // Agrega la columna para los nombres
        ejecucionModel.addColumn("Tiempo");

        tbEjecucion.setModel(ejecucionModel);

        // Crear una columna para los nombres de los procesos en la tabla "Terminado"
        terminadoModel = new DefaultTableModel();
        terminadoModel.addColumn(""); // Agrega la columna para los nombres
        terminadoModel.addColumn("Tiempo");

        tbTerminado.setModel(terminadoModel);

        // Crear una columna para los nombres de los procesos en la tabla "Bloqueado"
        bloqueadoModel = new DefaultTableModel();
        bloqueadoModel.addColumn(""); // Agrega la columna para los nombres
        bloqueadoModel.addColumn("Tiempo");


        tbBloqueado.setModel(bloqueadoModel);
        // Mostrar los procesos en la tabla "Listo"
        for (RoundRobinFrame.Proceso proceso : colaProcesos) {
            listoModel.addRow(new Object[]{proceso.getNombre(), proceso.getTiempo()});
        }
        
        actualizarInterfaz();
        
        
        // Iniciar simulación con un temporizador
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Verificar si se completaron todos los procesos y hay procesos en la tabla de listos
                if (procesoActualIndex >= colaProcesos.size() || listoModel.getRowCount() > 0) {
                    procesoActualIndex = 0; // Reinicia el índice para volver a ejecutar
                }
                ejecutarRoundRobin();
            }
        }, 0, 1000); // Se ejecutará cada segundo
    }
   
    private void ejecutarRoundRobin() {
        if (listoModel.getRowCount() > 0) {
            String nombre = (String) listoModel.getValueAt(0, 0);
            int tiempo = (int) listoModel.getValueAt(0, 1);
            System.out.println("Ejecutando proceso: " + nombre + " con tiempo restante " + tiempo);
            
            // Mueve el proceso de "Listo" a "Ejecución"
            listoModel.removeRow(0);
            ejecucionModel.addRow(new Object[]{nombre, tiempo});

            // Simula la ejecución del proceso
            int tiempoEjecutado = 0;
            while (tiempoEjecutado < quantum && tiempo > 0) {
                tiempoEjecutado++;
                tiempo--;
                tiempoGlobal++;
                actualizarInterfaz();
                esperarUnSegundo();
            }

            // Si el proceso ha terminado, muévelo a "Terminado"
            if (tiempo == 0) {
                System.out.println("Proceso " + nombre + " ha terminado y se mueve a 'Terminado'");
                terminadoModel.addRow(new Object[]{nombre, 0});
                procesosTerminados.add(nombre);
            } else {
                // Mueve el proceso a "Listo" o "Bloqueado" según la probabilidad
                Random random = new Random();
                if (random.nextDouble() <= umbralBloqueo) {
                    System.out.println("Proceso " + nombre + " se mueve a 'Bloqueado' con tiempo restante " + tiempo);
                    bloqueadoModel.addRow(new Object[]{nombre, tiempo});
                    procesosEnEjecucion.remove(nombre);
                } else {
                    System.out.println("Proceso " + nombre + " se mueve a 'Listo' con tiempo restante " + tiempo);
                    listoModel.addRow(new Object[]{nombre, tiempo});
                    procesosEnEjecucion.add(nombre);
                }
            }

            // Elimina el proceso de "Ejecución"
            ejecucionModel.removeRow(0);

            procesoActualIndex++;
        } else if (bloqueadoModel.getRowCount() >= 0 && procesosBloqueadosPendientes.isEmpty()) {
            // Si la tabla de "Listo" está vacía, y no hay procesos bloqueados pendientes

            // Mover todos los procesos bloqueados a la lista de procesos bloqueados pendientes
            for (int i = 0; i < bloqueadoModel.getRowCount(); i++) {
                String nombreBloqueado = (String) bloqueadoModel.getValueAt(i, 0);
                int tiempoRestanteBloqueado = (int) bloqueadoModel.getValueAt(i, 1);

                procesosBloqueadosPendientes.put(nombreBloqueado, tiempoRestanteBloqueado);
                listoModel.addRow(new Object[]{nombreBloqueado, tiempoRestanteBloqueado});
            }

            // Limpiar la tabla de "Bloqueado"
            bloqueadoModel.setRowCount(0);
        }else if (bloqueadoModel.getRowCount() > 0) {
            for (int i = 0; i < bloqueadoModel.getRowCount(); i++) {
                String nombreBloqueado = (String) bloqueadoModel.getValueAt(i, 0);
                int tiempoRestanteBloqueado = (int) bloqueadoModel.getValueAt(i, 1);

                procesosBloqueadosPendientes.put(nombreBloqueado, tiempoRestanteBloqueado);
                listoModel.addRow(new Object[]{nombreBloqueado, tiempoRestanteBloqueado});
            }

            // Limpiar la tabla de "Bloqueado"
            bloqueadoModel.setRowCount(0);
        }
    }
    
    private void esperarUnSegundo() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void actualizarInterfaz() {
            SwingUtilities.invokeLater(() -> {
            // Actualiza las tablas en la interfaz gráfica
            listoModel.fireTableDataChanged();
            ejecucionModel.fireTableDataChanged();
            terminadoModel.fireTableDataChanged();
            bloqueadoModel.fireTableDataChanged();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbListo = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbEjecucion = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbTerminado = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        tbBloqueado = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Listo:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 80, -1, -1));

        jLabel2.setText("En ejecución");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 80, -1, -1));

        jLabel3.setText("Terminado:");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 80, -1, -1));

        jLabel4.setText("Bloqueado:");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 80, -1, -1));

        tbListo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbListo);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 120, 140, 280));

        tbEjecucion.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tbEjecucion);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 120, 140, 280));

        tbTerminado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(tbTerminado);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 120, 140, 280));

        tbBloqueado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(tbBloqueado);

        getContentPane().add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, 140, 280));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RoundRobinProcesos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RoundRobinProcesos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RoundRobinProcesos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RoundRobinProcesos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // Crear una instancia de RoundRobinFrame
        RoundRobinFrame frame = new RoundRobinFrame();

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tbBloqueado;
    private javax.swing.JTable tbEjecucion;
    private javax.swing.JTable tbListo;
    private javax.swing.JTable tbTerminado;
    // End of variables declaration//GEN-END:variables
}
