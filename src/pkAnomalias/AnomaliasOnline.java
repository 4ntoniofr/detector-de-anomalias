package pkAnomalias;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class AnomaliasOnline {
    List<Double> probabilidades; // Lista que va guardando las probabilidades de las transiciones
    List<Integer> mediciones; // Lista que va guardando las mediciones introducidas
    private Scanner teclado;

    public AnomaliasOnline() {
        teclado = new Scanner(System.in);
        probabilidades = new ArrayList<>();
        mediciones = new ArrayList<>();
    }

    /*
     * Funcion que detecta anomalias en tiempo real desde mediciones insertadas
     * desde teclado
     */
    public void detectarAnomalias(Map<Integer, Map<Integer, Double>> matrizProbabilidades, Double minimaProbabilidad,
            int tamVentana, double probabilidadSiCero) throws IllegalArgumentException {
        if (matrizProbabilidades == null || matrizProbabilidades.size() == 0) { // Si no se ha entrenado previamente al
                                                                                // sistema
            throw new IllegalArgumentException();
        }
        System.out.println(
                "Introduzca las mediciones con un salto de línea entre ellas.\nIntroduce cualquier cosa que no sea un entero para detener la medicion");
        while (true) {
            try {
                Integer medicion = Integer.parseInt(teclado.nextLine());
                mediciones.add(medicion);
                if (mediciones.size() > 1) {
                    if (matrizProbabilidades.containsKey(medicion)) {
                        probabilidades.add(matrizProbabilidades.get(medicion)
                                .getOrDefault(mediciones.get(mediciones.size() - 2), probabilidadSiCero));
                    } else {
                        probabilidades.add(probabilidadSiCero);
                    }
                    if (mediciones.size() >= tamVentana) {
                        double currentWindowPossibility = 1;
                        for (int i = 1; i < tamVentana; i++) {
                            currentWindowPossibility *= probabilidades.get(probabilidades.size() - i);
                        }
                        if (currentWindowPossibility < minimaProbabilidad) {
                            System.out.println("Anomalia detectada en la ventana actual");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Medicion finalizada");
                break;
            }
        }
    }
}
