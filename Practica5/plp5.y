/**
   Autor: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 4
   Licencia: GPLv3
   Creado el: 25/04/2015
*/

/*------------------------------ plp4.y -------------------------------*/
%token tpari
%token tpard
%token tcori
%token tcord
%token tllavei
%token tllaved
%token tmulop
%token taddop
%token trelop
%token tpyc
%token tdosp
%token tcoma
%token tasig
%token tvar
%token treal
%token tinteger
%token tprogram
%token tbegin
%token tend
%token treferencia
%token tformato
%token tmain
%token tfloat
%token tint
%token tprintf
%token tscanf
%token tif
%token telse
%token tid
%token tfunction
%token twhile
%token tdo
%token twriteln
%token tnentero
%token tnreal

%{

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <vector>
#include <sstream>

  //      #define DEBUG 0
using namespace std;

#include "comun.h"

// variables y funciones del A. Léxico
extern int ncol,nlin,findefichero;


extern int yylex();
extern char *yytext;
extern FILE *yyin;


int yyerror(char *s);
/* Tabla de Simbolos */
void anyadirAmbito(string nombreAmbito);
void anyadirSimbolo(Simbolo simboloAux);
void anyadirSimbolo(string nombre, int tipo);
Simbolo obtenerSimbolo(string simbolo);
Simbolo obtenerSimboloEnAmbito(Ambito ambitoAux, string simbolo);
void borrarAmbito();
void asignarTipo(int tipo);
void imprimirAmbito(Ambito ambito);
void imprimirTablaSimbolos();
 void imprimirTablaTipos();
 int obtenerTipoTTipos(int tipo);
 int obtenerTBaseTTipos(int tipo);
 int obtenerTTamTTipos(int tipo);
/* Fin Tabla de simbolos */
/* Tabla de Tipos */
 int anyadirTTipos(int tipo, int tam, int tbase);
 TTipo obtenerTipoBasico(int tipo);
 bool esArray(int tipo);
 /* Fin Tabla de Tipos */

 
 int nTmp();
 void initMem();
 bool rellenarMemoria(int memoriaNecesaria);

void split(const string& s, char c,
           std::vector<string>& v) {
   string::size_type i = 0;
   string::size_type j = s.find(c);

   while (j != string::npos) {
      v.push_back(s.substr(i, j-i));
      i = ++j;
      j = s.find(c, j);

      if (j == string::npos)
         v.push_back(s.substr(i, s.length()));
   }
}
 
 int numVariables = 0;
 int numMemoria = 0;
 int etiquetaActual = 0;
 string operador, s1, s2;  // string auxiliares
 Simbolo simboloAux1, simboloAux2; // Simbolos Auxiliares
 std::vector<Ambito> tablaSimbolos;
 std::vector<TTipo> tablaTipos;
  
 int nuevaEtiqueta()
 {
   etiquetaActual++;
   return etiquetaActual;
 }

 string iToS(int n)
 {
   stringstream ss;
   ss << n;
   return ss.str();
 }

 int ncolCorchete = 0;
 int nlinCorchete = 0;
 
 %}

%%



S : FVM    { /* comprobar que después del programa
                no hay ningún token más */
#ifdef DEBUG
  std::cout<<"Entero en S" <<std::endl;
#endif
  int tk = yylex();
    if (tk != 0) yyerror("");
    $$.cod = $1.cod + "halt\n";
    
    std::cout<<$$.cod <<std::endl;
    
 };

FVM : DVar FVM
{
    #ifdef DEBUG
    std::cout <<"Entro en Vsp: Vsp Unsp" <<std::endl;
    #endif
    $$.cod = $1.cod + $2.cod;
};

FVM : tint tmain tpari tpard { anyadirAmbito("main");} Bloque
{
    #ifdef DEBUG
    std::cout <<"Entro en Vsp : Unsp" <<std::endl;
    #endif
    $$.cod = $6.cod;
    borrarAmbito();
};

Tipo : tint
{
    // CREAR AMBITO
#ifdef DEBUG
    std::cout <<"Entro en Unsp : Function tid tdosp" <<std::endl;
#endif
    $$.tipo = NENTERO;
};

Tipo : tfloat
{
#ifdef DEBUG
    std::cout <<"Entro en Unsp : Unsp : tvar LV" <<std::endl;
#endif
    $$.tipo = NREAL;
};

Bloque : tllavei BDecl SeqInstr tllaved
{
#ifdef DEBUG
    std::cout <<"Entro en LV : LV V" <<std::endl;
#endif
    $$.cod = $2.cod + $3.cod;
    
};

BDecl : BDecl DVar
{
#ifdef DEBUG
    std::cout <<"Entro en LV : V" <<std::endl;
#endif

    $$.cod = $1.cod + $2.cod;
};

BDecl : 
{
#ifdef DEBUG
    std::cout <<"Entro en V : Lid dosp Tipo pyc" <<std::endl;
#endif
};

DVar : Tipo LIdent tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Lid : Lid coma tid" <<std::endl;
#endif
    asignarTipo($1.tipo);
};

LIdent : LIdent tcoma Variable
{
#ifdef DEBUG
    std::cout <<"Entro en Lid : id" <<std::endl;
#endif
    $$.cod = $1.cod + $2.cod + "\n ; ESTOY EN LIDENT. SI ANYADO UN a";
};

LIdent : Variable
{
#ifdef DEBUG
    std::cout <<"Entro en Tipo : integer" <<std::endl;
#endif
    $$.cod = $1.cod;
    $$.tipo = $1.tipo;
    $$.dir = $1.dir;
};

Variable : tid {if(obtenerSimboloEnAmbito(tablaSimbolos.back(),$1.lexema).nombre != "") msgError(ERRYADECL,$1.nlin,$1.ncol,$1.lexema); } V
{
#ifdef DEBUG
    std::cout <<"Entro en Tipo : real" <<std::endl;
#endif
    if($3.tipo == ARRAY)
      {
        std::vector<string> stringsAux;
        split($3.cod,'|',stringsAux);
        int tbaseAux = 0;
        int posTTipo = 0;
        
        for(int i = stringsAux.size()-1; i >=0; --i)
          {
            
            if(stringsAux[i] != "")
              {
               
                tbaseAux = anyadirTTipos(ARRAY,atoi(stringsAux[i].c_str()),tbaseAux);
                if(i == 0)
                  posTTipo = tbaseAux;
              }
          }
        anyadirSimbolo($1.lexema, posTTipo);
        if(!rellenarMemoria($3.dbase))
          {
            msgError(ERR_NOCABE,$1.nlin,$1.ncol,$1.lexema);
            
          }
      }
    else 
      {
        anyadirSimbolo($1.lexema,NVACIO);
        $$.cod = $1.lexema;
      }
    
};

V :
{
#ifdef DEBUG
    std::cout <<"Entro en V: " <<std::endl;
#endif
    $$.dbase = 1;
    $$.tipo = NVACIO;
};

V : tcori tnentero tcord V
{
#ifdef DEBUG
    std::cout <<"Entro en  V : tcori tnentero tcord V" <<std::endl;
#endif
    int i = atoi($2.lexema.c_str());
    if(i < 1)
      {
        msgError(ERRDIM,$2.nlin,$2.ncol,$2.lexema);
      }
    $$.dbase = $4.dbase * i;
    $$.tipo = ARRAY;
    $$.cod = $2.lexema + "|" + $4.cod;
};

SeqInstr: SeqInstr Instr
{
#ifdef DEBUG
    std::cout <<"Entro en SeqInstr : SeqInstr Instr" <<std::endl;
#endif
    $$.cod = $1.cod + $2.cod;
    //std::cout<<"SeqInstr " <<$$.cod <<std::endl;
    
};

SeqInstr :
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : Bloque" <<std::endl;
#endif
    $$.cod = "";
};

//CHECK
Instr : tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : tpyc" <<std::endl;
#endif
    $$.cod = "";
};

Instr : Bloque
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : if E then Instr ColaIf" <<std::endl;
#endif
    $$.cod = $1.cod;
    $$.dir = $1.dir;
    $$.tipo = $1.tipo;
    $$.dbase = $1.dbase;
};

//FALTA LOS ERRORES DE LOS ARRAY
//CHECK Posibilidades ASIG
Instr : Ref {if(obtenerTipoBasico(obtenerSimbolo($1.lexema).tipo).tipo == FUNCION || obtenerTipoBasico(obtenerSimbolo($1.lexema).tipo).tipo == ARRAY) { /*imprimirTablaTipos(); std::cout <<"INTR: ASIG" <<$1.nlin <<std::endl <<$1.lexema <<std::endl;*/ msgError(ERREOF,$1.nlin,$1.ncol,$1.lexema);}} tasig Expr {if (obtenerSimbolo($4.lexema).tipo == ARRAY){/*cout <<"INTR: ASIG 2" <<std::endl;*/ msgError(ERREOF,$1.nlin,$1.ncol, $1.lexema);}} tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : Ref tasig Expr tpyc " <<std::endl;
#endif
    int tmp = nTmp();
    TTipo tipoTmp = obtenerTipoBasico($1.tipo);
    $$.dir = tmp;
    $$.cod = $1.cod + $4.cod; //Anyadir el codigo que viene de la expresion que
                      //queremos evaluar.
    if($1.tipo == NREAL && $4.tipo == NENTERO)
      {
        $$.tipo = NREAL;
        $$.cod += "mov " + iToS($4.dir);
        $$.cod += " A; Empezamos la asignacion ENTERO a REAL\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + iToS(tmp);
        $$.cod += "\n";
      }
    else if($1.tipo == NENTERO && $4.tipo == NREAL)
      {
        $$.tipo = NENTERO;
        $$.cod += "mov " + iToS($4.dir);
        $$.cod += " A; Empezamos la asignacion REAL a ENTERO\n";
        $$.cod += "rtoi\n";
        $$.cod += "mov A " + iToS(tmp);
        $$.cod += "\n";
      }
    $$.cod += "mov #0 " + iToS(tmp) +"\n";
    
    $$.cod += "mov " + iToS($1.dir);
    $$.cod += " A\n";
    $$.cod += "muli #" + iToS(tipoTmp.tam);
    $$.cod += "\n";
    $$.cod += "addi #" + iToS($1.dbase) + "; Instr: Ref asig\n";
    $$.cod += "mov @A " + iToS(tmp);
    $$.cod += ";Asig\n";
};

//CHECK ERRORS
Instr : tprintf tpari tformato tcoma Expr { if(esArray($5.tipo)){ /*std::cout <<"INTR: PRINT" <<std::endl;*/ msgError(ERREOF,$5.nlin,$5.ncol,$5.lexema);} } tpard tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : tprintf tpari tformato tcoma Expr tpard tpyc" <<std::endl;
#endif
    int tmp = nTmp();
    $$.cod = "";
    if($3.lexema == "%d" && $5.tipo == NREAL)
      {
        $$.cod = "mov " + iToS($5.dir) + "A; Print";
        $$.cod += "atoi; Realizo la conversion a entero.\n";
        $$.cod += "mov A " + iToS(tmp);
        $$.cod += "\n";
      }
    else if($3.lexema == "%g" && $5.tipo == NENTERO)
      {
        $$.cod = "mov " + iToS($5.dir) + "A; Print";
        $$.cod += "itor;Relizo la conversion a real.\n";
        $$.cod += "mov A " + iToS(tmp);
        $$.cod += "\n";
      }
    else
      {
        tmp = $5.dir;
      }
    $$.cod += "wr";
    if($3.lexema == "%d")
      $$.cod += "i";
    else
      $$.cod += "r";
    $$.cod += " " + iToS(tmp);
    $$.cod += "\n";
    $$.cod += "wrl\n";
    
};

//CHECK ERRORRRRS
Instr : tscanf tpari tformato tcoma treferencia Ref { if(esArray($6.tipo)){/*std::cout<<"SCANF" <<std::endl;*/ msgError(ERREOF,$6.nlin,$6.ncol,$6.lexema); }} tpard tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : while E do Instr" <<std::endl;
#endif
        int tmp = nTmp();
    if($3.lexema == "\"%d\"" && $6.tipo == NREAL)
      {
        $$.cod += "mov " + iToS($6.dir);
        $$.cod += " A; Realizo la conversion antes de leer REAL a ENT\n";
        $$.cod += "atoi\n";
        $$.cod += "mov A " + iToS(tmp);
        $$.cod += "\n";
      }
    else if($3.lexema == "\"%g\"" && $6.tipo == NENTERO)
      {
        $$.cod += "mov " + iToS($6.dir);
        $$.cod += " A; Realizo la conversion antes de imprimir ENT a R\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + iToS(tmp);
        $$.cod += "\n";
      }
    else
      {
        tmp = $6.dir;
      }
    $$.cod += "rd";
    if($3.lexema == "%d")
      $$.cod += "i";
    else
      $$.cod += "r";
    $$.cod += " " + iToS(tmp);
    $$.cod += "\n";
};

//TODO :Realizar comprobaciones oportunas
// TODO QUITAR ERROR
Instr : tif tpari Expr {if($3.tipo != NENTERO){/* POR PONER UNO. NO SE
                                                 ESPECIFICA EN EL ENUNCIADO*/ msgError(ERRDIM,$3.nlin, $3.ncol,$3.lexema);} } tpard {anyadirAmbito("IF"+numMemoria);} Instr {borrarAmbito();}Instr_prima
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : tif tpari Expr tpard Instr Instr_prima" <<std::endl;
#endif
    string s1 = "L" + iToS(nuevaEtiqueta());
    string s2 = "L" + iToS(nuevaEtiqueta());
    $$.cod += "mov " + iToS($3.dir) +" A; Realizamos el if\n";
    $$.cod += "jz " + s1;
    $$.cod += "\n";
    $$.cod += $7.cod;
    if($6.cod == "")
      {
        $$.cod += s1 + "\n";
      }
    else
      {
        $$.cod += "jmp " + s2 + "\n";
        $$.cod += s1 + "\n; Realizamos con else.";
        $$.cod += $9.cod;
        $$.cod += s2 + "\n";
      }
    
};

Instr_prima : telse {anyadirAmbito("ELSE" + numMemoria);} Instr
{
#ifdef DEBUG
  std::cout<<"Entro e Instr_prima : telse Instr"
#endif
    $$.cod += $3.cod + "\n";
  borrarAmbito();
  
}

Instr_prima : 
{
#ifdef DEBUG
    std::cout <<"Entro en Instr_prima : " <<std::endl;
#endif
    $$.cod = "";
};

//TODO : Realizar comprobaciones de Expr.
//TODO Probar declaracion de variables aqui.
Instr : twhile tpari Expr tpard Instr
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : twhile tpari Expr tpard Instr" <<std::endl;
#endif
    string s1 = "L" + nuevaEtiqueta();
    string s2 = "L" + nuevaEtiqueta();
    $$.cod += s1 + "\n";
    $$.cod += $3.cod;
    $$.cod += "mov " + iToS($3.dir);
    $$.cod += " A\n";
    $$.cod += "jz " + s2;
    $$.cod += $5.cod;
    $$.cod += "jmp " + s1;
    $$.cod += s2 + "\n";
    
};

Expr : Expr trelop Esimple
{
#ifdef DEBUG
    std::cout <<"Entro en Expr : Expr trelop Esimple" <<std::endl;
#endif
    string operacion = "";
    int tmp = nTmp();
    
    if($2.lexema == "==")
      {
        operacion = "eql";
      }
    else if($2.lexema == "!=")
      {
        operacion = "neq";
      }
    else if($2.lexema == ">")
      {
        operacion = "gtr";
      }
    else if($2.lexema == "<")
      {
        operacion = "lss";
      }
    else if($2.lexema == ">=")
      {
        operacion = "geq";
      }
    else if($2.lexema == "<=")
      {
        operacion = "leq";
      }
    $$.tipo = NENTERO;
    
    $$.cod += $1.cod + $3.cod;
    if($1.tipo == NENTERO && $3.tipo == NENTERO)
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A; Cargo el primer termino en A\n";
        $$.cod += operacion;
        $$.cod += "i " + iToS($3.dir);
        $$.cod += "; Realizamos la operacion entera total.\n";
      }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A; Cargo el primer termino en A\n";
        $$.cod += "itor; Realizo la conversión del primer termino\n";
        $$.cod += operacion;
        $$.cod += "r " + iToS($3.dir);
        $$.cod += ";Realizamos la operacion entera- real\n";
      }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
      {
        int tmpAux = nTmp();
        $$.cod += "mov " + iToS($3.dir);
        $$.cod += " A; Cargamos el segundo operando.\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + tmpAux;
        $$.cod += "; Guardamos el segundo termino convertido.\n";
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A\n";
        $$.cod += operacion;
        $$.cod += "r " + tmpAux;
        $$.cod += "; Realizamos la operacion real.\n";
      }
    else // REAL && REAL
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A;Cargamos el primer operando.";
        $$.cod += operacion;
        $$.cod += "r " + iToS($3.dir);
        $$.cod += "\n";
      }
    $$.cod += "mov A " + iToS(tmp);
    $$.cod += "; Lo cargamos en la dir correspondiente.\n";

    
};

Expr : Esimple
{
#ifdef DEBUG
    std::cout <<"Entro en Expr : ESimple" <<std::endl;
#endif
    $$.cod = $1.cod;
    $$.tipo = $1.tipo;
    $$.dir = $1.dir;
};

//CHECK
//Bajo mi punto de vista esta regla es la misma que la de Term : Term mulop Factor
Esimple : Esimple taddop Term
{
#ifdef DEBUG
    std::cout <<"Entro en Esimple : Esimple taddop Term" <<std::endl;
#endif
    int tmp = nTmp();
    $$.dir = tmp;
    string operacion = "";
    
     if($2.lexema == "+")
          {
            operacion = "add";
          }
        else
          operacion = "sub";
     
     $$.cod += $1.cod + $3.cod;
     if($1.tipo == NENTERO && $3.tipo == NENTERO)
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A; Cargo el primer termino en A\n";
        $$.cod += operacion;
        $$.cod += "i " + iToS($3.dir);
        $$.cod += "; Realizamos la operacion entera total.\n";
        $$.tipo = NENTERO;
      }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A; Cargo el primer termino en A\n";
        $$.cod += "itor; Realizo la conversión del primer termino\n";
        $$.cod += operacion;
        $$.cod += "r " + iToS($3.dir);
        $$.cod += ";Realizamos la operacion entera- real\n";
        $$.tipo = NREAL;
      }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
      {
        int tmpAux = nTmp();
        $$.cod += "mov " + iToS($3.dir);
        $$.cod += " A; Cargamos el segundo operando.\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + iToS(tmpAux);
        $$.cod += "; Guardamos el segundo termino convertido.\n";
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A\n";
        $$.cod += operacion;
        $$.cod += "r " + iToS(tmpAux);
        $$.cod += "; Realizamos la operacion real.\n";
        $$.tipo = NREAL;
      }
    else // REAL && REAL
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A;Cargamos el primer operando.";
        $$.cod += operacion;
        $$.cod += "r " + iToS($3.dir);
        $$.cod += "\n";
        $$.tipo = NREAL;
      }
     $$.cod += "mov A " + iToS(tmp);
     $$.cod += "; Lo cargamos en la dir correspondiente.\n";
};

Esimple : Term
{
#ifdef DEBUG
    std::cout <<"Entro en Esimple : Term" <<std::endl;
#endif
    $$.cod = $1.cod;
    $$.tipo = $1.tipo;
    $$.dir = $1.dir;
};

Term : Term tmulop Factor
{
#ifdef DEBUG
    std::cout <<"Entro en Term : Term tmulop Factor" <<std::endl;
#endif
    int tmp = nTmp();
    $$.dir = tmp;
    string operacion = "";
    
     if($2.lexema == "*")
          {
            operacion = "mul";
          }
        else
          operacion = "div";
     $$.cod += $1.cod + $2.cod;
     if($1.tipo == NENTERO && $3.tipo == NENTERO)
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A; Cargo el primer termino en A\n";
        $$.cod += operacion;
        $$.cod += "i " + iToS($3.dir);
        $$.cod += "; Realizamos la operacion entera total.\n";
        $$.tipo = NENTERO;
      }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A; Cargo el primer termino en A\n";
        $$.cod += "itor; Realizo la conversión del primer termino\n";
        $$.cod += operacion;
        $$.cod += "r " + iToS($3.dir);
        $$.cod += ";Realizamos la operacion entera- real\n";
        $$.tipo = NREAL;
      }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
      {
        int tmpAux = nTmp();
        $$.cod += "mov " + iToS($3.dir);
        $$.cod += " A; Cargamos el segundo operando.\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + tmpAux;
        $$.cod += "; Guardamos el segundo termino convertido.\n";
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A\n";
        $$.cod += operacion;
        $$.cod += "r " + tmpAux;
        $$.cod += "; Realizamos la operacion real.\n";
        $$.tipo = NREAL;
      }
    else // REAL && REAL
      {
        $$.cod += "mov " + iToS($1.dir);
        $$.cod += " A;Cargamos el primer operando.";
        $$.cod += operacion;
        $$.cod += "r " + iToS($3.dir);
        $$.cod += "\n";
        $$.tipo = NREAL;
      }
     $$.cod += "mov A " + iToS(tmp);
     $$.cod += "; Lo cargamos en la dir correspondiente.\n";
};

Term : Factor
{
#ifdef DEBUG
    std::cout <<"Entro en Term : Factor" <<std::endl;
#endif
    $$.cod = $1.cod;
    $$.dir = $1.dir;
    $$.tipo = $1.tipo;
};

Factor : Ref
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : Ref" <<std::endl;
#endif
    // std::cout<<"FACTOR: " <<$1.lexema <<std::endl;
    
    if(ARRAY == obtenerTipoTTipos($1.tipo))
      {
        msgError(ERRFALTAN,nlinCorchete, ncolCorchete, $1.lexema);
      }
        
    int tmp = nTmp();
    // TTipo tipoTmp = obtenerTipoTTipos($1.tipo);
    $$.dir = tmp;
    $$.cod += $1.cod + "\nmov ";
    $$.cod += iToS($1.dir);
    $$.cod += " A; Factor : Ref \n";
    $$.cod += "muli #" + iToS(obtenerTTamTTipos($1.tipo));
    $$.cod += "; Multiplicamos por el tamanyo del vector\n";
    $$.cod += "addi #" + iToS($1.dbase);
    $$.cod += ";Le sumo un tipo";
    $$.cod += "\n";
    $$.cod += "mov @A " + iToS(tmp);
    $$.cod += "\n";
    $$.tipo = $1.tipo;
};

Factor : tnentero
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : tnentero" <<std::endl;
#endif

    $$.tipo = NENTERO;
    int tmp = nTmp();
    
    $$.dir = tmp;
    $$.cod += "mov #" + $1.lexema + " ";
    $$.cod += iToS(tmp);
    $$.cod += "; Guardo un numero Entero\n";
};

Factor : tnreal
{
#ifdef DEBUG
  std::cout <<"Entro en Factor: tnreal" <<std::endl;
  #endif
  $$.tipo = NREAL;
  int tmp = nTmp();
   
  $$.dir = tmp;
  $$.cod += "mov $" + $1.lexema;
  $$.cod += " ";
  stringstream ss;
  ss << tmp;
  
  $$.cod += ss.str();
  $$.cod += "; Guardo un numero Real\n";
}

//No estoy muy convencido de esto.
Factor : tpari Expr tpard
{
#ifdef DEBUG
  std::cout <<"Entro en Factor: tpari Expr tard" <<std::endl;
#endif
  $$.cod = ";Factor : tpari Expr tpard\n";
  $$.tipo = $2.tipo;
  $$.dir = nTmp();
  $$.cod += $2.cod;
  $$.cod += "\n";
}

Ref : tid
{
  Simbolo sim  = obtenerSimbolo($1.lexema);
  if(sim.nombre == "")
    msgError(ERRNODECL,$1.nlin,$1.ncol,$1.lexema);
  int tmp = nTmp();
  if(tmp == -1)
    msgError(ERR_MAXTMP,$1.nlin,$1.ncol,$1.lexema);
  
  $$.dir = tmp;
  $$.tipo = sim.tipo;    
  $$.dbase = sim.dir;
  $$.cod += "mov #0 " + iToS($$.dir) + "; Estoy en Ref:tid\n";
}

//TODO Cambiar Error
Ref : Ref tcori {if(ARRAY != obtenerTipoTTipos($1.tipo)){/*std::cout <<"cori " <<$1.tipo <<std::endl;*/ msgError(ERRSOBRAN, $2.nlin, $2.ncol, $1.lexema);} } Esimple tcord 
{
  //std::cout<<"REF: REF cori: " <<$1.lexema <<std::endl;
  if ($4.tipo != NENTERO)
    {
      msgError(ERR_EXP_ENT,$5.nlin,$5.ncol,$5.lexema);
    }
  ncolCorchete = $5.ncol;
  nlinCorchete = $5.nlin;
  $$.tipo = obtenerTBaseTTipos($1.tipo);
  $$.dbase = $1.dbase;
  int tmp = nTmp();
  if (tmp == -1)
    msgError(ERR_MAXTMP,$1.nlin,$1.ncol,$1.lexema);
  $$.dir = tmp;
  $$.cod += $1.cod + $4.cod;
  $$.cod += "mov " + iToS($1.dir);
  $$.cod += " A; [ ]\n";
  $$.cod += "muli #" + iToS(obtenerTTamTTipos($1.tipo)) + "\n";
  $$.cod += "addi " + iToS($4.dir);
  $$.cod += "; \n";
  $$.cod += "mov A ";
  $$.cod += iToS($$.dir) + "\n";
  
  //$$.tipo = obtenerTipoTTipos($1.tipo);
  //    std::cout<<"Soy de tipo " <<$$.tipo <<std::endl;
  
}

%%

void msgError(int nerror,int nlin,int ncol,const string s)
{
     switch (nerror) {
     case ERRLEXICO: fprintf(stderr,"Error lexico (%d,%d): caracter '%s' incorrecto\n",nlin,ncol,s.c_str());
            break;
     case ERRSINT: fprintf(stderr,"Error sintactico (%d,%d): en '%s'\n",nlin,ncol,s.c_str());
            break;
         case ERREOF: fprintf(stderr,"Error sintactico: fin de fichero inesperado\n");
            break;
         case ERRLEXEOF: fprintf(stderr,"Error lexico: fin de fichero inesperado\n");
            break;
         default:
            fprintf(stderr,"Error semantico (%d,%d): ", nlin,ncol);
            switch(nerror) {
            case ERRYADECL: fprintf(stderr,"simbolo '%s' ya declarado\n",s.c_str());
               break;
            case ERRNODECL: fprintf(stderr,"identificador '%s' no declarado\n",s.c_str());
               break;
             case ERRDIM: fprintf(stderr,"la dimension debe ser mayor que cero\n");
               break;
             case ERRFALTAN: fprintf(stderr,"faltan indices\n");
               break;
             case ERRSOBRAN: fprintf(stderr,"sobran indices\n");
               break;
             case ERR_EXP_ENT: fprintf(stderr,"la expresion entre corchetes debe ser de tipo entero\n");
               break;

            case ERR_NOCABE:fprintf(stderr,"la variable '%s' ya no cabe en memoria\n",s.c_str());
               break;
            case ERR_MAXVAR:fprintf(stderr,"en la variable '%s', hay demasiadas variables declaradas\n",s.c_str());
               break;
             case ERR_MAXTIPOS:fprintf(stderr,"hay demasiados tipos definidos\n");
               break;
             case ERR_MAXTMP:fprintf(stderr,"no hay espacio para variables temporales\n");
               break;
            }
        }
     exit(1);
}

void anyadirAmbito(string nombreAmbito)
{
    Ambito nuevoAmbito;
    if(tablaSimbolos.back().nombre == "")
        nuevoAmbito.nombre = nombreAmbito;
    else 
        nuevoAmbito.nombre = tablaSimbolos[tablaSimbolos.size() -1].nombre + "_" + nombreAmbito;
    nuevoAmbito.posicionMemoriaInicio = numMemoria;
    nuevoAmbito.variablesInicio = numVariables;
    tablaSimbolos.push_back(nuevoAmbito);
}

void anyadirSimbolo(Simbolo simboloAux)
{
    tablaSimbolos[tablaSimbolos.size() -1].simbolos.push_back(simboloAux);
}

void anyadirSimbolo(string nombre, int tipo)
{
  if(numVariables > MAXVAR)
    msgError(ERR_MAXVAR,0,0,"AAAAA PAYOOO");

  Simbolo s;
    s.nombre = nombre;
    s.tipo = tipo;
    s.dir = nTmp();
    if(tablaSimbolos.back().nombre == "")
        s.ambito = "main";
    else 
        s.ambito = tablaSimbolos.back().nombre;
    tablaSimbolos.back().simbolos.push_back(s);
    numVariables++;
}

//CHECK
Simbolo obtenerSimboloEnAmbito(Ambito ambitoAux, string simbolo)
{
     for(int i = 0; i < ambitoAux.simbolos.size();i++)
        {
            if(ambitoAux.simbolos[i].nombre == simbolo)
                return ambitoAux.simbolos[i];
        }
     Simbolo s;
     return s;
}

Simbolo obtenerSimbolo(string simbolo)
{
    Simbolo simboloAux;
    // Recorremos la tabla de simbolos desde el ultimo ambito introducido hasta
    // el inicial.
    for(int i = tablaSimbolos.size()-1; i >=0; i--)
        {
            simboloAux = obtenerSimboloEnAmbito(tablaSimbolos[i],simbolo);
            if(simboloAux.nombre != "")
                {
                    return simboloAux;
                }
        }
     simboloAux.nombre = "";
     return simboloAux;
}

void asignarTipo(int tipo)
{
  for(int i = 0; i < tablaSimbolos.back().simbolos.size(); i++)
        {
            if(tablaSimbolos.back().simbolos[i].tipo == NVACIO)
                {
                  tablaSimbolos.back().simbolos[i].tipo = tipo;
                }
            
        }
  for(int i = 0; i < tablaTipos.size(); ++i)
    {
      if(tablaTipos[i].tbase == NVACIO)
        {
          tablaTipos[i].tbase = tipo;
          
        }
    }
}

void borrarAmbito()
{
  //int memoriaAmbito = tablaSimbolos.back().simbolos.front().dir;
  //numMemoria -= memoriaAmbito;
  numMemoria = tablaSimbolos.back().posicionMemoriaInicio;
  numVariables = tablaSimbolos.back().variablesInicio;
  //std::cout<<"HA BORRADO AMBITO" <<endl;
  tablaSimbolos.pop_back();
}

void imprimirAmbito(Ambito ambito)
{
     std::cout <<"Ambito: \n\tNombre:\t " <<ambito.nombre <<std::endl;
     std::cout <<"\tSimbolos:" <<std::endl;
     for(int i = 0; i < ambito.simbolos.size(); i++)
          std::cout<< "\t\t" <<i <<": " <<ambito.simbolos[i].nombre <<"\t" <<ambito.simbolos[i].tipo <<"\t" <<ambito.simbolos[i].dir <<std::endl; 
}

void imprimirTablaTipos()
{
  std::cout<<"TABLA DE TIPOS " <<std::endl;
  
  for(int i = 0; i < tablaTipos.size(); i++)
    {
      std::cout<< "\t\t" <<i <<": " <<tablaTipos[i].tipo <<"\t" <<tablaTipos[i].tam << "\t" <<tablaTipos[i].tbase <<std::endl;
    }

}


void imprimirTablaSimbolos()
{
     for(int i = 0; i < tablaSimbolos.size(); i++)
        imprimirAmbito(tablaSimbolos[i]);
}


int anyadirTTipos(int tipo, int tam, int tbase)
{
  TTipo entrada;
  entrada.tipo = tipo;
  entrada.tam = tam;
  entrada.tbase = tbase;
  tablaTipos.push_back(entrada);
  return tablaTipos.size()-1;
}


void eliminarSimboloTTipos(int tipo)
{
  int tipoAux = tipo;
  //Borro el elemento.
  tablaTipos.erase(tablaTipos.begin()+tipo);
  //Borro todos los que los referencian y los que referencian a estos últimos.
  int i = 0;
  while(i < tablaTipos.size())
    {
      if(tablaTipos[i].tbase == tipoAux)
        {
          tipoAux = i;
          tablaTipos.erase(tablaTipos.begin()+tipoAux);
        }
      i++;
    }
}

int obtenerTipoTTipos(int tipo)
{
  return tablaTipos[tipo].tipo;
}

int obtenerTBaseTTipos(int tipo)
{
  return tablaTipos[tipo].tbase;
}

 int obtenerTTamTTipos(int tipo)
 {
   return tablaTipos[tipo].tam;
 }

//CHECK
TTipo obtenerTipoBasico(int tipo)
{
  TTipo resultado;
  int tipoAux = tipo;
  while(true)
    {
      if(!esArray(tablaTipos[tipoAux].tipo))
        {
          resultado = tablaTipos[tipoAux];
          return resultado;
        }
      else
        tipoAux = tablaTipos[tipoAux].tbase;
    }
  return resultado;
}

bool esArray(int tipo)
 {
   //   std::cout<<"TIPO es ARRAY" <<tipo <<std::endl;
   
   if(tipo == ARRAY)
     {
       return true;
     }
   return false;
   
 }

bool rellenarMemoria(int memoriaNecesaria)
{
  int memoriaAux = numMemoria + memoriaNecesaria;
  if(memoriaAux > MAXMEM)
    return false;
  numMemoria = memoriaAux;
  return true;
}

int nTmp()
{
  if(numMemoria > MAXMEM)
  //LANZAR ERROR FALTA MEMORIA.
    msgError(ERR_MAXTMP,1,2,"FALTAAAAA");
  numMemoria++;
  return numMemoria;
}

int yyerror(char *s)
{
    if (findefichero) 
    {
        msgError(ERREOF,0,0,"");
    }
    else
    {
        msgError(ERRSINT,nlin,ncol-strlen(yytext),yytext);
    }
}

int main(int argc,char *argv[])
{
    FILE *fent;

    if (argc==2)
    {
        fent = fopen(argv[1],"rt");
        if (fent)
        {
          Ambito ambitoMain;
          ambitoMain.nombre = "";
          tablaSimbolos.push_back(ambitoMain);
          TTipo tipoVacio;
          tipoVacio.tipo = NVACIO;
          tipoVacio.tam = 1;
          tipoVacio.tbase = -1;
          tablaTipos.push_back(tipoVacio);
          TTipo tipoEntero;
          tipoEntero.tipo = NENTERO;
          tipoEntero.tam = 1;
          tipoEntero.tbase = -1;
          tablaTipos.push_back(tipoEntero);
          TTipo tipoReal;
          tipoReal.tipo = NREAL;
          tipoReal.tbase = -1;
          tipoReal.tam = 1;
          tablaTipos.push_back(tipoReal);
          yyin = fent;
          yyparse();
          fclose(fent);
        }
        else
            fprintf(stderr,"No puedo abrir el fichero\n");
    }
    else
        fprintf(stderr,"Uso: ejemplo <nombre de fichero>\n");
}
