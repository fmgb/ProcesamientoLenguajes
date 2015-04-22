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
import java.util.ArrayDeque;


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
    int debug = 1;
    
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
        flag = false;
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

    public final void errorSemanticoNoDeclarado()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): \'" + token.lexema + "\' no ha sido declarado");
        System.exit(-1);
    }

    public final void errorSemanticoIdRepetido()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): \'" + token.lexema + "\' ya existe en este ambito");
        System.exit(-1);
        
    }

    public final void errorSemanticoNoEsVariable()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): \'" + token.lexema + "\' no es una variable");
        System.exit(-1);
    }
    
    public final void errorSemanticoTipoRealAEntero()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): \'" + token.lexema + "\' debe ser de tipo real");
        System.exit(-1);
    }


    public final void errorSemanticoAsigBool() 
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): el operador \':= \'" + " no admite expresiones relacionales");
        System.exit(-1);
    }

    public final void errorSemanticoTipoRelacional()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): en la instruccion \'" + token.lexema + "\' la expresion debe ser relacional");
        System.exit(-1);
        
    }

    public final void errorSemanticoDivEntero()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): los dos operandos de \'div\' deben ser enteros");
        System.exit(-1);
     
    }

    public final void errorSemanticoImprimirBool()
    {
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): \'writeln\' no admite expresiones booleanas");
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
        String ambitoAux = "";
        int i = ambitos.size()-1;
        while(i> 0)
            {
                ambitoAux = ambitos.get(i) + "_" +ambitoAux;
                i--;
            }
        if(ambitoAux == "")
            {
                ambitoAux = "main_";
                
            }
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
                i--;
            }
        //PARCHEACO!!
        if(main == "")
            {
                ambitoAux = "main";
                
            }
        return ambitoAux;
    }

    private final void anyadirTS(String id, String tipo)
    {
        
        //System.out.println("CHECK AnyadirTS");
        //        System.out.println(ts);
        // Las nuevas variables son las primeras, así que hay que realizar
        // alguna forma para que al salir de la TS, salgan en su orden correcto.
        if(!ts.get(ambitos.size() -1).containsKey(id)) {
            if(tipo == "")
                ts.get(ambitos.size() -1).put(token.lexema,"VACIO");
            else
                ts.get(ambitos.size() -1).put(token.lexema,tipo);
        } else
            errorSemanticoIdRepetido();
        
    
        // System.out.println(ts);
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
        // System.out.println(ts.toString());d
        
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
                                // System.out.println(s);
                                ts.get(numAmbito).put(s,tipo);
                                
                                resultado = ambitoVariable() +s+ "," + resultado;
                            }
                    }
                i++;
            }
        //PARCHE PARA EVITAR LA COMA DEL FINAL
        if(resultado.charAt(resultado.length() -1) == ',')
            resultado = resultado.substring(0,resultado.length()-1);
        //System.out.println(resultado);
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
                        if(resultado == "funcion")
                            errorSemanticoNoEsVariable();
                        
                        break;
                    }
                numAmbitos--;
            }
        if(resultado == "")
            {
                //   System.out.println("ERROR: NO SE HA ENCONTRADO EN LA TABLA
                //   DE SIMBOLOS");
                errorSemanticoNoDeclarado();
                
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
                anyadirTS(token.lexema,"funcion");
                
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
                //System.out.println(resultado);
                resultado.trad += LV_prima().trad;
                //                System.out.println("LV \n " + resultado);
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
                resultado.trad += tipoAux + " " + arreglo + "hoaaa;" + "\n";
                
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
                //System.out.println("LID");
                resultado.trad = Lid(atributo).trad;
                //                System.out.println(resultado);
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
            atributo.th = "2";
            
            Atributos unaInstruccion = SInstr(atributo);
            //System.out.println("BLOQUE : " + unaInstruccion);
            if(unaInstruccion.th == "1")
                resultado.trad += unaInstruccion.trad;
            else{
            //ESTO SI QUE ES UN PARCHE DE LOS BUENOS
                if(unaInstruccion.trad.lastIndexOf('\n') == unaInstruccion.trad.length() -1)
                    resultado.trad += "{\n"+ unaInstruccion.trad + "\n}\n";
                else
                    resultado.trad += "{\n"+ unaInstruccion.trad + ";\n}\n";
            }
            
            
            
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
        
        
        return resultado;
    }

    public final Atributos SInstr(Atributos atributo) // 16 SInstr -> Instr SInstrp
    {
        Atributos resultado = new Atributos();
        
        //System.out.println("Entro en SInstr");
        //System.out.println(Token.tipo);
        if (token.tipo == Token.BEGIN || token.tipo == Token.ID || token.tipo == Token.IF || token.tipo == Token.WHILE || token.tipo == Token.WRITELN) {
            if(flag)
                reglasAplicadas.append(" 16");
            Atributos auxInstr = Instr(new Atributos());
            resultado.trad += auxInstr.trad;
            
            Atributos auxiliarAtri = new Atributos();
            auxiliarAtri.th = auxInstr.th;
            
            
            String comprobarUnaInstruccion = SInstrp(auxiliarAtri).trad;
            
            
            if(comprobarUnaInstruccion == "")
                {
                    resultado.th = "1";
                    
                }
            else
                {
                    resultado.th = "2";
                }
            //PARCHEACO FUNCIONES PARA QUE SIEMPRE TENGAN { }
            if(resultado.th != atributo.th)
                resultado.th = atributo.th;
            //resultado.trad+= " ejufj\n";
            resultado.trad += comprobarUnaInstruccion;
            //resultado.trad+= "hhggggg\n";
            //            System.out.println("HOAL");
            //            System.out.println(resultado.trad);
            if(resultado.trad.equals(";"))
                resultado.trad = "";
            
            //   resultado.trad += " ;";// + "\n";
            
        }
        else
            errorSintaxis(Token.BEGIN,Token.ID,Token.IF,Token.WHILE,Token.WRITELN);
        return resultado;
    }

    public final Atributos SInstrp(Atributos atributo) // 17 SInstrp -> pyc Instr SInstrp
                                     // 18 SInstrp -> €
    {
        Atributos resultado = new Atributos();
        
        //  System.out.println("Entro en SInstrp");
        if(token.tipo == Token.PYC)
            {
                Atributos pycParaIF = new Atributos();
                if(flag)
                    reglasAplicadas.append(" 17");
                emparejar(Token.PYC);
                //Final de cada instruccion un \n
                
                //resultado.trad += " ;" + "\n";
                
                if(atributo.th.equals("pyc")){
                    //resultado.trad += "p";
                    //resultado.trad +=";";
                    
                    pycParaIF.th = "pyc";
                }   
                else
                    pycParaIF.th = "nopyc";
                
                
                Atributos resultadoAux1 = Instr(pycParaIF);
                
                
                //if(!resultadoAux1.th.equals(""))
                
                atributo.th = resultadoAux1.th;
                
                //System.out.println("HASDI" + resultadoAux1);
                //if(resultadoAux1.th.equals("nopyc"))
                if(atributo.th.equals("nopyc"))
                    {
                        // System.out.println("HOLA");
                        
                        //                        System.out.println(resultadoAux1);
                        //   resultado.trad += "\n";
                    }
                else if(atributo.th.equals("pyc"))
                    resultado.trad += "padd;" + "\n";
                //resultado.trad += "hoasdasfkjag\n";
                resultado.trad += resultadoAux1.trad;
                //resultado.trad+="jasfjd";
                if(resultadoAux1.th.equals("nopyc"))
                    {
                        atributo.th = "nopyc";
                        //atributo.th = "";
                    }
                else if (resultadoAux1.th.equals("pyc"))
                    atributo.th = "pyc";
                else
                    atributo.th = "pyc";
                
                
                Atributos resultadoAux2 = SInstrp(atributo);
                resultado.trad += "ADIOS" +debug;
                
                resultado.trad += resultadoAux2.trad;
                resultado.trad += "HOLA"+ debug;
                //resultado.th = resultadoAux2.th;
                
                ++debug;
                
                //resultado.th += resultadoAux2.th;
                
                //System.out.println(resultadoAux1);
            }
        else if(token.tipo == Token.END)
            {
                if(flag)
                    reglasAplicadas.append(" 18");
                //resultado.th = "pyc";
                
                // SInstrp();
                // resultado.trad = ";";
                
            }
        else errorSintaxis(Token.PYC,Token.END);
        return resultado;
    }

    public final Atributos  Instr(Atributos atribu) // 19 Instr -> Bloque
                                    // 20 Instr -> id asig E
                                    // 21 Instr -> if E then Instr Instr_prima
                                    // 24 Instr -> while E do Instr
                                    // 25 Instr -> writeln pari E pard
    {
        Atributos resultado = new Atributos();
        Atributos atributo = new Atributos();
        resultado.th = "pyc";
        
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
               
                atributo.tipo = buscaTS(token.lexema);
                Token tokenAux = token;
                //System.out.println("ATRIBUTO: " + idAux + atributo);
                emparejar(Token.ID);
                Token tokenAuxAsig = token;
                emparejar(Token.ASIG);
                
                
                
                Atributos resultadoAux = E(new Atributos());
                if(atributo.tipo == "double" && resultadoAux.tipo == "int")
                    {
                        resultado.trad += idAux + " " + "=" + "r itor(" + resultadoAux.trad;
                        if(resultadoAux.trad.indexOf("))") != -1)
                            {
                                resultado.trad.replace("))",")");
                                
                            }
                    }
                else if(atributo.tipo == "double" && resultadoAux.tipo == "double") {
                    resultado.trad += idAux + " " + "=" + "r "+ resultadoAux.trad;
                }
                else if(atributo.tipo == "int" && resultadoAux.tipo == "int") {
                    resultado.trad += idAux + " " + "=" + "i " + resultadoAux.trad;
                }
                else{
                    token = tokenAuxAsig;
                    
                    if(resultadoAux.tipo.equals("booleano"))
                        errorSemanticoAsigBool();
                    token = tokenAux;
                    errorSemanticoTipoRealAEntero();
                    
                }
                // System.out.println("INSTR: " + resultado);
            }
        else if(token.tipo == Token.IF)
            { //TODO CUIDADO CON LOS { } NO SÉ SI HAY QUE PONERLOS O NO.
                if(flag)
                    reglasAplicadas.append(" 21");
                emparejar(Token.IF);
                // resultado.trad += "IFFSDF";
                
                if(atribu.th.equals("pyc")){
                    //System.out.println("adio");
                    resultado.trad += "foto;\n"; // foto
                    //                    System.out.println("HOLA");
                }
                else{
                    //                    System.out.println("quetal");
                    resultado.trad += "\n";
                }
                
                
                resultado.trad += "if";
                Atributos resultadoAux = E(atributo);
                //PARCHE PARENTESIS
                //System.out.println(resultadoAux.trad+ "k");
                if(resultadoAux.trad.charAt(0) != '(') {
                    resultado.trad += "(";
                    resultado.trad += resultadoAux.trad;
                    
                    resultado.trad += ")";
                }
                else
                    resultado.trad += resultadoAux.trad;
                resultado.trad += "\n";
                //                System.out.println("IF!!!");
                emparejar(Token.THEN);
                resultado.trad += Instr(atribu).trad;
                resultado.th = "nopyc";
                resultado.trad += Instr_prima().trad;
                //resultado.trad += "\n";
                //resultado.trad += "ifasd \n";
                
                //resultado.th= "";
                
                
                //System.out.println(resultado);
            }
        else if(token.tipo == Token.WHILE)
            {
                if(flag)
                    reglasAplicadas.append(" 24");
                Token tokenAux = token;
                emparejar(Token.WHILE);
                resultado.trad += "while ";
                //                System.out.println("WHILE");
                Atributos resultadoAux = E(atribu);
                if(!resultadoAux.tipo.equals("booleano"))
                    {
                        token = tokenAux;
                        errorSemanticoTipoRelacional();
                    }
                
                
                
                if(resultadoAux.trad.charAt(0) != '(') {
                    resultado.trad += "( ";
                    resultado.trad += resultadoAux.trad;
                    //resultado.th = "nopyc";
                    
                    resultado.trad += ")\n";
                }
                else
                    resultado.trad += resultadoAux.trad;
                emparejar(Token.DO);
                
                Atributos atributoAux = Instr(new Atributos());
                resultado.trad += atributoAux.trad;
                //cuidado esto!
                // ESTE ES EL DOBLE ;
                //resultado.th = atributoAux.th;
                //                if(atributoAux.th.equals("pyc"))
                
                //System.out.println("WHILE" + resultado.trad);
            }
        else if(token.tipo == Token.WRITELN)
            {
                if(flag)
                    reglasAplicadas.append(" 25");
                //Almaceno el token por si acaso resulta que hay un error.
                Token tokenAux = token;
                emparejar(Token.WRITELN);
                resultado.trad += "printf";
                
                emparejar(Token.PARI);
                resultado.trad += "(";
                
                
                Atributos resultadoAux = E(atributo);
                if(resultadoAux.tipo == "int")
                    resultado.trad += "\"%d" +"\\" + "n"+ "\"" + "," +resultadoAux.trad;
                else if(resultadoAux.tipo == "double")
                    resultado.trad += "\"%g" + "\\" + "n" + "\"" + "," + resultadoAux.trad;
                else
                    {
                        token = tokenAux;
                        errorSemanticoImprimirBool();
                    }
                
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
        //WARNING!
        
        if(token.tipo == Token.ENDIF)
            {
                if(flag)
                    reglasAplicadas.append(" 22");
                emparejar(Token.ENDIF);
                resultado.trad = "";
                //resultado.th = "pyc";
                //System.out.println("aad");
            }
        else if(token.tipo == Token.ELSE)
            {
        
                if(flag)
                    reglasAplicadas.append(" 23");
                emparejar(Token.ELSE);
                resultado.trad += "gato;\n";  //gato
                
                resultado.trad += "else";
                resultado.trad += "\n";
                // resultado.th = "pyc";
                Atributos atributoAuxiliar = new Atributos();
                // atributoAuxiliar.th = "pyc";
                
                atributoAuxiliar = Instr(atributoAuxiliar);
                
                resultado.trad += atributoAuxiliar.trad;
                resultado.th = atributoAuxiliar.th;
                
                if(atributoAuxiliar.th.equals("pyc") ) {
                    // System.out.println("HOLAS");
                    //                    System.out.println(atributoAuxiliar);
                    resultado.trad += "delta;"; // delta
                    //resultado.th = "nopyc";
                    //System.out.println("HASDAD");
                    //                    System.out.println(resultado);
                }
                
                
                
                // System.out.println(resultado);
                emparejar(Token.ENDIF);
            }
        else errorSintaxis(Token.ELSE, Token.ENDIF);
        //resultado.th = "pyc";
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
                if(resultadoAux1.tipo.equals("double"))
                    atributos.th = "double";
                
                Atributos resultadoAux2 = E_prima(atributos);
                
                //System.out.println("E\n" + resultadoAux1.tipo);
                if(resultadoAux2.th.equals("double")){
                    resultado.trad += "itor(" + resultadoAux1.trad + ")";
                }
                else 
                    resultado.trad +=  resultadoAux1.trad;
                if(resultadoAux2.tipo.equals("booleano"))
                    resultado.tipo = resultadoAux2.tipo;
                else
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
                if(token.lexema.equals("="))
                    resultado.trad += "==";
                else if(token.lexema.equals("<>"))
                    resultado.trad += "!=";
                else
                    resultado.trad += token.lexema;
                //                 System.out.println("SOUT" + resultado.trad);
                 emparejar(Token.RELOP);
                 Atributos resultadoAux = Expr(atributos);
                 if(resultadoAux.tipo.equals("int")){
                     resultado.th = "int";
                     
                     if(atributos.th.equals("double"))
                         resultado.trad += "r itor(" + resultadoAux.trad+ ")";
                     else
                         resultado.trad += "i " +resultadoAux.trad;
                 }
                 
                 else{
                     resultado.th = "double";
                     resultado.trad += "r " +resultadoAux.trad;
                 }
                 
                 
                 resultado.tipo = "booleano";
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
 
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 29");
 
                Atributos resultadoAux1 = Term();
                if(resultadoAux1.tipo.equals("double"))
                    atributos.th = "double";
                
                Atributos resultadoAux2 = Expr_prima(atributos);
                
                if(resultadoAux1.trad.indexOf("(*i ") != -1){
                    resultadoAux1.trad = resultadoAux1.trad.replace("(*i ", "(");
                }
                resultado.trad += resultadoAux1.trad + " ";
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
                Token tokenAux = token;
                
                
                emparejar(Token.ADDOP);
                Atributos resultadoAux1 = Term();
                Atributos resultadoAux2 = Expr_prima(atributos);
                //System.out.println("HOLA");
                if(resultadoAux1.tipo.equals("double") && resultadoAux2.tipo.equals("double")) {
                    resultado.trad += tokenAux.lexema;
                    resultado.tipo = "double";
                    resultado.trad += resultadoAux1.trad;
                    resultado.trad += resultadoAux2.trad;
                }
                else if(atributos.th.equals("double")) {
                    resultado.trad +=tokenAux.lexema;
                    resultado.tipo = "double";
                    resultado.trad += "r itor(" + resultadoAux1.trad + ")";
                    resultado.trad += resultadoAux2.trad;
                    
                      }
                else if(resultadoAux1.tipo.equals("double") && resultadoAux2.tipo.equals("int")) {
                    resultado.trad += tokenAux.lexema;
                    resultado.tipo = "double";
                    resultado.trad += resultadoAux1.trad;
                    resultado.trad += "r itor(" + resultadoAux2.trad;

                }
                else if(resultadoAux1.tipo.equals("int") && resultadoAux2.tipo.equals("int")) {
                    resultado.trad += tokenAux.lexema;
                    resultado.tipo = "int";
                    resultado.trad += "i " +resultadoAux1.trad + " ";
                    
                    resultado.trad += resultadoAux2.trad;
                }
                else if(resultadoAux1.tipo.equals("int") && resultadoAux2.tipo.equals("double")) {
                    resultado.tipo = "int";
                    resultado.trad += tokenAux.lexema;
                    resultado.trad += "i " + resultadoAux1.trad;
                    //PARCHEACO XD realizar itor( con varios int)
                    resultado.trad += ")";
                                        
                    resultado.trad += resultadoAux2.trad;
                }
                else
                    if(resultadoAux1.tipo.equals("double"))
                        {
                            resultado.trad += tokenAux.lexema;
                            resultado.tipo = "double";
                            resultado.trad += "r "+resultadoAux1.trad;
                            resultado.trad += resultadoAux2.trad;
                            
                        }
                    else {
                        resultado.trad += tokenAux.lexema;
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
        //System.out.println(resultado);
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
                Token auxToken = token;
                
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
                    if(resultadoAux1.tipo.equals("int") && resultadoAux2.tipo.equals("int"))
                        {
                            resultado.trad += resultadoAux1.trad + resultadoAux2.trad;
                        }
                    else{
                        //System.out.println(auxToken);
                        token = auxToken;
                        errorSemanticoDivEntero();
                    }  
                }
                else if(resultadoAux2.th.equals("*")){
                    //System.out.println("HOALDDADASD");
                    if(resultadoAux1.tipo.equals("double") && resultadoAux2.tipo.equals("double")){
                        resultado.trad += resultadoAux1.trad;
                        resultado.trad += resultadoAux2.trad;
                        resultado.tipo = "double";
                    }
                    else if(resultadoAux1.tipo.equals("double") && resultadoAux2.tipo.equals("int")) {
                        resultado.trad += resultadoAux1.trad + " ";
                        resultado.trad += "*r itor(" + resultadoAux2.trad + ")";
                        resultado.tipo = "double";
                    }
                    else if(resultadoAux1.tipo.equals("int") && resultadoAux2.tipo.equals("double")) {
                        resultado.trad += "itor(" + resultadoAux1.trad + ")";
                        resultado.trad += resultadoAux2.trad;
                        resultado.tipo = "double";
                        
                    }
                    
                    else{
                        resultado.tipo = "int";
                        resultado.trad += resultadoAux1.trad + resultadoAux2.trad;
                        
                    }
                }
                else {
                    //System.out.println("HOAA" + resultadoAux1 + resultadoAux2);
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
                            }
                        else if(resultadoAux1.tipo == "double" && resultadoAux2.tipo == "int")
                            {
                                resultado.tipo = "double";
                                //System.out.println("NETRO");
                            }
                        else
                            {
                                resultado.trad += resultadoAux1.trad;
                                resultado.tipo = resultadoAux1.tipo;
                                resultado.trad += resultadoAux2.trad;
                                
                            }
                    }
                
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
                //
                
                if(idAux.equals("/")) {
                    resultado.trad += idAux;
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
                        resultado.trad += "/";
                        
                        if(resultadoAux1.tipo == "int") {// && resultadoAux2.tipo == "int")
                            resultado.tipo = "int";
                            resultado.trad += "i "+ resultadoAux1.trad;
                        }
                        //else
                        //  errorSemanticoDivEntero();
                        
                    }
                else if(idAux.equals("*"))
                    {
                       resultado.trad += idAux;
                        //                        System.out.println("VOY A MULTIPLICAR");
                        resultado.th = "*";
                        if(resultadoAux1.tipo == "double")
                            {
                                
                                resultado.tipo = "double";
                                resultado.trad += "r " +resultadoAux1.trad;
                                resultado.trad += resultadoAux2.trad;
                            }
                        else if (resultadoAux1.tipo == "int")
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
                        resultado.trad += idAux;
                        resultado.tipo = "int";
                        
                    }
                else if(buscaTS(token.lexema) == "double")
                    {
                        resultado.trad += idAux;
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
                resultado.trad += token.lexema;
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
                //                System.out.println("ENTRO EN PARI" + resultado);
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
}
//  LocalWords:  sintactico
