/**
   Author: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Licencia: GPLv3
   Creado el: 14/03/2015

 */

import java.io.RandomAccessFile;
import java.util.Stack;
import java.io.EOFException;
import java.io.IOException;
import java.io.FileNotFoundException;


class plp2 {
    public static void main(String[] args) {
        if (args.length == 1)
            {
                try {
                    RandomAccessFile entrada = new RandomAccessFile(args[0],"r");
                    AnalizadorLexico al = new AnalizadorLexico(entrada);
                    //System.out.println("HOLA");
                    AnalizadorSintacticoSLR aslr = new AnalizadorSintacticoSLR(al);
                    aslr.analizar();
                }
                catch (FileNotFoundException e) {
                    System.out.println("Error, fichero no encontrado: " + args[0]);
                }
            }
        else System.out.println("Error, uso: java plp2 <nomfichero>");
    }
}
