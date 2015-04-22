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
import java.util.Stack;
import java.util.ArrayDeque;
class Simbolo
{
    public String nombreVariable;
    public String tipoVariable;

    public Simbolo()
    {
        nombreVariable = "";
        tipoVariable = "";
    }

    public Simbolo(String nombreVariable)
    {
        this.nombreVariable = nombreVariable;
        tipoVariable = "VACIO";
    }

    public Simbolo(String nombreVariable, String tipoVariable)
    {
        this.nombreVariable = nombreVariable;
        this.tipoVariable = tipoVariable;
    }
    @Override
    public String toString()
    {
        return "Nombre Variable: " + nombreVariable + "\nTipo: " + tipoVariable;
        
    }
}

class Ambito
{
    //Se le pasará el nombre entero del ámbito.
    public String nombre;
    public ArrayList<Simbolo> tablaSimbolos;

    public Ambito()
    {
        nombre = "";
        tablaSimbolos = new ArrayList<>();
    }

    public Ambito(String ambito, String nombre)
    {
        if(ambito.equals(""))
            this.nombre = ambito + nombre;
        else 
            this.nombre = ambito +"_"+ nombre;
        tablaSimbolos = new ArrayList<Simbolo>();
    }

    public boolean buscaTS(String variable)
    {
        String ambito = "";
        if(nombre.equals(""))
            ambito = "main_";
        else
            ambito = nombre+"_";
       
        for(Simbolo simbolo : tablaSimbolos){
            if(simbolo.nombreVariable.equals(ambito + variable))
                return true;
        }
        return false;
    }

    public String buscaTSTipo(String variable)
    {
        String ambito = "";
        if(nombre.equals(""))
            ambito = "main_";
        else
            ambito = nombre+"_";
        //        System.out.println("AAAAAA" + nombre);
        //       System.out.println("ENTRO CON : " + variable + "\n" + ambito);
        //        System.out.println(tablaSimbolos);
        for(Simbolo simbolo : tablaSimbolos) {
            //            System.out.println("DAS"+  ambito+variable+ "\n " + simbolo);
            if(simbolo.nombreVariable.equals(ambito + variable) || simbolo.nombreVariable.equals(variable)) {
                //  System.out.println(simbolo+ "\n hola");
                return simbolo.tipoVariable;
            }
            
        }
        return "";
    }

    public boolean anyadirTS(Simbolo nuevoSimbolo)
    {
        Simbolo auxiliar = new Simbolo();
        /*if(buscaTS(nuevoSimbolo.nombreVariable))
          errorSemanticoIdRepetido();*/
        
        if(nombre.equals("") && !nuevoSimbolo.tipoVariable.equals("funcion")) {
            auxiliar.nombreVariable = "main_" + nuevoSimbolo.nombreVariable;
            if(nuevoSimbolo.tipoVariable.equals("VACIO"))
                auxiliar.tipoVariable = "VACIO";
            else 
                auxiliar.tipoVariable = nuevoSimbolo.tipoVariable;
        }
        else if(nombre.equals("") && nuevoSimbolo.tipoVariable.equals("funcion")) {
            auxiliar.nombreVariable = nuevoSimbolo.nombreVariable;
            auxiliar.tipoVariable = nuevoSimbolo.tipoVariable;
        } else {
            //            System.out.println("HOLA");
            auxiliar.nombreVariable = nombre + "_" + nuevoSimbolo.nombreVariable;
            if(nuevoSimbolo.tipoVariable.equals("VACIO"))
                auxiliar.tipoVariable = "VACIO";
            else 
                auxiliar.tipoVariable = nuevoSimbolo.tipoVariable;
        }
        tablaSimbolos.add(auxiliar);
        return true;
    }

    public String arregloHerenciaDerecha(String tipo)
    {
        String resultado = "";
        boolean primeraVariable = true;
        //System.out.println("ENTRO CON: " + tipo);
        for(Simbolo s : tablaSimbolos)
            {
                if(s.tipoVariable.equals("VACIO"))
                    {
                        // System.out.println("ENTRO ALGUNA VEZ?");
                        s.tipoVariable = tipo;
                        if(primeraVariable) {
                            resultado = s.nombreVariable;
                            primeraVariable = false;
                        } else {
                            resultado = resultado + "," + s.nombreVariable;
                        }
                    }
            }
        //PARCHE PARA EVITAR LA COMA DEL FINAL
        /*if(resultado.charAt(resultado.length() -1) == ',')
          resultado = resultado.substring(0,resultado.length()-1);*/
        return resultado;        
    }

    @Override
    public String toString()
    {
        String resultado = "";
        resultado += "Ámbito: " + nombre + "\n";
        for(Simbolo simbolo : tablaSimbolos)
            resultado += simbolo + "\n";
        resultado += "\n";
        
        return resultado;
    }
}

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
        this.tipo= "";
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
        // Tabla de Símbolos. Cada posición determinará como un ámbito anidado. La
    // posición inicial, 0, será el ámbito del main.
    ArrayDeque<Ambito> ambitos;
    //    Hashtable<String,String> ts = new Hashtable<String,String>();

    public TraductorDR()
    {
        flag = true;
        reglasAplicadas = new StringBuilder();
        ambitos = new ArrayDeque<>();
        ambitos.push(new Ambito());
    }

    public TraductorDR(AnalizadorLexico lexico)
    {
        this.lexico = lexico;
        flag = false;
        reglasAplicadas = new StringBuilder();
        ambitos = new ArrayDeque<>();
        //  ambitos.push(new Ambito());
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
        // System.out.println(ambitos);
        System.err.println("Error semantico (" + token.fila + "," + token.columna + "): \'" + token.lexema + "\' no ha sido declarado");
        System.exit(-1);
    }

    public final void errorSemanticoIdRepetido()
    {
        // System.out.println(ambitos);
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
        //System.out.println(ambitos);
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

    private final String ambitoActual()
    {
        return ambitos.peek().nombre;
    }

    private final String nombreAmbitoVariable(String id)
    {
        for(Ambito ambito: ambitos)
            if(!ambito.buscaTSTipo(id).equals(""))
                {
                    return ambito.nombre;
                }
        return "error";
    }
    
    private final boolean buscarSimboloTS(String id)
    {
        //        System.out.println("entro y busco: " + id);
        int i = 0;
        
        /*for(Ambito ambito : ambitos)
            {
        */      //System.out.println("HHH"+i + "    " +ambito);
        if(ambitos.peek().buscaTS(id))
                    return true;
                i++;
                
                // }
        return false;
    }

    private final String getSimboloTS(String id)
    {
        //System.out.println("ENTRO CON: " + id);
        for(Ambito ambito : ambitos)
            if(!ambito.buscaTSTipo(id).equals(""))
                {
                    /* if(id.equals("verde")){
                        System.out.println("HOALAAS");
                        System.out.println(ambito);
                        }*/
                    
                    if(ambito.buscaTSTipo(id).equals("funcion"))
                        errorSemanticoNoEsVariable();
                    
                    return ambito.buscaTSTipo(id);
                }
        
        return "";
    }

    private final boolean buscarFuncionTS(String id)
    {
        for(Ambito ambito : ambitos)
            {
                //if(ambito.buscaTS(id))
                    if(ambito.buscaTSTipo(id).equals("funcion"))
                        return true;
            }
        return false;
    }
    //PREGUNTAR POR SI LAS FUNCIONES TAMBIEN SE CUENTAN EN LOS AMBITOS.
    private final void anyadirTS(String id, String tipo)
    {
        Simbolo auxiliar = new Simbolo();
        //        System.out.println("Anyadir TS: ID: " + id + "\n TIpo: " + tipo);
        if(buscarSimboloTS(id)){//} || buscarFuncionTS(id)) {
            errorSemanticoIdRepetido();
        }
        
        else {
            auxiliar.nombreVariable = id;
            auxiliar.tipoVariable = tipo;
            
            ambitos.peek().anyadirTS(auxiliar);
        }
    }
    
    //CHECK
    private final void nuevoAmbito(String nombreAmbito)
    {
        //       System.out.println("TENGO: " + ambitos);
       if(ambitos.isEmpty()){
           ambitos.push(new Ambito());
           //           System.out.println("ENTRO ASDALJFGAG");
       }
       else
            ambitos.push(new Ambito(ambitoActual(), nombreAmbito));
       //        System.out.println("SALGO CON: " + ambitos);
    }

    private final void borrarAmbito()
    {
        //        System.out.println("ENTRO A BORRAR: " + ambitos.size());//        System.out.println("aaaaaaaa");
        ambitos.pop();
        // System.out.println("SALGO CON: " + ambitos.size());
    }

    // CHECK
    private final String arregloHerenciaDerecha(String tipo)
    {
        String resultado ="";
        //        System.out.println("HASDASDFW" + tipo);
        resultado = ambitos.peek().arregloHerenciaDerecha(tipo);
        
        return resultado;
    }

    private final String buscaTS(String id)
    {
        String resultado = "";
        if(buscarFuncionTS(id)) {
            errorSemanticoNoEsVariable();
        }
        resultado = getSimboloTS(id);
        
        if(resultado.equals(""))
            {
                errorSemanticoNoDeclarado();
            }
        else if(resultado.equals("funcion"))
            {
                errorSemanticoNoEsVariable();
                
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
                trad += "// program " + token.lexema + "\n";
                emparejar(Token.ID);
                trad += "";
                emparejar(Token.PYC);
                nuevoAmbito("main");
                
                trad += Vsp(new Atributos()).trad;
                trad += Bloque(new Atributos("int main() ")).trad;
                //System.out.println(trad);
            }
        else
            errorSintaxis(Token.PROGRAM);
        return trad;
    }

    public final Atributos Vsp(Atributos atributos) // 2 Vsp -> Unsp Vsp_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.FUNCTION || token.tipo == Token.VAR) {
                if(flag)
                    reglasAplicadas.append(" 2");
                Atributos resultadoUnsp = Unsp(atributosAux);
                
                Atributos resultadoVsp_prima = Vsp_prima(atributosAux);
                resultado.trad += resultadoUnsp.trad;
                resultado.trad += resultadoVsp_prima.trad;
        }
        else
            errorSintaxis(Token.FUNCTION,Token.VAR);
        return resultado;
    }

    public final Atributos Vsp_prima(Atributos atributos) // 3 Vsp_prima -> Unsp Vsp_prima ||
                                        // 4 Vsp_prima -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.FUNCTION || token.tipo == Token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 3");
                Atributos resultadoUnsp = Unsp(atributosAux);
                Atributos resultadoVsp_prima = Vsp_prima(atributosAux);
                
                resultado.trad += resultadoUnsp.trad;
                resultado.trad += resultadoVsp_prima.trad;
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

    public final Atributos  Unsp(Atributos atributos) // 5 Unsp -> function id dosp Tipo pyc Vsp
                                   // Bloque pyc | 6 Unsp -> var LV
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.FUNCTION)
            {
                if(flag)
                    reglasAplicadas.append(" 5");
                emparejar(Token.FUNCTION);
                String idAuxFuncion = "";
                if(ambitoActual().equals(""))
                    idAuxFuncion = token.lexema;
                else 
                    idAuxFuncion = ambitoActual() + "_"+ token.lexema;
                anyadirTS(token.lexema,"funcion");
                nuevoAmbito(token.lexema);
                
                emparejar(Token.ID);
                emparejar(Token.DOSP);
                Atributos resultadoTipo = Tipo(atributosAux);
                //resultado.trad += resultadoTipo.trad;
                emparejar(Token.PYC);
                Atributos resultadoVsp = Vsp(atributosAux);
                atributosAux.th = "2"; //Parche para que en las funciones
                                       //siempre ponga los corchetes.
                Atributos resultadoBloque = Bloque(atributosAux);
                emparejar(Token.PYC);
                resultado.trad += resultadoVsp.trad + "" + resultadoTipo.trad + " " + idAuxFuncion + "() " + resultadoBloque.trad;
                //                resultado.trad += "rrrrrrrrrrr"; //QUITAR
                //Termina el ambito de la función.
                borrarAmbito();
            }
        else if(token.tipo == Token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 6");
                emparejar(Token.VAR);
                Atributos resultadoLV = LV(atributos);
                resultado.trad += resultadoLV.trad;
                //                resultado.trad += "jjjjjjjjjjjjj"; //QUITAR
            }
        else errorSintaxis(Token.FUNCTION,Token.VAR);
        
        return resultado;
    }

    public final Atributos LV(Atributos atributos) // 7 LV -> V LV_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en LV");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 7");
                Atributos resultadoV = V(atributosAux);
                //CUIDADO
                resultado.trad += resultadoV.trad;
                Atributos resultadoLV_prima = LV_prima(atributosAux);
                resultado.trad += resultadoLV_prima.trad;
            }
        else errorSintaxis(Token.ID);
        return resultado;
    }

    public final Atributos LV_prima(Atributos atributos) // 8 LV_prima -> V LV_prima || 9 LV_prima -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en LV_prima");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 8");
                Atributos resultadoV = V(atributosAux);
                resultado.trad += resultadoV.trad;
                Atributos resultadoLV_prima = LV_prima(atributosAux);
                resultado.trad += resultadoLV_prima.trad;
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

    public final Atributos  V(Atributos atributos) // 10 V -> id Lid dosp Tipo pyc
    {
        //System.out.println("Entro en V");
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 10");
                
                anyadirTS(token.lexema, "VACIO");
                
                emparejar(Token.ID);
                // WARNING!!
                Atributos resultadoLid = Lid(atributosAux);
                
                
                //String lidAux = Lid(atributosAux).trad;
                emparejar(Token.DOSP);
                Atributos resultadoTipo = Tipo(atributosAux);
                //String tipoAux = Tipo();
                //System.out.println(tipoAux);
                emparejar(Token.PYC);
                String arreglo = arregloHerenciaDerecha(resultadoTipo.trad);
                resultado.trad += resultadoTipo.trad + " " + arreglo + "tirao;" + "\n"; //; tirao
                //                System.out.println("RESULTADO" + resultado.trad);
            }
        else errorSintaxis(Token.ID);
        return resultado;
    }
    
    public final Atributos Lid(Atributos atributos) // 11 Lid ->coma id Lid ||
                                                   // 12 Lid -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en Lid");
        if(token.tipo == Token.COMA)
            {
                if(flag)
                    reglasAplicadas.append(" 11");
                emparejar(Token.COMA);
                // Tenemos que dejarlos sin inicializar ya que es una herencia
                // por la izquierda.
                anyadirTS(token.lexema, "VACIO");
                emparejar(Token.ID);
                Atributos resultadoLid = Lid(atributos);
                resultado.trad = resultadoLid.trad;
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

    public final Atributos  Tipo(Atributos atributos) // 13 Tipo -> Integer || 14 Tipo -> real
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en Tipo");
        if(token.tipo == Token.INTEGER)
            {
                if(flag)
                    reglasAplicadas.append(" 13");
                emparejar(Token.INTEGER);
                resultado.trad = "int";
                //resultado.tipo = "int";
            }
        else if(token.tipo == Token.REAL)
            {
                if(flag)
                    reglasAplicadas.append(" 14");
                emparejar(Token.REAL);
                resultado.trad = "double";
            //    resultado.tipo = "double";
            }
        else errorSintaxis(Token.INTEGER,Token.REAL);
        return resultado;
    }
    //CHECK
    public final Atributos Bloque(Atributos atributos) // 15 Bloque -> begin SInstr end
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en Bloque");
        if (token.tipo == Token.BEGIN) {
            if(flag)
                reglasAplicadas.append(" 15");
            emparejar(Token.BEGIN);
            //PARCHE MAIN
            if(atributos.trad != "")
                {
                    resultado.trad += atributos.trad;
                    atributos.trad = ""; //CAMBIADO
                    
                }
            atributos.th = "2";
            //            System.out.println(ambitos);
            //            resultado.trad += "zzzzzzzzz" + atributos.th; //QUITAR
            Atributos unaInstruccion = SInstr(atributos);
            //System.out.println("BLOQUE : " + unaInstruccion);
            if(unaInstruccion.th == "1"){
                //                resultado.trad += "kkkkkkkkk"; //QUITAR
                resultado.trad += unaInstruccion.trad;
                //                resultado.trad += "lllllllll";//QUITAR
            }
            else{
                //                resultado.trad +="wwwwwwww"; //QUITAR
                //ESTO SI QUE ES UN PARCHE DE LOS BUENOS
                if(unaInstruccion.trad.lastIndexOf('\n') == unaInstruccion.trad.length() -1)
                    resultado.trad += "{\n"+ unaInstruccion.trad + "\n}\n";
                else
                    resultado.trad += "{\n"+ unaInstruccion.trad + ";\n}\n"; //;
                                                                                  //cabra
                //                resultado.trad +="iiiiiiiii"; //QUITAR
            }
            //            resultado.trad += "pppppppppp" + unaInstruccion.th;//QUITAR
            
            //resultado.th = "nopyc";
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
        
        return resultado;
    }

    public final Atributos SInstr(Atributos atributos) // 16 SInstr -> Instr SInstrp
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en SInstr");
        //System.out.println(Token.tipo);
        if (token.tipo == Token.BEGIN || token.tipo == Token.ID || token.tipo == Token.IF || token.tipo == Token.WHILE || token.tipo == Token.WRITELN) {
            if(flag)
                reglasAplicadas.append(" 16");
            Atributos resultadoInstr = Instr(atributosAux);
            resultado.trad += resultadoInstr.trad;
            //            resultado.trad += "fffffff" + atributosAux.th; //QUITAR
            
            atributosAux.th = resultadoInstr.th;
            
            Atributos resultadoSInstrp = SInstrp(atributosAux);
            String comprobarUnaInstruccion = resultadoSInstrp.trad;
                        
            if(comprobarUnaInstruccion == "")
                {
                    resultado.th = "1";
                }
            else
                {
                    resultado.th = "2";
                }
            //PARCHEACO FUNCIONES PARA QUE SIEMPRE TENGAN { }
            if(resultado.th != atributos.th)
                resultado.th = atributos.th;
            //resultado.trad+= " ejufj\n";
            //OJO!
            //            resultado.trad+="bbbbbbbbbbbbb"; //QUITAR
            resultado.trad += comprobarUnaInstruccion;
            //            resultado.trad += "oooooooooooooooo" + resultado.th;//QUITAR
            
            //resultado.trad+= "hhggggg\n";
            //            System.out.println("HOAL");
            //            System.out.println(resultado.trad);
            if(resultado.trad.equals(";"))
                resultado.trad = "";
        }
        else
            errorSintaxis(Token.BEGIN,Token.ID,Token.IF,Token.WHILE,Token.WRITELN);
        return resultado;
    }

    public final Atributos SInstrp(Atributos atributos) // 17 SInstrp -> pyc Instr SInstrp
                                     // 18 SInstrp -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        //  System.out.println("Entro en SInstrp");
        //        resultado.trad+= "qqqqqqqq" + atributos.th; //QUITAR
        if(token.tipo == Token.PYC)
            {
                if(flag)
                    reglasAplicadas.append(" 17");
                emparejar(Token.PYC);
                //Final de cada instruccion un \n
                if(atributos.th.equals("pyc")){
                    atributosAux.th = "pyc"; //PROBLEMA DEL ; DEL IF ESTABA
                                               //AQUI. ANTES: "pyc", la
                                               //solución "nopyc"
                }
                else if(atributos.th.equals("nopyc"))
                    atributosAux.th = "nopyc";
                else
                    System.out.println("HOLA" + atributos.th);
                
                //ARREGLADO EL PROBLEMA CON EL ; DEL IF.
                //resultado.trad += "xxxxxxxx" + atributosAux.th; //QUITAR
                Atributos resultadoInstr = Instr(atributosAux);
                //              resultado.trad += "uuuuuuuuu"; //QUITAR
                //              resultado.trad += resultadoInstr.th;//QUITAR
                
                atributosAux.th = resultadoInstr.th;
                
                //System.out.println("HASDI" + resultadoAux1);
                //if(resultadoAux1.th.equals("nopyc"))
                /*if(atributosAux.th.equals("nopyc"))
                    {
                        // System.out.println("HOLA");
                        
                        //                        System.out.println(resultadoAux1);
                        //   resultado.trad += "\n";
                    }
                    else */if(atributosAux.th.equals("pyc"))
                    resultado.trad += "raton;" + "\n"; //; raton
                
                //resultado.trad += "hoasdasfkjag\n";
                resultado.trad += resultadoInstr.trad;
                //resultado.trad+="jasfjd";
                if(resultadoInstr.th.equals("nopyc"))
                    {
                        atributosAux.th = "nopyc";
                        //atributo.th = "";
                    }
                else if (resultadoInstr.th.equals("pyc"))
                    atributosAux.th = "pyc";
                else
                    atributosAux.th = "nopyc";
                //ARREGLADO EL foto; antes del IF me ponía un ; que no correspondia
                //atributosAux.th = "nopyc"; //QUITAR
                
                Atributos resultadoSInstrp = SInstrp(atributosAux);
                // resultado.trad += "ADIOS" +debug;
                //                resultado.trad += "vvvvvvvv" + atributosAux.th + "   " + resultadoSInstrp.th + "OSTIA"; //QUITAR
                resultado.trad += resultadoSInstrp.trad;
                //                resultado.trad += "ccccccc"; //QUITAR
                resultado.th = resultadoSInstrp.th;
                
                //resultado.trad += "HOLA"+ debug;
                //resultado.th = resultadoAux2.th;
                
                ++debug;
                
                //resultado.th += resultadoAux2.th;
                
                //System.out.println(resultadoAux1);
            }
        else if(token.tipo == Token.END)
            {
                if(flag)
                    reglasAplicadas.append(" 18");
                resultado.th = "nopyc";
                resultado.trad = "";
                
                // SInstrp();
                // resultado.trad = ";";
                
            }
        else errorSintaxis(Token.PYC,Token.END);
        return resultado;
    }

    public final Atributos Instr(Atributos atributos) // 19 Instr -> Bloque
                                    // 20 Instr -> id asig E
                                    // 21 Instr -> if E then Instr Instr_prima
                                    // 24 Instr -> while E do Instr
                                    // 25 Instr -> writeln pari E pard
    {
        Atributos resultado = new Atributos();
         Atributos atributosAux = new Atributos();
         resultado.th = "pyc";
        
         if(token.tipo == Token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 19");
                Atributos resultadoBloque = Bloque(atributosAux);
                
                resultado.trad += resultadoBloque.trad;
                resultado.th = "nopyc";
                
            }
        else if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 20");
                String idAux = "";
                String nombreAmbito = nombreAmbitoVariable(token.lexema);

                if(nombreAmbito.equals(""))
                    idAux = "main_" + token.lexema;
                else 
                    idAux = nombreAmbito +"_"+ token.lexema;
                atributosAux.tipo = buscaTS(token.lexema);
                //Para guardar el posible error
                Token tokenAux = token;
                emparejar(Token.ID);
                Token tokenAuxAsig = token;
                emparejar(Token.ASIG);
                                
                Atributos resultadoE = E(new Atributos());
                if(atributosAux.tipo == "double" && resultadoE.tipo == "int")
                    {
                        resultado.trad += idAux + " " + "=" + "r itor(" + resultadoE.trad +")" ;
                        if(resultado.trad.contains("))"))
                            {
                                resultado.trad = resultado.trad.substring(0,resultado.trad.indexOf("))"))+")";
                            }
                    }
                else if(atributosAux.tipo == "double" && resultadoE.tipo == "double") {
                    resultado.trad += idAux + " " + "=" + "r "+ resultadoE.trad;
                }
                else if(atributosAux.tipo == "int" && resultadoE.tipo == "int") {
                    resultado.trad += idAux + " " + "=" + "i " + resultadoE.trad;
                }
                else{
                    token = tokenAuxAsig;
                    
                    if(resultadoE.tipo.equals("booleano"))
                        errorSemanticoAsigBool();
                    token = tokenAux;
                    errorSemanticoTipoRealAEntero();
                    
                }
                resultado.trad+=";\n";
                resultado.th = "nopyc";
                
                // System.out.println("INSTR: " + resultado);
            }
        else if(token.tipo == Token.IF)
            { //TODO CUIDADO CON LOS { } NO SÉ SI HAY QUE PONERLOS O NO.
                if(flag)
                    reglasAplicadas.append(" 21");
                emparejar(Token.IF);
                /*if(atributos.th.equals("pyc")){
                    //System.out.println("adio");
                    resultado.trad += "foto;\n"; // foto
                    //                    System.out.println("HOLA");
                }
                else{
                    //                    System.out.println("quetal");
                    resultado.trad += "\n";
                    }*/
                
                
                resultado.trad += "\nif";
                Atributos resultadoE = E(atributosAux);
                //PARCHE PARENTESIS
                //System.out.println(resultadoE.trad+ "k");
                if(resultadoE.trad.charAt(0) != '(') {
                    resultado.trad += "(";
                    resultado.trad += resultadoE.trad;
                    
                    resultado.trad += ")";
                }
                else
                    resultado.trad += resultadoE.trad;
                resultado.trad += "\n";
                //                System.out.println("IF!!!");
                emparejar(Token.THEN);
                //resultado.trad += "111111!"; //QUITAR
                Atributos resultadoInstr = Instr(new Atributos());
                resultado.trad += resultadoInstr.trad;
                resultado.th = "nopyc";
                //                resultado.trad += "222222"; //QUITAR
                Atributos resultadoInstr_prima = Instr_prima(new Atributos());
                resultado.trad += resultadoInstr_prima.trad;
                //                resultado.trad += "aaaaa";//QUITAR
                
                
                resultado.th = resultadoInstr_prima.th;
                //                System.out.println(resultado.trad+ "TTEREDJSDEJE");
                //                resultado.trad += "QQQQQQQQ" + resultadoInstr_prima.th;//QUITAR
                //System.out.println(resultado);
            }
        else if(token.tipo == Token.WHILE)
            {
                if(flag)
                    reglasAplicadas.append(" 24");
                //Guardar para posible error.
                Token tokenAux = token;
                emparejar(Token.WHILE);
                resultado.trad += "while ";
                //                System.out.println("WHILE");
                Atributos resultadoE = E(atributos);
                if(!resultadoE.tipo.equals("booleano"))
                    {
                        token = tokenAux;
                        errorSemanticoTipoRelacional();
                    }
                
                
                
                if(resultadoE.trad.charAt(0) != '(') {
                    resultado.trad += "( ";
                    resultado.trad += resultadoE.trad;
                    //resultado.th = "nopyc";
                    
                    resultado.trad += ")\n";
                }
                else
                    resultado.trad += resultadoE.trad;
                emparejar(Token.DO);
                
                Atributos resultadoInstr = Instr(new Atributos());
                resultado.trad += resultadoInstr.trad;
                //cuidado esto!
                // ESTE ES EL DOBLE ;
                //resultado.th = resultadoInstr.th;
                //                if(resultadoInstr.th.equals("pyc"))
                
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
                
                
                Atributos resultadoE = E(atributosAux);
                if(resultadoE.tipo == "int")
                    resultado.trad += "\"%d" +"\\" + "n"+ "\"" + "," +resultadoE.trad;
                else if(resultadoE.tipo == "double")
                    resultado.trad += "\"%g" + "\\" + "n" + "\"" + "," + resultadoE.trad;
                else
                    {
                        token = tokenAux;
                        errorSemanticoImprimirBool();
                    }
                
                emparejar(Token.PARD);
                resultado.trad += ");\n";
                resultado.th = "nopyc";
                
            }
            else errorSintaxis(Token.BEGIN,Token.IF,Token.WHILE,Token.WRITELN,Token.ID);
        return resultado;
        
    }

    public final Atributos  Instr_prima(Atributos atributos) // 22 Instr_prima -> endif
                                          // 23 Instr_prima -> else Instr endif
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en Instr_prima");
        //WARNING!
        
        if(token.tipo == Token.ENDIF)
            {
                if(flag)
                    reglasAplicadas.append(" 22");
                emparejar(Token.ENDIF);
                resultado.trad = "";
                //                resultado.trad += "yyyyyy"; //QUITAR
                
                //resultado.th = atributos.th; CAMBIADO
                resultado.th = "nopyc";
                //resultado.th = "pyc";
                //System.out.println("aad");
            }
        else if(token.tipo == Token.ELSE)
            {
        
                if(flag)
                    reglasAplicadas.append(" 23");
                emparejar(Token.ELSE);
                resultado.trad += "gato;\n";  //; gato
                
                resultado.trad += "else";
                resultado.trad += "\n";
                // resultado.th = "pyc";
                Atributos atributoAuxiliar = new Atributos();
                // atributoAuxiliar.th = "pyc";
                //                resultado.trad += "ggggg"; //QUITAR
                
                Atributos resultadoInstr = Instr(atributos);
                //                resultado.trad += "hhhh" + resultadoInstr.th; //QUITAR
                 
                resultado.trad += resultadoInstr.trad;
                resultado.th = resultadoInstr.th;
                //                resultado.trad += "ttttt" ; //QUITAR
                
                if(resultadoInstr.th.equals("pyc") ) {
                    //                     System.out.println("HOLAS");
                    //                    System.out.println(atributoAuxiliar);
                    resultado.trad += "delta;"; //; delta
                    resultado.th = "nopyc";
                    //System.out.println("HASDAD");
                    //                    System.out.println(resultado);
                }
                
                
                
                // System.out.println(resultado);
                emparejar(Token.ENDIF);
            }
        else errorSintaxis(Token.ELSE, Token.ENDIF);
        resultado.th = "nopyc";
        return resultado;
    }

    public final Atributos E(Atributos atributos) // 26 E -> Expr E_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en E");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 26");
                // resultado.trad += atributos.th;
                Atributos resultadoExpr = Expr(atributos);
                if(resultadoExpr.tipo.equals("double"))
                    atributos.th = "double";
                
                Atributos resultadoE_prima = E_prima(atributos);
                
                //System.out.println("E\n" + resultadoAux1.tipo);
                if(resultadoE_prima.th.equals("double")){
                    resultado.trad += "itor(" + resultadoExpr.trad + ")";
                }
                else 
                    resultado.trad +=  resultadoExpr.trad;
                if(resultadoE_prima.tipo.equals("booleano"))
                    resultado.tipo = resultadoE_prima.tipo;
                else
                    resultado.tipo = resultadoExpr.tipo;
                
                resultado.trad += resultadoE_prima.trad;
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
    public final Atributos E_prima(Atributos atributos) // 27 E_prima -> relop Expr
                                                        // 28 E_prima -> € 
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
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
                emparejar(Token.RELOP);
                 Atributos resultadoExpr = Expr(atributos);
                 if(resultadoExpr.tipo.equals("int")){
                     resultado.th = "int";
                     
                     if(atributos.th.equals("double"))
                         resultado.trad += "r itor(" + resultadoExpr.trad+ ")";
                     else
                         resultado.trad += "i " +resultadoExpr.trad;
                 }
                 
                 else{
                     resultado.th = "double";
                     resultado.trad += "r " +resultadoExpr.trad;
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
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 29");
 
                Atributos resultadoTerm = Term(atributosAux);
                if(resultadoTerm.tipo.equals("double"))
                    atributos.th = "double";
                
                Atributos resultadoExpr_prima = Expr_prima(atributos);
                
                if(resultadoTerm.trad.indexOf("(*i ") != -1){
                    resultadoTerm.trad = resultadoTerm.trad.replace("(*i ", "(");
                }
                resultado.trad += resultadoTerm.trad + " ";
                resultado.tipo = resultadoTerm.tipo;
                
                resultado.trad += resultadoExpr_prima.trad;
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }

    public final Atributos Expr_prima(Atributos atributos) // 30 Expr_prima ->addop Term Expr_prima
                                                            // 31 Expr_prima -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.ADDOP)
            {
                if(flag)
                    reglasAplicadas.append(" 30");
                //Guardamos para posible error.
                Token tokenAux = token;

                emparejar(Token.ADDOP);
                Atributos resultadoTerm = Term(atributosAux);
                Atributos resultadoExpr_prima = Expr_prima(atributos);
                //System.out.println("HOLA");
                if(resultadoTerm.tipo.equals("double") && resultadoExpr_prima.tipo.equals("double")) {
                    resultado.trad += tokenAux.lexema;
                    resultado.tipo = "double";
                    resultado.trad += resultadoTerm.trad;
                    resultado.trad += resultadoExpr_prima.trad;
                }
                else if(atributos.th.equals("double")) {
                    resultado.trad +=tokenAux.lexema;
                    resultado.tipo = "double";
                    resultado.trad += "r itor(" + resultadoTerm.trad + ")";
                    resultado.trad += resultadoExpr_prima.trad;
                    
                      }
                else if(resultadoTerm.tipo.equals("double") && resultadoExpr_prima.tipo.equals("int")) {
                    resultado.trad += tokenAux.lexema;
                    resultado.tipo = "double";
                    resultado.trad += resultadoTerm.trad;
                    resultado.trad += "r itor(" + resultadoExpr_prima.trad + ")";

                }
                else if(resultadoTerm.tipo.equals("int") && resultadoExpr_prima.tipo.equals("int")) {
                    resultado.trad += tokenAux.lexema;
                    resultado.tipo = "int";
                    resultado.trad += "i " +resultadoTerm.trad + " ";
                    
                    resultado.trad += resultadoExpr_prima.trad;
                }
                else if(resultadoTerm.tipo.equals("int") && resultadoExpr_prima.tipo.equals("double")) {
                    resultado.tipo = "int";
                    resultado.trad += tokenAux.lexema;
                    resultado.trad += "i " + resultadoTerm.trad;
                    //PARCHEACO XD realizar itor( con varios int)
                    resultado.trad += ")";
                                        
                    resultado.trad += resultadoExpr_prima.trad;
                }
                else
                    if(resultadoTerm.tipo.equals("double"))
                        {
                            resultado.trad += tokenAux.lexema;
                            resultado.tipo = "double";
                            resultado.trad += "r "+resultadoTerm.trad;
                            resultado.trad += resultadoExpr_prima.trad;
                            
                        }
                    else {
                        resultado.trad += tokenAux.lexema;
                        resultado.tipo = "int";
                        resultado.trad += "i " +resultadoTerm.trad;
                        resultado.trad += resultadoExpr_prima.trad;
                        
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

    public final Atributos  Term(Atributos atributos) // 32 Term -> Factor Term_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        // System.out.println("Entro en Term");
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 32");
                Atributos resultadoFactor = Factor(atributosAux);
                Token auxToken = token;
                
                Atributos resultadoTerm_prima = Term_prima(atributosAux);
                //System.out.println("TERM\n" + resultadoFactor);
                if(resultadoTerm_prima.th.equals("/")) {
                    resultado.tipo = "double";
                        
                    if(resultadoFactor.tipo.equals("int") )
                        resultado.trad += "itor(" + resultadoFactor.trad + ")"+resultadoTerm_prima.trad;
                    else if(resultadoFactor.tipo.equals("double"))
                        resultado.trad += resultadoFactor.trad + resultadoTerm_prima.trad;
                    else
                        resultado.trad += "YA SE ME OCURRIRÁ ALGO";
                        
                }
                else if(resultadoTerm_prima.th.equals("div")){
                    resultado.tipo = "int";
                    if(resultadoFactor.tipo.equals("int") && resultadoTerm_prima.tipo.equals("int"))
                        {
                            resultado.trad += resultadoFactor.trad + resultadoTerm_prima.trad;
                        }
                    else{
                        //System.out.println(auxToken);
                        token = auxToken;
                        errorSemanticoDivEntero();
                    }  
                }
                else if(resultadoTerm_prima.th.equals("*")){
                    //System.out.println("HOALDDADASD");
                    if(resultadoFactor.tipo.equals("double") && resultadoTerm_prima.tipo.equals("double")){
                        resultado.trad += resultadoFactor.trad;
                        resultado.trad += resultadoTerm_prima.trad;
                        resultado.tipo = "double";
                    }
                    else if(resultadoFactor.tipo.equals("double") && resultadoTerm_prima.tipo.equals("int")) {
                        resultado.trad += resultadoFactor.trad + " ";
                        resultado.trad += "*r itor(" + resultadoTerm_prima.trad + ")";
                        resultado.tipo = "double";
                    }
                    else if(resultadoFactor.tipo.equals("int") && resultadoTerm_prima.tipo.equals("double")) {
                        resultado.trad += "itor(" + resultadoFactor.trad + ")";
                        resultado.trad += resultadoTerm_prima.trad;
                        resultado.tipo = "double";
                        
                    }
                    
                    else{
                        resultado.tipo = "int";
                        resultado.trad += resultadoFactor.trad + resultadoTerm_prima.trad;
                        
                    }
                }
                else {
                    //System.out.println("HOAA" + resultadoFactor + resultadoTerm_prima);
                    if(resultadoFactor.tipo == resultadoTerm_prima.tipo)
                    {
                        resultado.trad += resultadoFactor.trad;
                        resultado.tipo = resultadoFactor.tipo;
                        resultado.trad += resultadoTerm_prima.trad;
                    }
                    else
                    {
                        if(resultadoFactor.tipo == "int" && resultadoTerm_prima.tipo == "double")
                            {
                                resultado.tipo = "double";
                                resultado.trad = "itor(" + resultadoFactor.trad;
                                resultado.trad = ")";
                                resultado.trad = resultadoTerm_prima.trad;
                            }
                        else if(resultadoFactor.tipo == "double" && resultadoTerm_prima.tipo == "int")
                            {
                                resultado.tipo = "double";
                                //System.out.println("NETRO");
                            }
                        else
                            {
                                resultado.trad += resultadoFactor.trad;
                                resultado.tipo = resultadoFactor.tipo;
                                resultado.trad += resultadoTerm_prima.trad;
                                
                            }
                    }
                
                }
                
            }
            else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
    }

    //AQUÍ HAY MUCHA TELA. COMPROBACIONES por * / div
    public final Atributos  Term_prima(Atributos atributos) // 33 Term_prima -> mulop Factor Term_prima
                                                           // 34 Term_prima -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        //System.out.println("Entro en Term_prima");
        if(token.tipo == Token.MULOP)
            {
                if(flag)
                    reglasAplicadas.append(" 33");
                String idAux = token.lexema;
                emparejar(Token.MULOP);
                Atributos resultadoFactor = Factor(atributosAux);
                Atributos resultadoTerm_prima = Term_prima(atributos);
                //
                
                if(idAux.equals("/")) {
                    resultado.trad += idAux;
                    resultado.th = "/";
                    
                    if(resultadoFactor.tipo == "double")
                        {
                            resultado.trad += resultadoFactor.trad;
                            resultado.tipo = resultadoFactor.tipo;
                        }
                    else if(resultadoFactor.tipo == "int")
                        {
                            resultado.trad += "r itor(" + resultadoFactor.trad + ")";
                            resultado.tipo = "double";
                        }
                    else
                        System.out.println("ESTOY JODIDO");
                }
                else if( idAux.equals("div"))
                    {
                        resultado.th = "div";
                        resultado.trad += "/";
                        
                        if(resultadoFactor.tipo == "int") {// && resultadoTerm_prima.tipo == "int")
                            resultado.tipo = "int";
                            resultado.trad += "i "+ resultadoFactor.trad;
                        }
                        //else
                        //  errorSemanticoDivEntero();
                        
                    }
                else if(idAux.equals("*"))
                    {
                       resultado.trad += idAux;
                        //                        System.out.println("VOY A MULTIPLICAR");
                        resultado.th = "*";
                        if(resultadoFactor.tipo == "double")
                            {
                                
                                resultado.tipo = "double";
                                resultado.trad += "r " +resultadoFactor.trad;
                                resultado.trad += resultadoTerm_prima.trad;
                            }
                        else if (resultadoFactor.tipo == "int")
                            {
                                resultado.tipo = "int";
                                resultado.trad += "i " + resultadoFactor.trad;
                                resultado.trad += resultadoTerm_prima.trad;
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

    public final Atributos  Factor(Atributos atributos) // 35 Factor -> id
                                     // 36 Factor -> nentero
                                     // 37 Factor -> nreal
                                     // 38 Factor -> pari Expr pard
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
         //System.out.println("Entro en Factor");
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 35");
                String idAux = "";
                String nombreAmbito = nombreAmbitoVariable(token.lexema);

                /*if(!nombreAmbito.equals(""))
                    {
                        System.out.println("HASFDJASFJA"+nombreAmbito);
                        }*/
                if(nombreAmbito.equals(""))
                    idAux = "main_" + token.lexema;
                else 
                    idAux = nombreAmbito +"_"+ token.lexema;
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
                /*else if(buscaTS(token.lexema) == "funcion")
                    {
                        errorSemantic
                        }*/
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
                Atributos resultadoExpr = Expr(atributosAux);
                
                resultado.trad += "(" + resultadoExpr.trad + ")";
                resultado.tipo = resultadoExpr.tipo;
                
                emparejar(Token.PARD);
                //                System.out.println("ENTRO EN PARI" + resultado);
            }
            else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
}
//  LocalWords:  sintactico
