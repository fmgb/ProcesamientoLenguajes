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
    
        for(Simbolo simbolo : tablaSimbolos) {
            if(simbolo.nombreVariable.equals(ambito + variable) || simbolo.nombreVariable.equals(variable)) {
                return simbolo.tipoVariable;
            }
            
        }
        return "";
    }

    public boolean anyadirTS(Simbolo nuevoSimbolo)
    {
        Simbolo auxiliar = new Simbolo();
        
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

        for(Simbolo s : tablaSimbolos)
            {
                if(s.tipoVariable.equals("VACIO"))
                    {
                        s.tipoVariable = tipo;
                        if(primeraVariable) {
                            resultado = s.nombreVariable;
                            primeraVariable = false;
                        } else {
                            resultado = resultado + "," + s.nombreVariable;
                        }
                    }
            }
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
    Token auxToken;
        
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
        int i = 0;
        
        if(ambitos.peek().buscaTS(id))
                    return true;
        i++;
                
        return false;
    }

    private final String getSimboloTS(String id)
    {
        for(Ambito ambito : ambitos)
            if(!ambito.buscaTSTipo(id).equals(""))
                {
                    if(ambito.buscaTSTipo(id).equals("funcion")) {
                        errorSemanticoNoEsVariable();
                    }
                    return ambito.buscaTSTipo(id);
                }
        return "";
    }

    private final boolean buscarFuncionTS(String id)
    {
        for(Ambito ambito : ambitos)
            {
                if(ambito.buscaTSTipo(id).equals("funcion"))
                    return true;
            }
        return false;
    }
    //PREGUNTAR POR SI LAS FUNCIONES TAMBIEN SE CUENTAN EN LOS AMBITOS.
    private final void anyadirTS(String id, String tipo)
    {
        Simbolo auxiliar = new Simbolo();
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
       if(ambitos.isEmpty()){
           ambitos.push(new Ambito());
       }
       else
            ambitos.push(new Ambito(ambitoActual(), nombreAmbito));
    }

    private final void borrarAmbito()
    {
        ambitos.pop();
    }

    // CHECK
    private final String arregloHerenciaDerecha(String tipo)
    {
        String resultado ="";
        resultado = ambitos.peek().arregloHerenciaDerecha(tipo);
        return resultado;
    }

    private final String buscaTS(String id)
    {
        String resultado = "";

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
                emparejar(Token.PYC);
                Atributos resultadoVsp = Vsp(atributosAux);
                atributosAux.th = "2"; //Parche para que en las funciones
                                       //siempre ponga los corchetes.
                Atributos resultadoBloque = Bloque(atributosAux);
                emparejar(Token.PYC);
                resultado.trad += resultadoVsp.trad + "" + resultadoTipo.trad + " " + idAuxFuncion + "() " + resultadoBloque.trad;
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
            }
        else errorSintaxis(Token.FUNCTION,Token.VAR);
        
        return resultado;
    }

    public final Atributos LV(Atributos atributos) // 7 LV -> V LV_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 7");
                Atributos resultadoV = V(atributosAux);
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
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 10");
                
                anyadirTS(token.lexema, "VACIO");
                
                emparejar(Token.ID);

                Atributos resultadoLid = Lid(atributosAux);
                emparejar(Token.DOSP);

                Atributos resultadoTipo = Tipo(atributosAux);
                emparejar(Token.PYC);
                String arreglo = arregloHerenciaDerecha(resultadoTipo.trad);
                resultado.trad += resultadoTipo.trad + " " + arreglo + ";" + "\n"; //; tirao
            }
        else errorSintaxis(Token.ID);
        return resultado;
    }
    
    public final Atributos Lid(Atributos atributos) // 11 Lid ->coma id Lid ||
                                                   // 12 Lid -> €
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();

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

    public final Atributos Bloque(Atributos atributos) // 15 Bloque -> begin SInstr end
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();

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
            Atributos unaInstruccion = SInstr(atributos);

            if(unaInstruccion.th == "1"){
                resultado.trad += unaInstruccion.trad;
            }
            else{
                //ESTO SI QUE ES UN PARCHE DE LOS BUENOS
                if(unaInstruccion.trad.lastIndexOf('\n') == unaInstruccion.trad.length() -1)
                    resultado.trad += "{\n"+ unaInstruccion.trad + "\n}\n";
                else
                    resultado.trad += "{\n"+ unaInstruccion.trad + ";\n}\n"; //;
            }
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
        
        return resultado;
    }

    public final Atributos SInstr(Atributos atributos) // 16 SInstr -> Instr SInstrp
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();

        if (token.tipo == Token.BEGIN || token.tipo == Token.ID || token.tipo == Token.IF || token.tipo == Token.WHILE || token.tipo == Token.WRITELN) {
            if(flag)
                reglasAplicadas.append(" 16");
            Atributos resultadoInstr = Instr(atributosAux);
            resultado.trad += resultadoInstr.trad;
            
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
            
            resultado.trad += comprobarUnaInstruccion;
            
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
                
                Atributos resultadoInstr = Instr(atributosAux);
                atributosAux.th = resultadoInstr.th;
                
                resultado.trad += resultadoInstr.trad;
                
                if(resultadoInstr.th.equals("nopyc"))
                    {
                        atributosAux.th = "nopyc";
                    }
                else if (resultadoInstr.th.equals("pyc"))
                    atributosAux.th = "pyc";
                else
                    atributosAux.th = "nopyc";
                Atributos resultadoSInstrp = SInstrp(atributosAux);

                resultado.trad += resultadoSInstrp.trad;
                resultado.th = resultadoSInstrp.th;
                
                ++debug;
            }
        else if(token.tipo == Token.END)
            {
                if(flag)
                    reglasAplicadas.append(" 18");
                resultado.th = "nopyc";
                resultado.trad = "";
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

                Atributos resultadoE = E(atributosAux);
                if(atributosAux.tipo == "double" && resultadoE.tipo == "int")
                    {
                        resultado.trad += idAux + " " + "=" + "r itor(" + resultadoE.trad +")" ;
                    }
                else if(atributosAux.tipo == "double" && resultadoE.tipo == "double") {
                    resultado.trad += idAux + " " + "=" + "r "  + resultadoE.trad;
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
                
            }
        else if(token.tipo == Token.IF)
            { //TODO CUIDADO CON LOS { } NO SÉ SI HAY QUE PONERLOS O NO.
                if(flag)
                    reglasAplicadas.append(" 21");
                emparejar(Token.IF);
                resultado.trad += "\n" + "if";
                Atributos resultadoE = E(atributosAux);
                //PARCHE PARENTESIS
                if(resultadoE.trad.charAt(0) != '(') {
                    resultado.trad += "(";
                    resultado.trad += resultadoE.trad;
                    
                    resultado.trad += ")";
                }
                else
                    resultado.trad += resultadoE.trad;
                resultado.trad += "\n";
                emparejar(Token.THEN);

                Atributos resultadoInstr = Instr(new Atributos());
                resultado.trad += resultadoInstr.trad;
                resultado.th = "nopyc";

                Atributos resultadoInstr_prima = Instr_prima(new Atributos());
                resultado.trad += resultadoInstr_prima.trad;
                resultado.th = resultadoInstr_prima.th;
            }
        else if(token.tipo == Token.WHILE)
            {
                if(flag)
                    reglasAplicadas.append(" 24");
                //Guardar para posible error.
                Token tokenAux = token;
                emparejar(Token.WHILE);
                resultado.trad += "while ";

                Atributos resultadoE = E(atributos);
                if(!resultadoE.tipo.equals("booleano"))
                    {
                        token = tokenAux;
                        errorSemanticoTipoRelacional();
                    }

                if(resultadoE.trad.charAt(0) != '(') {
                    resultado.trad += "( ";
                    resultado.trad += resultadoE.trad;
                    resultado.trad += ")\n";
                }
                else
                    resultado.trad += resultadoE.trad;
                emparejar(Token.DO);
                
                Atributos resultadoInstr = Instr(new Atributos());
                resultado.trad += resultadoInstr.trad;
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
                if(resultadoE.tipo.equals("int"))
                    resultado.trad += "\"%d" +"\\" + "n"+ "\"" + "," +resultadoE.trad;
                else if(resultadoE.tipo.equals("double"))
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

        if(token.tipo == Token.ENDIF)
            {
                if(flag)
                    reglasAplicadas.append(" 22");
                emparejar(Token.ENDIF);
                resultado.trad = "";
                resultado.th = "nopyc";
            }
        else if(token.tipo == Token.ELSE)
            {
        
                if(flag)
                    reglasAplicadas.append(" 23");
                emparejar(Token.ELSE);
                            
                resultado.trad += "else";
                resultado.trad += "\n";
                Atributos atributoAuxiliar = new Atributos();
                Atributos resultadoInstr = Instr(atributos);
                                 
                resultado.trad += resultadoInstr.trad;
                resultado.th = resultadoInstr.th;
                                
                if(resultadoInstr.th.equals("pyc") ) {
                    resultado.trad += ";"; //; delta
                    resultado.th = "nopyc";
                }

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
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 26");
                Atributos resultadoExpr = Expr(atributosAux);
                Atributos resultadoE_prima = E_prima(resultadoExpr);
                if(resultadoE_prima.tipo.equals(""))
                    return resultadoExpr;
                
                if(resultadoE_prima.tipo.equals("booleano")){
                    resultado.tipo = "booleano";
                    if(resultadoExpr.tipo.equals("int") && resultadoE_prima.th.equals("double"))
                        resultado.trad += "itor(" + resultadoExpr.trad + ")";
                    else
                        resultado.trad += resultadoExpr.trad;
                    resultado.trad += resultadoE_prima.trad;
                }
                else if(resultadoExpr.tipo.equals("int") && resultadoE_prima.tipo.equals("double")) {
                        resultado.trad += "itor(" + resultadoExpr.trad + ")";
                        resultado.trad += resultadoE_prima.trad;
                        resultado.tipo = "double";
                }
                else if(resultadoExpr.tipo.equals("double") && resultadoE_prima.tipo.equals("double")) {
                        resultado.trad += resultadoExpr.trad;
                        resultado.trad += resultadoE_prima.trad;
                        resultado.tipo = "double";
                }
                else if(resultadoExpr.tipo.equals("int") && resultadoE_prima.tipo.equals("int")) {
                    resultado.trad += resultadoExpr.trad;
                    resultado.trad += resultadoE_prima.trad;
                    resultado.tipo = "int";
                } else if(resultadoExpr.tipo.equals("double") && resultadoE_prima.tipo.equals("int")) {
                    resultado.trad +=  resultadoExpr.trad;
                    resultado.trad += resultadoE_prima;
                    
                    resultado.tipo = "double";
                }
                else //case booleano
                    {
                        resultado.tipo = "notipo";
                        resultado.trad += resultadoExpr.trad;
                        resultado.trad += resultadoE_prima.trad;
                    }
            }
        else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
    }
    
    public final Atributos E_prima(Atributos atributos) // 27 E_prima -> relop Expr
                                                        // 28 E_prima -> € 
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        //DEBUG        System.out.println("Entro en E_prima");
        if(token.tipo == Token.RELOP)
            {
                if(flag)
                    reglasAplicadas.append(" 27");
                String signo = "";
                //Traducciones de diferencia de lenguajes
                if(token.lexema.equals("="))
                    signo = "==";
                else if(token.lexema.equals("<>"))
                    signo = "!=";
                else
                    signo = token.lexema;
                //System.out.println("SIGNO: " + signo);
                emparejar(Token.RELOP);
                Atributos resultadoExpr = Expr(atributos);
                resultado.trad += signo;
                //System.out.println(resultadoExpr);
                if(atributos.tipo.equals("double") && resultadoExpr.tipo.equals("int"))
                    {
                        resultado.th = "double";
                        resultado.trad += "r itor(" + resultadoExpr.trad + ")";
                    }
                else if(atributos.tipo.equals("double") && resultadoExpr.tipo.equals("double"))
                    {
                        resultado.th = "double";
                        resultado.trad += "r "+ resultadoExpr.trad;
                    }
                else if(atributos.tipo.equals("int") && resultadoExpr.tipo.equals("double"))
                    {
                        resultado.th = "double";
                        resultado.tipo = "double";
                        //PARCHEEEEEEEE PONER UN "r " +
                        resultado.trad += "r " +resultadoExpr.trad;
                    }
                else
                    {
                        resultado.th = "int";
                        resultado.trad += "i " + resultadoExpr.trad;
                    }
                resultado.tipo = "booleano";
                //System.out.println(resultado.trad);
            }
        else if(token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 28");
                resultado.trad += "";//atributos.trad;
                //resultado.tipo = atributos.tipo;
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
                
                Atributos resultadoTerm = Term(new Atributos());

                if(resultadoTerm.tipo.equals("int") && atributosAux.tipo.equals("double")) {
                    atributosAux.trad +=  resultadoTerm.trad;
                    atributosAux.trad += atributosAux.trad;
                    atributosAux.tipo = "double";
                }
                else if(resultadoTerm.tipo.equals("double") && atributosAux.tipo.equals("double")) {
                        atributosAux.trad +=  resultadoTerm.trad;
                        atributosAux.trad += atributosAux.trad;
                        atributosAux.tipo = "double";
                }
                else if(resultadoTerm.tipo.equals("int") && atributosAux.tipo.equals("int")) {
                    atributosAux.trad +=  resultadoTerm.trad;
                    atributosAux.trad += atributosAux.trad;
                    atributosAux.tipo = "int";
                } else if(resultadoTerm.tipo.equals("double") && atributosAux.tipo.equals("int")) {
                    atributosAux.trad +=  resultadoTerm.trad;
                    atributosAux.trad += atributosAux.trad;
                    atributosAux.tipo = "double";
                }
                else {
                    atributosAux = resultadoTerm;
                }
                Atributos resultadoExpr_prima = Expr_prima(atributosAux);
                resultado = resultadoExpr_prima;
                
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
                Atributos auxiliar = atributos;
                atributos.th = tokenAux.lexema;
                Atributos resultadoTerm = Term(atributos);
                atributos.th = "";

                if(resultadoTerm.tipo.equals("int") && atributos.tipo.equals("double")) {
                    atributosAux.trad += "itor( " + resultadoTerm.trad+ ")";
                    atributosAux.tipo = "double";
                }
                else if(resultadoTerm.tipo.equals("double") && atributos.tipo.equals("double")) {
                    atributosAux.trad += resultadoTerm.trad;
                        atributosAux.tipo = "double";
                }
                else if(resultadoTerm.tipo.equals("int") && atributos.tipo.equals("int")) {
                    atributosAux.trad += resultadoTerm.trad;
                    atributosAux.tipo = "int";
                } else if(resultadoTerm.tipo.equals("double") && atributos.tipo.equals("int")) {
                    atributosAux.trad += resultadoTerm.trad;
                    atributosAux.tipo = "double";
                }
                else{
                    if(resultadoTerm.tipo.equals("double"))
                        atributosAux.trad += resultadoTerm.trad;
                    else if(resultadoTerm.tipo.equals("int"))
                        atributosAux.trad += resultadoTerm.trad;
                    
                    atributosAux.th = resultadoTerm.th;
                    atributosAux.tipo = resultadoTerm.tipo;
                }
                Atributos resultadoExpr_prima = Expr_prima(atributosAux);
                resultado = resultadoExpr_prima;
                
            }
        else if(token.tipo == Token.RELOP || token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 31");
                resultado.trad = atributos.trad;
                resultado.tipo = atributos.tipo;
                resultado.th = atributos.th;
                
            }
        else errorSintaxis(Token.ADDOP, Token.RELOP, Token.PYC, Token.ENDIF, Token.ELSE, Token.END, Token.THEN, Token.DO, Token.PARD);
        return resultado;
    }

    public final Atributos Term(Atributos atributos) // 32 Term -> Factor Term_prima
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        boolean division = true;
        
        if(token.tipo == Token.ID || token.tipo == Token.NENTERO || token.tipo == Token.NREAL || token.tipo == Token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 32");
                
                Atributos resultadoFactor = Factor(atributos);
                auxToken = token;
                if(token.lexema.equals("/"))
                    {
                        if(atributos.tipo.equals("int") && resultadoFactor.tipo.equals("int"))
                            {
                                atributosAux.trad = "itor(" + atributos.trad + ")" + atributos.th + "r itor(" + resultadoFactor.trad + ")";
                                atributosAux.th = "division";
                                atributosAux.tipo = "double";
                                division = false;
                            }
                    }
                if(atributos.tipo.equals("int") && resultadoFactor.tipo.equals("double")) {
                    atributosAux.trad = " itor(" + atributos.trad + ")"+ atributos.th +"r "+ resultadoFactor.trad;
                    atributosAux.tipo = "double";
                }
                else if(atributos.tipo.equals("double") && resultadoFactor.tipo.equals("double")) {
                    atributosAux.trad = "r " + atributos.trad +atributos.th + resultadoFactor.trad;
                    atributosAux.tipo = "double";
                }
                else if(division && atributos.tipo.equals("int") && resultadoFactor.tipo.equals("int")) {
                    atributosAux.trad =  atributos.trad + atributos.th +"i "+ resultadoFactor.trad;
                    atributosAux.tipo = "int";
                } else if(atributos.tipo.equals("double") && resultadoFactor.tipo.equals("int")) {
                    atributosAux.trad = atributos.trad + atributos.th +"r itor( " + resultadoFactor.trad + ")";
                    atributosAux.tipo = "double";
                } else {
                    if(division) {
                        atributosAux.trad = resultadoFactor.trad;
                        atributosAux.th = resultadoFactor.th;
                        atributosAux.tipo = resultadoFactor.tipo;
                    }
                }
                Atributos resultadoTerm_prima = Term_prima(atributosAux);
                if(resultadoTerm_prima.th.equals("div"))
                    {
                        if(!resultadoFactor.tipo.equals("int") || !resultadoTerm_prima.tipo.equals("int")) {
                            token = auxToken;
                            errorSemanticoDivEntero();
                        }
                    }
                resultado = resultadoTerm_prima;
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
        //        System.out.println("Entro en term_prima");
        if(token.tipo == Token.MULOP)
            {
                if(flag)
                    reglasAplicadas.append(" 33");
                String idAux = token.lexema;
                emparejar(Token.MULOP);
                Atributos resultadoFactor = Factor(atributos);
               
                if(idAux.equals("/")) {
                    atributosAux.th = "/";
                    atributosAux.tipo = "double";
                    // ResultadoFactor es el Divisor
                    // Los atributos son el dividendo
                    if(resultadoFactor.tipo.equals("int") && atributos.tipo.equals("double")) {
                        atributosAux.trad = "itor(" + resultadoFactor.trad+ ")"  + atributosAux.trad;
                        if(atributos.th.equals("division"))
                            atributosAux.trad =  atributos.trad + "/" + "r " + atributosAux.trad;
                        else
                            atributosAux.trad =  "r " +atributos.trad + "/" + "r " + atributosAux.trad;
                    }
                    else if(resultadoFactor.tipo.equals("double") && atributos.tipo.equals("double")) {
                        atributosAux.trad = "r " +resultadoFactor.trad + atributosAux.trad;
                        atributosAux.trad = "r " + atributos.trad + "/" + atributosAux.trad;
                    }
                    else if(resultadoFactor.tipo.equals("int") && atributos.tipo.equals("int")) {
                        atributosAux.trad = "itor(" + resultadoFactor.trad + ")"+ atributosAux.trad;
                        atributosAux.trad = "itor(" + atributos.trad + ")" + "/"+ "r " + atributosAux.trad; 
                    } else if(resultadoFactor.tipo.equals("double") && atributos.tipo.equals("int")) {
                        atributosAux.trad = "r " +resultadoFactor.trad + atributosAux.trad;
                        atributosAux.trad = "itor(" + atributos.trad + ")" + "/" + atributosAux.trad;
                    }
                    else if (atributos.tipo.equals(""))
                        {
                            if(resultadoFactor.tipo.equals("int"))
                                atributosAux.trad += "itor(" + resultadoFactor.trad + ")";
                            else if(resultadoFactor.tipo.equals("double"))
                                atributosAux.trad +="r " + resultadoFactor.trad;
                            atributosAux.tipo = "double";
                        }
                    else
                        System.out.println("ERROR DIVISION");
                }
                else if(idAux.equals("div"))
                    {
                        atributosAux.th = "div";

                        atributosAux.tipo = "int";

                        if(resultadoFactor.tipo.equals("int") && atributos.tipo.equals("double")) {
                            atributosAux.trad = "itor(" + resultadoFactor.trad+ ")"  + atributosAux.trad;
                            atributosAux.trad = "r " + atributos.trad + "/" + atributosAux.trad;
                            token = auxToken;

                            errorSemanticoDivEntero();
                        }
                        else if(resultadoFactor.tipo.equals("double") && atributos.tipo.equals("double")) {
                            atributosAux.trad = "r " +resultadoFactor.trad + atributosAux.trad;
                            atributosAux.trad = "r " + atributos.trad + "/" +atributosAux.trad;
                            token = auxToken;
                            
                            errorSemanticoDivEntero();
                        }
                        else if(resultadoFactor.tipo.equals("int") && atributos.tipo.equals("int")) {
                            atributosAux.trad =  resultadoFactor.trad + atributosAux.trad;
                            atributosAux.trad =  atributos.trad + "/" + "i " +atributosAux.trad;
                            
                        } else if(resultadoFactor.tipo.equals("double") && atributos.tipo.equals("int")) {
                            atributosAux.trad = "r " + resultadoFactor.trad + atributosAux.trad;
                            atributosAux.trad = "itor(" + atributos.trad + ")" + "/" + atributosAux.trad;
                            token = auxToken;
                            
                            errorSemanticoDivEntero();
                        }
                        else if (atributos.tipo.equals(""))
                            {
                                if(resultadoFactor.tipo.equals("int"))
                                    atributosAux.trad += "itor(" + resultadoFactor.trad + ")";
                                else if(resultadoFactor.tipo.equals("double"))
                                    atributosAux.trad += "r " + resultadoFactor.trad;
                                atributosAux.tipo = "double";
                                token = auxToken;
                                errorSemanticoDivEntero();
                            }                        
                    }
                else if(idAux.equals("*"))
                    {
                        if(resultadoFactor.tipo.equals("int") && atributos.tipo.equals("double")) {
                            atributosAux.trad = "r itor(" + resultadoFactor.trad+ ")"  + atributosAux.trad;
                            atributosAux.trad =  atributos.trad + "*" + atributosAux.trad;
                            atributosAux.tipo = "double";
                        }
                        else if(resultadoFactor.tipo.equals("double") && atributos.tipo.equals("double")) {
                            atributosAux.trad = "r " +resultadoFactor.trad + atributosAux.trad;
                            atributosAux.trad = "r " + atributos.trad + "*" + atributosAux.trad;
                            atributosAux.tipo = "double";
                        }
                        else if(resultadoFactor.tipo.equals("int") && atributos.tipo.equals("int")) {
                            atributosAux.tipo = "int";
                            atributosAux.trad = resultadoFactor.trad+ atributosAux.trad;
                            atributosAux.trad = atributos.trad + "*"+ "i " + atributosAux.trad; 
                        } else if(resultadoFactor.tipo.equals("double") && atributos.tipo.equals("int")) {
                            atributosAux.tipo = "double";
                            atributosAux.trad = "r " + resultadoFactor.trad + atributosAux.trad;
                            atributosAux.trad = "itor(" + atributos.trad + ")" + "*" + atributosAux.trad;
                        }
                        else if (atributos.tipo.equals(""))
                            {
                                if(resultadoFactor.tipo.equals("int"))
                                    atributosAux.trad += "itor(" + resultadoFactor.trad + ")";
                                else if(resultadoFactor.tipo.equals("double"))
                                    atributosAux.trad += "r " + resultadoFactor.trad;
                                atributosAux.tipo = "double";
                            }
                        else
                            System.out.println("ERROR MULTIPLICACION");
                         
                    }
                
                Atributos resultadoTerm_prima = Term_prima(atributosAux);
                resultado.trad += resultadoTerm_prima.trad;
                resultado.tipo = resultadoTerm_prima.tipo;
                resultado.th = resultadoTerm_prima.th;
            }
        else if(token.tipo == Token.ADDOP || token.tipo == Token.RELOP || token.tipo == Token.PYC || token.tipo == Token.ENDIF || token.tipo == Token.ELSE || token.tipo == Token.END || token.tipo == Token.THEN || token.tipo == Token.DO || token.tipo == Token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 34");
                resultado = atributos;
                return resultado;
            }
        else errorSintaxis(Token.MULOP,Token.ADDOP, Token.RELOP, Token.PYC, Token.ENDIF, Token.ELSE, Token.END, Token.THEN, Token.DO, Token.PARD);
        return resultado;
    }

    public final Atributos  Factor(Atributos atributos) // 35 Factor -> id
                                     // 36 Factor -> nentero
                                     // 37 Factor -> nreal
                                     // 38 Factor -> pari Expr pard
    {
        Atributos resultado = new Atributos();
        Atributos atributosAux = new Atributos();
        
        if(token.tipo == Token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 35");
                String idAux = "";
                String nombreAmbito = nombreAmbitoVariable(token.lexema);

                if(nombreAmbito.equals(""))
                    idAux = "main_" + token.lexema;
                else 
                    idAux = nombreAmbito +"_"+ token.lexema;
                if(buscaTS(token.lexema) == "int")
                    {
                        resultado.trad +=  idAux;
                        resultado.tipo = "int";
                    }
                else if(buscaTS(token.lexema) == "double")
                    {
                        resultado.trad +=  idAux;
                        resultado.tipo = "double";
                    }
                emparejar(Token.ID);
            }
        else if(token.tipo == Token.NENTERO)
            {
                if(flag)
                    reglasAplicadas.append(" 36");
                resultado.trad += token.lexema+ " ";
                resultado.tipo = "int";
                
                emparejar(Token.NENTERO);
            }
        else if(token.tipo == Token.NREAL)
            {
                if(flag)
                    reglasAplicadas.append(" 37");
                resultado.trad +=  token.lexema+ " ";
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
            }
            else errorSintaxis(Token.ID, Token.NENTERO, Token.NREAL, Token.PARI);
        return resultado;
        
    }
}
//  LocalWords:  sintactico
