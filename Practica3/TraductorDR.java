/**
   Author: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 3
   Licencia: GPLv3
   Creado el: 25/03/2015

 */

import java.util.Arrays;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;

class Atributos 
{
    public String trad;
    public String th;
    

    public Atributos()
    {
        trad = "";
        th = "";
    }

    public Atributos(String traduccion)
    {
        this.trad = traduccion;    
    }
    
    public Atributos(String traduccion, String heredado)
    {
        this.trad = traduccion;
        this.th = heredado;
        
    }
}

class TraductorDR {

    Token token;
    AnalizadorLexico lexico;
    StringBuilder reglasAplicadas;
    boolean flag;

    //Variable que se utiliza para saber el nombre de los ámbitos a los que
    //pertecene la variable guardada en la TS.
    ArrayList<String> ambitos;
    // Tabla de Símbolos. Cada posición determinará como un ámbito anidado. La
    // posición inicial, 0, será el ámbito del main.
    ArrayList<Hashtable<String,String>> ts;
    //    Hashtable<String,String> ts = new Hashtable<String,String>();

    public TraductorDR()
    {
        flag = true;
        reglasAplicadas = new StringBuilder();
        ambitos = new ArrayList<>();
        ts = new ArrayList<>();
    }

    public TraductorDR(AnalizadorLexico lexico)
    {
        this.lexico = lexico;
        flag = true;
        reglasAplicadas = new StringBuilder();
        ambitos = new ArrayList<>();
        ts = new ArrayList<>();
        //nuevoAmbito("main");
                
    }

    public final void errorSintaxis(int... tokEsperados)
    {
        Arrays.sort(tokEsperados);
        
        if(token.tipo == Token.EOF)
            {
                System.err.print("Error sintactico: encontrado fin de fichero, esperaba "); 
            }
        else 
            System.err.print("Error sintactico (" +token.fila + "," + token.columna + "): encontrado \'" + token.lexema + "\', esperaba ");
        for(int a : tokEsperados)
            System.err.print(token.getTipoString(a)+ " ");
        System.err.println();
        
        System.exit(-1);
        
    }

    public final void errorFinalFichero(int... tokEsperado)
    {
        errorSintaxis(tokEsperado);
        System.exit(-1);     
    }
    
    public final void emparejar(int tokEsperado)
    {
        if(token.tipo == tokEsperado)
            token = lexico.siguienteToken();
        else
            errorSintaxis(tokEsperado);
    }

    public final void comprobarFinFichero()
    {
        if(token.tipo != Token.EOF)
            {
                errorFinalFichero(Token.EOF);
                
            }
        System.out.println(reglasAplicadas);
    }

    //CHECK
    private final String ambitoVariable()
    {
        System.out.println("CHECK AMBITO VARIABLE");
        String ambitoAux = "";
        int i = ambitos.size()-1;
        while(i> 0)
            {
                ambitoAux = ambitos.get(i) + "_" +ambitoAux;
                
                //System.out.println("AMBITO AUX " + ambitoAux);
                i--;
            }
        if(ambitoAux == "")
            {
                ambitoAux = "main_";
                
            }
        System.out.println("Variable está en el ambito: " + ambitoAux);
        return ambitoAux;
    }

    private final String ambitoFuncion(String main)
    {
        System.out.println("CHECK AMBITO FUNCION");
        String ambitoAux = "";
        int i = ambitos.size()-1;
        while(i> 0)
            {
                ambitoAux = ambitos.get(i) + "_" + ambitoAux;
                
                System.out.println(ambitoAux);
                i--;
            }
        //PARCHEACO!!
        if(main == "")
            {
                ambitoAux = "main";
                
            }
        System.out.println("Función está en el ámbito: " + ambitoAux);
        return ambitoAux;
    }

    private final void anyadirTS(String id, String tipo)
    {
        //System.out.println("CHECK AnyadirTS");
        //        System.out.println(ts);
        // Las nuevas variables son las primeras, así que hay que realizar
        // alguna forma para que al salir de la TS, salgan en su orden correcto.
        if(tipo == "")
            ts.get(ambitos.size() -1).put(token.lexema,"VACIO");
        else
            ts.get(ambitos.size() -1).put(token.lexema,tipo);
        //       System.out.println(ts);
    }
    //CHECK
    private final void nuevoAmbito(String nombreAmbito)
    {
        // System.out.println("CHECK NUEVO AMBITO" + nombreAmbito);
        // System.out.println(ts.toString());
        ambitos.add(nombreAmbito);
        // Inicio con 90 de tamanyo para que la tabla Hash no machaque y joda
        // el orden de las variables. Dudo que Paco, se aburra tanto de anidar
        // 90 variables en un mismo ámbito.
        ts.add(new Hashtable<String,String>(90));
        // System.out.println(ts.toString());
    }

    private final void borrarAmbito()
    {
        System.out.println("CHECK BORRAR AMBITO");
        int numAmbitos = ambitos.size()-1;
        
        ts.remove(numAmbitos);
        ambitos.remove(numAmbitos);
        
        
    }
    // CHECK
    private final String arregloHerenciaDerecha(String tipo)
    {
        //System.out.println("CHECK ARREGLO\n" + ts);
        String resultado ="";// tipo+" ";
        int numAmbito = ambitos.size() - 1;
        Set<String> auxiliarid = ts.get(numAmbito).keySet();
        int i = 0;
        
        for(String s : auxiliarid)
            {
                if(ts.get(numAmbito).containsKey(s))
                    {
                        String variableTipo = ts.get(numAmbito).get(s);
                        if(variableTipo == "VACIO")
                            {
                                ts.get(numAmbito).put(s,tipo);
                            }
                    }
                //System.out.println(i);
                //Elimino la última coma
                if(i == 0)
                    resultado = ambitoVariable() +s + resultado;
                else
                    resultado = ambitoVariable() +s+ "," + resultado;
                i++;
            }
        //System.out.println("SALGO DE CHECK ARREGLO con: \n" + ts);
        return resultado;
    }

    private final String buscaTS(String id)
    {
        int numAmbitos = ambitos.size() -1;
        String resultado = "";
        
        System.out.println("CHECK BUSCA TS");
        while(numAmbitos >= 0)
            {
                if(ts.get(numAmbitos).containsKey(id))
                    {
                        resultado = ts.get(numAmbitos).get(id);
                        break;
                    }
                numAmbitos--;
            }

        return resultado;
    }
    
    public final String S() // 1 S -> program id pyc Vsp Bloque
    {
        //Resultado final de la traduccion.
        String trad = "";
        
        token = lexico.siguienteToken();
        if(token.tipo == Token.PROGRAM)
            {
                if(flag)
                    reglasAplicadas.append(" 1");
                emparejar(Token.PROGRAM);
                trad = "// program " + token.lexema + "\n";
                emparejar(Token.ID);
                trad += "";
                emparejar(Token.PYC);
                nuevoAmbito("main");
                
                trad += Vsp().trad + "\n";
                trad += Bloque().trad;
                
            }
        else
            errorSintaxis(Token.PROGRAM);
        return trad;
    }

    public final Atributos Vsp() // 2 Vsp -> Unsp Vsp_prima
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Vsp");
        if(token.tipo == Token.FUNCTION || token.tipo == Token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 2");
                resultado.trad += Unsp().trad;
                resultado.trad += Vsp_prima().trad;
                
            }
        else
            errorSintaxis(Token.FUNCTION,Token.VAR);
        //WARNING!!
        return resultado;
    }

    public final Atributos  Vsp_prima() // 3 Vsp_prima -> Unsp Vsp_prima ||
                                        // 4 Vsp_prima -> €
    {
        Atributos resultado = new Atributos();
        
        if(token.tipo == Token.FUNCTION || token.tipo == Token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 3");
                resultado.trad += Unsp().trad;
                resultado.trad += Vsp_prima().trad;
            }
        else if(token.tipo == Token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 4");
                resultado.trad = "";
            }
        else errorSintaxis(Token.FUNCTION,Token.VAR,Token.BEGIN);
        return resultado;
        
    }

    public final Atributos  Unsp() // 5 Unsp -> function id dosp Tipo pyc Vsp
                                   // Bloque pyc | 6 Unsp -> var LV
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Unsp");
        if(token.tipo == Token.FUNCTION)
            {
                if(flag)
                    reglasAplicadas.append(" 5");
                emparejar(Token.FUNCTION);
                
                String idAuxFuncion = ambitoFuncion(token.lexema) + token.lexema;
                //Anyado un nuevo ambito.
                nuevoAmbito(token.lexema);
                                
                emparejar(Token.ID);
                emparejar(Token.DOSP);
                String tipoFuncion = Tipo().trad;
                emparejar(Token.PYC);
                String VspAuxFuncion = Vsp().trad;
                String BloqueAuxFuncion = Bloque().trad;
                emparejar(Token.PYC);
                resultado.trad += VspAuxFuncion +"" + tipoFuncion+ " " + idAuxFuncion + "() " + BloqueAuxFuncion;
          
                
            }
        else if(token.tipo == Token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 6");
                emparejar(Token.VAR);
                resultado.trad += LV().trad;
            }
        else errorSintaxis(Token.FUNCTION,Token.VAR);
        
        return resultado;
    }

    public final Atributos LV() // 7 LV -> V LV_prima
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en LV");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 7");
                resultado.trad += V().trad;
                resultado.trad += LV_prima().trad;
                
            }
        else errorSintaxis(Token.ID);
        return resultado;
    }

    public final Atributos LV_prima() // 8 LV_prima -> V LV_prima || 9 LV_prima -> €
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en LV_prima");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 8");
                resultado.trad += V().trad;
                resultado.trad += LV_prima().trad;
                
            }
        else if(token.tipo == Token.FUNCTION || token.tipo == Token.VAR || token.tipo == Token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 9");
                resultado.trad += "";
            }
        else errorSintaxis(Token.ID,Token.FUNCTION, Token.VAR,Token.BEGIN);
        return resultado;
    }

    public final Atributos  V() // 10 V -> id Lid dosp Tipo pyc
    {
        //System.out.println("Entro en V");
        Atributos resultado = new Atributos();
        
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 10");
                
                anyadirTS(token.lexema, "");
                
                emparejar(Token.ID);
                // WARNING!!
                
                String lidAux = Lid(new Atributos()).trad;
                emparejar(Token.DOSP);
                String tipoAux = Tipo().trad;
                System.out.println(tipoAux);
                emparejar(Token.PYC);
                String arreglo = arregloHerenciaDerecha(tipoAux);
                resultado.trad += tipoAux + " " + arreglo + ";" + "\n";
                
            }
        else errorSintaxis(Token.ID);
        return resultado;
    }
    // Array.sort();
    public final Atributos Lid(Atributos atributo) // 11 Lid ->coma id Lid ||
                                                   // 12 Lid -> €
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Lid");
        if(token.tipo == Token.COMA)
            {
                if(flag)
                    reglasAplicadas.append(" 11");
                emparejar(Token.COMA);
                anyadirTS(token.lexema, "");
                
                emparejar(Token.ID);

                //CHECK
                resultado.trad = Lid(atributo).trad;
            }
        else if(token.tipo == Token.DOSP)
            {
                if(flag)
                    reglasAplicadas.append(" 12");
                resultado.trad = "";
            }
        else errorSintaxis(Token.COMA,Token.DOSP);
        return resultado;
    }

    public final Atributos  Tipo() // 13 Tipo -> Integer || 14 Tipo -> real
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Tipo");
        if(token.tipo == Token.INTEGER)
            {
                if(flag)
                    reglasAplicadas.append(" 13");
                emparejar(Token.INTEGER);
                resultado.trad = "int";
                
            }
        else if(token.tipo == Token.REAL)
            {
                if(flag)
                    reglasAplicadas.append(" 14");
                emparejar(Token.REAL);
                resultado.trad = "double";
                
            }
        else errorSintaxis(Token.INTEGER,Token.REAL);
        return resultado;
    }

    public final Atributos Bloque() // 15 Bloque -> begin SInstr end
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Bloque");
        if (token.tipo == Token.BEGIN) {
            if(flag)
                reglasAplicadas.append(" 15");
            emparejar(Token.BEGIN);
            Atributos unaInstruccion = SInstr();
            if(unaInstruccion.th == "1")
                resultado.trad = unaInstruccion.trad;
            else
                resultado.trad = "{\n"+ unaInstruccion.trad + "\n}\n";
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
        
        
        return resultado;
    }

    public final Atributos SInstr()
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en SInstr");
        //System.out.println(Token.tipo);
        if (token.tipo == Token.BEGIN || token.tipo == Token.ID || token.tipo == Token.IF || token.tipo == Token.WHILE || token.tipo == Token.WRITELN) {
            if(flag)
                reglasAplicadas.append(" 16");
            resultado.trad += Instr().trad;
            
            String comprobarUnaInstruccion = SInstrp().trad;
            if(comprobarUnaInstruccion == "")
                {
                    resultado.th = "1";
                    
                }
            else
                {
                    resultado.th = "2";
                }
            resultado.trad += comprobarUnaInstruccion;
            resultado.trad += ";";
            
        }
        else
            errorSintaxis(Token.BEGIN,Token.ID,Token.IF,Token.WHILE,Token.WRITELN);
        return resultado;
    }

    public final Atributos SInstrp()
    {
        Atributos resultado = new Atributos();
        //  System.out.println("Entro en SInstrp");
        if(token.tipo == Token.PYC)
            {
                if(flag)
                    reglasAplicadas.append(" 17");
                emparejar(Token.PYC);
                resultado.trad += ";";
                
                resultado.trad += Instr().trad;
                resultado.trad += SInstrp().trad;
                
            }
        else if(token.tipo == Token.END)
            {
                if(flag)
                    reglasAplicadas.append(" 18");
                // SInstrp();
                resultado.trad = "";
                
            }
        else errorSintaxis(Token.PYC,Token.END);
        return resultado;
    }

    public final Atributos  Instr()
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Instr");
        //System.out.println("Entro con: " + Token.lexema);
        if(token.tipo == Token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 19");
                resultado.trad += Bloque().trad;
            }
        else if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 20");
                String idAux = token.lexema;
                Atributos atributo = new Atributos();
                atributo.th = buscaTS(idAux);
                
                emparejar(Token.ID);
                emparejar(Token.ASIG);
                resultado.trad += idAux + "=" + E(atributo).trad;
            }
        else if(token.tipo == Token.IF)
            {
                Atributos atributo = new Atributos();
                
                if(flag)
                    reglasAplicadas.append(" 21");
                emparejar(Token.IF);
                resultado.trad += "if";
                
                resultado.trad += E(atributo).trad;
                
                emparejar(Token.THEN);
                resultado.trad += Instr().trad;
                resultado.trad += Instr_prima().trad;
            }
        else if(token.tipo == Token.WHILE)
            {
                Atributos atributo = new Atributos();
                
                if(flag)
                    reglasAplicadas.append(" 24");
                emparejar(Token.WHILE);
                resultado.trad += "while";
                
                resultado.trad += E(atributo).trad;
                
                emparejar(Token.DO);
                resultado.trad += Instr().trad;
                
            }
        else if(token.tipo == Token.WRITELN)
            {
                Atributos atributo = new Atributos();
                if(flag)
                    reglasAplicadas.append(" 25");
                emparejar(Token.WRITELN);
                resultado.trad += "printf";
                
                emparejar(Token.PARI);
                resultado.trad += "(";
                
                resultado.trad += E(atributo).trad;
                
                emparejar(Token.PARD);
                resultado.trad += ")";
                
            }
        else errorSintaxis(Token.BEGIN,Token.IF,Token.WHILE,Token.WRITELN,Token.ID);
        return resultado;
        
    }

    public final Atributos  Instr_prima()
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Instr_prima");
        if(token.tipo == Token.ENDIF)
            {
                if(flag)
                    reglasAplicadas.append(" 22");
                emparejar(Token.ENDIF);
                resultado.trad = "";
                
            }
        else if(token.tipo == Token.ELSE)
            {
                if(flag)
                    reglasAplicadas.append(" 23");
                emparejar(Token.ELSE);
                resultado.trad = "else";
                
                resultado.trad = Instr().trad;
                
                emparejar(Token.ENDIF);
            }
        else errorSintaxis(Token.ELSE, Token.ENDIF);
        return resultado;
    }

    public final Atributos E(Atributos atributos)
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en E");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 26");
                resultado.trad += atributos.th;
                
                resultado.trad += Expr(atributos).trad;
                
                resultado.trad += E_prima(atributos).trad;
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
    public final Atributos E_prima(Atributos atributos)
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en E_prima");
        if(token.tipo == Token.RELOP)
            {
                if(flag)
                    reglasAplicadas.append(" 27");
                resultado.trad += atributos.th;

                resultado.trad += token.lexema;
                
                emparejar(Token.RELOP);
                resultado.trad += Expr(atributos).trad;
                
            }
        else if(token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 28");
                resultado.trad = "";
                
            }
        else errorSintaxis(Token.RELOP, Token.PYC, Token.ENDIF, Token.ELSE, Token.END, Token.THEN, Token.DO, Token.PARD);
        return resultado;
        
    }

    public final Atributos Expr(Atributos atributos)
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Expr");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 29");
                resultado.trad += atributos.th;
                resultado.trad += Term().trad;
                resultado.trad += Expr_prima(atributos).trad;
                
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }

    public final Atributos  Expr_prima(Atributos atributos)
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Expr_prima");
        if(token.tipo == Token.ADDOP)
            {
                if(flag)
                    reglasAplicadas.append(" 30");
                resultado.trad += token.lexema;
                
                emparejar(Token.ADDOP);
                resultado.trad += Term().trad;
                resultado.trad += Expr_prima(atributos).trad;
            }
        else if(token.tipo == Token.RELOP || token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 31");
                resultado.trad = "";
                
            }
        else errorSintaxis(Token.ADDOP, Token.RELOP, Token.PYC, Token.ENDIF, Token.ELSE, Token.END, Token.THEN, Token.DO, Token.PARD);
        return resultado;
        
    }

    public final Atributos  Term()
    {
        Atributos resultado = new Atributos();
        Atributos atributo = new Atributos();
        
        //System.out.println("Entro en Term");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 32");
                resultado.trad += Factor().trad;
                resultado.trad += Term_prima(atributo).trad;
            }
              else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
    }


    public final Atributos  Term_prima(Atributos atributo)
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Term_prima");
        if(token.tipo == Token.MULOP)
            {
                if(flag)
                    reglasAplicadas.append(" 33");
                resultado.trad += token.lexema;
                emparejar(Token.MULOP);
                
                resultado.trad += Factor().trad;
                resultado.trad += Term_prima(atributo).trad;
            }
        else if(token.tipo == Token.ADDOP || token.tipo == Token.RELOP || token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 34");
                resultado.trad = "";
                
            }
        else errorSintaxis(Token.MULOP,Token.ADDOP, Token.RELOP, Token.PYC, Token.ENDIF, Token.ELSE, Token.END, Token.THEN, Token.DO, Token.PARD);
        return resultado;
        
    }

    public final Atributos  Factor()
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Factor");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 35");
                String idAux = ambitoVariable()+token.lexema;
                
                emparejar(Token.ID);
                resultado.trad += idAux;
                
            }
        else if(token.tipo == Token.NENTERO)
            {
                if(flag)
                    reglasAplicadas.append(" 36");
                resultado.trad += "i " +token.lexema;
                
                emparejar(Token.NENTERO);
                
            }
        else if(token.tipo == Token.NREAL)
            {
                if(flag)
                    reglasAplicadas.append(" 37");
                resultado.trad += "r "+ token.lexema;
                
                emparejar(Token.NREAL);
                
            }
        else if(token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 38");
                
                emparejar(Token.PARI);
                resultado.trad += "(" + Expr(new Atributos()).trad + ")";
                emparejar(Token.PARD);
                
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
}
//  LocalWords:  sintactico
