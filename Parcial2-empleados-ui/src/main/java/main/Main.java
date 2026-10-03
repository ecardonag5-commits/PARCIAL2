package main;

import dao.EmpleadoDAO;
import Modelo.Empleado;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final EmpleadoDAO dao = new EmpleadoDAO();

    public static void main(String[] args) {
        int opcion = 0;
        do {
            System.out.println("\n=== SISTEMA DE GESTIÓN DE EMPLEADOS ===");
            System.out.println("1. Listar empleados");
            System.out.println("2. Registrar nuevo empleado");
            System.out.println("3. Editar empleado");
            System.out.println("4. Eliminar empleado");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                opcion = 0;
            }

            switch (opcion) {
                case 1:
                    listarEmpleados();
                    break;
                case 2:
                    registrarEmpleado();
                    break;
                case 3:
                    editarEmpleado();
                    break;
                case 4:
                    eliminarEmpleado();
                    break;
                case 5:
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 5);
    }

    private static void listarEmpleados() {
        List<Empleado> empleados = dao.listar();
        System.out.println("\nListado:");
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        for (Empleado e : empleados) {
            System.out.printf("[%d] %-20s | %-14s | Salario: Q%.2f | Bono: Q%.2f | %s\n",
                    e.getId(),
                    e.getNombreCompleto(),
                    e.getDepartamento(),
                    e.getSalarioMensual(),
                    e.getBono(),
                    e.getEstadoTexto());
        }
    }

    private static void registrarEmpleado() {
        System.out.println("\n--- REGISTRAR EMPLEADO ---");
        String nombre = pedirTextoNoVacio("Nombre completo: ");
        String departamento = pedirTextoNoVacio("Departamento: ");
        double salario = pedirSalarioPositivo();
        double bono = pedirBono();
        LocalDate fecha = pedirFechaValida();

        Empleado nuevo = new Empleado(nombre, departamento, salario, bono, fecha, true);
        if (dao.insertar(nuevo)) {
            System.out.println("Empleado registrado correctamente.");
        } else {
            System.out.println("Error al guardar el empleado.");
        }
    }

    private static void editarEmpleado() {
        System.out.println("\n--- EDITAR EMPLEADO ---");
        System.out.print("Ingrese el ID del empleado a editar: ");
        int id = pedirEntero();

        Empleado emp = dao.obtenerPorId(id);
        if (emp == null) {
            System.out.println("No se encontró ningún empleado con ese ID.");
            return;
        }

        String nombre = pedirTextoNoVacio("Nombre completo [" + emp.getNombreCompleto() + "]: ");
        String departamento = pedirTextoNoVacio("Departamento [" + emp.getDepartamento() + "]: ");
        double salario = pedirSalarioPositivo();
        double bono = pedirBono();
        LocalDate fecha = pedirFechaValida();

        System.out.print("¿Sigue activo en la empresa? (S/N): ");
        boolean activo = sc.nextLine().trim().equalsIgnoreCase("S");

        emp.setNombreCompleto(nombre);
        emp.setDepartamento(departamento);
        emp.setSalarioMensual(salario);
        emp.setBono(bono);
        emp.setFechaContratacion(fecha);
        emp.setActivo(activo);

        if (dao.actualizar(emp)) {
            System.out.println("Datos del empleado actualizados correctamente.");
        } else {
            System.out.println("Error al actualizar.");
        }
    }

    private static void eliminarEmpleado() {
        System.out.println("\n--- ELIMINAR EMPLEADO ---");
        System.out.print("Ingrese el ID del empleado a eliminar: ");
        int id = pedirEntero();

        Empleado emp = dao.obtenerPorId(id);
        if (emp == null) {
            System.out.println("No existe un empleado con ese ID.");
            return;
        }

        System.out.printf("¿Está seguro que desea eliminar a %s? (Escriba 'S' para confirmar): ", emp.getNombreCompleto());
        if (sc.nextLine().trim().equalsIgnoreCase("S")) {
            if (dao.eliminarFisico(id)) {
                System.out.println("Empleado eliminado permanentemente.");
            } else {
                System.out.println("Error al eliminar.");
            }
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private static String pedirTextoNoVacio(String mensaje) {
        String texto;
        do {
            System.out.print(mensaje);
            texto = sc.nextLine().trim();
            if (texto.isEmpty()) {
                System.out.println("El campo no puede quedar vacío.");
            }
        } while (texto.isEmpty());
        return texto;
    }

    private static double pedirSalarioPositivo() {
        double salario = -1;
        do {
            System.out.print("Salario mensual: ");
            try {
                salario = Double.parseDouble(sc.nextLine());
                if (salario <= 0) {
                    System.out.println("El salario debe ser mayor a cero.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Monto inválido. Ingrese un valor numérico positivo.");
            }
        } while (salario <= 0);
        return salario;
    }

    private static double pedirBono() {
        double bono = -1;
        do {
            System.out.print("Bono: ");
            try {
                bono = Double.parseDouble(sc.nextLine());
                if (bono < 0) {
                    System.out.println("El bono no puede ser negativo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Monto inválido. Ingrese un valor numérico mayor o igual a cero.");
            }
        } while (bono < 0);
        return bono;
    }

    private static LocalDate pedirFechaValida() {
        LocalDate fecha = null;
        LocalDate hoy = LocalDate.now();
        do {
            System.out.print("Fecha de contratación (YYYY-MM-DD): ");
            String entrada = sc.nextLine().trim();
            try {
                fecha = LocalDate.parse(entrada);
                if (fecha.isAfter(hoy)) {
                    System.out.println("La fecha de contratación no puede ser una fecha futura.");
                    fecha = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido. Use YYYY-MM-DD.");
            }
        } while (fecha == null);
        return fecha;
    }

    private static int pedirEntero() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Ingrese un número entero: ");
            }
        }
    }
}
