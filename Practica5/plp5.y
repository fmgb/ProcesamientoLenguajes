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


    //#define DEBUG 0
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
Simbolo buscaSimbolo(string simbolo);
Simbolo buscaSimboloEnAmbito(Ambito ambitoAux, string simbolo);
void borrarAmbito();
void asignarTipo(int tipo);
void imprimirAmbito(Ambito ambito);
void imprimirTablaSimbolos();

/* Fin Tabla de simbolos */
/* Tabla de Tipos */
 void anyadirTTipos(string nombre, int tipo, int tam, int tbase);
 TTipo sacarTTipo(string lexema);
 /* Fin Tabla de Tipos */

int nTmp();
 
string operador, s1, s2;  // string auxiliares
 Simbolo simboloAux1, simboloAux2; // Simbolos Auxiliares
 std::vector<Ambito> tablaSimbolos;
 std::vector<TTipo> tablaTipos;
 
 %}

%%



S : FVM    { /* comprobar que después del programa
                no hay ningún token más */
#ifdef DEBUG
  std::cout<<"Entero en S" <<std::endl;
#endif
  int tk = yylex();
    if (tk != 0) yyerror("");
 };

FVM : DVar FVM
{
    #ifdef DEBUG
    std::cout <<"Entro en Vsp: Vsp Unsp" <<std::endl;
    #endif
};

FVM : tint tmain tpari tpard Bloque
{
    #ifdef DEBUG
    std::cout <<"Entro en Vsp : Unsp" <<std::endl;
    #endif
};

Tipo : tint
{
    // CREAR AMBITO
#ifdef DEBUG
    std::cout <<"Entro en Unsp : Function tid tdosp" <<std::endl;
#endif    
};

Tipo : tfloat
{
#ifdef DEBUG
    std::cout <<"Entro en Unsp : Unsp : tvar LV" <<std::endl;
#endif
};

Bloque : tllavei BDecl SeqInstr tllaved
{
#ifdef DEBUG
    std::cout <<"Entro en LV : LV V" <<std::endl;
#endif
};

BDecl : BDecl DVar
{
#ifdef DEBUG
    std::cout <<"Entro en LV : V" <<std::endl;
#endif
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
};

LIdent : LIdent tcoma Variable
{
#ifdef DEBUG
    std::cout <<"Entro en Lid : id" <<std::endl;
#endif
};

LIdent : Variable
{
#ifdef DEBUG
    std::cout <<"Entro en Tipo : integer" <<std::endl;
#endif
 };

Variable : tid V
{
#ifdef DEBUG
    std::cout <<"Entro en Tipo : real" <<std::endl;
#endif
};

V :
{
#ifdef DEBUG
    std::cout <<"Entro en Bloque : begin SInstr tend" <<std::endl;
#endif
};

V : tcori tnentero tcord V
{
#ifdef DEBUG
    std::cout <<"Entro en SInstr : SInstr pyc Instr" <<std::endl;
#endif
};

SeqInst : SeqInstr Instr
{
#ifdef DEBUG
    std::cout <<"Entro en SInstr : Instr" <<std::endl;
#endif
};

SeqInstr :
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : Bloque" <<std::endl;
#endif
};

Instr : tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : id asig E" <<std::endl;
#endif
#ifdef DEBUG
    std::cout <<"Salgo de Instr : id asig E" <<std::endl;
#endif
};

Instr : Bloque
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : if E then Instr ColaIf" <<std::endl;
#endif
};

//FALTA LOS ERRORES DE LOS ARRAY
Instr : Ref {if(buscaSimbolo($1.lexema).tipo == NFUNCION) msgError(ERRSEMNOVAR),$1.nlin,$1.ncol,$1.lexema); if(buscaSimbolo($1.lexema).tipo == NARRAY) msgError(ERRSEMNOVAR,$1.nlin,$1.ncol, $1.lexema);} tasig Expr {if (buscaSimbolo($4.lexema).tipo == NARRAY) msgError(ERRSEMNOVAR,$1.nlin,$1.ncol, $1.lexema);} tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : Ref tasig Expr tpyc " <<std::endl;
#endif
    int tmp = nTmp();
    $$.dir = tmp;
    $$.cod += $4.cod; //Anyadir el codigo que viene de la expresion que
                      //queremos evaluar.
    if($1.tipo == NREAL && $4.tipo == NENTERO)
      {
        $$.tipo = NREAL;
        int tmpAux = nTmp();
        $$.cod += "mov " + $4.dir + " A; Empezamos la asignacion ENTERO a REAL\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + tmpAux + "\n";
        $$.cod += "mov " + tmpAux + " " + $1.dir + ";Asignamos el valor\n";
      }
    else if($1.tipo == NENTERO && $4.tipo == NREAL)
      {
        $$.tipo = NENTERO;
        int tmpAux = nTmp();
        $$.cod += "mov " + $4.dir + " A; Empezamos la asignacion REAL a ENTERO\n";
        $$.cod += "rtoi\n";
        $$.cod += "mov A " + tmpAux + "\n";
        $$.cod += "mov " + tmpAux + " " + $1.dir + ";Asignamos el valor\n";
      }
    else if($1.tipo == NENTERO && $4.tipo == NENTERO)
      {
        $$.tipo = NENTERO;
        $$.cod += "mov " + $4.dir + " " + $1.dir + "; Asignamos el valor ENTERO a ENTERO";
      }
    else
      {
        $$.tipo = NREAL;
        $$.cod += "mov " + $4.dir + " " + $1.dir + "; Asignamos el valor REAL a REAL";
      }
};

Instr : tprintf tpari tformato tcoma Expr tpard tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en ColaIf : else Instr endif" <<std::endl;
#endif
};

Instr : tscanf tpari tformato tcoma treferencia Ref tpard tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : while E do Instr" <<std::endl;
#endif
};

Instr : tif tpari Expr tpard Instr Instr_prima
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : writeln pari E pard" <<std::endl;
#endif
};

Instr_prima : telse Instr
{
#ifdef DEBUG
  std::cout<<"Entro e Instr_prima : telse Instr"
    #endif
    

}

Instr_prima : 
{
#ifdef DEBUG
    std::cout <<"Entro en Instr_prima : " <<std::endl;
#endif
    $$.cod = "";
};

Instr : twhile tpari Expr tpard Instr
{
#ifdef DEBUG
    std::cout <<"Entro en E : Expr" <<std::endl;
#endif
    
};

Expr : Expr trelop Esimple
{
#ifdef DEBUG
    std::cout <<"Entro en Expr : Expr trelop Esimple" <<std::endl;
#endif
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
        $$.cod += "mov " + $1.dir + " A; Cargo el primer termino en A\n";
        $$.cod += operacion;
        $$.cod += "i " + $3.dir + "; Realizamos la operacion entera total.\n";
      }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
      {
        $$.cod += "mov " + $1.dir + " A; Cargo el primer termino en A\n";
        $$.cod += "itor; Realizo la conversión del primer termino\n";
        $$.cod += operacion;
        $$.cod += "r " + $3.dir + ";Realizamos la operacion entera- real\n";
      }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
      {
        int tmpAux = nTmp();
        $$.cod += "mov " + $3.dir + " A; Cargamos el segundo operando.\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + tmpAux + "; Guardamos el segundo termino convertido.\n";
        $$.cod += "mov " + $1.dir + " A\n";
        $$.cod += operacion;
        $$.cod += "r " + tmpAux + "; Realizamos la operacion real.\n";
      }
    else // REAL && REAL
      {
        $$.cod += "mov " + $1.dir + " A;Cargamos el primer operando.";
        $$.cod += operacion;
        $$.cod += "r " + $3.dir + "\n";
      }
    $$.cod += "mov A " + tmp + "; Lo cargamos en la dir correspondiente.\n";

    
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
    String operacion = "";
    
     if($2.lexema == "+")
          {
            operacion = "add";
          }
        else
          operacion = "sub";
     
     $$.cod += $1.cod + $3.cod;
     if($1.tipo == NENTERO && $3.tipo == NENTERO)
      {
        $$.cod += "mov " + $1.dir + " A; Cargo el primer termino en A\n";
        $$.cod += operacion;
        $$.cod += "i " + $3.dir + "; Realizamos la operacion entera total.\n";
        $$.tipo = NENTERO;
      }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
      {
        $$.cod += "mov " + $1.dir + " A; Cargo el primer termino en A\n";
        $$.cod += "itor; Realizo la conversión del primer termino\n";
        $$.cod += operacion;
        $$.cod += "r " + $3.dir + ";Realizamos la operacion entera- real\n";
        $$.tipo = NREAL;
      }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
      {
        int tmpAux = nTmp();
        $$.cod += "mov " + $3.dir + " A; Cargamos el segundo operando.\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + tmpAux + "; Guardamos el segundo termino convertido.\n";
        $$.cod += "mov " + $1.dir + " A\n";
        $$.cod += operacion;
        $$.cod += "r " + tmpAux + "; Realizamos la operacion real.\n";
        $$.tipo = NREAL;
      }
    else // REAL && REAL
      {
        $$.cod += "mov " + $1.dir + " A;Cargamos el primer operando.";
        $$.cod += operacion;
        $$.cod += "r " + $3.dir + "\n";
        $$.tipo = NREAL;
      }
    $$.cod += "mov A " + tmp + "; Lo cargamos en la dir correspondiente.\n";
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
    String operacion = "";
    
     if($2.lexema == "*")
          {
            operacion = "mul";
          }
        else
          operacion = "div";
     $$.cod += $1.cod + $2.cod;
     if($1.tipo == NENTERO && $3.tipo == NENTERO)
      {
        $$.cod += "mov " + $1.dir + " A; Cargo el primer termino en A\n";
        $$.cod += operacion;
        $$.cod += "i " + $3.dir + "; Realizamos la operacion entera total.\n";
        $$.tipo = NENTERO;
      }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
      {
        $$.cod += "mov " + $1.dir + " A; Cargo el primer termino en A\n";
        $$.cod += "itor; Realizo la conversión del primer termino\n";
        $$.cod += operacion;
        $$.cod += "r " + $3.dir + ";Realizamos la operacion entera- real\n";
        $$.tipo = NREAL;
      }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
      {
        int tmpAux = nTmp();
        $$.cod += "mov " + $3.dir + " A; Cargamos el segundo operando.\n";
        $$.cod += "itor\n";
        $$.cod += "mov A " + tmpAux + "; Guardamos el segundo termino convertido.\n";
        $$.cod += "mov " + $1.dir + " A\n";
        $$.cod += operacion;
        $$.cod += "r " + tmpAux + "; Realizamos la operacion real.\n";
        $$.tipo = NREAL;
      }
    else // REAL && REAL
      {
        $$.cod += "mov " + $1.dir + " A;Cargamos el primer operando.";
        $$.cod += operacion;
        $$.cod += "r " + $3.dir + "\n";
        $$.tipo = NREAL;
      }
     $$.cod += "mov A " + tmp + "; Lo cargamos en la dir correspondiente.\n";
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
};

Factor : tnentero
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : tnentero" <<std::endl;
#endif

    $$.tipo = NENTERO;
    tmp = nTemp();
    
    $$.dir = tmp;
    $$.cod = "mov #" + $1.lexema + " " + tmp + "; Guardo un numero Entero\n";
};

Factor : tnreal
{
#ifdef DEBUG
  std::cout <<"Entro en Factor: tnreal" <<std::endl;
  #endif
  $$.tipo = NREAL;
  tmp = nTemp();
  $$.dir = tmp;
  $$.cod = "mov $" + $1.lexema + " " + tmp + "; Guardo un numero Real\n";
}

//No estoy muy convencido de esto.
Factor : tpari Expr tpard
{
#ifdef DEBUG
  std::cout <<"Entro en Factor: tpari Expr tard" <<std::endl;
#endif
  $$.tipo = $2.tipo;
  $$.dir = nTmp();
  $$.cod = $2.cod;
  $$.cod += "\n";
  


}

Ref : tid
{
  Simbolo sim  = buscarSimbolo(id.lexema);
  int tmp = nTmp();
  $$.tipo = sim.tipo;
  $$.dbase = sim.dir;
  $$.cod = "mov #0 " + tmp;
}

//TODO Cambiar Error
Ref : Ref tcori {if(!esArray($1.tipo)) msgError(ERRSEMNOVAR, $1.nlin, $1.ncol, $1.lexema); } Esimple tcord 
{
  if ($4.tipo != NENTERO)
    msgError(ERRSEMASIG,$4.nlin,$4.ncol,$4.lexema); //TODO Cambiar error
  TTipo ttipo = sacarTTipo($1.lexema);
  
  $$.tipo = ttipo.tbase;
  
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
    tablaSimbolos.push_back(nuevoAmbito);
}

void anyadirSimbolo(Simbolo simboloAux)
{
    tablaSimbolos[tablaSimbolos.size() -1].simbolos.push_back(simboloAux);
}

void anyadirSimbolo(string nombre, int tipo)
{
    Simbolo s;
    s.nombre = nombre;
    s.tipo = tipo;
    if(tablaSimbolos.back().nombre == "")
        s.ambito = "main";
    else 
        s.ambito = tablaSimbolos.back().nombre;
    tablaSimbolos.back().simbolos.push_back(s);
}

//CHECK
/*Simbolo buscaSimboloEnAmbito(Ambito ambitoAux, string simbolo)
{
     for(int i = 0; i < ambitoAux.simbolos.size();i++)
        {
            if(ambitoAux.simbolos[i].nombre == simbolo)
                return ambitoAux.simbolos[i];
        }
     Simbolo s;
     return s;
}

Simbolo buscaSimbolo(string simbolo)
{
    Simbolo simboloAux;
    // Recorremos la tabla de simbolos desde el ultimo ambito introducido hasta
    // el inicial.
    for(int i = tablaSimbolos.size()-1; i >=0; i--)
        {
            simboloAux = buscaSimboloEnAmbito(tablaSimbolos[i],simbolo);
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
            if(tablaSimbolos.back().simbolos[i].tipo == 0)
                {
                  tablaSimbolos.back().simbolos[i].tipo = tipo;
                }
        }
}

void borrarAmbito()
{
    tablaSimbolos.pop_back();
}

void imprimirAmbito(Ambito ambito)
{
     std::cout <<"Ambito: \n\tNombre:\t " <<ambito.nombre <<std::endl;
     std::cout <<"\tSimbolos:" <<std::endl;
     for(int i = 0; i < ambito.simbolos.size(); i++)
          std::cout<< "\t\t" <<i <<": " <<ambito.simbolos[i].nombre <<"\t" <<ambito.simbolos[i].tipo <<std::endl; 
}

void imprimirTablaSimbolos()
{
     for(int i = 0; i < tablaSimbolos.size(); i++)
        imprimirAmbito(tablaSimbolos[i]);
}
*/
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
