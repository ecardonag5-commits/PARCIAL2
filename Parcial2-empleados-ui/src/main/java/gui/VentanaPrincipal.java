package gui;

import dao.EmpleadoDAO;
import Modelo.Empleado;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VentanaPrincipal extends JFrame {

    private final EmpleadoDAO dao = new EmpleadoDAO();

    private JTable tabla;
    private DefaultTableModel modeloTabla;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtDepartamento;
    private JTextField txtSalario;
    private JTextField txtFecha;
    private JCheckBox chkActivo;

    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    public VentanaPrincipal() {
        setTitle("Sistema de Gestión de Empleados");
        setSize(850, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: FORMULARIO ---
        JPanel panelFormulario = new JPanel(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Empleado"));

        panelFormulario.add(new JLabel(" ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel(" Nombre Completo:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel(" Departamento:"));
        txtDepartamento = new JTextField();
        panelFormulario.add(txtDepartamento);

        panelFormulario.add(new JLabel(" Salario Mensual:"));
        txtSalario = new JTextField();
        panelFormulario.add(txtSalario);

        panelFormulario.add(new JLabel(" Fecha Contratación (YYYY-MM-DD):"));
        txtFecha = new JTextField();
        panelFormulario.add(txtFecha);

        panelFormulario.add(new JLabel(" Estado:"));
        chkActivo = new JCheckBox("Activo", true);
        panelFormulario.add(chkActivo);

        add(panelFormulario, BorderLayout.NORTH);

        // --- PANEL CENTRAL: TABLA ---
        String[] columnas = {"ID", "Nombre Completo", "Departamento", "Salario", "Fecha Contratación", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Evento para cargar datos al hacer clic en una fila
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabla.getSelectedRow() != -1) {
                cargarFormularioDesdeTabla();
            }
        });

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- PANEL INFERIOR: BOTONES ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnGuardar = new JButton("Guardar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar Campos");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        add(panelBotones, BorderLayout.SOUTH);

        // --- EVENTOS DE LOS BOTONES ---
        btnGuardar.addActionListener(e -> guardarEmpleado());
        btnActualizar.addActionListener(e -> actualizarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnLimpiar.addActionListener(e -> limpiarCampos());

        // Cargar tabla al iniciar
        cargarTabla();
    }

    private void cargarTabla() {
        modeloTabla.setRowCount(0);
        List<Empleado> empleados = dao.listar();
        for (Empleado emp : empleados) {
            modeloTabla.addRow(new Object[]{
                emp.getId(),
                emp.getNombreCompleto(),
                emp.getDepartamento(),
                String.format("Q%.2f", emp.getSalarioMensual()),
                emp.getFechaContratacion(),
                emp.getEstadoTexto()
            });
        }
    }

    private void cargarFormularioDesdeTabla() {
        int fila = tabla.getSelectedRow();
        if (fila != -1) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtDepartamento.setText(modeloTabla.getValueAt(fila, 2).toString());

            String salarioStr = modeloTabla.getValueAt(fila, 3).toString().replace("Q", "").replace(",", "").trim();
            txtSalario.setText(salarioStr);

            txtFecha.setText(modeloTabla.getValueAt(fila, 4).toString());

            boolean activo = modeloTabla.getValueAt(fila, 5).toString().equalsIgnoreCase("Activo");
            chkActivo.setSelected(activo);
        }
    }

    private void guardarEmpleado() {
        if (!validarCampos()) return;

        try {
            String nombre = txtNombre.getText().trim();
            String depto = txtDepartamento.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            boolean activo = chkActivo.isSelected();

            Empleado emp = new Empleado(nombre, depto, salario, fecha, activo);
            if (dao.insertar(emp)) {
                JOptionPane.showMessageDialog(this, "Empleado registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en los datos ingresados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEmpleado() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione primero un empleado de la tabla para editar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validarCampos()) return;

        try {
            int id = Integer.parseInt(txtId.getText());
            String nombre = txtNombre.getText().trim();
            String depto = txtDepartamento.getText().trim();
            double salario = Double.parseDouble(txtSalario.getText().trim());
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            boolean activo = chkActivo.isSelected();

            Empleado emp = new Empleado(id, nombre, depto, salario, fecha, activo);
            if (dao.actualizar(emp)) {
                JOptionPane.showMessageDialog(this, "Empleado actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al actualizar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEmpleado() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Desea eliminar permanentemente al empleado seleccionado?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(txtId.getText());
            if (dao.eliminarFisico(id)) {
                JOptionPane.showMessageDialog(this, "Empleado eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty() || txtDepartamento.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Departamento no pueden estar vacíos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            double salario = Double.parseDouble(txtSalario.getText().trim());
            if (salario <= 0) {
                JOptionPane.showMessageDialog(this, "El salario debe ser mayor a 0.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto de salario válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
            if (fecha.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "La fecha de contratación no puede ser futura.", "Validación", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD.", "Validación", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        txtId.setText("");
        txtNombre.setText("");
        txtDepartamento.setText("");
        txtSalario.setText("");
        txtFecha.setText("");
        chkActivo.setSelected(true);
        tabla.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}