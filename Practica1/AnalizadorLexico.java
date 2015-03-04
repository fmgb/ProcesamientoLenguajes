import java.io.RandomAccessFile;
import java.util.Stack;
import java.io.EOFException;
import java.io.IOException;
import java.io.FileNotFoundException;

class AnalizadorLexico
{
    private String _filePath;
    private RandomAccessFile _file;
    private Stack<Character> _stack;
    private int fila;
    private int columna;
    
      public static final int EOF = -1, PARI = 0, PARD = 1, MULOP = 2, ADDOP = 3, RELOP = 4, PYC = 5,
        DOSP = 6, COMA = 7, ASIG = 8, VAR = 9, REAL = 10, INTEGER = 11, PROGRAM = 12, BEGIN = 13,
        END = 14, FUNCTION = 15, IF = 16, THEN = 17, ELSE = 18, ENDIF = 19, WHILE = 20, DO = 21,
        WRITELN = 22, NENTERO = 23, ID = 24, NREAL = 25;

    public AnalizadorLexico() throws FileNotFoundException
    {
        _filePath = "./Fichero.txt";
        _file = new RandomAccessFile(_filePath, "r");
        _stack = new Stack<Character>();
        fila = 0;
        columna = 0;
    }

    public AnalizadorLexico(String filePath) throws FileNotFoundException
    {
        _filePath = filePath;
        _file = new RandomAccessFile(_filePath, "r");
        _stack = new Stack<Character>();
        fila = 0;
        columna = 0;
    }

    public AnalizadorLexico(RandomAccessFile file) 
    {
        _file = file;
        _stack = new Stack<Character>();
        fila = 1;
        columna = 0;
    }
    
    public void analizar()
    {
        
    }

    public Token siguienteToken()
    {
        Token tok = new Token();
        boolean token = false;
        int estado = 0;
        boolean leyendoComentario = false;
        
        while(!token)
            {
                char c =' ';
                if(_stack.isEmpty())
                    {
                        try{
                            
                        c = readCharacter();
                        ++columna;
                        }
                        catch(EOFException e)
                            {
                                if(leyendoComentario)
                                    {
                                        tok.errorFinalFichero();
                                    }
                                tok.setTipo(EOF);
                                token = true;
                            }
                        catch(IOException e)
                            {
                                System.out.println("Fallo en la lectura del fichero");
                            }
                    }
                else
                    {
                        //    System.out.println("Quito en la pila");
                        Character c1 = (Character)_stack.pop();
                        c = c1.charValue();
                          
                    }
                switch(estado)
                    {
                        case 0:
                            tok.setFila(fila);
                            tok.setColumna(columna);
                            if(c =='\n')
                                {
                                    columna = 0;
                                    ++fila;
                                }
                            else if(c == '\t' || c == ' ')
                                {
                                    
                                }
                            else if(Character.isDigit(c))
                                {
                                    estado = 1;
                                    tok.anyadirLetra(c);
                                }
                            else if(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z')
                                {
                                    estado = 5;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == ':')
                                {
                                    estado = 7;
                                    tok.anyadirLetra(c);
                                }
                        
                            else if(c == ',')
                                {
                                    estado =  10;
                                    tok.anyadirLetra(c);
                                }
                        
                            else if(c == ';')
                                {        
                                    estado = 11;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '<')
                                {
                                    estado = 12;
                                    tok.anyadirLetra(c);
                                }
                        
                            else if(c == '>')
                                {
                                    estado = 13;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '=')
                                {
                                    estado = 14;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '*')
                                {
                                    estado = 15;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '/')
                                {
                                    estado = 16;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '+')
                                {
                                    estado = 17;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '-')
                                {
                                    estado = 18;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '(')
                                {
                                    estado = 19;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == ')')
                                {
                                    estado = 20;
                                    tok.anyadirLetra(c);
                                }
                            
                            else
                                {
                                    //                                   System.out.println("Fila: " + fila);
                                    //                                    System.out.println("Columna: " + columna);
                                    tok.errorLexico(fila,columna, c);
                                }                            
                            break;
                        case 1:
                            if(Character.isDigit(c))
                                {
                                    estado = 1;
                                    tok.anyadirLetra(c);
                                }
                            else if(c == '.')
                                {//No le metemos la letra porque no sabemos si
                                 //es real o entero todavía.
                                    estado = 2;
                                    //tok.anyadirLetra(c);
                                }
                            else
                                {
                                    _stack.push(c);
                                    estado = 4;
                                }
                            break;
                        case 2:
                            if(Character.isDigit(c))
                                {
                                    estado = 3;
                                    //Aseguramos que es un real.
                                    tok.anyadirLetra('.');
                                    tok.anyadirLetra(c);
                                }
                            else
                                {
                                    _stack.push(c);
                                    columna--;
                                    _stack.push('.');
                                    estado = 26;
                                }
                            break;
                        case 3:
                            if(Character.isDigit(c))
                                {
                                tok.anyadirLetra(c);
                                estado = 3;
                                }
                            else
                                {
                                    _stack.push(c);
                                    estado = 27;
                                }
                            break;
                        case 4:
                            estado = 0;
                            tok.setTipo(NENTERO);
                            token = true;
                            //Volvemos a colocar el caracter c en la pila para
                            //que lo lea cuando vuelva a por el siguiente token.
                            _stack.push(c);
                            break;
                        case 5:
                            if(Character.isDigit(c) || c >= 'A' && c <= 'Z' || c>= 'a' && c<= 'z')
                                {
                                    tok.anyadirLetra(c);
                                    estado = 5;
                                }
                            else
                                {
                                    estado = 6;
                                    _stack.push(c);
                                }
                            break;
                        case 6:
                            tok.isReservedWord();
                            estado = 0;
                            token = true;
                            _stack.push(c);
                            break;
                        case 7:
                            if(c == '=')
                                {
                                    estado = 8;
                                    tok.anyadirLetra(c);
                                }
                            else
                                {
                                    estado = 9;
                                    _stack.push(c);
                                    
                                }
                            break;
                        case 8:
                            tok.anyadirLetra(c);
                            tok.setTipo(ASIG);
                            token = true;
                            estado = 0;
                            break;
                        case 9:
                            tok.setTipo(DOSP);
                            _stack.push(c);
                            token = true;
                            estado = 0;
                            break;
                        case 10:
                            tok.setTipo(COMA);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 11:
                            tok.setTipo(PYC);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 12:
                            if(c == '=')
                                {
                                    tok.anyadirLetra(c);
                                    estado = 21;
                                }
                            else if(c == '>')
                                {
                                    tok.anyadirLetra(c);
                                    estado = 22;
                                }
                            else
                                {
                                    estado = 23;
                                    _stack.push(c);
                                }
                            break;
                        case 13:
                            if(c == '=')
                                {
                                    tok.anyadirLetra(c);
                                    estado = 24;
                                }
                            else
                                {
                                    estado = 25;
                                    _stack.push(c);
                                    
                                }
                            break;
                        case 14:
                            tok.setTipo(RELOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 15:
                            tok.setTipo(MULOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 16:
                            tok.setTipo(MULOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 17:
                            tok.setTipo(ADDOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 18:
                            tok.setTipo(ADDOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 19:
                            if(c == '*')
                                {
                                    leyendoComentario = true;
                                    estado = 28;
                                    tok.anyadirLetra(c);
                                }
                            else
                                {
                                    tok.setTipo(PARI);
                                    token = true;
                                    _stack.push(c);
                                    estado = 0;
                                }
                            break;
                        case 20:
                            tok.setTipo(PARD);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 21:
                            tok.setTipo(RELOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 22:
                            tok.setTipo(RELOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 23:
                            tok.setTipo(RELOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 24:
                            tok.setTipo(RELOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 25:
                            tok.setTipo(RELOP);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 26:
                            tok.setTipo(NENTERO);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                        case 27:
                            tok.setTipo(NREAL);
                            token = true;
                            _stack.push(c);
                            estado = 0;
                            break;
                    case 28:
                        if(c == '\n')
                            {
                                columna = 0;
                                ++fila;
                            }
                            else if( c == '*')
                            {
                                estado = 29;
   
                                tok.anyadirLetra(c);
                            }
                        else
                            {
                                estado = 28;
                                tok.anyadirLetra(c);
                            }
                        break;
                    case 29:
                        if(c == ')')
                            {
                                tok.anyadirLetra(c);
                                leyendoComentario = false;
                                tok.borrarLexema();
                                estado = 0;
                            }
                         else
                            {
                                estado = 28;
                                _stack.push(c);
                                
                                tok.anyadirLetra(c);
                            }
                        break;
                    default:
                            break;
                            
                            
                            
                        }                
            }
        return tok;
        
    }
    

    private char readCharacter() throws IOException
    {
        char currentChar;
        
        currentChar = (char)_file.readByte();
        return currentChar;
    }
}
