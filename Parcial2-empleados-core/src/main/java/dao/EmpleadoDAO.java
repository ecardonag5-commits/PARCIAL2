package dao;

import config.Conexion;
import Modelo.Empleado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    public List<Empleado> listar() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados ORDER BY id ASC";

        try (Connection con = Conexion.getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Empleado emp = new Empleado(
                    rs.getInt("id"),
                    rs.getString("nombre_completo"),
                    rs.getString("departamento"),
                    rs.getDouble("salario_mensual"),
                    rs.getDate("fecha_contratacion").toLocalDate(),
                    rs.getBoolean("activo")
                );
                lista.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Empleado emp) {
        String sql = "INSERT INTO empleados (nombre_completo, departamento, salario_mensual, fecha_contratacion, activo) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getNombreCompleto());
            ps.setString(2, emp.getDepartamento());
            ps.setDouble(3, emp.getSalarioMensual());
            ps.setDate(4, Date.valueOf(emp.getFechaContratacion()));
            ps.setBoolean(5, emp.isActivo());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar empleado: " + e.getMessage());
            return false;
        }
    }

    public Empleado obtenerPorId(int id) {
        String sql = "SELECT * FROM empleados WHERE id = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Empleado(
                        rs.getInt("id"),
                        rs.getString("nombre_completo"),
                        rs.getString("departamento"),
                        rs.getDouble("salario_mensual"),
                        rs.getDate("fecha_contratacion").toLocalDate(),
                        rs.getBoolean("activo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar empleado: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Empleado emp) {
        String sql = "UPDATE empleados SET nombre_completo = ?, departamento = ?, salario_mensual = ?, fecha_contratacion = ?, activo = ? WHERE id = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emp.getNombreCompleto());
            ps.setString(2, emp.getDepartamento());
            ps.setDouble(3, emp.getSalarioMensual());
            ps.setDate(4, Date.valueOf(emp.getFechaContratacion()));
            ps.setBoolean(5, emp.isActivo());
            ps.setInt(6, emp.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarFisico(int id) {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
            return false;
        }
    }
}