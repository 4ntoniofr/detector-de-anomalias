# Detector de anomalías

Detector de anomalías que, en base a mediciones de distintos dispositivos, es capaz de encontrar datos anómalos en mediciones posteriores.

Entre sus funcionalidades, puede detectar anomalías de varios dispositivos a la vez e incluso en tiempo real.

Esto lo consigue utilizando [Cadenas de Markov](https://en.wikipedia.org/wiki/Markov_chain).

## Ejecución

Se necesita [Java 18](https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html) o superior
para la ejecución del programa.

```bash
  java -jar <directorio>/AnomalyDetector.jar
```

## Documentación

### Formato de ficheros

El formato de los ficheros para ejecutar debe ser el siguiente:

```
  <identificador_dispositivo>,<tiempo_de_medida>,<medida>
```

### Entrenamiento

Para entrenar el modelo y generar la matriz de probabilidades que será la que nos ayudará a detectar anomalías
seleccionaremos la opción 1 del menú "Cargar fichero de entrenamiento y entrenar".

### Persistencia

Con la finalidad de no tener que entrenar el modelo cada vez que ejecutemos el programa, este
te da la posibilidad de guardar la matriz de probabilidades, que es el resultado del entrenamiento,
en un fichero de texto para poder cargarla en una ejecución posterior.

### Detección de anomalías en tiempo real

En la opción 5 del manú (Detectar anomalias en tiempo real) el programa leerá mediciones por consola
hasta que se le introduzca algo distinto a un número entero. De esta forma avisará cuando se introduzcan
valores anómalos,
