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
%token tid
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
void anyadirAmbito(string nombreAmbito);
void anyadirSimbolo(Simbolo simboloAux);
void anyadirSimbolo(string nombre, int tipo);
Simbolo buscaSimbolo(string simbolo);
 
Simbolo buscaSimboloEnAmbito(Ambito ambitoAux, string simbolo);
void borrarAmbito();
void asignarTipo(int tipo);
void imprimirAmbito(Ambito ambito);
void imprimirTablaSimbolos();

const int NENTERO=1;
const int NREAL=2;
const int BOOL = 3;
const int NVACIO = 0;
const int NFUNCION = 4;
 
string operador, s1, s2;  // string auxiliares
 Simbolo simboloAux1, simboloAux2; // Simbolos Auxiliares
 std::vector<Ambito> tablaSimbolos;
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

Instr : Ref tasig Expr tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en ColaIf : endif " <<std::endl;
#endif
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

Instr : tif tpari Expr tpard Instr_prima
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : writeln pari E pard" <<std::endl;
#endif
};

Instr_prima : Expr tpard tpyc
{


}

Instr_prima : treferencia Ref tpard tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en E : Expr relop Expr" <<std::endl;
#endif
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
    std::cout <<"Entro en Expr : Expr addop Term" <<std::endl;
#endif
};

Expr : Esimple
{
#ifdef DEBUG
    std::cout <<"Entro en Expr : Term" <<std::endl;
#endif
};

Esimple : Esimple taddop Term
{
#ifdef DEBUG
    std::cout <<"Entro en Term : Term mulop Factor" <<std::endl;
#endif
};

Esimple : Term
{
#ifdef DEBUG
    std::cout <<"Entro en Term : Factor" <<std::endl;
#endif
};

Term : Term tmulop Factor
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : id" <<std::endl;
#endif
};

Term : Factor
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : nentero" <<std::endl;
#endif
};

Factor : Ref
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : real" <<std::endl;
#endif
};

Factor : tnentero
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : pari Expr pard" <<std::endl;
#endif
};

Factor : tnreal
{



}

Ref : tid
{


}

Ref : Ref tcori Esimple tcord
{


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
     case ERRSEMMISMO: fprintf(stderr,"Error semantico (%d,%d): \'%s\' ya existe en este ambito\n",nlin,ncol,s.c_str());
         break;
     case ERRSEMASIG: fprintf(stderr,"Error semantico (%d,%d): \'%s\' no ha sido declarado\n",nlin,ncol,s.c_str());
         break;
     case ERRSEMNOVAR: fprintf(stderr,"Error semantico (%d,%d): \'%s\' no es una variable\n",nlin,ncol,s.c_str());
         break;
     case ERRSEMREAL: fprintf(stderr,"Error semantico (%d,%d) :\'%s\' debe ser de tipo real\n",nlin,ncol,s.c_str());
         break;
     case ERRSEMBOOL: fprintf(stderr,"Error semantico (%d,%d): el operador \':=\' no admite expresiones relacionales\n",nlin,ncol);
         break;
     case ERRSEMREL: fprintf(stderr,"Error semantico (%d,%d): en la instruccion \'%s\' la expresion debe ser relacional\n",nlin,ncol,s.c_str());
         break;
     case ERRSEMDIV: fprintf(stderr,"Error semantico (%d,%d): los dos operandos de \'div\' deben ser enteros\n",nlin,ncol);
         break;
     case ERRSEMWRLN: fprintf(stderr,"Error semantico (%d,%d): \'writeln\' no admite expresiones booleanas\n",nlin,ncol);
         break;
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
Simbolo buscaSimboloEnAmbito(Ambito ambitoAux, string simbolo)
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
