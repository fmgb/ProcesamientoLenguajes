package 1Practica;


class Token {
    public int fila;
    public int columna;

    public String lexema;

    public int tipo;

    public static final int PARI = 1, PARD = 2, MULOP = 3, ADDOP = 4, RELOP = 5, PYC = 6,
        DOSP = 7, COMA = 8, ASIG = 9, VAR = 10, REAL = 11, INTEGER = 12, PROGRAM = 13, BEGIN = 14,
        END = 15, FUNCTION = 16, IF = 17, THEN = 18, ELSE = 19, ENDIF = 20, WHILE = 21, DO = 22,
        WRITELN = 23, NENTERO = 24, ID = 25, NREAL = 26;

    public Token()
    {

    }
    
    private short errorLexico(int fila, int columna, char caracter)
    {
        System.out.println("Error lexico (" + fila "," + columna "): caracter '" + caracter + "' incorrecto");
        return -1;
    }

    private short errorFinalFichero()
    {
        System.out.println("Error lexico: fin de fichero inesperado");
        return -1;
    }
    
    public String toString() {
        switch(lexema)
            {
            case "(":
                return "(";
            case ")":
                return ")";
            case "*":
                return "*";
            case "/":
                return "/";
            case "div":
                return "div";
            case "+":
                return "+";
            case "-":
                return "-";
            case "<":
                return "<";
            case ">":
                return ">";
            case "<=":
                return "<=";
            case ">=":
                return ">=";
            case "=":
                return "=";
            case "<>":
                return "<>";
            case ";":
                return ";";
            case ":":
                return ":";
            case ",":
                return ",";
            case ":=":
                return ":=";
            case "var":
                return "var";
            case "real":
                return "real";
            case "integer":
                return "integer";
            case "program":
                return "program";
            case "begin":
                return "begin";
            case "end":
                return "end";
            case "function":
                return "function";
            case "if":
                return "if";
            case "then":
                return "then";
            case "else":
                return "else";
            case "endif":
                return "else";
            case "while":
                return "while";
            case "do":
                return "do";
            case "writeln":
                return "writeln";
            case "nentero":
                return "numero entero";
            case "id":
                return "identificador";
            case "nreal":
                return "numero real";
            default:
                return "ERROR";
            }
    }
}
//  LocalWords:  lexico div var
