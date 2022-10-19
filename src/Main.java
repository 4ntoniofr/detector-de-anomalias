import java.io.FileNotFoundException;
import java.util.Scanner;

import pkAnomalias.UtilsAnomalias;

public class Main {
    public static void main(String[] args) {
        /*
         * Creamos una instancia de la clase que contiene todos los procedimientos
         * que vamos a utilizar
         */
        UtilsAnomalias utilsAnomalias = new UtilsAnomalias();

        /* Constantes a utilizar durante el análisis */
        final int tamVentana = 4;
        final double probabilidadSiCero = 0.001;

        /* Menu de opciones */
        Scanner teclado = new Scanner(System.in);
        boolean exit = false;
        System.out.println("\n====== Sistema de deteccion de anomalias ======");
        do {
            System.out.println("\n1. Cargar fichero de entrenamiento y entrenar");
            System.out.println("2. Detectar anomalias de un fichero");
            System.out.println("3. Guardar matriz generada en un fichero");
            System.out.println("4. Cargar matriz de fichero");
            System.out.println("5. Detectar anomalias en tiempo real");
            System.out.println("6. Salir");
            System.out.print("Selecciona una opcion: ");
            try {
                int opcion = Integer.parseInt(teclado.nextLine());
                switch (opcion) {
                    case 1: // Entrenar
                        System.out.print("Inserte el nombre del fichero: ");
                        String nombreFichero = teclado.nextLine();
                        try {
                            utilsAnomalias.leerFichero(nombreFichero, true);
                            utilsAnomalias.getMatrizOcurrencias();
                            utilsAnomalias.getMatrizPosibilidades();
                            System.out.println("Matriz de probabilidades generada con exito.");
                        } catch (NumberFormatException e) { // Si el fichero no sigue el formato esperado
                            System.err.println("Formato invalido");
                        } catch (IllegalArgumentException e) { // Si no se ha cargado el fichero de forma correcta
                            System.err.println("Antes de generar una matriz debe cargar un fichero de entrenamiento");
                        } catch (FileNotFoundException e) { // Si no se ha encontrado el fichero especificado
                            System.err.println("Fichero " + nombreFichero + " no encontrado");
                        } catch (Exception e) {
                        }
                        break;
                    case 2: // Detectar anomalias de un fichero
                        System.out.print("Introduce el fichero a leer: ");
                        String nombreFicheroAnalizar = teclado.nextLine();
                        try {
                            utilsAnomalias.leerFichero(nombreFicheroAnalizar, false);
                            utilsAnomalias.detectarAnomaliasFichero(tamVentana, probabilidadSiCero);
                        } catch (NumberFormatException e) { // Si el fichero no sigue el formato esperado
                            System.err.println("Formato invalido");
                        } catch (IllegalArgumentException e) { // Si no se ha entrenado el sistema antes
                            System.err.println("Entrena el sistema antes de detectar anomalias");
                        } catch (FileNotFoundException e) { // Si no se ha encontrado el fichero especificado
                            System.err.println("Fichero " + nombreFicheroAnalizar + " no encontrado");
                        } catch (Exception e) {
                        }
                        break;
                    case 3: // Guardar matriz en fichero
                        try {
                            utilsAnomalias.guardarMatrizProbabilidades(tamVentana);
                        } catch (Exception e) { // Si no se ha entrenado al sistema antes
                            System.err.println("Antes de guardar una matriz debe generarla");
                        }
                        break;
                    case 4: // Cargar matriz de fichero
                        System.out.print("Introduce el fichero a leer: ");
                        String nombreFicheroMatriz = teclado.nextLine();
                        try {
                            utilsAnomalias.leerMatriz(nombreFicheroMatriz);
                            System.out.println("Matriz cargada con exito");
                        } catch (FileNotFoundException e) { // Si no se ha encontrado el fichero especificado
                            System.err.println("Fichero " + nombreFicheroMatriz + " no encontrado");
                        } catch (Exception e) { // Si el fichero no sigue el formato esperado
                            System.err.println("Formato de fichero invalido");
                        }
                        break;

                    case 5: // Detectar anomalias 'online'
                        try {
                            utilsAnomalias.detectarAnomaliasOnline(tamVentana, probabilidadSiCero);
                        } catch (IllegalArgumentException e) { // Si no se ha entrenado al sistema antes
                            System.err.println("Antes debe generar o importar una matriz");
                        }
                        break;
                    case 6: // Salir
                        teclado.close();
                        exit = true;
                        break;
                    default:
                        System.out.println("Opcion incorrecta");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion incorrecta");
            }
        } while (!exit);
    }
}
