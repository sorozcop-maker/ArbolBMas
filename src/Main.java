import java.util.Scanner;

public class Main {

    // Lector único de consola para todo el programa
    private static final Scanner lector = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Árbol B+ ===");
        int orden = leerEntero("Orden del árbol (mínimo 3, por ejemplo 4): ", 3);

        ArbolBMas arbol = new ArbolBMas(orden);
        menu(arbol);

        System.out.println("¡Hasta luego!");
    }

    // Menú principal de consola
    private static void menu(ArbolBMas arbol) {
        int opcion;
        do {
            System.out.println();
            System.out.println("------------ MENÚ ------------");
            System.out.println("1. Insertar clave y dato");
            System.out.println("2. Buscar clave");
            System.out.println("3. Eliminar clave");
            System.out.println("4. Recorrer rango");
            System.out.println("5. Mostrar árbol");
            System.out.println("6. Mostrar hojas enlazadas");
            System.out.println("7. Cargar datos de ejemplo");
            System.out.println("0. Salir");
            System.out.println("------------------------------");
            opcion = leerEntero("Elija una opción: ", Integer.MIN_VALUE);

            switch(opcion) {
                case 1 -> insertar(arbol);
                case 2 -> buscar(arbol);
                case 3 -> eliminar(arbol);
                case 4 -> recorrerRango(arbol);
                case 5 -> arbol.imprimirArbol();
                case 6 -> arbol.imprimirHojas();
                case 7 -> cargarEjemplo(arbol);
                case 0 -> { }
                default -> System.out.println("Opción no válida, intente de nuevo.");
            }
        } while(opcion != 0);
    }

    private static void insertar(ArbolBMas arbol) {
        int clave = leerEntero("Clave (número entero): ", Integer.MIN_VALUE);
        System.out.print("Dato asociado (texto): ");
        String dato = lector.nextLine().trim();

        if(arbol.insertar(clave, dato)) {
            System.out.println("Se insertó la clave " + clave + ".");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " ya existe, no se insertó.");
        }
    }

    private static void buscar(ArbolBMas arbol) {
        int clave = leerEntero("Clave a buscar: ", Integer.MIN_VALUE);
        String dato = arbol.buscarDato(clave);

        if(dato != null) {
            System.out.println("Encontrada: clave " + clave + " -> " + dato);
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void eliminar(ArbolBMas arbol) {
        int clave = leerEntero("Clave a eliminar: ", Integer.MIN_VALUE);

        if(arbol.eliminar(clave)) {
            System.out.println("Se eliminó la clave " + clave + ".");
            arbol.imprimirArbol();
        } else {
            System.out.println("La clave " + clave + " no existe en el árbol.");
        }
    }

    private static void recorrerRango(ArbolBMas arbol) {
        int clave = leerEntero("Clave inicial: ", Integer.MIN_VALUE);
        int n = leerEntero("Cantidad de elementos a mostrar (mínimo 1): ", 1);

        System.out.println("Elementos desde la clave " + clave + ":");
        arbol.recorrerRango(clave, n);
    }

    // Inserta datos de ejemplo para probar el árbol rápidamente (las claves repetidas se ignoran)
    private static void cargarEjemplo(ArbolBMas arbol) {
        int[] claves = {10, 20, 5, 6, 15, 30, 25, 40, 45, 50};
        String[] datos = {"Ana", "Luis", "María", "José", "Carla", "Pedro", "Lucía", "Diego", "Sofía", "Andrés"};

        for(int i = 0; i < claves.length; i++) {
            arbol.insertar(claves[i], datos[i]);
        }
        System.out.println("Datos de ejemplo cargados.");
        arbol.imprimirArbol();
    }

    // Lee un entero validando la entrada; repite la pregunta hasta que sea válido y >= minimo
    private static int leerEntero(String mensaje, int minimo) {
        while(true) {
            System.out.print(mensaje);
            String texto = lector.nextLine().trim();
            try {
                int valor = Integer.parseInt(texto);
                if(valor >= minimo) {
                    return valor;
                }
                System.out.println("El valor debe ser mayor o igual a " + minimo + ".");
            } catch(NumberFormatException e) {
                System.out.println("Entrada no válida, escriba un número entero.");
            }
        }
    }
}