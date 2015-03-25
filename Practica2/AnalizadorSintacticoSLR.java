/**
   Author: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 2
   Licencia: GPLv3
   Creado el: 14/03/2015

 */
import java.util.Arrays;
import java.io.IOException;
import java.util.Stack;

class Accion
{
    public static final char DESPLAZAR = 'd';
    public static final char REDUCIR = 'r';
    public static final char ACEPTAR = 'a';
    
    public char _accion;
    public int _estado;

    public Accion(char accion, int estado)
    {
        this._accion = accion;
        this._estado = estado;
    }

    public Accion()
    {
        _accion = 'A';
        _estado = 0;
    }

    public void setAccion(char accion,int estado)
    {
        this._accion = accion;
        this._estado = estado;     
    }
}

class AnalizadorSintacticoSLR
{

    public static final int numEstados = 54;
    public static final int numTokensDif = 23;
    private AnalizadorLexico al;
    private Token token;
    private StringBuilder reglasAplicadas;
    private boolean flag;
    private Stack<Integer> _stack;
    private Accion [][] _acciones;
    private int [][] _ir_A;
    
    
    public AnalizadorSintacticoSLR()
    {
        try{
            al = new AnalizadorLexico();
            _stack = new Stack<Integer>();
            _acciones = new Accion[numEstados][numTokensDif];
            _ir_A = new int[numEstados][numTokensDif];
            
            inicializarTabla();
        }
        catch(IOException e)
            {
                System.err.println("Error léxico: Error en la apertura del fichero.");
                
            }
    }

    public AnalizadorSintacticoSLR(AnalizadorLexico al)
    {
        this.al = al;
        flag = true;
        reglasAplicadas = new StringBuilder();
        _stack = new Stack<Integer>();
        _acciones = new Accion[numEstados][numTokensDif];
        _ir_A = new int[numEstados][numTokensDif];
        inicializarTabla();
    }

    public final void errorSintaxis(int estadoActual)
    {
        if(token.tipo == Token.EOF)
            {
                System.err.print("Error sintactico: encontrado el final del fichero, esperaba "); 
            }
        else 
            System.err.print("Error sintactico (" +token.fila + "," + token.columna + "): encontrado \'" + token.lexema + "\', esperaba ");
        
        for(int i = 0; i<numTokensDif;++i)
            {
                if(_acciones[estadoActual][i] != null)
                    {
                        System.err.print(token.getTipoString(i) + " ");
                    }
            }
        System.err.println();
        System.exit(-1);
    }

    public final void errorFinalFichero(int estadoActual)
    {
        errorSintaxis(estadoActual);
        System.exit(-1);     
    }

    public final void comprobarFinFichero()
    {
        if(token.tipo != Token.EOF)
            {
                errorFinalFichero(Token.EOF);
            }
        System.out.println(reglasAplicadas);
    }
    
    public void analizar()
    {
        _stack.push(0);
        Stack<Integer> solucion = new Stack<Integer>();
        int estadoActual = 0;
        boolean finDeAnalisis = false;
        token = al.siguienteToken();

        while(true)
            {
                estadoActual = _stack.peek();

                if(_acciones[estadoActual][token.tipo] == null)
                    {
                        errorSintaxis(estadoActual);
                    }
                else if(_acciones[estadoActual][token.tipo]._accion == Accion.DESPLAZAR)
                    {
                        _stack.push(_acciones[estadoActual][token.tipo]._estado);
                        token = al.siguienteToken();
                    }
                else if (_acciones[estadoActual][token.tipo]._accion == Accion.REDUCIR)
                    {
                        int n = Longitud_Parte_Derecha(_acciones[estadoActual][token.tipo]._estado);
                        int p = 0;

                        for(int i = 1; i <= n; ++i)
                            {
                                _stack.pop();
                            }
                        solucion.push(_acciones[estadoActual][token.tipo]._estado);        
                        int A = ParteIzquierda(_acciones[estadoActual][token.tipo]._estado);
                        p = _stack.peek();

                        _stack.push(_ir_A[p][A]);
                    }
                else if (_acciones[estadoActual][token.tipo]._accion == Accion.ACEPTAR)
                    {
                        break;
                    }
                else
                    errorSintaxis(estadoActual);
            }

        while(!solucion.empty())
            {
                System.out.print(solucion.pop() + " ");
            }
        System.out.println();
        
    }
    
    public int ParteIzquierda(int regla)
    {
        switch(regla)
            {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 3;
            case 6:
                return 3;
            case 7:
                return 4;
            case 8:
                return 5;
            case 9:
                return 5;
            case 10:
                return 6;
            case 11:
                return 7;
            case 12:
                return 7;
            case 13:
                return 8;
            case 14:
                return 8;
            case 15:
                return 8;
            case 16:
                return 8;
            case 17:
                return 8;
            case 18:
                return 9;
            case 19:
                return 9;
            case 20:
                return 10;
            case 21:
                return 10;
            case 22:
                return 11;
            case 23:
                return 11;
            case 24:
                return 11;
            case 25:
                return 11; 
            default :
                return 0;
                
            }
    }



    public int Longitud_Parte_Derecha(int regla)
    {
        switch(regla)
            {
            case 1:
                return 5;
            case 2:
                return 2;
            case 3:
                return 1;
            case 4:
                return 2;
            case 5:
                return 2;
            case 6:
                return 1;
            case 7:
                return 4;
            case 8:
                return 1;
            case 9:
                return 1;
            case 10:
                return 3;
            case 11:
                return 3;
            case 12:
                return 1;
            case 13:
                return 1;
            case 14:
                return 3;
            case 15:
                return 5;
            case 16:
                return 4;
            case 17:
                return 4;
            case 18:
                return 3;
            case 19:
                return 1;
            case 20:
                return 3;
            case 21:
                return 1;
            case 22:
                return 1;
            case 23:
                return 1;
            case 24:
                return 1;
            case 25:
                return 3;
            default:
                return 0;
                
            }
    }
    
    public void inicializarTabla()
    {
        _acciones[0][10] = new Accion('d',2);
        _ir_A[0][0] = 1;

        _acciones[1][22] = new Accion('a',0);

        _acciones[2][20] = new Accion('d',3);

        _acciones[3][4] = new Accion('d',4);

        _acciones[4][7] = new Accion('d',7);
        _ir_A[4][1] = 5;
        _ir_A[4][2] = 6;

        _acciones[5][7] = new Accion('d',7);
        _acciones[5][11] = new Accion('d',10);
        _ir_A[5][2] = 9;
        _ir_A[5][6] = 8;

        _acciones[6][7] = new Accion('r',3);
        _acciones[6][11] = new Accion('r',3);

        _acciones[7][20] = new Accion('d',13);
        _ir_A[7][3] = 11;
        _ir_A[7][4] = 12;

        _acciones[8][22] = new Accion('r',1);

        _acciones[9][7] = new Accion('r',2);
        _acciones[9][11] = new Accion('r',2);

        _acciones[10][11] = new Accion('d',10);
        _acciones[10][13] = new Accion('d',18);
        _acciones[10][16] = new Accion('d',19);
        _acciones[10][18] = new Accion('d',20);
        _acciones[10][20] = new Accion('d',17);
        _ir_A[10][6] = 16;
        _ir_A[10][7] = 14;
        _ir_A[10][8] = 15;

        _acciones[11][7] = new Accion('r',4);
        _acciones[11][11] = new Accion('r',4);
        _acciones[11][20] = new Accion('d',13);
        _ir_A[11][4] = 21;

        _acciones[12][7] = new Accion('r',6);
        _acciones[12][11] = new Accion('r',6);
        _acciones[12][20] = new Accion('r',6);

        _acciones[13][5] = new Accion('d',22);

        _acciones[14][4] = new Accion('d',24);
        _acciones[14][12] = new Accion('d',23);

        _acciones[15][4] = new Accion('r',12);
        _acciones[15][12] = new Accion('r',12);

        _acciones[16][4] = new Accion('r',13);
        _acciones[16][12] = new Accion('r',13);
        _acciones[16][15] = new Accion('r',13);

        _acciones[17][6] = new Accion('d',25);

        _acciones[18][0] = new Accion('d',32);
        _acciones[18][19] = new Accion('d',30);
        _acciones[18][20] = new Accion('d',29);
        _acciones[18][21] = new Accion('d',31);
        _ir_A[18][9] = 26;
        _ir_A[18][10] = 27;
        _ir_A[18][11] = 28;

        _acciones[19][0] = new Accion('d',32);
        _acciones[19][19] = new Accion('d',30);
        _acciones[19][20] = new Accion('d',29);
        _acciones[19][21] = new Accion('d',31);
        _ir_A[19][9] = 33;
        _ir_A[19][10] = 27;
        _ir_A[19][11] = 28;

        _acciones[20][0] = new Accion('d',34);

        _acciones[21][7] = new Accion('r',5);
        _acciones[21][11] = new Accion('r',5);
        _acciones[21][20] = new Accion('r',5);

        _acciones[22][8] = new Accion('d',37);
        _acciones[22][9] = new Accion('d',36);
        _ir_A[22][5] = 35;

        _acciones[23][4] = new Accion('r',10);
        _acciones[23][12] = new Accion('r',10);
        _acciones[23][15] = new Accion('r',10);
        _acciones[23][22] = new Accion('r',10);

        _acciones[24][11] = new Accion('d',10);
        _acciones[24][13] = new Accion('d',18);
        _acciones[24][16] = new Accion('d',19);
        _acciones[24][18] = new Accion('d',20);
        _acciones[24][20] = new Accion('d',17);
        _ir_A[24][6] = 16;
        _ir_A[24][8] = 38;

        _acciones[25][0] = new Accion('d',32);
        _acciones[25][19] = new Accion('d',30);
        _acciones[25][20] = new Accion('d',29);
        _acciones[25][21] = new Accion('d',31);
        _ir_A[25][9] = 39;
        _ir_A[25][10] = 27;
        _ir_A[25][11] = 28;

        _acciones[26][3] = new Accion('d',41);
        _acciones[26][14] = new Accion('d',40);

        _acciones[27][1] = new Accion('r',19);
        _acciones[27][2] = new Accion('d',42);
        _acciones[27][3] = new Accion('r',19);
        _acciones[27][4] = new Accion('r',19);
        _acciones[27][12] = new Accion('r',19);
        _acciones[27][14] = new Accion('r',19);
        _acciones[27][15] = new Accion('r',19);
        _acciones[27][17] = new Accion('r',19);

        _acciones[28][1] = new Accion('r',21);
        _acciones[28][2] = new Accion('r',21);
        _acciones[28][3] = new Accion('r',21);
        _acciones[28][4] = new Accion('r',21);
        _acciones[28][12] = new Accion('r',21);
        _acciones[28][14] = new Accion('r',21);
        _acciones[28][15] = new Accion('r',21);
        _acciones[28][17] = new Accion('r',21);

        _acciones[29][1] = new Accion('r',22);
        _acciones[29][2] = new Accion('r',22);
        _acciones[29][3] = new Accion('r',22);
        _acciones[29][4] = new Accion('r',22);
        _acciones[29][12] = new Accion('r',22);
        _acciones[29][14] = new Accion('r',22);
        _acciones[29][15] = new Accion('r',22);
        _acciones[29][17] = new Accion('r',22);

        _acciones[30][1] = new Accion('r',23);
        _acciones[30][2] = new Accion('r',23);
        _acciones[30][3] = new Accion('r',23);
        _acciones[30][4] = new Accion('r',23);
        _acciones[30][12] = new Accion('r',23);
        _acciones[30][14] = new Accion('r',23);
        _acciones[30][15] = new Accion('r',23);
        _acciones[30][17] = new Accion('r',23);

        _acciones[31][1] = new Accion('r',24);
        _acciones[31][2] = new Accion('r',24);
        _acciones[31][3] = new Accion('r',24);
        _acciones[31][4] = new Accion('r',24);
        _acciones[31][12] = new Accion('r',24);
        _acciones[31][14] = new Accion('r',24);
        _acciones[31][15] = new Accion('r',24);
        _acciones[31][17] = new Accion('r',24);

        _acciones[32][0] = new Accion('d',32);
        _acciones[32][19] = new Accion('d',30);
        _acciones[32][20] = new Accion('d',29);
        _acciones[32][21] = new Accion('d',31);
        _ir_A[32][9] = 43;
        _ir_A[32][10] = 27;
        _ir_A[32][11] = 28;

        _acciones[33][3] = new Accion('d',41);
        _acciones[33][17] = new Accion('d',44);

        _acciones[34][0] = new Accion('d',32);
        _acciones[34][19] = new Accion('d',30);
        _acciones[34][20] = new Accion('d',29);
        _acciones[34][21] = new Accion('d',31);
        _ir_A[34][9] = 45;
        _ir_A[34][10] = 27;
        _ir_A[34][11] = 28;

        _acciones[35][4] = new Accion('d',46);

        _acciones[36][4] = new Accion('r',8);

        _acciones[37][4] = new Accion('r',9);

        _acciones[38][4] = new Accion('r',11);
        _acciones[38][12] = new Accion('r',11);

        _acciones[39][3] = new Accion('d',41);
        _acciones[39][4] = new Accion('r',14);
        _acciones[39][12] = new Accion('r',14);
        _acciones[39][15] = new Accion('r',14);

        _acciones[40][11] = new Accion('d',10);
        _acciones[40][13] = new Accion('d',18);
        _acciones[40][16] = new Accion('d',19);
        _acciones[40][18] = new Accion('d',20);
        _acciones[40][20] = new Accion('d',17);
        _ir_A[40][6] = 16;
        _ir_A[40][8] = 47;

        _acciones[41][0] = new Accion('d',32);
        _acciones[41][19] = new Accion('d',30);
        _acciones[41][20] = new Accion('d',29);
        _acciones[41][21] = new Accion('d',31);
        _ir_A[41][10] = 48;
        _ir_A[41][11] = 28;

        _acciones[42][0] = new Accion('d',32);
        _acciones[42][19] = new Accion('d',30);
        _acciones[42][20] = new Accion('d',29);
        _acciones[42][21] = new Accion('d',31);
        _ir_A[42][11] = 49;

        _acciones[43][1] = new Accion('d',50);
        _acciones[43][3] = new Accion('d',41);

        _acciones[44][11] = new Accion('d',10);
        _acciones[44][13] = new Accion('d',18);
        _acciones[44][16] = new Accion('d',19);
        _acciones[44][18] = new Accion('d',20);
        _acciones[44][20] = new Accion('d',17);
        _ir_A[44][6] = 16;
        _ir_A[44][8] = 51;

        _acciones[45][1] = new Accion('d',52);
        _acciones[45][3] = new Accion('d',41);

        _acciones[46][7] = new Accion('r',7);
        _acciones[46][11] = new Accion('r',7);
        _acciones[46][20] = new Accion('r',7);

        _acciones[47][15] = new Accion('d',53);

        _acciones[48][1] = new Accion('r',18);
        _acciones[48][2] = new Accion('d',42);
        _acciones[48][3] = new Accion('r',18);
        _acciones[48][4] = new Accion('r',18);
        _acciones[48][12] = new Accion('r',18);
        _acciones[48][14] = new Accion('r',18);
        _acciones[48][15] = new Accion('r',18);
        _acciones[48][17] = new Accion('r',18);

        _acciones[49][1] = new Accion('r',20);
        _acciones[49][2] = new Accion('r',20);
        _acciones[49][3] = new Accion('r',20);
        _acciones[49][4] = new Accion('r',20);
        _acciones[49][12] = new Accion('r',20);
        _acciones[49][14] = new Accion('r',20);
        _acciones[49][15] = new Accion('r',20);
        _acciones[49][17] = new Accion('r',20);

        _acciones[50][1] = new Accion('r',25);
        _acciones[50][2] = new Accion('r',25);
        _acciones[50][3] = new Accion('r',25);
        _acciones[50][4] = new Accion('r',25);
        _acciones[50][12] = new Accion('r',25);
        _acciones[50][14] = new Accion('r',25);
        _acciones[50][15] = new Accion('r',25);
        _acciones[50][17] = new Accion('r',25);

        _acciones[51][4] = new Accion('r',16);
        _acciones[51][12] = new Accion('r',16);
        _acciones[51][15] = new Accion('r',16);

        _acciones[52][4] = new Accion('r',17);
        _acciones[52][12] = new Accion('r',17);
        _acciones[52][15] = new Accion('r',17);

        _acciones[53][4] = new Accion('r',15);
        _acciones[53][12] = new Accion('r',15);
        _acciones[53][15] = new Accion('r',15);
    }
}
