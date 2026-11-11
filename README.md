# Analizador Léxico para Python

Emulador de analizador léxico que procesa código fuente escrito en Python, desarrollado en Java. Reconoce palabras reservadas, identificadores, constantes, operadores y símbolos especiales, y genera como resultado una lista de tokens y una tabla de símbolos con la posición de cada elemento.

## Contexto

Proyecto de la asignatura **Teoría de Compiladores** — Actividad Complementaria del Segundo Corte.

- **Lenguaje de implementación:** Java
- **Lenguaje analizado:** Python
- **Interfaz:** consola


## Características

- Reconocimiento de tokens:
  - Palabras reservadas (`def`, `return`, `if`, `while`, `class`, etc.)
  - Identificadores
  - Constantes (números enteros y cadenas de texto)
  - Operadores (`+`, `-`, `==`, `>=`, `**`, `//`, etc.)
  - Símbolos especiales (`(`, `)`, `[`, `]`, `:`, `,`, etc.)
- Detección de errores léxicos (caracteres no reconocidos, cadenas sin cerrar).
- Generación de tabla de símbolos con la primera aparición de cada identificador.
- Salida estructurada con línea y columna de cada token.

## Requisitos

- Java JDK 8 o superior
- NetBeans (recomendado) o cualquier IDE de Java

## Cómo ejecutar

1. Clona el repositorio:

   ```bash
   git clone https://github.com/Gabriel02RD/analizador-lexico-python.git
