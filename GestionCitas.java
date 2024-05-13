package com.mycompany.gestioncitas;

/**
 *
 * @author Jojo
 */

import java.io.*;
import java.util.*;

class Usuario {
    private int id;
    private String nombre;
    private String contraseña;

    public Usuario(int id, String nombre, String contraseña) {
        this.id = id;
        this.nombre = nombre;
        this.contraseña = contraseña;
    }

    // Métodos getter para los atributos
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContraseña() {
        return contraseña;
    }
}

class Cita {
    private int id;
    private String doctor;
    private String paciente;
    private int hora;
    private int dia;
    private int mes;

    public Cita(int id, String doctor, String paciente, int hora, int dia, int mes) {
        this.id = id;
        this.doctor = doctor;
        this.paciente = paciente;
        this.hora = hora;
        this.dia = dia;
        this.mes = mes;
    }

    // Métodos getter para los atributos
    public int getId() {
        return id;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getPaciente() {
        return paciente;
    }

    public int getHora() {
        return hora;
    }

    public int getDia() {
        return dia;
    }

    public int getMes() {
        return mes;
    }
}

public class GestionCitas {

    private static int ultimoIdUsuario = 0;
    private static int ultimoIdCita = 0;

    public static void main(String[] args) {
        // Verificar si existe el archivo de usuarios, si no, crearlo
        File usuariosFile = new File("usuarios.csv");
        if (!usuariosFile.exists()) {
            try {
                usuariosFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error al crear el archivo de usuarios: " + e.getMessage());
            }
        }

        // Verificar si existe el archivo de citas, si no, crearlo
        File citasFile = new File("citas.csv");
        if (!citasFile.exists()) {
            try {
                citasFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Error al crear el archivo de citas: " + e.getMessage());
            }
        }

        // Obtener el último ID de los usuarios del archivo
        ultimoIdUsuario = obtenerUltimoId("usuarios.csv");

        Scanner scanner = new Scanner(System.in);

        // Si hay usuarios registrados, pedir usuario y contraseña
        List<Usuario> usuarios = leerUsuariosDesdeArchivo("usuarios.csv");
        if (!usuarios.isEmpty()) {
            boolean credencialesValidas = false;
            while (!credencialesValidas) {
                System.out.println("Por favor, inicie sesión:");
                System.out.print("Usuario: ");
                String usuario = scanner.nextLine();
                System.out.print("Contraseña: ");
                String contraseña = scanner.nextLine();

                credencialesValidas = validarCredenciales(usuarios, usuario, contraseña);
                if (!credencialesValidas) {
                    System.out.println("Usuario o contraseña incorrectos. Inténtelo de nuevo.");
                }
            }
        } else {
            System.out.println("No hay usuarios registrados. Por favor, cree uno nuevo.");
        }

        // Obtener el último ID de las citas del archivo
        ultimoIdCita = obtenerUltimoId("citas.csv");

        int opcion;
        do {
            System.out.println("Menú Principal:");
            System.out.println("1. Buscar citas por mes");
            System.out.println("2. Buscar citas por doctor");
            System.out.println("3. Buscar citas por paciente");
            System.out.println("4. Buscar citas por ID");
            System.out.println("5. Agregar una nueva cita");
            System.out.println("6. Borrar una cita por ID");
            System.out.println("7. Crear un nuevo usuario");
            System.out.println("8. Borrar usuario por nombre");
            System.out.println("9. Salir");
            System.out.print("Ingrese la opción deseada: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir la nueva línea después de nextInt()

            switch (opcion) {
                case 1:
                    buscarPorMes();
                    break;
                case 2:
                    buscarPorDoctor();
                    break;
                case 3:
                    buscarPorPaciente();
                    break;
                case 4:
                    buscarPorId();
                    break;
                case 5:
                    agregarNuevaCita();
                    break;
                case 6:
                    borrarCitaPorId();
                    break;
                case 7:
                    crearNuevoUsuario();
                    break;
                case 8:
                    borrarUsuarioPorNombre();
                    break;
                case 9:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
            }
        } while (opcion != 9);
        scanner.close();
    }

    private static boolean validarCredenciales(List<Usuario> usuarios, String nombreUsuario, String contraseña) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().equals(nombreUsuario) && usuario.getContraseña().equals(contraseña)) {
                System.out.println("¡Inicio de sesión exitoso!");
                return true;
            }
        }
        return false;
    }

    private static void crearNuevoUsuario() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los detalles del nuevo usuario:");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Contraseña: ");
        String contraseña = scanner.nextLine();

        // Incrementar el ID y guardar el nuevo usuario en el archivo
        int id = ++ultimoIdUsuario;
        try (PrintWriter writer = new PrintWriter(new FileWriter("usuarios.csv", true))) {
            writer.println(id + "," + nombre + "," + contraseña);
            System.out.println("Nuevo usuario creado exitosamente con ID: " + id);
        } catch (IOException e) {
            System.out.println("Error al agregar el nuevo usuario: " + e.getMessage());
        }
    }

    private static void borrarUsuarioPorNombre() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del usuario a borrar: ");
        String nombreBorrar = scanner.nextLine();

        boolean encontrado = false;
        List<Usuario> nuevosUsuarios = new ArrayList<>();
        List<Usuario> usuarios = leerUsuariosDesdeArchivo("usuarios.csv");
        for (Usuario usuario : usuarios) {
            if (!usuario.getNombre().equalsIgnoreCase(nombreBorrar)) {
                nuevosUsuarios.add(usuario);
            } else {
                encontrado = true;
                System.out.println("Usuario '" + nombreBorrar + "' encontrado y eliminado correctamente.");
            }
        }
        if (!encontrado) {
            System.out.println("No se encontró ningún usuario con el nombre '" + nombreBorrar + "'.");
        }

        // Sobrescribir el archivo con los usuarios restantes
        try (PrintWriter writer = new PrintWriter(new FileWriter("usuarios.csv"))) {
            for (Usuario usuario : nuevosUsuarios) {
                writer.println(usuario.getId() + "," + usuario.getNombre() + "," + usuario.getContraseña());
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo usuarios.csv: " + e.getMessage());
        }
    }

    private static void buscarPorMes() {
        List<Cita> citas = leerCitasDesdeArchivo("citas.csv");
        if (citas.isEmpty()) {
            System.out.println("No se encontraron citas en el archivo.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el mes a buscar (1-12): ");
        int mesBuscado = scanner.nextInt();

        mostrarCitasPorMes(citas, mesBuscado);
    }

    private static void buscarPorDoctor() {
        List<Cita> citas = leerCitasDesdeArchivo("citas.csv");
        if (citas.isEmpty()) {
            System.out.println("No se encontraron citas en el archivo.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del doctor a buscar: ");
        String doctorBuscado = scanner.nextLine();

        mostrarCitasPorDoctor(citas, doctorBuscado);
    }

    private static void buscarPorPaciente() {
        List<Cita> citas = leerCitasDesdeArchivo("citas.csv");
        if (citas.isEmpty()) {
            System.out.println("No se encontraron citas en el archivo.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del paciente a buscar: ");
        String pacienteBuscado = scanner.nextLine();

        mostrarCitasPorPaciente(citas, pacienteBuscado);
    }

    private static void buscarPorId() {
        List<Cita> citas = leerCitasDesdeArchivo("citas.csv");
        if (citas.isEmpty()) {
            System.out.println("No se encontraron citas en el archivo.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de la cita a buscar: ");
        int idBuscado = scanner.nextInt();

        mostrarCitaPorId(citas, idBuscado);
    }

    private static void agregarNuevaCita() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese los detalles de la nueva cita:");
        System.out.print("Doctor: ");
        String doctor = scanner.nextLine();
        System.out.print("Paciente: ");
        String paciente = scanner.nextLine();
        System.out.print("Hora (0-23): ");
        int hora = scanner.nextInt();
        System.out.print("Día (1-31): ");
        int dia = scanner.nextInt();
        System.out.print("Mes (1-12): ");
        int mes = scanner.nextInt();

        // Incrementar el ID y guardar la nueva cita en el archivo
        int id = ++ultimoIdCita;
        try (PrintWriter writer = new PrintWriter(new FileWriter("citas.csv", true))) {
            writer.println(id + "," + doctor + "," + paciente + "," + hora + "," + dia + "," + mes);
            System.out.println("Nueva cita agregada exitosamente con ID: " + id);
        } catch (IOException e) {
            System.out.println("Error al agregar la nueva cita: " + e.getMessage());
        }
    }

    private static void borrarCitaPorId() {
        List<Cita> citas = leerCitasDesdeArchivo("citas.csv");
        if (citas.isEmpty()) {
            System.out.println("No hay citas para borrar.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el ID de la cita a borrar: ");
        int idBorrar = scanner.nextInt();

        boolean encontrado = false;
        List<Cita> nuevasCitas = new ArrayList<>();
        for (Cita cita : citas) {
            if (cita.getId() == idBorrar) {
                encontrado = true;
                System.out.println("Se ha encontrado la siguiente cita para borrar:");
                System.out.println("ID: " + cita.getId() + ", Doctor: " + cita.getDoctor() + ", Paciente: " + cita.getPaciente() + ", Hora: " + cita.getHora() + ", Día: " + cita.getDia() + ", Mes: " + cita.getMes());
                System.out.print("¿Está seguro que desea borrar esta cita? (S/N): ");
                String confirmacion = scanner.next();
                if (confirmacion.equalsIgnoreCase("S")) {
                    System.out.println("Cita eliminada correctamente.");
                } else {
                    nuevasCitas.add(cita);
                    System.out.println("Cita no borrada.");
                }
            } else {
                nuevasCitas.add(cita);
            }
        }
        if (!encontrado) {
            System.out.println("No se encontró ninguna cita con el ID " + idBorrar + ".");
        }

        // Sobrescribir el archivo con las citas restantes
        try (PrintWriter writer = new PrintWriter(new FileWriter("citas.csv"))) {
            for (Cita cita : nuevasCitas) {
                writer.println(cita.getId() + "," + cita.getDoctor() + "," + cita.getPaciente() + "," + cita.getHora() + "," + cita.getDia() + "," + cita.getMes());
            }
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo citas.csv: " + e.getMessage());
        }
    }

    private static List<Usuario> leerUsuariosDesdeArchivo(String nombreArchivo) {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                int id = Integer.parseInt(datos[0].trim());
                usuarios.add(new Usuario(id, datos[1].trim(), datos[2].trim()));
                // Actualizar el último ID de usuario
                if (id > ultimoIdUsuario) {
                    ultimoIdUsuario = id;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
        }
        return usuarios;
    }

    private static List<Cita> leerCitasDesdeArchivo(String nombreArchivo) {
        List<Cita> citas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                int id = Integer.parseInt(datos[0].trim());
                citas.add(new Cita(id, datos[1].trim(), datos[2].trim(), Integer.parseInt(datos[3].trim()), Integer.parseInt(datos[4].trim()), Integer.parseInt(datos[5].trim())));
                // Actualizar el último ID de cita
                if (id > ultimoIdCita) {
                    ultimoIdCita = id;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
        }
        return citas;
    }

    private static void mostrarCitasPorMes(List<Cita> citas, int mesBuscado) {
        System.out.println("Citas del mes " + mesBuscado + ":");
        boolean encontrada = false;
        for (Cita cita : citas) {
            if (cita.getMes() == mesBuscado) {
                System.out.println("ID: " + cita.getId() + ", Doctor: " + cita.getDoctor() + ", Paciente: " + cita.getPaciente() + ", Hora: " + cita.getHora() + ", Día: " + cita.getDia() + ", Mes: " + cita.getMes());
                encontrada = true;
            }
        }
        if (!encontrada) {
            System.out.println("No se encontraron citas para el mes " + mesBuscado + ".");
        }
    }

    private static void mostrarCitasPorDoctor(List<Cita> citas, String doctorBuscado) {
        System.out.println("Citas del doctor " + doctorBuscado + ":");
        boolean encontrada = false;
        for (Cita cita : citas) {
            if (cita.getDoctor().equalsIgnoreCase(doctorBuscado)) {
                System.out.println("ID: " + cita.getId() + ", Paciente: " + cita.getPaciente() + ", Hora: " + cita.getHora() + ", Día: " + cita.getDia() + ", Mes: " + cita.getMes());
                encontrada = true;
            }
        }
        if (!encontrada) {
            System.out.println("No se encontraron citas para el doctor " + doctorBuscado + ".");
        }
    }

    private static void mostrarCitasPorPaciente(List<Cita> citas, String pacienteBuscado) {
        System.out.println("Citas del paciente " + pacienteBuscado + ":");
        boolean encontrada = false;
        for (Cita cita : citas) {
            if (cita.getPaciente().equalsIgnoreCase(pacienteBuscado)) {
                System.out.println("ID: " + cita.getId() + ", Doctor: " + cita.getDoctor() + ", Hora: " + cita.getHora() + ", Día: " + cita.getDia() + ", Mes: " + cita.getMes());
                encontrada = true;
            }
        }
        if (!encontrada) {
            System.out.println("No se encontraron citas para el paciente " + pacienteBuscado + ".");
        }
    }

    private static void mostrarCitaPorId(List<Cita> citas, int idBuscado) {
        boolean encontrada = false;
        for (Cita cita : citas) {
            if (cita.getId() == idBuscado) {
                System.out.println("Cita encontrada:");
                System.out.println("Doctor: " + cita.getDoctor() + ", Paciente: " + cita.getPaciente() + ", Hora: " + cita.getHora() + ", Día: " + cita.getDia() + ", Mes: " + cita.getMes());
                encontrada = true;
                break;
            }
        }
        if (!encontrada) {
            System.out.println("No se encontró ninguna cita con el ID " + idBuscado + ".");
        }
    }

    private static int obtenerUltimoId(String nombreArchivo) {
        int ultimoId = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(",");
                int id = Integer.parseInt(datos[0].trim());
                if (id > ultimoId) {
                    ultimoId = id;
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo " + nombreArchivo + ": " + e.getMessage());
        }
        return ultimoId;
    }
}
