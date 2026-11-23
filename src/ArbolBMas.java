import java.util.ArrayList;

/**
 * Árbol B+ de orden m (cada nodo puede tener como máximo m hijos).
 * - Todos los datos viven en las hojas; los nodos internos solo guardan claves.
 * - Las hojas están enlazadas entre sí.
 * - Se mantiene balanceado automáticamente al insertar y al eliminar.
 */
public class ArbolBMas {

    // Atributos
    private final int orden;
    private NodoArbolBMas raiz;

    // Métodos
    // Constructor
    public ArbolBMas(int orden) {
        if(orden < 3) {
            throw new IllegalArgumentException("El orden mínimo de un árbol B+ es 3.");
        }
        this.orden = orden;
        raiz = new NodoArbolBMas(true);
    }

    // Inserción de una llave (con su dato) en el árbol B+
    // Devuelve false si la clave ya existía (no se permiten claves repetidas)
    public boolean insertar(int clave, String dato) {
        if(buscar(clave)) {
            return false;
        }

        // Se inserta primero de forma recursiva
        insertarRecursivo(this.raiz, clave, dato);

        // Se verifica si la raíz se desbordó
        if(this.raiz.getClaves().size() == orden) {
            NodoArbolBMas nuevaRaiz = new NodoArbolBMas(false);
            nuevaRaiz.getHijos().add(this.raiz);
            dividirHijo(nuevaRaiz, 0);
            this.raiz = nuevaRaiz;
        }
        return true;
    }

    // Inserción recursiva de una llave
    // Permite que los nodos se desborden temporalmente y los divide al regresar de la recursión
    private void insertarRecursivo(NodoArbolBMas nodo, int llave, String dato) {
        int i = nodo.getClaves().size() - 1;

        if(nodo.esHoja()) {
            while(i >= 0 && llave < nodo.getClaves().get(i)) {
                i--;
            }
            // La clave y su dato se insertan en la misma posición
            nodo.getClaves().add(i + 1, llave);
            nodo.getDatos().add(i + 1, dato);
        } else {
            while(i >= 0 && llave < nodo.getClaves().get(i)) {
                i--;
            }
            i++;
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            // Llamada recursiva para insertar la llave primero en el hijo correspondiente
            insertarRecursivo(hijo, llave, dato);

            // Evaluación de desbordamiento al retornar de la recursividad
            if(hijo.getClaves().size() == orden) {
                dividirHijo(nodo, i);
            }
        }
    }

    // Dividir un nodo que está lleno
    private void dividirHijo(NodoArbolBMas padre, int indice) {
        NodoArbolBMas nodoLleno = padre.getHijos().get(indice);
        NodoArbolBMas nuevoNodo = new NodoArbolBMas(nodoLleno.esHoja());
        int mitad = orden / 2;

        if (nodoLleno.esHoja()) {
            // En nodos hoja, la llave media se mantiene en la hoja derecha (y se copia al padre)
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad, nodoLleno.getClaves().size())));
            nuevoNodo.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(mitad, nodoLleno.getDatos().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));
            nodoLleno.setDatos(new ArrayList<>(nodoLleno.getDatos().subList(0, mitad)));

            // Se enlaza la hoja nueva en la lista de hojas: nodoLleno -> nuevoNodo -> (antigua siguiente)
            nuevoNodo.setSiguiente(nodoLleno.getSiguiente());
            nodoLleno.setSiguiente(nuevoNodo);

            // El padre recibe como separador la primera llave del nuevo nodo
            int llaveMedia = nuevoNodo.getClaves().getFirst();
            padre.getClaves().add(indice, llaveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
        else {
            // En nodos internos, la llave media sube y se elimina del hijo
            int claveMedia = nodoLleno.getClaves().get(mitad);
            nuevoNodo.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(mitad + 1, nodoLleno.getClaves().size())));
            nodoLleno.setClaves(new ArrayList<>(nodoLleno.getClaves().subList(0, mitad)));

            // Mover los hijos del nodo dividido
            nuevoNodo.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(mitad + 1, nodoLleno.getHijos().size())));
            nodoLleno.setHijos(new ArrayList<>(nodoLleno.getHijos().subList(0, mitad + 1)));

            padre.getClaves().add(indice, claveMedia);
            padre.getHijos().add(indice + 1, nuevoNodo);
        }
    }

    // Impresión del árbol como parte de la interfaz pública del árbol
    public void imprimirArbol() {
        if(raiz.esHoja() && raiz.getClaves().isEmpty()) {
            System.out.println("(El árbol está vacío)");
            return;
        }
        imprimirNodo(raiz, "", true);
    }

    // Impresión recursiva privada de los nodos a partir de uno inicial
    private void imprimirNodo(NodoArbolBMas nodo, String indentacion, boolean esUltimo) {
        System.out.println(indentacion + "+- " + (nodo.esHoja() ? "Hoja > " : "Interno > ") + nodo.getClaves());
        indentacion += esUltimo ? "   " : "|  ";
        for(int i = 0; i < nodo.getHijos().size(); i++) {
            imprimirNodo(nodo.getHijos().get(i), indentacion, i == (nodo.getHijos().size() - 1));
        }
    }

    // Impresión de la lista enlazada de hojas, de la primera a la última
    public void imprimirHojas() {
        NodoArbolBMas hoja = raiz;
        while(!hoja.esHoja()) {
            hoja = hoja.getHijos().getFirst();
        }

        StringBuilder cadena = new StringBuilder();
        while(hoja != null) {
            cadena.append(hoja.getClaves()).append(" -> ");
            hoja = hoja.getSiguiente();
        }
        System.out.println(cadena + "null");
    }

    // Índice del hijo por el que se debe bajar para encontrar una clave
    // (cantidad de claves del nodo que son menores o iguales a la clave buscada)
    private int indiceHijo(NodoArbolBMas nodo, int clave) {
        int i = 0;
        while(i < nodo.getClaves().size() && clave >= nodo.getClaves().get(i)) {
            i++;
        }
        return i;
    }

    // Desciende desde la raíz hasta la hoja donde está (o debería estar) la clave
    private NodoArbolBMas encontrarHoja(int clave) {
        NodoArbolBMas nodo = raiz;
        while(!nodo.esHoja()) {
            nodo = nodo.getHijos().get(indiceHijo(nodo, clave));
        }
        return nodo;
    }

    // Búsqueda de una llave como parte de la interfaz pública del árbol
    // Siempre se llega hasta la hoja: las claves de los nodos internos son solo separadores
    // y, después de eliminar, pueden quedar claves que ya no existen en las hojas
    public boolean buscar(int llave) {
        return encontrarHoja(llave).getClaves().contains(llave);
    }

    // Devuelve el dato asociado a una clave, o null si la clave no existe
    public String buscarDato(int llave) {
        NodoArbolBMas hoja = encontrarHoja(llave);
        int posicion = hoja.getClaves().indexOf(llave);
        return posicion >= 0 ? hoja.getDatos().get(posicion) : null;
    }

    // Eliminación
    // Devuelve false si la clave no existía
    public boolean eliminar(int clave) {
        if(!buscar(clave)) {
            return false;
        }

        eliminarRecursivo(raiz, clave);

        // Si la raíz interna se quedó sin claves, su único hijo pasa a ser la nueva raíz (el árbol pierde un nivel)
        if(!raiz.esHoja() && raiz.getClaves().isEmpty()) {
            raiz = raiz.getHijos().getFirst();
        }
        return true;
    }

    // Eliminación recursiva: borra en la hoja y, al regresar de la recursión, rebalancea los hijos que quedaron con pocas claves
    private void eliminarRecursivo(NodoArbolBMas nodo, int clave) {
        if(nodo.esHoja()) {
            int posicion = nodo.getClaves().indexOf(clave);
            nodo.getClaves().remove(posicion);
            nodo.getDatos().remove(posicion);
        } else {
            int i = indiceHijo(nodo, clave);
            NodoArbolBMas hijo = nodo.getHijos().get(i);

            eliminarRecursivo(hijo, clave);

            if(hijo.getClaves().size() < minimoClaves(hijo)) {
                rebalancear(nodo, i);
            }
        }
    }

    // Mínimo de claves que debe tener un nodo (la raíz está exenta)
    // Hojas: m/2 claves. Internos: ceil(m/2) hijos, es decir, ceil(m/2) - 1 claves
    private int minimoClaves(NodoArbolBMas nodo) {
        return nodo.esHoja() ? orden / 2 : (orden + 1) / 2 - 1;
    }

    // Rebalancea el hijo en la posición i del padre: primero intenta pedir prestado a un hermano, si no puede se fusiona con él
    private void rebalancear(NodoArbolBMas padre, int i) {
        
        NodoArbolBMas izquierdo = i > 0 ? padre.getHijos().get(i - 1) : null;
        NodoArbolBMas derecho = i < padre.getHijos().size() - 1 ? padre.getHijos().get(i + 1) : null;

        if(izquierdo != null && izquierdo.getClaves().size() > minimoClaves(izquierdo)) {
            prestarDeIzquierdo(padre, i);
        } else if(derecho != null && derecho.getClaves().size() > minimoClaves(derecho)) {
            prestarDeDerecho(padre, i);
        } else if(izquierdo != null) {
            fusionar(padre, i - 1);
        } else {
            fusionar(padre, i);
        }
    }

    // Toma la última clave del hermano izquierdo para el hijo i
    private void prestarDeIzquierdo(NodoArbolBMas padre, int i) {
        NodoArbolBMas hijo = padre.getHijos().get(i);
        NodoArbolBMas izquierdo = padre.getHijos().get(i - 1);
        int ultima = izquierdo.getClaves().size() - 1;

        if(hijo.esHoja()) {
            // En hojas, la clave y su dato se mueven y el separador pasa a ser la nueva primera clave del hijo
            hijo.getClaves().add(0, izquierdo.getClaves().remove(ultima));
            hijo.getDatos().add(0, izquierdo.getDatos().remove(ultima));
            padre.getClaves().set(i - 1, hijo.getClaves().getFirst());
        } else {
            // En nodos internos se rota: el separador baja al hijo y la última clave del hermano sube al padre
            hijo.getClaves().add(0, padre.getClaves().get(i - 1));
            padre.getClaves().set(i - 1, izquierdo.getClaves().remove(ultima));
            hijo.getHijos().add(0, izquierdo.getHijos().remove(izquierdo.getHijos().size() - 1));
        }
    }

    // Toma la primera clave del hermano derecho para el hijo i
    private void prestarDeDerecho(NodoArbolBMas padre, int i) {
        NodoArbolBMas hijo = padre.getHijos().get(i);
        NodoArbolBMas derecho = padre.getHijos().get(i + 1);

        if(hijo.esHoja()) {
            // En hojas, la clave y su dato se mueven y el separador pasa a ser la nueva primera clave del hermano
            hijo.getClaves().add(derecho.getClaves().remove(0));
            hijo.getDatos().add(derecho.getDatos().remove(0));
            padre.getClaves().set(i, derecho.getClaves().getFirst());
        } else {
            // En nodos internos se rota: el separador baja al hijo y la primera clave del hermano sube al padre
            hijo.getClaves().add(padre.getClaves().get(i));
            padre.getClaves().set(i, derecho.getClaves().remove(0));
            hijo.getHijos().add(derecho.getHijos().remove(0));
        }
    }

    // Fusiona el hijo i+1 dentro del hijo i y elimina del padre el separador y el hijo sobrante
    private void fusionar(NodoArbolBMas padre, int i) {
        NodoArbolBMas izquierdo = padre.getHijos().get(i);
        NodoArbolBMas derecho = padre.getHijos().get(i + 1);

        if(izquierdo.esHoja()) {
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getDatos().addAll(derecho.getDatos());
            // La hoja derecha desaparece, así que se salta en la lista enlazada
            izquierdo.setSiguiente(derecho.getSiguiente());
        } else {
            // En nodos internos el separador del padre baja para quedar entre las claves de ambos
            izquierdo.getClaves().add(padre.getClaves().get(i));
            izquierdo.getClaves().addAll(derecho.getClaves());
            izquierdo.getHijos().addAll(derecho.getHijos());
        }

        padre.getClaves().remove(i);
        padre.getHijos().remove(i + 1);
    }

    // Recorrido por rango: busca la primera clave >= claveInicial e imprime esa clave y las siguientes, hasta n elementos con sus datos
    public void recorrerRango(int claveInicial, int n) {
        if(n <= 0) {
            System.out.println("La cantidad de elementos debe ser mayor que cero.");
            return;
        }

        // Se localiza la hoja y la posición de la primera clave que cumple
        NodoArbolBMas hoja = encontrarHoja(claveInicial);
        int i = 0;
        while(i < hoja.getClaves().size() && hoja.getClaves().get(i) < claveInicial) {
            i++;
        }

        // Se avanza por las hojas enlazadas hasta imprimir n elementos o quedarse sin hojas
        int impresos = 0;
        while(hoja != null && impresos < n) {
            while(i < hoja.getClaves().size() && impresos < n) {
                System.out.println("  Clave " + hoja.getClaves().get(i) + " -> " + hoja.getDatos().get(i));
                i++;
                impresos++;
            }
            hoja = hoja.getSiguiente();
            i = 0;
        }

        if(impresos == 0) {
            System.out.println("No hay elementos con clave mayor o igual a " + claveInicial + ".");
        } else if(impresos < n) {
            System.out.println("(Solo había " + impresos + " elemento(s) disponible(s) desde esa clave)");
        }
    }
}