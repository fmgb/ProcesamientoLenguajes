/**
   Author: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 3
   Licencia: GPLv3
   Creado el: 25/03/2015

 */

import java.io.RandomAccessFile;
import java.util.Stack;
import java.io.EOFException;
import java.io.IOException;
import java.io.FileNotFoundException;

class plp3 {
    public static void main(String[] args) {
        if (args.length == 1)
            {
                try {
                    RandomAccessFile entrada = new RandomAccessFile(args[0],"r");
                    AnalizadorLexico al = new AnalizadorLexico(entrada);
                    TraductorDR tdr = new TraductorDR(al);
                    String trad = tdr.S(); // simbolo inicial de la gramatica
                    tdr.comprobarFinFichero();
                    System.out.println(trad);
                }
                catch (FileNotFoundException e) {
                    System.out.println("Error, fichero no encontrado: " + args[0]);
                }
            }
        else System.out.println("Error, uso: java plp3 <nomfichero>");
    }

}
