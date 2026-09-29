package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:mariadb://localhost:3306/gestion_empleados_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Cardona"; // Ajusta la contraseña si tienes una en tu MariaDB

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
