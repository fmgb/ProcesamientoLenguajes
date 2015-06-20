/**
   Autor: Francisco Manuel García Botella (fmgb3@alu.ua.es)
   Objetivo: Procesamiento de Lenguajes 2014/2015(Universidad Alicante)
   Práctica 4
   Licencia: GPLv3
   Creado el: 25/04/2015
*/

/*----------------------- comun.h -----------------------------*/

/* fichero con definciones comunes para los ficheros .l y .y */

typedef struct {
  string lexema;
  int nlin,ncol;
  int tipo;
  string cod;
  string ambito;
  int dbase;
  int dir;
} MITIPO;

typedef struct 
{
  string nombre;
  int tipo;
  int dir;
  string ambito;
} Simbolo;

typedef struct 
{
  string nombre;
  vector<Simbolo> simbolos;
  int posicionMemoriaInicio;
  int variablesInicio;
} Ambito;

typedef struct
{
  int tipo;
  int tam;
  int tbase;
} TTipo;

#define YYSTYPE MITIPO

/* Tipos posibles en la gramática */
const int NVACIO = 0;
const int NENTERO=1;
const int NREAL=2;
const int BOOL = 3;

const int ARRAY = 4;
const int FUNCION = 5;

const int MAXVAR = 1599;
const int MAXMEM = 16383;

/* Tipo de erorres en la gramática */

#define ERRLEXICO    1
#define ERRSINT      2
#define ERREOF       3
#define ERRLEXEOF    4

#define ERRYADECL       10
#define ERRNODECL       11
#define ERRDIM          12
#define ERRFALTAN       13
#define ERRSOBRAN       14
#define ERR_EXP_ENT     15

#define ERR_NOCABE     100
#define ERR_MAXVAR     101
#define ERR_MAXTIPOS   102
#define ERR_MAXTMP     103

void msgError(int nerror,int nlin,int ncol,const string s);
