/*----------------------- comun.h -----------------------------*/

/* fichero con definciones comunes para los ficheros .l y .y */

typedef struct {
    string lexema;
    int nlin,ncol;
    int tipo;
    string trad;
    string ambito;
} MITIPO;

typedef struct 
{
    string nombre;
    int tipo;
    string ambito;
} Simbolo;

typedef struct 
{
    string nombre;
    vector<Simbolo> simbolos;
} Ambito;

#define YYSTYPE MITIPO


#define ERRLEXICO    1
#define ERRSINT      2
#define ERREOF       3
#define ERRLEXEOF    4
#define ERRSEMMISMO  5
#define ERRSEMASIG   6
#define ERRSEMNOVAR  7
#define ERRSEMREAL   8
#define ERRSEMBOOL   9
#define ERRSEMREL   10
#define ERRSEMDIV   11
#define ERRSEMWRLN  12

void msgError(int nerror,int nlin,int ncol,const string s);
