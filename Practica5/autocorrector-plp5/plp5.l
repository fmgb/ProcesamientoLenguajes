/**
   Autor: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 5
   Licencia: GPLv3
   Creado el: 19/05/2015
*/

/*------------------------------ plp4.l -------------------------------*/
D    [0-9]
L    [a-zA-Z]
LD   [0-9a-zA-Z]
          /*ID {L}({L} | {D})*
            NUM {D}+(\.{D}+)?(E[+\-]?{D}+)? */
%{
  
#include <string.h> 
#include <string>
#include <iostream>
#include <vector>

using namespace std;

#include "comun.h"
#include "plp5.tab.h"


int ncol = 1,
    nlin = 1;

 int findefichero = 0;

int ret(int token);  
// funciÃ³n que actualiza 'nlin' y 'ncol' y devuelve el token

void msgError(int nerror, int nlin,int ncol,const string s);
// funciÃ³n para producir mensajes de error

%}

%x COMENTARIO

%%
%{
/* codigo local */
%}

" "                {ncol++;}
[\t]               {ncol++;}
[\n]               {nlin++;ncol=1;}
"//"(.)*           {; /* comentarios de una linea, no hacer nada */} 
"/*"                    {ncol += strlen(yytext);BEGIN(COMENTARIO);}
<COMENTARIO>[^/*\n]+    {ncol += strlen(yytext);}
<COMENTARIO>\n          {nlin++;ncol=1;}
<COMENTARIO><<EOF>>     {msgError(ERRLEXEOF,-1,-1,"");}
<COMENTARIO>"*/"        {ncol+=strlen(yytext); BEGIN(INITIAL);}
<COMENTARIO>[*/]        {ncol += strlen(yytext);}
        /* Las palabras reservadas deben aparecer antes de la regla que
          reconoce los identificadores, para evitar que sean reconocidas
          como identificadores en lugar de como palabras reservadas */            

"("               {return ret(tpari);}
")"               {return ret(tpard);}
"["               {return ret(tcori);}
"]"               {return ret(tcord);}
"{"               {return ret(tllavei);}
"}"               {return ret(tllaved);}
"*"               {return ret(tmulop);}
"/"               {return ret(tmulop);}
"+"               {return ret(taddop);}
"-"               {return ret(taddop);}
"<"               {return ret(trelop);}
">"               {return ret(trelop);}
"<="              {return ret(trelop);}
">="              {return ret(trelop);}
"=="              {return ret(trelop);}
"!="              {return ret(trelop);}
";"               {return ret(tpyc);}
":"               {return ret(tdosp);}
","               {return ret(tcoma);}
"="               {return ret(tasig);}
"&"               {return ret(treferencia);}
"%d"              {return ret(tformato);}
"%g"              {return ret(tformato);}
"main"            {return ret(tmain);}
"float"           {return ret(tfloat);}
"int"             {return ret(tint);}
"printf"          {return ret(tprintf);}
"scanf"           {return ret(tscanf);}
"if"              {return ret(tif);}
"else"            {return ret(telse);}
"while"           {return ret(twhile);}

{D}+              {return ret(tnentero);}
{L}({LD})*        {return ret(tid);}
{D}+(\.){D}+      {return ret(tnreal);}
.                 {msgError(ERRLEXICO,nlin,ncol,yytext);}

%%

int yywrap(void) {findefichero=1; return 1;} /* para no tener que linkar con la
                                libreria del lex */


int ret(int token)
{

    yylval.lexema=strdup(yytext);
  yylval.nlin=nlin;
  yylval.ncol=ncol;
  ncol+=(strlen(yytext));

  return(token);
}
