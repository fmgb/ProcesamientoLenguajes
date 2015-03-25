/**
   Author: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 2
   Licencia: GPLv3
   Creado el: 14/03/2015

 */
class Token {
    public int fila;
    public int columna;

    public String lexema;

    public int tipo;

    public static final int EOF = 22, PARI = 0, PARD = 1, MULOP = 2, ADDOP = 3, PYC = 4,
        DOSP = 5,  ASIG = 6, VAR = 7, REAL = 8, INTEGER = 9, PROGRAM = 10, BEGIN = 11,
        END = 12, IF = 13, THEN = 14, ENDIF = 15, WHILE = 16, DO = 17,
        WRITELN = 18, NENTERO = 19, ID = 20, NREAL = 21;
    //,RELOP = 30, FUNCTION = 31,ELSE = 32,COMA = 33;

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
               case "if":
                tipo = IF;
                break;
            case "then":
                tipo = THEN;
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
              case PYC:
                return ";";
            case DOSP:
                return ":";
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
            case IF:
                return "\'if\'";
            case THEN:
                return "\'then\'";
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
