package pkAnomalias;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UtilsAnomalias {
    private Map<String, List<Tuple<Integer, Integer>>> dispositivosEntrenamiento; // Almacena las mediciones de un
                                                                                  // fichero de entrenamiento
    private Map<String, List<Tuple<Integer, Integer>>> dispositivosAnalisis; // Almacena las mediciones de un fichero a
                                                                             // analizar
    private Map<Integer, Map<Integer, Integer>> matrizOcurrencias; // Matriz que almacena el número de veces que sucede
                                                                   // una transicion
    private Map<Integer, Integer> sumaOcurrenciasFila; // La suma de cada una de las 'filas' de la matriz de ocurrencias
    private Map<Integer, Map<Integer, Double>> matrizProbabilidades; // Matriz que almacena la probabilidad de que
                                                                     // suceda una transicion
    private double minProbabilidadCargada = -1; // En caso de que se cargue una matriz, la minima probabilidad que trae
                                                // asignada el fichero

    public UtilsAnomalias() {
        dispositivosEntrenamiento = new HashMap<>();
        dispositivosAnalisis = new HashMap<>();
        matrizOcurrencias = new HashMap<>();
        sumaOcurrenciasFila = new HashMap<>();
        matrizProbabilidades = new HashMap<>();
    }

    /*
     * Procedimiento que lee el fichero pasado por parametro y dependiendo del
     * parametro entrenamiento lo gusrda con intencion de entrenar al sistema o de
     * analizar el fichero en busca de anomalias
     */
    public void leerFichero(String nombreFichero, boolean entrenamiento) throws Exception {
        if (!entrenamiento && matrizProbabilidades.isEmpty()) { // Si quiere analizar el fichero y el sistema no esta
                                                                // entrenado
            throw new IllegalArgumentException();
        }
        File fichero = new File(nombreFichero);
        Scanner sc = new Scanner(fichero);
        if (entrenamiento && (!matrizProbabilidades.isEmpty())) { // En caso de que ya haya entrenado el sistema
            System.out.println(
                    "Al cargar un fichero de entrenamiento nuevo borrara la matriz de probabilidades existente, ¿esta seguro? (y/N)");
            Scanner teclado = new Scanner(System.in);
            String res = teclado.nextLine();
            if (res.compareToIgnoreCase("y") == 0) {
                dispositivosEntrenamiento.clear();
                matrizOcurrencias.clear();
                sumaOcurrenciasFila.clear();
                matrizProbabilidades.clear();
                teclado.close();
            } else {
                System.out.println("Operacion cancelada");
                teclado.close();
                throw new Exception();
            }
        }
        if (entrenamiento) {
            dispositivosEntrenamiento.clear();
        } else {
            dispositivosAnalisis.clear();
        }
        /*
         * Cargamos los datos en dispositivosentrenamiento o dispositivosAnalisis
         * dependiendo del valor del parametro "entrenamiento"
         */
        while (sc.hasNextLine()) {
            String[] parts = sc.nextLine().split(",");
            if (entrenamiento) {
                if (!dispositivosEntrenamiento.containsKey(parts[0])) {
                    dispositivosEntrenamiento.put(parts[0], new ArrayList<>());
                }
                dispositivosEntrenamiento.get(parts[0])
                        .add(new Tuple<Integer, Integer>(Integer.parseInt(parts[2]), Integer.parseInt(parts[1])));
            } else {
                if (!dispositivosAnalisis.containsKey(parts[0])) {
                    dispositivosAnalisis.put(parts[0], new ArrayList<>());
                }
                dispositivosAnalisis.get(parts[0])
                        .add(new Tuple<Integer, Integer>(Integer.parseInt(parts[2]), Integer.parseInt(parts[1])));
            }
        }
        sc.close();
        System.out.println("Fichero " + nombreFichero + " cargado con exito.");
    }

    /*
     * Obtenemos la matriz de ocurrencias asi como la suma de las ocurrencias de
     * cada "fila"
     */
    public void getMatrizOcurrencias() throws IllegalArgumentException {
        if (dispositivosEntrenamiento.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (List<Tuple<Integer, Integer>> listaValores : dispositivosEntrenamiento.values()) {
            for (int i = 1; i < listaValores.size(); i++) {
                int current = listaValores.get(i).getFirst(), prev = listaValores.get(i - 1).getFirst();
                matrizOcurrencias.putIfAbsent(prev, new HashMap<>());
                matrizOcurrencias.get(prev).put(current, matrizOcurrencias.get(prev).getOrDefault(current, 0) + 1);

                sumaOcurrenciasFila.put(prev, sumaOcurrenciasFila.getOrDefault(prev, 0) + 1);
            }
        }
    }

    /* Obtenemos la matriz de probabilidades gracias a la de ocurrencias */
    public void getMatrizPosibilidades() throws IllegalArgumentException {
        if (dispositivosEntrenamiento.isEmpty()) {
            throw new IllegalArgumentException();
        }
        for (Integer key : matrizOcurrencias.keySet()) {
            matrizProbabilidades.put(key, new HashMap<>());
            for (Integer key2 : matrizOcurrencias.get(key).keySet()) {
                matrizProbabilidades.get(key).put(key2,
                        (double) matrizOcurrencias.get(key).get(key2) / (double) sumaOcurrenciasFila.get(key));
            }
        }
    }

    /*
     * Guardamos la matriz de probabilidades asi como la probabilidad minima (de ahi
     * la necesidad del parametro "tamVentana") en un fichero para asegurar la
     * persistencia de nuestro sistema
     */
    public void guardarMatrizProbabilidades(int tamVentana) throws IllegalArgumentException {
        if (matrizProbabilidades.isEmpty() || dispositivosEntrenamiento.isEmpty()) {
            throw new IllegalArgumentException();
        }
        Scanner teclado = new Scanner(System.in);
        System.out.print("Introduce el nombre del fichero en el que se va a guardar la matriz de posibilidades: ");
        String nombreFichero = teclado.nextLine();
        File fichero = new File(nombreFichero);
        try {
            if (fichero.createNewFile()) {
                System.out.println("El fichero " + nombreFichero + " se ha creado con exito");
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(fichero));
            for (Integer key : matrizProbabilidades.keySet()) {
                bw.write(key + ":");
                for (Integer key2 : matrizProbabilidades.get(key).keySet()) {
                    bw.write(key2 + "," + matrizProbabilidades.get(key).get(key2) + ";");
                }
                bw.write("-");
            }
            bw.write("|" + getMinimaProbabilidad(tamVentana));
            bw.close();
            System.out.println("Matriz almacenada en el fichero " + nombreFichero + " con exito");
        } catch (IOException e) { // Si ocurre un error creando o leyendo el fichero destino
            System.err.println("Error creando/leyendo el fichero " + nombreFichero);
        }
    }

    /*
     * Detectamos las anomalias del fichero especificado. Al tener que analizar
     * necesitamos los parámetros tamVentana y la probabilidad asociada a una
     * transicion que no aparezca en la matiz de probabilidades
     */
    public void detectarAnomaliasFichero(int tamVentana, double probabilidadSiCero) throws IllegalArgumentException {
        if (matrizProbabilidades.isEmpty()) {
            throw new IllegalArgumentException();
        }
        /*
         * En caso de que se haya cargado la matriz desde un fichero externo se toma la
         * probabilidad de dicha matriz
         */
        double minimaProbabilidad = minProbabilidadCargada == -1 ? getMinimaProbabilidad(tamVentana)
                : minProbabilidadCargada;

        int numAnomalias = 0;

        for (String dispositivo : dispositivosAnalisis.keySet()) {
            for (int i = 0; i <= dispositivosAnalisis.get(dispositivo).size() - tamVentana; i++) {
                List<Tuple<Integer, Integer>> currentList = dispositivosAnalisis.get(dispositivo);
                double currentWindowPossibility = 1;
                for (int j = 0; j < tamVentana - 1; j++) {
                    if (matrizProbabilidades.containsKey(currentList.get(i + j).getFirst())
                            && matrizProbabilidades.get(currentList.get(i + j).getFirst())
                                    .containsKey(currentList.get(i + j + 1).getFirst())) {
                        currentWindowPossibility *= matrizProbabilidades
                                .get(currentList.get(i + j).getFirst())
                                .get(currentList.get(i + j + 1).getFirst());
                    } else {
                        currentWindowPossibility *= probabilidadSiCero;
                    }
                }
                if (currentWindowPossibility < minimaProbabilidad) {
                    System.out.print("Anomalia encontrada entre las mediciones " + i + " - "
                            + (i + tamVentana - 1) + " del dispositivo " + dispositivo + " Ventana: [");
                    for (int j = 0; j < tamVentana; j++) {
                        System.out.print(currentList.get(i + j).getFirst());
                        if (j < tamVentana - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.println("]");
                    numAnomalias++;
                }
            }
        }

        System.out.println("Se han encontrado " + numAnomalias + " anomalias");
    }

    /* Función que devuelve la mínima probabilidad de los datos de entrenamiento */
    public double getMinimaProbabilidad(int tamVentana) {
        double minProbabilidad = Double.MAX_VALUE;

        for (List<Tuple<Integer, Integer>> listaValores : dispositivosEntrenamiento.values()) {
            for (int i = 0; i <= listaValores.size() - tamVentana; i++) {
                double currentWindowPossibility = 1;
                for (int j = 0; j < tamVentana - 1; j++) {
                    currentWindowPossibility *= matrizProbabilidades.get(listaValores.get(i + j).getFirst())
                            .get(listaValores.get(i + j + 1).getFirst());
                }
                if (currentWindowPossibility < minProbabilidad) {
                    minProbabilidad = currentWindowPossibility;
                }
            }
        }

        return minProbabilidad;
    }

    /*
     * Procedimiento que lee un fichero que contenga la matriz para poder cargarla
     */
    public void leerMatriz(String nombreFichero)
            throws FileNotFoundException, NumberFormatException, IndexOutOfBoundsException {
        Scanner fichero = new Scanner(new File(nombreFichero));
        matrizProbabilidades.clear();
        String matriz = fichero.nextLine();
        String[] minProbabilidadToken = matriz.split("\\|");
        minProbabilidadCargada = Double.parseDouble(minProbabilidadToken[1]);
        String[] rows = minProbabilidadToken[0].split("-");
        for (String row : rows) {
            String[] keys = row.split(":");
            matrizProbabilidades.put(Integer.parseInt(keys[0]), new HashMap<>());
            String[] values = keys[1].split(";");
            for (String value : values) {
                String[] tokens = value.split(",");
                matrizProbabilidades.get(Integer.parseInt(keys[0])).put(Integer.parseInt(tokens[0]),
                        Double.parseDouble(tokens[1]));
            }
        }
    }

    /*
     * Procedimiento que crea y usa una instancia de la clase AnomaliasOnline en la
     * que se encuentran los procedimientos y funciones necesarios para esta opcion
     * del programa
     */
    public void detectarAnomaliasOnline(int tamVentana, double probabilidadSiCero) throws IllegalArgumentException {
        AnomaliasOnline anomaliasOnline = new AnomaliasOnline();
        anomaliasOnline.detectarAnomalias(matrizProbabilidades,
                minProbabilidadCargada == -1 ? getMinimaProbabilidad(tamVentana) : minProbabilidadCargada, tamVentana,
                probabilidadSiCero);
    }
}
