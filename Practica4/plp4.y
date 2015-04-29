/*------------------------------ ejemplo.y -------------------------------*/
%token tpari
%token tpard
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
%token tfunction
%token tif
%token tthen
%token telse
%token tendif
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
Simbolo buscaSimboloEnAmbito(Ambito ambitoAux, string simbolo);
void borrarAmbito();
void asignarTipo(int tipo);
void imprimirAmbito(Ambito ambito);
void imprimirTablaSimbolos();
//void anyadirTS(MITIPO variable);
//void anyadirTSLexema(string lexema);
//void anyadirTS(string lexema, int nlin,int ncol, int tipo, string trad);
//int buscaTS(string variable);
 
// void borrarAmbito();
 

const int NENTERO=1;
const int NREAL=2;
const int BOOL = 3;
const int NVACIO = 0;
const int NFUNCION = 4;

string operador, s1, s2;  // string auxiliares

 std::vector<Ambito> tablaSimbolos;
 %}

%%


S : tprogram tid tpyc Vsp Bloque    { /* comprobar que después del programa
                                            no hay ningún token más */
    int tk = yylex();

    $$.trad = "// program" + $2.lexema + "\n" + $4.trad + "\nint main()" + $5.trad;
    //    std::cout<<"ESTOY EN S: " <<std::endl;
    
    if (tk != 0) yyerror("");
 };

Vsp : Vsp Unsp
{
    #ifdef DEBUG
    std::cout <<"entro en Vsp: Vsp Unsp" <<std::endl;
    #endif
    $$.trad = $1.trad + $2.trad;
};

Vsp : Unsp
{
    #ifdef DEBUG
    std::cout <<"Entro en Vsp : Unsp" <<std::endl;
    #endif
    $$.trad = $1.trad;
    //    std::cout<<"SALGO DE VSP: UNSP " <<std::endl;
    
};

Unsp : tfunction tid { buscaSimboloEnAmbito(tablaSimbolos.back(), $2.lexema).nombre == "" ? anyadirSimbolo($2.lexema, NFUNCION) : msgError(ERRSEMMISMO,0,0,"");  anyadirAmbito($2.lexema);} tdosp Tipo tpyc Vsp Bloque tpyc
{
    // CREAR AMBITO
    #ifdef DEBUG
    std::cout <<"Entro en Unsp : Function tid tdosp" <<std::endl;
#endif    
    //std::cout<<"TAMANYO AMBITOS " <<tablaSimbolos.size() <<std::endl;
    
     //tablaSimbolos[0].nombre = "HOLAAAA";
   /* std::cout<<"VECTOR CAMBIADO " <<tablaSimbolos.size() <<std::endl;
    for(int i = 0; i < tablaSimbolos.size(); i++)
        std::cout<<"Nombre: " <<tablaSimbolos[i].nombre <<std::endl;*/
    $$.trad = $6.trad + "\n" + $4.trad + $2.trad + "()" + $7.trad;
    borrarAmbito();
};

Unsp :  tvar LV
{
    #ifdef DEBUG
    std::cout <<"Entro en Unsp : Unsp : tvar LV" <<std::endl;
    #endif
    $$.trad = $2.trad;
}  ;

LV : LV V
{
    #ifdef DEBUG
    std::cout <<"Entro en LV : LV V" <<std::endl;
    #endif
    $$.trad = $1.trad + $2.trad;
};

LV : V
{
    #ifdef DEBUG
    std::cout <<"Entro en LV : V" <<std::endl;
    #endif
    $$.trad = $1.trad;
};

V : Lid tdosp Tipo tpyc
{
#ifdef DEBUG
    std::cout <<"Entro en V : Lid dosp Tipo pyc" <<std::endl;
#endif
    $$.trad = $3.trad + $1.trad + ";";
    asignarTipo($3.tipo);
};

Lid : Lid tcoma tid
{
#ifdef DEBUG
    std::cout <<"Entro en Lid : Lid coma tid" <<std::endl;
#endif
    //if(strcmp(buscarSimboloEnAmbito(tablaSimbolos.back(),)))
      //  msgError(ERRSEMNOVAR); // ERROR NO ENCONTRADA VARIABLE
    
//    anyadirTSLexema($1.lexema);
    $$.trad = $1.trad + "," + $3.lexema;
};

Lid : tid
{
#ifdef DEBUG
    std::cout <<"Entro en Lid : id" <<std::endl;
#endif
   /* if(buscaTS($1.lexema) == -1)
        {
            int i = 0;
            //            std::cout<<"No he encontrado nada" <<std::endl;
            
        }
    else
        {
            //   std::cout<<"Anyaddddo" <<std::endl;
            
//            anyadirTSLexema($1.lexema);
            $$.trad = $1.lexema;
        }*/
};

Tipo : tinteger
{
#ifdef DEBUG
    std::cout <<"Entro en Tipo : integer" <<std::endl;
#endif
    $$.tipo = NENTERO;
    $$.trad = "int";
 };

Tipo : treal
{
#ifdef DEBUG
    std::cout <<"Entro en Tipo : real" <<std::endl;
#endif
    $$.tipo = NREAL;
    $$.trad = "double";
};

Bloque : tbegin SInstr tend
{
#ifdef DEBUG
    std::cout <<"Entro en Bloque : begin SInstr tend" <<std::endl;
#endif
    $$.trad = "{" + $2.trad + "}";
};

SInstr : SInstr tpyc Instr
{
#ifdef DEBUG
    std::cout <<"Entro en SInstr : SInstr pyc Instr" <<std::endl;
#endif
    $$.trad = $1.trad + ";\n" + $3.trad;
};

SInstr :  Instr
{
#ifdef DEBUG
    std::cout <<"Entro en SInstr : Instr" <<std::endl;
#endif
    $$.trad = $1.trad;
};

Instr : Bloque
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : Bloque" <<std::endl;
#endif
    $$.trad = $1.trad;
};

Instr : tid tasig E
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : id asig E" <<std::endl;
#endif
    /*if(buscaTS($1.trad) != -1)
        {
            $$.trad = $1.lexema;
        }
    else
        {
            std::cout<<"TENGO QUE COLOCAR EL ERROR QUE NO ENCUENTRA VARIABLE" <<std::endl;
            
            //ERROR MENSAJE NO ENCONTRADA VARIABLE
        }*/
    $$.trad += "=";
    if($3.tipo == NREAL && $1.tipo == NENTERO)
        {
            //ERRORSEM REAL
        }
    
    if($3.tipo == NENTERO && $1.tipo == NREAL)
        {
            $$.trad += "itor(" + $3.trad + ")";
        }
    else 
        {
            $$.trad += $3.trad;
        }
    $$.tipo = $1.tipo;
#ifdef DEBUG
    std::cout <<"Salgo de Instr : id asig E" <<std::endl;
#endif

};

Instr : tif E tthen Instr ColaIf
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : if E then Instr ColaIf" <<std::endl;
#endif
    //TODO
    /* if(buscaTS($1.trad) != -1)
       $$.trad = $1.lexema*/
};

ColaIf : tendif  {

#ifdef DEBUG
    std::cout <<"Entro en ColaIf : endif " <<std::endl;
#endif
     $$.trad = ""; };

ColaIf : telse Instr tendif
{
#ifdef DEBUG
    std::cout <<"Entro en ColaIf : else Instr endif" <<std::endl;
#endif
    $$.trad = $1.lexema + $2.trad;
};

Instr : twhile E tdo Instr
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : while E do Instr" <<std::endl;
#endif
    if($2.tipo != BOOL) 
        {
            //ERROR SEM BOOL
        }
    $$.trad = $1.lexema + "(" + $2.trad + ")\n" + $4.trad;
};

Instr : twriteln tpari E tpard
{
#ifdef DEBUG
    std::cout <<"Entro en Instr : writeln pari E pard" <<std::endl;
#endif
    $$.trad = "printf(";
    if($3.tipo == NENTERO)
        $$.trad += "%d\n,";
    else if( $3.tipo ==NREAL)
        $$.trad += "%g\n,";
    else
        {
            //ERRSEMWRLN
        }
    $$.trad += $3.trad + ")";
};

E : Expr trelop Expr
{
#ifdef DEBUG
    std::cout <<"Entro en E : Expr relop Expr" <<std::endl;
#endif
    $$.trad = $1.trad + $2.lexema + $3.trad;
    $$.tipo = BOOL;
};

E : Expr
{
#ifdef DEBUG
    std::cout <<"Entro en E : Expr" <<std::endl;
#endif
    $$.trad = $1.trad;
    $$.tipo = $1.tipo;
};

Expr : Expr taddop Term
{
#ifdef DEBUG
    std::cout <<"Entro en Expr : Expr addop Term" <<std::endl;
#endif
    if($1.tipo  == NENTERO && $3.tipo == NENTERO)
        {
            $$.trad = $1.trad + $2.lexema +"i " + $3.trad; $$.tipo = NENTERO;
        }
    else if($1.tipo == NREAL && $3.tipo == NREAL)
     {
         $$.trad = $1.trad + $2.lexema +"d "+ $3.trad; $$.tipo = NREAL;
     }
    else if($1.tipo == NREAL && $3.tipo == NENTERO)
        {
                $$.trad = $1.trad + $2.lexema +"d " + "itor(" + $3.trad + ")"; $$.tipo = NREAL;
        }
    else if($1.tipo == NENTERO && $3.tipo == NREAL)
        {
            $$.trad = "itor(" + $1.trad + ")" + $2.lexema +"d " + $3.trad; $$.tipo = NREAL;
        }
};

Expr : Term
{
#ifdef DEBUG
    std::cout <<"Entro en Expr : Term" <<std::endl;
#endif
    $$.trad = $1.trad;
    $$.tipo = $1.tipo;
};

Term : Term tmulop Factor
{
#ifdef DEBUG
    std::cout <<"Entro en Term : Term mulop Factor" <<std::endl;
#endif
    if($2.lexema == "div")
        {
            if($1.tipo == NREAL || $3.tipo == NREAL )
                {

                                //TODO GENERAR ERROR
                }
            else
                $$.trad = $1.trad + "/i" + $3.trad;
        }
    else if($2.lexema == "/")
        {
            $$.trad = $1.tipo == NENTERO ? "itor("+$1.trad+")" : $1.trad;
            $$.trad += "/r";
            
                $$.trad += $3.tipo == NENTERO ? "itor("+$3.trad+")" : $3.trad;
        }
    else if($2.lexema == "*")
        {

            if($1.tipo == NREAL || $3.tipo == NREAL)
                {
                    $$.trad = $1.tipo == NENTERO ? "itor("+$1.trad+")" : $1.trad;
                    $$.trad += "*r";
                    
                        $$.trad += $3.tipo == NENTERO ? "itor("+$3.trad+")" : $3.trad;
                }
            else
                {
                    $$.trad = $1.trad;
                    $$.trad += "*i";
                    
                        $$.trad += $3.trad;        
                }
        }
};

Term : Factor
{
#ifdef DEBUG
    std::cout <<"Entro en Term : Factor" <<std::endl;
#endif
    $$.trad = $1.trad;
    $$.tipo = $1.tipo;
};

Factor : tid
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : id" <<std::endl;
#endif
    //    $$.trad = ambitoActual + "_" + $1.lexema;
    // $$.tipo = buscaTS($1.lexema);
};

Factor : tnentero
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : nentero" <<std::endl;
#endif
    $$.trad = $1.lexema;
    $$.tipo = NENTERO;
};

Factor : tnreal
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : real" <<std::endl;
#endif
    $$.trad = $1.lexema;
    $$.tipo = NREAL;
};

Factor : tpari Expr tpard
{
#ifdef DEBUG
    std::cout <<"Entro en Factor : pari Expr pard" <<std::endl;
#endif
    $$.trad = "(" + $2.trad + ")";
    $$.tipo = $2.tipo;
};

%%

void msgError(int nerror,int nlin,int ncol,const char *s)
{
     switch (nerror) {
     case ERRLEXICO: fprintf(stderr,"Error lexico (%d,%d): caracter '%s' incorrecto\n",nlin,ncol,s);
         break;
     case ERRSINT: fprintf(stderr,"Error sintactico (%d,%d): en '%s'\n",nlin,ncol,s);
         break;
     case ERREOF: fprintf(stderr,"Error sintactico: fin de fichero inesperado\n");
         break;
     case ERRLEXEOF: fprintf(stderr,"Error lexico: fin de fichero inesperado\n");
         break;
     case ERRSEMMISMO: fprintf(stderr,"Error semantico (%d,%d): \'%s\' ya existe en este ambito\n",nlin,ncol,s);
         break;
     case ERRSEMASIG: fprintf(stderr,"Error semantico (%d,%d):\'%s\' no ha sido declarado\n",nlin,ncol,s);
         break;
     case ERRSEMNOVAR: fprintf(stderr,"Error semantico (%d,%d):\'%s\' no es una variabla\n",nlin,ncol,s);
         break;
     case ERRSEMREAL: fprintf(stderr,"Error semantico (%d,%d):\'%s\' debe ser de tipo real\n",nlin,ncol,s);
         break;
     case ERRSEMBOOL: fprintf(stderr,"Error semantico (%d,%d): el operador \':=\' no admite expresiones relacionales\n",nlin,ncol);
         break;
     case ERRSEMREL: fprintf(stderr,"Error semantico (%d,%d):en la instrucción \'%s\' la expresión debe ser relacional\n",nlin,ncol,s);
         break;
     case ERRSEMDIV: fprintf(stderr,"Error semantico (%d,%d):lso dos operandos de \'div\' deben ser enteros\n",nlin,ncol);
         break;
     case ERRSEMWRLN: fprintf(stderr,"Error semantico (%d,%d): \'writeln\' no admite expresiones booleanas\n",nlin,ncol);
         break;
     }
        
     exit(1);
}

//CHECK
void anyadirAmbito(string nombreAmbito)
{
    Ambito nuevoAmbito;
    if(tablaSimbolos.back().nombre == "")
        nuevoAmbito.nombre = nombreAmbito;
    else 
        nuevoAmbito.nombre = tablaSimbolos[tablaSimbolos.size() -1].nombre + "_" + nombreAmbito;
    tablaSimbolos.push_back(nuevoAmbito);
}

//CHECK
void anyadirSimbolo(Simbolo simboloAux)
{
    tablaSimbolos[tablaSimbolos.size() -1].simbolos.push_back(simboloAux);
}

void anyadirSimbolo(string nombre, int tipo)
{
    Simbolo s;
    s.nombre = nombre;
    s.tipo = tipo;
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
     std::cout<<"FALTA IMPLEMENTAR ASIGNARTIPO" <<std::endl;
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
