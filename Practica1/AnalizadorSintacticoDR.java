import java.util.Arrays;

class AnalizadorSintacticoDR {

    Token token;
    AnalizadorLexico lexico;
    StringBuilder reglasAplicadas;
    boolean flag;
    

    public AnalizadorSintacticoDR()
    {
        flag = true;
        reglasAplicadas = new StringBuilder();
    }

    public AnalizadorSintacticoDR(AnalizadorLexico lexico)
    {
        this.lexico = lexico;
        flag = true;
        reglasAplicadas = new StringBuilder();
        
    }

    public final void errorSintaxis(int... tokEsperados)
    {
        
        Arrays.sort(tokEsperados);
        
        if(token.tipo == token.EOF)
            {
                System.err.print("Error sintactico: encontrado fin de fichero, esperaba "); 
            }
        else 
            System.err.print("Error sintactico (" +token.fila + "," + token.columna + "): encontrado \'" + token.lexema + "\', esperaba ");
        for(int a : tokEsperados)
            System.err.print(token.getTipoString(a)+ " ");
        System.err.println();
        
        System.exit(-1);
        
    }

    public final void errorFinalFichero(int... tokEsperado)
    {
        errorSintaxis(tokEsperado);
        System.exit(-1);     
    }
    
    public final void emparejar(int tokEsperado)
    {
        if(token.tipo == tokEsperado)
            token = lexico.siguienteToken();
        else
            errorSintaxis(tokEsperado);
    }

    public final void comprobarFinFichero()
    {
        if(token.tipo != token.EOF)
            {
                errorFinalFichero(token.EOF);
                
            }
        System.out.println(reglasAplicadas);
    }
    
    public final void S()
    {
        //System.out.println("Entro en S");
        token = lexico.siguienteToken();
        if(token.tipo == token.PROGRAM)
            {
                if(flag)
                    reglasAplicadas.append(" 1");
                emparejar(token.PROGRAM);
                emparejar(token.ID);
                emparejar(token.PYC);
                Vsp();
                Bloque();
            }
        else
            errorSintaxis(token.PROGRAM);
    }

    public final void  Vsp()
    {
        //System.out.println("Entro en Vsp");
        if(token.tipo == token.FUNCTION || token.tipo == token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 2");
                Unsp();
                Vsp_prima();
            }
        else
            errorSintaxis(token.FUNCTION,token.VAR);
        //WARNING!!
    }

    public final void  Vsp_prima()
    {
        //System.out.println("Entro en Vsp_prima");
        if(token.tipo == token.FUNCTION || token.tipo == token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 3");
                Unsp();
                Vsp_prima();
            }
        else if(token.tipo == token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 4");
            }
        else errorSintaxis(token.FUNCTION,token.VAR,token.BEGIN);
    }

    public final void  Unsp()
    {
        //System.out.println("Entro en Unsp");
        if(token.tipo == token.FUNCTION)
            {
                if(flag)
                    reglasAplicadas.append(" 5");
                emparejar(token.FUNCTION);
                emparejar(token.ID);
                emparejar(token.DOSP);
                Tipo();
                emparejar(token.PYC);
                Vsp();
                Bloque();
                emparejar(token.PYC);
            }
        else if(token.tipo == token.VAR)
            {
                if(flag)
                    reglasAplicadas.append(" 6");
                emparejar(token.VAR);
                LV();
            }
        else errorSintaxis(token.FUNCTION,token.VAR);
        
    }

    public final void LV()
    {
        //System.out.println("Entro en LV");
        if(token.tipo == token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 7");
                V();
                LV_prima();
            }
        else errorSintaxis(token.ID);
        
    }

    public final void  LV_prima()
    {
        //System.out.println("Entro en LV_prima");
        if(token.tipo == token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 8");
                V();
                LV_prima();
            }
        else if(token.tipo == token.FUNCTION || token.tipo == token.VAR || token.tipo == token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 9");
                
            }
        else errorSintaxis(token.ID,token.FUNCTION, token.VAR,token.BEGIN);
    }

    public final void  V()
    {
        //System.out.println("Entro en V");
        if(token.tipo == token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 10");
                emparejar(token.ID);
                Lid();
                emparejar(token.DOSP);
                Tipo();
                emparejar(token.PYC);
            }
        else errorSintaxis(token.ID);
        
    }
    // Array.sort();
    public final void  Lid()
    {
        //System.out.println("Entro en Lid");
        if(token.tipo == token.COMA)
            {
                if(flag)
                    reglasAplicadas.append(" 11");
                emparejar(token.COMA);
                emparejar(token.ID);
                Lid();
                
            }
        else if(token.tipo == token.DOSP)
            {
                if(flag)
                    reglasAplicadas.append(" 12");
                
            }
        else errorSintaxis(token.COMA,token.DOSP);
        
    }

    public final void  Tipo()
    {
        //System.out.println("Entro en Tipo");
        if(token.tipo == token.INTEGER)
            {
                if(flag)
                    reglasAplicadas.append(" 13");
                emparejar(token.INTEGER);
                
            }
        else if(token.tipo == token.REAL)
            {
                if(flag)
                    reglasAplicadas.append(" 14");
                emparejar(token.REAL);
                
            }
        else errorSintaxis(token.INTEGER,token.REAL);
        
    }

    public final void  Bloque()
    {
        //System.out.println("Entro en Bloque");
        if (token.tipo == token.BEGIN) {
            if(flag)
                reglasAplicadas.append(" 15");
            emparejar(token.BEGIN);
            SInstr();
            emparejar(token.END);
        }
        else errorSintaxis(token.BEGIN);
    }

    public final void  SInstr()
    {
        //System.out.println("Entro en SInstr");
        //System.out.println(token.tipo);
        if (token.tipo == token.BEGIN || token.tipo == token.ID || token.tipo == token.IF || token.tipo == token.WHILE || token.tipo == token.WRITELN) {
            if(flag)
                reglasAplicadas.append(" 16");
            Instr();
            SInstrp();
        }
        else errorSintaxis(token.BEGIN,token.ID,token.IF,token.WHILE,token.WRITELN);
    }

    public final void  SInstrp()
    {
        //  System.out.println("Entro en SInstrp");
        if(token.tipo == token.PYC)
            {
                if(flag)
                    reglasAplicadas.append(" 17");
                emparejar(token.PYC);
                Instr();
                SInstrp();
            }
        else if(token.tipo == token.END)
            {
                if(flag)
                    reglasAplicadas.append(" 18");
                // SInstrp();
                
            }
        else errorSintaxis(token.PYC,token.END);
        
    }

    public final void  Instr()
    {
        //System.out.println("Entro en Instr");
        //System.out.println("Entro con: " + token.lexema);
        if(token.tipo == token.BEGIN)
            {
                if(flag)
                    reglasAplicadas.append(" 19");
                Bloque();
                
            }
        else if(token.tipo == token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 20");
                emparejar(token.ID);
                emparejar(token.ASIG);
                E();
                
            }
        else if(token.tipo == token.IF)
            {
                if(flag)
                    reglasAplicadas.append(" 21");
                emparejar(token.IF);
                E();
                emparejar(token.THEN);
                Instr();
                Instr_prima();
                
            }
        else if(token.tipo == token.WHILE)
            {
                if(flag)
                    reglasAplicadas.append(" 24");
                emparejar(token.WHILE);
                E();
                emparejar(token.DO);
                Instr();
            }
        else if(token.tipo == token.WRITELN)
            {
                if(flag)
                    reglasAplicadas.append(" 25");
                emparejar(token.WRITELN);
                emparejar(token.PARI);
                E();
                emparejar(token.PARD);
            }
        else errorSintaxis(token.BEGIN,token.IF,token.WHILE,token.WRITELN,token.ID);
        
    }

    public final void  Instr_prima()
    {
        //System.out.println("Entro en Instr_prima");
        if(token.tipo == token.ENDIF)
            {
                if(flag)
                    reglasAplicadas.append(" 22");
                emparejar(token.ENDIF);
                
            }
        else if(token.tipo == token.ELSE)
            {
                if(flag)
                    reglasAplicadas.append(" 23");
                emparejar(token.ELSE);
                Instr();
                emparejar(token.ENDIF);
                
            }
        else errorSintaxis(token.ELSE, token.ENDIF);
        
    }

    public final void  E()
    {
        //System.out.println("Entro en E");
        if(token.tipo == token.ID || token.tipo == token.NENTERO || token.tipo == token.NREAL || token.tipo == token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 26");
                Expr();
                E_prima();
            }
        else errorSintaxis(token.ID, token.NENTERO, token.NREAL, token.PARI);
        
    }
    public final void E_prima()
    {
        //System.out.println("Entro en E_prima");
        if(token.tipo == token.RELOP)
            {
                if(flag)
                    reglasAplicadas.append(" 27");
                emparejar(token.RELOP);
                Expr();
            }
        else if(token.tipo == token.PYC || token.tipo == token.ENDIF || token.tipo == token.ELSE || token.tipo == token.END || token.tipo == token.THEN || token.tipo == token.DO || token.tipo == token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 28");
                
            }
        else errorSintaxis(token.RELOP, token.PYC, token.ENDIF, token.ELSE, token.END, token.THEN, token.DO, token.PARD);
        
    }

    public final void  Expr()
    {
        //System.out.println("Entro en Expr");
        if(token.tipo == token.ID || token.tipo == token.NENTERO || token.tipo == token.NREAL || token.tipo == token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 29");
                Term();
                Expr_prima();
            }
        else errorSintaxis(token.ID, token.NENTERO, token.NREAL, token.PARI);
        
    }

    public final void  Expr_prima()
    {
        //System.out.println("Entro en Expr_prima");
        if(token.tipo == token.ADDOP)
            {
                if(flag)
                    reglasAplicadas.append(" 30");
                emparejar(token.ADDOP);
                Term();
                Expr_prima();
                
            }
        else if(token.tipo == token.RELOP || token.tipo == token.PYC || token.tipo == token.ENDIF || token.tipo == token.ELSE || token.tipo == token.END || token.tipo == token.THEN || token.tipo == token.DO || token.tipo == token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 31");
                
            }
        else errorSintaxis(token.ADDOP, token.RELOP, token.PYC, token.ENDIF, token.ELSE, token.END, token.THEN, token.DO, token.PARD);
    }

    public final void  Term()
    {
        //System.out.println("Entro en Term");
        if(token.tipo == token.ID || token.tipo == token.NENTERO || token.tipo == token.NREAL || token.tipo == token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 32");
                Factor();
                Term_prima();
            }
              else errorSintaxis(token.ID, token.NENTERO, token.NREAL, token.PARI);
    }

    public final void  Term_prima()
    {
        //System.out.println("Entro en Term_prima");
        if(token.tipo == token.MULOP)
            {
                if(flag)
                    reglasAplicadas.append(" 33");
                emparejar(token.MULOP);
                Factor();
                Term_prima();
            }
        else if(token.tipo == token.ADDOP || token.tipo == token.RELOP || token.tipo == token.PYC || token.tipo == token.ENDIF || token.tipo == token.ELSE || token.tipo == token.END || token.tipo == token.THEN || token.tipo == token.DO || token.tipo == token.PARD)
            {
                if(flag)
                    reglasAplicadas.append(" 34");
                
            }
        else errorSintaxis(token.MULOP,token.ADDOP, token.RELOP, token.PYC, token.ENDIF, token.ELSE, token.END, token.THEN, token.DO, token.PARD);
    }

    public final void  Factor()
    {
        //System.out.println("Entro en Factor");
        if(token.tipo == token.ID)
            {
                if(flag)
                    reglasAplicadas.append(" 35");
                emparejar(token.ID);
                
            }
        else if(token.tipo == token.NENTERO)
            {
                if(flag)
                    reglasAplicadas.append(" 36");
                emparejar(token.NENTERO);
                
            }
        else if(token.tipo == token.NREAL)
            {
                if(flag)
                    reglasAplicadas.append(" 37");
                emparejar(token.NREAL);
                
            }
        else if(token.tipo == token.PARI)
            {
                if(flag)
                    reglasAplicadas.append(" 38");
                emparejar(token.PARI);
                Expr();
                emparejar(token.PARD);
                
            }
        else errorSintaxis(token.ID, token.NENTERO, token.NREAL, token.PARI);
    }
}
//  LocalWords:  sintactico
