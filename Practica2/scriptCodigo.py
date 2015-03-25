#!/usr/bin/python

import xlrd

class Script():
    def __init__(self,file,npage):
        self._file= file
        self._npage = npage
        self._nColInit = 0
        self._nRowInit = 0
        self._nombreVariableAccion = '_acciones'
        self._nombreClaseAccion = 'Accion'
        self._nombreVariableIrA = '_ir_A'
        self.openFile()
        self._nColEnd = self._sheet.ncols
        self._nRowEnd = 56
        self._nColSep = 23
    def openFile(self):
        doc = xlrd.open_workbook(self._file)
        self._sheet = doc.sheet_by_index(self._npage)

    def setColInit(self,nColInit):
        self._nColInit = nColInit

    def setColEnd(self, nColEnd):
        self._nColEnd = nColEnd

    def setRowInit(self,nRowInit):
        self._nRowInit = nRowInit

    def setRowEnd(self,nRowEnd):
        self._nRowEnd = nRowEnd

    def run(self):
        for i in range(self._nRowInit,self._nRowEnd):
            string = ''
            row = 0
            col = 0
            for j in range(self._nColInit,self._nColEnd):
               # if (i >= self._nRowInit and j >= self._nColInit and j != self._nColSep):
                row = i - self._nRowInit
                col = j - self._nColInit
                if( j < self._nColSep):
                    if(self._sheet.cell_value(i,j) != ''):
                        #Acción  acciones = new Accion[2][2]();
                        if(self._sheet.cell_value(i,j) == 'aceptar'):
                            aux = self._nombreVariableAccion + "["+'%s'%row + "]["+ '%s'%col + "] = new "+self._nombreClaseAccion+ "('a',0);\n"
                        else:
                            aux = self._nombreVariableAccion + "["+'%s'%row + "]["+ '%s'%col + "] = new " + self._nombreClaseAccion + "('"+'%s'%self._sheet.cell_value(i,j)[0]+"',"+'%s'%self._sheet.cell_value(i,j)[1:]+");\n"
                        string += aux
                elif (j > self._nColSep):
                    #Ir_A
                    if(self._sheet.cell_value(i,j) != ''):
                        aux = self._nombreVariableIrA + "[" + '%s'%row+"]["+'%s'%(col-self._nColSep)+"] = "+'%s'%int(self._sheet.cell_value(i,j))+ ";\n"
                        string +=aux
            print(string)

      


# Run script
script = Script('Tabla Kraken.xlsx',0)
script._nRowInit = 2
script._nColInit = 1
script._nColSep = 24
#script._nColInit = 50
script.run()
