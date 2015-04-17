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
    public String tipo;
    

    public Atributos()
    {
        trad = "";
        th = "";
        tipo = "";
        
    }

    public Atributos(String traduccion)
    {
        this.trad = traduccion;
        this.th = "";
        this.tipo = "";
        
    }
    
    public Atributos(String traduccion, String heredado)
    {
        this.trad = traduccion;
        this.th = heredado;
        this.tipo = "";
    }
    @Override
    public String toString()
    {
        return "Atributo: \n\tTraducción: " + trad + "\n\tHeredado: " + th + "\n\tTipo: " + tipo + "\n";
        
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
        //System.out.println("CHECK AMBITO VARIABLE");
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
        //System.out.println("Variable está en el ambito: " + ambitoAux);
        return ambitoAux;
    }

    private final String ambitoFuncion(String main)
    {
        //System.out.println("CHECK AMBITO FUNCION");
        String ambitoAux = "";
        int i = ambitos.size()-1;
        while(i> 0)
            {
                ambitoAux = ambitos.get(i) + "_" + ambitoAux;
                
                //System.out.println(ambitoAux);
                i--;
            }
        //PARCHEACO!!
        if(main == "")
            {
                ambitoAux = "main";
                
            }
        //System.out.println("Función está en el ámbito: " + ambitoAux);
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
        //System.out.println("CHECK BORRAR AMBITO");
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
                                if(i == 0)
                                    resultado = ambitoVariable() +s + resultado;
                                else
                                    resultado = ambitoVariable() +s+ "," + resultado;
                            }
                    }
                //System.out.println(i);
                //Elimino la última coma
                
                i++;
            }
        //System.out.println("SALGO DE CHECK ARREGLO con: \n" + ts);
        return resultado;
    }

    private final String buscaTS(String id)
    {
        int numAmbitos = ambitos.size() -1;
        String resultado = "";
        
        //  System.out.println("CHECK BUSCA TS");
        while(numAmbitos >= 0)
            {
                // System.out.println(numAmbitos + "\n" + id);
                if(ts.get(numAmbitos).containsKey(id))
                    {
                        //System.out.println("ENTRO");
                        resultado = ts.get(numAmbitos).get(id);
                        break;
                    }
                numAmbitos--;
            }
        if(resultado == "")
            {
                System.out.println("ERROR: NO SE HA ENCONTRADO EN LA TABLA DE SIMBOLOS");
            }
        //System.out.println("RES " +resultado);
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
                
                trad += Vsp().trad;
                trad += Bloque(new Atributos("int main() ")).trad;
                
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
                Atributos atributosSiempreCorchetesParaFuncion = new Atributos();
                atributosSiempreCorchetesParaFuncion.th = "2";
                
                String BloqueAuxFuncion = Bloque(atributosSiempreCorchetesParaFuncion).trad;
                emparejar(Token.PYC);
                resultado.trad += VspAuxFuncion + "" + tipoFuncion+ " " + idAuxFuncion + "() " + BloqueAuxFuncion;
                //Termina el ambito de la función.
                borrarAmbito();
                
                
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
                //System.out.println(tipoAux);
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

    public final Atributos Bloque(Atributos atributo) // 15 Bloque -> begin SInstr end
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Bloque");
        if (token.tipo == Token.BEGIN) {
            if(flag)
                reglasAplicadas.append(" 15");
            emparejar(Token.BEGIN);
            //PARCHE MAIN
            if(atributo.trad != "")
                {
                    resultado.trad += atributo.trad;
                }
            Atributos unaInstruccion = SInstr();
            if(unaInstruccion.th == "1")
                resultado.trad += unaInstruccion.trad;
            else
                resultado.trad += "{\n"+ unaInstruccion.trad + "\n}\n";
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
        
        
        return resultado;
    }

    public final Atributos SInstr() // 16 SInstr -> Instr SInstrp
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
            resultado.trad += ";";// + "\n";
            
        }
        else
            errorSintaxis(Token.BEGIN,Token.ID,Token.IF,Token.WHILE,Token.WRITELN);
        return resultado;
    }

    public final Atributos SInstrp() // 17 SInstrp -> pyc Instr SInstrp
                                     // 18 SInstrp -> €
    {
        Atributos resultado = new Atributos();
        //  System.out.println("Entro en SInstrp");
        if(token.tipo == Token.PYC)
            {
                if(flag)
                    reglasAplicadas.append(" 17");
                emparejar(Token.PYC);
                //Final de cada instruccion un \n
                resultado.trad += ";"+ "\n";
                
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

    public final Atributos  Instr() // 19 Instr -> Bloque
                                    // 20 Instr -> id asig E
                                    // 21 Instr -> if E then Instr Instr_prima
                                    // 24 Instr -> while E do Instr
                                    // 25 Instr -> writeln pari E pard
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Instr");
        //System.out.println("Entro con: " + Token.lexema);
        if(token.tipo == Token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 19");
                resultado.trad += Bloque(new Atributos()).trad;
            }
        else if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 20");
                String idAux = ambitoVariable() + token.lexema;
                Atributos atributo = new Atributos();
                atributo.tipo = buscaTS(token.lexema);
                
                //System.out.println("ATRIBUTO: " + idAux + atributo);
                emparejar(Token.ID);
                emparejar(Token.ASIG);
                Atributos resultadoAux = E(new Atributos());
                if(atributo.tipo == "double" && resultadoAux.tipo == "int")
                    {
                        resultado.trad += idAux + " " + "=" + "r itor(" + resultadoAux.trad + ")";
                        
                    }
                else if(atributo.tipo == "double" && resultadoAux.tipo == "double") {
                    resultado.trad += idAux + " " + "=" + "r "+ resultadoAux.trad;
                }
                else if(atributo.tipo == "int" && resultadoAux.tipo == "int") {
                    resultado.trad += idAux + " " + "=" + "i " + resultadoAux.trad;
                }
                else{
                    resultado.trad += idAux + " " + "=" + "r " + resultadoAux.trad;
                    System.out.println("WARNING! Pérdida de información");
                }
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
                Atributos resultadoAux = E(atributo);
                if(resultadoAux.tipo == "int")
                    resultado.trad += "\"%d" +"\\" + "n"+ "\"" + "," +resultadoAux.trad;
                else
                    resultado.trad += "\"%g" + "\\" + "\n" + "\"" + "," + resultadoAux.trad;
                
                //CREO QUE ESTO YA NO VALE EN ESTE PUNTO
                //resultado.tipo += resultadoAux.tipo;
                
                //System.out.println("WRITELN" +resultado);
                emparejar(Token.PARD);
                resultado.trad += ")";
                
            }
        else errorSintaxis(Token.BEGIN,Token.IF,Token.WHILE,Token.WRITELN,Token.ID);
        return resultado;
        
    }

    public final Atributos  Instr_prima() // 22 Instr_prima -> endif
                                          // 23 Instr_prima -> else Instr endif
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

    public final Atributos E(Atributos atributos) // 26 E -> Expr E_prima
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en E");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 26");
                // resultado.trad += atributos.th;
                Atributos resultadoAux1 = Expr(atributos);
                Atributos resultadoAux2 = E_prima(atributos);
                
                //System.out.println("E\n" + resultadoAux1.tipo);
                resultado.trad += resultadoAux1.trad;
                resultado.tipo = resultadoAux1.tipo;
                
                resultado.trad += resultadoAux2.trad;
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
    public final Atributos E_prima(Atributos atributos) // 27 E_prima -> relop Expr
                                                        // 28 E_prima -> € 
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en E_prima");
        if(token.tipo == Token.RELOP)
            {
                if(flag)
                    reglasAplicadas.append(" 27");
                //CHECK
                //resultado.trad += atributos.th;

                resultado.trad += token.lexema;
                
                emparejar(Token.RELOP);
                Atributos resultadoAux = Expr(atributos);
                
                resultado.trad += resultadoAux.trad;
                resultado.tipo = resultadoAux.tipo;
                
                
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

    public final Atributos Expr(Atributos atributos) // 29 Expr -> Term Expr_prima
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Expr");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 29");
                //resultado.trad += atributos.th;
                Atributos resultadoAux1 = Term();
                Atributos resultadoAux2 = Expr_prima(atributos);
                //System.out.println("EXPR\n" +resultadoAux1);
                resultado.trad += resultadoAux1.trad;
                resultado.tipo = resultadoAux1.tipo;
                
                resultado.trad += resultadoAux2.trad;
                
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }

    public final Atributos Expr_prima(Atributos atributos) // 30 Expr_prima ->addop Term Expr_prima
                                                            // 31 Expr_prima -> €
    {
        Atributos resultado = new Atributos();
        if(token.tipo == Token.ADDOP)
            {
                if(flag)
                    reglasAplicadas.append(" 30");
                resultado.trad += token.lexema;
                
                emparejar(Token.ADDOP);
                Atributos resultadoAux1 = Term();
                Atributos resultadoAux2 = Expr_prima(atributos);
                
                if(resultadoAux1.tipo.equals("double") && resultadoAux2.tipo.equals("double")) {
                        resultado.tipo = "double";
                        resultado.trad += resultadoAux1.trad;
                        resultado.trad += resultadoAux2.trad;
                }
                else if(resultadoAux1.tipo.equals("double") && resultadoAux2.tipo.equals("int")) {
                    resultado.tipo = "double";
                    resultado.trad += resultadoAux1.trad;
                    resultado.trad += "r itor(" + resultadoAux2.trad + ")";
                }
                else if(resultadoAux1.tipo.equals("int") && resultadoAux2.tipo.equals("int")) {
                    resultado.tipo = "int";
                    resultado.trad += resultadoAux1.trad;
                    resultado.trad += resultadoAux2.trad;
                }
                else if(resultadoAux1.tipo.equals("int") && resultadoAux2.tipo.equals("double")) {
                    resultado.tipo = "double";
                    resultado.trad += "r itor( " + resultadoAux1.trad + ")";
                    resultado.trad += resultadoAux2.trad;
                    
                }
                else
                    if(resultadoAux1.tipo.equals("double"))
                        {
                            resultado.tipo = "double";
                            resultado.trad += "r "+resultadoAux1.trad;
                            resultado.trad += resultadoAux2.trad;
                            
                        }
                    else {
                        resultado.tipo = "int";
                        resultado.trad += "i " +resultadoAux1.trad;
                        resultado.trad += resultadoAux2.trad;
                        
                    }
                //System.out.println("ALGO FALLA");
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

    public final Atributos  Term() // 32 Term -> Factor Term_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributo = new Atributos();
        
        // System.out.println("Entro en Term");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 32");
                Atributos resultadoAux1 = Factor();
                Atributos resultadoAux2 = Term_prima(atributo);
                //System.out.println("TERM\n" + resultadoAux1);
                if(resultadoAux2.th.equals("/")) {
                        resultado.tipo = "double";
                        
                        if(resultadoAux1.tipo.equals("int") )
                                resultado.trad += "itor(" + resultadoAux1.trad + ")"+resultadoAux2.trad;
                        else if(resultadoAux1.tipo.equals("double"))
                                resultado.trad += resultadoAux1.trad + resultadoAux2.trad;
                        else
                            resultado.trad += "YA SE ME OCURRIRÁ ALGO";
                        
                    }
                else if(resultadoAux2.th.equals("div")){
                    resultado.tipo = "int";
                    
                }
                else if(resultadoAux2.th.equals("*")){
                    
                    
                    if(resultadoAux1.tipo.equals("double") || resultadoAux2.tipo.equals("double"))
                        resultado.tipo = "double";
                    else
                        resultado.tipo = "int";
                    resultado.trad += resultadoAux1.trad;
                    resultado.trad += resultadoAux2.trad;
                    
                }
                else {
                    if(resultadoAux1.tipo == resultadoAux2.tipo)
                    {
                        resultado.trad += resultadoAux1.trad;
                        resultado.tipo = resultadoAux1.tipo;
                
                        resultado.trad += resultadoAux2.trad;
                    }
                else
                    {
                        if(resultadoAux1.tipo == "int" && resultadoAux2.tipo == "double")
                            {
                                resultado.tipo = "double";
                                resultado.trad = "itor(" + resultadoAux1.trad;
                                resultado.trad = ")";
                                resultado.trad = resultadoAux2.trad;
                                // System.out.println("ENTRO AQUI" + resultado);
                            }
                        else if(resultadoAux1.tipo == "double" && resultadoAux2.tipo == "int")
                            {
                                
                            }
                        else
                            {
                                resultado.trad += resultadoAux1.trad;
                                resultado.tipo = resultadoAux1.tipo;
                                resultado.trad += resultadoAux2.trad;
                                
                            }
                    }
                // System.out.println("RESULTADO TRAD" + resultado.trad);
                }
                
            }
              else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
    }

    //AQUÍ HAY MUCHA TELA. COMPROBACIONES por * / div
    public final Atributos  Term_prima(Atributos atributo) // 33 Term_prima -> mulop Factor Term_prima
                                                           // 34 Term_prima -> €
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en Term_prima");
        if(token.tipo == Token.MULOP)
            {
                if(flag)
                    reglasAplicadas.append(" 33");
                String idAux = token.lexema;
                emparejar(Token.MULOP);
                Atributos resultadoAux1 = Factor();
                Atributos resultadoAux2 = Term_prima(atributo);
                resultado.trad += idAux;
                
                if(idAux.equals("/")) {
                    resultado.th = "/";
                    
                    if(resultadoAux1.tipo == "double")
                        {
                            resultado.trad += resultadoAux1.trad;
                            resultado.tipo = resultadoAux1.tipo;
                        }
                    else if(resultadoAux1.tipo == "int")
                        {
                            resultado.trad += "r itor(" + resultadoAux1.trad + ")";
                            resultado.tipo = "double";
                        }
                    else
                        System.out.println("ESTOY JODIDO");
                }
                else if( idAux.equals("div"))
                    {
                        resultado.th = "div";
                        
                        if(resultadoAux1.tipo == "int" && resultadoAux2.tipo == "int")
                                System.out.println("REALIZAR DIV");
                        else
                            System.out.println("ERROR!! DIV");
                    }
                else if(idAux.equals("*"))
                    {
                        //                        System.out.println("VOY A MULTIPLICAR");
                        resultado.th = "*";
                        if(resultadoAux1.tipo == "double")
                            {
                                resultado.tipo = "double";
                                resultado.trad += "r " +resultadoAux1.trad;
                                resultado.trad += resultadoAux2.trad;
                            }
                        else
                            {
                                resultado.tipo = "int";
                                resultado.trad += "i " + resultadoAux1.trad;
                                resultado.trad += resultadoAux2.trad;
                            }
                        // System.out.println(resultado);
                    }
                else
                    System.out.println("A TOMAR POR CULO"+idAux);
                
            }
        else if(token.tipo == Token.ADDOP || token.tipo == Token.RELOP || token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 34");
                resultado.trad = "";
                
            }
        else errorSintaxis(Token.MULOP,Token.ADDOP, Token.RELOP, Token.PYC, Token.ENDIF, Token.ELSE, Token.END, Token.THEN, Token.DO, Token.PARD);
        //System.out.println("SALGO DE TERM_PRIMA");
        return resultado;
        
    }

    public final Atributos  Factor() // 35 Factor -> id
                                     // 36 Factor -> nentero
                                     // 37 Factor -> nreal
                                     // 38 Factor -> pari Expr pard
    {
        Atributos resultado = new Atributos();
        //System.out.println("Entro en Factor");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 35");
                String idAux = ambitoVariable()+token.lexema;
                if(buscaTS(token.lexema) == "int")
                    {
                        resultado.trad += idAux + " ";
                        resultado.tipo = "int";
                        
                    }
                else if(buscaTS(token.lexema) == "double")
                    {
                        resultado.trad += idAux + " ";
                        resultado.tipo = "double";
                        
                    }
                emparejar(Token.ID);
                //resultado.trad += idAux + " ";
                
            }
        else if(token.tipo == Token.NENTERO)
            {
                if(flag)
                    reglasAplicadas.append(" 36");
                resultado.trad +=token.lexema+ " ";
                resultado.tipo = "int";
                
                emparejar(Token.NENTERO);
                
            }
        else if(token.tipo == Token.NREAL)
            {
                if(flag)
                    reglasAplicadas.append(" 37");
                resultado.trad += token.lexema + " ";
                resultado.tipo = "double";
                
                emparejar(Token.NREAL);
                
            }
        else if(token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 38");
                
                emparejar(Token.PARI);
                Atributos resultadoAux = Expr(new Atributos());
                
                resultado.trad += "(" + resultadoAux.trad + ")";
                resultado.tipo = resultadoAux.tipo;
                
                emparejar(Token.PARD);
                
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
}
//  LocalWords:  sintactico
