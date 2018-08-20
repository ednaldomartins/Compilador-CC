
package business.control;

import business.model.Lexico;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import business.model.Simbolo;
import infra.Arquivo;

/**
 * @author marti
 *  date: 04.08.2018
 */
public class ControllerAnalisadorLexico {
    private List <Simbolo> tabela;
    private final List codigo;
    private final String ARQUIVO_ORIGEM = "..\\Compilador\\codigo.txt";

    public ControllerAnalisadorLexico() {
        this.tabela = new LinkedList<>();
        this.codigo = new LinkedList<String>(carregarListaArquivo());
    }
    
    //trocar por switch case mudando algumas coisas
    public void analisarV1() 
    {
        int numeroLinhas = codigo.size();
        for(int l = 5; l < numeroLinhas; l++) 
        {            
            String linha = codigo.get(l).toString();
            int tamanhoLinha = linha.length();
            for(int i = 0; i < tamanhoLinha;) {
                String teste = linha.substring(i, i+1);
                //pular espaco e TAB
                if (i+1 <= tamanhoLinha && linha.substring(i, i+1).matches(" |\t")) {i++;}
                //comentario + comentario aplicado em aula
                else
                if (i+1 <= tamanhoLinha && ("{".equals(linha.substring(i, i+1)) || "/".equals(linha.substring(i, i+1)))   )
                {
                    int j = i + 1;
                    while(   j < tamanhoLinha && (!"}".equals(linha.substring(i, j)))   ) {j++;}
                    if (j <= tamanhoLinha && (linha.substring(i, j).matches(Lexico.COMENTARIO) || linha.substring(i, j).matches(Lexico.COMENTARIO_AULA) ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "COMENTARIO", l+1 ) );
                        i = j;
                    }         
                }
                else
                //operador aditivo "or"
                if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches("or"))
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_ADITIVO_OR ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_RELACIONAL", l+1 ) );
                        i = j;
                    }
                    else
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "IDENTIFICADOR", l+1 ) );
                        i = j; 
                    }
                }
                else
                //palavra
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches("[a-z]*|_*") )
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                    //palavra reservada
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.PALAVRAS_CHAVES))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "PALAVRA_CHAVE", l+1 ) );
                        i = j;
                    }
                    else //identificador
                        if ( j <= tamanhoLinha && (linha.substring(i, j).matches( Lexico.IDENTIDICADOR )
                                && !(linha.substring(i, j).matches( Lexico.OPERADOR_MULTIPLICATIVO_AND ) && linha.substring(i, j).matches( Lexico.IDENTIDICADOR )) ) )
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j), "IDENTIFICADOR", l+1 ) );
                            i = j; 
                        }
                }
                else
                //numeros 
                if (i < tamanhoLinha && Character.isDigit(linha.charAt(i)) )
                {
                    int j = i + 1;
                    while(   j <= tamanhoLinha && (linha.substring(i, j).matches( Lexico.NUMEROS_INTEIROS ) || linha.substring(i, j).matches( Lexico.NUMEROS_REAIS ) )  ) {j++;}
                    if (linha.substring(i, j-1).matches( Lexico.NUMEROS_INTEIROS ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_INTEIRO", l+1 ) );
                        i = j;
                    }
                    else if (linha.substring(i, j-1).matches( Lexico.NUMEROS_REAIS ))
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_REAL", l+1 ) );
                        i = j;
                    }
                    break;
                }  
                else
                //operadores aditivos
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.OPERADORES_ADITIVOS ) )
                {
                    tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_ADITIVO", l+1 ) );
                    i++;
                }
                else
                //operadores relacionais
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.OPERADORES_RELACIONAIS ) )
                {
                    if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches( Lexico.OPERADORES_RELACIONAIS ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+2), "OPERADOR_RELACIONAL", l+1 ) );
                        i += 2;
                    }
                    else if ( !linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_RELACIONAL", l+1 ) );
                    }
                    i++;
                }
                else
                //delimitadores e atribuicao
                if ( i+1 <= tamanhoLinha && linha.substring(i, i+1).matches( Lexico.DELIMITADORES ) )
                {
                    if (   i+2 <= tamanhoLinha && ( linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )   )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "ATRIBUIÇÃO", l+1 ) );
                        i++;
                    }
                    else 
                    {
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "DELIMITADOR", l+1 ) );
                        i++;
                    }
                }
                else
                //operadores multiplicativos
                if (   i+1 <= tamanhoLinha && ( linha.substring(i, i+1).matches( Lexico.OPERADORES_MULTIPLICATIVOS ))  )
                {
                    tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                    i++;
                }
                else
                //operador multiplicativo and
                if ( i+3 <= tamanhoLinha && linha.substring(i, i+3).matches("and"))
                {
                    int j = i + 1;
                    while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                    if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_MULTIPLICATIVO_AND ) )
                    {
                        tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                        i = j;
                    }
                }
                else
                {
                    System.err.println("Erro na linha " +l+ "(" +linha.substring(i,i+1) +")");
                    i++;
                }
                
            }//fim do for do for de tamanho da linha 
            String teste="";
        }// fim do for de numero de linhas   
        String teste="";
    }//fim do metodo

    public void exibirTabela ()
    {
        
    }
    
    public void analisarV2()
    {
        int numeroLinhas = codigo.size();
        for(int l = 0; l < numeroLinhas; l++) 
        {           
            String linha = codigo.get(l).toString();
            int tamanhoLinha = linha.length();
            
            for(int i = 0; i < tamanhoLinha; i++) {
                String caracter = linha.substring(i, i+1);
                int j = i + 1;
                switch (caracter)
                {
                    //pula espaco
                    case " ":
                        i++;
                        break;
                    //comentario
                    case "{":
                        while(   j+1 <= tamanhoLinha && (!"}".equals(linha.substring(j, j+1)))   ) {j++;}
                        if (j < tamanhoLinha && linha.substring(i, j+1).matches(Lexico.COMENTARIO))
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j+1), "COMENTARIO", l+1 ) );
                            i = j;
                        }
                        break;
                    //operador aditivo "or"
                    case "o":
                        if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches(Lexico.OPERADOR_ADITIVO_OR) )
                        {
                            while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                            if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_ADITIVO_OR ) )
                            {
                                tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_RELACIONAL", l+1 ) );
                                i = j;
                            }
                        }
                        break;
                    //palavra
                    case Lexico.IDENTIDICADOR2:
                        while(  j < tamanhoLinha && (Character.isLetterOrDigit(linha.charAt(j)) || linha.charAt(j)=='_')  ) {j++;}
                        //palavra reservada
                        if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.PALAVRAS_CHAVES))
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j), "PALAVRA_CHAVE", l+1 ) );
                            i = j;
                        }
                        //identificador
                        else if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.IDENTIDICADOR ) )
                             {
                                tabela.add( new Simbolo ( linha.substring(i, j), "IDENTIFICADOR", l+1 ) );
                                i = j; 
                             }
                        break;
                    //numeros
                    case "\\d":
                        while(   j <= tamanhoLinha && (linha.substring(i, j).matches( Lexico.NUMEROS_INTEIROS ) || linha.substring(i, j).matches( Lexico.NUMEROS_REAIS ) )  ) {j++;}
                        if (linha.substring(i, j-1).matches( Lexico.NUMEROS_INTEIROS ))
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_INTEIRO", l+1 ) );
                            i = j;
                        }
                        else if (linha.substring(i, j-1).matches( Lexico.NUMEROS_REAIS ))
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j-1), "NUMERO_REAL", l+1 ) );
                            i = j;
                        }
                        break;
                    //operadores relacionais
                    case "\\w":
                        if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches( Lexico.OPERADORES_RELACIONAIS ) )
                        {
                            tabela.add( new Simbolo ( linha.substring(i, i+2), "OPERADOR_RELACIONAL", l+1 ) );
                            i += 2;
                        }
                        else if ( !linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )
                        {
                            tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_RELACIONAL", l+1 ) );
                        }
                    //operadores aditivos
                    case Lexico.OPERADORES_ADITIVOS:
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_ADITIVO", l+1 ) );
                        break;
                    //atribuição
                    case ":":
                        if ( i+2 <= tamanhoLinha && linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) )
                        {
                            tabela.add( new Simbolo ( linha.substring(i, i+2), "ATRIBUICAO", l+1 ) );
                            i += 2;
                        }
                    //delimitadores
                    case Lexico.DELIMITADORES:
                        if ( i+2 <= tamanhoLinha && ( linha.substring(i, i+2).matches( Lexico.ATRIBUICAO ) || linha.substring(i, i+2).matches( Lexico.NUMEROS_INTEIROS )) ) { }
                        else 
                        {
                            tabela.add( new Simbolo ( linha.substring(i, i+1), "DELIMITADOR", l+1 ) );
                        }
                        break;
                    //operadores multiplicativos
                    case Lexico.OPERADORES_MULTIPLICATIVOS:
                        tabela.add( new Simbolo ( linha.substring(i, i+1), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                        break;
                    //operador multiplicativo and
                    case "a":
                    if ( i+3 <= tamanhoLinha && linha.substring(i, i+3).matches("and"))
                    {
                        while(    j < tamanhoLinha && Character.isLetterOrDigit(linha.charAt(j))   ) {j++;}
                        if ( j <= tamanhoLinha && linha.substring(i, j).matches( Lexico.OPERADOR_MULTIPLICATIVO_AND ) )
                        {
                            tabela.add( new Simbolo ( linha.substring(i, j), "OPERADOR_MULTIPLICATIVO", l+1 ) );
                            i = j;
                        }
                    }
                    default:
                        System.err.print("caracter inesperado: linha " + (i+1) + ", " + caracter);
                        break;
                        
                }         
            }//fim do for do for de tamanho da linha         
        }// fim do for de numero de linhas   
        String teste="";
    }
    
    /**************************************************************************
    *   Metodo para retornar Arquivo de codigo em List                        *                                                           *  
    *   @return List                                                          * 
    ***************************************************************************/
    public List carregarListaArquivo ()  
    {
        try {
            return new Arquivo(ARQUIVO_ORIGEM).carregarCodigo();
        } catch (IOException ex) {
            Logger.getLogger(ControllerAnalisadorLexico.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
