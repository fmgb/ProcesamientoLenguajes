package Practica0;

import java.io.RandomAccessFile;
import java.util.Stack;
import java.io.EOFException;
import java.io.IOException;
import Practica0.Operator;

public class plp0
{
     public static void main(String args[])
    {
        try{
            plp0 program = new plp0(args[0]);
        
            char c = 'a';
            int i = 0;
            while(c != ' ')
                {
                    i++;
                    c = program.readCharacter();
                   
                
                    program.translate(c);
                
                    System.out.println("Leo el caracter: " + i + "   " + c);
                }
        }catch(IOException e)
            {
                System.out.println("No se ha encontrado el fichero");
            
            }
    }
    private String _pathFile;
    private Stack<String> _digitalStack; //Stack A
    private Stack<Operator> _operatorStack; //Stack B
    private RandomAccessFile _file;
    public plp0(String pathFile) throws IOException
    {
        _pathFile = pathFile;
        _file = new RandomAccessFile(_pathFile,"r");
        _digitalStack = new Stack<String>();
        _operatorStack = new Stack<Operator>();
    }
    
    public plp0() throws IOException
    {
        _pathFile = "";
        _digitalStack = new Stack<String>();
        _operatorStack = new Stack<Operator>();
        _file = new RandomAccessFile(_pathFile, "r");
    }

    public String getPathFile()
    {
        return _pathFile;
    }

    public void setPathFile(String pathFile)
    {
        _pathFile = pathFile;
    }
    
    public char readCharacter()
    {
        char currentChar;
        try {
            currentChar = (char) _file.readByte();
            return currentChar;
        }
        catch (EOFException e)
            {
               
            }
        catch (IOException e)
            {
                System.out.println("Error " + e.getMessage());
                e.printStackTrace();       
            }
        return ' ';
    }

    public String translate(char c)
    {
        if(c != '\n'){
            if(Character.isDigit(c))
                {
                    _digitalStack.push(""+c);
                }
            else
                {
                    try{
                        Operator X = translateOperator(c);
                
                        //           System.out.println("HOLA2");
                        if(X == Operator.IGU)
                            {
                                


                                while(!_operatorStack.empty())
                                    {
                                        System.out.println("PILA A: " + _digitalStack);
                                
                                        System.out.println("PILA B: " + _operatorStack);
                                        String a2 = _digitalStack.pop();
                                        String a1 = _digitalStack.pop();
                                        Operator Y = _operatorStack.pop();
                                        if((X == Operator.SUM && Y == Operator.SUM) || (X == Operator.SUM && Y == Operator.RES) || (X == Operator.RES && Y == Operator.SUM) || (X == Operator.RES && Y == Operator.RES))
                                            {
                                                System.out.println("HAAAAABBBBBBBBBBBBBBB");

                                                Operator aux = Y;
                                                Y = X;
                                                X = aux;
                                                }
                                        switch(Y)
                                            {
                                            case SUM:
                                                _digitalStack.push("suma(" + a1 + "," + a2 + ")");
                                                break;
                                            case RES:
                                                 _digitalStack.push("resta(" + a1 + "," + a2 + ")");
                                                 break;
                                            case MUL:
                                                 _digitalStack.push("prod(" + a1 + "," + a2 + ")");
                                                 break;
                                            case DIV:
                                                 _digitalStack.push("div(" + a1 + "," + a2 + ")");
                                                 break;
                                            }
                                    }
                                System.out.println("PILA FINAL A: " + _digitalStack);

                                String resultado = "";
                                while(!_digitalStack.empty())
                                    {
                                        resultado += _digitalStack.pop();
                                    }
                                System.out.println("Resultado Final: " + resultado);
                                return resultado;
                            }
                        else {
                            if(_operatorStack.empty())
                                {
                                    _operatorStack.push(X);
                                }
                            else
                                {
                                    System.out.println("ESTADO ANTES DE HACER NADA");
                                    System.out.println("PILA A: " + _digitalStack);
                                    System.out.println("PILA B: " + _operatorStack);

                                    Operator Y = _operatorStack.pop();
                                    if((X == Operator.MUL || X == Operator.DIV) && (Y == Operator.SUM || Y == Operator.RES))
                                        {
                                            System.out.println("El Operador X es : " + X + " y el Y es: " + Y );
                                            //Como he sacado antes el operador que estaba,
                                            //ahora lo vuelvo a introducir de nuevo.
                                            _operatorStack.push(Y);
                                            _operatorStack.push(X);
                                        }
                                    else
                                        {
                                            boolean cambiado = false;
                                            String a2 = _digitalStack.pop();
                                            String a1 = _digitalStack.pop();
                                            

                                            /*          if((X == Operator.SUM && Y == Operator.SUM) || (X == Operator.SUM && Y == Operator.RES) || (X == Operator.RES && Y == Operator.SUM) || (X == Operator.RES && Y == Operator.RES))
                                                {
                                                    System.out.println("HAAAAA");

                                                    Operator aux = Y;
                                                    Y = X;
                                                    X = aux;
                                                    cambiado = true;
                                                    }*/
                                            switch(Y)
                                                {
                                                case SUM:
                                                _digitalStack.push("suma(" + a1 + "," + a2 + ")");
                                                break;
                                                case RES:
                                                _digitalStack.push("resta(" + a1 + "," + a2 + ")");
                                                 break;
                                                case MUL:
                                                _digitalStack.push("prod(" + a1 + "," + a2 + ")");
                                                 break;
                                                case DIV:
                                                _digitalStack.push("div(" + a1 + "," + a2 + ")");
                                                 break;
                                                }
                                            /*if(cambiado)
                                                {
                                                    Operator aux = Y;
                                                    Y = X;
                                                    X = aux;
                                                    cambiado = false;
                                                    }*/
                                             _operatorStack.push(X);
                                             System.out.println("ESTADO DESPUES DE HACER NADA");
                                             System.out.println("PILA A: " + _digitalStack);
                                             System.out.println("PILA B: " + _operatorStack);
                                        }
                                }
                        }
                    }catch(Exception e)
                        {
                            System.out.println("Error en la lectura del operador. Operador " + c + " inválido");
                        }
                }
        }
        return "";
        }

    private Operator translateOperator(char c) throws Exception
    {
        switch(c)
            {
            case '+': return Operator.SUM;

            case '-': return Operator.RES;

            case '*': return Operator.MUL;

            case '/': return Operator.DIV;

            case '=': return Operator.IGU;

            default:
                throw new Exception();
            }
    }
}
