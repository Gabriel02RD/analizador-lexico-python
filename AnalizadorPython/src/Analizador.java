import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Analizador {

    // Palabras clave de Python
    static List<String> palabrasClave = Arrays.asList(
        "def", "return", "if", "else", "elif", "while", "for", "in",
        "import", "from", "as", "class", "try", "except", "finally",
        "True", "False", "None", "and", "or", "not", "is", "pass", "break"
    );

    // Operadores de Python
    static List<String> operadores = Arrays.asList(
        "**=", "//=", "==", "!=", "<=", ">=", "+=", "-=", "*=", "/=",
        "%=", "&=", "|=", "^=", "<<", ">>", "**", "//", ":=", "->",
        "+", "-", "*", "/", "%", "=", "<", ">", "&", "|", "^", "~"
    );

    // Simbolos especiales (delimitadores)
    static List<Character> simbolos = Arrays.asList(
        '(', ')', '[', ']', '{', '}', ',', ':', '.', ';', '@'
    );

    // Contadores para el resumen
    static int cuentaReservadas;
    static int cuentaIdentificadores;
    static int cuentaNumeros;
    static int cuentaCadenas;
    static int cuentaOperadores;
    static int cuentaSimbolos;

    static List<String> listaErrores;
    static List<String[]> tablaTokens;
    static List<String[]> tablaSimbolos;

    static Scanner leer = new Scanner(System.in);

    public static void main(String[] args) {
        boolean salir = false;

        // Menu en bucle hasta que el usuario elija salir
        while (!salir) {
            System.out.println();
            System.out.println("=== ANALIZADOR LEXICO PARA PYTHON ===");
            System.out.println("1. Escribir mi propio codigo");
            System.out.println("2. Usar un ejemplo listo");
            System.out.println("3. Salir");
            System.out.print("Elige una opcion: ");

            String opcion = leer.nextLine().trim();

            if (opcion.equals("1")) {
                pedirCodigo();
            } else if (opcion.equals("2")) {
                reiniciar();
                analizar(ejemplo());
                mostrarReporte();
                pausa();
            } else if (opcion.equals("3")) {
                salir = true;
                System.out.println("Hasta luego.");
            } else {
                System.out.println("Opcion no valida.");
            }
        }
    }

    // Pide el codigo hasta que el usuario escriba FIN
    static void pedirCodigo() {
        System.out.println();
        System.out.println("Escribe tu codigo Python.");
        System.out.println("Cuando termines, escribe FIN en una linea nueva.");
        System.out.println();

        String codigo = "";
        String linea = leer.nextLine();
        while (!linea.trim().equalsIgnoreCase("FIN")) {
            codigo = codigo + linea + "\n";
            linea = leer.nextLine();
        }

        if (codigo.trim().isEmpty()) {
            System.out.println();
            System.out.println("No escribiste nada. Volviendo al menu...");
            return;
        }

        reiniciar();
        analizar(codigo);
        mostrarReporte();
        pausa();
    }

    // Espera un Enter para no llenar la pantalla de mensajes
    static void pausa() {
        System.out.println();
        System.out.println("Pulsa Enter para volver al menu...");
        leer.nextLine();
    }

    // Reinicia todo antes de cada analisis
    static void reiniciar() {
        cuentaReservadas = 0;
        cuentaIdentificadores = 0;
        cuentaNumeros = 0;
        cuentaCadenas = 0;
        cuentaOperadores = 0;
        cuentaSimbolos = 0;
        listaErrores = new ArrayList<>();
        tablaTokens = new ArrayList<>();
        tablaSimbolos = new ArrayList<>();
    }

    static String ejemplo() {
        return "def suma(a, b):\n"
             + "    # esto es un comentario\n"
             + "    resultado = a + b\n"
             + "    return resultado\n"
             + "\n"
             + "x = suma(10, 20)\n"
             + "if x >= 30:\n"
             + "    print(\"grande\")\n";
    }

    // Recorre el codigo caracter por caracter y clasifica cada token
    static void analizar(String codigo) {
        int i = 0;
        int linea = 1;
        int columna = 1;

        while (i < codigo.length()) {
            char c = codigo.charAt(i);

            if (c == ' ' || c == '\t') {
                i = i + 1;
                columna = columna + 1;
            }
            else if (c == '\n') {
                linea = linea + 1;
                columna = 1;
                i = i + 1;
            }
            // Comentario: se ignora hasta el fin de linea
            else if (c == '#') {
                while (i < codigo.length() && codigo.charAt(i) != '\n') {
                    i = i + 1;
                    columna = columna + 1;
                }
            }
            // Cadena: se lee hasta la comilla de cierre
            else if (c == '"' || c == '\'') {
                char comilla = c;
                int inicio = i;
                int colInicio = columna;
                i = i + 1;
                columna = columna + 1;
                boolean cerrada = false;
                while (i < codigo.length() && codigo.charAt(i) != '\n') {
                    if (codigo.charAt(i) == comilla) {
                        cerrada = true;
                        i = i + 1;
                        columna = columna + 1;
                        break;
                    }
                    i = i + 1;
                    columna = columna + 1;
                }
                String texto = codigo.substring(inicio, i);
                if (cerrada) {
                    cuentaCadenas = cuentaCadenas + 1;
                    tablaTokens.add(new String[]{texto, "constante", "" + linea, "" + colInicio});
                } else {
                    listaErrores.add("Linea " + linea + ": cadena sin cerrar -> " + texto);
                }
            }
            // Numero entero
            else if (Character.isDigit(c)) {
                int inicio = i;
                int colInicio = columna;
                while (i < codigo.length() && Character.isDigit(codigo.charAt(i))) {
                    i = i + 1;
                    columna = columna + 1;
                }
                String num = codigo.substring(inicio, i);
                cuentaNumeros = cuentaNumeros + 1;
                tablaTokens.add(new String[]{num, "constante", "" + linea, "" + colInicio});
            }
            // Identificador o palabra clave
            else if (Character.isLetter(c) || c == '_') {
                int inicio = i;
                int colInicio = columna;
                while (i < codigo.length()
                        && (Character.isLetterOrDigit(codigo.charAt(i))
                            || codigo.charAt(i) == '_')) {
                    i = i + 1;
                    columna = columna + 1;
                }
                String palabra = codigo.substring(inicio, i);
                if (palabrasClave.contains(palabra)) {
                    cuentaReservadas = cuentaReservadas + 1;
                    tablaTokens.add(new String[]{palabra, "reservada", "" + linea, "" + colInicio});
                } else {
                    cuentaIdentificadores = cuentaIdentificadores + 1;
                    tablaTokens.add(new String[]{palabra, "identificador", "" + linea, "" + colInicio});
                    // Solo se agrega la primera vez que aparece
                    if (!existeEnTablaSimbolos(palabra)) {
                        tablaSimbolos.add(new String[]{palabra, "variable", "" + linea, "" + colInicio});
                    }
                }
            }
            else {
                // Se buscan operadores primero, del mas largo al mas corto
                String operador = encontrarOperador(codigo, i);
                if (operador != null) {
                    cuentaOperadores = cuentaOperadores + 1;
                    tablaTokens.add(new String[]{operador, "operador", "" + linea, "" + columna});
                    i = i + operador.length();
                    columna = columna + operador.length();
                }
                else if (simbolos.contains(c)) {
                    cuentaSimbolos = cuentaSimbolos + 1;
                    tablaTokens.add(new String[]{"" + c, "simbolo", "" + linea, "" + columna});
                    i = i + 1;
                    columna = columna + 1;
                }
                else {
                    listaErrores.add("Linea " + linea + ", columna " + columna
                            + ": caracter '" + c + "' no reconocido");
                    i = i + 1;
                    columna = columna + 1;
                }
            }
        }
    }

    // Busca el operador mas largo que coincida en la posicion i
    static String encontrarOperador(String codigo, int i) {
        for (int k = 0; k < operadores.size(); k++) {
            String op = operadores.get(k);
            if (codigo.startsWith(op, i)) {
                return op;
            }
        }
        return null;
    }

    static boolean existeEnTablaSimbolos(String lexema) {
        for (int k = 0; k < tablaSimbolos.size(); k++) {
            if (tablaSimbolos.get(k)[0].equals(lexema)) {
                return true;
            }
        }
        return false;
    }

    static void mostrarReporte() {
        System.out.println();
        System.out.println("LISTA DE TOKENS");
        System.out.println("---------------");
        System.out.println(formato("LEXEMA", 16) + formato("TOKEN", 16)
                + formato("LINEA", 8) + "COLUMNA");
        for (int k = 0; k < tablaTokens.size(); k++) {
            String[] t = tablaTokens.get(k);
            System.out.println(formato(t[0], 16) + formato(t[1], 16)
                    + formato(t[2], 8) + t[3]);
        }

        System.out.println();
        System.out.println("TABLA DE SIMBOLOS");
        System.out.println("-----------------");
        if (tablaSimbolos.size() == 0) {
            System.out.println("(vacia)");
        } else {
            System.out.println(formato("LEXEMA", 16) + formato("TIPO", 14)
                    + formato("LINEA", 8) + "COLUMNA");
            for (int k = 0; k < tablaSimbolos.size(); k++) {
                String[] s = tablaSimbolos.get(k);
                System.out.println(formato(s[0], 16) + formato(s[1], 14)
                        + formato(s[2], 8) + s[3]);
            }
        }

        System.out.println();
        System.out.println("RESUMEN");
        System.out.println("-------");
        System.out.println("Palabras reservadas : " + cuentaReservadas);
        System.out.println("Identificadores     : " + cuentaIdentificadores);
        System.out.println("Numeros             : " + cuentaNumeros);
        System.out.println("Cadenas             : " + cuentaCadenas);
        System.out.println("Operadores          : " + cuentaOperadores);
        System.out.println("Simbolos            : " + cuentaSimbolos);
        System.out.println("Errores             : " + listaErrores.size());

        if (listaErrores.size() > 0) {
            System.out.println();
            System.out.println("ERRORES");
            System.out.println("-------");
            for (int k = 0; k < listaErrores.size(); k++) {
                System.out.println("  " + listaErrores.get(k));
            }
        }
    }

    // Rellena con espacios para que las columnas queden alineadas
    static String formato(String texto, int ancho) {
        String resultado = texto;
        while (resultado.length() < ancho) {
            resultado = resultado + " ";
        }
        return resultado;
    }
}