JFLAGS = -g
JC = javac
Corrector = autocorrector-plp1/
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Token.java \
	AnalizadorLexico.java \
	plp1.java

all: classes clean

classes: $(CLASSES:.java=.class)

test-lexico: Token.java AnalizadorLexico.java $(Corrector)
	$(shell cp Token.java AnalizadorLexico.java $(Corrector))
	$(shell cd $(Corrector) && ./autocorrector-plp1-solo-lexico.sh > ../salida.lexico)
	$(shell cat salida.lexico | grep FALLAN > error)
	cat error

test: Token.java AnalizadorLexico.java AnalizadorSintacticoDR.java plp1.java $(Corrector)
	$(shell cp Token.java AnalizadorLexico.java AnalizadorSintacticoDR.java plp1.java $(Corrector))
	$(shell cd $(Corrector) && ./autocorrector-plp1.sh > ../salida.sintactico)
	$(shell cat salida.sintactico | grep FALLAN > error)
	cat error


clean:
		$(RM) *.class salida.lexico salida.sintactico error
