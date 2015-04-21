/**
   Author: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 3
   Licencia: GPLv3
   Creado el: 25/03/2015

 */

class Token {
    public int fila;
    public int columna;

    public String lexema;

    public int tipo;

    public static final int EOF = -1, PARI = 0, PARD = 1, MULOP = 2, ADDOP = 3, RELOP = 4, PYC = 5,
        DOSP = 6, COMA = 7, ASIG = 8, VAR = 9, REAL = 10, INTEGER = 11, PROGRAM = 12, BEGIN = 13,
        END = 14, FUNCTION = 15, IF = 16, THEN = 17, ELSE = 18, ENDIF = 19, WHILE = 20, DO = 21,
        WRITELN = 22, NENTERO = 23, ID = 24, NREAL = 25;

    public Token()
    {
        fila = 0;
        columna = 0;
        lexema = "";
        tipo = -2;
    }
    
    public Token(int fila,int columna)
    {
        fila = fila;
        columna = columna;
    }
    
    public void errorLexico(int fila, int columna, char caracter)
    {
        System.err.println("Error lexico (" + fila + "," + columna + "): caracter '" + caracter + "' incorrecto");
        System.exit(-1);
    }

    public void errorFinalFichero()
    {
        System.err.println("Error lexico: fin de fichero inesperado");
         System.exit(-1);
    }

    public void anyadirLetra(char c)
    {
        lexema += c;
    }

    public void borrarLexema()
    {
        lexema = "";
    }
    
    public void borrarUltimaLetra()
    {
        lexema.substring(0,lexema.length()-2);
    }
    
    public void setTipo(int tipo)
    {
       
        this.tipo = tipo;
    }

    public void setFila(int fila)
    {
        this.fila = fila;
    }

    public void setColumna(int columna)
    {
        this.columna = columna;
    }

    public void isReservedWord()
    {
        switch(lexema)
            {
            case "var":
                tipo = VAR;
                break;
            case "real":
                tipo = REAL;
                break;
            case "integer":
                tipo = INTEGER;
                break;
            case "program":
                tipo = PROGRAM;
                break;
            case "begin":
                tipo = BEGIN;
                break;
            case "end":
                tipo = END;
                break;
            case "function":
                tipo = FUNCTION;
                break;
            case "if":
                tipo = IF;
                break;
            case "then":
                tipo = THEN;
                break;
            case "else":
                tipo = ELSE;
                break;
            case "endif":
                tipo = ENDIF;
                break;
            case "while":
                tipo = WHILE;
                break;
            case "do":
                tipo = DO;
                break;
            case "writeln":
                tipo = WRITELN;
                break;
            case "div":
                tipo = MULOP;
                break;
                
            default:
                tipo = ID;
                break;
            }
    }


    public String getTipoString(int tipoEsperado)
    {
        switch(tipoEsperado)
            {
            case PARI:
                return "(";
            case PARD:
                return ")";
            case MULOP:
                return "* / div";
            case ADDOP:
                return "+ -";
            case RELOP:
                return "< > <= >= = <>";
            case PYC:
                return ";";
            case DOSP:
                return ":";
            case COMA:
                return ",";
            case ASIG:
                return ":=";
            case VAR:
                return "\'var\'";
            case REAL:
                return "\'real\'";
            case INTEGER:
                return "\'integer\'";
            case PROGRAM:
                return "\'program\'";
            case BEGIN:
                return "\'begin\'";
            case END:
                return "\'end\'";
            case FUNCTION:
                return "\'function\'";
            case IF:
                return "\'if\'";
            case THEN:
                return "\'then\'";
            case ELSE:
                return "\'else\'";
            case ENDIF:
                return "\'endif\'";
            case WHILE:
                return "\'while\'";
            case DO:
                return "\'do\'";
            case WRITELN:
                return "\'writeln\'";
            case NENTERO:
                return "numero entero";
            case ID:
                return "identificador";
            case NREAL:
                return "numero real";
            case EOF:
                return "fin de fichero";
            default:
                return "ERROR";
            }
    }
    
    @Override
    public String toString() {
        return "(" + fila + "," + columna + "): " + lexema + " es de tipo " + tipo;
        
    }
}
//  LocalWords:  lexico div var
