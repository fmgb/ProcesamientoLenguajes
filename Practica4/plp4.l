/* Author: Francisco Manuel García Botella
   License: GNU GPLv3
   Subject: Procesamiento de Lenguajes(PL)
   Target: Practice 4
*/

/*------------------------------ plp4.l -------------------------------*/
/*   -------  Definiciones regulares  -------- */
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
#include "plp4.tab.h"

int ncol = 1,
    nlin = 1;

 bool comentario = false;
 
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

"(*" {ncol += strlen(yytext); BEGIN(COMENTARIO);}
<COMENTARIO>[^(*\n]+ {ncol += strlen(yytext);}
<COMENTARIO>\n {nlin++;ncol=1;}
<COMENTARIO>«EOF» {msgError(ERRLEXEOF,-1,-1,"");}
<COMENTARIO>"*)" {ncol+=strlen(yytext); BEGIN(INITIAL);}
<COMENTARIO>[*)] {ncol += strlen(yytext);}
        /* Las palabras reservadas deben aparecer antes de la regla que
          reconoce los identificadores, para evitar que sean reconocidas
          como identificadores en lugar de como palabras reservadas */            

"("               {return ret(tpari);}
")"               {return ret(tpard);}
"*"                {return ret(tmulop);}
"/"                {return ret(tmulop);}
"div"              {return ret(tmulop);}
"+"                {return ret(taddop);}
"-"                {return ret(taddop);}
"<"                {return ret(trelop);}
">"                {return ret(trelop);}
"<="               {return ret(trelop);}
">="               {return ret(trelop);}
"="                {return ret(trelop);}
"<>"               {return ret(trelop);}
";"                {return ret(tpyc);}
":"                {return ret(tdosp);}
","                {return ret(tcoma);}
":="               {return ret(tasig);}
"var"              {return ret(tvar);}
"real"             {return ret(treal);}
"integer"          {return ret(tinteger);}
"program"          {return ret(tprogram);}
"begin"            {return ret(tbegin);}
"end"              {return ret(tend);}
"function"         {return ret(tfunction);}
"if"               {return ret(tif);}
"then"             {return ret(tthen);}
"else"             {return ret(telse);}
"endif"            {return ret(tendif);}
"while"            {return ret(twhile);}
"do"               {return ret(tdo);}
"writeln"          {return ret(twriteln);}
{D}+               {return ret(tnentero);}
{L}({LD})*         {return ret(tid);}
{D}+(\.){D}+       {return ret(tnreal);}
.                  {msgError(ERRLEXICO,nlin,ncol,yytext);}

%%

int yywrap(void) {findefichero=1; return 1;} /* para no tener que linkar con la
                                libreria del lex */


int ret(int token)
{
  if(token == trelop)
    {
      if(strcmp(yytext,"=")){
        yylval.lexema=strdup("==");
      }
      else if (strcmp(yytext,"<>")) {
        yylval.lexema=strdup("!=");
      }
      else {
        yylval.lexema=strdup(yytext);
      }
    }
  else {
    yylval.lexema=strdup(yytext);
  }
  
  yylval.nlin=nlin;
  yylval.ncol=ncol;
  ncol+=(strlen(yytext));

  return(token);
}
